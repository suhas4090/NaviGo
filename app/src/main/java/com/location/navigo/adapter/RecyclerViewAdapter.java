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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> namelist = new ArrayList<>();
    private ArrayList<Boolean> dStatus = new ArrayList<>();
    private ArrayList<GeoPoint> geoPoints = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<Number> mPhoneNo = new ArrayList<>();
    private ArrayList<Number> mQuantity = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> name,ArrayList<String> address,ArrayList<Number> quantity, ArrayList<Number> phoneNo, ArrayList<Boolean> dStatus, ArrayList<GeoPoint> geoPoints, Context mContext) {
        this.namelist = name;
        this.dStatus = dStatus;
        this.mContext = mContext;
        this.geoPoints = geoPoints;
        this.mAddress = address;
        this.mPhoneNo = phoneNo;
        this.mQuantity = quantity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder: called.");

        viewHolder.name.setText(namelist.get(i));
        viewHolder.dStatus.setText(dStatus.get(i) == true ? "Complete" : "InComplete");
        viewHolder.map.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(mContext, MapsActivity.class);

            @Override
            public void onClick(View view) {
                Log.d("clicking", "this is clicking");
                intent.putExtra("AllMap", false);
                intent.putExtra("Names", namelist.get(i));
                intent.putExtra("latitude", geoPoints.get(i).getLatitude());
                intent.putExtra("longitude", geoPoints.get(i).getLongitude());
                mContext.startActivity(intent);
            }
        });
        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on : " + namelist.get(i));
                Toast.makeText(mContext, namelist.get(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, UserProfile.class);
                intent.putExtra("Names", namelist.get(i));
                intent.putExtra("Address", mAddress.get(i));
                intent.putExtra("Quantity", mQuantity.get(i));
                intent.putExtra("PhoneNo", mPhoneNo.get(i));
                intent.putExtra("Status", dStatus.get(i));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return namelist.size();
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
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
