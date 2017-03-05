package com.iptv.iptv.lib;


import android.app.Activity;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.iptv.iptv.R;
import com.iptv.iptv.main.model.SeriesItem;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeriesPlayerFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static final String TAG = "SeriesPlayerFragment";
    private static final boolean SHOW_DETAIL = true;
    private static final int BACKGROUND_TYPE = PlaybackOverlayFragment.BG_LIGHT;
    private static final int CARD_WIDTH = 150;
    private static final int CARD_HEIGHT = 240;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int SIMULATED_BUFFERED_TIME = 10000;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private PlaybackControlsRow.FastForwardAction mFastForwardAction;
    private PlaybackControlsRow.PlayPauseAction mPlayPauseAction;
    private PlaybackControlsRow.RewindAction mRewindAction;
    private PlaybackControlsRow mPlaybackControlsRow;
    private Handler mHandler;
    private Runnable mRunnable;
    private SeriesItem mSelectedSeries;

    private MediaController mMediaController;
    private final MediaController.Callback mMediaControllerCallback = new MediaControllerCallback();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mSelectedSeries = Parcels.unwrap(getActivity()
                .getIntent().getParcelableExtra(SeriesDetailsActivity.SERIES));

        mHandler = new Handler();

        setBackgroundType(BACKGROUND_TYPE);
        setFadingEnabled(false);

        setupRows();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = (Activity) context;
        mMediaController = activity.getMediaController();
        Log.d(TAG, "register callback of mediaController");
        mMediaController.registerCallback(mMediaControllerCallback);
    }

    @Override
    public void onStop() {
        stopProgressAutomation();
        mRowsAdapter = null;
        super.onStop();
    }

    @Override
    public void onDetach() {
        if (mMediaController != null) {
            Log.d(TAG, "unregister callback of mediaController");
            mMediaController.unregisterCallback(mMediaControllerCallback);
        }
        super.onDetach();
    }

    private void setupRows() {
        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(
                    new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    togglePlayback(mPlayPauseAction.getIndex() == PlaybackControlsRow.PlayPauseAction.PLAY);
                } else if (action.getId() == mFastForwardAction.getId()) {
                    fastForward();
                } else if (action.getId() == mRewindAction.getId()) {
                    fastRewind();
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    notifyChanged(action);
                }
            }
        });
        playbackControlsRowPresenter.setBackgroundColor(getResources().getColor(R.color.selected_background));
        playbackControlsRowPresenter.setProgressColor(getResources().getColor(R.color.fastlane_background));

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();

        setAdapter(mRowsAdapter);
    }

    private void togglePlayback(boolean playPause) {
        if (playPause) {
            mMediaController.getTransportControls().play();
        } else {
            mMediaController.getTransportControls().pause();
        }
    }

    private void addPlaybackControlsRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = new PlaybackControlsRow(mSelectedSeries);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowsAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow();

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);

        Activity activity = getActivity();
        mPlayPauseAction = new PlaybackControlsRow.PlayPauseAction(activity);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(activity);
        mRewindAction = new PlaybackControlsRow.RewindAction(activity);

        // Add main controls to primary adapter.
        mPrimaryActionsAdapter.add(mRewindAction);
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        mPrimaryActionsAdapter.add(mFastForwardAction);
    }

    private void notifyChanged(Action action) {
        int index = mPrimaryActionsAdapter.indexOf(action);
        if (index >= 0) {
            mPrimaryActionsAdapter.notifyArrayItemRangeChanged(index, 1);
        }
    }

    private void updatePlaybackRow() {
        mPlaybackControlsRow.setCurrentTime(0);
        mPlaybackControlsRow.setBufferedProgress(0);
        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
    }

    private void updateMovieView(String title, String cardImageUrl, long duration) {
        if (mPlaybackControlsRow.getItem() != null) {
            SeriesItem item = (SeriesItem) mPlaybackControlsRow.getItem();
            item.setName(title);
        }
        mPlaybackControlsRow.setTotalTime((int) duration);

        // Show the video card image if there is enough room in the UI for it.
        // If you have many primary actions, you may not have enough room.
        updateVideoImage(cardImageUrl);
    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0 || getView().getWidth() == 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void startProgressAutomation() {
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    int updatePeriod = getUpdatePeriod();
                    int currentTime = mPlaybackControlsRow.getCurrentTime() + updatePeriod;
                    int totalTime = mPlaybackControlsRow.getTotalTime();
                    mPlaybackControlsRow.setCurrentTime(currentTime);
                    mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);

                    if (totalTime > 0 && totalTime <= currentTime) {
                        stopProgressAutomation();
                        getActivity().finish();
                    } else {
                        mHandler.postDelayed(this, updatePeriod);
                    }
                }
            };
            mHandler.postDelayed(mRunnable, getUpdatePeriod());
        }
    }

    private void fastForward() {
        mMediaController.getTransportControls().fastForward();
    }

    private void fastRewind() {
        mMediaController.getTransportControls().rewind();
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
        }
    }

    private void updateVideoImage(String uri) {
        Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>(CARD_WIDTH, CARD_HEIGHT) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mPlaybackControlsRow.setImageDrawable(resource);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                    }
                });
    }

    private static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder viewHolder, Object item) {
            viewHolder.getTitle().setText(((SeriesItem) item).getName());
//            viewHolder.getSubtitle().setText(((Movie) item).getStudio());
        }
    }

    private class MediaControllerCallback extends MediaController.Callback {

        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackState state) {
            // The playback state has changed, so update your UI accordingly.
            // This should not update any media player / state!
            Log.d(TAG, "Playback state changed: " + state.getState());

            int nextState = state.getState();

            if (nextState == PlaybackState.STATE_PLAYING) {
                startProgressAutomation();
                setFadingEnabled(true);
                mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PAUSE);
                mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PAUSE));
                notifyChanged(mPlayPauseAction);
            } else if (nextState == PlaybackState.STATE_PAUSED) {
                stopProgressAutomation();
                setFadingEnabled(false);
                mPlayPauseAction.setIndex(PlaybackControlsRow.PlayPauseAction.PLAY);
                mPlayPauseAction.setIcon(mPlayPauseAction.getDrawable(PlaybackControlsRow.PlayPauseAction.PLAY));
                notifyChanged(mPlayPauseAction);
            }

            int currentTime = (int) state.getPosition();
            mPlaybackControlsRow.setCurrentTime(currentTime);
            mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);
        }

        @Override
        public void onMetadataChanged(final MediaMetadata metadata) {
            Log.d(TAG, "received update of media metadata");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateMovieView(
                            metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE),
                            metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI),
                            metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
                    );
                }
            }, 1000);
        }
    }
}
