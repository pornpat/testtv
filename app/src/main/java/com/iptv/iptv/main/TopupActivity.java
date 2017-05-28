package com.iptv.iptv.main;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.iptv.iptv.R;

public class TopupActivity extends LeanbackActivity {

    Button mPincodeButton;
    Button mTrueButton;
//    View mWalletDateButton;
//    View mWalletRefButton;
//    View mMoneyRefButton;
    CheckBox mWalletDateBox;
    CheckBox mWalletRefBox;
    CheckBox mMoneyRefBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);

        mPincodeButton = (Button) findViewById(R.id.btn_pincode);
        mPincodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPincodeButton.setBackground(ContextCompat.getDrawable(TopupActivity.this, R.drawable.bg_topup_activated_button));
                mTrueButton.setBackground(ContextCompat.getDrawable(TopupActivity.this, R.drawable.bg_topup_button));

                findViewById(R.id.layout_pincode).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_true).setVisibility(View.GONE);
            }
        });

        mTrueButton = (Button) findViewById(R.id.btn_true);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTrueButton.setBackground(ContextCompat.getDrawable(TopupActivity.this, R.drawable.bg_topup_activated_button));
                mPincodeButton.setBackground(ContextCompat.getDrawable(TopupActivity.this, R.drawable.bg_topup_button));

                findViewById(R.id.layout_true).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_pincode).setVisibility(View.GONE);
            }
        });

        mWalletDateBox = (CheckBox) findViewById(R.id.cb_wallet_date);
        mWalletRefBox = (CheckBox) findViewById(R.id.cb_wallet_ref);
        mMoneyRefBox = (CheckBox) findViewById(R.id.cb_money_ref);

        findViewById(R.id.btn_wallet_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.layout_true_ref).setVisibility(View.VISIBLE);

                mWalletDateBox.setChecked(true);
                mWalletRefBox.setChecked(false);
                mMoneyRefBox.setChecked(false);
            }
        });

        findViewById(R.id.btn_wallet_ref).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.layout_true_ref).setVisibility(View.VISIBLE);

                mWalletDateBox.setChecked(false);
                mWalletRefBox.setChecked(true);
                mMoneyRefBox.setChecked(false);
            }
        });

        findViewById(R.id.btn_money_ref).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.layout_true_ref).setVisibility(View.VISIBLE);

                mWalletDateBox.setChecked(false);
                mWalletRefBox.setChecked(false);
                mMoneyRefBox.setChecked(true);
            }
        });

    }
}
