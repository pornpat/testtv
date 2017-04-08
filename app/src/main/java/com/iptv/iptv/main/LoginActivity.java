package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mUsernameText = (EditText) findViewById(R.id.text_username);
        mPasswordText = (EditText) findViewById(R.id.text_password);

        mUsernameText.setText("tester");
        mPasswordText.setText("123456");
    }

    public void signIn(View view) {
//        RequestParams params = new RequestParams("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImlzcyI6Imh0dHA6XC9cLzEzOS41OS4yMzEuMTM1XC91cGxheVwvcHVibGljXC9hcGlcL3YxXC9hdXRoIiwiaWF0IjoxNDkxNjU0NjY1LCJleHAiOjE0OTE3NDEwNjUsIm5iZiI6MTQ5MTY1NDY2NSwianRpIjoiYWYwNDhkNGMxODBlMWQzZGI2ZTllMWQwZDZhNTNkZGEifQ.GMyho7yk0DiauXYC15xzzThYbEPlBq2ktwYCdthhz9s");
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get("http://139.59.231.135/uplay/public/api/v1/filters", params, new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Log.v("testkn", responseString);
//            }
//        });

        RequestParams params = new RequestParams();
        params.put("name", mUsernameText.getText().toString());
        params.put("password", mPasswordText.getText().toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://139.59.231.135/uplay/public/api/v1/auth", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    String token = jsonObject.getString("token");
                    PrefUtil.setStringProperty(R.string.pref_token, token);

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
