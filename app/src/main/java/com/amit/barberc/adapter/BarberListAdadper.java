package com.amit.barberc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.amit.barberc.R;
import com.amit.barberc.listener.OnQueueListener;
import com.amit.barberc.model.BarberUser;
import com.amit.barberc.model.CustomerUser;
import com.amit.barberc.model.DistanceModel;
import com.amit.barberc.util.Global;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class BarberListAdadper extends BaseAdapter {

    private Context mContext;
    private List<BarberUser> mBarbers;
    private List<DistanceModel> mDistences;
    private Map<String, List<CustomerUser>> mCustomers;

    private OnQueueListener mListener;

    public BarberListAdadper(Context context, List<BarberUser> barbers, Map<String, List<CustomerUser>> customers, List<DistanceModel> distances){
        mContext = context;
        mBarbers = barbers;
        mCustomers = customers;
        mDistences = distances;
    }

    @Override
    public int getCount() {
        return mBarbers.size() + 1;
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
        if (position == mBarbers.size()) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_empty, null);
            return convertView;
        }

        BarberUser barber = mBarbers.get(position);
        DistanceModel distance = mDistences.get(position);
        List<CustomerUser> customers = mCustomers.get(barber.id);

        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barber, null);
        convertView.setOnClickListener(v -> mListener.OnClickBarber(barber));

        ImageView img_good = convertView.findViewById(R.id.img_item_avatar);
        if (barber.avatarImgUrl.length() == 0) {
            barber.avatarImgUrl = "url";
        }

        Picasso.with(mContext).load(barber.avatarImgUrl).fit().centerCrop()
                .placeholder(R.drawable.icon)
                .error(R.drawable.icon)
                .into(img_good, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap src = ((BitmapDrawable)img_good.getDrawable()).getBitmap();
                        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(mContext.getResources(), src);
                        dr.setCornerRadius(100);
                        img_good.setImageDrawable(dr);
                    }

                    @Override
                    public void onError() {

                    }
                });

        TextView name = convertView.findViewById(R.id.lbl_item_name);
        name.setText(barber.name);

        TextView lbl_distance = convertView.findViewById(R.id.lbl_item_distence);
        String str_distance = "";
        if (distance.distance > 40) {
            str_distance = "--- Km";
        } else {
            str_distance = String.format("%.1f Km", distance.distance);
        }

        lbl_distance.setText(str_distance);

        LinearLayout spec = convertView.findViewById(R.id.llt_item_spec);
        if (position == mBarbers.size() - 1) {
            spec.setVisibility(View.GONE);
        }

        TextView time = convertView.findViewById(R.id.lbl_item_time);
        Button queue = convertView.findViewById(R.id.btn_item_queue);
        queue.setOnClickListener(v -> {
            mListener.OnClickQueue(barber);
        });

        String timeStr = "";
        if (Global.gUser.barberID.length() > 0) {
            if (position == 0) {
                queue.setText("Queued");
                queue.setEnabled(false);
                queue.setBackground(mContext.getDrawable(R.drawable.btn_back_orange));

                if (Global.gUser.status == 1) {
                    timeStr = "In Chair";
                } else {
                    timeStr = onCalculateWaitingTime(customers, barber);
                }
            } else {
                queue.setVisibility(View.GONE);
                timeStr = onCalculateWaitingTime(customers, barber);
            }
        } else {
            timeStr = onCalculateWaitingTime(customers, barber);
        }
        time.setText(timeStr);

        return convertView;
    }

    private String onCalculateWaitingTime(List<CustomerUser> users, BarberUser barber) {
        if (users == null || users.size() == 0) {
            return  "Ready";
        }
        int cnt = 0;
        for (CustomerUser user: users) {
            if (user.status == 1) {
                continue;
            }
            cnt++;
            if (user.id.equals(Global.gUser.id)) {
                break;
            }
        }

        int timeAll = cnt * Integer.parseInt(barber.pertime);
        int hour = timeAll / 60;
        int min = timeAll % 60;

        String str = "";
        if (hour == 0) {
            str = String.format("%d min", min);
        } else if (min == 0) {
            str = String.format("%d hr", hour);
        } else {
            str = String.format("%dh %dm", hour, min);
        }

        return str;
    }

    public void setOnQueueListener (OnQueueListener listener) {
        mListener = listener;
    }

}
