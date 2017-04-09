package com.iptv.iptv.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends LeanbackActivity {

    private EditText mUsernameText;
    private EditText mPasswordText;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mUsernameText = (EditText) findViewById(R.id.text_username);
        mPasswordText = (EditText) findViewById(R.id.text_password);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        mUsernameText.setText("tester");
        mPasswordText.setText("123456");
    }

    public void signIn(View view) {
        mProgressDialog.show();

        RequestParams params = new RequestParams();
        params.put("name", mUsernameText.getText().toString());
        params.put("password", mPasswordText.getText().toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlUtil.AUTH_URL, params, new TextHttpResponseHandler() {
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
                    PrefUtil.setStringProperty(R.string.pref_token, token);
                    PrefUtil.setStringProperty(R.string.pref_username, mUsernameText.getText().toString());

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void test() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlUtil.appendUri(UrlUtil.addMediaId(UrlUtil.FAVORITE_URL, 12), UrlUtil.addToken()), new TextHttpResponseHandler() {
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

}
