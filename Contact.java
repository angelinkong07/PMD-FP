package com.example.myapplicationnew;

public class Contact {
    private String name;
    private String phone;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    // Correct getters returning String
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    // Optional setters (only include if you need to modify fields after creation)
    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
