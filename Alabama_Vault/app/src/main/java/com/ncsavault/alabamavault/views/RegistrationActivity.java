package com.ncsavault.alabamavault.views;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.twitter.sdk.android.Twitter;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.customviews.VerticalScrollview;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.FBLoginModel;
import com.ncsavault.alabamavault.models.FetchingAllDataModel;
import com.ncsavault.alabamavault.models.LoginEmailModel;
import com.ncsavault.alabamavault.models.MailChimpDataModel;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.wheeladapters.NumericWheelAdapter;
import com.ncsavault.alabamavault.wheelwidget.OnWheelChangedListener;
import com.ncsavault.alabamavault.wheelwidget.OnWheelScrollListener;
import com.ncsavault.alabamavault.wheelwidget.WheelView;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.reginald.editspinner.EditSpinner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gauravkumar.singh on 2/1/2017.
 */

public class RegistrationActivity extends PermissionActivity implements AbstractView {

    private EditSpinner mEditSpinner;
    private ImageView mProfileImage;
    private EditText mUserName;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailId;
    private EditText mEmailIdFB;
    private EditSpinner mGender;
    private EditText mYOB;
    private ProgressBar pBar;
    private DisplayImageOptions options;
    private Button mRegistertionButton,mSignUpButton;
    private WheelView yearWheel;
    private VerticalScrollview scrollView;
    private View view;
    private View viewLayout;
    private TextView mBackButton,tvUploadPhoto,tvAlreadyRegistered,tvSignUpWithoutProfile;;
    private boolean isBackToSplashScreen = false;
    private boolean askAgainForMustPermissions = false;
    private boolean goToSettingsScreen = false;
    private boolean twitter;
    private Uri selectedImageUri = null;
    private Uri outputFileUri;
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 100;
    private File sdImageMainDirectory;
    private User socialUser;
    private AsyncTask<Void, Void, String> mLoginTask;
    private ProgressDialog pDialog;
    private FBLoginModel fbLoginModel;
    private FetchingAllDataModel fetchingAllDataModel;
    private int screenWidth;
    private boolean wheelScrolled = false;
    private String[] yearArray;
    private AlertDialog alertDialog;
    private int Measuredheight = 0;
    private MailChimpDataModel mMailChimpModelData;
    private boolean isBlankEmail = false;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params = new Bundle();
    private boolean isImageProvided = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_screen_layout);
        initialiseAllData();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void initialiseAllData() {
        initViews();
        initData();
        initListener();
        setGenderAdapter();
        getDataOfSocialLogin();
        getScreenDimensions();
    }


    private void getDataOfSocialLogin() {

        twitter = AppController.getInstance().getModelFacade().getLocalModel().isTwitterLogin();
        socialUser = AppController.getInstance().getModelFacade().getLocalModel().getUser();

        if (socialUser.getFname() != null && socialUser.getFname() != "") {
            firstName = socialUser.getFname().trim().substring(0, 1).toUpperCase() + socialUser.getFname().trim().substring(1);
        }
        if (socialUser.getFname() != null && socialUser.getLname() != "") {
            lastName = socialUser.getLname().trim().substring(0, 1).toUpperCase() + socialUser.getLname().trim().substring(1);
        }
        String userName = socialUser.getUsername().trim().substring(0, 1).toUpperCase() + socialUser.getUsername().trim().substring(1);

        mFirstName.setText(firstName);
        mLastName.setText(lastName);
        mUserName.setText(userName);
        if (socialUser.getEmailID() != "") {

            mEmailIdFB.setText(socialUser.getEmailID().trim());
            mEmailIdFB.setVisibility(View.VISIBLE);
            mEmailId.setVisibility(View.GONE);
        } else {
            isBlankEmail = true;
            mEmailIdFB.setVisibility(View.GONE);
            mEmailId.setVisibility(View.VISIBLE);
        }

        mYOB.setText(mYOB.getText().toString().trim());
        mGender.setText(mGender.getText().toString().trim());
        String profileImage = socialUser.getImageurl();

        if (profileImage != null) {
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(profileImage, mProfileImage, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    pBar.setVisibility(View.VISIBLE);
                    try {
                                    /*InputStream istr = getAssets().open("placeholder.jpg");
                                    //set drawable from stream
                                    imgUserProfile.setImageDrawable(Drawable.createFromStream(istr, null));*/

                        mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    pBar.setVisibility(View.GONE);
                    try {
                                    /*InputStream istr = getAssets().open("placeholder.jpg");
                                    //set drawable from stream
                                    imgUserProfile.setImageDrawable(Drawable.createFromStream(istr, null));*/
                        mProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    pBar.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setGenderAdapter() {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.gender_selection));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditSpinner.setAdapter(adapter);
    }

    private void facebookLogin(User socialUserData, String registerUser) {

        String email = "";
        if(mEmailId.getVisibility() == View.VISIBLE)
        {
            email = mEmailId.getText().toString();
        }else if(mEmailIdFB.getVisibility() == View.VISIBLE)
        {
            email = mEmailIdFB.getText().toString();
        }
        if (loginEmailModel != null) {
            loginEmailModel.unRegisterView(this);
        }
        socialUserData.setEmailID(email);
        socialUserData.setIsRegisteredUser(registerUser);
        if (isImageProvided) {
            try {
                selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, RegistrationActivity.this);
                selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, RegistrationActivity.this, sdImageMainDirectory);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
            socialUserData.setImageurl(convertedImage);
        }

        pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        if (fbLoginModel != null) {
            fbLoginModel.unRegisterView(RegistrationActivity.this);
        }
        if (fetchingAllDataModel != null) {
            fetchingAllDataModel.unRegisterView(RegistrationActivity.this);
            fetchingAllDataModel = null;
        }
        socialUser = socialUserData;

        if (AppController.getInstance().getModelFacade().getLocalModel().isOverride()) {
            AppController.getInstance().getModelFacade().getLocalModel().setOverride(false);
            overrideUserData();
        } else {
            fbLoginModel = AppController.getInstance().getModelFacade().getRemoteModel().getFbLoginModel();
            fbLoginModel.registerView(RegistrationActivity.this);
            fbLoginModel.setProgressDialog(pDialog);
            fbLoginModel.fetchData(socialUser);
        }
    }

    public String ConvertBitmapToBase64Format(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }

    String mLogin;
    private void overrideUserData() {
        mLoginTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {
                    result = AppController.getInstance().getServiceManager().getVaultService().updateUserData(socialUser);

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
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                        pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();
                        // isfetchData = true;
                        fetchInitialRecordsForAll();

                        if (AppController.getInstance().getModelFacade().getLocalModel().isGoogleLogin()) {
                            mLogin = "gm_exist";
                        } else if (AppController.getInstance().getModelFacade().getLocalModel().isFacebookLogin()) {
                            mLogin = "fb_exist";
                        } else if (AppController.getInstance().getModelFacade().getLocalModel().isTwitterLogin()) {
                            mLogin = "tw_exist";
                        }
                        params.putString(mLogin, mLogin);
                        mFirebaseAnalytics.logEvent(mLogin, params);
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
                                Utils.getInstance().showToastMessage(RegistrationActivity.this, "Can not connect to server. Please try again...", view);
                            }

                            mLoginTask = null;
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            pDialog.dismiss();
                            mLoginTask = null;
                            // tvFacebookLogin.setText("Login with Facebook");
                            Utils.getInstance().showToastMessage(RegistrationActivity.this, "We are unable to process your request", view);
                        }
                    }

                } else {
                    pDialog.dismiss();
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, "We are unable to process your request", view);
                }
            }
        };
        mLoginTask.execute();
    }

    /**
     * Methos used for save fb data at server
     */
    public void getFBData() {
        System.out.println("Result of post user data : " + fbLoginModel.getResultData());
        if (Utils.isInternetAvailable(RegistrationActivity.this)) {
            if (fbLoginModel.getResultData() != null) {
                if (fbLoginModel.getResultData().contains("true") /*|| fbLoginModel.getResultData().contains("success")*/) {
                    pDialog.dismiss();
                    Gson gson = new Gson();
                    Type classType = new TypeToken<APIResponse>() {
                    }.getType();
                    APIResponse response = gson.fromJson(fbLoginModel.getResultData().trim(), classType);
                    SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                    pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                    pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();

                    fetchInitialRecordsForAll();

                    if (AppController.getInstance().getModelFacade().getLocalModel().isGoogleLogin()) {
                        mLogin = "gm_exist";
                    } else if (AppController.getInstance().getModelFacade().getLocalModel().isFacebookLogin()) {
                        mLogin = "fb_exist";
                    } else if (AppController.getInstance().getModelFacade().getLocalModel().isTwitterLogin()) {
                        mLogin = "tw_exist";
                    }

                    params.putString(mLogin, mLogin);
                    mFirebaseAnalytics.logEvent(mLogin, params);

                } else {
                    try {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(fbLoginModel.getResultData().trim(), classType);
                        if (response.getReturnStatus() != null) {
                            if (response.getReturnStatus().toLowerCase().contains("vt_exists")
                                    || response.getReturnStatus().toLowerCase().contains("false")) {
                                pDialog.dismiss();
                                showAlertDialog("Vault",response.getEmailID());
                            } else if (response.getReturnStatus().toLowerCase().contains("fb_exists") /*|| response.getReturnStatus().toLowerCase().contains("" +
                                                    "")*/) {
                                pDialog.dismiss();
                                showAlertDialog("Facebook",response.getEmailID());


                            } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                pDialog.dismiss();
                                showAlertDialog("Twitter",response.getEmailID());
                            } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                pDialog.dismiss();
                                showAlertDialog("Google",response.getEmailID());
                            }
                        } else {
                            pDialog.dismiss();
                            LoginManager.getInstance().logOut();
                            // tvFacebookLogin.setText("Login with Facebook");
                            //showToastMessage(result);
                            Utils.getInstance().showToastMessage(RegistrationActivity.this, "Can not connect to server. Please try again...", view);
                        }

                    } catch (Exception e) {
                        LoginManager.getInstance().logOut();
                        e.printStackTrace();
                        pDialog.dismiss();
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, "We are unable to process your request", view);
                    }
                }
            } else {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
            }
        } else {
            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
        pDialog.dismiss();
    }


    public void fetchInitialRecordsForAll() {

        if (Utils.isInternetAvailable(this)) {
            pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //  tvFacebookLogin.setText("Login with Facebook");
                    LoginManager.getInstance().logOut();
                }
            });
            if (fbLoginModel != null) {
                fbLoginModel.unRegisterView(RegistrationActivity.this);
                fbLoginModel = null;
            }

            if (fetchingAllDataModel != null) {
                fetchingAllDataModel.unRegisterView(this);
            }
            fetchingAllDataModel = AppController.getInstance().getModelFacade().getRemoteModel().getFetchingAllDataModel();
            fetchingAllDataModel.registerView(this);
            fetchingAllDataModel.setProgressDialog(pDialog);
            fetchingAllDataModel.fetchData();

        } else {
            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
    }


    public void initViews() {

        mEditSpinner = (EditSpinner) findViewById(R.id.edit_spinner);
        mProfileImage = (ImageView) findViewById(R.id.imgUserProfile);
        mUserName = (EditText) findViewById(R.id.username);
        mFirstName = (EditText) findViewById(R.id.fname);
        mLastName = (EditText) findViewById(R.id.lname);
        mEmailId = (EditText) findViewById(R.id.Email);
        mEmailId.setVisibility(View.GONE);
        mEmailIdFB = (EditText) findViewById(R.id.Email_FB);
        mGender = (EditSpinner) findViewById(R.id.edit_spinner);
//        mPassword = (EditText) findViewById(R.id.password);
//        mConfirmPassword = (EditText) findViewById(R.id.confirm_pass);
//        viewLayout = (View) findViewById(R.id.view_layout);
//        viewLayout.setVisibility(View.VISIBLE);
        mRegistertionButton = (Button) findViewById(R.id.btn_signup);
        pBar = (ProgressBar) findViewById(R.id.registerprogressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
        }


        yearWheel = (WheelView) findViewById(R.id.year_wheel);
        initWheel();
        yearWheel.setBackgroundColor(Color.parseColor("#797979"));
        mYOB = (EditText) findViewById(R.id.yob);
        //gk mYOB.setInputType(InputType.TYPE_NULL);
       // scrollView = (VerticalScrollview) findViewById(R.id.scroll_view);
        view = (View) findViewById(R.id.llToast);
       // mBackButton = (TextView) findViewById(R.id.tv_back);

        mFirstName.setOnFocusChangeListener(onFocusChangeListener);
        mLastName.setOnFocusChangeListener(onFocusChangeListener);
        mYOB.setOnFocusChangeListener(onFocusChangeListener);
        mEmailId.setOnFocusChangeListener(onFocusChangeListener);
        mUserName.setOnFocusChangeListener(onFocusChangeListener);

        mUserName.setImeOptions(EditorInfo.IME_ACTION_DONE);

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

    public void initData() {

        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3) // default
                .denyCacheImageMultipleSizesInMemory()
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .build();
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

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

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth / 3, screenWidth / 3);
            mProfileImage.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkYearWheelVisibility() {
        if (yearWheel.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.slidedown);
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

    private void openYearWheel() {
        mRegistertionButton.setVisibility(View.GONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int) (Measuredheight * 0.30));
        lp.setMargins(10, 10, 10, 0);
        lp.gravity = Gravity.BOTTOM;
        yearWheel.setLayoutParams(lp);
//      yearWheel.setMinimumHeight((int) (Measuredheight*0.30));

        Animation anim = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.slideup);
        yearWheel.setAnimation(anim);
        yearWheel.setVisibility(View.VISIBLE);
        // mYOB.setShowSoftInputOnFocus(false);
        Utils.getInstance().gethideKeyboard(RegistrationActivity.this);
    }

    private boolean isDeleteKey = true;
    public void initListener() {

        mRegistertionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                valadationOfSocialLogin();

            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBlankEmail) {
                    isBlankEmail = false;
                    checkEmailAndProceed();

                } else {

                    showConfirmLoginDialog(GlobalConstants.DO_YOU_WANT_TO_JOIN_OUR_MAILING_LIST, firstName,
                            lastName, socialUser.getEmailID());
                    //gk  overrideUserData();
                }
            }
        });

        tvAlreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitter = false;
                finish();
                LoginManager.getInstance().logOut();
                Twitter.logOut();
                Auth.GoogleSignInApi.signOut(LoginEmailActivity.mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                //updateUI(null);
                            }
                        });
                overridePendingTransition(R.anim.leftin, R.anim.rightout);

            }
        });

        mYOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYearWheel();
                mYOB.requestFocus();
            }
        });


        yearWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkYearWheelVisibility();
            }
        });

        mYOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                openYearWheel();
                mYOB.requestFocus();
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
//                mFirstName.setFilters(new InputFilter[]{
//                        new InputFilter() {
//                            public CharSequence filter(CharSequence src, int start,
//                                                       int end, Spanned dst, int dstart, int dend) {
//                                if (src.equals("")) { // for backspace
//                                    return src;
//                                }
//                                if (src.toString().matches("[a-zA-Z ]+")) {
//                                    return src;
//                                }
//                                if (!isDeleteKey) {
//                                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.ENTER_ONLY_ALPHABETS, view);
//                                } else {
//                                    isDeleteKey = false;
//                                }
//
//                                return "";
//                            }
//                        }
//                });

                return false;
            }
        });


        mFirstName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mFirstName.getText().toString().length() == 0) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY, view);
                        //return true ;

                    } else if (mFirstName.getText().toString().length() < 3) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this,
                                GlobalConstants.FIRST_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
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


        mLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    // do your stuff here
                    if (mLastName.getText().toString().length() == 0) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.LAST_NAME_CAN_NOT_EMPTY, view);
                        //return true ;

                    } else if (mLastName.getText().toString().length() < 3) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.LAST_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
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
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY, view);
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
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_CAN_NOT_EMPTY, view);
                        //return true ;

                    } else if (mUserName.getText().toString().length() < 3) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
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


        mLastName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mLastName.requestFocus();

