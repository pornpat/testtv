package com.iptv.iptv.main.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Karn on 25/2/2560.
 */

public class MovieContent {

    public static final List<MovieItem> ITEMS = new ArrayList<>();
    public static final Map<String, MovieItem> ITEM_MAP = new HashMap<>();
    private static final int COUNT = 20;

    static {
        for (int i = 0; i < COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(MovieItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static MovieItem createDummyItem(int position) {
        return new MovieItem(String.valueOf(position), "Movie " + position, "Movie details: " + position, "https://upload.wikimedia.org/wikipedia/en/0/04/X-Men_-_Apocalypse.jpg");
    }

    public static class MovieItem {
        public final String id;
        public final String title;
        public final String desc;
        public final String image;

        public MovieItem(String id, String title, String desc, String image) {
            this.id = id;
            this.title = title;
            this.desc = desc;
            this.image = image;
        }

        @Override
        public String toString() {
            return title;
        }
    }

}
