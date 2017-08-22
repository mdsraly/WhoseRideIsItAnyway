package com.example.com.whoserideisitanyway;


import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Permission;
import java.util.HashMap;

public class Browse extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public double lat;
    public double lon;
    public static final String DATA_URL = "http://fustlearn.com/wmr/browse_map.php";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_ID = "upload_id";
    private static final String ID = "upload_id";
    private static final String TIME = "time";

    MarkerDetails details;


    private ProgressDialog loading;
    ImageButton list;
    public String latitude;
    public String longitude;
    public String date;
    public String time;
    public Double myLat;
    public Double myLon;
    private static final String TAG = "MyTag";

    private InterstitialAd mInterstitialAd;

    public String upload_id;
    public static final String JSON_ARRAY = "result";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getData();

        //To load Interstitial Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3076799194669466/9964785835");

        AdRequest adRequestI = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();


        mInterstitialAd.loadAd(adRequestI);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Browse.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Browse.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Browse.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);


            }

        }
                   list = (ImageButton)findViewById(R.id.ListView);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.v(TAG, "LOADED!");
                }
         Intent i = new Intent(Browse.this,GridSearch.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(this,"granted",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

    }
    private void getData() {

        loading = ProgressDialog.show(this, "Please wait...", "Loading Map...", false, true);

        details = new MarkerDetails();
        String url = DATA_URL;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Browse.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){

        try {
            int i;
          final  HashMap<String, HashMap> extraMarkerInfo = new HashMap<String, HashMap>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            final int number = result.length();

            for( i=0;i<number;i++) {
            final JSONObject collegeData = result.getJSONObject(i);
                upload_id = collegeData.getString(KEY_ID);
                latitude = collegeData.getString(KEY_LAT);
                longitude = collegeData.getString(KEY_LON);
                date = collegeData.getString(KEY_DATE);
                time = collegeData.getString(KEY_TIME);


                details.setDate(date);
                details.setId(upload_id);
                details.setTime(time);
            lat = Double.parseDouble(latitude);
            lon = Double.parseDouble(longitude);

            LatLng current = new LatLng(lat, lon);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(current));


                Marker marker = mMap.addMarker(new MarkerOptions().position(current).title("Tap for Details" +
                        "").snippet(details.getDate()));
                HashMap<String, String> data = new HashMap<String, String>();

                data.put(ID,upload_id);
                data.put(TIME,time);
                extraMarkerInfo.put(marker.getId(),data);



                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override

                    public void onInfoWindowClick(Marker marker) {
                        final HashMap<String, String> marker_data = extraMarkerInfo.get(marker.getId());
                        Intent intent = new Intent(Browse.this, Details.class);
                        intent.putExtra("time",marker_data.get(TIME));
                        intent.putExtra("id",marker_data.get(ID));
                        startActivity(intent);


                    }
                });
                GPSTracker gps = new GPSTracker(this);
                if(gps.canGetLocation()) {
                    myLat=gps.getLatitude();
                    myLon=gps.getLongitude();
                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLat, myLon))      // Sets the center of the map to location user
                    .zoom(18)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
