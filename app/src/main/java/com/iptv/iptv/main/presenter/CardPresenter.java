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

package com.iptv.iptv.main.presenter;

import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.iptv.iptv.R;
import com.iptv.iptv.main.model.MovieItem;
import com.iptv.iptv.main.model.SeriesItem;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static int sSelectedBackgroundColor;
    private static int sDefaultBackgroundColor;
    private Drawable mDefaultCardImage;

    private static void updateCardBackgroundColor(ImageCardView view, boolean selected) {
        int color = selected ? sSelectedBackgroundColor : sDefaultBackgroundColor;
        // Both background colors should be set because the view's background is temporarily visible
        // during animations.
        view.setBackgroundColor(color);
        view.findViewById(R.id.info_field).setBackgroundColor(color);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");

        sDefaultBackgroundColor = parent.getResources().getColor(R.color.default_background);
        sSelectedBackgroundColor = parent.getResources().getColor(R.color.selected_background);
        mDefaultCardImage = parent.getResources().getDrawable(R.drawable.movie);

        ImageCardView cardView = new ImageCardView(parent.getContext()) {
            @Override
            public void setSelected(boolean selected) {
                updateCardBackgroundColor(this, selected);
                super.setSelected(selected);
            }
        };

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        updateCardBackgroundColor(cardView, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        if (item instanceof MovieItem) {
            MovieItem movie = (MovieItem) item;
            ImageCardView cardView = (ImageCardView) viewHolder.view;

            Log.d(TAG, "onBindViewHolder");
            if (movie.getImageUrl() != null) {
                cardView.setTitleText(movie.getName());

                StringBuilder content = new StringBuilder();
                content.append("เสียง: ");
                for (int i = 0; i < movie.getTracks().size(); i++) {
                    content.append(movie.getTracks().get(i).getAudio() + "/");
                }
                content.deleteCharAt(content.length() - 1);
                cardView.setContentText(content.toString());

                int width = cardView.getResources().getDimensionPixelSize(R.dimen.card_width);
                int height = cardView.getResources().getDimensionPixelSize(R.dimen.card_height);
                cardView.setMainImageDimensions(width, height);
                Glide.with(viewHolder.view.getContext())
                        .load(movie.getImageUrl())
                        .centerCrop()
                        .error(mDefaultCardImage)
                        .into(cardView.getMainImageView());
            }
        } else if (item instanceof SeriesItem) {
            SeriesItem series = (SeriesItem) item;
            ImageCardView cardView = (ImageCardView) viewHolder.view;

            Log.d(TAG, "onBindViewHolder");
            if (series.getImageUrl() != null) {
                cardView.setTitleText(series.getName());

                StringBuilder content = new StringBuilder();
                content.append("เสียง: ");
                for (int i = 0; i < series.getTracks().size(); i++) {
                    content.append(series.getTracks().get(i).getAudio() + "/");
                }
                content.deleteCharAt(content.length() - 1);
                cardView.setContentText(content.toString());

                int width = cardView.getResources().getDimensionPixelSize(R.dimen.card_width);
                int height = cardView.getResources().getDimensionPixelSize(R.dimen.card_height);
                cardView.setMainImageDimensions(width, height);
                Glide.with(viewHolder.view.getContext())
                        .load(series.getImageUrl())
                        .centerCrop()
                        .error(mDefaultCardImage)
                        .into(cardView.getMainImageView());
            }
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder");
        ImageCardView cardView = (ImageCardView) viewHolder.view;
        // Remove references to images so that the garbage collector can free up memory
        cardView.setBadgeImage(null);
        cardView.setMainImage(null);
    }
}
