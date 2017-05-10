package com.iptv.iptv.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iptv.iptv.R;
import com.iptv.iptv.main.event.SelectMovieEvent;
import com.iptv.iptv.main.model.LiveItem;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class LiveGridAdapter extends RecyclerView.Adapter<LiveGridAdapter.ViewHolder> {

    private final Context mContext;
    private final List<LiveItem> mValues;

    public LiveGridAdapter(Context context, List<LiveItem> items) {
        mContext = context;
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        Glide.with(mContext).load(mValues.get(position).getLogoUrl()).override(200, 200).centerCrop().into(holder.mImage);
        holder.mTitle.setText(mValues.get(position).getName());

        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    holder.mImage.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_movie_selected));
                } else {
                    holder.mImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectMovieEvent(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RoundedImageView mImage;
        public final TextView mTitle;
        public LiveItem mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImage = (RoundedImageView) itemView.findViewById(R.id.image);
            mTitle = (TextView) itemView.findViewById(R.id.title);
        }
    }

}