package com.iptv.iptv.main;


import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.activity.LoginActivity;
import com.iptv.iptv.main.activity.MovieDetailsActivity;
import com.iptv.iptv.main.activity.MoviePlayerActivity;
import com.iptv.iptv.main.data.VipLoader;
import com.iptv.iptv.main.data.VipProvider;
import com.iptv.iptv.main.event.LoadVipEvent;
import com.iptv.iptv.main.event.PageVipEvent;
import com.iptv.iptv.main.event.SelectVipEvent;
import com.iptv.iptv.main.event.TokenErrorEvent;
import com.iptv.iptv.main.model.MovieItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class VipGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, List<MovieItem>>> {

    private RecyclerView mRecyclerView;
    private VipGridAdapter mAdapter;

    private List<MovieItem> mMovieList = new ArrayList<>();
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

    public VipGridFragment() {

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
        mAdapter = new VipGridAdapter(getActivity(), mMovieList);
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

        VipProvider.setContext(getActivity());
        mVideosUrl = url;
        if (getLoaderManager().getLoader(loaderId) != null) {
            getLoaderManager().destroyLoader(loaderId);
        }
        getLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    public Loader<HashMap<String, List<MovieItem>>> onCreateLoader(int i, Bundle bundle) {
        return new VipLoader(getActivity(), mVideosUrl);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<MovieItem>>> loader, HashMap<String, List<MovieItem>> data) {
        if (shouldLoad) {
            if (!isLoadmore) {
                if (null != data && !data.isEmpty()) {
                    if (isNewLoad) {
                        mMovieList.clear();
                        isNewLoad = false;
                    }
                    for (Map.Entry<String, List<MovieItem>> entry : data.entrySet()) {
                        List<MovieItem> list = entry.getValue();

                        for (int j = 0; j < list.size(); j++) {
                            mMovieList.add(list.get(j));
                        }
                    }
                }
                if (isNextAvailable) {
                    MovieItem item = new MovieItem();
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
                    for (Map.Entry<String, List<MovieItem>> entry : data.entrySet()) {
                        List<MovieItem> list = entry.getValue();

                        for (int j = 0; j < list.size(); j++) {
                            mMovieList.add(list.get(j));
                        }
                    }
                }
                if (isNextAvailable) {
                    MovieItem item = new MovieItem();
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
    public void onLoaderReset(Loader<HashMap<String, List<MovieItem>>> loader) {
//        if (!isLoadmore) {
//            mMovieList.clear();
//        }
    }

    @Subscribe
    public void onLoadVipData(LoadVipEvent event) {
        isNextAvailable = false;
        isNewLoad = true;
        loadVideoData(event.url);
    }

    @Subscribe
    public void onSelectVip(SelectVipEvent event) {
        if (event.position != -1) {
            final MovieItem movie = mMovieList.get(event.position);

            final ProgressDialog progress = new ProgressDialog(getActivity());
            progress.setMessage("โปรดรอ...");
            progress.show();

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(ApiUtils.appendUri(ApiUtils.EXPIRE_CHECK_URL, ApiUtils.addToken()), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(getActivity(), "กรุณาลองใหม่ในภายหลัง", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        boolean isExpired = jsonObject.getBoolean("expired");
                        if (!isExpired) {
                            Intent intent = new Intent(getActivity(), MoviePlayerActivity.class);
                            intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(movie));
                            intent.putExtra("url", movie.getTracks().get(0).getDiscs().get(0).getVideoUrl());
                            intent.putExtra("extra_id", movie.getTracks().get(0).getDiscs().get(0).getDiscId());
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "วันใช้งานของคุณหมด กรุณาเติมเวันใช้งานเพื่อรับชม", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progress.dismiss();
                }
            });


        } else {
            isLoadmore = true;
            loadVideoData(ApiUtils.appendUri(nextPageUrl, ApiUtils.addToken()));
        }
    }

    @Subscribe
    public void onInformPage(PageVipEvent event) {
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
        if (PrefUtils.getBooleanProperty(R.string.pref_update_movie) && PrefUtils.getBooleanProperty(R.string.pref_current_favorite)) {
            isNewLoad = true;
            shouldLoad = true;
        }
        PrefUtils.setBooleanProperty(R.string.pref_update_movie, false);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
