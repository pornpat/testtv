package com.iptv.iptv.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.FilterFragment.OnListFragmentInteractionListener;
import com.iptv.iptv.main.model.CategoryItem;

import java.util.List;


public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder> {

    private final List<CategoryItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    private int currentId = -1;
    private View lastCheckedView = null;
    private int lastCheckedPosition = -1;

    public FilterCategoryAdapter(List<CategoryItem> items, int currentId, OnListFragmentInteractionListener listener) {
        mValues = items;
        this.currentId = currentId;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_filter_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getName());

        if (mValues.get(position).getId() == currentId) {
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
                    mListener.onListFragmentInteraction(holder.mItem);

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
        public CategoryItem mItem;

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
