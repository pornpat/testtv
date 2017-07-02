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
import com.iptv.iptv.main.event.RecommendEvent;
import com.iptv.iptv.main.model.MovieItem;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Karn on 7/5/2560.
 */

public class MovieRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<MovieItem> mValues;

    public MovieRecommendAdapter(Context context, List<MovieItem> items) {
        mContext = context;
        mValues = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recommend, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final MovieViewHolder vh = (MovieViewHolder) holder;
        vh.mItem = mValues.get(position);
        Glide.with(mContext.getApplicationContext()).load(mValues.get(position).getImageUrl()).placeholder(R.drawable.movie_placeholder)
                .error(R.drawable.movie_placeholder).override(140, 210).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
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

        vh.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new RecommendEvent(position));
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

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final RoundedImageView mImage;
        public final TextView mTitle;
        public final TextView mEngTitle;
        public MovieItem mItem;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mImage = (RoundedImageView) itemView.findViewById(R.id.image);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mEngTitle = (TextView) itemView.findViewById(R.id.eng_title);
        }
    }

}
