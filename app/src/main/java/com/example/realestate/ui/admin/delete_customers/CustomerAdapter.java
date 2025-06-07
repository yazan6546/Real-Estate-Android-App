package com.example.realestate.ui.admin.delete_customers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestate.R;
import com.example.realestate.domain.model.User;
import com.example.realestate.domain.service.CountryService;
import com.example.realestate.domain.service.PhoneFormatter;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private List<User> customers = new ArrayList<>();
    private OnCustomerDeleteListener deleteListener;

    public interface OnCustomerDeleteListener {
        void onDeleteCustomer(User customer);
    }

    public CustomerAdapter(OnCustomerDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setCustomers(List<User> customers) {
        this.customers = customers != null ? customers : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customers.get(position);
        holder.bind(customer);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView customerImageView;
        private final TextView customerNameTextView;
        private final TextView customerEmailTextView;
        private final TextView customerPhoneTextView;
        private final Button deleteButton;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerImageView = itemView.findViewById(R.id.customerImageView);
            customerNameTextView = itemView.findViewById(R.id.customerNameTextView);
            customerEmailTextView = itemView.findViewById(R.id.customerEmailTextView);
            customerPhoneTextView = itemView.findViewById(R.id.customerPhoneTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(User customer) {
            // Set customer name
            String fullName = customer.getFirstName() + " " + customer.getLastName();
            customerNameTextView.setText(fullName);
            
            // Set email
            customerEmailTextView.setText(customer.getEmail());
            String phoneNumber = customer.getPhone();

            customerPhoneTextView.setText(PhoneFormatter.formatMobile(phoneNumber, "Palestine"));
            
            // Set profile image (use default person icon for now)
            customerImageView.setImageResource(R.drawable.ic_person);
            
            // Set delete button click listener
            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteCustomer(customer);
                }
            });
        }
    }
}