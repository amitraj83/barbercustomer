package com.amit.barberc.activity;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.amit.barberc.MainActivity;
import com.amit.barberc.R;
import com.amit.barberc.util.Global;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Global.initUIActivity(this);

        new Handler().postDelayed(this::onNextActivity, 1500);
    }

    private void onNextActivity() {
        Global.showOtherActivity(this, MainActivity.class, -1);
    }

}
