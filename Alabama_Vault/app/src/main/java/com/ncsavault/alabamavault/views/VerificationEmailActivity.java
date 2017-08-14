package com.ncsavault.alabamavault.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.FetchingAllDataModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gauravkumar.singh on 3/16/2017.
 */

public class VerificationEmailActivity extends BaseActivity implements AbstractView {

    private EditText registeredEmailId, verificationCode;
    private TextView tvHeaderText, tvVerificationCode, tvResendCode, tvCancel, tvBack;
    private AsyncTask<Void, Void, String> mChangeTask;
    ProgressDialog pDialog;
    private String verificationCodeValue;
    private Animation animation;
    private long userId;
    private Button nextButtonTextView, tvSubmitButton;
    private AlertDialog alertDialog;
    private User vaultUser;
    private String mRegisteredEmailId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verification_email);

        vaultUser = AppController.getInstance().getModelFacade().getLocalModel().getUser();
        mRegisteredEmailId = vaultUser.getEmailID();
        initViews();
        initData();
        initListener();

        verificationEmailCall(mRegisteredEmailId);
    }


    @Override
    public void initViews() {
        registeredEmailId = (EditText) findViewById(R.id.ed_registered_email_id);
        if (getIntent() != null) {
            String emailId = getIntent().getStringExtra("email_id");
            registeredEmailId.setText(emailId);
        }
       // tvHeaderText = (TextView) findViewById(R.id.tv_header_text);
        nextButtonTextView = (Button) findViewById(R.id.tv_next);

        verificationCode = (EditText) findViewById(R.id.ed_verification_code);
       // tvVerificationCode = (TextView) findViewById(R.id.tv_verification_text);
        tvSubmitButton = (Button) findViewById(R.id.tv_submit);
        tvResendCode = (TextView) findViewById(R.id.tv_resend);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        //tvBack = (TextView) findViewById(R.id.tv_back);
        tvSubmitButton.setVisibility(View.VISIBLE);
        tvResendCode.setVisibility(View.VISIBLE);

    }

    @Override
    public void initData() {
        registeredEmailId.setFocusableInTouchMode(true);
        registeredEmailId.requestFocus();
    }

    @Override
    public void initListener() {


        tvSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationCode.getText().toString().equals("")) {
                    showToastMessage("Please enter verification code.");
                } else if (verificationCode.getText().toString().equals(verificationCodeValue)) {
                    //GK  fetchInitialRecordsForAll();
                    overrideUserData(vaultUser);
                } else {
                    showToastMessage("Entered code is either invalid or expired.");
                }

            }
        });

        verificationCode.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (verificationCode.getText().toString().equals(verificationCodeValue)) {
                        //GK  fetchInitialRecordsForAll();
                        overrideUserData(vaultUser);
                    } else if (verificationCode.getText().toString().equals("")) {
                        showToastMessage("Please enter verification code");
                    } else {
                        showToastMessage("Entered code is either invalid or expired.");
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registeredEmailId.getText().toString() != null) {
                    showAlert(registeredEmailId.getText().toString());
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerificationEmailActivity.this, LoginEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("key", true);
                intent.putExtra("email_id", registeredEmailId.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.slidedown, R.anim.nochange);
                finish();
            }
        });

