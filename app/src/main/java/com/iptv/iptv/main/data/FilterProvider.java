package com.iptv.iptv.main.data;

import android.content.Context;
import android.content.res.Resources;

import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.CountryItem;
import com.iptv.iptv.main.model.FilterItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Asus N46V on 26/3/2017.
 */

public class FilterProvider {

    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_COUNTRIES = "countries";
    private static final String TAG_YEARS = "years";

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_ORDER = "order";
    private static final String TAG_COUNTRY = "country";

    private static FilterItem sFilter;

    private static Resources sResources;

    public static void setContext(Context context) {
        if (null == sResources) {
            sResources = context.getResources();
        }
    }

    public static FilterItem getFilter() {
        return sFilter;
    }

    public static FilterItem buildMedia(String url) throws JSONException {
        sFilter = new FilterItem();

        JSONObject jsonObject = new FilterProvider().fetchJSON(url);

        if (null == jsonObject) {
            return sFilter;
        }

        List<CategoryItem> categoryList = new ArrayList<>();

        JSONArray categoryArray = jsonObject.getJSONArray(TAG_CATEGORIES);
        for (int i = 0; i < categoryArray.length(); i++) {
            JSONObject jsonObj = categoryArray.getJSONObject(i);
            int id = jsonObj.getInt(TAG_ID);
            String name = jsonObj.getString(TAG_NAME);
            int order = jsonObj.getInt(TAG_ORDER);

            categoryList.add(buildCategoryInfo(id, name, order));
        }

        List<CountryItem> countryList = new ArrayList<>();

        JSONArray countryArray = jsonObject.getJSONArray(TAG_COUNTRIES);
        for (int i = 0; i < countryArray.length(); i++) {
            JSONObject jsonObj = countryArray.getJSONObject(i);
            int id = jsonObj.getInt(TAG_ID);
            String country = jsonObj.getString(TAG_COUNTRY);

            countryList.add(buildCountryInfo(id, country));
        }

        List<Integer> yearList = new ArrayList<>();

        JSONArray yearArray = jsonObject.getJSONArray(TAG_YEARS);
        for (int i = 0; i < yearArray.length(); i++) {
            yearList.add(yearArray.getInt(i));
        }

        if (categoryList.size() > 0) {
            Collections.sort(categoryList, new Comparator<CategoryItem>() {
                @Override
                public int compare(CategoryItem obj1, CategoryItem obj2) {
                    return obj1.getOrder() - obj2.getOrder();
                }
            });
        }
        sFilter.setCategoryList(categoryList);
        sFilter.setCountryList(countryList);
        sFilter.setYearList(yearList);

        return sFilter;
    }

    private static CategoryItem buildCategoryInfo(int id, String name, int order) {
        CategoryItem item = new CategoryItem();
        item.setId(id);
        item.setName(name);
        item.setOrder(order);

        return item;
    }

    private static CountryItem buildCountryInfo(int id, String country) {
        CountryItem item = new CountryItem();
        item.setId(id);
        item.setName(country);

        return item;
    }

    private JSONObject fetchJSON(String urlString) {
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
            return new JSONObject(json);
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
