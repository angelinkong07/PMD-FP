package com.example.myapplicationnew;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLatLng;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error loading map", Toast.LENGTH_SHORT).show();
        }

        Button cancelButton = findViewById(R.id.cancelNavigationButton);
        cancelButton.setOnClickListener(v -> {
            speak("Navigation cancelled.");
            finish(); // Close MapsActivity
        });
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                        String destination = getIntent().getStringExtra("DESTINATION");
                        if (destination != null && !destination.isEmpty()) {
                            speak("Navigation started to " + destination);
                            getDestinationCoordinates(destination);
                        } else {
                            updateNavInfo(getString(R.string.no_destination));
                        }
                    } else {
                        updateNavInfo(getString(R.string.failed_location));
                    }
                });
    }

    private void getDestinationCoordinates(String destinationName) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocationName(destinationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng destinationLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination: " + destinationName));
                mMap.addMarker(new MarkerOptions().position(currentLatLng).title("You"));

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(currentLatLng);
                builder.include(destinationLatLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));

                fetchETA(currentLatLng, destinationLatLng);
            } else {
                Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MapsActivity", "Error getting destination coordinates", e);
            Toast.makeText(this, getString(R.string.error_getting_coordinates), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchETA(LatLng origin, LatLng destination) {
        String apiKey = getString(R.string.google_maps_key);
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving" +
                "&key=" + apiKey;

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                URL apiUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                runOnUiThread(() -> handleDirectionsResponse(result.toString()));
            } catch (Exception e) {
                Log.e("MapsActivity", "Failed to fetch directions", e);
                runOnUiThread(() ->
                        Toast.makeText(MapsActivity.this, "Failed to get directions", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void handleDirectionsResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONArray routes = json.getJSONArray("routes");
            if (routes.length() > 0) {
                JSONObject route = routes.getJSONObject(0);
                JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                String duration = leg.getJSONObject("duration").getString("text");
                String distance = leg.getJSONObject("distance").getString("text");

                TextView navInfo = findViewById(R.id.navInfoTextView);
                String etaText = getString(R.string.eta_format, duration, distance);
                navInfo.setText(etaText);
                speak("Estimated time of arrival is " + duration + ". Distance is " + distance);

                // Draw route
                String polyline = route.getJSONObject("overview_polyline").getString("points");
                List<LatLng> points = decodePolyline(polyline);
                PolylineOptions polylineOptions = new PolylineOptions()
                        .addAll(points)
                        .width(12)
                        .color(Color.BLUE)
                        .geodesic(true);
                mMap.addPolyline(polylineOptions);

            } else {
                Toast.makeText(this, getString(R.string.no_route_found), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("MapsActivity", "Error parsing directions", e);
            Toast.makeText(this, getString(R.string.route_parse_error), Toast.LENGTH_SHORT).show();
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new java.util.ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((lat / 1E5), (lng / 1E5));
            poly.add(p);
        }

        return poly;
    }

    private void updateNavInfo(String text) {
        TextView navInfo = findViewById(R.id.navInfoTextView);
        navInfo.setText(text);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