//        tvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(VerificationEmailActivity.this, LoginEmailActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                intent.putExtra("key_is", true);
//                intent.putExtra("status", "vt_exists");
//                intent.putExtra("email", registeredEmailId.getText().toString());
//                startActivity(intent);
//                overridePendingTransition(R.anim.slideup, R.anim.nochange);
//                Utils.getInstance().gethideKeyboard(VerificationEmailActivity.this);
//                finish();
//            }
//        });
    }


    AsyncTask<Void, Void, String> mLoginTask;

    private void overrideUserData(final User vaultUser) {
        mLoginTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(VerificationEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(VerificationEmailActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {

                    result = AppController.getInstance().getServiceManager().getVaultService().updateUserData(vaultUser);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    System.out.println("Result of post user data : " + result);
                    if (result.contains("true") || result.contains("success")) {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(result.trim(), classType);
                        pDialog.dismiss();
                        SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                        pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, vaultUser.getUsername()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, vaultUser.getEmailID()).apply();
                        pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();
                        // isfetchData = true;

                        fetchInitialRecordsForAll();
//gk                        params.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, "gmail_exist");
//gk                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, params);
                    } else {
                        try {
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response.getReturnStatus() != null) {
                                if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Vault Account");
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Gmail");
                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Twitter");
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Facebook");
                                }
                            } else {
                                pDialog.dismiss();
                                LoginManager.getInstance().logOut();
                                // tvFacebookLogin.setText("Login with Facebook");
                                // showToastMessage(result);
                                showToastMessage("Can not connect to server. Please try again...");
                            }

                            mLoginTask = null;
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            pDialog.dismiss();
                            mLoginTask = null;
                            // tvFacebookLogin.setText("Login with Facebook");
                            showToastMessage("We are unable to process your request");
                        }
                    }

                }
            }
        };
        mLoginTask.execute();
    }

    public void verificationEmailCall(final String registeredEmail) {
        if (Utils.isInternetAvailable(this)) {
//                View view = getCurrentFocus();
//                if (view != null) {
//                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                }
            if (mChangeTask == null) {
                mChangeTask = new AsyncTask<Void, Void, String>() {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        pDialog = new ProgressDialog(VerificationEmailActivity.this, R.style.CustomDialogTheme);
                        pDialog.show();
                        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(VerificationEmailActivity.this));
                        pDialog.setCanceledOnTouchOutside(false);
                        pDialog.setCancelable(false);
                    }


                    @Override
                    protected String doInBackground(Void... params) {
                        String result = "";
                        try {
                            result = AppController.getInstance().getServiceManager().getVaultService().forgotPassword(registeredEmail,false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);

                        try {
                            if (Utils.isInternetAvailable(VerificationEmailActivity.this)) {
                                if (result != null) {
                                    Gson gson = new Gson();
                                    Type classType = new TypeToken<APIResponse>() {
                                    }.getType();
                                    APIResponse response = gson.fromJson(result.trim(), classType);
                                    userId = response.getUserID();
                                    SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                                    pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, userId).apply();
                                    if (userId > 0) {
                                        verificationCodeValue = response.getVerficationCode();
                                    }
                                } else {
                                    showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                                }
                            } else {
                                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                            }
                            mChangeTask = null;
                            pDialog.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mChangeTask = null;
                            pDialog.dismiss();
                        }
                    }
                };
                mChangeTask.execute();
            }

        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    private Animation leftOutAnimation, leftInAnimation;
    private Animation rightInAnimation, rightOutAnimation;



    public void showToastMessage(String message) {
        View includedLayout = findViewById(R.id.llToast);

        final TextView text = (TextView) includedLayout.findViewById(R.id.tv_toast_message);
        text.setText(message);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.abc_fade_in);

        text.setAnimation(animation);
        text.setVisibility(View.VISIBLE);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(VerificationEmailActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void showAlertDialog(String mesg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(mesg);

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();

                        AppController.getInstance().getModelFacade().getLocalModel().setRegisteredEmailIdForgot(true);
                        AppController.getInstance().getModelFacade().getLocalModel().setRegisteredEmailIdForgot(registeredEmailId.getText().toString());
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();


                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            showToastMessage("Please enter registered email id");
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                showToastMessage("Please enter registered email id");
                return false;
            } else
                return matcher.matches();
        }
    }


    public void showAlert(final String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Verification code has been sent to be on " + emailId + " .");
        alertDialogBuilder.setTitle("Confirmation");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        verificationEmailCall(emailId);
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void fetchInitialRecordsForAll() {


        pDialog = new ProgressDialog(VerificationEmailActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(VerificationEmailActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        if (mFetchingAllDataModel != null) {
            mFetchingAllDataModel.unRegisterView(this);
        }
        mFetchingAllDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getFetchingAllDataModel();
        mFetchingAllDataModel.registerView(this);
        mFetchingAllDataModel.setProgressDialog(pDialog);
        mFetchingAllDataModel.fetchData();

    }

    private FetchingAllDataModel mFetchingAllDataModel;

    @Override
    public void update() {

        System.out.println("Uploaded photo update");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Uploaded photo update 123");
                    if (mFetchingAllDataModel != null && mFetchingAllDataModel.getState() ==
                            BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {

                        pDialog.dismiss();
                        showAlertDialogForSuccess(GlobalConstants.USER_SUCCESSFULLY_REGISTERED);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void showAlertDialogForSuccess(String mesg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(mesg);

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        getFetchDataResponce();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void getFetchDataResponce() {
        try {
            mFetchingAllDataModel.unRegisterView(VerificationEmailActivity.this);
            if (Utils.isInternetAvailable(VerificationEmailActivity.this)) {
                if (mFetchingAllDataModel.getABoolean()) {
                    Profile fbProfile = Profile.getCurrentProfile();
                    SharedPreferences pref = AppController.getInstance().getApplicationContext().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                    // boolean isJoinMailChimp = pref.getBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP, false);
                    // pref.edit().putBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP, false).commit();
                    if (fbProfile != null || userId > 0) {
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_MAIN_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();
                       //gk if (!VideoDataService.isServiceRunning)
                            startService(new Intent(VerificationEmailActivity.this, TrendingFeaturedVideoService.class));

                    }
                }

            } else {
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            stopService(new Intent(VerificationEmailActivity.this, TrendingFeaturedVideoService.class));
            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
            pDialog.dismiss();
        }
    }

}
