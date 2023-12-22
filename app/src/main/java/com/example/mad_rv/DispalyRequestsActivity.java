package com.example.mad_rv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.mad_rv.adapters.UserAdapter;
import com.example.mad_rv.model.User;

import java.util.ArrayList;
import java.util.HashMap;

public class DispalyRequestsActivity extends AppCompatActivity {

    RecyclerView list;
    ArrayList<User>requests,temp;
    UserAdapter adapter;
    EditText districtFilter;
    User self;
    String uid = FirebaseAuth.getInstance().getUid();
    PopupMenu popupMenu;

    com.google.android.material.button.MaterialButton requestCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaly_requests);


        initializeComponents();
        getRequests();


    }


    private void updateList(String toString) {
        temp.clear();
        for( User v : requests){
            if(v.getDistrict().toUpperCase().contains(toString)||toString.equalsIgnoreCase("ALL")) {
                System.out.println(v.getDistrict());
                temp.add(v);
            }
        }
        adapter.updateList(temp);
    }

    private void initializeComponents() {

        Button btnRequestForOthers = findViewById(R.id.btnRequestForOthers);
        btnRequestForOthers.setOnClickListener(v -> showRequestPopup());
    requestCancelBtn = findViewById(R.id.btnAddRequest);
        popupMenu = new PopupMenu(this,findViewById(R.id.more));
        popupMenu.getMenuInflater().inflate(R.menu.context_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.changePass){
                FirebaseAuth.getInstance().sendPasswordResetEmail(self.getEmail());
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Password Reset Link Sent On Registered Email.", Snackbar.LENGTH_LONG);
                View view1 = snack.getView();
                FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view1.getLayoutParams();
                params.gravity = Gravity.CENTER_VERTICAL;
                view1.setLayoutParams(params);
                snack.show();
            }else if(item.getItemId() == R.id.logout){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this,SplashScreen.class));
                DispalyRequestsActivity.this.finish();
            } else if (item.getItemId() == R.id.bloodbank) {
                startActivity(new Intent(this, BloodBankActivity.class));
            }
            return true;
        });

        temp = new ArrayList<>();
        requests = new ArrayList<>();
        districtFilter = findViewById(R.id.districtFilterRequest);
        list = findViewById(R.id.requestList);
        requests = new ArrayList<>();
        adapter = new UserAdapter(this, requests, position -> {
            //call button handle
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+temp.get(position).getMobile()));
            startActivity(intent);
        }, position -> {
            //share button handle
            User sent = temp.get(position);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE,"Be Hero, Donate Blood.");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "*Its Urgent.*\n"+"Here is the Information about blood Request:\n"+sent.getFName()+" "+sent.getLName()+"\nBlood Group : "+sent.getBloodGroup()+"\nAddress:"+sent.getState()+" "+sent.getDistrict()+" "+sent.getTehsil()+"\nMobile Number :"+sent.getMobile());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "Be Hero, Donate Blood.");
            startActivity(shareIntent);
        });

        list.setLayoutManager(new LinearLayoutManager(DispalyRequestsActivity.this));
        list.setAdapter(adapter);

        districtFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateList(s.toString());
            }
        });
    }

    public void viewDonorsList(View view) {
        startActivity(new Intent(DispalyRequestsActivity.this,DisplayDonorsActivity.class));
        this.finish();
    }


    public void popUp(View view) {
        popupMenu.show();
    }



    private void getRequests() {
        FirebaseDatabase.getInstance().getReference("Donors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                temp.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user != null && user.getStep() != null && user.getRequestBlood() != null && user.getUID() != null) {
                        if (user.getStep().equals("Done")) {
                            if ("True".equals(user.getRequestBlood())) { // use "True".equals() to avoid potential NPE
                                requests.add(user);
                                temp.add(user);
                            }
                            if (uid.equals(user.getUID())) {
                                self = user;
                                if ("True".equals(self.getRequestBlood())) { // use "True".equals() to avoid potential NPE
                                    requestCancelBtn.setText("Cancel Blood Request");
                                } else {
                                    requestCancelBtn.setText("Request Blood");
                                }
                            }
                        }
                    }
                }
                updateList(districtFilter.getText().toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showRequestPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_request_layout, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);


        EditText edtRecipientName = popupView.findViewById(R.id.edtRecipientName);
        EditText edtBloodGroup = popupView.findViewById(R.id.edtBloodGroup);
        EditText edtContactNumber = popupView.findViewById(R.id.edtContactNumber);
        EditText edtLocation = popupView.findViewById(R.id.edtLocation);
        Button btnSubmitRequest = popupView.findViewById(R.id.btnSubmitRequest);

        btnSubmitRequest.setOnClickListener(view -> {
            String recipientName = edtRecipientName.getText().toString().trim();
            String bloodGroup = edtBloodGroup.getText().toString().trim();
            String contactNumber = edtContactNumber.getText().toString().trim();
            String location = edtLocation.getText().toString().trim();

            if (!recipientName.isEmpty() && !bloodGroup.isEmpty() && !contactNumber.isEmpty() && !location.isEmpty()) {
                addRequestToFirebase(recipientName, bloodGroup, contactNumber, location);
                popupWindow.dismiss();
            } else {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }


        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void addRequestToFirebase(String recipientName, String bloodGroup, String contactNumber, String location) {
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("Requests");
        String requestId = requestsRef.push().getKey();
        Request request = new Request(requestId, recipientName, bloodGroup, contactNumber, location, uid);
        requestsRef.child(requestId).setValue(request);

        Toast.makeText(this, "Request submitted successfully", Toast.LENGTH_SHORT).show();
    }

    public void requestBlood(View view) {
        if (self != null && "True".equals(self.getRequestBlood())) {
            updateBloodRequest(false);
        } else {
            updateBloodRequest(true);
        }
    }

    private void updateBloodRequest(boolean b) {
        HashMap<String,Object> hashMap = new HashMap<>();
        if(b){
            hashMap.put("RequestBlood","True");
        }else {
            hashMap.put("RequestBlood","False");
        }
        FirebaseDatabase.getInstance().getReference("Donors").child(uid).updateChildren(hashMap);
    }
    public void viewRequests(View view) {
        startActivity(new Intent(DispalyRequestsActivity.this, ViewRequestsActivity.class));
    }
}