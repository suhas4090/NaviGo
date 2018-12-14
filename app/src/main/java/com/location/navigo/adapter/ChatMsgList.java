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

public class ChatMsgList extends RecyclerView.Adapter<ChatMsgList.ViewHolder> {
    private static final String TAG = "ChatMsgList";

    private ArrayList<String> mId = new ArrayList<>();
    private ArrayList<String> mName = new ArrayList<>();
    private ArrayList<Boolean> mStatus = new ArrayList<>();
    private ArrayList<GeoPoint> geoPoints = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<Number> mPhoneNo = new ArrayList<>();
    private ArrayList<Number> mQuantity = new ArrayList<>();
    private Context mContext;

    public ChatMsgList(ArrayList<String> id,ArrayList<String> name,ArrayList<String> address,ArrayList<Number> quantity, ArrayList<Number> phoneNo, ArrayList<Boolean> dStatus, ArrayList<GeoPoint> geoPoints, Context mContext) {
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

        viewHolder.title.setText(mName.get(i));
        viewHolder.msg.setText(mStatus.get(i) == true ? "Complete" : "InComplete");
        viewHolder.sendmsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("clicking", "sending message");
            }
        });

        viewHolder.chatLayout.setOnClickListener(new View.OnClickListener() {
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

        TextView title;
        TextView msg;
        FloatingActionButton sendmsg;
        RelativeLayout chatLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.name);
            msg = itemView.findViewById(R.id.status);
            sendmsg = itemView.findViewById(R.id.mapviewer);
            if (true){
                chatLayout = itemView.findViewById(R.id.chatlistreceived);
            }else{
                chatLayout = itemView.findViewById(R.id.chatlistsend);
            }

        }
    }
}

