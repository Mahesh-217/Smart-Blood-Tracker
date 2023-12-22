// Inside BloodBankAdapter.java
package com.example.mad_rv.adapters;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_rv.BloodBankCamp;
import com.example.mad_rv.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BloodBankAdapter extends RecyclerView.Adapter<BloodBankAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BloodBankCamp> bloodBankCamps;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    public BloodBankAdapter(Context context, ArrayList<BloodBankCamp> bloodBankCamps) {
        this.context = context;
        this.bloodBankCamps = bloodBankCamps;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("blood_banks");
        this.auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blood_bank, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodBankCamp bloodBankCamp = bloodBankCamps.get(position);

        // Set your data to the views in the ViewHolder
        holder.campNameTextView.setText(bloodBankCamp.getCampName());
        holder.locationTextView.setText(bloodBankCamp.getLocation());
        holder.dateTextView.setText(bloodBankCamp.getDate());
        holder.PhoneTextView.setText(bloodBankCamp.getPhone());

        // Set click listener for share button
        holder.shareButton.setOnClickListener(v -> {
            shareBloodBankCamp(bloodBankCamp);
        });

        // Set click listener for join button
        holder.joinButton.setOnClickListener(v -> {
            // Handle the join button click
            handleJoinButtonClick(bloodBankCamp);
        });

        // Set click listener for list button
        holder.listButton.setOnClickListener(v -> {
            // Show the joined users list
            showJoinedUsersList(bloodBankCamp);
        });
    }

    @Override
    public int getItemCount() {
        return bloodBankCamps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView campNameTextView;
        TextView locationTextView;
        TextView dateTextView;
        TextView PhoneTextView;
        ImageView shareButton;
        Button joinButton;
        Button listButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            campNameTextView = itemView.findViewById(R.id.campNameTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            PhoneTextView = itemView.findViewById(R.id.PhoneTextView);
            shareButton = itemView.findViewById(R.id.bloodBankShare);
            joinButton = itemView.findViewById(R.id.bloodBankJoin);
            listButton = itemView.findViewById(R.id.bloodBankList);
        }
    }

    // Method to share blood bank camp details
    private void shareBloodBankCamp(BloodBankCamp bloodBankCamp) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Join the Blood Bank Camp!!");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Join the Blood Donation Camp!!\n Blood Bank Name: " + bloodBankCamp.getCampName() +
                "\nLocation: " + bloodBankCamp.getLocation() +
                "\nDate: " + bloodBankCamp.getDate() +
                "\nPhone: " + bloodBankCamp.getPhone());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, "Join the Blood Bank Camp!!");
        context.startActivity(shareIntent);
    }

    // Method to handle the join button click
    private void handleJoinButtonClick(BloodBankCamp bloodBankCamp) {
        // You can implement your logic here when the user clicks the "Join" button.
        // For example, you can show a confirmation dialog.
        // In this example, I'll show a simple Toast message.

        Toast.makeText(context, "Registration confirmed check your SMS for details\n", Toast.LENGTH_SHORT).show();

        // Send an SMS to the user with blood bank camp details
        sendSmsToUser(bloodBankCamp);

        // Add the current user to the joined users list for the blood bank camp
        addCurrentUserToJoinedUsersList(bloodBankCamp);
    }

    // Method to send an SMS to the user with blood bank camp details
    private void sendSmsToUser(BloodBankCamp bloodBankCamp) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                String message = "Thanks for Successfully Registering in the blood Donation camp: \n" + "Name:" + currentUser.getDisplayName() +
                        "\nLocation: " + bloodBankCamp.getLocation() +
                        "\nDate: " + bloodBankCamp.getDate() +
                        "\nPhone: " + bloodBankCamp.getPhone() + "\nBe there to save lives!!";
                smsManager.sendTextMessage(currentUser.getPhoneNumber(), null, message, null, null);
                // You might want to add a log or Toast message here to indicate the SMS was sent successfully
            } catch (SecurityException e) {
                e.printStackTrace();
                // Handle the exception, e.g., request SMS permission or show a message to the user
            } catch (Exception e) {
                e.printStackTrace();
                // Handle other exceptions, if any
            }
        } else {
            // Handle the case where the user's phone number is not available
            Toast.makeText(context, "User's phone number not available", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to add the current user to the joined users list for the blood bank camp

    private void addCurrentUserToJoinedUsersList(BloodBankCamp bloodBankCamp) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Get the user details from the "donors" node
            String userId = currentUser.getUid();
            DatabaseReference donorsReference = FirebaseDatabase.getInstance().getReference("Donors").child(userId);

            donorsReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        // Retrieve the user's name from the "donors" node
                        String userName = dataSnapshot.child("FName").getValue(String.class);
                        String userPhone = dataSnapshot.child("Mobile").getValue(String.class);

                        Log.d("BloodBankAdapter", "User Name: " + userName);
                        Log.d("BloodBankAdapter", "User Phone: " + userPhone);

                        // Create a map to store the user details
                        Map<String, Object> userDetailsMap = new HashMap<>();
                        userDetailsMap.put("name", userName);
                        userDetailsMap.put("phone", userPhone);

                        // Update the joined users list in Firebase
                        String bloodBankCampName = bloodBankCamp.getCampName();
                        databaseReference.child(bloodBankCampName).child("joined_users").child(userId).setValue(userDetailsMap)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Log.d("BloodBankAdapter", "User added to joined users list successfully");
                                    } else {
                                        Log.e("BloodBankAdapter", "Error adding user to joined users list", updateTask.getException());
                                    }
                                });
                    } else {
                        // Handle the case where no user data is found in the "donors" node
                        Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the error
                    Toast.makeText(context, "Error retrieving user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    // Method to show the joined users list for the blood bank camp
    private void showJoinedUsersList(BloodBankCamp bloodBankCamp) {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Reference to the "blood_banks" node
            DatabaseReference bloodBanksReference = FirebaseDatabase.getInstance().getReference("blood_banks");

            // Reference to a specific "CampName" node
            String bloodBankCampName = bloodBankCamp.getCampName();
            DatabaseReference campReference = bloodBanksReference.child(bloodBankCampName).child("joined_users");

            campReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null && dataSnapshot.exists()) {
                        // Convert the dataSnapshot to a string for display
                        StringBuilder joinedUsersStringBuilder = new StringBuilder();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.getKey();
                            if (userId.equals(currentUser.getUid())) {
                                // Skip the current user's details
//                                continue;
                            }




                            String name = userSnapshot.child("name").getValue(String.class);
                            String phone = userSnapshot.child("phone").getValue(String.class);

                            Log.d("BloodBankAdapter", "Joined User Name: " + name + ", Phone: " + phone);

                            joinedUsersStringBuilder.append("Name: ").append(name).append("\nPhone: ").append(phone).append("\n\n");
                        }

                        // Create an AlertDialog to display the joined users list
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Joined Users List")
                                .setMessage(joinedUsersStringBuilder.toString())
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        // Handle the case where no joined users are found
                        Toast.makeText(context, "No joined users found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the error
                    Toast.makeText(context, "Error retrieving joined users", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}