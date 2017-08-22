package com.example.com.whoserideisitanyway;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class Capture extends AppCompatActivity {

ImageView viewRear,viewFront,viewLeft,viewRight;
EditText comments;
    TextView upload;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmap3;
    private Bitmap bitmap4;
    private int PICK_IMAGE_REQUEST = 1;

    String image1,image2,image3,image4,imagename;

    private String UPLOAD_URL ="http://fustlearn.com/wmr/upload.php";
    private InterstitialAd mInterstitialAd;
    private static final String TAG = "MyTag";

    private String KEY_IMAGE1 = "image1";
    private String KEY_IMAGE2 = "image2";
    private String KEY_IMAGE3 = "image3";
    private String KEY_IMAGE4 = "image4";
    private String KEY_COMMENTS = "comments";
    private String KEY_DATE = "date";
    private String KEY_TIME = "time";
    private String KEY_PHONE = "phone";
    private String KEY_LAT = "latitude";
    private String KEY_LON = "longitude";
    public Double lat;
    public Double lon;
    File f,WMR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Capture.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Capture.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Capture.this,
                        new String[]{Manifest.permission.CAMERA},
                        1);


            }

        }


        if (!Fabric.isInitialized()) {
            TwitterAuthConfig authConfig = new TwitterAuthConfig(Login.TWITTER_KEY,Login.TWITTER_SECRET);
            Fabric.with(this, new TwitterCore(authConfig));
        }
        viewRear = (ImageView)findViewById(R.id.imageView4);
        viewFront = (ImageView)findViewById(R.id.imageView1);
        viewRight = (ImageView)findViewById(R.id.imageView3);
        viewLeft = (ImageView)findViewById(R.id.imageView2);
        comments = (EditText)findViewById(R.id.note);
        upload = (TextView) findViewById(R.id.upload);

        //imagename = "temp1.jpg";
        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()) {
        lat=gps.getLatitude();
        lon=gps.getLongitude();
        }

        viewRear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage("rear");
                imagename = "temp4.jpg";
            }
        });
        viewFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage("front");
                imagename = "temp1.jpg";

            }
        });
        viewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage("right");
                imagename = "temp2.jpg";
            }
        });
        viewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage("left");
                imagename = "temp3.jpg";
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(hasImage(viewFront) && hasImage(viewLeft) && hasImage(viewRear) && hasImage(viewRight))
            {
                uploadImage();
            }
            else
            {
                Toast.makeText(Capture.this,"Please Upload All Images!!",Toast.LENGTH_SHORT).show();
            }
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

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        upload.setTypeface(customFont);
    }


    public void selectImage(final String view) {

            final CharSequence[] options = {"Take Photo", "Cancel"};

            AlertDialog.Builder builder = new AlertDialog.Builder(Capture.this);
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        WMR = new File(Environment.getExternalStorageDirectory(), "WMR");
                        WMR.mkdirs();
                        f = new File(WMR, view+".jpg");

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        if (view.equals("front"))
                            startActivityForResult(intent, 11);
                        else if (view.equals("right"))
                            startActivityForResult(intent, 21);
                        else if (view.equals("rear"))
                            startActivityForResult(intent, 31);
                        else if (view.equals("left"))
                            startActivityForResult(intent, 41);
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.v(TAG, "LOADED!");
        }

    }
    public Bitmap get_Reduce_bitmap_Picture(String imagePath) {

        int ample_size = 16;
        // change ample_size to 32 or any power of 2 to increase or decrease bitmap size of image


        Bitmap bitmap = null;
        BitmapFactory.Options bitoption = new BitmapFactory.Options();
        bitoption.inSampleSize = ample_size;

        Bitmap bitmapPhoto = BitmapFactory.decodeFile(imagePath, bitoption);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        int orientation = exif
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        Matrix matrix = new Matrix();

        if ((orientation == 3)) {
            matrix.postRotate(180);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else if (orientation == 6) {
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else if (orientation == 8) {
            matrix.postRotate(270);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        } else {
            matrix.postRotate(0);
            bitmap = Bitmap.createBitmap(bitmapPhoto, 0, 0,
                    bitmapPhoto.getWidth(), bitmapPhoto.getHeight(), matrix,
                    true);

        }

        return bitmap;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 11) {
                File f = new File(WMR.toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("front.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {

                   BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    //bitmap1 =BitmapFactory.decodeFile(f.getAbsolutePath(),
                      //      bitmapOptions);

                     bitmap1 = get_Reduce_bitmap_Picture(f.getAbsolutePath());

                    viewFront.setImageBitmap(bitmap1);

                    /*String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*bitmap1 = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewFront.setImageBitmap(bitmap1);*/


            }
            else if (requestCode == 21) {
                File f = new File(WMR.toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("right.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {

                   BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    //bitmap2 =BitmapFactory.decodeFile(f.getAbsolutePath(),
                      //      bitmapOptions);

                    bitmap2  = get_Reduce_bitmap_Picture(f.getAbsolutePath());
                    viewRight.setImageBitmap(bitmap2);

                   /*String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*bitmap2 = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewRight.setImageBitmap(bitmap2);*/

            }
            else if (requestCode == 31) {
                File f = new File(WMR.toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("rear.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    //bitmap3 =BitmapFactory.decodeFile(f.getAbsolutePath(),
                      //      bitmapOptions);

                    bitmap3 =get_Reduce_bitmap_Picture(f.getAbsolutePath());
                    viewRear.setImageBitmap(bitmap3);

                    /*String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*bitmap3 = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap3.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewRear.setImageBitmap(bitmap3);*/

            }
            else if (requestCode == 41) {
                File f = new File(WMR.toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("left.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                    //bitmap4 = BitmapFactory.decodeFile(f.getAbsolutePath(),
                      //      bitmapOptions);

                    bitmap4  = get_Reduce_bitmap_Picture(f.getAbsolutePath());
                    viewLeft.setImageBitmap(bitmap4);

                    /*String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*bitmap4 = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap4.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());

                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewLeft.setImageBitmap(bitmap4);*/

            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private boolean hasImage(ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

       if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }
    private void uploadImage()
    {
    //Showing the progress dialog
    final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
    StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                        //Disimissing the progress dialog
                    loading.dismiss();
                    //Showing toast message of the response
                    Toast.makeText(Capture.this, s , Toast.LENGTH_LONG).show();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    //Dismissing the progress dialog
                    loading.dismiss();

                    //Showing toast
                    Toast.makeText(Capture.this,volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }){
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            //Converting to String

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (viewFront.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500,getTheme()).getConstantState()))
                    image1 = "";

                else
                    image1 = getStringImage(bitmap1);
            }
            else {
                if (viewFront.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500).getConstantState()))
                    image1 = "";
                 else
                    image1 = getStringImage(bitmap1);

            }


            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (viewRear.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500,getTheme()).getConstantState()))
                    image4 = "";

                else
                    image4 = getStringImage(bitmap3);
            }
            else {
                if (viewRear.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500).getConstantState()))
                    image4 = "";
                 else
                    image4 = getStringImage(bitmap3);

            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (viewRight.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500,getTheme()).getConstantState()))
                    image2 = "";

                else
                    image2 = getStringImage(bitmap2);
            }
            else {
                if (viewRight.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500).getConstantState()))
                    image2 = "";
                 else
                    image2 = getStringImage(bitmap2);

            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (viewLeft.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500,getTheme()).getConstantState()))
                    image3 = "";

                else
                    image3 = getStringImage(bitmap4);
            }
            else {
                if (viewLeft.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.capture1_500x500).getConstantState()))
                    image3 = "";
                 else
                    image3 = getStringImage(bitmap4);

            }


            //Getting Image Name
            //String phone_num = "8805009086";
            UserSessionManager sessionManager = new UserSessionManager(Capture.this);
           String phone_num = sessionManager.getPhone();
            DateFormat df = new SimpleDateFormat("dd MMM yyyy ");
            String date = df.format(Calendar.getInstance().getTime());
            String comment = comments.getText().toString();
            df = new SimpleDateFormat("dd MMM yyyy, HH:mm");
            String time = df.format(Calendar.getInstance().getTime());
            //Creating parameters
            Map<String,String> params = new Hashtable<String, String>();

            //Adding parameters
            params.put(KEY_IMAGE1, image1);
            params.put(KEY_IMAGE2, image2);
            params.put(KEY_IMAGE3, image3);
            params.put(KEY_IMAGE4, image4);
            params.put(KEY_COMMENTS, comment);
            params.put(KEY_LAT, Double.toString(lat));
            params.put(KEY_LON, Double.toString(lon));
            params.put(KEY_DATE, date);
            params.put(KEY_TIME, time);
            params.put(KEY_PHONE, phone_num);

            //returning parameters
            return params;
        }
    };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    //Creating a Request Queue
    RequestQueue requestQueue = Volley.newRequestQueue(this);

    //Adding request to the queue
    requestQueue.add(stringRequest);
}
}
