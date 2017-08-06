package com.iptv.iptv.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.ApiUtils;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.PrefUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserSettingActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    Button mChangePasswordButton;
    Button mLogoutButton;

    EditText mNewPasswordText;
    EditText mConfirmPasswordText;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChangePasswordButton = (Button) findViewById(R.id.btn_change_password);
        mLogoutButton = (Button) findViewById(R.id.btn_logout);

        mChangePasswordButton.requestFocus();

        mNewPasswordText = (EditText) findViewById(R.id.txt_new_password);
        mConfirmPasswordText = (EditText) findViewById(R.id.txt_confirm_password);

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChangePasswordButton.setBackground(ContextCompat.getDrawable(UserSettingActivity.this, R.drawable.bg_topup_activated_button));
                mLogoutButton.setBackground(ContextCompat.getDrawable(UserSettingActivity.this, R.drawable.bg_topup_button));

                findViewById(R.id.layout_change_password).setVisibility(View.VISIBLE);

                mNewPasswordText.requestFocus();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtils.setStringProperty(R.string.pref_token, "");
                Intent intent = new Intent(UserSettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void changePassword(View v) {
        if (mNewPasswordText.getText().toString().equals(mConfirmPasswordText.getText().toString())) {
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("โปรดรอ...");
            progress.show();

            RequestParams params = new RequestParams();
            params.put("new_password", mNewPasswordText.getText().toString());

            AsyncHttpClient client = new AsyncHttpClient();
            client.put(ApiUtils.appendUri(ApiUtils.CHANGE_PASSWORD_URL, ApiUtils.addToken()), params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(UserSettingActivity.this, "เกิดข้อผิดพลาด กรุณาลองใหม่ในภายหลัง", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String token = jsonObject.getString("token");
                        PrefUtils.setStringProperty(R.string.pref_token, token);

                        Toast.makeText(UserSettingActivity.this, "เปลี่ยนรหัสผ่านสำเร็จ", Toast.LENGTH_SHORT).show();
                        UserSettingActivity.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progress.dismiss();
                }
            });
        } else {
            Toast.makeText(UserSettingActivity.this, "กรุณาใส่ password ใหม่ให้ตรงกัน", Toast.LENGTH_SHORT).show();
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
