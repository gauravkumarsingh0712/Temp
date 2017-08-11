package com.ncsavault.alabamavault.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
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
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.dto.MailChimpData;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.FetchingAllDataModel;
import com.ncsavault.alabamavault.models.LoginPasswordModel;
import com.ncsavault.alabamavault.models.MailChimpDataModel;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.wheeladapters.NumericWheelAdapter;
import com.ncsavault.alabamavault.wheelwidget.OnWheelChangedListener;
import com.ncsavault.alabamavault.wheelwidget.OnWheelScrollListener;
import com.ncsavault.alabamavault.wheelwidget.WheelView;
import com.facebook.Profile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by aqeeb.pathan on 13-04-2015.
 */
public class LoginPasswordActivity extends BaseActivity implements AbstractView {

    //Initialize UI components
    private EditText edPassword;
    private EditText edConfirmPassword;
    private EditText edUsername;
    private EditText edFirstName;
    private EditText edLastName;
    private EditText edAgeOptional;
    private CheckBox chkChangePassword;
    private RadioGroup radGroupGenderOptional;
    private RadioButton radMaleOptional, radFemaleOptional;
    private LinearLayout llPasswordBlock, llUsernameBlock, llUserDetailBlock, llUserOptionalDetailBlock, llHeaderImage, llConfirmPasswordLine, llAgeBox;
    private TextView tvHeader;
    private TextView tvPasswordNext, tvUserNameNext, tvUserRegisterNext, tvOptionalSkipNext;
    private TextView tvBack;
    private ScrollView scrollView;
    private WheelView yearWheel;

    private Animation leftOutAnimation, leftInAnimation;
    private Animation rightInAnimation, rightOutAnimation;

    private String loginStatus;
    private String email;
    private boolean isSignUpFieldsValid = true;

    private String[] yearArray;
    boolean isErrorPassword = false;
    ProgressDialog pDialog;
    private Animation animation;
    private int Measuredheight = 0;

    //variable used in mail chimp intregation
    private String mFirstName;
    private String mLastName;
    private String mEmailId;
    private long mUserId;
    private MailChimpDataModel mMailChimpModelData;
    private MailChimpData mailChimpData;
    private TextView forgotPasswordTextView;
    private FetchingAllDataModel mFetchingAllDataModel;
    private LoginPasswordModel mLoginPasswordModel;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_password_activity);

        getScreenDimensions();
        initViews();
        initData();
        //  initFirebase();
        initListener();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void initViews() {
        edPassword = (EditText) findViewById(R.id.ed_password);
        edConfirmPassword = (EditText) findViewById(R.id.ed_confirm_password);

        edPassword.setTypeface(Typeface.DEFAULT);
        edConfirmPassword.setTypeface(Typeface.DEFAULT);

        edUsername = (EditText) findViewById(R.id.ed_username);
        edFirstName = (EditText) findViewById(R.id.ed_first_name);
        edLastName = (EditText) findViewById(R.id.ed_last_name);

        edAgeOptional = (EditText) findViewById(R.id.ed_age_optional);
        edAgeOptional.setInputType(InputType.TYPE_NULL);

        llUsernameBlock = (LinearLayout) findViewById(R.id.ll_username);
        llHeaderImage = (LinearLayout) findViewById(R.id.ll_header_image);
        llUserDetailBlock = (LinearLayout) findViewById(R.id.ll_user_detail);
        llUserOptionalDetailBlock = (LinearLayout) findViewById(R.id.ll_optional_layout);
        llPasswordBlock = (LinearLayout) findViewById(R.id.ll_password_block);
        llConfirmPasswordLine = (LinearLayout) findViewById(R.id.ll_confirm_password_line);
        llAgeBox = (LinearLayout) findViewById(R.id.ll_age_box);

        tvHeader = (TextView) findViewById(R.id.tv_header_text);

        tvPasswordNext = (TextView) findViewById(R.id.tv_password_next);
        tvUserNameNext = (TextView) findViewById(R.id.tv_username_next);
        tvUserRegisterNext = (TextView) findViewById(R.id.tv_register_data_next);
        tvOptionalSkipNext = (TextView) findViewById(R.id.tv_user_optional_skip);

        tvBack = (TextView) findViewById(R.id.tv_back);

        radGroupGenderOptional = (RadioGroup) findViewById(R.id.radGroupOptional);
        radMaleOptional = (RadioButton) findViewById(R.id.radMaleOptional);
        radFemaleOptional = (RadioButton) findViewById(R.id.radFemaleOptional);

        yearWheel = (WheelView) findViewById(R.id.year_wheel);
        initWheel();
        yearWheel.setBackgroundColor(Color.parseColor("#797979"));
        chkChangePassword = (CheckBox) findViewById(R.id.chk_show_password);

        edFirstName.setTag(false);
        edLastName.setTag(false);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        forgotPasswordTextView = (TextView) findViewById(R.id.tv_forgot_password);
        forgotPasswordTextView.setVisibility(View.GONE);

    }

    @SuppressWarnings("deprecation")
    @Override
    public void initData() {
        leftOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightin);

        leftInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftin);
        rightOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightout);

        HashMap<String, String> stringMap = (HashMap<String, String>) getIntent().getSerializableExtra("eventObject");

        if (stringMap != null) {
            email = stringMap.get("email");
            loginStatus = stringMap.get("status");
        }

        edPassword.setFocusableInTouchMode(true);
        edPassword.requestFocus();

        boolean isValue = getIntent().getBooleanExtra("key_is", false);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && isValue) {
            email = bundle.getString("email");
            loginStatus = bundle.getString("status");
        }

        if ((loginStatus != null && loginStatus.toLowerCase().contains("vt_exists")) || isValue) {
            isValue = false;
            edConfirmPassword.setVisibility(View.GONE);
            llConfirmPasswordLine.setVisibility(View.GONE);
            tvHeader.setText("Login");
            edPassword.setImeOptions(EditorInfo.IME_ACTION_GO);
            forgotPasswordTextView.setVisibility(View.VISIBLE);
        }

        Point size = new Point();
        WindowManager w = getWindowManager();
        int screenWidth;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            screenWidth = size.x;
            // Measuredheight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            // Measuredheight = d.getHeight();
            screenWidth = d.getWidth();
        }

        int dimension = (int) (screenWidth * 0.45);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dimension, dimension);
        lp.setMargins(0, 20, 0, 0);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
