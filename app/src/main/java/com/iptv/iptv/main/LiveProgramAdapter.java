package com.iptv.iptv.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.model.LiveProgramItem;

import java.util.List;

/**
 * Created by Karn on 23/4/2560.
 */

public class LiveProgramAdapter extends RecyclerView.Adapter<LiveProgramAdapter.ViewHolder>{

    private List<LiveProgramItem> mValues;

    public LiveProgramAdapter(List<LiveProgramItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_program, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mName.setText(mValues.get(position).getProgramName());
        holder.mName.setSelected(true);
        holder.mPeriod.setText(String.format("%02d", mValues.get(position).getStartHour()) + ":" +
                String.format("%02d", mValues.get(position).getStartMin()) + " - " +
                String.format("%02d", mValues.get(position).getEndHour()) + ":" +
                String.format("%02d", mValues.get(position).getEndMin()));

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
