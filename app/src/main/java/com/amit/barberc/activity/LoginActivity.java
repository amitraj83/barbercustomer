package com.amit.barberc.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amit.barberc.R;
import com.amit.barberc.util.Global;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText txt_name, txt_phone, txt_code;
    private LinearLayout llt_code;
    private Button btn_send;

    private FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Global.initUIActivity(this);

        mAuth = FirebaseAuth.getInstance();

        initUIView();
        initFireBaseCallbacks();
    }

    private void initUIView() {
        txt_name = findViewById(R.id.txt_login_name);
        txt_phone = findViewById(R.id.txt_login_phone);
        txt_code = findViewById(R.id.txt_login_code);

        llt_code = findViewById(R.id.llt_login_code);
        llt_code.setVisibility(View.GONE);

        btn_send = findViewById(R.id.btn_login_send);
    }

    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Toast.makeText(LoginActivity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;

                llt_code.setVisibility(View.VISIBLE);
                count = 60;

                Timer t = new Timer();
                t.scheduleAtFixedRate(new TimerTask() {
                                          @Override
                                          public void run() {
                                              //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                              count = count - 1;
                                              if (count < 0) {
                                                  t.cancel();
                                                  btn_send.setText(getResources().getString(R.string.login_resend));
                                              } else {
                                                  btn_send.setText(String.format("%ds", count));
                                              }
                                          }
                                      },
                        0,
                        1000);
            }
        };
    }

    public void onClickBtnSend(View view) {
        if (count > 0) {
            return;
        }

        String nameStr = txt_name.getText().toString();
        if (nameStr.length() == 0) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(5)
                    .playOn(findViewById(R.id.llt_login_name));
            return;
        }

        String phoneStr = txt_phone.getText().toString();
        if (phoneStr.length() == 0) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(5)
                    .playOn(findViewById(R.id.llt_login_phone));
            return;
        }

        Global.hideKeyboard(this);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+856"+ phoneStr,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }

    public void onClickBtnLogin(View view) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, txt_code.getText().toString());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            //
                        }
                    }
                });
    }
}
