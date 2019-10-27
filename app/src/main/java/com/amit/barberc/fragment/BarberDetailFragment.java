package com.amit.barberc.fragment;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import com.amit.barberc.MainActivity;
import com.amit.barberc.R;
import com.amit.barberc.model.BarberUser;
import com.amit.barberc.model.CustomerUser;
import com.amit.barberc.util.Global;

import com.dkv.bubblealertlib.AppConstants;
import com.dkv.bubblealertlib.AppLog;
import com.dkv.bubblealertlib.BblContentFragment;
import com.dkv.bubblealertlib.BblDialog;
import com.dkv.bubblealertlib.ConstantsIcons;
import com.dkv.bubblealertlib.IAlertClickedCallBack;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class BarberDetailFragment extends Fragment {

    private Button btn_join, btn_leave;
    private ImageView img_avatar, img_logo;
    private TextView lbl_name, lbl_address, lbl_waiting, lbl_hour, lbl_min, lbl_phone, lbl_pertime;
    private LinearLayout llt_time, llt_more;

    private String queueDate;
    private String queueID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.detailFragment = this;

        btn_join = view.findViewById(R.id.btn_detail_join);
        btn_join.setOnClickListener(v -> {
            try {
                BblContentFragment fragment = BblContentFragment.newInstance(AppConstants.TAG_FEEDBACK_SUCCESS);
                String content = "Do you want to join the queue now?";
                if (TextUtils.isEmpty(content)) {
                    content = getString(com.dkv.bubblealertlib.R.string.err_server_error);
                }
                fragment.setContent(content, "Join", "Cancel", null, "Join Queue");
                fragment.setClickedCallBack(new IAlertClickedCallBack() {
                    @Override
                    public void onOkClicked(String tag) {
                        Global.gIsQueue = true;
                        queueID = Global.gBarber.id;

                        Calendar calendar = Calendar.getInstance();
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int day = calendar.get(Calendar.DATE);
                        queueDate = String.format("%02d-%02d", month, day);
                        onSaveQueueInfo();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference mRefQueue = database.getReference().child("Queues");
                        mRefQueue.child(Global.gBarber.id).push().setValue(Global.gUser);

                        Global.gBarber.onSetQueueWithCustomer();
                        DatabaseReference mRefBarber = database.getReference().child("Barbers");
                        mRefBarber.child(Global.gBarber.id).setValue(Global.gBarber);

                        for (int i = 0; i < Global.gBarberUsers.size(); i++) {
                            BarberUser barberUser = Global.gBarberUsers.get(i);
                            if (barberUser.id.equals(queueID)) {
                                Global.gBarberUsers.remove(barberUser);
                                Global.gBarberUsers.add(0, barberUser);
                            }
                        }

                        initUIView();
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
                        , LayoutInflater.from(getContext()), content
                        , ConstantsIcons.ALERT_ICON_SUCCESS, getContext());
                sampleDialog.setDisMissCallBack(null);
                getActivity().getSupportFragmentManager().beginTransaction().add(sampleDialog, "Test").commitAllowingStateLoss();
            } catch (Exception e) {
                AppLog.logException(AppConstants.TAG_FEEDBACK_SUCCESS, e);
            }
        });

        btn_leave = view.findViewById(R.id.btn_detail_leave);
        btn_leave.setOnClickListener(v -> {
            try {
                BblContentFragment fragment = BblContentFragment.newInstance(AppConstants.TAG_FEEDBACK_SUCCESS);
                String content = "Do you want to leave now?";
                if (TextUtils.isEmpty(content)) {
                    content = getString(com.dkv.bubblealertlib.R.string.err_server_error);
                }
                fragment.setContent(content, "Leave", "Cancel", null, "Leave Queue");
                fragment.setClickedCallBack(new IAlertClickedCallBack() {
                    @Override
                    public void onOkClicked(String tag) {
                        Global.gIsQueue = false;
                        queueID = Global.gBarber.id;

                        onSaveLeaveInfo();

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference mRefQueue = database.getReference().child("Queues");
                        Query deleteQuery = mRefQueue.child(queueID).orderByChild("id").equalTo(Global.gUser.id);
                        deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                                    appleSnapshot.getRef().removeValue();
                                }
                                initUIView();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        Global.gBarber.onUnsetQueueWithCustomer();
                        DatabaseReference mRefBarber = database.getReference().child("Barbers");
                        mRefBarber.child(Global.gBarber.id).setValue(Global.gBarber);
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
                        , LayoutInflater.from(getContext()), content
                        , ConstantsIcons.ALERT_ICON_SUCCESS, getContext());
                sampleDialog.setDisMissCallBack(null);
                getActivity().getSupportFragmentManager().beginTransaction().add(sampleDialog, "Test").commitAllowingStateLoss();
            } catch (Exception e) {
                AppLog.logException(AppConstants.TAG_FEEDBACK_SUCCESS, e);
            }
        });

        img_avatar = view.findViewById(R.id.img_detail_avatar);
        img_logo = view.findViewById(R.id.img_detail_logo);

        lbl_name = view.findViewById(R.id.lbl_detail_name);
        lbl_address = view.findViewById(R.id.lbl_detail_address);
        lbl_waiting = view.findViewById(R.id.lbl_detail_waiting);
        lbl_hour = view.findViewById(R.id.lbl_detail_hour);
        lbl_min = view.findViewById(R.id.lbl_detail_min);
        lbl_phone = view.findViewById(R.id.lbl_detail_phone);
        lbl_pertime = view.findViewById(R.id.lbl_detail_pertime);

        llt_time = view.findViewById(R.id.llt_detail_time);
        llt_more = view.findViewById(R.id.llt_detail_more);

        initUIView();
    }

    public void initUIView() {
        SharedPreferences prefs = MainActivity.mActivity.getSharedPreferences(Global.AppTag, MODE_PRIVATE);
        String queueID = prefs.getString(Global.KeyQueueID, "");

        if (Global.gBarber.avatarImgUrl.length() == 0) {
            Global.gBarber.avatarImgUrl = "url";
        }
        Picasso.with(getContext()).load(Global.gBarber.avatarImgUrl).fit().centerCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(img_avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap src = ((BitmapDrawable)img_avatar.getDrawable()).getBitmap();
                        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getContext().getResources(), src);
                        dr.setCornerRadius(100);
                        img_avatar.setImageDrawable(dr);
                    }

                    @Override
                    public void onError() {

                    }
                });

        if (Global.gBarber.logoImgUrl.length() == 0) {
            Global.gBarber.logoImgUrl = "url";
        }
        Picasso.with(getContext()).load(Global.gBarber.logoImgUrl).fit().centerCrop()
                .placeholder(R.drawable.img_barber)
                .error(R.drawable.img_barber)
                .into(img_logo);

        lbl_name.setText(Global.gBarber.name);
        lbl_address.setText(Global.gBarber.address);

        btn_join.setEnabled(true);
        btn_join.setAlpha((float) 1.0);
        btn_leave.setEnabled(true);
        btn_leave.setAlpha((float) 1.0);

        llt_time.setVisibility(View.VISIBLE);
        llt_more.setVisibility(View.GONE);

        lbl_min.setVisibility(View.VISIBLE);
        lbl_hour.setVisibility(View.VISIBLE);
        lbl_phone.setText("Phone Number : " + Global.gBarber.phoneNumber);
        lbl_pertime.setText("Per Time : " + Global.gBarber.pertime + " min");

        if (queueID.length() > 0) {
            btn_join.setEnabled(false);
            btn_join.setAlpha((float) 0.1);

            if (queueID.equals(Global.gBarber.id)) {
                onSetQueuedBarber();
            } else {
                onSetUnqueuedBarber();
            }
        } else {
            onSetNormalBarber();
        }
    }

    private void onSetNormalBarber() {
        btn_leave.setEnabled(false);
        btn_leave.setAlpha((float) 0.1);

        int cnt_waiting = Global.gBarber.customers;
        int restTime = 0;
        if (cnt_waiting > 0) {
            restTime = cnt_waiting * Integer.parseInt(Global.gBarber.pertime);
        }
        int hour = restTime / 60;
        int min = restTime % 60 / 10;

        if (cnt_waiting == 0) {
            lbl_waiting.setText(getResources().getString(R.string.detail_already_barber));
            llt_time.setVisibility(View.GONE);
        } else {
            lbl_waiting.setText(getResources().getString(R.string.detail_set_queue));

            lbl_hour.setText(String.format("%d h", hour));
            if (hour == 0) {
                lbl_hour.setVisibility(View.GONE);
            }
            lbl_min.setText(String.format("%d m", min * 10));
            if (min == 0) {
                lbl_min.setVisibility(View.GONE);
            }
        }
    }

    private void onSetUnqueuedBarber() {
        btn_leave.setEnabled(false);
        btn_leave.setAlpha((float) 0.1);
        llt_time.setVisibility(View.GONE);
        lbl_waiting.setText(getResources().getString(R.string.detail_already_queue));
    }

    private void onSetQueuedBarber() {
        llt_more.setVisibility(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference().child("Queues").child(Global.gBarber.id);
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!Global.gIsQueue) {
                    return;
                }
                int cnt = 0;
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    CustomerUser customerUser = postSnapshot.getValue(CustomerUser.class);
                    if (customerUser.id.equals(Global.gUser.id)) {
                        break;
                    }
                    cnt++;
                }

                if (cnt > 0) {
                    String waitingStr = String.format(getResources().getString(R.string.detail_waiting_queue), cnt);
                    if (waitingStr.contains("1th")) {
                        waitingStr = waitingStr.replace("1th", "1st");
                    }
                    if (waitingStr.contains("2th")) {
                        waitingStr = waitingStr.replace("2th", "2nd");
                    }
                    if (waitingStr.contains("3th")) {
                        waitingStr = waitingStr.replace("3th", "3rd");
                    }
                    lbl_waiting.setText(waitingStr);

                    int restTime = cnt * Integer.parseInt(Global.gBarber.pertime);
                    int hour = restTime / 60;
                    int min = restTime % 60 / 10;
                    lbl_hour.setText(String.format("%d h", hour));
                    if (hour == 0) {
                        lbl_hour.setVisibility(View.GONE);
                    }
                    lbl_min.setText(String.format("%d m", min * 10));
                    if (min == 0) {
                        lbl_min.setVisibility(View.GONE);
                    }
                } else {
                    lbl_waiting.setText(getResources().getString(R.string.detail_cutting));
                    llt_time.setVisibility(View.GONE);
                    btn_leave.setEnabled(false);
                    btn_leave.setAlpha((float) 0.1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });
    }

    private void onSaveQueueInfo() {
        SharedPreferences.Editor editor = MainActivity.mActivity.getSharedPreferences(Global.AppTag, MODE_PRIVATE).edit();
        editor.putBoolean(Global.KeyIsQueue, Global.gIsQueue);
        editor.putString(Global.KeyQueueDate, queueDate);
        editor.putString(Global.KeyQueueID, queueID);

        editor.apply();
    }

    private void onSaveLeaveInfo() {
        SharedPreferences.Editor editor = MainActivity.mActivity.getSharedPreferences(Global.AppTag, MODE_PRIVATE).edit();
        editor.putBoolean(Global.KeyIsQueue, false);
        editor.putString(Global.KeyQueueDate, "00-00");
        editor.putString(Global.KeyQueueID, "");

        editor.apply();
    }

}
