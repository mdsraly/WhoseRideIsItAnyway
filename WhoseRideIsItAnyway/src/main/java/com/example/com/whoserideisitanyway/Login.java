package com.example.com.whoserideisitanyway;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class Login extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    public static final String TWITTER_KEY = "O4je46XIGtWynm7AzSSjaxW1F";
    public static final String TWITTER_SECRET = "5JsddbXgKL8tHGPy24s5syB19IOMJ6MSkJCdljQ75MjFdUpFua";
    public static String phoneNum;
    UserSessionManager usersession;
    TextView digitsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       //session management
        usersession = new UserSessionManager(getApplicationContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());
        //DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton = (TextView) findViewById(R.id.auth_button);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        digitsButton.setTypeface(customFont);

        final AuthCallback digitsCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model

                Toast.makeText(getApplicationContext(), "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_LONG).show();
                usersession.createUserLoginSession("User",
                        phoneNumber);
                phoneNum=phoneNumber;
                Intent i = new Intent(Login.this,HomeActivity.class);
                startActivity(i);
                finish();
            }


            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        };
        digitsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Digits.authenticate(digitsCallback);
            }
        });

    }

}
