package com.iptv.iptv.main.model;

import java.util.List;

/**
 * Created by Karn on 8/4/2560.
 */

public class FilterItem {

    private List<CategoryItem> categoryList;
    private List<CountryItem> countryList;
    private List<Integer> yearList;

    public List<CategoryItem> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryItem> categoryList) {
        this.categoryList = categoryList;
    }

    public List<CountryItem> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryItem> countryList) {
        this.countryList = countryList;
    }

    public List<Integer> getYearList() {
        return yearList;
    }

    public void setYearList(List<Integer> yearList) {
        this.yearList = yearList;
    }
}
