package com.location.navigo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.location.navigo.db.Model;
import com.location.navigo.maps.DirectionsParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    LocationManager locationManager;
    LocationListener locationListener;
    Location myLocation;
    LatLng currentLocation;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSTION_REQUEST_CODE = 1234;
    private static final int DEFAULT_BOUNDS = 200;

    boolean mLocationPermissionsGranted = false;

    private long UPDATE_INTERVAL = 60 * 1000 * 2;
    private long DISTANCE_RADIUS = 500;

    ArrayList<Model> pointsExtra = new ArrayList<Model>();
    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
    ArrayList<String> mNames = new ArrayList<>();


    ArrayList<LatLng> listPoints = new ArrayList<LatLng>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        CheckPermission();

        Bundle extras = getIntent().getExtras();
        boolean status = (boolean) extras.get("AllMap");


        if (status) {
            mNames = (ArrayList<String>) extras.get("Names");
            pointsExtra = getIntent().getParcelableArrayListExtra("geopoints");


            for (Model point : pointsExtra) {
                points.add(point.getGeoPoint());
            }
            Log.d(TAG, "onCreate: " + points);

            for (GeoPoint geoPnt : points) {
                Log.i("for Loop", String.valueOf(geoPnt));

                double latitude = geoPnt.getLatitude();
                double longitude = geoPnt.getLongitude();
                listPoints.add(new LatLng(latitude, longitude));
            }
        } else {
            mNames.add((String) extras.get("Names"));
            double lat = (double) extras.get("latitude");
            double lon = (double) extras.get("longitude");
            listPoints.add(new LatLng(lat, lon));
        }
    }

    private void iniMap() {
        Log.d(TAG, "iniMap: initialized maps");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                iniMap();
            } else {
                ActivityCompat.requestPermissions(this, permission,
                        LOCATION_PERMISSTION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permission,
                    LOCATION_PERMISSTION_REQUEST_CODE);
        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location temp = (Location) task.getResult();
                            if (temp!=null)
                            currentLocation = new LatLng(temp.getLatitude(), temp.getLongitude());
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLngBounds latLng, int bounds) {
        Log.d(TAG, "moveCamera: moving the camera");
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLng, bounds));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        try {
            mMap = googleMap;

            if (mLocationPermissionsGranted) {
                getDeviceLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                   return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().isCompassEnabled();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                MarkerOptions markerOptions = new MarkerOptions();
                for (int i = 0; i< listPoints.size();i++) {
                    if (mNames!=null){
                    markerOptions.title(mNames.get(i));
                    }
                    markerOptions.position(listPoints.get(i));
                    mMap.addMarker(markerOptions);
                    builder.include(markerOptions.getPosition());
                }

                if (currentLocation!=null) {
                    //add my location to the map and display within the mobile screen
                    markerOptions.position(currentLocation);
                }
                builder.include(markerOptions.getPosition());

                //creating bounds to all the markers given
                LatLngBounds bounds = builder.build();

                if(listPoints.size()>1) {
                    moveCamera(bounds, DEFAULT_BOUNDS);
                }else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(listPoints.get(0),15f));
                }
            }


            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (currentLocation!=null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15f));
//                    String url = getRequestUrl(listPoints);
//                    Log.i("onMyLocationButtonClick", "user searching for the root" + url);
//                    TaskRequestDirection taskRequestDirection = new TaskRequestDirection();
//                    taskRequestDirection.execute(url);
                    return true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getRequestUrl(ArrayList<LatLng> list) {
        String url="";
        try {
            LatLng origin = currentLocation;
            LatLng dest = list.remove(0);
            //value of origin
            String str_org = "origin=" + origin.latitude + "," + origin.longitude;
            //value of destination
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
            //set value enable the sensor
//            String sensor = "sensor=false";
            //mode for find direction
            String mode = "mode=driving";
            // waypoints is been added
            String waypoints = "waypoints=";
            StringBuilder temppoints = new StringBuilder();
            int size = list.size();
            for (LatLng temp : list) {
                Log.d(TAG, "getRequestUrl: " + temp);
                temppoints.append(temp.latitude).append(",").append(temp.longitude).append("|");
            }
            waypoints = waypoints + temppoints;

            //build the full param
            String key = "key=AIzaSyBtCWUz7SPb_J3ZzYzBZBCclHuVC4iWKTE";
            String param = str_org + "&" + str_dest + "&" + waypoints + "&" + key;
            //output format
            String output = "json";
            //create url to request
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;

        }catch (Exception e){
            e.printStackTrace();
        }
        return url;
    }



    //==========================================================================
    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);


            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
                httpURLConnection.disconnect();
            }
        }
        return responseString;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSTION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result: grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Log.d(TAG, "onRequestPermissionsResult: permission failed!!!");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mLocationPermissionsGranted=true;
                    //initialize our map
                    iniMap();

                }
                break;
        }
    }

    public class TaskRequestDirection extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                Log.d(TAG, "doInBackground: "+ strings[0]);
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TaskParser taskParser = new TaskParser();

            // Invokes the thread for parsing the JSON data
            taskParser.execute(s);
        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
//                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null) {
                mMap.addPolyline(polylineOptions);
            } else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
