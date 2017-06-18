package com.iptv.iptv.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SettingActivity extends LeanbackActivity {

    Button mChangePasswordButton;
    Button mLogoutButton;

    EditText mNewPasswordText;
    EditText mConfirmPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mChangePasswordButton = (Button) findViewById(R.id.btn_change_password);
        mLogoutButton = (Button) findViewById(R.id.btn_logout);

        mNewPasswordText = (EditText) findViewById(R.id.txt_new_password);
        mConfirmPasswordText = (EditText) findViewById(R.id.txt_confirm_password);

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChangePasswordButton.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.bg_topup_activated_button));
                mLogoutButton.setBackground(ContextCompat.getDrawable(SettingActivity.this, R.drawable.bg_topup_button));

                findViewById(R.id.layout_change_password).setVisibility(View.VISIBLE);

                mNewPasswordText.requestFocus();
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtils.setStringProperty(R.string.pref_token, "");
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
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
                    Toast.makeText(SettingActivity.this, "เกิดข้อผิดพลาด กรุณาลองใหม่ในภายหลัง", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String token = jsonObject.getString("token");
                        PrefUtils.setStringProperty(R.string.pref_token, token);

                        Toast.makeText(SettingActivity.this, "เปลี่ยนรหัสผ่านสำเร็จ", Toast.LENGTH_SHORT).show();
                        SettingActivity.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progress.dismiss();
                }
            });
        } else {
            Toast.makeText(SettingActivity.this, "กรุณาใส่ password ใหม่ให้ตรงกัน", Toast.LENGTH_SHORT).show();
        }
    }

}
