package com.iptv.iptv.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.iptv.iptv.main.ApiUtils;
import com.iptv.iptv.main.NetworkStateReceiver;
import com.iptv.iptv.main.PrefUtils;
import com.iptv.iptv.main.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener {

    private EditText mUsernameText;
    private EditText mPasswordText;
    private ProgressDialog mProgressDialog;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mUsernameText = (EditText) findViewById(R.id.text_username);
        mPasswordText = (EditText) findViewById(R.id.text_password);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        mUsernameText.setText("user");
        mPasswordText.setText("123456");

        if (getIntent().hasExtra("toast")) {
            Toast.makeText(LoginActivity.this, "Token หมดอายุ กรุณาล็อกอินใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
        }

        if (!PrefUtils.getStringProperty(R.string.pref_token).equals("")) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

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

    public void signIn(View view) {
        if (mUsernameText.getText().toString().trim().length() == 0 || mPasswordText.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please input username and password", Toast.LENGTH_SHORT).show();
        } else if (!Utils.isInternetConnectionAvailable(this)) {
            Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        } else {
            mProgressDialog.show();

            RequestParams params = new RequestParams();
            params.put("name", mUsernameText.getText().toString());
            params.put("password", mPasswordText.getText().toString());

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(ApiUtils.AUTH_URL, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    mProgressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String token = jsonObject.getString("token");
                        PrefUtils.setStringProperty(R.string.pref_token, token);
                        PrefUtils.setStringProperty(R.string.pref_username, mUsernameText.getText().toString());

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void test() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApiUtils.appendUri(ApiUtils.addMediaId(ApiUtils.FAVORITE_URL, 12), ApiUtils.addToken()), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.v("testkn", responseString);
            }
        });
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
