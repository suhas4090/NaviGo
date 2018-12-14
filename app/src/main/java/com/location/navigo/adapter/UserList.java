package com.location.navigo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.GeoPoint;
import com.location.navigo.MapsActivity;
import com.location.navigo.R;
import com.location.navigo.UserProfile;

import java.util.ArrayList;

public class UserList extends RecyclerView.Adapter<UserList.ViewHolder> {
    private static final String TAG = "UserList";

    private ArrayList<String> mId = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<Boolean> mStatus = new ArrayList<>();
    private ArrayList<GeoPoint> geoPoints = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<Number> mPhoneNo = new ArrayList<>();
    private ArrayList<Number> mQuantity = new ArrayList<>();
    private Context mContext;

    public UserList(ArrayList<String> id,ArrayList<String> name,ArrayList<String> address,ArrayList<Number> quantity, ArrayList<Number> phoneNo, ArrayList<Boolean> dStatus, ArrayList<GeoPoint> geoPoints, Context mContext) {
        this.mId = id;
        this.mName = name;
        this.mStatus = dStatus;
        this.mContext = mContext;
        this.geoPoints = geoPoints;
        this.mAddress = address;
        this.mPhoneNo = phoneNo;
        this.mQuantity = quantity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_userslists, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");

        viewHolder.name.setText(mName.get(i));
        viewHolder.dStatus.setText(mStatus.get(i) == true ? "Complete" : "InComplete");
        viewHolder.map.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(mContext, MapsActivity.class);

            @Override
            public void onClick(View view) {
                Log.d("clicking", "this is clicking");
                intent.putExtra("AllMap", false);
                intent.putExtra("Names", mName.get(i));
                intent.putExtra("latitude", geoPoints.get(i).getLatitude());
                intent.putExtra("longitude", geoPoints.get(i).getLongitude());
                mContext.startActivity(intent);
            }
        });
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on : " + mName.get(i));
                Toast.makeText(mContext, mName.get(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, UserProfile.class);
                intent.putExtra("Id", mId.get(i));
                intent.putExtra("Names", mName.get(i));
                intent.putExtra("Address", mAddress.get(i));
                intent.putExtra("Quantity", mQuantity.get(i));
                intent.putExtra("PhoneNo", mPhoneNo.get(i));
                intent.putExtra("Status", mStatus.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView dStatus;
        FloatingActionButton map;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            dStatus = itemView.findViewById(R.id.status);
            map = itemView.findViewById(R.id.mapviewer);
            parentLayout = itemView.findViewById(R.id.userslists);
        }
    }
}
