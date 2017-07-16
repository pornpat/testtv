package com.iptv.iptv.main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iptv.iptv.R;
import com.iptv.iptv.main.model.ScheduleItem;
import com.iptv.iptv.main.model.ScheduleSubItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class LiveScheduleFragment extends Fragment {

    RecyclerView recyclerView;
    List<ScheduleItem> scheduleList = new ArrayList<>();
    long currentTime;

    View loading;

    private OnListFragmentInteractionListener mListener;

    public LiveScheduleFragment() {
    }

    public static LiveScheduleFragment newInstance() {
        LiveScheduleFragment fragment = new LiveScheduleFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_COLUMN_COUNT, columnCount);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        loading = view.findViewById(R.id.loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.appendUri(ApiUtils.TIME_URL, ApiUtils.addToken()),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            currentTime = jsonObject.getLong("timestamp") * 1000L;

                            fetchSchedule();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void fetchSchedule() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(ApiUtils.SCHEDULE_URL, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString,
                    Throwable throwable) {
//                recyclerView.setAdapter(new LiveScheduleAdapter(DummyContent.ITEMS, mListener));
//                recyclerView.requestFocus();

                loading.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONArray result = jsonObject.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        ScheduleItem schedule = new ScheduleItem();
                        JSONObject jsonSchedule = result.getJSONObject(i);
                        schedule.setType(jsonSchedule.getString("type"));
                        schedule.setName(jsonSchedule.getString("name"));

                        List<ScheduleSubItem> list = new ArrayList<>();
                        JSONArray jsonList = jsonSchedule.getJSONArray("list");
                        for (int j = 0; j < jsonList.length(); j++) {
                            ScheduleSubItem item = new ScheduleSubItem();
                            JSONObject obj = jsonList.getJSONObject(j);
                            item.setLeague(obj.getString("leauge"));
                            item.setTime(obj.getString("time"));
                            item.setDate(obj.getString("date"));
                            item.setDuration(Integer.parseInt(obj.getString("match_end")));
                            JSONArray jsonTeam = obj.getJSONArray("team");
                            if (jsonTeam.length() > 1) {
                                JSONObject jsonTeam1 = jsonTeam.getJSONObject(0);
                                JSONObject jsonTeam2 = jsonTeam.getJSONObject(1);
                                item.setTeamName1(jsonTeam1.getString("name"));
                                item.setTeamLogo1(jsonTeam1.getString("logo"));
                                item.setTeamName2(jsonTeam2.getString("name"));
                                item.setTeamLogo2(jsonTeam2.getString("logo"));
                            } else {
                                item.setTeamName1("");
                                item.setTeamLogo1("");
                                item.setTeamName2("");
                                item.setTeamLogo2("");
                            }
                            JSONArray jsonChannel = obj.getJSONArray("channel");
                            if (jsonChannel.length() > 0) {
                                JSONObject jsonCh = jsonChannel.getJSONObject(0);
                                item.setChannelName(jsonCh.getString("name"));
                                item.setChannelStream(jsonCh.getString("stream"));
                            }

                            list.add(item);
                        }
                        schedule.setList(list);
                        scheduleList.add(schedule);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                recyclerView.setAdapter(new LiveScheduleAdapter(scheduleList.get(0).getList(), currentTime, mListener));
                recyclerView.requestFocus();

                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(String streamKey);
    }
}
