package com.example.com.whoserideisitanyway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Details extends AppCompatActivity {

    public String id;
    public String lat;
    public String lon;
    public String time;
    public static final String DATA_URL = "http://fustlearn.com/wmr/details.php?id=";
    public static final String IMAGE1 = "image1";
    public static final String IMAGE2 = "image2";
    public static final String IMAGE3 = "image3";
    public static final String IMAGE4 = "image4";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_TIME = "time";
    public static final String JSON_ARRAY = "result";
    private NetworkImageView imageView1;
    private NetworkImageView imageView2;
    private NetworkImageView imageView3;
    private NetworkImageView imageView4;
    private CoordinatorLayout coordinatorLayout;

    private NetworkImageView largeimageView;
    private Button closeView;
    ImageButton imageButton;
    private ProgressDialog loading;
    private ImageLoader imageLoader;

    private InterstitialAd mInterstitialAd;

    public String image1,image2,image3,image4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView1 = (NetworkImageView)findViewById(R.id.imageView);
        imageView2 = (NetworkImageView)findViewById(R.id.imageView2);
        imageView3 = (NetworkImageView)findViewById(R.id.imageView3);
        imageView4 = (NetworkImageView)findViewById(R.id.imageView4);
        largeimageView = (NetworkImageView)findViewById(R.id.expanded_image);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        closeView = (Button)findViewById(R.id.closeView);
        imageButton =  (ImageButton)findViewById(R.id.imageButton);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        id = b.getString("id");
        time = b.getString("time");
        getData();

        Snackbar snackbar = Snackbar.make(coordinatorLayout, time, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();





        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                largeimageView.setVisibility(View.INVISIBLE);
                closeView.setVisibility(View.INVISIBLE);
            }
        });
        //To load Interstitial Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3076799194669466/9964785835");

        AdRequest adRequestI = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();


        mInterstitialAd.loadAd(adRequestI);



//To load Ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    public void mapView(View view) {

        Intent i = new Intent(Details.this,FinalNav.class);
        i.putExtra("id",id);
        i.putExtra("lat",lat);
        i.putExtra("lon",lon);
        startActivity(i);

    }
    private void getData() {

        loading = ProgressDialog.show(this, "Please wait...", "Fetching...", false, false);

        String url = DATA_URL+id;


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
                        Toast.makeText(Details.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {

        try {
            int i;

            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(JSON_ARRAY);
            final int number = result.length();
            for (i = 0; i < number; i++) {
                JSONObject collegeData = result.getJSONObject(i);
                lat = collegeData.getString(KEY_LAT);
                lon = collegeData.getString(KEY_LON);
                image1 = collegeData.getString(IMAGE1);
                image2 = collegeData.getString(IMAGE2);
                image3 = collegeData.getString(IMAGE3);
                image4 = collegeData.getString(IMAGE4);
                time = collegeData.getString(KEY_TIME);




           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadImage1();
        loadImage2();
        loadImage3();
        loadImage4();


    }
    private void loadImage1(){
        //String url = editTextUrl.getText().toString().trim();


                imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(image1, ImageLoader.getImageListener(imageView1,
                R.drawable.load1, android.R.drawable
                        .ic_dialog_alert),600,600);
        imageView1.setImageUrl(image1, imageLoader);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                largeimageView.setVisibility(View.VISIBLE);
                closeView.setVisibility(View.VISIBLE);
                largeimageView.setImageUrl(image1, imageLoader);
            }
        });
    }
    private void loadImage2(){
        //String url = editTextUrl.getText().toString().trim();


        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(image2, ImageLoader.getImageListener(imageView2,
                R.drawable.load1, android.R.drawable
                        .ic_dialog_alert));
        imageView2.setImageUrl(image2, imageLoader);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                largeimageView.setVisibility(View.VISIBLE);
                closeView.setVisibility(View.VISIBLE);
                largeimageView.setImageUrl(image2, imageLoader);
            }
        });
    }
    private void loadImage3(){
        //String url = editTextUrl.getText().toString().trim();


        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(image3, ImageLoader.getImageListener(imageView3,
                R.drawable.load1, android.R.drawable
                        .ic_dialog_alert));
        imageView3.setImageUrl(image3, imageLoader);
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                largeimageView.setVisibility(View.VISIBLE);
                closeView.setVisibility(View.VISIBLE);
                largeimageView.setImageUrl(image3, imageLoader);
            }
        });
    }
    private void loadImage4(){
        //String url = editTextUrl.getText().toString().trim();


        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                .getImageLoader();
        imageLoader.get(image4, ImageLoader.getImageListener(imageView4,
                R.drawable.load1, android.R.drawable
                        .ic_dialog_alert));
        imageView4.setImageUrl(image4, imageLoader);

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                largeimageView.setVisibility(View.VISIBLE);
                closeView.setVisibility(View.VISIBLE);
                largeimageView.setImageUrl(image4, imageLoader);
            }
        });
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

}

