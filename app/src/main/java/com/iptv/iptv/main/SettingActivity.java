package com.iptv.iptv.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.iptv.iptv.R;

public class SettingActivity extends AppCompatActivity {

    Button mDefaultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.btn_update).requestFocus();

        mDefaultButton = (Button) findViewById(R.id.btn_default);
        if (PrefUtils.getBooleanProperty(R.string.pref_default_on)) {
            mDefaultButton.setText("App Default: ON");
        } else {
            mDefaultButton.setText("App Default: OFF");
        }

        findViewById(R.id.btn_network).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
            }
        });

        mDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefUtils.getBooleanProperty(R.string.pref_default_on)) {
                    getPackageManager().setComponentEnabledSetting(new ComponentName("com.iptv.iptv", "com.iptv.iptv.main.StartupActivity"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    PrefUtils.setBooleanProperty(R.string.pref_default_on, false);
                    mDefaultButton.setText("App Default: OFF");
                    // clear default
//                getPackageManager().clearPackagePreferredActivities("com.iptv.iptv");
                } else {
                    getPackageManager().setComponentEnabledSetting(new ComponentName("com.iptv.iptv", "com.iptv.iptv.main.StartupActivity"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    PrefUtils.setBooleanProperty(R.string.pref_default_on, true);
                    mDefaultButton.setText("App Default: ON");
                }
            }
        });
    }
}
