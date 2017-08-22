package com.example.com.whoserideisitanyway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginNew extends AppCompatActivity {

    TextView signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);


        signin = (TextView)findViewById(R.id.auth_button);






        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(LoginNew.this,Login.class);
                startActivity(it);
                //Toast.makeText(LoginNew.this,"Hello",Toast.LENGTH_LONG).show();


            }
        });

    }
}
