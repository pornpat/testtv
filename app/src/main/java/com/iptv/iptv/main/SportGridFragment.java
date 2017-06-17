//package com.iptv.iptv.main;
//
//import android.app.LoaderManager;
//import android.content.Intent;
//import android.content.Loader;
//import android.os.Bundle;
//import android.support.v17.leanback.app.VerticalGridFragment;
//import android.support.v17.leanback.widget.ArrayObjectAdapter;
//import android.support.v17.leanback.widget.ImageCardView;
//import android.support.v17.leanback.widget.OnItemViewClickedListener;
//import android.support.v17.leanback.widget.OnItemViewSelectedListener;
//import android.support.v17.leanback.widget.Presenter;
//import android.support.v17.leanback.widget.Row;
//import android.support.v17.leanback.widget.RowPresenter;
//import android.support.v17.leanback.widget.VerticalGridPresenter;
//import android.support.v4.app.ActivityOptionsCompat;
//import android.widget.Toast;
//
//import com.iptv.iptv.main.MovieDetailsActivity;
//import com.iptv.iptv.main.data.SportLoader;
//import com.iptv.iptv.main.data.SportProvider;
//import com.iptv.iptv.main.event.LoadSportEvent;
//import com.iptv.iptv.main.model.MovieItem;
//import com.iptv.iptv.main.presenter.CardPresenter;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.parceler.Parcels;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Karn on 22/4/2560.
// */
//
//public class SportGridFragment extends VerticalGridFragment implements LoaderManager.LoaderCallbacks<HashMap<String, List<MovieItem>>> {
//
//    private static final int NUM_COLUMNS = 5;
//    private final ArrayObjectAdapter mVideoObjectAdapter = new ArrayObjectAdapter(new CardPresenter());
//
//    private static String mVideosUrl;
//
//    private int loaderId = 0;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        showTitle(false);
//
//        prepareEntranceTransition();
//        setupFragment();
//    }
//
//    private void loadVideoData(String url) {
//        SportProvider.setContext(getActivity());
////        mVideosUrl = getActivity().getResources().getString(R.string.catalog_url);
//        mVideosUrl = url;
//        if (getLoaderManager().getLoader(loaderId) != null) {
//            getLoaderManager().destroyLoader(loaderId);
//        }
//        getLoaderManager().initLoader(loaderId, null, this);
//    }
//
//    private void setupFragment() {
//        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
//        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
//        setGridPresenter(gridPresenter);
//
//        setOnItemViewClickedListener(new ItemViewClickedListener());
//        setOnItemViewSelectedListener(new ItemViewSelectedListener());
//    }
//
//    @Override
//    public Loader<HashMap<String, List<MovieItem>>> onCreateLoader(int i, Bundle bundle) {
//        return new SportLoader(getActivity(), mVideosUrl);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<HashMap<String, List<MovieItem>>> loader, HashMap<String, List<MovieItem>> data) {
//        if (null != data && !data.isEmpty()) {
//            mVideoObjectAdapter.clear();
//            for (Map.Entry<String, List<MovieItem>> entry : data.entrySet()) {
//                List<MovieItem> list = entry.getValue();
//
//                for (int j = 0; j < list.size(); j++) {
//                    mVideoObjectAdapter.add(list.get(j));
//                }
////                for (int j = 0; j < list.size(); j++) {
////                    mVideoObjectAdapter.add(list.get(j));
////                }
//            }
//        } else {
//            Toast.makeText(getActivity(), "Failed to load videos.", Toast.LENGTH_LONG).show();
//        }
//
//        setAdapter(mVideoObjectAdapter);
//
//        startEntranceTransition();
//    }
//
//    @Override
//    public void onLoaderReset(Loader<HashMap<String, List<MovieItem>>> loader) {
//        mVideoObjectAdapter.clear();
//    }
//
//    private final class ItemViewClickedListener implements OnItemViewClickedListener {
//        @Override
//        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
//                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
//            MovieItem movie = (MovieItem) item;
//            Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
//            intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(movie));
//
//            Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                    getActivity(),
//                    ((ImageCardView) itemViewHolder.view).getMainImageView(),
//                    MovieDetailsActivity.SHARED_ELEMENT_NAME).toBundle();
//            getActivity().startActivity(intent, bundle);
//        }
//    }
//
//    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
//        @Override
//        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
//                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
//        }
//    }
//
//    @Subscribe
//    public void onLoadSportData(LoadSportEvent event) {
//        loadVideoData(event.url);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        EventBus.getDefault().unregister(this);
//        super.onStop();
//    }
//}
