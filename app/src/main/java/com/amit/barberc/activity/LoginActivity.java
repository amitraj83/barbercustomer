package com.amit.barberc.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.amit.barberc.R;
import com.amit.barberc.util.Global;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Global.initUIActivity(this);

        initUIView();
    }

    private void initUIView() {
        //
    }

    public void onClickBtnSend(View view) {
        //
    }

    public void onClickBtnLogin(View view) {
        //
    }
}
