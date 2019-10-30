package com.amit.barberc.fragment;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amit.barberc.MainActivity;
import com.amit.barberc.R;
import com.amit.barberc.adapter.BarberListAdadper;
import com.amit.barberc.listener.OnFireBaseListener;
import com.amit.barberc.listener.OnLocationListener;
import com.amit.barberc.listener.OnQueueListener;
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
import com.fevziomurtekin.customprogress.Dialog;
import com.fevziomurtekin.customprogress.Type;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarberListFragment extends Fragment implements OnQueueListener {
    static public BarberListFragment listFragment;

    private ListView lst_barber;
    private BarberListAdadper listAdadper;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listFragment = this;

        MainActivity activity = (MainActivity)getActivity();
        lst_barber = view.findViewById(R.id.lst_frg_list);
        listAdadper = new BarberListAdadper(getContext(), activity.mBarbers, activity.mMapCustomers, activity.mDistances);
        listAdadper.setOnQueueListener(this);
        lst_barber.setAdapter(listAdadper);

        ImageView img_up = view.findViewById(R.id.img_frg_list);
        img_up.setOnClickListener(v -> {
            lst_barber.smoothScrollToPosition(0);
        });
    }

    public void onRefreshBarberList() {
        listAdadper.notifyDataSetChanged();
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
                    Global.gUser.barberID = barber.id;
                    Global.gUser.status = 0;

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference mCustomerRef = database.getReference().child("Customers");
                    mCustomerRef.child(Global.gUser.id).setValue(Global.gUser);

                    DatabaseReference mQueueRef = database.getReference().child("Queues").child(barber.id);
                    String key = mQueueRef.push().getKey();
                    mQueueRef.child(key).setValue(Global.gUser);
                }

                @Override
                public void onCancelClicked(String tag) { }

                @Override
                public void onExitClicked(String tag) { }
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

}
