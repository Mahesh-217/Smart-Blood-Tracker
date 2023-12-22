package com.example.mad_rv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_rv.adapters.BloodBankAdapter;
import com.example.mad_rv.BloodBankCamp;
import com.example.mad_rv.R;

import java.util.ArrayList;

public class BloodBankActivity extends AppCompatActivity {

    private RecyclerView bloodBankRecyclerView;
    private ArrayList<BloodBankCamp> bloodBankCamps;
    private BloodBankAdapter bloodBankAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank);

        bloodBankRecyclerView = findViewById(R.id.bloodBankRecyclerView);
        bloodBankCamps = new ArrayList<>();
        bloodBankAdapter = new BloodBankAdapter(this, bloodBankCamps);

        bloodBankRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bloodBankRecyclerView.setAdapter(bloodBankAdapter);

        // Load sample blood bank data
        loadSampleBloodBankData();

        // Add Blood Camp button click listener
        Button btnAddBloodCamp = findViewById(R.id.btnAddBloodCamp);
        btnAddBloodCamp.setOnClickListener(v -> showAddBloodCampDialog());
    }

    private void loadSampleBloodBankData() {
        // Add sample data directly to the list
            bloodBankCamps.add(new BloodBankCamp("RV Camp", "Naidupeta", "2023-12-15", "7569391134"));
        bloodBankCamps.add(new BloodBankCamp("Red Cross", "Nellore", "2023-12-20", "8919283377"));
        bloodBankCamps.add(new BloodBankCamp("Save Lives", "Gudur", "2023-12-25", "9440929177"));

        // Notify the adapter about the data change
        bloodBankAdapter.notifyDataSetChanged();
    }

    // Method to show the dialog for entering blood camp details
    private void showAddBloodCampDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_blood_camp, null);
        builder.setView(view);

        // Find views in the dialog
        EditText etCampName = view.findViewById(R.id.etCampName);
        EditText etLocation = view.findViewById(R.id.etLocation);
        EditText etDate = view.findViewById(R.id.etDate);
        EditText etPhone = view.findViewById(R.id.etPhone);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        AlertDialog dialog = builder.create();

        // Handle submit button click
        btnSubmit.setOnClickListener(v -> {
            // Get entered details
            String campName = etCampName.getText().toString();
            String location = etLocation.getText().toString();
            String date = etDate.getText().toString();
            String phone = etPhone.getText().toString();

            // Validate the entered details
            if (isValidDetails(campName, location, date, phone)) {
                // Add blood camp using the entered details
                addBloodCamp(campName, location, date, phone);

                // Dismiss the dialog
                dialog.dismiss();
            } else {
                // Show an error message if details are invalid
                etCampName.setError("Please enter valid details");
                // You can add similar error handling for other fields
            }
        });

        // Show the dialog
        dialog.show();
    }

    // Method to validate entered details (you can customize the validation logic)
    private boolean isValidDetails(String campName, String location, String date, String phone) {
        return !campName.isEmpty() && !location.isEmpty() && !date.isEmpty() && !phone.isEmpty();
    }

    // Method to add a blood camp to the list
    private void addBloodCamp(String campName, String location, String date, String phone) {
        // Create a new BloodBankCamp object
        BloodBankCamp bloodBankCamp = new BloodBankCamp(campName, location, date, phone);

        // Add the blood camp to the list
        bloodBankCamps.add(bloodBankCamp);

        // Notify the adapter about the data change
        bloodBankAdapter.notifyDataSetChanged();
    }
}