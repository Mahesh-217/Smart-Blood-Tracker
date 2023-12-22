// ViewRequestsActivity.java
package com.example.mad_rv;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mad_rv.adapters.RequestAdapter;
import com.example.mad_rv.listeners.MyOnClickListener;
import com.example.mad_rv.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewRequestsActivity extends AppCompatActivity {

    private RecyclerView requestList;
    private ArrayList<Request> requests;
    private RequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        initializeComponents();
        getRequests();
    }

    private void initializeComponents() {
        requestList = findViewById(R.id.viewRequestList);
        requests = new ArrayList<>();
        adapter = new RequestAdapter(this, requests, position -> {
            // Handle item click if needed
        }, position -> {
            // Handle item click if needed
        });

        requestList.setLayoutManager(new LinearLayoutManager(this));
        requestList.setAdapter(adapter);
    }

    private void getRequests() {
        FirebaseDatabase.getInstance().getReference("Requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requests.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Request request = ds.getValue(Request.class);
                    if (request != null) {
                        requests.add(request);
                    }
                }
                adapter.updateList(requests);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