//                mLastName.setFilters(new InputFilter[]{
//                        new InputFilter() {
//                            public CharSequence filter(CharSequence src, int start,
//                                                       int end, Spanned dst, int dstart, int dend) {
//                                if (src.equals("")) { // for backspace
//                                    return src;
//                                }
//                                if (src.toString().matches("[a-zA-Z ]+")) {
//                                    return src;
//                                }
//                                if (!isDeleteKey) {
//                                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.ENTER_ONLY_ALPHABETS, view);
//                                } else {
//                                    isDeleteKey = false;
//                                }
//
//                                return "";
//                            }
//                        }
//                });
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
                //if (twitter) {
                mEmailId.requestFocus();
                //}
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

        mEmailIdFB.setOnTouchListener(new View.OnTouchListener() {
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


        mGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                checkYearWheelVisibility();
                mGender.showDropDown();
                mGender.requestFocus();
                Utils.getInstance().gethideKeyboard(RegistrationActivity.this);
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

//        mBackButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                twitter = false;
//                finish();
//                LoginManager.getInstance().logOut();
//                Twitter.logOut();
//                Auth.GoogleSignInApi.signOut(LoginEmailActivity.mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(@NonNull Status status) {
//                                //updateUI(null);
//                            }
//                        });
//                overridePendingTransition(R.anim.leftin, R.anim.rightout);
//            }
//        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (yearWheel.isShown()) {
                    Animation anim = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.slidedown);
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
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
            }
        });


    }

    String firstName = "";
    String lastName = "";

    private void valadationOfSocialLogin() {
        if (Utils.isInternetAvailable(RegistrationActivity.this)) {

            checkYearWheelVisibility();
            if (mFirstName.getText().toString().length() == 0) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.FIRST_NAME_CAN_NOT_EMPTY, view);
                return;

            } else if (mFirstName.getText().toString().length() < 3) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.FIRST_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
                return;
            }else if(!mFirstName.getText().toString().matches("[a-zA-Z ]+"))
            {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.ENTER_ONLY_ALPHABETS+" in first name", view);
                return;
            }

            if (mLastName.getText().toString().length() == 0) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.LAST_NAME_CAN_NOT_EMPTY, view);
                return;

            } else if (mLastName.getText().toString().length() < 3) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.LAST_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
                return;
            }else if(!mLastName.getText().toString().matches("[a-zA-Z ]+"))
            {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.ENTER_ONLY_ALPHABETS+" in last name", view);
                return;
            }

            if (mEmailId.getText().length() == 0 && mEmailId.getVisibility() == View.VISIBLE) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.EMAIL_ID_CAN_NOT_EMPTY, view);
                return;
            }

            if (isValidText(mUserName.getText().toString().replace(" ", "").trim())) {

            } else if (mUserName.getText().toString().length() == 0) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_CAN_NOT_EMPTY, view);
                return;

            } else if (mUserName.getText().toString().length() < 3) {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.USER_NAME_SHOULD_CONTAIN_THREE_CHARACTER, view);
                return;
            }
            String email = "";
            if(mEmailId.getVisibility() == View.VISIBLE)
            {
                email = mEmailId.getText().toString();
            }else if(mEmailIdFB.getVisibility() == View.VISIBLE)
            {
                email = mEmailIdFB.getText().toString();
            }

            if (isValidEmail(email)) {
                if (twitter) {
                    socialUser.setEmailID(email);
                }
                if (socialUser != null) {

                    socialUser.setFname(mFirstName.getText().toString().trim());
                    socialUser.setLname(mLastName.getText().toString().trim());
                    socialUser.setUsername(mUserName.getText().toString().trim());

                    if (isValidEmail(socialUser.getEmailID())) {

                        if (firstName != null && firstName != "") {
                            firstName = socialUser.getFname().trim().substring(0, 1).toUpperCase() + socialUser.getFname().trim().substring(1);
                        } else {
                            firstName = mFirstName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                    mFirstName.getText().toString().trim().substring(1);
                        }
                        if (lastName != null && lastName != "") {
                            lastName = socialUser.getLname().trim().substring(0, 1).toUpperCase() + socialUser.getLname().trim().substring(1);
                        } else {
                            lastName = mLastName.getText().toString().trim().substring(0, 1).toUpperCase() +
                                    mLastName.getText().toString().trim().substring(1);
                        }

                        checkEmailIdAndProceed();

                    }
                }

            } else {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, "Invalid Email Id", view);
            }
        }
    }
    String email = "";
    public void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {

            Utils.getInstance().gethideKeyboard(this);

            pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);


            if(mEmailId.getVisibility() == View.VISIBLE)
            {
                email = mEmailId.getText().toString();
            }else if(mEmailIdFB.getVisibility() == View.VISIBLE)
            {
                email = mEmailIdFB.getText().toString();
            }
            if (loginEmailModel != null) {
                loginEmailModel.unRegisterView(this);
            }

            loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
            loginEmailModel.registerView(this);
            loginEmailModel.setProgressDialog(pDialog);
            loginEmailModel.loadLoginData(email);


        } else {
            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
        }
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


    private final CharSequence[] alertListItems = {"Take from camera", "Select from gallery"};
    private final String[] MEDIA_AND_CAMERA_PERMISSIONS_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    /**
     * Method to choose an image and convert it to bitmap to set an profile picture
     * of the new user at the time of registration
     **/
    private void getUserChooserOptions() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegistrationActivity.this);
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

    private Bitmap selectedBitmap;
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
                    selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, RegistrationActivity.this);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, RegistrationActivity.this, sdImageMainDirectory);
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

        if (requestCode == 500) {
            isBackToSplashScreen = true;
        }
    }

    private boolean isValidText(String text) {
        return text != null && text.length() >= 3;
    }


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


    public void showAlertDialog(String loginType,String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder
//                .setMessage("Oops, you previously used this email for " + loginType + " login. Please, login through different email.");
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, "+emailId+", with "+ loginType +" login, would you like to update your profile with this new login method?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        overrideUserData();


                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        // tvFacebookLogin.setText("Login with Facebook");
                        LoginManager.getInstance().logOut();
                        Utils.getInstance().gethideKeyboard(RegistrationActivity.this);
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.leftin, R.anim.rightout);
                        finish();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void showAlertBox(String loginType, String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder
//                .setMessage("Oops, you previously used this email for " + loginType + " login. Please, login through different email.");
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, "+emailId+", with "+ loginType +" login, would you like to update your profile with this new login method?");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        if (socialUser != null) {
                            showAlert(socialUser.getEmailID());
                        }
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        // tvFacebookLogin.setText("Login with Facebook");
                        LoginManager.getInstance().logOut();
                        Twitter.logOut();
                        Utils.getInstance().gethideKeyboard(RegistrationActivity.this);
                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_SCREEN);
                        overridePendingTransition(R.anim.leftin, R.anim.rightout);
                        finish();
                    }
                });


        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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
                        if (isImageProvided) {
                            try {
                                selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, RegistrationActivity.this);
                                selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, RegistrationActivity.this, sdImageMainDirectory);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
                            socialUser.setImageurl(convertedImage);
                        }
                        AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
                        Intent intent = new Intent(RegistrationActivity.this, VerificationEmailActivity.class);
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

    private LoginEmailModel loginEmailModel;

    @Override
    public void update() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fetchingAllDataModel != null && fetchingAllDataModel.getState() == BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                    showAlertDialogForSuccess(GlobalConstants.USER_SUCCESSFULLY_REGISTERED);
                } else if (fbLoginModel != null && fbLoginModel.getState() == BaseModel.STATE_SUCCESS_FETCH_FB_DATA) {
                    fbLoginModel.unRegisterView(RegistrationActivity.this);
                    getFBData();
                } else if (mMailChimpModelData != null && mMailChimpModelData.getState() == BaseModel.STATE_SUCCESS_MAIL_CHIMP) {
                    mMailChimpModelData.unRegisterView(RegistrationActivity.this);
                    if (!Utils.isInternetAvailable(RegistrationActivity.this) && pDialog.isShowing()) {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
                    } else {
                        getAllSocialLoginData("Y");
                    }
                    pDialog.dismiss();
                } else if (loginEmailModel != null && loginEmailModel.getState() == BaseModel.STATE_SUCCESS) {
                    pDialog.dismiss();
                    loginEmailModel.unRegisterView(RegistrationActivity.this);
                    if (Utils.isInternetAvailable(RegistrationActivity.this)) {
                        if (loginEmailModel.getLoginResult().toLowerCase().contains("vt_exists")) {

                            showAlertBox("Vault",email);

                        } else if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists")) {

                            showAlertBox("Facebook",email);

                        } else if (loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists")) {

                            showAlertBox("Twitter",email);

                        } else if (loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists")) {

                            showAlertBox("Google",email);

                        } else {

                            showConfirmLoginDialog(GlobalConstants.DO_YOU_WANT_TO_JOIN_OUR_MAILING_LIST, firstName,
                                    lastName, socialUser.getEmailID());
                            //gk  overrideUserData();
                        }

                    } else {
                        Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
                    }

                } else {
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
                }
            }

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        twitter = false;
        finish();
        LoginManager.getInstance().logOut();
        Twitter.logOut();
        Auth.GoogleSignInApi.signOut(LoginEmailActivity.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
        overridePendingTransition(R.anim.leftin, R.anim.rightout);
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
                        //GK storeDataOnServer("N");
                        getAllSocialLoginData("N");

                    }
                });

        alertDialogBuilder.setNegativeButton("Yes! Keep me Updated",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        if (Utils.isInternetAvailable(RegistrationActivity.this)) {

                            loadDataFromMailChimp(emailId, firstName, lastName);

                        } else {
                            Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
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

    private void getAllSocialLoginData(String registerValue) {

        facebookLogin(socialUser, registerValue);

    }

    /**
     * Method used for load mail chimp data from server
     *
     * @param email
     * @param firstName
     * @param lastName
     */
    public void loadDataFromMailChimp(String email, String firstName, String lastName) {

        pDialog = new ProgressDialog(RegistrationActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(RegistrationActivity.this));
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


    private void getFetchDataResponce() {
        try {
            pDialog.dismiss();
            fetchingAllDataModel.unRegisterView(RegistrationActivity.this);
            if (Utils.isInternetAvailable(RegistrationActivity.this)) {
                if (fetchingAllDataModel.getABoolean()) {
                    twitter = false;
                    Profile fbProfile = Profile.getCurrentProfile();
                    SharedPreferences pref = AppController.getInstance().getApplicationContext().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                    long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                    if (fbProfile != null || userId > 0) {

                        AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);
                        overridePendingTransition(R.anim.slideup, R.anim.nochange);
                        finish();
                        //gk if (!VideoDataService.isServiceRunning)

                            startService(new Intent(RegistrationActivity.this, TrendingFeaturedVideoService.class));
                    }
                } else {
                    Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_CONNECTION_TIMEOUT, view);
                }
            } else {
                Utils.getInstance().showToastMessage(RegistrationActivity.this, GlobalConstants.MSG_NO_CONNECTION, view);
            }


        } catch (Exception e) {
            e.printStackTrace();
            stopService(new Intent(RegistrationActivity.this, TrendingFeaturedVideoService.class));
            VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
            pDialog.dismiss();

        }
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

        leftOutAnimation = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.leftout);
        rightInAnimation = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.rightin);

        mFirstName.setAnimation(leftOutAnimation);
        mLastName.setAnimation(leftOutAnimation);
        mYOB.setAnimation(leftOutAnimation);
        mGender.setAnimation(leftOutAnimation);
        mEmailId.setAnimation(leftOutAnimation);
        mUserName.setAnimation(leftOutAnimation);
        mRegistertionButton.setAnimation(leftOutAnimation);
        tvAlreadyRegistered.setAnimation(leftOutAnimation);
        mEmailIdFB.setAnimation(leftOutAnimation);

        mFirstName.setVisibility(View.GONE);
        mLastName.setVisibility(View.GONE);
        mYOB.setVisibility(View.GONE);
        mGender.setVisibility(View.GONE);
        mEmailId.setVisibility(View.GONE);
        mEmailIdFB.setVisibility(View.GONE);
        mUserName.setVisibility(View.GONE);
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

