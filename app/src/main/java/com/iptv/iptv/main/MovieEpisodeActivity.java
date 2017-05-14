package com.iptv.iptv.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;
import android.support.v4.content.ContextCompat;

import com.iptv.iptv.R;
import com.iptv.iptv.lib.MovieDetailsActivity;
import com.iptv.iptv.main.model.MovieItem;

import org.parceler.Parcels;

import java.util.List;

public class MovieEpisodeActivity extends LeanbackActivity {

    private static MovieItem mSelectMovie;
    private static int track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectMovie = Parcels.unwrap(getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE));
        track = getIntent().getExtras().getInt("track");

        if (null == savedInstanceState) {
            GuidedStepFragment.addAsRoot(this, new FirstStepFragment(), android.R.id.content);
        }

    }

    private static void addAction(List<GuidedAction> actions, long id, String title) {
        actions.add(new GuidedAction.Builder()
                .id(id)
                .title(title)
                .build());
    }

    public static class FirstStepFragment extends GuidedStepFragment {

        @Override
        @NonNull
        public GuidanceStylist.Guidance onCreateGuidance(@NonNull Bundle savedInstanceState) {
            final String title = mSelectMovie.getName();
            final String breadcrumb = "Select episode";
            final String description = "Audio: " + mSelectMovie.getTracks().get(track).getAudio() + " / Subtitle: " + mSelectMovie.getTracks().get(track).getSubtitle();
            final Drawable icon = ContextCompat.getDrawable(getActivity(), R.drawable.icon_movie);

            return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            for (int i = 0; i < mSelectMovie.getTracks().get(track).getDiscs().size(); i++) {
                addAction(actions, i, "Episode " + (i + 1));
            }
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            Intent intent = new Intent(getActivity(), MoviePlayerActivity.class);
            intent.putExtra(MovieDetailsActivity.MOVIE, Parcels.wrap(mSelectMovie));
            intent.putExtra("url", mSelectMovie.getTracks().get(track).getDiscs().get((int) action.getId()).getVideoUrl());
            intent.putExtra("extra_id", mSelectMovie.getTracks().get(track).getDiscs().get((int) action.getId()).getDiscId());
            startActivity(intent);
        }
    }
}
