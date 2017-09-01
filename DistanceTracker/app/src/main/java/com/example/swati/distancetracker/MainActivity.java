package com.example.swati.distancetracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

// Reference : https://developer.android.com/training/location/receive-location-updates.html

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate","Just entered onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText hostField = (EditText) findViewById(R.id.host);
        hostField.setText("10.0.2.2:9000");
        // Before starting the app, necessary to ask for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        else {
            createLocationRequest();
            buildLocationSettingsRequest();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    Log.d("location_update",Double.toString(location.getLatitude()));
                    String user = ((EditText)findViewById(R.id.username)).toString();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    sendLocationToPlay(user,latitude,longitude);
                }
            }
        };

        Button button = (Button) findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d("inside_onclick","inside onclick");
                startLocationUpdates();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    createLocationRequest();
                    buildLocationSettingsRequest();
                } else {
                }
                break;
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
    private void startLocationUpdates() {
        Log.i("start_location","in start location");
       /**
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("location settings", "All location settings are satisfied.");
                        //Only after location setting is enabled
       **/
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, null);
    }

    // Referred : https://stackoverflow.com/questions/40079174/how-to-send-a-post-request-with-json-body-using-volley
    public void sendLocationToPlay(String user, double latitude, double longitude) {
        RequestQueue locationQueue = Volley.newRequestQueue(this);
        String playServerUrl ="http://10.0.2.2:9000/location_update";
        HashMap<String, String> distanceTrackerInfo = new HashMap<String, String>();
        distanceTrackerInfo.put("user", user);
        distanceTrackerInfo.put("latitude", Double.toString(latitude));
        distanceTrackerInfo.put("longitude", Double.toString(longitude));
        System.out.println("In send location");
        JsonObjectRequest requestLocationJSON = new JsonObjectRequest(Request.Method.POST, playServerUrl, new JSONObject(distanceTrackerInfo),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("JSON_request", "Inside sendLocationToPlay");
                            double distanceTravelled = (double)response.get("distance");
                            TextView displayBox = (TextView) findViewById(R.id.display_distance);
                            displayBox.append("\nDistance covered " + distanceTravelled+" in metres");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        locationQueue.add(requestLocationJSON);
    }
}

