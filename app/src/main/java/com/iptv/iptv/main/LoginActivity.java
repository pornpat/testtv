package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.iptv.iptv.R;

public class LoginActivity extends LeanbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
