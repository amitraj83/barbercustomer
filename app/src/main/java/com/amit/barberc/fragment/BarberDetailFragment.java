package com.amit.barberc.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amit.barberc.R;
import com.dkv.bubblealertlib.AppConstants;
import com.dkv.bubblealertlib.AppLog;
import com.dkv.bubblealertlib.BblContentFragment;
import com.dkv.bubblealertlib.BblDialog;
import com.dkv.bubblealertlib.ConstantsIcons;
import com.dkv.bubblealertlib.IAlertClickedCallBack;

public class BarberDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btn_join = view.findViewById(R.id.btn_detail_join);
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
                        //
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

        Button btn_leave = view.findViewById(R.id.btn_detail_leave);
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
                        //
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
    }
}
