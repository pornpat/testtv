package com.iptv.iptv.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iptv.iptv.R;
import com.iptv.iptv.main.LiveScheduleFragment.OnListFragmentInteractionListener;
import com.iptv.iptv.main.model.ScheduleSubItem;

import java.util.Date;
import java.util.List;


public class LiveScheduleAdapter extends RecyclerView.Adapter<LiveScheduleAdapter.ViewHolder> {

    private final List<ScheduleSubItem> mValues;
    private final long mCurrentTime;
    private final OnListFragmentInteractionListener mListener;

    public LiveScheduleAdapter(List<ScheduleSubItem> items, long currentTime, OnListFragmentInteractionListener listener) {
        mValues = items;
        mCurrentTime = currentTime;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String d = mValues.get(position).getDate();
        String date = d.substring(d.length() - 2, d.length());
        String month = d.substring(d.length() - 5, d.length() - 3);
        String t = mValues.get(position).getTime();
        String time = t.substring(0, 5);
        holder.mTimeText.setText(date + "/" + month + " " + time);

        String fullDate = d + " " + t;
        if (TimeUtils.isScheduleLive(new Date(mCurrentTime), fullDate, mValues.get(position).getDuration())) {
            holder.mLiveText.setVisibility(View.VISIBLE);
        } else {
            holder.mLiveText.setVisibility(View.GONE);
        }

        holder.mNameText.setText(mValues.get(position).getChannelName());
        holder.mTitleText.setText(mValues.get(position).getLeague());
        if (mValues.get(position).getTeamName1().length() > 0 && mValues.get(position).getTeamName2().length() > 0) {
            holder.mMatchText.setText(mValues.get(position).getTeamName1() + " VS " +
                    mValues.get(position).getTeamName2());
        }
        Glide.with(holder.mImage1.getContext().getApplicationContext()).load(mValues.get(position).
                getTeamLogo1()).override(150, 150).into(holder.mImage1);
        Glide.with(holder.mImage2.getContext().getApplicationContext()).load(mValues.get(position).
                getTeamLogo2()).override(150, 150).into(holder.mImage2);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem.getChannelStream());
                }
            }
        });
        holder.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(holder.mItem.getChannelStream());
                    }
                }
                return false;
            }
        });

        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    holder.mView.setBackgroundColor(ContextCompat.getColor(holder.mView.getContext(), R.color.fastlane_background));
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
        public final TextView mLiveText;
        public final TextView mTimeText;
        public final TextView mNameText;
        public final TextView mTitleText;
        public final TextView mMatchText;
        public final ImageView mImage1;
        public final ImageView mImage2;
        public ScheduleSubItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLiveText = (TextView) view.findViewById(R.id.txt_live);
            mTimeText = (TextView) view.findViewById(R.id.txt_datetime);
            mNameText = (TextView) view.findViewById(R.id.txt_name);
            mTitleText = (TextView) view.findViewById(R.id.txt_title);
            mMatchText = (TextView) view.findViewById(R.id.txt_match);
            mImage1 = (ImageView) view.findViewById(R.id.img_team1);
            mImage2 = (ImageView) view.findViewById(R.id.img_team2);
        }
    }
}
