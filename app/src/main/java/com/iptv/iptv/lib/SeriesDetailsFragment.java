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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.iptv.iptv.R;
import com.iptv.iptv.main.data.SeriesProvider;
import com.iptv.iptv.main.model.SeriesItem;
import com.iptv.iptv.main.presenter.CardPresenter;
import com.iptv.iptv.main.presenter.DetailsDescriptionPresenter;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeriesDetailsFragment extends DetailsFragment {
    private static final String TAG = "SeriesDetailsFragment";

    private static final int ACTION_WATCH_EN = 1;
    private static final int ACTION_WATCH_TH = 2;
    private static final int ACTION_ADD_FAV = 3;

    private static final int DETAIL_THUMB_WIDTH = 200;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private SeriesItem mSelectedMovie;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mSelectedMovie = Parcels.unwrap(getActivity().getIntent()
                .getParcelableExtra(SeriesDetailsActivity.SERIES));

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

    private void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(getResources().getDrawable(R.drawable.default_background));
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
                        .error(R.drawable.default_background)
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
        adapter.set(ACTION_WATCH_EN, new Action(ACTION_WATCH_EN, getResources()
                .getString(R.string.watch),
                getResources().getString(R.string.sound_en)));
        adapter.set(ACTION_WATCH_TH, new Action(ACTION_WATCH_TH, getResources().getString(R.string.watch),
                getResources().getString(R.string.sound_th)));
        adapter.set(ACTION_ADD_FAV, new Action(ACTION_ADD_FAV, getResources().getString(R.string.add_fav)));

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
                if (action.getId() == ACTION_WATCH_EN || action.getId() == ACTION_WATCH_TH) {
                    Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
                    intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(mSelectedMovie));
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupMovieListRow() {
        String subcategories[] = {getString(R.string.related_movies)};
        HashMap<String, List<SeriesItem>> series = SeriesProvider.getMovieList();

        // Generating related video list.
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (Map.Entry<String, List<SeriesItem>> entry : series.entrySet()) {
            List<SeriesItem> list = entry.getValue();
            for (int j = 0; j < list.size(); j++) {
                listRowAdapter.add(list.get(j));
            }
        }

        HeaderItem header = new HeaderItem(0, subcategories[0]);
        mAdapter.add(new ListRow(header, listRowAdapter));
    }

    private void setupMovieListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
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
