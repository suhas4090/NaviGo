package com.location.navigo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.location.navigo.adapter.RecyclerViewAdapter;
import com.location.navigo.db.Model;
import com.location.navigo.network.DownloadImage;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> dStatus = new ArrayList<>();
    private ArrayList<String> mAddress = new ArrayList<>();
    private ArrayList<Number> mContact = new ArrayList<>();
    private ArrayList<Number> mQuantity = new ArrayList<>();
    private ArrayList<GeoPoint> mCoordinates = new ArrayList<>();
    private ArrayList<Number> mCustomerId = new ArrayList<>();
    private ArrayList<String> mDeliveryAgent = new ArrayList<>();

    private MainActivity that = this;
    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    NavigationView navigationView;

    ImageView menuProfileImage;

    ImageView drawerProfileImage;
    TextView drawerEmailId;

    Uri photoUrl;
    String email = "";
    DownloadImage downloadImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        Bundle extras = getIntent().getExtras();
        photoUrl = (Uri) extras.get("url");
        email = (String) extras.get("email");

        navigationView = findViewById(R.id.nav_view);
        menuProfileImage =findViewById(R.id.menu_profile_image);
        View view = navigationView.getHeaderView(0);
        drawerProfileImage = view.findViewById(R.id.drawer_profile_image);
        drawerEmailId = view.findViewById(R.id.drawer_emailId);
        drawerEmailId.setText(""+email);
        downloadImage = new DownloadImage(menuProfileImage, drawerProfileImage);
        downloadImage.execute(photoUrl.toString());

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        Log.d(TAG, "onNavigationItemSelected: the item is clicked" );

                        switch (menuItem.getItemId()) {
                            case R.id.menu_allmap:
                                if (mCoordinates.size() > 0) {
                                    Intent intent1 = new Intent(that, MapsActivity.class);

                                    ArrayList<Model> pointsExtra = new ArrayList<Model>();
                                    for (GeoPoint geo : mCoordinates) {
                                        pointsExtra.add(new Model(geo));
                                    }
                                    intent1.putExtra("AllMap", true);
                                    intent1.putExtra("Names", mNames);
                                    intent1.putExtra("geopoints", pointsExtra);
                                    that.startActivity(intent1);
                                } else {
                                    Toast.makeText(that, "Nothing to show in the Map", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            case R.id.menu_logout:
                                LoginActivity.signOut();
                                Intent intent = new Intent(that, LoginActivity.class);
                                that.startActivity(intent);
                                finish();
                                return true;
                            case R.id.menu_about:
                                return false;
                            case R.id.menu_profile:
                                return false;
                            default:
                                return MainActivity.super.onOptionsItemSelected(menuItem);
                        }
                    }
                });

        if (isServicesOK()) {
            initCustomer();
        }

    }

    @OnClick(R.id.menu_button)
    public void OnMenuButtonClick(){
        mDrawerLayout.openDrawer(GravityCompat.START);
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

    private void initCustomer() {
        db.collection("daily_delivery")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData().get("name"));
                                mNames.add(String.valueOf(document.getData().get("name")));
                                dStatus.add((String) document.getData().get("status"));
                                mCoordinates.add((GeoPoint) document.getData().get("coordinates"));
                                mAddress.add((String) document.getData().get("address"));
                                mContact.add((Number) document.getData().get("contact"));
                                mCustomerId.add((Number) document.getData().get("customer_id"));
                                mDeliveryAgent.add((String) document.getData().get("delivery_agent"));
                                mQuantity.add((Number) document.getData().get("quantity"));
                            }
                            Log.d(TAG, "onComplete: " + mCoordinates.toString());
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
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mNames, mAddress, mQuantity, mContact, dStatus, mCoordinates, mCustomerId, mDeliveryAgent, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
