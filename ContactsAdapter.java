package com.example.myapplicationnew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    private List<Contact> contacts;
    private OnItemClickListener listener;

    public ContactsAdapter(List<Contact> contacts, OnItemClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(contact, listener);
    }

    @Override
    public int getItemCount() {
        return contacts != null ? contacts.size() : 0;
    }

    public void updateList(List<Contact> updatedContacts) {
        this.contacts = updatedContacts;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView phoneTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            phoneTextView = itemView.findViewById(R.id.phone_text_view);
        }

        public void bind(Contact contact, OnItemClickListener listener) {
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhone());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(contact);
                }
            });

            // Optional: Long press to remove contact in future
            itemView.setOnLongClickListener(v -> {
                // You can show a confirmation dialog in the Activity
                return false;
            });
        }
    }
}
