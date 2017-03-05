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

import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.iptv.iptv.main.model.MovieItem;
import com.iptv.iptv.main.model.SeriesItem;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        if (item instanceof MovieItem) {
            MovieItem movie = (MovieItem) item;

            if (movie != null) {
                viewHolder.getTitle().setText(movie.getName());
//            viewHolder.getSubtitle().setText("ความยาว: 135 min " + " ประเภท: Action & Adventure");
                viewHolder.getSubtitle().setText("ประเภท: Action");
                viewHolder.getBody().setText(movie.getDescription());
            }
        } else if (item instanceof SeriesItem) {
            SeriesItem series = (SeriesItem) item;

            if (series != null) {
                viewHolder.getTitle().setText(series.getName());
//            viewHolder.getSubtitle().setText("ความยาว: 135 min " + " ประเภท: Action & Adventure");
                viewHolder.getSubtitle().setText("ประเภท: Action");
                viewHolder.getBody().setText(series.getDescription());
            }
        }
    }
}
