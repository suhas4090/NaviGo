package com.location.navigo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private ArrayList<String> dStatus = new ArrayList<>();
    private ArrayList<GeoPoint> mCoordinates = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<Number> mPhoneNo = new ArrayList<>();
    private ArrayList<Number> mQuantity = new ArrayList<>();
    private ArrayList<Number> mCustomerId = new ArrayList<>();
    private ArrayList<String> mDeliveryAgent = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> name,ArrayList<String> address,ArrayList<Number> quantity,
                               ArrayList<Number> phoneNo, ArrayList<String> dStatus, ArrayList<GeoPoint> coordinates,
                               ArrayList<Number> customerId, ArrayList<String> deliveryAgent, Context mContext) {
        this.namelist = name;
        this.dStatus = dStatus;
        this.mContext = mContext;
        this.mCoordinates = coordinates;
        this.mAddress = address;
        this.mPhoneNo = phoneNo;
        this.mQuantity = quantity;
        this.mCustomerId = customerId;
        this.mDeliveryAgent = deliveryAgent;

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
        viewHolder.quantity.setText("Quantity: "+ String.valueOf(mQuantity.get(i)));
        viewHolder.address.setText(mAddress.get(i));
        viewHolder.dStatus.setText(dStatus.get(i) != "pending" ? "Complete" : "InComplete");
        viewHolder.map.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(mContext, MapsActivity.class);

            @Override
            public void onClick(View view) {
                Log.d("clicking", "this is clicking");
                intent.putExtra("AllMap", false);
                intent.putExtra("Names", namelist.get(i));
                intent.putExtra("latitude", mCoordinates.get(i).getLatitude());
                intent.putExtra("longitude", mCoordinates.get(i).getLongitude());
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

        TextView name,dStatus,quantity,address;

        ImageView map;
        CardView parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cardname);
            dStatus = itemView.findViewById(R.id.cardstatus);
            quantity = itemView.findViewById(R.id.cardquantity);
            address = itemView.findViewById(R.id.cardaddress);
            map = itemView.findViewById(R.id.mapviewer);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
