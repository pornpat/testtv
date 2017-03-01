package com.iptv.iptv.main;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;

import com.iptv.iptv.lib.MovieDetailsActivity;
import com.iptv.iptv.main.data.VideoProvider;
import com.iptv.iptv.main.event.SelectCategoryEvent;
import com.iptv.iptv.main.model.Movie;
import com.iptv.iptv.main.presenter.CardPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieGridFragment extends VerticalGridFragment {

    private static final int NUM_COLUMNS = 4;
    private final ArrayObjectAdapter mVideoObjectAdapter = new ArrayObjectAdapter(new CardPresenter());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                long seed = System.nanoTime();
                HashMap<String, List<Movie>> movies = VideoProvider.getMovieList();
                for (Map.Entry<String, List<Movie>> entry : movies.entrySet()) {
                    List<Movie> list = entry.getValue();
                    Collections.shuffle(list, new Random(seed));
                    for (Movie movie : list) {
                        mVideoObjectAdapter.add(movie);
                    }
                }
                startEntranceTransition();
            }
        }, 500);

        setAdapter(mVideoObjectAdapter);

        showTitle(false);

        if (savedInstanceState == null) {
            prepareEntranceTransition();
        }
        setupFragment();
    }

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);

        // After 500ms, start the animation to transition the cards into view.
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startEntranceTransition();
            }
        }, 500);

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            Movie movie = (Movie) item;
            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.MOVIE, movie);

            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
                    MovieDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
            getActivity().startActivity(intent, bundle);
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
        }
    }

    @Subscribe
    public void onSelectCategory(SelectCategoryEvent event) {

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
