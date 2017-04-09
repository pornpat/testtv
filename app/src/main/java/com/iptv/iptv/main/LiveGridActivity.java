package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.event.LoadLiveEvent;

import org.greenrobot.eventbus.EventBus;

public class LiveGridActivity extends LeanbackActivity {

    TextView mLiveText;
    TextView mRecentText;
    TextView mFavoriteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_grid);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mLiveText = (TextView) findViewById(R.id.live);
        mRecentText = (TextView) findViewById(R.id.recent);
        mFavoriteText = (TextView) findViewById(R.id.favorite);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.LIVE_URL, UrlUtil.addToken())));
            }
        }, 500);

        mLiveText.requestFocus();
        mLiveText.setSelected(true);

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LiveGridActivity.this, SearchActivity.class);
                intent.putExtra("origin", "live");
                startActivity(intent);
            }
        });

        mLiveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.LIVE_URL, UrlUtil.addToken())));
                setTextSelected(mLiveText);
            }
        });

        mRecentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.HISTORY_URL, UrlUtil.addToken())));
                setTextSelected(mRecentText);
            }
        });

        mFavoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new LoadLiveEvent(
                        UrlUtil.appendUri(UrlUtil.FAVORITE_URL, UrlUtil.addToken())));
                setTextSelected(mFavoriteText);
            }
        });
    }

    private void setTextSelected(TextView currentText) {
        mLiveText.setSelected(false);
        mRecentText.setSelected(false);
        mFavoriteText.setSelected(false);

        currentText.setSelected(true);
    }
}