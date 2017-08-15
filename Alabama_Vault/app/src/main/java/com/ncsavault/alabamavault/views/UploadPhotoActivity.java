package com.ncsavault.alabamavault.views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ncsavault.alabamavault.models.LoginEmailModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.reginald.editspinner.EditSpinner;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.customviews.VerticalScrollview;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.FetchingAllDataModel;
import com.ncsavault.alabamavault.models.MailChimpDataModel;
import com.ncsavault.alabamavault.models.UserDataModel;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.wheeladapters.NumericWheelAdapter;
import com.ncsavault.alabamavault.wheelwidget.OnWheelChangedListener;
import com.ncsavault.alabamavault.wheelwidget.OnWheelScrollListener;
import com.ncsavault.alabamavault.wheelwidget.WheelView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aqeeb.pathan on 15-04-2015.
 */
public class UploadPhotoActivity extends PermissionActivity implements AbstractView {

    private boolean isImageProvided = false;
    private Uri selectedImageUri = null;
    private Bitmap selectedBitmap = null;
    private Uri outputFileUri;
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 100;
    private File sdImageMainDirectory;
    private User vaultUser = null;
    private int screenWidth = 0;
    private ProgressDialog pDialog;
    private Animation animation;
    private String fName;
    private String lName;
    private String emailId;
    private boolean isBackToSplashScreen = false;
    private boolean askAgainForMustPermissions = false;
    private boolean goToSettingsScreen = false;
    private UserDataModel mVaultUserDataModel;
    private FetchingAllDataModel mFetchingAllDataModel;
    private MailChimpDataModel mMailChimpModelData;
    private EditSpinner mEditSpinner;
    private ImageView mProfileImage;
    private EditText mUserName;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailId;
    private EditSpinner mGender;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mYOB;
    private ProgressBar pBar;
    private Button mRegistertionButton,mSignUpButton;
    private WheelView yearWheel;
    private VerticalScrollview scrollView;
    private View view;
    private TextInputLayout mInputTypePassword;
    private TextInputLayout getmInputTypeConPassword;
    private TextView mBackButton,tvUploadPhoto,tvAlreadyRegistered,tvSignUpWithoutProfile;
    private boolean wheelScrolled = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //gk setContentView(R.layout.photo_upload_activity);
        setContentView(R.layout.signup_screen_layout);
//        getScreenDimensions();
        System.out.println("initilize again ");
        initialiseAllData();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public void initialiseAllData() {
        initViews();
        initData();
        initListener();
        setGenderAdapter();
        getScreenDimensions();
    }

