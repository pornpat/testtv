package com.iptv.iptv.main;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iptv.iptv.R;
import com.iptv.iptv.main.data.SeriesLoader;
import com.iptv.iptv.main.data.SeriesProvider;
import com.iptv.iptv.main.event.LoadSeriesEvent;
import com.iptv.iptv.main.event.PageSeriesEvent;
import com.iptv.iptv.main.event.SelectSeriesEvent;
import com.iptv.iptv.main.event.TokenErrorEvent;
import com.iptv.iptv.main.model.SeriesItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, List<SeriesItem>>> {

    private RecyclerView mRecyclerView;
    private SeriesGridAdapter mAdapter;

    private List<SeriesItem> mMovieList = new ArrayList<>();
    private View mLoading;
    private View mEmpty;

    private boolean isNextAvailable = false;
    private boolean isLoadmore = false;
    private String nextPageUrl;

    private static String mVideosUrl;
    private int loaderId = 0;

    private boolean shouldLoad = false;
    private boolean isNewLoad = false;
    private boolean isLoading = false;

    public SeriesGridFragment() {

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
        mAdapter = new SeriesGridAdapter(getActivity(), mMovieList);
        mRecyclerView.setAdapter(mAdapter);

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && isNextAvailable && totalItemCount <= (lastVisibleItem + 2)) {
                    isNextAvailable = false;
                    isLoadmore = true;
                    loadVideoData(ApiUtils.appendUri(nextPageUrl, ApiUtils.addToken()));
                }
            }
        });

        mLoading = view.findViewById(R.id.loading);
        mEmpty = view.findViewById(R.id.empty);
    }

    private void loadVideoData(String url) {
        isLoading = true;
        shouldLoad = true;
        if (!isLoadmore) {
            updateUI(mLoading);
        }

        SeriesProvider.setContext(getActivity());
        mVideosUrl = url;
        if (getLoaderManager().getLoader(loaderId) != null) {
            getLoaderManager().destroyLoader(loaderId);
        }
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public Loader<HashMap<String, List<SeriesItem>>> onCreateLoader(int i, Bundle bundle) {
        return new SeriesLoader(getActivity(), mVideosUrl);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<SeriesItem>>> loader, HashMap<String, List<SeriesItem>> data) {
        if (shouldLoad) {
            if (!isLoadmore) {
                if (null != data && !data.isEmpty()) {
                    if (isNewLoad) {
                        mMovieList.clear();
                        isNewLoad = false;
                    }
                    for (Map.Entry<String, List<SeriesItem>> entry : data.entrySet()) {
                        List<SeriesItem> list = entry.getValue();

                        for (int j = 0; j < list.size(); j++) {
                            mMovieList.add(list.get(j));
                        }
                    }
                }
                if (isNextAvailable) {
                    SeriesItem item = new SeriesItem();
                    item.setId(-1);
                    mMovieList.add(item);
//                    isNextAvailable = false;
                }
                mAdapter.notifyDataSetChanged();

                if (mMovieList.size() > 0) {
                    updateUI(mRecyclerView);
                } else {
                    updateUI(mEmpty);
                }
            } else {
                if (null != data && !data.isEmpty()) {
                    mMovieList.remove(mMovieList.size() - 1);
                    for (Map.Entry<String, List<SeriesItem>> entry : data.entrySet()) {
                        List<SeriesItem> list = entry.getValue();

                        for (int j = 0; j < list.size(); j++) {
                            mMovieList.add(list.get(j));
                        }
                    }
                }
                if(isNextAvailable) {
                    SeriesItem item = new SeriesItem();
                    item.setId(-1);
                    mMovieList.add(item);
//                    isNextAvailable = false;
                }
                mAdapter.notifyItemInserted(mMovieList.size());

                isLoadmore = false;
            }
            isLoading = false;
            shouldLoad = false;
        }
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, List<SeriesItem>>> loader) {
//        if (!isLoadmore) {
//            mMovieList.clear();
//        }
    }

    @Subscribe
    public void onLoadMovieData(LoadSeriesEvent event) {
        isNextAvailable = false;
        isNewLoad = true;
        loadVideoData(event.url);
    }

    @Subscribe
    public void onSelectMovie(SelectSeriesEvent event) {
        if (event.position != -1) {
            SeriesItem movie = mMovieList.get(event.position);
            Intent intent = new Intent(getActivity(), SeriesDetailsActivity.class);
            intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(movie));
            getActivity().startActivity(intent);
        } else {
            isLoadmore = true;
            loadVideoData(ApiUtils.appendUri(nextPageUrl, ApiUtils.addToken()));
        }
    }

    @Subscribe
    public void onInformPage(PageSeriesEvent event) {
        nextPageUrl = event.nextUrl;

        if (event.hasNext) {
            isNextAvailable = true;
        }
    }

    @Subscribe
    public void onTokenError(TokenErrorEvent event) {
        PrefUtils.setStringProperty(R.string.pref_token, "");
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.putExtra("toast", true);
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
        if (PrefUtils.getBooleanProperty(R.string.pref_update_series) && PrefUtils.getBooleanProperty(R.string.pref_current_favorite)) {
            isNewLoad = true;
            shouldLoad = true;
        }
        PrefUtils.setBooleanProperty(R.string.pref_update_series, false);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
