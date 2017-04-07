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
import com.iptv.iptv.main.data.CategoryLoader;
import com.iptv.iptv.main.data.MovieProvider;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.model.CategoryItem;
import com.iptv.iptv.main.model.CountryItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilterFragment extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, List<CategoryItem>>> {

    private String mCategoryUrl = "http://139.59.231.135/uplay/public/api/v1/categories";
    private String mCountryUrl = "http://139.59.231.135/uplay/public/api/v1/countries";
    private String mYearUrl = "http://139.59.231.135/uplay/public/api/v1/years";

    private FilterCategoryAdapter mCategoryAdapter;
    private List<CategoryItem> mCategoryList;
    private FilterCountryAdapter mCountryAdapter;
    private List<CountryItem> mCountryList;

    private int currentId = -1;

    private OnCategoryInteractionListener mCategoryListener;
    private OnCountryInteractionListener mCountryListener;

    public static FilterFragment newInstance(int currentPosition) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", currentPosition);
        fragment.setArguments(bundle);
        return fragment;
    }

    public FilterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentId = getArguments().getInt("position");
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
        mCategoryAdapter = new FilterCategoryAdapter(mCategoryList, currentId, mCategoryListener);

        mCountryList = new ArrayList<>();
        mCountryAdapter = new FilterCountryAdapter(mCountryList, currentId, mCountryListener);

        RecyclerView categoryList = (RecyclerView) view.findViewById(R.id.list_category);
        categoryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryList.setAdapter(mCategoryAdapter);
        categoryList.requestFocus();

        RecyclerView countryList = (RecyclerView) view.findViewById(R.id.list_country);
        countryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        countryList.setAdapter(mCountryAdapter);

        RecyclerView yearList = (RecyclerView) view.findViewById(R.id.list_year);
        yearList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        yearList.setAdapter(mCategoryAdapter);

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

        loadCategoryData();
    }

    private void loadCategoryData() {
        MovieProvider.setContext(getActivity());
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<HashMap<String, List<CategoryItem>>> onCreateLoader(int i, Bundle bundle) {
        return new CategoryLoader(getActivity(), mCategoryUrl);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<CategoryItem>>> loader, HashMap<String, List<CategoryItem>> data) {
        if (null != data && !data.isEmpty()) {
            mCategoryList.clear();
            for (Map.Entry<String, List<CategoryItem>> entry : data.entrySet()) {
                List<CategoryItem> list = entry.getValue();
                if (list.size() > 0) {
                    Collections.sort(list, new Comparator<CategoryItem>() {
                        @Override
                        public int compare(CategoryItem obj1, CategoryItem obj2) {
                            return obj1.getOrder() - obj2.getOrder();
                        }
                    });
                }

                for (int j = 0; j < list.size(); j++) {
                    mCategoryList.add(list.get(j));
                }
            }
        } else {
            Toast.makeText(getActivity(), "Failed to load videos.", Toast.LENGTH_LONG).show();
        }

        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, List<CategoryItem>>> loader) {
        mCategoryList.clear();
        mCategoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCategoryInteractionListener && activity instanceof OnCountryInteractionListener) {
            mCategoryListener = (OnCategoryInteractionListener) activity;
            mCountryListener = (OnCountryInteractionListener) activity;
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
    }

    public interface OnCategoryInteractionListener {
        void onCategoryInteraction(CategoryItem item);
    }

    public interface OnCountryInteractionListener {
        void onCountryInteraction(CountryItem item);
    }
}
