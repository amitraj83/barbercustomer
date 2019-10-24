package com.amit.barberc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amit.barberc.R;
import com.amit.barberc.listener.OnQueueListener;
import com.amit.barberc.model.BarberUser;

import java.util.ArrayList;
import java.util.List;

public class BarberListAdadper extends BaseAdapter {

    private Context mContext;
    private List<BarberUser> mBarbers = new ArrayList<>();

    private OnQueueListener mListener;

    public BarberListAdadper(Context context, List<BarberUser> barbers){
        mContext = context;
        mBarbers = barbers;
    }

    @Override
    public int getCount() {
//        return mBarbers.size();
        return 21;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 20) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_empty, null);
            return convertView;
        }
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barber, null);

        TextView name = convertView.findViewById(R.id.lbl_item_name);
        name.setText(String.format("Barbers %d", position + 1));
        TextView time = convertView.findViewById(R.id.lbl_item_time);
        TextView distance = convertView.findViewById(R.id.lbl_item_distence);
        Button queue = convertView.findViewById(R.id.btn_item_queue);
        queue.setOnClickListener(v -> {
            mListener.OnQueueClickListener(new BarberUser());
        });
        LinearLayout spec = convertView.findViewById(R.id.llt_item_spec);
        if (position == 19) {
            spec.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setOnQueueListener (OnQueueListener listener) {
        mListener = listener;
    }
}
