package com.iptv.iptv.main.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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

        findViewById(R.id.btn_network).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                    return true;
                }
                return false;
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

        findViewById(R.id.btn_update).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
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
                    return true;
                }
                return false;
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

        mDefaultButton.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DPAD_CENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
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
                    return true;
                }
                return false;
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

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView) findViewById(R.id.txt_version)).setText("App Version " + pInfo.versionCode + " " + "(" + pInfo.versionName + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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