//        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        llHeaderImage.setLayoutParams(lp);
    }

    // Wheel scrolled flag
    private boolean wheelScrolled = false;

    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }

        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            edAgeOptional.setText(yearArray[yearWheel.getCurrentItem()]);
        }
    };

    // Wheel changed listener
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                edAgeOptional.setText(String.valueOf(yearArray[newValue]));
            }
        }
    };

    private void initWheel() {
        int startingYear = 1901;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int numberOfYears = currentYear - startingYear;
        yearArray = new String[numberOfYears + 1];
        int yearCheck = startingYear;
        for (int i = 0; i <= numberOfYears; i++) {
            yearArray[i] = String.valueOf(yearCheck);
            yearCheck++;
        }

        yearWheel.setViewAdapter(new NumericWheelAdapter(this, startingYear, currentYear));
        yearWheel.setCurrentItem(numberOfYears / 2);

        yearWheel.addChangingListener(changedListener);
        yearWheel.addScrollingListener(scrolledListener);
        yearWheel.setCyclic(false);
//        yearWheel.setInterpolator(new AnticipateOvershootInterpolator());
    }

    public void getScreenDimensions() {
        Point size = new Point();
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            Measuredheight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            Measuredheight = d.getHeight();
        }
    }

    @Override
    public void initListener() {

       /* scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });*/

        tvPasswordNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.getInstance().gethideKeyboard(LoginPasswordActivity.this);
                if (llPasswordBlock.isShown()) {


                    if (loginStatus.toLowerCase().toString().contains("vt_exists")) {

                        loginVaultUser();
                    } else {
                        checkPasswordAndProceed();
                    }

                }

            }
        });

        tvUserNameNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(LoginPasswordActivity.this);
                if (llUsernameBlock.isShown()) {
                    checkUsernameAndProceed();
                }
            }
        });

        tvUserRegisterNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(LoginPasswordActivity.this);
                if (llUserDetailBlock.isShown()) {
                    checkSignUpFieldAndProceed();
                }
            }
        });

        tvOptionalSkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(LoginPasswordActivity.this);
                if (llUserOptionalDetailBlock.isShown()) {
                    checkOptionalValuesAndProceed();
                }
            }
        });

        edAgeOptional.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    tvOptionalSkipNext.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edAgeOptional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (Measuredheight * 0.30));
                lp.setMargins(0, 10, 0, 0);
                lp.gravity = Gravity.BOTTOM;
                yearWheel.setLayoutParams(lp);
