package com.iptv.iptv.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.main.event.SelectSeriesEvent;
import com.iptv.iptv.main.model.SeriesItem;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Karn on 10/5/2560.
 */

public class SeriesGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOADMORE = 1;

    private final Context mContext;
    private final List<SeriesItem> mValues;

    public SeriesGridAdapter(Context context, List<SeriesItem> items) {
        mContext = context;
        mValues = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADMORE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_loadmore, parent, false);
            return new LoadmoreViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_movie, parent, false);
            return new SeriesViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SeriesViewHolder) {
            final SeriesViewHolder vh = (SeriesViewHolder) holder;
            vh.mItem = mValues.get(position);
            Glide.with(mContext).load(mValues.get(position).getImageUrl()).placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.movie_placeholder).override(200, 300).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    vh.mTitle.setText(mValues.get(position).getName());
                    vh.mEngTitle.setText(mValues.get(position).getEngName());
                    vh.mImage.setImageDrawable(resource);
                    return true;
                }
            }).into(vh.mImage);

            vh.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocused) {
                    if (isFocused) {
                        vh.mImage.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_movie_selected));
                    } else {
                        vh.mImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
                    }
                }
            });

            vh.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new SelectSeriesEvent(position));
                }
            });
        } else if (holder instanceof LoadmoreViewHolder) {
            final LoadmoreViewHolder vh = (LoadmoreViewHolder) holder;
            vh.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocused) {
                    if (isFocused) {
                        vh.mImage.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_movie_selected));
                    } else {
                        vh.mImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
                    }
                }
            });

            vh.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new SelectSeriesEvent(-1));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof SeriesViewHolder) {
            Glide.clear(((SeriesViewHolder) holder).mImage);
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemViewType(int position) {
        if (mValues.get(position).getId() == -1) {
            return TYPE_LOADMORE;
        } else {
            return TYPE_NORMAL;
        }
    }

    public class SeriesViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RoundedImageView mImage;
        public final TextView mTitle;
        public final TextView mEngTitle;
        public SeriesItem mItem;

        public SeriesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImage = (RoundedImageView) itemView.findViewById(R.id.image);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mEngTitle = (TextView) itemView.findViewById(R.id.eng_title);
        }
    }

    public class LoadmoreViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RoundedImageView mImage;

        public LoadmoreViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImage = (RoundedImageView) itemView.findViewById(R.id.image);
        }
    }

}