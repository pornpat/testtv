package com.iptv.iptv.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iptv.iptv.R;
import com.iptv.iptv.main.model.LiveItem;

import java.util.List;

/**
 * Created by Karn on 23/4/2560.
 */

public class LiveChannelAdapter extends RecyclerView.Adapter<LiveChannelAdapter.ViewHolder> {

    private List<LiveItem> mValues;

    public LiveChannelAdapter(List<LiveItem> items) {
        mValues = items;
    }

    @Override
    public LiveChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_channel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LiveChannelAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mChannelName.setText(mValues.get(position).getName());
        Glide.with(holder.mLogo.getContext()).load(mValues.get(position).getLogoUrl()).override(150, 150).into(holder.mLogo);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mChannelName;
        public final ImageView mLogo;
        public LiveItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChannelName = (TextView) view.findViewById(R.id.txt_name);
            mLogo = (ImageView) view.findViewById(R.id.logo);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }

}
