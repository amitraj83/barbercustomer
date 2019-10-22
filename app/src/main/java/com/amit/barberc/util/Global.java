package com.amit.barberc.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.amit.barberc.R;

import java.io.File;
import java.io.FileWriter;

public class Global {

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

    public static void writeToFile(String data, String filename, Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), context.getString(R.string.app_name));
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return;
            }
        }
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, filename + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(data);
            writer.flush();
            writer.close();

            Toast.makeText(context, gpxfile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isCheckSpelling (String str) {
        if (str.length() == 0) {
            return false;
        }
        String str_first_able = "abcdefghijklmnopqrstuvwxyz";
        String str_first = Character.toString(str.charAt(0));
        if (!str_first_able.contains(str_first)) {
            return false;
        }
        String str_able = "abcdefghijklmnopqrstuvwxyz_1234567890";
        for (int i = 1; i < str.length(); i++) {
            String letter = Character.toString(str.charAt(i));
            if (!str_able.contains(letter)) {
                return false;
            }
        }
        return true;
    }

    public static void initUIActivity (Activity activity) {

        // Change Status Bar Color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().setStatusBarColor(activity.getColor(R.color.custom_White));
        } else {
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.custom_White));
        }

        // Hide Navigation Bar (Full Screen)
//        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
//        final View decorView = activity.getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                    decorView.setSystemUiVisibility(flags);
//                }
//            }
//        });
    }

}
