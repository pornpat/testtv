package com.iptv.iptv.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.model.LiveProgramItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Karn on 23/4/2560.
 */

public class LiveProgramAdapter extends RecyclerView.Adapter<LiveProgramAdapter.ViewHolder>{

    private List<LiveProgramItem> mValues = new ArrayList<>();

    public LiveProgramAdapter(List<LiveProgramItem> items, long current) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        boolean isFound = false;
        for (int i = 0; i < items.size(); i++) {
            try {
                long startTime = dateFormat.parse(items.get(i).getStartTime()).getTime();
                long endTime = dateFormat.parse(items.get(i).getEndTime()).getTime();

                if (current >= startTime && current < endTime) {
                    isFound = true;
                }
                if (isFound) {
                    mValues.add(items.get(i));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (mValues.size() == 0) {
            mValues.add(new LiveProgramItem("ไม่มีข้อมูลรายการ", "", ""));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_program, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position == 0) {
            holder.mName.setText("ขณะนี้: " + mValues.get(position).getProgramName());
        } else {
            holder.mName.setText(mValues.get(position).getProgramName());
        }
        holder.mName.setSelected(true);
        holder.mPeriod.setText(mValues.get(position).getStartTime() + " - " + mValues.get(position).getEndTime());

        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.detail_background));
                } else {
                    holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), android.R.color.transparent));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mPeriod;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.txt_program);
            mPeriod = (TextView) view.findViewById(R.id.txt_period);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
