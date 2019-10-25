package com.amit.barberc.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amit.barberc.R;
import com.amit.barberc.adapter.BarberListAdadper;
import com.amit.barberc.listener.OnQueueListener;
import com.amit.barberc.model.BarberUser;
import com.amit.barberc.util.Global;
import com.fevziomurtekin.customprogress.Dialog;
import com.fevziomurtekin.customprogress.Type;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BarberListFragment extends Fragment implements OnQueueListener {

    private ListView lst_barber;
    private ImageView img_up;
    private Dialog progressbar;

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

        progressbar = view.findViewById(R.id.progress);
        progressbar.settype(Type.RIPPLE);

        getBarbersDatas();

    }

    private void getBarbersDatas() {
        progressbar.show();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference().child("Barbers");
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressbar.gone();

                barberUsers.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    BarberUser barberUser = postSnapshot.getValue(BarberUser.class);
                    barberUsers.add(barberUser);
                }

                listAdadper.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressbar.gone();
            }
        });
    }

    @Override
    public void OnQueueClickListener(BarberUser barber) {
        //
    }
}
