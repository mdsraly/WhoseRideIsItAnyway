package com.example.com.whoserideisitanyway;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.ads.MobileAds;

public class Check extends Activity {

    UserSessionManager session;
    Intent i;
    Button b;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3076799194669466~7837407833");

                checkLogin();
    }
    public void checkLogin()
    {
        session = new UserSessionManager(getApplicationContext());
        if(session.isUserLoggedIn()) {
            i = new Intent(Check.this, HomeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             Thread t = new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(1500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        startActivity(i);
                        finish();
                    }
                }


            };
            t.start();

        }

        else
        {
            i = new Intent(Check.this,Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    startActivity(i);

                    finish();




        }

    }




}
