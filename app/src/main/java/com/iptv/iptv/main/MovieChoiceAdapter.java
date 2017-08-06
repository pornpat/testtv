package com.iptv.iptv.main;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.ChoiceEvent;
import com.iptv.iptv.main.model.TrackItem;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Karn on 7/5/2560.
 */

public class MovieChoiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FAV = 1;

    private final Context mContext;
    private final List<TrackItem> mValues;
    private final boolean isFav;

    public MovieChoiceAdapter(Context context, List<TrackItem> items, boolean isFav) {
        mContext = context;
        mValues = items;
        this.isFav = isFav;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FAV) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_choice, parent, false);
            return new FavoriteViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_choice, parent, false);
            return new ChoiceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ChoiceViewHolder) {
            final ChoiceViewHolder vh = (ChoiceViewHolder) holder;

            vh.mText.setText("เสียง: " + mValues.get(position).getAudio());

            vh.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocused) {
                    if (isFocused) {
                        vh.mView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_search_selected));
                    } else {
                        vh.mView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_search_normal));
                    }
                }
            });

            vh.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new ChoiceEvent(position));
                }
            });
            vh.mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        EventBus.getDefault().post(new ChoiceEvent(position));
                    }
                    return false;
                }
            });

        } else if (holder instanceof FavoriteViewHolder) {
            final FavoriteViewHolder vh = (FavoriteViewHolder) holder;

            if (isFav) {
                vh.mText.setText("ลบรายการโปรด");
            } else {
                vh.mText.setText("เพิ่มรายการโปรด");
            }

            vh.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean isFocused) {
                    if (isFocused) {
                        vh.mView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_search_selected));
                    } else {
                        vh.mView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_search_normal));
                    }
                }
            });

            vh.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new ChoiceEvent(-1));
                }
            });
            vh.mView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        EventBus.getDefault().post(new ChoiceEvent(-1));
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public int getItemViewType(int position) {
        if (position == (mValues.size() - 1)) {
            return TYPE_FAV;
        } else {
            return TYPE_NORMAL;
        }
    }

    public class ChoiceViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mText;

        public ChoiceViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mText = (TextView) itemView.findViewById(R.id.txt_choice);
        }
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mText;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mText = (TextView) itemView.findViewById(R.id.txt_choice);
        }
    }

}
