package com.amit.barberc.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;

import com.amit.barberc.R;
import com.amit.barberc.model.BarberUser;
import com.amit.barberc.model.CustomerUser;
import com.amit.barberc.model.DistanceModel;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Global {
    static final public String AppTag = "com.amit.barberc";

    static final public String KeyIsQueue = "isqueue";
    static final public String KeyQueueDate = "queuedate";
    static final public String KeyQueueID = "queueid";

    static public CustomerUser gUser = new CustomerUser();
    static public BarberUser gBarber = new BarberUser();
    static public List<BarberUser> gBarberUsers = new ArrayList<>();
    static public List<DistanceModel> gBarberDistences = new ArrayList<>();

    static public boolean gIsQueue = false;
    static public double gLat, gLan;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showOtherActivity (Activity activity, Class<?> cls, int direction) {
        Intent myIntent = new Intent(activity, cls);
        ActivityOptions options;
        switch (direction) {
            case 0:
                options = ActivityOptions.makeCustomAnimation(activity, R.anim.slide_in_right, R.anim.slide_out_left);
                activity.startActivity(myIntent, options.toBundle());
                break;
            case 1:
                options = ActivityOptions.makeCustomAnimation(activity, R.anim.slide_in_left, R.anim.slide_out_right);
                activity.startActivity(myIntent, options.toBundle());
                break;
            default:
                activity.startActivity(myIntent);
                break;
        }
        activity.finish();
    }

    public static void initUIActivity (Activity activity) {
        // Change Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().setStatusBarColor(activity.getColor(R.color.custom_White));
        } else {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.custom_White));
        }
    }

    static public double onCalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km

        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

}
