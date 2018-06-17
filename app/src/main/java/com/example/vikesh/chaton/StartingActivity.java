package com.example.vikesh.chaton;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class StartingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnSignin, btnSignUp;
    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        init();
    }

    private void init() {
        relativeLayout = (RelativeLayout)findViewById(R.id.starting_layout);
        animationDrawable = (AnimationDrawable)relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(2500);
        animationDrawable.start();
        btnSignin = (Button) findViewById(R.id.btnsignin_starting_activty);
        btnSignUp = (Button) findViewById(R.id.btnsignup_starting_activty);
        btnSignin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsignin_starting_activty:
                startActivity(new Intent(StartingActivity.this, SignInActivity.class));
                break;
            case R.id.btnsignup_starting_activty:
                startActivity(new Intent(StartingActivity.this, SignUpActivity.class));
                break;
        }
    }
}