    @Override
    public void onPermissionResult(int requestCode, boolean isGranted, Object extras) {

        switch (requestCode) {
            case PERMISSION_REQUEST_MUST:
                if (isGranted) {
                    //perform action here
                    initialiseAllData();
                } else {
                    if (!askAgainForMustPermissions) {
                        askAgainForMustPermissions = true;
                        haveAllMustPermissions(writeExternalStorage, PERMISSION_REQUEST_MUST);
                    } else if (!goToSettingsScreen) {
                        goToSettingsScreen = true;

                        showPermissionsConfirmationDialog(GlobalConstants.VAULT_PERMISSION);

                    } else {
                        showPermissionsConfirmationDialog(GlobalConstants.VAULT_PERMISSION);
                    }

                }
                break;
//            case REQUEST_MEDIA_CAMERA_PERMISSION_CALLBACK:
//            {
//                if (!haveAllMustPermissions()) {
//                    // Permissions has not been granted.
//                    ActivityCompat.requestPermissions(this, MEDIA_AND_CAMERA_PERMISSIONS_LIST,
//                            REQUEST_MEDIA_CAMERA_PERMISSION_CALLBACK);
//                } else {
//                    getUserChooserOptions();
//                }
//                break;
//            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (isBackToSplashScreen) {
                isBackToSplashScreen = false;
                if (haveAllMustPermissions(writeExternalStorage, PERMISSION_REQUEST_MUST)) {
                    initialiseAllData();
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (pDialog != null)
            pDialog.dismiss();
    }

    private void setGenderAdapter() {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.gender_selection));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditSpinner.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initViews() {

        mEditSpinner = (EditSpinner) findViewById(R.id.edit_spinner);

        mUserName = (EditText) findViewById(R.id.username);
        mFirstName = (EditText) findViewById(R.id.fname);
        mLastName = (EditText) findViewById(R.id.lname);
        mEmailId = (EditText) findViewById(R.id.Email);
        mGender = (EditSpinner) findViewById(R.id.edit_spinner);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirmPassword = (EditText) findViewById(R.id.confirm_pass);
        mRegistertionButton = (Button) findViewById(R.id.btn_signup);
        pBar = (ProgressBar) findViewById(R.id.registerprogressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
        }
        yearWheel = (WheelView) findViewById(R.id.year_wheel);
        initWheel();
        yearWheel.setBackgroundColor(getResources().getColor(R.color.app_dark_grey));

        mYOB = (EditText) findViewById(R.id.yob);
        //GK mYOB.setInputType(InputType.TYPE_NULL);
        scrollView = (VerticalScrollview) findViewById(R.id.scroll_view);
        view = (View) findViewById(R.id.llToast);
        mBackButton = (TextView) findViewById(R.id.tv_back);


        mFirstName.setOnFocusChangeListener(onFocusChangeListener);
        mLastName.setOnFocusChangeListener(onFocusChangeListener);
        mYOB.setOnFocusChangeListener(onFocusChangeListener);
        mEmailId.setOnFocusChangeListener(onFocusChangeListener);
        mUserName.setOnFocusChangeListener(onFocusChangeListener);
        mPassword.setOnFocusChangeListener(onFocusChangeListener);
        mConfirmPassword.setOnFocusChangeListener(onFocusChangeListener);

        mProfileImage = (ImageView) findViewById(R.id.imgUserProfile);
        mSignUpButton = (Button) findViewById(R.id.tv_signup_button);
        tvUploadPhoto =(TextView) findViewById(R.id.upload_phototextView);


        tvAlreadyRegistered = (TextView) findViewById(R.id.tv_already_registered);
        tvSignUpWithoutProfile = (TextView) findViewById(R.id.tv_singup_withput);
    }

    /**
     * Set pointer to end of text in edittext when user clicks Next on KeyBoard.
     */
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                ((EditText) view).setSelection(((EditText) view).getText().length());
            }
        }
    };


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
                showToastMessage("Please enter the password more than 6 character!");
            }
        }
        return false;
    }


    private boolean isValidText(String text) {
        return text != null && text.length() >= 3;
    }

    private boolean isConfirmPasswordValid(String confirmPass) {
        return confirmPass != null && (confirmPass.equals(mPassword.getText().toString()));
    }

    private String[] yearArray;

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


    // Wheel scrolled listener
    OnWheelScrollListener scrolledListener = new OnWheelScrollListener() {
        public void onScrollingStarted(WheelView wheel) {
            wheelScrolled = true;
        }

        public void onScrollingFinished(WheelView wheel) {
            wheelScrolled = false;
            mYOB.setText(yearArray[yearWheel.getCurrentItem()]);
        }

    };

    // Wheel changed listener
    private OnWheelChangedListener changedListener = new OnWheelChangedListener() {
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            if (!wheelScrolled) {
                mYOB.setText(String.valueOf(yearArray[newValue]));
            }

        }

    };


    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            Utils.getInstance().showToastMessage(this, "Email Not Entered!", view);
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                Utils.getInstance().showToastMessage(this, "Invalid Email", view);
                return false;
            } else
                return matcher.matches();
        }
    }


    @SuppressWarnings("deprecation")
    public void initData() {

        try {

            mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
            Point size = new Point();
            WindowManager w = getWindowManager();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                w.getDefaultDisplay().getSize(size);
                screenWidth = size.x;
                // Measuredheight = size.y;
            } else {
                Display d = w.getDefaultDisplay();
                // Measuredheight = d.getHeight();
                screenWidth = d.getWidth();
            }

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (screenWidth / 3), (int) (screenWidth / 3));
            lp.gravity = Gravity.CENTER_HORIZONTAL;
//            lp.setMargins(20,20,20,20);
            mProfileImage.setLayoutParams(lp);
            HashMap<String, String> stringMap = (HashMap<String, String>) getIntent().getSerializableExtra("eventObject");
            if (stringMap != null) {
                emailId = stringMap.get("email_id");
                mEmailId.setText(emailId);
                getAllRegistrationDetail();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private boolean isDeleteKey = true;

    public void initListener() {

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yearWheel.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.slidedown);
                    yearWheel.setAnimation(anim);
                    yearWheel.setVisibility(View.GONE);
                }

                if (Utils.isInternetAvailable(getApplicationContext()))
                    try {
                        //Marshmallow permissions for write external storage.
                        if (haveAllMustPermissions(writeExternalStorage, PERMISSION_REQUEST_MUST)) {
                            openImageIntent();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                else
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
        });

        mYOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYearWheel();
                mYOB.requestFocus();
                Utils.getInstance().gethideKeyboard(UploadPhotoActivity.this);
            }
        });


        yearWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYearWheelVisibility();
                Utils.getInstance().gethideKeyboard(UploadPhotoActivity.this);
            }
        });

        mYOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                openYearWheel();
                mYOB.requestFocus();
                Utils.getInstance().gethideKeyboard(UploadPhotoActivity.this);

                if (mLastName.getText().toString().length() > 0) {
                    String lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mLastName.getText().toString().trim().substring(1);
                    lastName = lastName.replace(" ", "");
                    mLastName.setText(lastName);
                }

                if (mFirstName.getText().toString().length() > 0) {
                    String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mFirstName.getText().toString().trim().substring(1);
                    firstName = firstName.replace(" ", "");
                    mFirstName.setText(firstName);
                }
                return false;
            }
        });

        mFirstName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    isDeleteKey = true;
                }
                return false;
            }
        });

        mLastName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    isDeleteKey = true;
                }
                return false;
            }
        });


        mFirstName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mFirstName.requestFocus();
                if (mLastName.getText().toString().length() > 0) {
                    String firstName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mLastName.getText().toString().trim().substring(1);
                    firstName = firstName.replace(" ", "");
                    mLastName.setText(firstName);
                }


                return false;
            }
        });


        mFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mFirstName.getText().toString().length() == 0) {
                        showToastMessage(GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY);
                        //return true ;

                    } else if (mFirstName.getText().toString().length() < 3) {
                        showToastMessage(GlobalConstants.FIRST_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                    }


                    if (mFirstName.getText().toString().length() > 0) {
                        String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() + mFirstName.getText().toString().trim().substring(1);
                        firstName = firstName.replace(" ", "");
                        mFirstName.setText(firstName);
                    }

                    //return true;
                }
                return false;
            }
        });

        mFirstName.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {


                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }

                        if (!isDeleteKey) {
                            showToastMessage(GlobalConstants.ENTER_ONLY_ALPHABETS);
                        } else {
                            isDeleteKey = false;
                        }

                        return "";

                    }
                }
        });


        mLastName.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {

                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }

                        if (!isDeleteKey) {
                            showToastMessage(GlobalConstants.ENTER_ONLY_ALPHABETS);
                        } else {
                            isDeleteKey = false;
                        }

                        return "";

                    }
                }
        });

        mLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mLastName.getText().toString().length() == 0) {
                        showToastMessage(GlobalConstants.LAST_NAME_CAN_NOT_EMPTY);
                        //return true ;

                    } else if (mLastName.getText().toString().length() < 3) {
                        showToastMessage(GlobalConstants.LAST_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                    }


                    if (mLastName.getText().toString().length() > 0) {
                        String firstName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                mLastName.getText().toString().trim().substring(1);
                        firstName = firstName.replace(" ", "");
                        mLastName.setText(firstName);
                    }

                    openYearWheel();

                    //return true;
                }
                return false;
            }
        });


        mEmailId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mEmailId.getText().toString().length() == 0) {
                        showToastMessage(GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY);
                        //return true ;

                    }
                    if (mEmailId.getText().toString().length() > 0) {
                        String firstName = mEmailId.getText().toString().replace(" ", "");
                        mEmailId.setText(firstName);
                    }

                    //return true;
                }
                return false;
            }
        });

