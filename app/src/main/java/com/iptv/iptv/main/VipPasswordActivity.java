package com.iptv.iptv.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.iptv.iptv.R;

public class VipPasswordActivity extends AppCompatActivity {

    EditText mPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_password);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPasswordText = (EditText) findViewById(R.id.txt_password);
    }

    public void confirmPassword(View view) {
        if (mPasswordText.getText().toString().length() > 0) {
            if (mPasswordText.getText().toString().equals("2016")) {
                Intent intent = new Intent(VipPasswordActivity.this, TopupActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "ขออภัย รหัสยืนยันไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "กรุณาใส่รหัสยืนยัน", Toast.LENGTH_SHORT).show();
        }
    }

}
