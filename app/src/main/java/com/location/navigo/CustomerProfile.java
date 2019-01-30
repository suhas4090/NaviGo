package com.location.navigo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;

public class CustomerProfile extends AppCompatActivity {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.quantity)
    TextView quantity;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.call)
    ImageView call;
    @BindView(R.id.delivered)
    ImageView delivered;
    //vars
    String mName,mAddress,mStatus;
    Number mQuantity,mPhoneNo;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        Bundle extras = getIntent().getExtras();
        mName = (String) extras.get("Names");
        mAddress = (String) extras.get("Address");
        mQuantity = (Number) extras.get("Quantity");
        mPhoneNo = (Number) extras.get("PhoneNo");
        mStatus = (String) extras.get("Status");

//        name =  findViewById(R.id.name);
//        address =  findViewById(R.id.address);
//        quantity =  findViewById(R.id.quantity);
//        mobile =  findViewById(R.id.mobile);
//        status =  findViewById(R.id.status);
//        call = findViewById(R.id.call);
//        delivered = findViewById(R.id.delivered);

        name.setText(mName);
        address.setText(mAddress);
        quantity.setText(mQuantity.toString());
        mobile.setText(mPhoneNo.toString());
        if (mStatus!="pending") {
            status.setText("Complete");
        } else {
            status.setText("InComplete");
        }
    }

    @OnClick(R.id.call)
    protected void onCallClick(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String number = "tel:"+mPhoneNo;
        intent.setData(Uri.parse(number));
        startActivity(intent);
    }

    @OnClick(R.id.delivered)
    protected void OnDeleveredClick(){
        delivered.setVisibility(View.GONE);
    }
}
