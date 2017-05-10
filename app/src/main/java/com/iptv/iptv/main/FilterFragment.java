package com.iptv.iptv.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.data.FilterLoader;
import com.iptv.iptv.main.data.MovieProvider;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.CountryItem;
import com.iptv.iptv.main.model.FilterItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class FilterFragment extends Fragment implements LoaderManager.LoaderCallbacks<FilterItem> {

    private String mFilterUrl;

    private FilterCategoryAdapter mCategoryAdapter;
    private List<CategoryItem> mCategoryList;
    private FilterCountryAdapter mCountryAdapter;
    private List<CountryItem> mCountryList;
    private FilterYearAdapter mYearAdapter;
    private List<Integer> mYearList;

    private int currentCategory = -1;
    private int currentCountry = -1;
    private int currentYear = -1;

    private OnCategoryInteractionListener mCategoryListener;
    private OnCountryInteractionListener mCountryListener;
    private OnYearInteractionListener mYearListener;

    public static FilterFragment newInstance(String url, int currentCategory, int currentCountry, int currentYear) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putInt("currentCategory", currentCategory);
        bundle.putInt("currentCountry", currentCountry);
        bundle.putInt("currentYear", currentYear);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FilterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilterUrl = getArguments().getString("url");
            currentCategory = getArguments().getInt("currentCategory");
            currentCountry = getArguments().getInt("currentCountry");
            currentYear = getArguments().getInt("currentYear");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCategoryList = new ArrayList<>();
        mCategoryAdapter = new FilterCategoryAdapter(mCategoryList, currentCategory, mCategoryListener);

        mCountryList = new ArrayList<>();
        mCountryAdapter = new FilterCountryAdapter(mCountryList, currentCountry, mCountryListener);

        mYearList = new ArrayList<>();
        mYearAdapter = new FilterYearAdapter(mYearList, currentYear, mYearListener);

        RecyclerView categoryList = (RecyclerView) view.findViewById(R.id.list_category);
        categoryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        categoryList.setAdapter(mCategoryAdapter);
        categoryList.requestFocus();

        RecyclerView countryList = (RecyclerView) view.findViewById(R.id.list_country);
        countryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        countryList.setAdapter(mCountryAdapter);

        RecyclerView yearList = (RecyclerView) view.findViewById(R.id.list_year);
        yearList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        yearList.setAdapter(mYearAdapter);

        view.findViewById(R.id.btn_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ApplyFilterEvent(true));
            }
        });

        view.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ApplyFilterEvent(false));
            }
        });

        loadFilterData();
    }

    private void loadFilterData() {
        MovieProvider.setContext(getActivity());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<FilterItem> onCreateLoader(int i, Bundle bundle) {
        return new FilterLoader(getActivity(), mFilterUrl);
    }

    @Override
    public void onLoadFinished(Loader<FilterItem> loader, FilterItem data) {
        if (data.getCategoryList().size() > 0 && data.getCountryList().size() > 0 && data.getYearList().size() > 0) {
            mCategoryList.clear();
            mCountryList.clear();
            mYearList.clear();
            for (int i = 0; i < data.getCategoryList().size(); i++) {
                mCategoryList.add(data.getCategoryList().get(i));
            }
            for (int i = 0; i < data.getCountryList().size(); i++) {
                mCountryList.add(data.getCountryList().get(i));
            }
            for (int i = 0; i < data.getYearList().size(); i++) {
                mYearList.add(data.getYearList().get(i));
            }
        } else {
            Toast.makeText(getActivity(), "Failed to load videos.", Toast.LENGTH_LONG).show();
        }

        mCategoryAdapter.notifyDataSetChanged();
        mCountryAdapter.notifyDataSetChanged();
        mYearAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<FilterItem> loader) {
        mCategoryList.clear();
        mCountryList.clear();
        mYearList.clear();

        mCategoryAdapter.notifyDataSetChanged();
        mCountryAdapter.notifyDataSetChanged();
        mYearAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCategoryInteractionListener && activity instanceof OnCountryInteractionListener) {
            mCategoryListener = (OnCategoryInteractionListener) activity;
            mCountryListener = (OnCountryInteractionListener) activity;
            mYearListener = (OnYearInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCategoryListener = null;
        mCountryListener = null;
        mYearListener = null;
    }

    public interface OnCategoryInteractionListener {
        void onCategoryInteraction(CategoryItem item);
    }

    public interface OnCountryInteractionListener {
        void onCountryInteraction(CountryItem item);
    }

    public interface OnYearInteractionListener {
        void onYearInteraction(int year);
    }
}
