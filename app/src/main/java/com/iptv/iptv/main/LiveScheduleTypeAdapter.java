package com.iptv.iptv.main;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.SelectScheduleEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Asus N46V on 9/7/2017.
 */

public class LiveScheduleTypeAdapter extends RecyclerView.Adapter<LiveScheduleTypeAdapter.ViewHolder> {

    private final List<String> mValues;

    private View lastCheckedView = null;
    private int lastCheckedPosition = -1;

    public LiveScheduleTypeAdapter(List<String> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_schedule_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mText.setText(mValues.get(position));

        if (lastCheckedPosition == -1 && position == 0) {
            holder.mView.setSelected(true);

            lastCheckedView = holder.mView;
            lastCheckedPosition = position;
        } else {
            holder.mView.setSelected(false);
        }

        holder.mView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    if (lastCheckedPosition != position) {
                        holder.mView.setSelected(true);
                        if (lastCheckedView != null) {
                            lastCheckedView.setSelected(false);
                        }

                        lastCheckedView = holder.mView;
                        lastCheckedPosition = position;

                        EventBus.getDefault().post(new SelectScheduleEvent(position));
                    }
                    return true;
                }
                return false;
            }
        });
        holder.mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (lastCheckedPosition != position) {
                        holder.mView.setSelected(true);
                        if (lastCheckedView != null) {
                            lastCheckedView.setSelected(false);
                        }

                        lastCheckedView = holder.mView;
                        lastCheckedPosition = position;

                        EventBus.getDefault().post(new SelectScheduleEvent(position));
                    }
                }
                return false;
            }
        });

        holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                if (isFocused) {
                    holder.mView.setBackgroundResource(R.drawable.bg_menu_selected);
                } else {
                    holder.mView.setBackgroundResource(android.R.color.transparent);
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