//        mYOB.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                    // do your stuff here
//                    if (mYOB.getText().toString().length() <= 3) {
//                        showToastMessage(GlobalConstants.YOB_SHOULD_BE_MUST_FOUR_CHARACTER);
//                    }
//
//                        String yob = mYOB.getText().toString().replace(" ", "");
//                        mUserName.setText(yob);
//
//                    //return true;
//                }
//                return false;
//            }
//        });

        mUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mUserName.getText().toString().length() == 0) {
                        showToastMessage(GlobalConstants.USER_NAME_CAN_NOT_EMPTY);
                        //return true ;

                    } else if (mUserName.getText().toString().length() < 3) {
                        showToastMessage(GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                    }

                    if (mUserName.getText().toString().length() > 0) {
                        String firstName = mUserName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                mUserName.getText().toString().trim().substring(1);
                        firstName = firstName.replace(" ", "");
                        mUserName.setText(firstName);
                    }

                    //return true;
                }
                return false;
            }
        });


        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mPassword.getText().toString().contains(" ")) {
                        String password = mPassword.getText().toString().replace(" ", "");
                        mPassword.setText(password);
                    }

                    if (isValidPassword(mPassword.getText().toString())) {

                    }

                    //return true;
                }
                return false;
            }
        });

        mConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    if (mConfirmPassword.getText().toString().contains(" ")) {
                        String password = mConfirmPassword.getText().toString().replace(" ", "");
                        mConfirmPassword.setText(password);
                    }
                    if (isValidPassword(mConfirmPassword.getText().toString())) {

                    }
                    if (isConfirmPasswordValid(mConfirmPassword.getText().toString())) {

                    } else {
                        showToastMessage(GlobalConstants.PASSWORD_AND_CONFIREM_PASSWORD_DOES_NOT_MATCH);
                    }

                    //return true;
                }
                return false;
            }
        });


        mLastName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mLastName.requestFocus();
                return false;
            }
        });

        mUserName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mUserName.requestFocus();

                return false;
            }
        });

        mEmailId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                if (mLastName.getText().toString().length() > 0) {
                    String lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mLastName.getText().toString().trim().substring(1);
                    lastName = lastName.replace(" ", "");
                    mLastName.setText(lastName);
                }

                if (mFirstName.getText().toString().length() > 0) {
                    String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                            mFirstName.getText().toString().trim().substring(1);
                    firstName = firstName.replace(" ", "");
                    mFirstName.setText(firstName);
                }

                return false;
            }
        });

        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mPassword.requestFocus();
                if (mConfirmPassword.getText().toString().contains(" ")) {
                    String password = mConfirmPassword.getText().toString().replace(" ", "");
                    mConfirmPassword.setText(password);
                }
                return false;
            }
        });


        mConfirmPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mConfirmPassword.requestFocus();
                if (mPassword.getText().toString().contains(" ")) {
                    String password = mPassword.getText().toString().replace(" ", "");
                    mPassword.setText(password);
                }
                return false;
            }
        });

        mGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mGender.showDropDown();
                //  mGender.requestFocus();
                Utils.getInstance().gethideKeyboard(UploadPhotoActivity.this);
                return false;
            }
        });


