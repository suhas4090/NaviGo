package com.location.navigo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.location.navigo.adapter.RecyclerViewAdapter;
import com.location.navigo.db.Model;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<Boolean> dStatus = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<Number> mPhoneNo = new ArrayList<>();
    private ArrayList<Number> mQuantity = new ArrayList<>();

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Object object;


    ArrayList<GeoPoint> geoPoints = new ArrayList<GeoPoint>();
//    ArrayList<Double> latitude = new ArrayList<Double>();
//    ArrayList<Double> longitude = new ArrayList<Double>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        if (isServicesOK()) {
            initCustomer();
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make may requests
            Log.d(TAG, "isServicesOK: Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "you can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.allmap:
                if (geoPoints.size() > 0) {
                    Intent intent1 = new Intent(this, MapsActivity.class);

                    ArrayList<Model> pointsExtra = new ArrayList<Model>();
                    for (GeoPoint geo : geoPoints) {
                        pointsExtra.add(new Model(geo));
                    }
                    intent1.putExtra("AllMap", true);
                    intent1.putExtra("Names", mNames);
                    intent1.putExtra("geopoints", pointsExtra);
                    this.startActivity(intent1);
                } else {
                    Toast.makeText(this, "Nothing to show in the Map", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.signout:
                LoginActivity.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initCustomer() {
        db.collection("customer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("name"));
                                mNames.add(String.valueOf(document.getData().get("name")));
                                dStatus.add((Boolean) document.getData().get("status"));
                                geoPoints.add((GeoPoint) document.getData().get("location"));
                                mAddress.add((String) document.getData().get("address"));
                                mPhoneNo.add((Number) document.getData().get("phone number"));
                                mQuantity.add((Number) document.getData().get("quantity"));
                            }
                            Log.d(TAG, "onComplete: " + geoPoints.toString());
                            initRecyclerView();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview");
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mAddress, mQuantity, mPhoneNo, dStatus, geoPoints, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
