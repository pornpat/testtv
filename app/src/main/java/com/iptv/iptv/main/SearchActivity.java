package com.iptv.iptv.main;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.iptv.iptv.R;

public class SearchActivity extends LeanbackActivity {

    EditText mSearchText;
    TextView mMovieText;
    TextView mSeriesText;
    TextView mLiveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mSearchText = (EditText) findViewById(R.id.search);
        mMovieText = (TextView) findViewById(R.id.movie);
        mSeriesText = (TextView) findViewById(R.id.series);
        mLiveText = (TextView) findViewById(R.id.live);

        mSearchText.requestFocus();
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch() {

        initialSelect();
    }

    private void initialSelect() {
        mMovieText.setVisibility(View.VISIBLE);
        mSeriesText.setVisibility(View.VISIBLE);
        mLiveText.setVisibility(View.VISIBLE);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        mSearchText.clearFocus();

        mMovieText.requestFocus();
    }

}
