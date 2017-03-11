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
import com.iptv.iptv.lib.SeriesDetailsActivity;
import com.iptv.iptv.lib.SeriesPlayerActivity;
import com.iptv.iptv.main.model.SeriesItem;

import org.parceler.Parcels;

import java.util.List;

public class SeriesEpisodeActivity extends LeanbackActivity {

    private static SeriesItem mSelectSeries;
    private static int track;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSelectSeries = Parcels.unwrap(getIntent().getParcelableExtra(SeriesDetailsActivity.SERIES));
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
            final String title = mSelectSeries.getName();
            final String breadcrumb = "Select episode";
            final String description = "Soundtrack: " + mSelectSeries.getTracks().get(track).getAudio() + " / Subtitle: " + mSelectSeries.getTracks().get(track).getSubtitle();
            final Drawable icon = ContextCompat.getDrawable(getActivity(), R.drawable.icon_series);

            return new GuidanceStylist.Guidance(title, description, breadcrumb, icon);
        }

        @Override
        public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
            for (int i = 0; i < mSelectSeries.getTracks().get(track).getEpisodes().size(); i++) {
                addAction(actions, i, "Episode " + (i + 1));
            }
        }

        @Override
        public void onGuidedActionClicked(GuidedAction action) {
            Intent intent = new Intent(getActivity(), SeriesPlayerActivity.class);
            intent.putExtra(SeriesDetailsActivity.SERIES, Parcels.wrap(mSelectSeries));
            intent.putExtra("url", mSelectSeries.getTracks().get(track).getEpisodes().get((int) action.getId()).getUrl());
            startActivity(intent);
        }
    }
}
