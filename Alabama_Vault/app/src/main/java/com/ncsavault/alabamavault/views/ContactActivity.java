package com.ncsavault.alabamavault.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.CreateTaskOnAsanaModel;
import com.ncsavault.alabamavault.utils.Utils;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aqeeb.pathan on 01-07-2015.
 */
public class ContactActivity extends BaseActivity implements AbstractView {

    private TextView tvTitle, tvSubTitle, tvGuestText;
    private TextView tvClose;
    private TextView tvSubmit;
    private EditText edMessage, edName, edEmail;
    private Animation animation;

    private ProgressDialog pDialog;
    private String tagId = "";
    private AlertDialog alertDialog;
    private CreateTaskOnAsanaModel mCreateTaskOnAsanaModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);

        initViews();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.getInstance().gethideKeyboard(this);
        overridePendingTransition(R.anim.nochange, R.anim.slidedown);
    }

    @Override
    public void initViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        tvGuestText = (TextView) findViewById(R.id.tv_guest_text);

        tvClose = (TextView) findViewById(R.id.tv_close);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);

        edMessage = (EditText) findViewById(R.id.ed_message);
        edName = (EditText) findViewById(R.id.ed_name);
        edEmail = (EditText) findViewById(R.id.ed_email);
    }

    @Override
    public void initData() {
        long userID = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
        if (userID == GlobalConstants.DEFAULT_USER_ID) {
            tvTitle.setVisibility(View.GONE);
            tvSubTitle.setVisibility(View.GONE);

            tvGuestText.setVisibility(View.VISIBLE);
            edName.setVisibility(View.VISIBLE);
            edEmail.setVisibility(View.VISIBLE);
        } else {
            String title = getIntent().getStringExtra("title");
            String subTitle = getIntent().getStringExtra("subtitle");

            tvTitle.setText(title);
            tvSubTitle.setText(subTitle);

            tvTitle.setVisibility(View.VISIBLE);
            tvSubTitle.setVisibility(View.VISIBLE);

            tvGuestText.setVisibility(View.GONE);
            edName.setVisibility(View.GONE);
            edEmail.setVisibility(View.GONE);
        }

        tagId = GlobalConstants.NO_LOGIN_TAG_ID;//getIntent().getStringExtra("tagId");
    }

    @Override
    public void initListener() {
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(ContactActivity.this);
                if (!edMessage.getText().toString().isEmpty()) {
                    showConfirmationDialog();
                } else {
                    onBackPressed();
                }
            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(ContactActivity.this);
                boolean isChecked = true;
                long userId = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
                String nameAndEmail = "";
                if (userId == GlobalConstants.DEFAULT_USER_ID) {
                    if (!isValidEmail(edEmail.getText().toString()))
                        isChecked = false;
                    if (!isValidText(edName.getText().toString())) {
                        isChecked = false;
                        edName.setError("Minimum 3 characters required");
                    }
                    if (isChecked) {
                        nameAndEmail = edName.getText().toString() + " , " + edEmail.getText().toString();
                    }
                } else {
                    nameAndEmail = AppController.getInstance().getModelFacade().getLocalModel().getFName()
                            .toString() + " " + AppController.getInstance().getModelFacade().getLocalModel().
                            getLName().toString() + " , " + AppController.getInstance().getModelFacade().getLocalModel()
                            .getEmailAddress().toString();
                }
                if (isChecked) {
                    edMessage.setText(edMessage.getText().toString().trim());
                    if (!edMessage.getText().toString().isEmpty()) {


                        pDialog = new ProgressDialog(ContactActivity.this, R.style.CustomDialogTheme);
                        pDialog.show();
                        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(ContactActivity.this));
                        pDialog.setCanceledOnTouchOutside(false);
                        pDialog.setCancelable(false);

                        if(mCreateTaskOnAsanaModel != null)
                        {
                            mCreateTaskOnAsanaModel.unRegisterView(ContactActivity.this);
                        }
                        mCreateTaskOnAsanaModel = AppController.getInstance().getModelFacade().getRemoteModel()
                                .getCreateTaskOnAsanaModel();
                        mCreateTaskOnAsanaModel.registerView(ContactActivity.this);
                        mCreateTaskOnAsanaModel.setProgressDialog(pDialog);
                        mCreateTaskOnAsanaModel.loadAsanaData(nameAndEmail, edMessage.getText().toString(), tagId);

                    } else {
                        showToastMessage("Please provide message", false);
//                        edMessage.setError("Please provide message");
                    }
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            edEmail.setError("Email Not Entered!");
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                edEmail.setError("Invalid Email!");
                return false;
            } else
                return matcher.matches();
        }
    }

    private boolean isValidText(String text) {
        if (text != null && text.length() >= 3) {
            return true;
        }
        return false;
    }

    public void showConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Do you want to discard this message?");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Keep",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton("Discard",
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
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }

    @Override
    public void update() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mCreateTaskOnAsanaModel != null && mCreateTaskOnAsanaModel.getState() == BaseModel.STATE_SUCCESS) {
                    if (mCreateTaskOnAsanaModel.getStatusResult()) {
                        showTicketSuccess(mCreateTaskOnAsanaModel.getTaskId());
                    } else {
                        showToastMessage(GlobalConstants.EMAIL_FAILURE_MESSAGE, false);
                    }

                    pDialog.dismiss();
                }
            }
        });


    }


    public void showTicketSuccess(String taskId) {
        try {
            String successMessage = "";
            long userID = AppController.getInstance().getModelFacade().getLocalModel().getUserId();
            if (userID == GlobalConstants.DEFAULT_USER_ID)
                successMessage = "Thank you. Ticket #" + taskId + " has been created. Someone from "
                        + GlobalConstants.APP_FULL_NAME + " will reply to you via your registered email,  "
                        + edEmail.getText().toString() + ". We appreciate you taking the time to contact us. -The "
                        + GlobalConstants.APP_FULL_NAME;
            else
                successMessage = "Thank you. Ticket #" + taskId + " has been created. Someone from "
                        + GlobalConstants.APP_FULL_NAME + " will reply to you via your registered email,  "
                        + AppController.getInstance().getModelFacade().getLocalModel().getEmailAddress()
                        + ". We appreciate you taking the time to contact us. -The " + GlobalConstants.APP_FULL_NAME;
            AlertDialog alertDialog = null;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactActivity.this);
            alertDialogBuilder
                    .setMessage(successMessage);
//        alertDialogBuilder.setTitle("Success");

            alertDialogBuilder.setNegativeButton("Close",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });

            alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            nbutton.setAllCaps(false);
            nbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
            pbutton.setAllCaps(false);

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void showToastMessage(String message, final boolean closeContact) {

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
                animation = AnimationUtils.loadAnimation(ContactActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
                if (closeContact)
                    finish();
            }
        }, 3000);
    }
}
