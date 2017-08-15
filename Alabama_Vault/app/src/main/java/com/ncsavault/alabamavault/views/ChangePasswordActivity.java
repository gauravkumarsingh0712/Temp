package com.ncsavault.alabamavault.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.ChangePasswordModel;
import com.ncsavault.alabamavault.utils.Utils;

import java.lang.reflect.Type;

/**
 * Created by gauravkumar.singh on 16-08-2017.
 */
public class ChangePasswordActivity extends BaseActivity implements TextWatcher, AbstractView {

    private EditText edOldPassword;
    private EditText edNewPassword;
    private EditText edConfirmPassword;
    private TextView tvSavePassword;
    private TextView tvBack;
    private CheckBox chkShowPassword;
    private LinearLayout ll_header_image;

    private boolean isAllFieldsChecked = false;
    private boolean isEditing = false;
    private AlertDialog alertDialog;
    ProgressDialog pDialog;
    Animation animation= null;

    private ChangePasswordModel mChangePasswordModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_change_activity);

        initViews();
        initData();
        initListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog != null)
            pDialog.dismiss();
       Utils.getInstance().gethideKeyboard(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isEditing) {
            showConfirmation();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void initViews() {
        edOldPassword = (EditText) findViewById(R.id.ed_old_password);
        edNewPassword = (EditText) findViewById(R.id.ed_new_password);
        edConfirmPassword = (EditText) findViewById(R.id.ed_confirm_password);

        edOldPassword.setTypeface(Typeface.DEFAULT);
        edNewPassword.setTypeface(Typeface.DEFAULT);
        edConfirmPassword.setTypeface(Typeface.DEFAULT);

        tvSavePassword = (TextView) findViewById(R.id.tv_save);
        tvBack = (TextView) findViewById(R.id.tv_back);
        chkShowPassword = (CheckBox) findViewById(R.id.chk_show_password);
        ll_header_image = (LinearLayout) findViewById(R.id.ll_header_image);

    }

    @Override
    public void initData() {
        Point size = new Point();
        WindowManager w = getWindowManager();
        int screenWidth = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            screenWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            screenWidth = d.getWidth();
        }
        int dimension = (int) (screenWidth * 0.45);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dimension, dimension);
        lp.setMargins(0, 20, 0, 0);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
       // ll_header_image.setLayoutParams(lp);
    }

    @Override
    public void initListener() {
        edOldPassword.addTextChangedListener(this);
        edNewPassword.addTextChangedListener(this);
        edConfirmPassword.addTextChangedListener(this);


        edOldPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (!isValidPass(edOldPassword.getText().toString(), edOldPassword, "Old Password"))
                        return true;
                }
                return false;
            }
        });

        edNewPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (!isValidPass(edNewPassword.getText().toString(), edNewPassword, "New Password"))
                        return true;
                }
                return false;
            }
        });

        edConfirmPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (!isValidPass(edConfirmPassword.getText().toString(), edConfirmPassword, "Confirm Password"))
                        return true;
                    else {
                        if (!isVaildNewPassword(edConfirmPassword.getText().toString()))
                            return true;
                        else {
                            if (checkPasswordEquality())
                                Utils.getInstance().showToastMessage(ChangePasswordActivity.this, "New and old password cannot be same", findViewById(R.id.llToast));
                            else
                                changePasswordCall();
                        }
                    }
                }
                return false;
            }
        });

        chkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edOldPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    edNewPassword.setInputType(129);
                    edConfirmPassword.setInputType(129);
                    edOldPassword.setInputType(129);
                }
                edOldPassword.setTypeface(Typeface.DEFAULT);
                edNewPassword.setTypeface(Typeface.DEFAULT);
                edConfirmPassword.setTypeface(Typeface.DEFAULT);
            }
        });

        tvSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(ChangePasswordActivity.this);
                if (Utils.isInternetAvailable(getApplicationContext())) {
                    isAllFieldsChecked = true;
                    if (!isValidPass(edOldPassword.getText().toString(), edOldPassword, "Old Password")) {
                        isAllFieldsChecked = false;
                    } else if (!isValidPass(edNewPassword.getText().toString(), edNewPassword, "New Password")) {
                        isAllFieldsChecked = false;
                    } else if (!isValidPass(edConfirmPassword.getText().toString(), edConfirmPassword, "Confirm Password")) {
                        isAllFieldsChecked = false;
                    } else {
                        if (!isVaildNewPassword(edConfirmPassword.getText().toString())) {
                            isAllFieldsChecked = false;
                        }
                    }

                    if (isAllFieldsChecked) {
                        if (checkPasswordEquality())
                            Utils.getInstance().showToastMessage(ChangePasswordActivity.this, "New and old password cannot be same", findViewById(R.id.llToast));
                        else
                            changePasswordCall();
                    }
                } else {
                    Utils.getInstance().showToastMessage(ChangePasswordActivity.this, GlobalConstants.MSG_NO_CONNECTION, findViewById(R.id.llToast));
                }
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditing) {
                    showConfirmation();
                } else {
                    finish();
                }
            }
        });
    }

    public void showConfirmation() {
        if (isEditing) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setMessage("Do you want to save changes you made?");
            alertDialogBuilder.setTitle("Alert");
            alertDialogBuilder.setPositiveButton("Save",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            isAllFieldsChecked = true;
                            if (!isValidPass(edOldPassword.getText().toString(), edOldPassword, "Old Password") || !isValidPass(edNewPassword.getText().toString(), edNewPassword, "New Password") || !isValidPass(edConfirmPassword.getText().toString(), edConfirmPassword, "Confirm Password"))
                                isAllFieldsChecked = false;
                            else {
                                if (!isVaildNewPassword(edConfirmPassword.getText().toString())) {
                                    isAllFieldsChecked = false;
                                }
                            }

                            if (isAllFieldsChecked) {
                                if (checkPasswordEquality())
                                    Utils.getInstance().showToastMessage(ChangePasswordActivity.this, "New and old password cannot be same", findViewById(R.id.llToast));
                                else
                                    changePasswordCall();
                            }
                            alertDialog.dismiss();
                        }
                    });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            alertDialog.dismiss();
                            finish();
                        }
                    });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }


    private boolean isValidPass(String str, EditText edCheck, String fieldName) {
        if (str != null && str.length() >= 6) {
            return true;
        } else {
            if (str.length() == 0)
                Utils.getInstance().showToastMessage(ChangePasswordActivity.this, fieldName + " should not be empty", findViewById(R.id.llToast));
            else if (str.length() < 6)
                Utils.getInstance().showToastMessage(ChangePasswordActivity.this, fieldName + " should have minimum 6 characters!", findViewById(R.id.llToast));
        }
        return false;
    }

    private boolean isVaildNewPassword(String confirmPass) {
        if (confirmPass != null) {
            if (confirmPass.equals(edNewPassword.getText().toString()))
                return true;
            else {
                Utils.getInstance().showToastMessage(ChangePasswordActivity.this, "Password does not match", findViewById(R.id.llToast));
            }
        }
        return false;
    }

    private boolean checkPasswordEquality() {
        if (edOldPassword.getText().toString().equals(edNewPassword.getText().toString())) {
            return true;
        }
        return false;
    }

    public void changePasswordCall() {
        String oldPassword = "";
        String newPassword = "";

        pDialog = new ProgressDialog(ChangePasswordActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(ChangePasswordActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        oldPassword = edOldPassword.getText().toString();
        newPassword = edNewPassword.getText().toString();

        if (mChangePasswordModel != null) {
            mChangePasswordModel.unRegisterView(this);
        }
        mChangePasswordModel = AppController.getInstance().getModelFacade().getRemoteModel().getChangePasswordModel();
        mChangePasswordModel.registerView(this);
        mChangePasswordModel.setProgressDialog(pDialog);
        mChangePasswordModel.loadChnagePasswordData(oldPassword, newPassword);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isEditing = true;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

//    public void showToastMessage(String message){
//        View includedLayout = findViewById(R.id.llToast);
//
//        final TextView text = (TextView) includedLayout.findViewById(R.id.tv_toast_message);
//        text.setText(message);
//
//        animation = AnimationUtils.loadAnimation(this,
//                R.anim.abc_fade_in);
//
//        text.setAnimation(animation);
//        text.setVisibility(View.VISIBLE);
//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable(){
//            @Override
//            public void run(){
//                animation = AnimationUtils.loadAnimation(ChangePasswordActivity.this,
//                        R.anim.abc_fade_out);
//
//                text.setAnimation(animation);
//                text.setVisibility(View.GONE);
//            }
//        }, 2000);
//    }

    @Override
    public void update() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mChangePasswordModel != null && mChangePasswordModel.getState() == BaseModel.STATE_SUCCESS) {
                    APIResponse response;
                    try {
                        if (Utils.isInternetAvailable(ChangePasswordActivity.this)) {
                            if (mChangePasswordModel.getResult() != null) {
                                Gson gson = new Gson();
                                Type classType = new TypeToken<APIResponse>() {
                                }.getType();
                                response = gson.fromJson(mChangePasswordModel.getResult().trim(), classType);
                                if (response != null) {
                                    if (response.getReturnStatus() != null) {
                                        if (response.getReturnStatus().toLowerCase().equals("true")) {
                                            Utils.getInstance().gethideKeyboard(ChangePasswordActivity.this);
                                            Utils.getInstance().showToastMessage(ChangePasswordActivity.this, "Password changed successfully", findViewById(R.id.llToast));
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 2000);
                                        } else {
                                            Utils.getInstance().showToastMessage(ChangePasswordActivity.this, "Old Password is incorrect", findViewById(R.id.llToast));
                                        }

                                    }
                                }
                            } else {
                                Utils.getInstance().showToastMessage(ChangePasswordActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, findViewById(R.id.llToast));
                            }
                        } else {
                            Utils.getInstance().showToastMessage(ChangePasswordActivity.this, GlobalConstants.MSG_NO_CONNECTION, findViewById(R.id.llToast));
                        }
                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                    }
                }
            }
        });
    }
}
