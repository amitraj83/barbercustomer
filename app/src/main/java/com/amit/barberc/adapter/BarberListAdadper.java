package com.amit.barberc.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.amit.barberc.MainActivity;
import com.amit.barberc.R;
import com.amit.barberc.listener.OnQueueListener;
import com.amit.barberc.model.BarberUser;
import com.amit.barberc.model.CustomerUser;
import com.amit.barberc.model.DistanceModel;
import com.amit.barberc.util.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class BarberListAdadper extends BaseAdapter {

    private Context mContext;
    private List<BarberUser> mBarbers;
    private List<DistanceModel> mDistences;

    private OnQueueListener mListener;

    public BarberListAdadper(Context context, List<BarberUser> barbers, List<DistanceModel> distances){
        mContext = context;
        mBarbers = barbers;
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

        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barber, null);
        convertView.setOnClickListener(v -> mListener.OnClickBarber(barber));

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
                        dr.setCornerRadius(100);
                        img_good.setImageDrawable(dr);
                    }

                    @Override
                    public void onError() {

                    }
                });

        Button queue = convertView.findViewById(R.id.btn_item_queue);
        queue.setOnClickListener(v -> {
            mListener.OnClickQueue(barber);
        });

        TextView name = convertView.findViewById(R.id.lbl_item_name);
        name.setText(barber.name);

        TextView lbl_distance = convertView.findViewById(R.id.lbl_item_distence);
        String str_distance = String.format("%.1f Km", distance.distance);
        lbl_distance.setText(str_distance);

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
            showTime = mContext.getResources().getString(R.string.item_ready);
        }
        time.setText(showTime);

        LinearLayout spec = convertView.findViewById(R.id.llt_item_spec);
        if (position == mBarbers.size() - 1) {
            spec.setVisibility(View.GONE);
        }

        if (Global.gIsQueue) {
            queue.setVisibility(View.GONE);
            if (position == 0) {
                queue.setVisibility(View.VISIBLE);
                queue.setEnabled(false);
                queue.setBackground(mContext.getDrawable(R.drawable.btn_back_orange));
                queue.setText("Queued");

                convertView.setBackgroundColor(mContext.getColor(R.color.custom_WhiteBlack));

                SharedPreferences prefs = MainActivity.mActivity.getSharedPreferences(Global.AppTag, MODE_PRIVATE);
                String queueID = prefs.getString(Global.KeyQueueID, "");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference mRef = database.getReference().child("Queues").child(queueID);
                mRef.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int cnt = 0;
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            CustomerUser customerUser = postSnapshot.getValue(CustomerUser.class);
                            if (customerUser.id.equals(Global.gUser.id)) {
                                break;
                            }
                            cnt++;
                        }

                        if (cnt > 0) {
                            String showTime = "";
                            int waiting = cnt * Integer.parseInt(barber.pertime);

                            int hour = waiting / 60;
                            int min = waiting % 60 / 10;
                            if (hour == 0) {
                                showTime = String.format("%dmin", min * 10);
                            } else if (min == 0) {
                                showTime = String.format("%dhr", hour);
                            } else {
                                showTime = String.format("%dh %dm", hour, min * 10);
                            }

                            time.setText(showTime);
                        } else {
                            time.setText(mContext.getResources().getString(R.string.item_cutting));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //
                    }
                });
            }
        }

        return convertView;
    }

    public void setOnQueueListener (OnQueueListener listener) {
        mListener = listener;
    }
}
