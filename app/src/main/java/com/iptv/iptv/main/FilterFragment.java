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

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FilterFragment extends Fragment implements LoaderManager.LoaderCallbacks<HashMap<String, List<CategoryItem>>>{

    private String mUrl = "http://139.59.231.135/uplay/public/api/v1/categories";

    private FilterCategoryAdapter mAdapter;
    private List<CategoryItem> mList;

    private int currentId = -1;

    private OnListFragmentInteractionListener mListener;

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

        mList = new ArrayList<>();
        mAdapter = new FilterCategoryAdapter(mList, currentId, mListener);

        RecyclerView categoryList = (RecyclerView) view.findViewById(R.id.list_category);
        categoryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        categoryList.setAdapter(mAdapter);
        categoryList.requestFocus();

        RecyclerView countryList = (RecyclerView) view.findViewById(R.id.list_country);
        countryList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        countryList.setAdapter(mAdapter);

        RecyclerView yearList = (RecyclerView) view.findViewById(R.id.list_year);
        yearList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        yearList.setAdapter(mAdapter);

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
        return new CategoryLoader(getActivity(), mUrl);
    }

    @Override
    public void onLoadFinished(Loader<HashMap<String, List<CategoryItem>>> loader, HashMap<String, List<CategoryItem>> data) {
        if (null != data && !data.isEmpty()) {
            mList.clear();
            for (Map.Entry<String, List<CategoryItem>> entry : data.entrySet()) {
                List<CategoryItem> list = entry.getValue();

                for (int j = 0; j < list.size(); j++) {
                    mList.add(list.get(j));
                    mList.add(list.get(j));
                    mList.add(list.get(j));
                }
            }
        } else {
            Toast.makeText(getActivity(), "Failed to load videos.", Toast.LENGTH_LONG).show();
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<HashMap<String, List<CategoryItem>>> loader) {
        mList.clear();
        mAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(CategoryItem item);
    }
}
