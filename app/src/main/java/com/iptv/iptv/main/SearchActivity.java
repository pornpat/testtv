package com.iptv.iptv.main;

import android.os.Bundle;
import android.widget.EditText;

import com.iptv.iptv.R;

public class SearchActivity extends LeanbackActivity {

    EditText mSearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mSearchText = (EditText) findViewById(R.id.search);
//        mSearchText.requestFocus();
    }
}
