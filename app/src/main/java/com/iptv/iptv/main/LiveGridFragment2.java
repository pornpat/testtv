package com.iptv.iptv.main;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iptv.iptv.R;
import com.iptv.iptv.main.data.LiveLoader;
import com.iptv.iptv.main.data.LiveProvider;
import com.iptv.iptv.main.event.LoadLiveEvent;
import com.iptv.iptv.main.event.PageLiveEvent;
import com.iptv.iptv.main.event.SelectLiveEvent;
import com.iptv.iptv.main.event.TokenErrorEvent;
import com.iptv.iptv.main.model.LiveItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iptv.iptv.R.id.next;
import static com.iptv.iptv.R.id.prev;

public class LiveGridFragment2 extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, List<LiveItem>>> {

    private RecyclerView mRecyclerView;
    private List<LiveItem> mMovieList = new ArrayList<>();
    private View mLoading;
    private View mEmpty;
    private TextView mPrev;
    private TextView mNext;

    private static String mVideosUrl;
    private int loaderId = 0;

    private String nextPageUrl;
    private String prevPageUrl;

    public LiveGridFragment2() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mLoading = view.findViewById(R.id.loading);
        mEmpty = view.findViewById(R.id.empty);

        mPrev = (TextView) view.findViewById(prev);
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadVideoData(UrlUtil.appendUri(prevPageUrl, UrlUtil.addToken()));
                mRecyclerView.requestFocus();
            }
        });
        mNext = (TextView) view.findViewById(next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadVideoData(UrlUtil.appendUri(nextPageUrl, UrlUtil.addToken()));
                mRecyclerView.requestFocus();
            }
        });
    }

    private void loadVideoData(String url) {
        updateUI(mLoading);

        LiveProvider.setContext(getActivity());
        mVideosUrl = url;
        if (getLoaderManager().getLoader(loaderId) != null) {
            getLoaderManager().destroyLoader(loaderId);
        }
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public Loader<HashMap<String, List<LiveItem>>> onCreateLoader(int i, Bundle bundle) {
        return new LiveLoader(getActivity(), mVideosUrl);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<LiveItem>>> loader, HashMap<String, List<LiveItem>> data) {
        if (null != data && !data.isEmpty()) {
            mMovieList.clear();
            for (Map.Entry<String, List<LiveItem>> entry : data.entrySet()) {
                List<LiveItem> list = entry.getValue();

                for (int j = 0; j < list.size(); j++) {
                    mMovieList.add(list.get(j));
                }
            }
        }
        mRecyclerView.setAdapter(new LiveGridAdapter(getActivity(), mMovieList));

        if (mMovieList.size() > 0) {
            updateUI(mRecyclerView);
        } else {
            updateUI(mEmpty);
        }
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, List<LiveItem>>> loader) {
        mMovieList.clear();
    }

    @Subscribe
    public void onLoadMovieData(LoadLiveEvent event) {
        loadVideoData(event.url);
    }

    @Subscribe
    public void onSelectMovie(SelectLiveEvent event) {
        LiveItem live = mMovieList.get(event.position);
        Intent intent = new Intent(getActivity(), LivePlayerActivity.class);
        intent.putExtra("id", live.getId());
        startActivity(intent);
    }

    @Subscribe
    public void onInformPage(PageLiveEvent event) {
        prevPageUrl = event.prevUrl;
        nextPageUrl = event.nextUrl;

        if (prevPageUrl.length() > 10) {
            mPrev.setVisibility(View.VISIBLE);
        } else {
            mPrev.setVisibility(View.INVISIBLE);
        }

        if (nextPageUrl.length() > 10) {
            mNext.setVisibility(View.VISIBLE);
        } else {
            mNext.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe
    public void onTokenError(TokenErrorEvent event) {
        PrefUtil.setStringProperty(R.string.pref_token, "");
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateUI(View view) {
        mLoading.setVisibility(View.GONE);
        mEmpty.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
