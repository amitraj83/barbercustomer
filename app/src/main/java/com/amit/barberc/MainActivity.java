package com.amit.barberc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.amit.barberc.adapter.FragmentAdapter;
import com.amit.barberc.fragment.BarberDetailFragment;
import com.amit.barberc.fragment.BarberListFragment;
import com.amit.barberc.model.BarberUser;
import com.amit.barberc.model.CustomerUser;
import com.amit.barberc.model.DistanceModel;
import com.amit.barberc.util.Global;
import com.dkv.bubblealertlib.AppConstants;
import com.dkv.bubblealertlib.AppLog;
import com.dkv.bubblealertlib.BblContentFragment;
import com.dkv.bubblealertlib.BblDialog;
import com.dkv.bubblealertlib.ConstantsIcons;
import com.dkv.bubblealertlib.IAlertClickedCallBack;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static public MainActivity mActivity;

    private ViewPager viewPager;

    public List<BarberUser> mBarbers = new ArrayList<>();
    private List<BarberUser> mPreBarbers = new ArrayList<>();
    public List<DistanceModel> mDistances = new ArrayList<>();
    public Map<String, List<CustomerUser>> mMapCustomers = new HashMap<>();

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(final Location location) {
            Global.gLan = location.getLongitude();
            Global.gLat = location.getLatitude();

//            Global.gLat = 17.955984;
//            Global.gLan = 102.657542;

            onRefreshBarberList();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Global.initUIActivity(this);
        mActivity = this;

        initUIView();
        initFirebaseListener();
    }

    private void initFirebaseListener() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mBarberRef = database.getReference().child("Barbers");
        mBarberRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPreBarbers.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    BarberUser barberUser = postSnapshot.getValue(BarberUser.class);
                    mPreBarbers.add(barberUser);
                }

                onRefreshBarberList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        DatabaseReference mUserRef = database.getReference().child("Customers").child(Global.gUser.id);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Global.gUser = dataSnapshot.getValue(CustomerUser.class);
                onRefreshBarberList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mQueueRef = database.getReference().child("Queues");
        mQueueRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMapCustomers.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    List<CustomerUser> users = new ArrayList<>();
                    for (DataSnapshot snapshot: postSnapshot.getChildren()) {
                        CustomerUser user = snapshot.getValue(CustomerUser.class);
                        users.add(user);
                    }
                    if (users.size() > 0) {
                        mMapCustomers.put(postSnapshot.getKey(), users);
                    }
                }

                onRefreshBarberList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void onRefreshBarberList() {
        mDistances.clear();
        for (BarberUser barber : mPreBarbers) {
            DistanceModel model = new DistanceModel();
            model.barberID = barber.id;
            LatLng p1 = new LatLng(Global.gLat, Global.gLan);
            LatLng p2 = new LatLng(Double.parseDouble(barber.latitude), Double.parseDouble(barber.langitude));
            model.distance = Global.onCalculationByDistance(p1, p2);
            mDistances.add(model);
        }

        if (mDistances.size() > 0) {
            Collections.sort(mDistances, (model1, model2) ->
                    (String.valueOf(model1.distance)).compareTo(String.valueOf(model2.distance))
            );
            mBarbers.clear();
            for (DistanceModel model : mDistances) {
                for (BarberUser barber : mPreBarbers) {
                    if (barber.id.equals(model.barberID)) {
                        mBarbers.add(barber);
                    }
                }
            }
        }

        if (Global.gUser.barberID.length() > 0) {
            for (DistanceModel model: mDistances) {
                if (model.barberID.equals(Global.gUser.barberID)) {
                    mDistances.remove(model);
                    mDistances.add(0, model);
                    break;
                }
            }
            for (BarberUser barber: mBarbers) {
                if (barber.id.equals(Global.gUser.barberID)) {
                    mBarbers.remove(barber);
                    mBarbers.add(0, barber);
                    break;
                }
            }
        }

        if (BarberListFragment.listFragment != null) {
            BarberListFragment.listFragment.onRefreshBarberList();
        }
        if (BarberDetailFragment.detailFragment != null) {
            BarberDetailFragment.detailFragment.onRefreshBarberList();
        }
    }

    @SuppressLint("MissingPermission")
    private void initUIView() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000
                , 0, mLocationListener);

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(new BarberListFragment(), "List of Barbers");
        adapter.addFragment(new BarberDetailFragment(), "Details of Barbers");
        viewPager = findViewById(R.id.vwp_main_content);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = findViewById(R.id.tab_main_title);
        viewPagerTab.setViewPager(viewPager);
    }

    public void onViewPagerSetted(int num) {
        viewPager.setCurrentItem(num);
        onRefreshBarberList();
    }

    @Override
    public void onBackPressed() {
        try {
            BblContentFragment fragment = BblContentFragment.newInstance(AppConstants.TAG_FEEDBACK_SUCCESS);
            String content = "Do you really close this application?";
            if (TextUtils.isEmpty(content)) {
                content = getString(R.string.err_server_error);
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
                public void onCancelClicked(String tag) { }

                @Override
                public void onExitClicked(String tag) { }
            });
            BblDialog sampleDialog = new BblDialog();
            sampleDialog.setContentFragment(fragment
                    , R.layout.layout_bbl_content
                    , LayoutInflater.from(this), content
                    , ConstantsIcons.ALERT_ICON_INFO, this);
            sampleDialog.setDisMissCallBack(null);
            getSupportFragmentManager().beginTransaction().add(sampleDialog, "Test").commitAllowingStateLoss();
        } catch (Exception e) {
            AppLog.logException(AppConstants.TAG_FEEDBACK_SUCCESS, e);
        }
    }

}
