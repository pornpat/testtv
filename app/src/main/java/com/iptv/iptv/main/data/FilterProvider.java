package com.iptv.iptv.main.data;

import android.content.Context;
import android.content.res.Resources;

import com.iptv.iptv.main.model.CategoryItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Asus N46V on 26/3/2017.
 */

public class FilterProvider {

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ORDER = "order";

    private static HashMap<String, List<CategoryItem>> sCategoryList;

    private static Resources sResources;

    public static void setContext(Context context) {
        if (null == sResources) {
            sResources = context.getResources();
        }
    }

    public static HashMap<String, List<CategoryItem>> getsCategoryList() {
        return sCategoryList;
    }

    public static HashMap<String, List<CategoryItem>> buildMedia(String url) throws JSONException {
        sCategoryList = new HashMap<>();

        JSONArray jsonArray = new FilterProvider().fetchJSON(url);

        if (null == jsonArray) {
            return sCategoryList;
        }

        List<CategoryItem> categoryList = new ArrayList<>();

        int id;
        String name;
        int order;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            id = jsonObj.getInt(TAG_ID);
            name = jsonObj.getString(TAG_NAME);
            order = jsonObj.getInt(TAG_ORDER);

            categoryList.add(buildCategoryInfo(id, name, order));
        }

        sCategoryList.put("", categoryList);

        return sCategoryList;
    }

    private static CategoryItem buildCategoryInfo(int id, String name, int order) {
        CategoryItem category = new CategoryItem();
        category.setId(id);
        category.setName(name);
        category.setOrder(order);

        return category;
    }

    private JSONArray fetchJSON(String urlString) {
        BufferedReader reader = null;

        try {
            java.net.URL url = new java.net.URL(urlString);
            URLConnection urlConnection = url.openConnection();
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "iso-8859-1"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            return new JSONArray(json);
        } catch (Exception e) {
            return null;
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
