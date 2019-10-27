package com.amit.barberc.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amit.barberc.MainActivity;
import com.amit.barberc.R;
import com.amit.barberc.adapter.BarberListAdadper;
import com.amit.barberc.listener.OnQueueListener;
import com.amit.barberc.model.BarberUser;
import com.amit.barberc.model.DistanceModel;
import com.amit.barberc.util.Global;
import com.dkv.bubblealertlib.AppConstants;
import com.dkv.bubblealertlib.AppLog;
import com.dkv.bubblealertlib.BblContentFragment;
import com.dkv.bubblealertlib.BblDialog;
import com.dkv.bubblealertlib.ConstantsIcons;
import com.dkv.bubblealertlib.IAlertClickedCallBack;
import com.fevziomurtekin.customprogress.Dialog;
import com.fevziomurtekin.customprogress.Type;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BarberListFragment extends Fragment implements OnQueueListener {

    private ListView lst_barber;
    private ImageView img_up;
    private Dialog progressbar;

    private BarberListAdadper listAdadper;
    private List<BarberUser> mBarbers = new ArrayList<>();

    private String queueDate;
    private String queueID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.listFragment = this;

        lst_barber = view.findViewById(R.id.lst_frg_list);
        listAdadper = new BarberListAdadper(getContext(), Global.gBarberUsers, Global.gBarberDistences);
        listAdadper.setOnQueueListener(this);
        lst_barber.setAdapter(listAdadper);

        img_up = view.findViewById(R.id.img_frg_list);
        img_up.setOnClickListener(v -> {
            lst_barber.smoothScrollToPosition(0);
        });

        progressbar = view.findViewById(R.id.progress);
        progressbar.settype(Type.RIPPLE);

        initUIView();
    }

    public void initUIView() {
        SharedPreferences prefs = MainActivity.mActivity.getSharedPreferences(Global.AppTag, MODE_PRIVATE);
        Global.gIsQueue = prefs.getBoolean(Global.KeyIsQueue, false);
        queueDate = prefs.getString(Global.KeyQueueDate, "00-00");
        queueID = prefs.getString(Global.KeyQueueID, "");

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        String currentDate = String.format("%02d-%02d", month, day);
        if (!currentDate.equals(queueDate)) {
            Global.gIsQueue = false;
            queueDate = "00-00";
            queueID = "";
            onSaveQueueInfo();
        }

        getBarbersDatas();
        onResetBarberList();
    }

    private void getBarbersDatas() {
        progressbar.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference().child("Barbers");
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressbar.gone();

                Global.gBarberDistences.clear();
                mBarbers.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    BarberUser barberUser = postSnapshot.getValue(BarberUser.class);
                    mBarbers.add(barberUser);

                    DistanceModel model = new DistanceModel();
                    model.barberID = barberUser.id;
                    LatLng p1 = new LatLng(Global.gLan, Global.gLat);
                    LatLng p2 = new LatLng(Double.parseDouble(barberUser.langitude), Double.parseDouble(barberUser.latitude));
                    model.distance = Global.onCalculationByDistance(p1, p2);
                    Global.gBarberDistences.add(model);
                }

                if (mBarbers.size() > 0) {
                    Collections.sort(Global.gBarberDistences, (model1, model2) ->
                            (String.valueOf(model1.distance)).compareTo(String.valueOf(model2.distance))
                    );
                    onResetBarberList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressbar.gone();
            }
        });
    }

    private void onResetBarberList() {
        Global.gBarberUsers.clear();
        for (DistanceModel mode: Global.gBarberDistences) {
            String barberID = mode.barberID;
            for (BarberUser barberUser: mBarbers) {
                if (barberID.equals(barberUser.id)) {
                    Global.gBarberUsers.add(barberUser);
                    break;
                }
            }
        }
        if (Global.gIsQueue) {
            for (int i = 0; i < Global.gBarberUsers.size(); i++) {
                BarberUser barberUser = Global.gBarberUsers.get(i);
                DistanceModel distanceModel = Global.gBarberDistences.get(i);
                if (barberUser.id.equals(queueID)) {
                    Global.gBarberUsers.remove(barberUser);
                    Global.gBarberUsers.add(0, barberUser);
                    Global.gBarberDistences.remove(distanceModel);
                    Global.gBarberDistences.add(0, distanceModel);
                }
            }
        }
        listAdadper.notifyDataSetChanged();

        if (Global.gBarberUsers.size() > 0 && Global.gBarber.id.equals("")) {
            Global.gBarber = Global.gBarberUsers.get(0);
        }
    }

    @Override
    public void OnClickQueue(BarberUser barber) {
        try {
            BblContentFragment fragment = BblContentFragment.newInstance(AppConstants.TAG_FEEDBACK_SUCCESS);
            String content = "Are you sure to queue this barber?";
            if (TextUtils.isEmpty(content)) {
                content = getString(com.dkv.bubblealertlib.R.string.err_server_error);
            }
            fragment.setContent(content, "Yes", "No", null, "Queue");
            fragment.setClickedCallBack(new IAlertClickedCallBack() {
                @Override
                public void onOkClicked(String tag) {
                    Global.gIsQueue = true;
                    queueID = barber.id;

                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DATE);
                    queueDate = String.format("%02d-%02d", month, day);
                    onSaveQueueInfo();

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference mRefQueue = database.getReference().child("Queues");
                    mRefQueue.child(barber.id).push().setValue(Global.gUser);

                    barber.onSetQueueWithCustomer();
                    DatabaseReference mRefBarber = database.getReference().child("Barbers");
                    mRefBarber.child(barber.id).setValue(barber);
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
                    , ConstantsIcons.ALERT_ICON_INFO, getContext());
            sampleDialog.setDisMissCallBack(null);
            getActivity().getSupportFragmentManager().beginTransaction().add(sampleDialog, "Test").commitAllowingStateLoss();
        } catch (Exception e) {
            AppLog.logException(AppConstants.TAG_FEEDBACK_SUCCESS, e);
        }
    }

    @Override
    public void OnClickBarber(BarberUser barber) {
        Global.gBarber = barber;
        MainActivity.mActivity.onViewPagerSetted(1);
    }

    private void onSaveQueueInfo() {
        SharedPreferences.Editor editor = MainActivity.mActivity.getSharedPreferences(Global.AppTag, MODE_PRIVATE).edit();
        editor.putBoolean(Global.KeyIsQueue, Global.gIsQueue);
        editor.putString(Global.KeyQueueDate, queueDate);
        editor.putString(Global.KeyQueueID, queueID);

        editor.apply();
    }

}
