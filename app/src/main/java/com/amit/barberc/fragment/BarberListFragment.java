package com.amit.barberc.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amit.barberc.R;
import com.amit.barberc.adapter.BarberListAdadper;
import com.amit.barberc.listener.OnQueueListener;
import com.amit.barberc.model.BarberUser;

import java.util.ArrayList;
import java.util.List;

public class BarberListFragment extends Fragment implements OnQueueListener {

    private ListView lst_barber;
    private ImageView img_up;

    private BarberListAdadper listAdadper;
    private List<BarberUser> barberUsers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lst_barber = view.findViewById(R.id.lst_frg_list);
        listAdadper = new BarberListAdadper(getContext(), barberUsers);
        listAdadper.setOnQueueListener(this);
        lst_barber.setAdapter(listAdadper);

        img_up = view.findViewById(R.id.img_frg_list);
        img_up.setOnClickListener(v -> {
            lst_barber.smoothScrollToPosition(0);
        });

    }

    @Override
    public void OnQueueClickListener(BarberUser barber) {
        //
    }
}
