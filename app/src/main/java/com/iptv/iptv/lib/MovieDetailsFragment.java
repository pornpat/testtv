/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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
import com.iptv.iptv.main.MovieEpisodeActivity;
import com.iptv.iptv.main.MoviePlayerActivity;
import com.iptv.iptv.main.presenter.CardPresenter;
import com.iptv.iptv.main.presenter.DetailsDescriptionPresenter;
import com.iptv.iptv.main.model.MovieItem;
import com.iptv.iptv.main.data.MovieProvider;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class MovieDetailsFragment extends DetailsFragment {
    private static final String TAG = "MovieDetailsFragment";

    private static final int ACTION_ADD_FAV = 99;

    private static final int DETAIL_THUMB_WIDTH = 225;
    private static final int DETAIL_THUMB_HEIGHT = 300;

    private MovieItem mSelectedMovie;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mSelectedMovie = Parcels.unwrap(getActivity().getIntent()
                .getParcelableExtra(MovieDetailsActivity.MOVIE));

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
                MovieDetailsActivity.SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_ADD_FAV) {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    if (mSelectedMovie.getTracks().get((int)action.getId()).getDiscs().size() < 1) {
                        Intent intent = new Intent(getActivity(), MoviePlayerActivity.class);
                        intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(mSelectedMovie));
                        intent.putExtra("url", mSelectedMovie.getTracks().get((int)action.getId()).getDiscs().get(0).getVideoUrl());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), MovieEpisodeActivity.class);
                        intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(mSelectedMovie));
                        intent.putExtra("track", (int)action.getId());
                        startActivity(intent);
                    }
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupMovieListRow() {
        String subcategories[] = {getString(R.string.related_movies)};
        HashMap<String, List<MovieItem>> movies = MovieProvider.getMovieList();

        // Generating related video list.
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        for (Map.Entry<String, List<MovieItem>> entry : movies.entrySet()) {
            List<MovieItem> list = entry.getValue();
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

            if (item instanceof MovieItem) {
                MovieItem movie = (MovieItem) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(movie));
                intent.putExtra(getResources().getString(R.string.should_start), true);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        MovieDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }
        }
    }
}
