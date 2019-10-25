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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class BarberListAdadper extends BaseAdapter {

    private Context mContext;
    private List<BarberUser> mBarbers;

    private OnQueueListener mListener;

    public BarberListAdadper(Context context, List<BarberUser> barbers){
        mContext = context;
        mBarbers = barbers;
    }

    @Override
    public int getCount() {
        return mBarbers.size();
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
        if (position == mBarbers.size() - 1) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_empty, null);
            return convertView;
        }
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barber, null);

        BarberUser barber = mBarbers.get(position);

        ImageView img_good = convertView.findViewById(R.id.img_item_avatar);
        if (barber.avatarImgUrl.length() == 0) {
            barber.avatarImgUrl = "url";
        }
        Picasso.with(mContext).load(barber.avatarImgUrl).fit().centerCrop()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(img_good, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap src = ((BitmapDrawable)img_good.getDrawable()).getBitmap();
                        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(mContext.getResources(), src);
                        dr.setCornerRadius(50);
                        img_good.setImageDrawable(dr);
                    }

                    @Override
                    public void onError() {

                    }
                });

        Button queue = convertView.findViewById(R.id.btn_item_queue);
        queue.setOnClickListener(v -> {
            mListener.OnQueueClickListener(new BarberUser());
        });

        TextView name = convertView.findViewById(R.id.lbl_item_name);
        name.setText(barber.name);

        TextView time = convertView.findViewById(R.id.lbl_item_time);
        int restTime = 0;
        if (barber.customers > 0) {
            restTime = barber.customers * Integer.parseInt(barber.pertime);
        }
        String showTime = "";
        if (restTime > 0) {
            int hour = restTime / 60;
            int min = restTime % 60 / 10;

            if (hour == 0) {
                showTime = String.format("%dmin", min * 10);
            } else if (min == 0) {
                showTime = String.format("%dhr", hour);
            } else {
                showTime = String.format("%dh %dm", hour, min * 10);
            }

        } else {
            showTime = " Ready ";
        }
        time.setText(showTime);

        TextView distance = convertView.findViewById(R.id.lbl_item_distence);

        TextView during = convertView.findViewById(R.id.lbl_item_during);
        Calendar calendar = Calendar.getInstance();
        int indexofWeek = calendar.get(Calendar.DAY_OF_WEEK);
        BarberUser.WorkTime workTime = barber.workTimeList.get((indexofWeek + 5) % 7);
        String duringStr = "";
        if (workTime.fromtime.length() > 0) {
            duringStr = String.format("During : %s ~ %s", workTime.fromtime, workTime.totime);
        } else {
            duringStr = "During: Today is rest.";
            queue.setVisibility(View.GONE);
        }
        during.setText(duringStr);

        LinearLayout spec = convertView.findViewById(R.id.llt_item_spec);
        if (position == mBarbers.size() - 2) {
            spec.setVisibility(View.GONE);
        }

        return convertView;
    }

    public void setOnQueueListener (OnQueueListener listener) {
        mListener = listener;
    }
}
