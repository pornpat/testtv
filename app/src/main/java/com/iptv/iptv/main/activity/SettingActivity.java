package com.iptv.iptv.main.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.PrefUtils;

import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.UpdateManagerListener;

public class SettingActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    Button mDefaultButton;

    private NetworkStateReceiver networkStateReceiver;

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

        findViewById(R.id.btn_network).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                }
                return false;
            }
        });

        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateManager.register(SettingActivity.this, "1f94e29f831a4d199ddd98835c3ebec4",
                        new UpdateManagerListener() {
                            @Override
                            public void onNoUpdateAvailable() {
                                super.onNoUpdateAvailable();
                                Toast.makeText(SettingActivity.this, "No updates found", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onUpdateAvailable() {
                                super.onUpdateAvailable();
                            }
                        });
            }
        });

        findViewById(R.id.btn_update).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    UpdateManager.register(SettingActivity.this, "1f94e29f831a4d199ddd98835c3ebec4",
                            new UpdateManagerListener() {
                                @Override
                                public void onNoUpdateAvailable() {
                                    super.onNoUpdateAvailable();
                                    Toast.makeText(SettingActivity.this, "No updates found", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onUpdateAvailable() {
                                    super.onUpdateAvailable();
                                }
                            });
                }
                return false;
            }
        });

        mDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrefUtils.getBooleanProperty(R.string.pref_default_on)) {
                    getPackageManager().setComponentEnabledSetting(new ComponentName("com.iptv.iptv", "com.iptv.iptv.main.activity.StartupActivity"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    PrefUtils.setBooleanProperty(R.string.pref_default_on, false);
                    mDefaultButton.setText("App Default: OFF");
                    // clear default
//                getPackageManager().clearPackagePreferredActivities("com.iptv.iptv");
                } else {
                    getPackageManager().setComponentEnabledSetting(new ComponentName("com.iptv.iptv", "com.iptv.iptv.main.activity.StartupActivity"),
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                    PrefUtils.setBooleanProperty(R.string.pref_default_on, true);
                    mDefaultButton.setText("App Default: ON");
                }
            }
        });

        mDefaultButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (PrefUtils.getBooleanProperty(R.string.pref_default_on)) {
                        getPackageManager().setComponentEnabledSetting(new ComponentName("com.iptv.iptv", "com.iptv.iptv.main.activity.StartupActivity"),
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                        PrefUtils.setBooleanProperty(R.string.pref_default_on, false);
                        mDefaultButton.setText("App Default: OFF");
                        // clear default
//                getPackageManager().clearPackagePreferredActivities("com.iptv.iptv");
                    } else {
                        getPackageManager().setComponentEnabledSetting(new ComponentName("com.iptv.iptv", "com.iptv.iptv.main.activity.StartupActivity"),
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                        PrefUtils.setBooleanProperty(R.string.pref_default_on, true);
                        mDefaultButton.setText("App Default: ON");
                    }
                }
                return false;
            }
        });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
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
