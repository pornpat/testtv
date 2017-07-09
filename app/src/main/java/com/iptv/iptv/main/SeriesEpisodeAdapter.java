package com.iptv.iptv.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.SelectEpisodeEvent;
import com.iptv.iptv.main.model.SeriesEpisodeItem;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Asus N46V on 8/7/2017.
 */

public class SeriesEpisodeAdapter extends RecyclerView.Adapter<SeriesEpisodeAdapter.ViewHolder> {

    private final List<SeriesEpisodeItem> mValues;

    public SeriesEpisodeAdapter(List<SeriesEpisodeItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mText.setText("ตอนที่ " + (position + 1));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new SelectEpisodeEvent(position));
            }
        });

        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    holder.mText.setTextColor(
                            ContextCompat.getColor(holder.mView.getContext(), R.color.fastlane_background));
                } else {
                    holder.mText.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.text_default));
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
        public final TextView mText;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mText = (TextView) view.findViewById(R.id.content);
        }
    }
}
