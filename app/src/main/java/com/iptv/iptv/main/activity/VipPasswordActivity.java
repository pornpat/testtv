package com.iptv.iptv.main.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.NetworkStateReceiver;

public class VipPasswordActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    EditText mPasswordText;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_password);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPasswordText = (EditText) findViewById(R.id.txt_password);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void confirmPassword(View view) {
        if (mPasswordText.getText().toString().length() > 0) {
            if (mPasswordText.getText().toString().equals("2016")) {
                Intent intent = new Intent(VipPasswordActivity.this, VipGridActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "ขออภัย รหัสยืนยันไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "กรุณาใส่รหัสยืนยัน", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {
        Toast.makeText(this, "Network unavailable.. Please check your wifi-connection", Toast.LENGTH_LONG).show();
    }

    public void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }
}
