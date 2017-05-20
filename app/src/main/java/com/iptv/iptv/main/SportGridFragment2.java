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
import com.iptv.iptv.lib.MovieDetailsActivity;
import com.iptv.iptv.main.data.SportLoader;
import com.iptv.iptv.main.data.SportProvider;
import com.iptv.iptv.main.event.LoadSportEvent;
import com.iptv.iptv.main.event.PageSportEvent;
import com.iptv.iptv.main.event.SelectSportEvent;
import com.iptv.iptv.main.event.TokenErrorEvent;
import com.iptv.iptv.main.model.MovieItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.iptv.iptv.R.id.next;
import static com.iptv.iptv.R.id.prev;

public class SportGridFragment2 extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, List<MovieItem>>> {

    private RecyclerView mRecyclerView;
    private List<MovieItem> mMovieList = new ArrayList<>();
    private View mLoading;
    private View mEmpty;
    private TextView mPrev;
    private TextView mNext;

    private static String mVideosUrl;
    private int loaderId = 0;

    private String nextPageUrl;
    private String prevPageUrl;

    public SportGridFragment2() {

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

        SportProvider.setContext(getActivity());
        mVideosUrl = url;
        if (getLoaderManager().getLoader(loaderId) != null) {
            getLoaderManager().destroyLoader(loaderId);
        }
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public Loader<HashMap<String, List<MovieItem>>> onCreateLoader(int i, Bundle bundle) {
        return new SportLoader(getActivity(), mVideosUrl);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<MovieItem>>> loader, HashMap<String, List<MovieItem>> data) {
        if (null != data && !data.isEmpty()) {
            mMovieList.clear();
            for (Map.Entry<String, List<MovieItem>> entry : data.entrySet()) {
                List<MovieItem> list = entry.getValue();

                for (int j = 0; j < list.size(); j++) {
                    mMovieList.add(list.get(j));
                }
            }
        }
        mRecyclerView.setAdapter(new SportGridAdapter(getActivity(), mMovieList));

        if (mMovieList.size() > 0) {
            updateUI(mRecyclerView);
        } else {
            updateUI(mEmpty);
        }
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, List<MovieItem>>> loader) {
        mMovieList.clear();
    }

    @Subscribe
    public void onLoadMovieData(LoadSportEvent event) {
        loadVideoData(event.url);
    }

    @Subscribe
    public void onSelectMovie(SelectSportEvent event) {
        MovieItem movie = mMovieList.get(event.position);
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(movie));
        getActivity().startActivity(intent);
    }

    @Subscribe
    public void onInformPage(PageSportEvent event) {
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
