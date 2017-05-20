package com.iptv.iptv.lib;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.iptv.iptv.R;
import com.iptv.iptv.main.SeriesEpisodeActivity;
import com.iptv.iptv.main.UrlUtil;
import com.iptv.iptv.main.data.SeriesDataUtil;
import com.iptv.iptv.main.model.SeriesItem;
import com.iptv.iptv.main.presenter.CardPresenter;
import com.iptv.iptv.main.presenter.DetailsDescriptionPresenter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SeriesDetailsFragment extends DetailsFragment {
    private static final String TAG = "SeriesDetailsFragment";

    private static final int ACTION_ADD_FAV = 99;

    private static final int DETAIL_THUMB_WIDTH = 225;
    private static final int DETAIL_THUMB_HEIGHT = 300;

    private SeriesItem mSelectedMovie;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private boolean isFav = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mSelectedMovie = Parcels.unwrap(getActivity().getIntent()
                .getParcelableExtra(SeriesDetailsActivity.SERIES));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(UrlUtil.appendUri(UrlUtil.SERIES_FAVORITE_URL, UrlUtil.addToken()), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject movieObj = jsonArray.getJSONObject(i);
                        JSONObject media = movieObj.getJSONObject("media");
                        int id = media.getInt("id");
                        if (id == mSelectedMovie.getId()) {
                            isFav = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (mSelectedMovie != null) {
                    setupAdapter();
                    setupDetailsOverviewRow();
                    setupDetailsOverviewRowPresenter();
                    setupMovieListRow();
                    setupMovieListRowPresenter();
                    setOnItemViewClickedListener(new ItemViewClickedListener());
                } else {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(getResources().getDrawable(R.drawable.movie_placeholder));
        final int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        final int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity())
                        .load(mSelectedMovie.getImageUrl())
                        .centerCrop()
                        .error(R.drawable.movie_placeholder)
                        .into(new SimpleTarget<GlideDrawable>(width, height) {
                            @Override
                            public void onResourceReady(GlideDrawable resource,
                                                        GlideAnimation<? super GlideDrawable>
                                                                glideAnimation) {
                                Log.d(TAG, "details overview card image url ready: " + resource);
                                row.setImageDrawable(resource);
                                mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                            }
                        });
            }
        }, 500);

        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
        for (int i = 0; i < mSelectedMovie.getTracks().size(); i++) {
            adapter.set(i, new Action(i, "เสียง: " + mSelectedMovie.getTracks().get(i).getAudio()
                    , "บรรยาย: " + mSelectedMovie.getTracks().get(i).getSubtitle()));
        }
        adapter.set(ACTION_ADD_FAV, new Action(ACTION_ADD_FAV, isFav ? getResources().getString(R.string.remove_fav) : getResources().getString(R.string.add_fav)));

        row.setActionsAdapter(adapter);

        mAdapter.add(row);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background and style.
        DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                SeriesDetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_ADD_FAV) {
                    if (!isFav) {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.post(UrlUtil.appendUri(UrlUtil.addMediaId(UrlUtil.FAVORITE_URL, mSelectedMovie.getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            }
                        });
                        isFav = true;
                        updateDetailsOverviewRow();
                    } else {
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.delete(UrlUtil.appendUri(UrlUtil.addMediaId(UrlUtil.FAVORITE_URL, mSelectedMovie.getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                            }
                        });
                        isFav = false;
                        updateDetailsOverviewRow();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), SeriesEpisodeActivity.class);
                    intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(mSelectedMovie));
                    intent.putExtra("track", (int)action.getId());
                    startActivity(intent);
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupMovieListRow() {
        String subcategories[] = {getString(R.string.related_movies)};
        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(UrlUtil.appendUri(UrlUtil.getRecommendUrl(UrlUtil.SERIES_URL, mSelectedMovie.getId()), UrlUtil.addToken()), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                List<SeriesItem> list = SeriesDataUtil.getSeriesListFromJson(responseString);
                for (int i = 0; i < list.size(); i++) {
                    listRowAdapter.add(list.get(i));
                }
            }
        });

        HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void setupMovieListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private void updateDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(getResources().getDrawable(R.drawable.movie_placeholder));
        final int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        final int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Glide.with(getActivity())
                        .load(mSelectedMovie.getImageUrl())
                        .centerCrop()
                        .error(R.drawable.movie_placeholder)
                        .into(new SimpleTarget<GlideDrawable>(width, height) {
                            @Override
                            public void onResourceReady(GlideDrawable resource,
                                                        GlideAnimation<? super GlideDrawable>
                                                                glideAnimation) {
                                Log.d(TAG, "details overview card image url ready: " + resource);
                                row.setImageDrawable(resource);
                                mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                            }
                        });
            }
        }, 500);

        SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
        for (int i = 0; i < mSelectedMovie.getTracks().size(); i++) {
            adapter.set(i, new Action(i, "เสียง: " + mSelectedMovie.getTracks().get(i).getAudio()
                    , "บรรยาย: " + mSelectedMovie.getTracks().get(i).getSubtitle()));
        }
        adapter.set(ACTION_ADD_FAV, new Action(ACTION_ADD_FAV, isFav ? getResources().getString(R.string.remove_fav) : getResources().getString(R.string.add_fav)));

        row.setActionsAdapter(adapter);

        mAdapter.replace(0, row);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof SeriesItem) {
                SeriesItem series = (SeriesItem) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), SeriesDetailsActivity.class);
                intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(series));
                intent.putExtra(getResources().getString(R.string.should_start), true);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        SeriesDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }
}
