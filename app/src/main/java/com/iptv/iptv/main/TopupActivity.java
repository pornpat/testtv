package com.iptv.iptv.main;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.iptv.iptv.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TopupActivity extends LeanbackActivity {

    Button mPincodeButton;
    Button mTrueButton;
    CheckBox mWalletDateBox;
    CheckBox mWalletRefBox;
    CheckBox mMoneyRefBox;

    EditText mPincodeText;
    EditText mMoneyRefText;
    EditText mMoneyPriceText;
    EditText mWalletRefText;
    EditText mWalletPriceText;
    EditText mDateText;
    EditText mMonthText;
    EditText mYearText;
    EditText mHourText;
    EditText mMinuteText;
    EditText mPriceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        getWindow().setBackgroundDrawableResource(R.drawable.custom_background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPincodeText = (EditText) findViewById(R.id.txt_pincode);
        mMoneyRefText = (EditText) findViewById(R.id.txt_money_ref);
        mMoneyPriceText = (EditText) findViewById(R.id.txt_money_price);
        mWalletRefText = (EditText) findViewById(R.id.txt_wallet_ref);
        mWalletPriceText = (EditText) findViewById(R.id.txt_wallet_price);
        mDateText = (EditText) findViewById(R.id.txt_date);
        mMonthText = (EditText) findViewById(R.id.txt_month);
        mYearText = (EditText) findViewById(R.id.txt_year);
        mHourText = (EditText) findViewById(R.id.txt_hour);
        mMinuteText = (EditText) findViewById(R.id.txt_minute);
        mPriceText = (EditText) findViewById(R.id.txt_price);

        mPincodeButton = (Button) findViewById(R.id.btn_pincode);
        mPincodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPincodeButton.setBackground(ContextCompat.getDrawable(TopupActivity.this, R.drawable.bg_topup_activated_button));
                mTrueButton.setBackground(ContextCompat.getDrawable(TopupActivity.this, R.drawable.bg_topup_button));

                findViewById(R.id.layout_pincode).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_true).setVisibility(View.GONE);

                mPincodeText.requestFocus();
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

                findViewById(R.id.btn_wallet_date).requestFocus();
            }
        });

        mWalletDateBox = (CheckBox) findViewById(R.id.cb_wallet_date);
        mWalletRefBox = (CheckBox) findViewById(R.id.cb_wallet_ref);
        mMoneyRefBox = (CheckBox) findViewById(R.id.cb_money_ref);

        findViewById(R.id.btn_wallet_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.layout_wallet_date).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_wallet_ref).setVisibility(View.GONE);
                findViewById(R.id.layout_money_ref).setVisibility(View.GONE);

                mWalletDateBox.setChecked(true);
                mWalletRefBox.setChecked(false);
                mMoneyRefBox.setChecked(false);
            }
        });

        findViewById(R.id.btn_wallet_ref).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.layout_wallet_date).setVisibility(View.GONE);
                findViewById(R.id.layout_wallet_ref).setVisibility(View.VISIBLE);
                findViewById(R.id.layout_money_ref).setVisibility(View.GONE);

                mWalletDateBox.setChecked(false);
                mWalletRefBox.setChecked(true);
                mMoneyRefBox.setChecked(false);
            }
        });

        findViewById(R.id.btn_money_ref).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.layout_wallet_date).setVisibility(View.GONE);
                findViewById(R.id.layout_wallet_ref).setVisibility(View.GONE);
                findViewById(R.id.layout_money_ref).setVisibility(View.VISIBLE);

                mWalletDateBox.setChecked(false);
                mWalletRefBox.setChecked(false);
                mMoneyRefBox.setChecked(true);
            }
        });
    }

    public void applyPincodeTopup(View v) {
        if (mPincodeText.getText().length() == 9) {
            final ProgressDialog pDialog = new ProgressDialog(TopupActivity.this);
            pDialog.setMessage("กำลังดำเนินการ..");
            pDialog.show();

            RequestParams params = new RequestParams();
            params.put("pincode", mPincodeText.getText().toString());

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(ApiUtils.appendUri(ApiUtils.TOPUP_PINCODE, ApiUtils.addSession()), params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(TopupActivity.this, responseString, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONObject payment = jsonObject.getJSONObject("payment");
                        Toast.makeText(TopupActivity.this, payment.getString("message"), Toast.LENGTH_LONG).show();
                        boolean status = payment.getBoolean("status");
                        if (status) {
                            int day = payment.getInt("day");

                            // add day to PACKAGE API
                            RequestParams params = new RequestParams();
                            params.put("package", "ALL");
                            params.put("days", day);

                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post(ApiUtils.appendUri(ApiUtils.PACKAGE_URL, ApiUtils.addToken()), params, new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    pDialog.dismiss();
                                    Toast.makeText(TopupActivity.this, "ขออภัย มีความผิดพลาดในการเติมเงิน", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    pDialog.dismiss();
                                    finish();
                                }
                            });
                        } else {
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "รหัส Pincode ไม่ถูกต้อง กรุณากรอกใหม่่", Toast.LENGTH_SHORT).show();
        }
    }

    public void applyMoneyRefTopup(View v) {
        if (mMoneyRefText.getText().length() > 0 && mMoneyPriceText.getText().length() > 0) {
            final ProgressDialog pDialog = new ProgressDialog(TopupActivity.this);
            pDialog.setMessage("กำลังดำเนินการ..");
            pDialog.show();

            RequestParams params = new RequestParams();
            params.put("type", "truemoney");
            params.put("price", Integer.parseInt(mMoneyPriceText.getText().toString()));
            params.put("reference", Integer.parseInt(mMoneyRefText.getText().toString()));

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(ApiUtils.appendUri(ApiUtils.TOPUP_TRUE, ApiUtils.addSession()), params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(TopupActivity.this, responseString, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONObject payment = jsonObject.getJSONObject("payment");
                        boolean status = payment.getBoolean("status");
                        if (status) {
                            int transaction = payment.getInt("transaction");

                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(ApiUtils.appendUri(ApiUtils.getTopupTransaction(transaction), ApiUtils.addSession()), new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Toast.makeText(TopupActivity.this, responseString, Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseString);
                                        JSONObject payment = jsonObject.getJSONObject("payment");
                                        Toast.makeText(TopupActivity.this, payment.getString("message"), Toast.LENGTH_LONG).show();
                                        boolean status = payment.getBoolean("status");
                                        if (status) {
                                            int day = payment.getInt("day");

                                            // add day to PACKAGE API
                                            RequestParams params = new RequestParams();
                                            params.put("package", "ALL");
                                            params.put("days", day);

                                            AsyncHttpClient client = new AsyncHttpClient();
                                            client.post(ApiUtils.appendUri(ApiUtils.PACKAGE_URL, ApiUtils.addToken()), params, new TextHttpResponseHandler() {
                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                    pDialog.dismiss();
                                                    Toast.makeText(TopupActivity.this, "ขออภัย มีความผิดพลาดในการเติมเงิน", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                                    pDialog.dismiss();
                                                    finish();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        pDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(TopupActivity.this, payment.getString("message"), Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        }
    }

    public void applyWalletRefTopup(View v) {
        if (mWalletRefText.getText().length() > 0 && mWalletPriceText.getText().length() > 0) {
            final ProgressDialog pDialog = new ProgressDialog(TopupActivity.this);
            pDialog.setMessage("กำลังดำเนินการ..");
            pDialog.show();

            RequestParams params = new RequestParams();
            params.put("type", "wallet_ref");
            params.put("price", Integer.parseInt(mWalletPriceText.getText().toString()));
            params.put("reference", Integer.parseInt(mWalletRefText.getText().toString()));

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(ApiUtils.appendUri(ApiUtils.TOPUP_TRUE, ApiUtils.addSession()), params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(TopupActivity.this, responseString, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONObject payment = jsonObject.getJSONObject("payment");
                        boolean status = payment.getBoolean("status");
                        if (status) {
                            int transaction = payment.getInt("transaction");

                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(ApiUtils.appendUri(ApiUtils.getTopupTransaction(transaction), ApiUtils.addSession()), new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Toast.makeText(TopupActivity.this, responseString, Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseString);
                                        JSONObject payment = jsonObject.getJSONObject("payment");
                                        Toast.makeText(TopupActivity.this, payment.getString("message"), Toast.LENGTH_LONG).show();
                                        boolean status = payment.getBoolean("status");
                                        if (status) {
                                            int day = payment.getInt("day");

                                            // add day to PACKAGE API
                                            RequestParams params = new RequestParams();
                                            params.put("package", "ALL");
                                            params.put("days", day);

                                            AsyncHttpClient client = new AsyncHttpClient();
                                            client.post(ApiUtils.appendUri(ApiUtils.PACKAGE_URL, ApiUtils.addToken()), params, new TextHttpResponseHandler() {
                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                    pDialog.dismiss();
                                                    Toast.makeText(TopupActivity.this, "ขออภัย มีความผิดพลาดในการเติมเงิน", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                                    pDialog.dismiss();
                                                    finish();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        pDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(TopupActivity.this, payment.getString("message"), Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        }
    }

    public void applyWalletDateTopup(View v) {
        if (mDateText.getText().length() > 0 && mMonthText.getText().length() > 0 && mYearText.getText().length() > 0
                && mHourText.getText().length() > 0 && mMinuteText.getText().length() > 0 && mPriceText.getText().length() > 0) {
            final ProgressDialog pDialog = new ProgressDialog(TopupActivity.this);
            pDialog.setMessage("กำลังดำเนินการ..");
            pDialog.show();

            if (mDateText.getText().length() == 1) {
                mDateText.setText("0" + mDateText.getText().toString());
            }
            if (mMonthText.getText().length() == 1) {
                mMonthText.setText("0" + mMonthText.getText().toString());
            }
            if (mHourText.getText().length() == 1) {
                mHourText.setText("0" + mHourText.getText().toString());
            }
            if (mMinuteText.getText().length() == 1) {
                mMinuteText.setText("0" + mMinuteText.getText().toString());
            }

            RequestParams params = new RequestParams();
            params.put("type", "wallet_date");
            params.put("price", Integer.parseInt(mPriceText.getText().toString()));
            params.put("reference", mYearText.getText().toString() + "-" + mMonthText.getText().toString() + "-" +
                    mDateText.getText().toString() + " " + mHourText.getText().toString() + ":" + mMinuteText.getText().toString() + ":00");

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(ApiUtils.appendUri(ApiUtils.TOPUP_TRUE, ApiUtils.addSession()), params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(TopupActivity.this, responseString, Toast.LENGTH_LONG).show();
                    pDialog.dismiss();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        JSONObject payment = jsonObject.getJSONObject("payment");
                        boolean status = payment.getBoolean("status");
                        if (status) {
                            int transaction = payment.getInt("transaction");

                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(ApiUtils.appendUri(ApiUtils.getTopupTransaction(transaction), ApiUtils.addSession()), new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Toast.makeText(TopupActivity.this, responseString, Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseString);
                                        JSONObject payment = jsonObject.getJSONObject("payment");
                                        Toast.makeText(TopupActivity.this, payment.getString("message"), Toast.LENGTH_LONG).show();
                                        boolean status = payment.getBoolean("status");
                                        if (status) {
                                            int day = payment.getInt("day");

                                            // add day to PACKAGE API
                                            RequestParams params = new RequestParams();
                                            params.put("package", "ALL");
                                            params.put("days", day);

                                            AsyncHttpClient client = new AsyncHttpClient();
                                            client.post(ApiUtils.appendUri(ApiUtils.PACKAGE_URL, ApiUtils.addToken()), params, new TextHttpResponseHandler() {
                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                                    pDialog.dismiss();
                                                    Toast.makeText(TopupActivity.this, "ขออภัย มีความผิดพลาดในการเติมเงิน", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                                    pDialog.dismiss();
                                                    finish();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        pDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(TopupActivity.this, payment.getString("message"), Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        }
    }

}
