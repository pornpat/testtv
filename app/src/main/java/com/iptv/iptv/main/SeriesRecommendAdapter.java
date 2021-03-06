package com.iptv.iptv.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iptv.iptv.R;
import com.iptv.iptv.main.event.RecommendEvent;
import com.iptv.iptv.main.model.SeriesItem;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Karn on 7/5/2560.
 */

public class SeriesRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<SeriesItem> mValues;

    public SeriesRecommendAdapter(Context context, List<SeriesItem> items) {
        mContext = context;
        mValues = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recommend, parent, false);
        return new SeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final SeriesViewHolder vh = (SeriesViewHolder) holder;
        vh.mItem = mValues.get(position);
        Glide.with(mContext.getApplicationContext()).load(mValues.get(position).getImageUrl()).placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder).override(200, 300).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                vh.mTitle.setText(mValues.get(position).getName());
                vh.mEngTitle.setText(mValues.get(position).getEngName());
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
                    vh.mTitle.setSelected(true);
                    vh.mEngTitle.setSelected(true);
                    vh.mImage.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_movie_selected));
                } else {
                    vh.mTitle.setSelected(false);
                    vh.mEngTitle.setSelected(false);
                    vh.mImage.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
                }
            }
        });

        vh.mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    EventBus.getDefault().post(new RecommendEvent(position));
                    return true;
                }
                return false;
            }
        });
        vh.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    EventBus.getDefault().post(new RecommendEvent(position));
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

//    @Override
//    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
//        if (holder instanceof MovieViewHolder) {
//            Glide.clear(((MovieViewHolder) holder).mImage);
//        }
//        super.onViewDetachedFromWindow(holder);
//    }

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

}