//                yearWheel.setMinimumHeight((int) (Measuredheight*0.30));

                Animation anim = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.slideup);
                yearWheel.setAnimation(anim);
                yearWheel.setVisibility(View.VISIBLE);
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (yearWheel.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.slidedown);
                    yearWheel.setAnimation(anim);
                    yearWheel.setVisibility(View.GONE);
                }
                return false;
            }
        });

        radGroupGenderOptional.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tvOptionalSkipNext.setText("Next");
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        chkChangePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edPassword.setSelection(edPassword.getText().length());
                    edConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edConfirmPassword.setSelection(edConfirmPassword.getText().length());
                } else {
                    edPassword.setInputType(129);
                    edConfirmPassword.setInputType(129);
                    edPassword.setSelection(edPassword.getText().length());
                    edConfirmPassword.setSelection(edConfirmPassword.getText().length());
                }

                edPassword.setTypeface(Typeface.DEFAULT);
                edConfirmPassword.setTypeface(Typeface.DEFAULT);
            }
        });

        edPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    isErrorPassword = !isValidPassword(edPassword.getText().toString());
                    return isErrorPassword;
                } else {
                    if (loginStatus.toLowerCase().contains("vt_exists")) {
                        loginVaultUser();
                    }
                }
                return false;
            }
        });

        edConfirmPassword.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    checkPasswordAndProceed();
                    return true;
                } else {
                    return false;
                }
            }
        });

        edUsername.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Utils.getInstance().gethideKeyboard(LoginPasswordActivity.this);
                    if (llUsernameBlock.isShown()) {
                        System.out.println("edUsername");
                        checkUsernameAndProceed();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        edFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (edFirstName.getText().length() == 0) {
//                    edFirstName.setError("Cannot be empty!");
                    showToastMessage("First name cannot be empty!");
                    return true;
                } else if (edFirstName.getText().length() < 3) {
//                    edFirstName.setError("Minimum 3 characters!");
                    showToastMessage("First name should contain minimum 3 characters!");
                    return true;
                }
                return false;
            }
        });
        edLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (edLastName.getText().length() == 0) {
//                    edLastName.setError("Cannot be empty!");
                    showToastMessage("Last name cannot be empty!");
                    return true;
                } else if (edLastName.getText().length() < 3) {
//                    edLastName.setError("Minimum 3 characters!");
                    showToastMessage("Last name should contain minimum 3 characters!");
                    return true;
                }
                return false;
            }
        });

        edLastName.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Utils.getInstance().gethideKeyboard(LoginPasswordActivity.this);
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    checkSignUpFieldAndProceed();
                    return true;
                } else {
                    return false;
                }
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = AppController.getInstance().getModelFacade().getLocalModel().getRegisterEmailId();
                Intent intent = new Intent(LoginPasswordActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("key", true);
                intent.putExtra("email_id", email);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                finish();
            }
        });
    }

    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            if (pass.contains(" ")) {
                showToastMessage("Please enter valid password");
                return false;
            }
            return true;
        }
        if (pass != null) {
            if (pass.length() == 0) {
//                    edPassword.setError("Password not entered");
                showToastMessage("Password not entered");
            } else if (pass.length() < 6) {
//                    edPassword.setError("Minimum 6 characters required!");
                showToastMessage("Password should contain minimum 6 characters!");
            }
        }
        return false;
    }

    private boolean isValidText(String text) {
        return text != null && text.length() >= 3;
    }

    private boolean isConfirmPasswordValid(String confirmPass) {
        return confirmPass != null && (confirmPass.equals(edPassword.getText().toString()));
    }

    public void loginVaultUser() {
        if (Utils.isInternetAvailable(this)) {
            if (isValidPassword(edPassword.getText().toString())) {

                Utils.getInstance().gethideKeyboard(this);
                String password = "";

                pDialog = new ProgressDialog(LoginPasswordActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginPasswordActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                password = edPassword.getText().toString();

                if (mLoginPasswordModel != null) {
                    mLoginPasswordModel.unRegisterView(this);
                }
                mLoginPasswordModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginPasswordModel();
                mLoginPasswordModel.registerView(this);
                mLoginPasswordModel.setProgressDialog(pDialog);
                mLoginPasswordModel.loadEmailAndPassData(email, password);
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }


    public void fetchInitialRecordsForAll() {

        if (mLoginPasswordModel != null) {
            mLoginPasswordModel.unRegisterView(this);
            mLoginPasswordModel = null;

        }
        System.out.println("LoginPasswordActivity fetchInitialRecordsForAll ");
        pDialog = new ProgressDialog(LoginPasswordActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginPasswordActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);


        if (mFetchingAllDataModel != null) {
            mFetchingAllDataModel.unRegisterView(LoginPasswordActivity.this);
        }
        mFetchingAllDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getFetchingAllDataModel();
        mFetchingAllDataModel.registerView(LoginPasswordActivity.this);
        mFetchingAllDataModel.setProgressDialog(pDialog);
        mFetchingAllDataModel.fetchData();


    }

    public void checkPasswordAndProceed() {
        if (Utils.isInternetAvailable(this)) {
            if (isValidPassword(edPassword.getText().toString())) {
                if (isConfirmPasswordValid(edConfirmPassword.getText().toString())) {

                    leftOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftout);
                    rightInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightin);

                    llPasswordBlock.setAnimation(leftOutAnimation);
                    llHeaderImage.setAnimation(leftOutAnimation);
                    llPasswordBlock.setVisibility(View.GONE);
                    llHeaderImage.setVisibility(View.GONE);

                    llUsernameBlock.setFocusableInTouchMode(true);
                    llUsernameBlock.requestFocus();

                    tvHeader.setText("Register");
                    llHeaderImage.setAnimation(rightInAnimation);
                    llUsernameBlock.setAnimation(rightInAnimation);
                    llUsernameBlock.setVisibility(View.VISIBLE);
                    llHeaderImage.setVisibility(View.VISIBLE);
                } else {
//                edConfirmPassword.setError("Password does not match!");
                    showToastMessage("Password does not match!");
                }
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    public void checkUsernameAndProceed() {
        if (Utils.isInternetAvailable(this))
            if (isValidText(edUsername.getText().toString().replace(" ", "").trim())) {
                if (pDialog == null) {
                    String username = "";
                    pDialog = new ProgressDialog(LoginPasswordActivity.this, R.style.CustomDialogTheme);
                    pDialog.show();
                    pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginPasswordActivity.this));
                    pDialog.setCanceledOnTouchOutside(false);
                    username = edUsername.getText().toString();

                    if (mLoginPasswordModel != null) {
                        mLoginPasswordModel.unRegisterView(this);
                    }
                    mLoginPasswordModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginPasswordModel();
                    mLoginPasswordModel.registerView(this);
                    mLoginPasswordModel.setProgressDialog(pDialog);
                    mLoginPasswordModel.loadUserNameData(username);
                }

            } else

            {
//            edUsername.setError("Minimum 3 characters");
                showToastMessage("Username should contain minimum 3 characters");
            }

        else

        {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }

    }

    public void checkSignUpFieldAndProceed() {

        if (edFirstName.getText().toString().replace(" ", "").trim().length() == 0) {
//            edFirstName.setError("First name required");
            showToastMessage("First name required");
            isSignUpFieldsValid = false;
        }
        if (isSignUpFieldsValid)
            if (edFirstName.getText().toString().replace(" ", "").trim().length() < 3) {
//                edFirstName.setError("Minimum 3 characters ");
                showToastMessage("First name should contain minimum 3 characters");
                isSignUpFieldsValid = false;
            }
        if (isSignUpFieldsValid)
            if (edLastName.getText().toString().replace(" ", "").trim().length() == 0) {
                //edLastName.setError("Last name required");
                showToastMessage("Last name required");
                isSignUpFieldsValid = false;
            }
        if (isSignUpFieldsValid)
            if (edLastName.getText().toString().replace(" ", "").trim().length() < 3) {
//                edLastName.setError("Minimum 3 characters ");
                showToastMessage("Last name should contain minimum 3 characters ");
                isSignUpFieldsValid = false;
            }
        if (isSignUpFieldsValid) {
            leftOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftout);
            rightInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightin);

            llUserDetailBlock.setAnimation(leftOutAnimation);
            llHeaderImage.setAnimation(leftOutAnimation);
            llUserDetailBlock.setVisibility(View.GONE);
            llHeaderImage.setVisibility(View.GONE);

            llHeaderImage.setAnimation(rightInAnimation);
            llUserOptionalDetailBlock.setAnimation(rightInAnimation);
            tvHeader.setText("Register");
            llUserOptionalDetailBlock.setVisibility(View.VISIBLE);
            llHeaderImage.setVisibility(View.VISIBLE);
            radGroupGenderOptional.setVisibility(View.GONE);
            if (edAgeOptional.getText().length() > 0)
                tvOptionalSkipNext.setText("Next");

            AppController.getInstance().getModelFacade().getLocalModel().setFirstName(edFirstName.getText().toString());
            AppController.getInstance().getModelFacade().getLocalModel().setLastName(edLastName.getText().toString());
            AppController.getInstance().getModelFacade().getLocalModel().storeFnameAndLname(edFirstName.getText().toString(), edLastName.getText().toString());
        }
        isSignUpFieldsValid = true;
    }

    public void checkOptionalValuesAndProceed() {
        boolean isValidated = false;
        if (llAgeBox.isShown()) {
            if (edAgeOptional.getText().toString().length() > 0 && tvOptionalSkipNext.getText().toString().toLowerCase().equals("next")) {
                isValidated = true;
            } else if (tvOptionalSkipNext.getText().toString().toLowerCase().equals("skip") && edAgeOptional.getText().toString().length() == 0) {
                isValidated = true;
            } else {
                if (edAgeOptional.getText().toString().length() == 0)
                    showToastMessage("Please provide proper age");
            }
            if (isValidated) {
                if (yearWheel.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.slidedown);
                    yearWheel.setAnimation(anim);
                    yearWheel.setVisibility(View.GONE);
                }
                leftOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftout);
                rightInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightin);

                llUserOptionalDetailBlock.setAnimation(leftOutAnimation);
                llHeaderImage.setAnimation(leftOutAnimation);
                llUserOptionalDetailBlock.setVisibility(View.GONE);
                llHeaderImage.setVisibility(View.GONE);


                radGroupGenderOptional.setVisibility(View.VISIBLE);
                llAgeBox.setVisibility(View.GONE);
                llHeaderImage.setAnimation(rightInAnimation);
                llUserOptionalDetailBlock.setAnimation(rightInAnimation);
                tvHeader.setText("Register");
                llUserOptionalDetailBlock.setVisibility(View.VISIBLE);
                llHeaderImage.setVisibility(View.VISIBLE);
                tvOptionalSkipNext.setText("Skip");

                return;
            }

        } else if (radGroupGenderOptional.isShown()) {
            if ((radFemaleOptional.isChecked() || radMaleOptional.isChecked()) && tvOptionalSkipNext.getText().toString().toLowerCase().equals("next")) {
                isValidated = true;
            } else if (tvOptionalSkipNext.getText().toString().toLowerCase().equals("skip") && (!radFemaleOptional.isChecked() && !radMaleOptional.isChecked())) {
                isValidated = true;
            }
            if (isValidated) {
                User usr = new User();
                usr.setFname(edFirstName.getText().toString());
                usr.setLname(edLastName.getText().toString());
                usr.setUsername(edUsername.getText().toString());
                usr.setEmailID(email);
                usr.setPasswd(edPassword.getText().toString());
                AppController.getInstance().getModelFacade().getLocalModel().setFirstName(usr.getFname());
                AppController.getInstance().getModelFacade().getLocalModel().setLastName(usr.getLname());
                AppController.getInstance().getModelFacade().getLocalModel().storeFnameAndLname(usr.getFname(), usr.getLname());
                if (edAgeOptional.getText().toString().length() > 0)
                    usr.setAge(Integer.parseInt(edAgeOptional.getText().toString()));
                usr.setAppID(1);
                if (radMaleOptional.isChecked())
                    usr.setGender(radMaleOptional.getText().toString());
                else if (radFemaleOptional.isChecked())
                    usr.setGender(radFemaleOptional.getText().toString());
                else
                    usr.setGender("");
                usr.setFlagStatus("vt");

                //set ImageUrl on the next screen when user selects image
                AppController.getInstance().handleEvent(AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN, usr);

                overridePendingTransition(R.anim.rightin, R.anim.leftout);
            } else {
                if (!radGroupGenderOptional.isSelected())
                    showToastMessage("Please provide proper gender");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Utils.hideSoftKeyboard(LoginPasswordActivity.this);
        if (llUserOptionalDetailBlock.isShown()) {
            if (radGroupGenderOptional.isShown()) {
                leftInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftin);
                rightOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightout);

                llUserOptionalDetailBlock.setAnimation(rightOutAnimation);
                llHeaderImage.setAnimation(rightOutAnimation);
                llUserOptionalDetailBlock.setVisibility(View.GONE);
                llHeaderImage.setVisibility(View.GONE);
                radGroupGenderOptional.clearCheck();
                radGroupGenderOptional.setVisibility(View.GONE);

                llAgeBox.setVisibility(View.VISIBLE);
                llUserOptionalDetailBlock.setAnimation(leftInAnimation);
                llHeaderImage.setAnimation(leftInAnimation);
                tvHeader.setText("Register");
                llUserOptionalDetailBlock.setVisibility(View.VISIBLE);
                llHeaderImage.setVisibility(View.VISIBLE);
                if (edAgeOptional.getText().length() > 0)
                    tvOptionalSkipNext.setText("Next");
                else
                    tvOptionalSkipNext.setText("Skip");

            } else if (llAgeBox.isShown()) {
                if (yearWheel.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.slidedown);
                    yearWheel.setAnimation(anim);
                    yearWheel.setVisibility(View.GONE);
                }
                leftInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftin);
                rightOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightout);

                llUserOptionalDetailBlock.setAnimation(rightOutAnimation);
                llHeaderImage.setAnimation(rightOutAnimation);
                llUserOptionalDetailBlock.setVisibility(View.GONE);
                llHeaderImage.setVisibility(View.GONE);
                radGroupGenderOptional.clearCheck();

                edAgeOptional.setText("");
                tvOptionalSkipNext.setText("Skip");
                llUserDetailBlock.setAnimation(leftInAnimation);
                llHeaderImage.setAnimation(leftInAnimation);
                tvHeader.setText("Register");
                llUserDetailBlock.setVisibility(View.VISIBLE);
                llHeaderImage.setVisibility(View.VISIBLE);
            }
            return;
        } else if (llUserDetailBlock.isShown()) {
            leftInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftin);
            rightOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightout);

            llUserDetailBlock.setAnimation(rightOutAnimation);
            llHeaderImage.setAnimation(rightOutAnimation);
            llUserDetailBlock.setVisibility(View.GONE);
            llHeaderImage.setVisibility(View.GONE);

            llUsernameBlock.setAnimation(leftInAnimation);
            llHeaderImage.setAnimation(leftInAnimation);
            tvHeader.setText("Register");
            llUsernameBlock.setVisibility(View.VISIBLE);
            llHeaderImage.setVisibility(View.VISIBLE);
            return;
        } else if (llUsernameBlock.isShown()) {
            leftInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftin);
            rightOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightout);

            llUsernameBlock.setAnimation(rightOutAnimation);
            llHeaderImage.setAnimation(rightOutAnimation);
            llUsernameBlock.setVisibility(View.GONE);
            llHeaderImage.setVisibility(View.GONE);

            llPasswordBlock.setAnimation(leftInAnimation);
            tvHeader.setText("Register");
            llHeaderImage.setAnimation(leftInAnimation);
            llPasswordBlock.setVisibility(View.VISIBLE);
            llHeaderImage.setVisibility(View.VISIBLE);
            return;
        }
        super.onBackPressed();

        overridePendingTransition(R.anim.leftin, R.anim.rightout);
    }

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
                animation = AnimationUtils.loadAnimation(LoginPasswordActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }


    public void showConfirmLoginDialog(final String mailChimpMessage, final String firstName, final String lastName,
                                       final String emailId, final long userId) {

        AlertDialog alertDialog = null;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        TextView message = new TextView(this);
        //message.setGravity(Gravity.CENTER);
        message.setPadding(75, 50, 5, 10);
        message.setTextSize(17);
        message.setText(mailChimpMessage);
        message.setTextColor(getResources().getColor(R.color.gray));
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(message);
        alertDialogBuilder.setTitle("Join our Mailing list?");
        alertDialogBuilder.setView(layout);
//        alertDialogBuilder
//                .setMessage(message);
//        alertDialogBuilder.setTitle("Join our Mailing list?");

        alertDialogBuilder.setPositiveButton("No Thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Utils.isInternetAvailable(LoginPasswordActivity.this)) {
                            loadData(userId, "N", emailId, firstName, lastName);
                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Yes! Keep me Updated",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (Utils.isInternetAvailable(LoginPasswordActivity.this)) {

                            loadData(userId, "Y", emailId, firstName, lastName);
                        } else {
                            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        }

                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(Color.GRAY);
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }


    @Override
    public void onStart() {
        super.onStart();
        //  mAuth.addAuthStateListener(mAuthListener);
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();
      /*  if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }*/
    }

    @Override
    public void update() {

        System.out.println("Login password screen Update");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mLoginPasswordModel != null && mLoginPasswordModel.getState() ==
                        BaseModel.STATE_SUCCESS_EMAIL_PASSWORD_DATA) {

                    mLoginPasswordModel.unRegisterView(LoginPasswordActivity.this);
                    loadEmailAndPasswordData();
                } else if (mLoginPasswordModel != null && mLoginPasswordModel.getState() ==
                        BaseModel.STATE_SUCCESS_USERNAME_DATA) {

                    mLoginPasswordModel.unRegisterView(LoginPasswordActivity.this);
                    loadUserName();
                } else if (mMailChimpModelData != null && mMailChimpModelData.getState() == BaseModel.STATE_SUCCESS_MAIL_CHIMP) {

                    mMailChimpModelData.unRegisterView(LoginPasswordActivity.this);
                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_MAIN_SCREEN);
                    overridePendingTransition(R.anim.slideup, R.anim.nochange);
                    finish();
                    if (!VideoDataService.isServiceRunning)
                        startService(new Intent(LoginPasswordActivity.this, VideoDataService.class));
                    pDialog.dismiss();
                } else if (mFetchingAllDataModel != null && mFetchingAllDataModel.getState() ==
                        BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                    try {
                        mFetchingAllDataModel.getProgressDialog().dismiss();
                        mFetchingAllDataModel.unRegisterView(LoginPasswordActivity.this);
                        if (Utils.isInternetAvailable(LoginPasswordActivity.this)) {
                            if (mFetchingAllDataModel.getABoolean()) {
                                Profile fbProfile = Profile.getCurrentProfile();
                                SharedPreferences pref = AppController.getInstance().getApplicationContext().
                                        getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, MODE_PRIVATE);
                                long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                                if (fbProfile != null || userId > 0) {

                                       if (mFetchingAllDataModel.getResponeUserData().getIsRegisteredUser().equals("N") && !AppController.getInstance().getModelFacade()
                                            .getLocalModel().getMailChimpRegisterUser()) {
                                        AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(true);
                                        mFirstName = mFetchingAllDataModel.getResponeUserData().getFname()
                                                .toString().substring(0, 1).toUpperCase() + mFetchingAllDataModel.getResponeUserData().getFname().toString().substring(1);//AppController.getInstance().getFirstName().toString();
                                        mLastName = mFetchingAllDataModel.getResponeUserData().getLname()
                                                .toString().substring(0, 1).toUpperCase() + mFetchingAllDataModel.getResponeUserData().getLname().toString().substring(1);
                                        mEmailId = mFetchingAllDataModel.getResponeUserData().getEmailID();
                                        mUserId = mFetchingAllDataModel.getResponeUserData().getUserID();

                                        showConfirmLoginDialog(GlobalConstants.DO_YOU_WANT_TO_JOIN_OUR_MAILING_LIST,
                                                mFirstName, mLastName, mEmailId, mUserId);

                                    } else {
                                        System.out.println("LoginPasswordActivity EVENT_ID_MAIN_SCREEN ");
                                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_MAIN_SCREEN);
                                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                                        finish();
                                        if (!VideoDataService.isServiceRunning)
                                            startService(new Intent(LoginPasswordActivity.this, VideoDataService.class));
                                    }
                                }
                            } else {
                                showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);

                            }
                        } else {
                            showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        stopService(new Intent(LoginPasswordActivity.this, VideoDataService.class));
                        VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
                        mFetchingAllDataModel.getProgressDialog().dismiss();
                    }
                }
            }
        });


    }

    /**
     * Method used for load mail chimp data from server
     *
     * @param userId
     * @param value
     */
    public void loadData(long userId, String value, String email, String firstName, String lastName) {
        mailChimpData = new MailChimpData();
        mailChimpData.setIsRegisteredUser(value);
        mailChimpData.setUserID(userId);

        pDialog = new ProgressDialog(LoginPasswordActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginPasswordActivity.this));
        pDialog.setCanceledOnTouchOutside(false);

        if (mMailChimpModelData != null) {
            mMailChimpModelData.unRegisterView(this);
        }
        mMailChimpModelData = AppController.getInstance().getModelFacade().getRemoteModel().
                getMailChimpDataModel();
        mMailChimpModelData.registerView(LoginPasswordActivity.this);
        mMailChimpModelData.setProgressDialog(pDialog);
        mMailChimpModelData.loadMailChimpData(mailChimpData, email, firstName, lastName);
    }

    public void loadEmailAndPasswordData() {
        if (!Utils.isInternetAvailable(LoginPasswordActivity.this) && mLoginPasswordModel.getProgressDialog().isShowing()) {
            if (mLoginPasswordModel.getmEmailPasswordResult() == null || mLoginPasswordModel != null && mLoginPasswordModel.getmEmailPasswordResult().equals("vt_exists")) {
                mLoginPasswordModel.getProgressDialog().dismiss();
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            } else {
                mLoginPasswordModel.getProgressDialog().dismiss();
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }

        } else {
            try {
                mLoginPasswordModel.getProgressDialog().dismiss();
                Gson gson = new Gson();
                Type classType = new TypeToken<APIResponse>() {
                }.getType();
                if (mLoginPasswordModel.getmEmailPasswordResult() != null) {
                    APIResponse response = gson.fromJson(mLoginPasswordModel.getmEmailPasswordResult().trim(), classType);
                    if (response != null) {
                        if (response.getReturnStatus().toLowerCase().equals("true")) {
                            SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, MODE_PRIVATE);
                            pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, email).apply();
                            pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();

                            // createAccount(email, edPassword.getText().toString());
                            fetchInitialRecordsForAll();
                            params.putString("vt_exist", "vt_exist");
                            mFirebaseAnalytics.logEvent("vt_exist", params);
                            isErrorPassword = false;
                        } else {
//                                        edPassword.setError("Password is incorrect!");
                            showToastMessage("Password is incorrect!");
                            isErrorPassword = true;
                        }
                    }
                } else {

                    Thread.currentThread();
                    Thread.sleep(2000);
                    if (!Utils.isInternetAvailable(LoginPasswordActivity.this)) {
                        showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        mLoginPasswordModel.getProgressDialog().dismiss();

                    } else {
                        showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                        mLoginPasswordModel.getProgressDialog().dismiss();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("On Post :Exception Occur");
                pDialog.dismiss();
            }
        }
    }

    private void loadUserName() {

        mLoginPasswordModel.getProgressDialog().dismiss();
        pDialog = null;
        if (Utils.isInternetAvailable(LoginPasswordActivity.this)) {
            if (mLoginPasswordModel.getmUserNameResult() != null) {
                if (mLoginPasswordModel.getmUserNameResult().toLowerCase().contains("true")) {
                    leftOutAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.leftout);
                    rightInAnimation = AnimationUtils.loadAnimation(LoginPasswordActivity.this, R.anim.rightin);

                    llUsernameBlock.setAnimation(leftOutAnimation);
                    llHeaderImage.setAnimation(leftOutAnimation);
                    llUsernameBlock.setVisibility(View.GONE);
                    llHeaderImage.setVisibility(View.GONE);

                    llHeaderImage.setAnimation(rightInAnimation);
                    llUserDetailBlock.setAnimation(rightInAnimation);
                    edFirstName.setFocusableInTouchMode(true);
                    edFirstName.requestFocus();
                    tvHeader.setText("Register");
                    llUserDetailBlock.setVisibility(View.VISIBLE);
                    llHeaderImage.setVisibility(View.VISIBLE);
                } else if (mLoginPasswordModel.getmUserNameResult().toLowerCase().contains("false")) {
//                                edUsername.setError("Username already exists!");
                    showToastMessage("Username already exists!");
                } else {
                    Toast.makeText(LoginPasswordActivity.this, mLoginPasswordModel.getmUserNameResult(), Toast.LENGTH_SHORT).show();
                }
            } else {
                showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }

    }


}
