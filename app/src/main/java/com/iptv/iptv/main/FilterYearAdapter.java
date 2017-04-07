package com.iptv.iptv.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;

import java.util.List;


public class FilterYearAdapter extends RecyclerView.Adapter<FilterYearAdapter.ViewHolder> {

    private final List<Integer> mValues;
    private final FilterFragment.OnYearInteractionListener mListener;

    private int currentYear = -1;
    private View lastCheckedView = null;
    private int lastCheckedPosition = -1;

    public FilterYearAdapter(List<Integer> items, int currentYear, FilterFragment.OnYearInteractionListener listener) {
        mValues = items;
        this.currentYear = currentYear;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mContentView.setText(mValues.get(position));

        if (mValues.get(position) == currentYear) {
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.selected_background));
            lastCheckedView = holder.mView;
            lastCheckedPosition = position;
        } else {
            holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.black));
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onYearInteraction(mValues.get(position));

                    if (lastCheckedPosition != position) {
                        holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.selected_background));
                        if (lastCheckedView != null) {
                            lastCheckedView.setBackgroundColor(ContextCompat.getColor(lastCheckedView.getContext(), R.color.black));
                        }

                        lastCheckedView = holder.mView;
                        lastCheckedPosition = position;
                    }
                }
            }
        });

        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    holder.mContentView.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.fastlane_background));
                } else {
                    holder.mContentView.setTextColor(ContextCompat.getColor(holder.mView.getContext(), R.color.text_default));
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
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
