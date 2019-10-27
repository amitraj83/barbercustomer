package com.amit.barberc.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amit.barberc.MainActivity;
import com.amit.barberc.R;
import com.amit.barberc.util.Global;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import com.dkv.bubblealertlib.AppConstants;
import com.dkv.bubblealertlib.AppLog;
import com.dkv.bubblealertlib.BblContentFragment;
import com.dkv.bubblealertlib.BblDialog;
import com.dkv.bubblealertlib.ConstantsIcons;
import com.dkv.bubblealertlib.IAlertClickedCallBack;

import com.fevziomurtekin.customprogress.Dialog;
import com.fevziomurtekin.customprogress.Type;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText txt_name, txt_phone, txt_code;
    private TextView lbl_count;
    private LinearLayout llt_code;
    private Button btn_login;
    private CountryCodePicker ccp;
    private Dialog progressbar;

    private FirebaseAuth mAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private int count = 0;
    private boolean isVerify = true;
    private boolean isLocation = false;

    private LocationManager mLocationManager;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            if (isLocation) {
                return;
            }
            isLocation = true;
//            Global.gLat = location.getLatitude() - 20;
//            Global.gLot = -location.getLongitude() - 20;
            Global.gLat = 102.634751;
            Global.gLan = 17.956487;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //
        }

        @Override
        public void onProviderEnabled(String provider) {
            //
        }

        @Override
        public void onProviderDisabled(String provider) {
            //
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Global.initUIActivity(this);

        onCheckAllPermission();
    }

    private void onCheckAllPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 100);
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        initUIView();
        initFireBaseCallbacks();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCheckAllPermission();
            } else {
                Toast.makeText(this, "All permitions are not setted.", Toast.LENGTH_LONG).show();

                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void initUIView() {
        isLocation = false;

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0
                , 0, mLocationListener);

        txt_name = findViewById(R.id.txt_login_name);
        txt_phone = findViewById(R.id.txt_login_phone);
        txt_code = findViewById(R.id.txt_login_code);

        lbl_count = findViewById(R.id.lbl_login_count);

        llt_code = findViewById(R.id.llt_login_code);
        llt_code.setVisibility(View.GONE);

        btn_login = findViewById(R.id.btn_login);

        ccp = findViewById(R.id.ccp_login);

        progressbar = findViewById(R.id.progress);
        progressbar.settype(Type.RIPPLE);
    }

    void initFireBaseCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                progressbar.gone();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressbar.gone();
                Toast.makeText(LoginActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressbar.gone();

                Toast.makeText(LoginActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                mVerificationId = verificationId;

                llt_code.setVisibility(View.VISIBLE);
                btn_login.setText(getResources().getString(R.string.login_now));

                isVerify = false;

                count = 60;

                Timer t = new Timer();
                t.scheduleAtFixedRate(new TimerTask()
                        {
                            @Override
                            public void run() {
                                //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                count = count - 1;
                                if (count < 0) {
                                    t.cancel();
                                    btn_login.setText(getResources().getString(R.string.login_resend));
                                    runOnUiThread(() -> llt_code.setVisibility(View.GONE));

                                    isVerify = true;
                                } else {
                                    runOnUiThread(() -> lbl_count.setText(String.format("%ds", count)));
                                }
                            }
                        },
                        0,
                        1000);
            }
        };
    }

    private void onClickBtnVerify() {
        if (count > 0) {
            progressbar.gone();
            return;
        }

        String nameStr = txt_name.getText().toString();
        if (nameStr.length() == 0) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(0)
                    .playOn(findViewById(R.id.llt_login_name));
            progressbar.gone();
            return;
        }

        String phoneStr = txt_phone.getText().toString();
        if (phoneStr.length() == 0) {
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(0)
                    .playOn(findViewById(R.id.llt_login_phone));
            progressbar.gone();
            return;
        }

        Global.hideKeyboard(this);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                ccp.getFullNumberWithPlus() + phoneStr,
                30,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    public void onClickBtnLogin(View view) {
//        if (true) {
//            Global.gUser.id = "SpGo6vRCLAdqir7aQoQIVbc6uFc2";
//            Global.gUser.name = "Noma";
//            Global.gUser.phone = "2055592450";
//
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference mRef = database.getReference("Customers").child(Global.gUser.id);
//            mRef.setValue(Global.gUser);
//
//            Global.showOtherActivity(LoginActivity.this, MainActivity.class, -1);
//            return;
//        }

        if (true) {
            Global.gUser.id = "xarLSUpYEUUVavCXYHUwZdnK2Am1";
            Global.gUser.name = "Black Gold";
            Global.gUser.phone = "2096227257";

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference mRef = database.getReference("Customers").child(Global.gUser.id);
            mRef.setValue(Global.gUser);

            Global.showOtherActivity(LoginActivity.this, MainActivity.class, -1);
            return;
        }

//        if (true) {
//            Global.gUser.id = "SdpU64FW3hSbJvSnjV7Ra0Vstyu1";
//            Global.gUser.name = "Sayavong";
//            Global.gUser.phone = "2091659709";
//
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference mRef = database.getReference("Customers").child(Global.gUser.id);
//            mRef.setValue(Global.gUser);
//
//            Global.showOtherActivity(LoginActivity.this, MainActivity.class, -1);
//            return;
//        }

//        if (true) {
//            Global.gUser.id = "vrFfGuysKUOh6ASm4yMALXbyjap2";
//            Global.gUser.name = "Berk";
//            Global.gUser.phone = "2058076889";
//
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference mRef = database.getReference("Customers").child(Global.gUser.id);
//            mRef.setValue(Global.gUser);
//
//            Global.showOtherActivity(LoginActivity.this, MainActivity.class, -1);
//            return;
//        }

        progressbar.show();

        if (isVerify) {
            onClickBtnVerify();
            return;
        }

        String codeStr = txt_code.getText().toString();
        if (codeStr.length() == 0) {
            progressbar.gone();
            YoYo.with(Techniques.Shake)
                    .duration(700)
                    .repeat(0)
                    .playOn(findViewById(R.id.llt_login_code));
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, codeStr);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();

                        Global.gUser.id = currentUser.getUid();
                        Global.gUser.name = txt_name.getText().toString();
                        Global.gUser.phone = txt_phone.getText().toString();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference mRef = database.getReference("Customers").child(Global.gUser.id);
                        mRef.setValue(Global.gUser);

                        Global.showOtherActivity(LoginActivity.this, MainActivity.class, -1);
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_faild_verify), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressbar.gone();
                });
    }

    @Override
    public void onBackPressed() {
        try {
            BblContentFragment fragment = BblContentFragment.newInstance(AppConstants.TAG_FEEDBACK_SUCCESS);
            String content = "Do you really close this application?";
            if (TextUtils.isEmpty(content)) {
                content = getString(com.dkv.bubblealertlib.R.string.err_server_error);
            }
            fragment.setContent(content, "Yes", "No", null, "Exit");
            fragment.setClickedCallBack(new IAlertClickedCallBack() {
                @Override
                public void onOkClicked(String tag) {
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }

                @Override
                public void onCancelClicked(String tag) {
                    //
                }

                @Override
                public void onExitClicked(String tag) {

                }
            });
            BblDialog sampleDialog = new BblDialog();
            sampleDialog.setContentFragment(fragment
                    , com.dkv.bubblealertlib.R.layout.layout_bbl_content
                    , LayoutInflater.from(this), content
                    , ConstantsIcons.ALERT_ICON_INFO, this);
            sampleDialog.setDisMissCallBack(null);
            getSupportFragmentManager().beginTransaction().add(sampleDialog, "Test").commitAllowingStateLoss();
        } catch (Exception e) {
            AppLog.logException(AppConstants.TAG_FEEDBACK_SUCCESS, e);
        }
    }

}
