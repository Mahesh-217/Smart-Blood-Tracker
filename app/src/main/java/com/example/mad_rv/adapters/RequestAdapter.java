package com.example.mad_rv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import com.example.mad_rv.R;
import com.example.mad_rv.listeners.MyOnClickListener;
import com.example.mad_rv.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {

    private ArrayList<Request> requests;
    private Context context;
    private MyOnClickListener myOnClickListenerCall, myOnClickListenerShare;

    public RequestAdapter(Context context, ArrayList<Request> requests, MyOnClickListener onClickListenerCall, MyOnClickListener onClickListenerShare) {
        this.context = context;
        this.requests = requests;
        this.myOnClickListenerCall = onClickListenerCall;
        this.myOnClickListenerShare = onClickListenerShare;
    }


    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestHolder(LayoutInflater.from(context).inflate(R.layout.item_request, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        Request request = requests.get(position);

        holder.recipientName.setText(request.getRecipientName());
        holder.bloodGroup.setText(request.getBloodGroup());
        holder.contactNumber.setText(request.getContactNumber());
        holder.location.setText(request.getLocation());
        holder.location.setSelected(true);

        holder.call.setOnClickListener(v -> myOnClickListenerCall.getPosition(position));
        holder.share.setOnClickListener(v -> myOnClickListenerShare.getPosition(position));
        holder.call.setOnClickListener(v -> {
            if (myOnClickListenerCall != null) {
                myOnClickListenerCall.getPosition(position);

                // Use the position to get the clicked request and initiate a call
                Request clickedRequest = requests.get(position);
                initiateCall(clickedRequest.getContactNumber());
            }
        });

        // Handle share button click
        holder.share.setOnClickListener(v -> {
            if (myOnClickListenerShare != null) {
                myOnClickListenerShare.getPosition(position);

                // Use the position to get the clicked request and share the details
                Request clickedRequest = requests.get(position);
                shareRequestDetails(clickedRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateList(ArrayList<Request> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    static class RequestHolder extends RecyclerView.ViewHolder {

        TextView recipientName, bloodGroup, contactNumber, location;
        View call, share;

        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            recipientName = itemView.findViewById(R.id.requestRecipientName);
            bloodGroup = itemView.findViewById(R.id.requestBloodGroup);
            contactNumber = itemView.findViewById(R.id.requestContactNumber);
            location = itemView.findViewById(R.id.requestLocation);
            call = itemView.findViewById(R.id.call);
            share = itemView.findViewById(R.id.share);
        }
    }
    private void shareRequestDetails(Request request) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TITLE, "Blood Request Details");
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Emergency Blood requirement!!\nRecipient: " + request.getRecipientName() +
                "\nBlood Group: " + request.getBloodGroup() +
                "\nContact Number: " + request.getContactNumber() +
                "\nLocation: " + request.getLocation());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }
    private void initiateCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

}