//        scrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                //gk checkYearWheelVisibility();
//                return false;
//            }
//        });

        mRegistertionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setValidationOfRegistrationScreen();

            }


//            );

//        tvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + GlobalConstants.PROFILE_PIC_DIRECTORY + File.separator);
//                if (root != null) {
//                    if (root.listFiles() != null) {
//                        for (File childFile : root.listFiles()) {
//                            if (childFile != null) {
//                                if (childFile.exists())
//                                    childFile.delete();
//                            }
//
//                        }
//                        if (root.exists())
//                            root.delete();
//                    }
//                }
//                onBackPressed();
//                overridePendingTransition(R.anim.leftin, R.anim.rightout);
//
//            }
        });

//        mBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                storeAllRegistrationDetail();
//                overridePendingTransition(R.anim.leftin, R.anim.rightout);
//            }
//        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmailAndProceed();

               }
        });

        tvAlreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                storeAllRegistrationDetail();
                overridePendingTransition(R.anim.leftin, R.anim.rightout);
            }
        });
    }


    private void openYearWheel() {

        mRegistertionButton.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (Measuredheight * 0.30));
        lp.setMargins(10, 10, 10, 0);
        lp.gravity = Gravity.BOTTOM;
        yearWheel.setLayoutParams(lp);
//                yearWheel.setMinimumHeight((int) (Measuredheight*0.30));

        Animation anim = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.slideup);
        yearWheel.setAnimation(anim);
        yearWheel.setVisibility(View.VISIBLE);
        // mYOB.setShowSoftInputOnFocus(false);
        Utils.getInstance().gethideKeyboard(UploadPhotoActivity.this);
    }

    private void checkYearWheelVisibility() {
        if (yearWheel.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.slidedown);
            yearWheel.setAnimation(anim);
            yearWheel.setVisibility(View.GONE);
            mRegistertionButton.setVisibility(View.VISIBLE);
        }


        if (mLastName.getText().toString().length() > 0) {
            String lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                    mLastName.getText().toString().trim().substring(1);
            lastName = lastName.replace(" ", "");
            mLastName.setText(lastName);
        }

        if (mFirstName.getText().toString().length() > 0) {
            String firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                    mFirstName.getText().toString().trim().substring(1);
            firstName = firstName.replace(" ", "");
            mFirstName.setText(firstName);
        }

    }

    private void setValidationOfRegistrationScreen() {
        if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {
            storeAllRegistrationDetail();

            checkYearWheelVisibility();
            if (mPassword.getText().toString().contains(" ")) {
                String password = mPassword.getText().toString().replace(" ", "");
                mPassword.setText(password);
            }
            if (mConfirmPassword.getText().toString().contains(" ")) {
                String password = mConfirmPassword.getText().toString().replace(" ", "");
                mConfirmPassword.setText(password);
            }

            if (mFirstName.getText().toString().length() == 0) {
                showToastMessage(GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY);
                return;

            } else if (mFirstName.getText().toString().length() < 3) {
                showToastMessage(GlobalConstants.FIRST_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                return;
            } else {
                fName = mFirstName.getText().toString().toString().trim();
            }

            if (mLastName.getText().toString().length() == 0) {
                showToastMessage(GlobalConstants.LAST_NAME_CAN_NOT_EMPTY);
                return;

            } else if (mLastName.getText().toString().length() < 3) {
                showToastMessage(GlobalConstants.LAST_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                return;
            } else {
                lName = mLastName.getText().toString().toString().trim();
            }

//            if (mYOB.getText().toString().length() <= 3) {
//                showToastMessage(GlobalConstants.YOB_SHOULD_BE_MUST_FOUR_CHARACTER);
//            }

            if (mEmailId.getText().length() == 0) {
                showToastMessage(GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY);
                return;

            } else {
                emailId = mEmailId.getText().toString().trim();
            }

            if (isValidText(mUserName.getText().toString().replace(" ", "").trim())) {

            } else if (mUserName.getText().toString().length() == 0) {
                showToastMessage(GlobalConstants.USER_NAME_CAN_NOT_EMPTY);
                return;

            } else if (mUserName.getText().toString().length() < 3) {
                showToastMessage(GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER);
                return;
            }


            if (isValidPassword(mPassword.getText().toString())) {
                if (isConfirmPasswordValid(mConfirmPassword.getText().toString())) {
                    if (isValidPassword(mPassword.getText().toString())) ;
                    {
                        if (isValidEmail(emailId)) {
                            checkEmailIdAndProceed();

//                            fName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() + mFirstName.getText().toString().trim().substring(1);
//                            lName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() + mLastName.getText().toString().trim().substring(1);
//                            if (!AppController.getInstance().getModelFacade().getLocalModel().isOverride()) {
//                                checkEmailIdAndProceed();
//                               //gk  showConfirmLoginDialog(GlobalConstants.DO_YOU_WANT_TO_JOIN_OUR_MAILING_LIST, fName, lName, emailId);
//                            } else {
//                                AppController.getInstance().getModelFacade().getLocalModel().setOverride(false);
//                                AppController.getInstance().getModelFacade().getLocalModel().setUser(setAllVaultUserData(""));
//                                showAlert(emailId);
//                            }
                        }
                    }

                } else {
                    showToastMessage(GlobalConstants.PASSWORD_AND_CONFIREM_PASSWORD_DOES_NOT_MATCH);
                    return;
                }
            }


        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
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
                        alertDialog.dismiss();
                        Intent intent = new Intent(UploadPhotoActivity.this, VerificationEmailActivity.class);
                        intent.putExtra("registration_screen", true);
                        intent.putExtra("email_id", emailId);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                        finish();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        storeAllRegistrationDetail();
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
    }

    private void storeAllRegistrationDetail() {
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_DATA, mUserName.getText().toString()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_FIRST_NAME, mFirstName.getText().toString()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_LAST_NAME, mLastName.getText().toString()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_EMAIL, mEmailId.getText().toString()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_AGE, mYOB.getText().toString()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_GENDER, mGender.getText().toString()).commit();
        selectedBitmap = AppController.getInstance().getModelFacade().getLocalModel().getSelectImageBitmap();
        if (selectedBitmap != null) {
            String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
            pref.edit().putString(GlobalConstants.PREF_VAULT_IMAGE_URL, convertedImage).commit();
            // pref.edit().putString(GlobalConstants.PREF_VAULT_URI_IMAGE, String.valueOf(selectedImageUri)).commit();

        } else {
            pref.edit().putString(GlobalConstants.PREF_VAULT_IMAGE_URL, "").commit();
        }
        pref.edit().putString(GlobalConstants.PREF_VAULT_PASSWORD, mPassword.getText().toString()).commit();
        pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, false).commit();
    }

    private void getAllRegistrationDetail() {
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        boolean isStatus = pref.getBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, false);
        if (isStatus) {
            mUserName.setText(pref.getString(GlobalConstants.PREF_VAULT_USER_DATA, ""));
            mFirstName.setText(pref.getString(GlobalConstants.PREF_VAULT_FIRST_NAME, ""));
            mLastName.setText(pref.getString(GlobalConstants.PREF_VAULT_LAST_NAME, ""));
            mGender.setText(pref.getString(GlobalConstants.PREF_VAULT_GENDER, ""));
            mYOB.setText(pref.getString(GlobalConstants.PREF_VAULT_AGE, ""));
            String imgUrl = pref.getString(GlobalConstants.PREF_VAULT_IMAGE_URL, "");
            if (imgUrl != null && imgUrl != "") {
                Bitmap bitmapImg = StringToBitMap(imgUrl);
                try {

                    mProfileImage.setImageBitmap(bitmapImg);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (screenWidth / 3), (int) (screenWidth / 3));
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
//                        lp.setMargins(0,30,0,0);
                    mProfileImage.setLayoutParams(lp);
                    isImageProvided = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
                }
                //selectedImageUri = Uri.parse(pref.getString(GlobalConstants.PREF_VAULT_URI_IMAGE, ""));
            }
            mPassword.setText(pref.getString(GlobalConstants.PREF_VAULT_PASSWORD, ""));
        }
    }

    private int Measuredheight = 0;

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


    public void fetchInitialRecordsForAll() {

        if (mVaultUserDataModel != null) {
            mVaultUserDataModel.unRegisterView(this);
            mVaultUserDataModel = null;
        }
        pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
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

    private User setAllVaultUserData(String registerUserValue) {
        vaultUser = new User();
        try {
            if (isImageProvided) {
                selectedBitmap = AppController.getInstance().getModelFacade().getLocalModel().getSelectImageBitmap();
//                selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, UploadPhotoActivity.this);
//                selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, UploadPhotoActivity.this, sdImageMainDirectory);
                String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
                vaultUser.setImageurl(convertedImage);
            } else {
                vaultUser.setImageurl("");
            }

            vaultUser.setFname(mFirstName.getText().toString().trim());
            vaultUser.setLname(mLastName.getText().toString().trim());
            vaultUser.setEmailID(emailId.trim());
            vaultUser.setUsername(mUserName.getText().toString().trim());
            vaultUser.setPasswd(mPassword.getText().toString().trim());
            vaultUser.setGender(mGender.getText().toString().trim());
            if (mYOB.getText().toString().length() > 0) {
                vaultUser.setAge(Integer.parseInt(mYOB.getText().toString()));
            }
            vaultUser.setFlagStatus("vt");
            vaultUser.setAppID(GlobalConstants.APP_ID);
            vaultUser.setAppVersion(GlobalConstants.APP_VERSION);
            vaultUser.setDeviceType(GlobalConstants.DEVICE_TYPE);
            vaultUser.setIsRegisteredUser(registerUserValue);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return vaultUser;
    }


    public void storeDataOnServer(final String registerUserValue) {

        try {
            pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            vaultUser = setAllVaultUserData(registerUserValue);

            if (mFetchingAllDataModel != null) {
                mFetchingAllDataModel.unRegisterView(this);
                mFetchingAllDataModel = null;
            }

            if (mVaultUserDataModel != null) {
                mVaultUserDataModel.unRegisterView(this);
            }

//            if (AppController.getInstance().getModelFacade().getLocalModel().isOverride()) {
//                AppController.getInstance().getModelFacade().getLocalModel().setOverride(false);
//                overrideUserData(vaultUser);
//            } else {
            mVaultUserDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserDataModel();
            mVaultUserDataModel.registerView(this);
            mVaultUserDataModel.setProgressDialog(pDialog);
            mVaultUserDataModel.loadVaultData(vaultUser);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
                animation = AnimationUtils.loadAnimation(UploadPhotoActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public String ConvertBitmapToBase64Format(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }

    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case YOUR_SELECT_PICTURE_REQUEST_CODE: {
                    final boolean isCamera;
                    isCamera = data == null || MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                    selectedImageUri = data.getData();
                }
                break;
                case PICK_FROM_CAMERA: {
                    selectedImageUri = outputFileUri;
                }
                break;
            }

            if (selectedImageUri != null) {
                try {
                    selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, UploadPhotoActivity.this);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, UploadPhotoActivity.this, sdImageMainDirectory);
                        /*Drawable drawable = new BitmapDrawable(getResources(), selectedBitmap);
                        userProfilePic.setImageDrawable(drawable);*/
                    AppController.getInstance().getModelFacade().getLocalModel().setSelectImageBitmap(selectedBitmap);
                    mProfileImage.setImageBitmap(selectedBitmap);
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (screenWidth / 3), (int) (screenWidth / 3));
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
//                        lp.setMargins(0,30,0,0);
                    mProfileImage.setLayoutParams(lp);
                    isImageProvided = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
                }
            }
        }

        if (requestCode == 500)

        {
            isBackToSplashScreen = true;
        }
    }


    private final CharSequence[] alertListItems = {"Take from camera", "Select from gallery"};
    private final String[] MEDIA_AND_CAMERA_PERMISSIONS_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    /**
     * Method to choose an image and convert it to bitmap to set an profile picture
     * of the new user at the time of registration
     **/
    private void getUserChooserOptions() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UploadPhotoActivity.this);
        builder.setTitle("Complete action using");
        builder.setItems(alertListItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 0) {
                    // Pick from camera
                    choiceAvatarFromCamera();
                } else {
                    // Pick from gallery
                    // Filesystem.
                    final Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                    // Chooser of filesystem options.
                    final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

                    startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
                }
            }
        });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static final int PICK_FROM_CAMERA = 1;

    private void choiceAvatarFromCamera() {
        // Check for Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            String cameraPermission = Manifest.permission.CAMERA;
//            if (ContextCompat.checkSelfPermission(this, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{cameraPermission}, REQUEST_CAMERA_PERMISSION_CALLBACK);
//            } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Check for Nougat devices, as Nougat doesn't support Uri.
            // We need to provide FileProvider to access file system for image cropping
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider",
                        sdImageMainDirectory);
            } else {
                // Marshmallow doesn't require FileProviders, they can use Uri to access
                // File system for image cropping
                outputFileUri = Uri.fromFile(sdImageMainDirectory);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            try {
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            outputFileUri = Uri.fromFile(sdImageMainDirectory);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            try {
                intent.putExtra("return-data", true);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + GlobalConstants.PROFILE_PIC_DIRECTORY + File.separator);
        root.mkdirs();
        Random randomNumber = new Random();
        final String fname = GlobalConstants.PROFILE_PIC_DIRECTORY + "_" + randomNumber.nextInt(1000) + 1;
        sdImageMainDirectory = new File(root, fname);

//        outputFileUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider",
//                new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
//
//        // Camera.
//        final List<Intent> cameraIntents = new ArrayList<>();
//        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        final PackageManager packageManager = getPackageManager();
//        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for (ResolveInfo res : listCam) {
//            final String packageName = res.activityInfo.packageName;
//            final Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(packageName);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            cameraIntents.add(intent);
//        }
//
//        // Filesystem.
//        final Intent galleryIntent = new Intent();
//        galleryIntent.setType("image/*");
//        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//        // Chooser of filesystem options.
//        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
//
//        // Add the camera options.
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
//
//        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);

        getUserChooserOptions();
    }

    public void showConfirmLoginDialog(String mailChimpMessage, final String firstName, final String lastName, final String emailId) {
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
        alertDialogBuilder.setTitle("Join our Mailing List?");
        alertDialogBuilder.setView(layout);
//        alertDialogBuilder
//                .setMessage(message);
//        alertDialogBuilder.setTitle("Join our Mailing list?");
        alertDialogBuilder.setPositiveButton("No Thanks",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AppController.getInstance().getModelFacade().getLocalModel().setMailChimpRegisterUser(false);
                        storeDataOnServer("N");

                    }
                });

        alertDialogBuilder.setNegativeButton("Yes! Keep me Updated",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {

                            loadData(emailId, firstName, lastName);

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


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
//        if (!validateForm()) {
//            return;
//        }

        //showProgressDialog();

       /* // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(UploadPhotoActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });*/
        // [END create_user_with_email]
    }

    private static final String TAG = "EmailPassword";

    @Override
    public void onStart() {
        super.onStart();
    }
    // [END on_start_add_listener]

    // [START on_stop_remove_listener]
    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void update() {

        System.out.println("Uploaded photo update");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Uploaded photo update 123");
                    if (mVaultUserDataModel != null && mVaultUserDataModel.getState() ==
                            BaseModel.STATE_SUCCESS_VAULTUSER_DATA) {
                        mVaultUserDataModel.unRegisterView(UploadPhotoActivity.this);
                        loadvaultUserData();
                    } else if (mFetchingAllDataModel != null && mFetchingAllDataModel.getState() ==
                            BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {

                        showAlertDialogForSuccess(GlobalConstants.USER_SUCCESSFULLY_REGISTERED);

                    } else if (mMailChimpModelData != null && mMailChimpModelData.getState() == BaseModel.STATE_SUCCESS_MAIL_CHIMP) {
                        mMailChimpModelData.unRegisterView(UploadPhotoActivity.this);
                        if (!Utils.isInternetAvailable(UploadPhotoActivity.this) && pDialog.isShowing()) {
                            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                        } else {
                            storeDataOnServer("Y");
                        }
                        pDialog.dismiss();
                    } else if (loginEmailModel != null && loginEmailModel.getState() == BaseModel.STATE_SUCCESS) {
                        pDialog.dismiss();
                        loginEmailModel.unRegisterView(UploadPhotoActivity.this);
                        if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {
                            if (loginEmailModel.getLoginResult().toLowerCase().contains("vt_exists")) {

                                showAlertDialog("Vault",email);

                            } else if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists")) {

                                showAlertDialog("Facebook",email);

                            } else if (loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists")) {

                                showAlertDialog("Twitter",email);

                            } else if (loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists")) {

                                showAlertDialog("Google",email);

                            } else {
                                fName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() + mFirstName.getText().toString().trim().substring(1);
                                lName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() + mLastName.getText().toString().trim().substring(1);


                                showConfirmLoginDialog(GlobalConstants.DO_YOU_WANT_TO_JOIN_OUR_MAILING_LIST, fName,
                                        lName, email);
                                //gk  overrideUserData();
                            }

                        } else {
                            Utils.getInstance().showToastMessage(UploadPhotoActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private LoginEmailModel loginEmailModel;
    String email = "";
    public void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {

            Utils.getInstance().gethideKeyboard(this);

            pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            email = mEmailId.getText().toString();

            if (mVaultUserDataModel != null) {
                mVaultUserDataModel.unRegisterView(this);
                mVaultUserDataModel = null;
            }

            if (mFetchingAllDataModel != null) {
                mFetchingAllDataModel.unRegisterView(this);
                mFetchingAllDataModel = null;
            }

            if (mMailChimpModelData != null) {
                mMailChimpModelData.unRegisterView(this);
                mMailChimpModelData = null;
            }

            if (loginEmailModel != null) {
                loginEmailModel.unRegisterView(this);
            }


            loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
            loginEmailModel.registerView(this);
            loginEmailModel.setProgressDialog(pDialog);
            loginEmailModel.loadLoginData(email);


        } else {
            Utils.getInstance().showToastMessage(UploadPhotoActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
    }

    private void getFetchDataResponce() {
        try {
            mFetchingAllDataModel.unRegisterView(UploadPhotoActivity.this);
            if (Utils.isInternetAvailable(UploadPhotoActivity.this)) {
                if (mFetchingAllDataModel.getABoolean()) {
                    Profile fbProfile = Profile.getCurrentProfile();
                    SharedPreferences pref = AppController.getInstance().getApplicationContext().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
                    // boolean isJoinMailChimp = pref.getBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP, false);
                    // pref.edit().putBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP, false).commit();
                    if (fbProfile != null || userId > 0) {
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();
                        //gk if (!VideoDataService.isServiceRunning)
                        startService(new Intent(UploadPhotoActivity.this, TrendingFeaturedVideoService.class));
                            //startService(new Intent(UploadPhotoActivity.this, VideoDataService.class));
                    }
                }

            } else {
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
            pDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            stopService(new Intent(UploadPhotoActivity.this, TrendingFeaturedVideoService.class));
            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
            pDialog.dismiss();
        }
    }

    /**
     * Method used for load mail chimp data from server
     *
     * @param email
     * @param firstName
     * @param lastName
     */
    public void loadData(String email, String firstName, String lastName) {

        pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
        pDialog.setCanceledOnTouchOutside(false);

        if (mMailChimpModelData != null) {
            mMailChimpModelData.unRegisterView(this);
        }
        mMailChimpModelData = AppController.getInstance().getModelFacade().getRemoteModel().
                getMailChimpDataModel();
        mMailChimpModelData.registerView(this);
        mMailChimpModelData.setProgressDialog(pDialog);
        mMailChimpModelData.loadMailChimpData(null, email, firstName, lastName);
    }

    private void loadvaultUserData() {
        if (!Utils.isInternetAvailable(UploadPhotoActivity.this) && pDialog.isShowing()) {
            pDialog.dismiss();
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);

        } else {
            try {
                pDialog.dismiss();
                Gson gson = new Gson();
                Type classType = new TypeToken<APIResponse>() {
                }.getType();
                APIResponse response = gson.fromJson(mVaultUserDataModel.getmVaultUserResult().trim(), classType);

                if (response != null) {
                    if (mVaultUserDataModel.getmVaultUserResult().toLowerCase().contains("vt_exists")
                            || mVaultUserDataModel.getmVaultUserResult().toLowerCase().contains("false")) {

                        showAlertDialog("Vault",response.getEmailID());

                    } else if (mVaultUserDataModel.getmVaultUserResult().toLowerCase().contains("fb_exists")) {
                        showAlertDialog("Facebook",response.getEmailID());
                    } else if (mVaultUserDataModel.getmVaultUserResult().toLowerCase().contains("tw_exists")) {
                        showAlertDialog("Twitter",response.getEmailID());
                    } else if (mVaultUserDataModel.getmVaultUserResult().toLowerCase().contains("gm_exists")) {
                        showAlertDialog("Google",response.getEmailID());
                    } else {
                        if (response.getReturnStatus().toLowerCase().equals("true") || response.getReturnStatus().toLowerCase().equals("vt_exists")) {
                            SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                            pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, vaultUser.getUsername()).apply();
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, vaultUser.getEmailID()).apply();

                            fetchInitialRecordsForAll();

                            if (isImageProvided) {
                                final File root = new File(Environment.getExternalStorageDirectory() + File.separator + GlobalConstants.PROFILE_PIC_DIRECTORY + File.separator);
                                if (root != null) {
                                    if (root.listFiles() != null) {
                                        for (File childFile : root.listFiles()) {
                                            if (childFile != null) {
                                                if (childFile.exists())
                                                    childFile.delete();
                                            }

                                        }
                                        if (root.exists())
                                            root.delete();
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(UploadPhotoActivity.this, response.getReturnStatus(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                pDialog.dismiss();
            }

        }
    }

    AsyncTask<Void, Void, String> mLoginTask;

    private void overrideUserData(final User vaultUser) {
        mLoginTask = new AsyncTask<Void, Void, String>() {

            long userId = 0;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(UploadPhotoActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UploadPhotoActivity.this));
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
                        params.putString("vt_exist", "vt_exist");
                        mFirebaseAnalytics.logEvent("vt_exist", params);
                    } else {
                        try {
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response.getReturnStatus() != null) {
                                if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Vault",response.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Google",response.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Twitter",response.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Facebook",response.getEmailID());
                                }
                            } else {
                                pDialog.dismiss();
                                LoginManager.getInstance().logOut();
                                // tvFacebookLogin.setText("Login with Facebook");
                                // showToastMessage(result);
                                Utils.getInstance().showToastMessage(UploadPhotoActivity.this, "Can not connect to server. Please try again...", view);
                            }

                            mLoginTask = null;
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            pDialog.dismiss();
                            mLoginTask = null;
                            // tvFacebookLogin.setText("Login with Facebook");
                            Utils.getInstance().showToastMessage(UploadPhotoActivity.this, "We are unable to process your request", view);
                        }
                    }

                }
            }
        };
        mLoginTask.execute();
    }

    public void showAlertDialog(String loginType,final String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, "+emailId+", with "+ loginType +" login, would you like to update your profile with this new login method?");

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();

                            AppController.getInstance().getModelFacade().getLocalModel().setOverride(false);
                            AppController.getInstance().getModelFacade().getLocalModel().setUser(setAllVaultUserData(""));
                            showAlert(emailId);

                      //GK  overrideUserData(vaultUser);
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.rightin, R.anim.leftout);
                        finish();
                        // tvFacebookLogin.setText("Login with Facebook");
                        LoginManager.getInstance().logOut();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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

    private Animation leftOutAnimation;
    private Animation rightInAnimation;

    private void checkEmailIdAndProceed() {

        leftOutAnimation = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(UploadPhotoActivity.this, R.anim.rightin);

        mFirstName.setAnimation(leftOutAnimation);
        mLastName.setAnimation(leftOutAnimation);
        mYOB.setAnimation(leftOutAnimation);
        mGender.setAnimation(leftOutAnimation);
        mEmailId.setAnimation(leftOutAnimation);
        mUserName.setAnimation(leftOutAnimation);
        mPassword.setAnimation(leftOutAnimation);
        mConfirmPassword.setAnimation(leftOutAnimation);
        mRegistertionButton.setAnimation(leftOutAnimation);
        tvAlreadyRegistered.setAnimation(leftOutAnimation);

        mFirstName.setVisibility(View.GONE);
        mLastName.setVisibility(View.GONE);
        mYOB.setVisibility(View.GONE);
        mGender.setVisibility(View.GONE);
        mEmailId.setVisibility(View.GONE);
        mUserName.setVisibility(View.GONE);
        mPassword.setVisibility(View.GONE);
        mConfirmPassword.setVisibility(View.GONE);
        mRegistertionButton.setVisibility(View.GONE);
        tvAlreadyRegistered.setVisibility(View.GONE);
            //tvHeader.setText("Register");

        mProfileImage.setAnimation(rightInAnimation);
        mSignUpButton.setAnimation(rightInAnimation);
        tvUploadPhoto.setAnimation(rightInAnimation);
        tvSignUpWithoutProfile.setAnimation(rightInAnimation);

        mProfileImage.setVisibility(View.VISIBLE);
        mSignUpButton.setVisibility(View.VISIBLE);
        tvUploadPhoto.setVisibility(View.VISIBLE);
        tvSignUpWithoutProfile.setVisibility(View.VISIBLE);
        }

}
