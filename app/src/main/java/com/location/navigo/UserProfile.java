package com.location.navigo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserProfile extends AppCompatActivity {

    private TextView name,address,quantity,mobile,status;

    //vars
    String mName,mAddress;
    Number mQuantity,mPhoneNo;
    Boolean mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle extras = getIntent().getExtras();
        mName = (String) extras.get("Names");
        mAddress = (String) extras.get("Address");
        mQuantity = (Number) extras.get("Quantity");
        mPhoneNo = (Number) extras.get("PhoneNo");
        mStatus = (boolean) extras.get("Status");

        name = (TextView) findViewById(R.id.name);
        address = (TextView) findViewById(R.id.address);
        quantity = (TextView) findViewById(R.id.quantity);
        mobile = (TextView) findViewById(R.id.mobile);
        status = (TextView) findViewById(R.id.status);

        name.setText(mName);
        address.setText(mAddress);
        quantity.setText(mQuantity.toString());
        mobile.setText(mPhoneNo.toString());
        if (mStatus) {
            status.setText("Complete");
        } else {
            status.setText("InComplete");
        }
    }
}
