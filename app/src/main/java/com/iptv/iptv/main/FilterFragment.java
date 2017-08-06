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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.data.FilterLoader;
import com.iptv.iptv.main.data.MovieProvider;
import com.iptv.iptv.main.event.ApplyFilterEvent;
import com.iptv.iptv.main.model.CountryItem;
import com.iptv.iptv.main.model.FilterItem;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class FilterFragment extends Fragment implements LoaderManager.LoaderCallbacks<FilterItem> {

    private String mFilterUrl;

    private FilterCountryAdapter mCountryAdapter;
    private List<CountryItem> mCountryList;
    private FilterYearAdapter mYearAdapter;
    private List<Integer> mYearList;

    private int currentCountry = -1;
    private int currentYear = -1;

    private OnCountryInteractionListener mCountryListener;
    private OnYearInteractionListener mYearListener;

    private View mView;

    public static FilterFragment newInstance(String url, int currentCountry, int currentYear) {
        FilterFragment fragment = new FilterFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
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

        mView = view;

        mCountryList = new ArrayList<>();
        mCountryAdapter = new FilterCountryAdapter(mCountryList, currentCountry, mCountryListener);

        mYearList = new ArrayList<>();
        mYearAdapter = new FilterYearAdapter(mYearList, currentYear, mYearListener);

        RecyclerView countryList = (RecyclerView) view.findViewById(R.id.list_country);
        countryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        countryList.setAdapter(mCountryAdapter);
        countryList.requestFocus();

        RecyclerView yearList = (RecyclerView) view.findViewById(R.id.list_year);
        yearList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        yearList.setAdapter(mYearAdapter);

        view.findViewById(R.id.btn_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ApplyFilterEvent(true));
            }
        });
        view.findViewById(R.id.btn_apply).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    EventBus.getDefault().post(new ApplyFilterEvent(true));
                }
                return false;
            }
        });

        view.findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new ApplyFilterEvent(false));
            }
        });
        view.findViewById(R.id.btn_clear).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    EventBus.getDefault().post(new ApplyFilterEvent(false));
                }
                return false;
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
        if (data.getCountryList().size() > 0 && data.getYearList().size() > 0) {
            mCountryList.clear();
            mYearList.clear();
            for (int i = 0; i < data.getCountryList().size(); i++) {
                mCountryList.add(data.getCountryList().get(i));
            }
            for (int i = 0; i < data.getYearList().size(); i++) {
                mYearList.add(data.getYearList().get(i));
            }
        } else {
            Toast.makeText(getActivity(), "Failed to load.", Toast.LENGTH_LONG).show();
        }

        mCountryAdapter.notifyDataSetChanged();
        mYearAdapter.notifyDataSetChanged();

        mView.findViewById(R.id.loading).setVisibility(View.GONE);
        mView.findViewById(R.id.content).setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<FilterItem> loader) {
        mCountryList.clear();
        mYearList.clear();

        mCountryAdapter.notifyDataSetChanged();
        mYearAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnCountryInteractionListener && activity instanceof OnYearInteractionListener) {
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
        mCountryListener = null;
        mYearListener = null;
    }

    public interface OnCountryInteractionListener {
        void onCountryInteraction(CountryItem item);
    }

    public interface OnYearInteractionListener {
        void onYearInteraction(int year);
    }
}
