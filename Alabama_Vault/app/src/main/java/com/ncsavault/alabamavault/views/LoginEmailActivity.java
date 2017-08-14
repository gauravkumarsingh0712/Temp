package com.ncsavault.alabamavault.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.APIResponse;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.FBLoginModel;
import com.ncsavault.alabamavault.models.FetchingAllDataModel;
import com.ncsavault.alabamavault.models.LoginEmailModel;
import com.ncsavault.alabamavault.models.LoginPasswordModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

/**
 * Created by gauravkumar.singh on 07-08-2017.
 */
public class LoginEmailActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, AbstractView {

    private EditText edEmailBox,edPassword;
    private TextView tvSkipLogin,createNewAccount,tvForgotPassword;
    private Button tvNextLogin;
    private LinearLayout ll_header_image, ll_facebook_login;
    private ImageView tvFacebookLogin;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    User socialUser = new User();
    AlertDialog alertDialog;
    Profile fbProfile;
    ProgressDialog pDialog;
    private Animation animation;
    private SharedPreferences prefs;
    String videoUrl;
    ImageView gmailLogin;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private LoginEmailModel loginEmailModel;
    private FetchingAllDataModel fetchingAllDataModel;
    private FBLoginModel fbLoginModel;
    public static GoogleApiClient mGoogleApiClient;
    private FirebaseAnalytics mFirebaseAnalytics;
    Bundle params = new Bundle();
    TwitterLoginButton twitterLoginButton;
    ImageView imgTwitterLogin;
    // [START declare_auth]

    private static final int RC_SIGN_IN = 9001;
    private boolean isValue = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            System.out.println("push notification LoginEmailActivity");
            initThirdPartyLibary();


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(LoginEmailActivity.this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            // firebaseAuth();
            registerFacebookCallbackManager();

          //  setContentView(R.layout.login_email_activity);
            setContentView(R.layout.login_screen_layout);

            initAllDataRequiredInEmailActivity();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void registerFacebookCallbackManager() {

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // tvFacebookLogin.setText("Log Out");
                        getFacebookLoginStatus(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        //gk showAlert();

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        exception.printStackTrace();
                        showAlert();
                    }

                    private void showAlert() {
                        showToastMessage(GlobalConstants.FACEBOOK_LOGIN_CANCEL);

                    }
                });
    }


    private void initThirdPartyLibary() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        //gk CrashManager.initialize(this, GlobalConstants.HOCKEY_APP_ID, null);
        callbackManager = CallbackManager.Factory.create();

        // The Dev key cab be set here or in the manifest.xml
        AppsFlyerLib.setAppsFlyerKey("i6ZusgQ8L8qW9ADfXbqgre");
        AppsFlyerLib.sendTracking(getApplicationContext());

        TwitterAuthConfig authConfig =
                new TwitterAuthConfig(GlobalConstants.TWITTER_CONSUMER_KEY,
                        GlobalConstants.TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }

    private void initAllDataRequiredInEmailActivity() {
        initViews();
        initData();
        String videoId = AppController.getInstance().getModelFacade().getLocalModel().getVideoId();

        prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);

        if (Utils.isInternetAvailable(this)) {
            boolean isConfirmed = prefs.getBoolean(GlobalConstants.PREF_IS_CONFIRMATION_DONE, false);
            if (!isConfirmed)
                showNotificationConfirmationDialog(LoginEmailActivity.this);
        }

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
            }
        };

        fbProfile = Profile.getCurrentProfile();
        SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

//gk        if (videoId == null) {
//            if (fbProfile != null || userId > 0) {
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//gk        }

        String videoUrl = AppController.getInstance().getModelFacade().getLocalModel().getVideoUrl();
        if (videoUrl != null) {
            skipLogin();
        }

        if (videoId != null && videoId != "0") {
            skipLogin();
        }
        initListener();


    }

    @Override
    public void initData() {
        try {
            Point size = new Point();
            WindowManager w = getWindowManager();
            int screenWidth;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                w.getDefaultDisplay().getSize(size);
                screenWidth = size.x;
            } else {
                Display d = w.getDefaultDisplay();
                screenWidth = d.getWidth();
            }

            int dimension = (int) (screenWidth * 0.45);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dimension, dimension);
            lp.setMargins(0, 30, 0, 0);
            ll_header_image.setLayoutParams(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initViews() {
        try {
            edEmailBox = (EditText) findViewById(R.id.ed_email);
            edPassword = (EditText) findViewById(R.id.ed_password);
            ll_header_image = (LinearLayout) findViewById(R.id.ll_header_image);
            tvFacebookLogin = (ImageView) findViewById(R.id.tv_facebook_login);
            gmailLogin = (ImageView) findViewById(R.id.gmail_login);
            imgTwitterLogin = (ImageView) findViewById(R.id.twitter_login);
            tvSkipLogin = (TextView) findViewById(R.id.tv_skip_login);
            tvNextLogin = (Button) findViewById(R.id.tv_next_email);
            ll_facebook_login = (LinearLayout) findViewById(R.id.ll_facebook_login);

            isValue = AppController.getInstance().getModelFacade().getLocalModel().isRegisteredEmailIdForgot();
            String emailId = AppController.getInstance().getModelFacade().getLocalModel().getRegisteredEmailIdForgot();

            createNewAccount = (TextView) findViewById(R.id.tv_new_account);
            tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);


//            if (isValue) {
//                AppController.getInstance().getModelFacade().getLocalModel().setRegisteredEmailIdForgot(false);
//                edEmailBox.setText(emailId);
//                tvNextLogin.setVisibility(View.VISIBLE);
//            }

            twitterLogin();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initListener() {

        tvSkipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(LoginEmailActivity.this);
                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    SharedPreferences prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                    prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, GlobalConstants.DEFAULT_USER_ID).apply();
                    prefs.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, true).apply();
                    fetchInitialRecordsForAll();
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }

            }
        });

        tvFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edEmailBox.getText().toString().isEmpty()) {
                    edEmailBox.setText("");
                }
                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    LoginManager.getInstance().logOut();
                    LoginManager.getInstance().logInWithReadPermissions(LoginEmailActivity.this, Arrays.asList(GlobalConstants.FACEBOOK_PERMISSION));

//                    if (Profile.getCurrentProfile() == null)
//                        LoginManager.getInstance().logOut();
//                        LoginManager.getInstance().logInWithReadPermissions(LoginEmailActivity.this, Collections.singletonList("public_profile, email, user_birthday"));
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        gmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edEmailBox.getText().toString().isEmpty()) {
                    edEmailBox.setText("");
                }
                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    signout();
                    if (mGoogleApiClient != null) {
                        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                        startActivityForResult(signInIntent, RC_SIGN_IN);
                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }

            }
        });

        imgTwitterLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TwitterSession session = Twitter.getSessionManager().getActiveSession();
//                TwitterAuthToken authToken = session.getAuthToken();
//                String token = authToken.token;
//                String secret = authToken.secret;
//
//                if (token != null) {
//                    session = null;
//                    authToken = null;
//                }
//
//                if (session == null) {
//                    twitterLoginButton.performClick();
//                } else {
//                    String twitterToken = prefs.getString(TWEET_AUTH_KEY, "");
//                    String twitterSecret = prefs.getString(TWEET_AUTH_SECRET_KEY, "");
//
//
//                }
                Utils.getInstance().gethideKeyboard(LoginEmailActivity.this);

                if (!edEmailBox.getText().toString().isEmpty()) {
                    edEmailBox.setText("");
                }

                if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                    boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");
                    if (!installedTwitterApp) {

                        String twitterPlayStoreUrl = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
                        showConfirmSharingDialog("Twitter app is not installed would you like to install it now?", twitterPlayStoreUrl);


                    } else {

                        twitterLoginButton.performClick();

                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }

            }
        });

//        edEmailBox.setOnKeyListener(new View.OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    checkEmailAndProceed();
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });

        edEmailBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    // do your stuff here
                    checkEmailAndProceed();
                    //return true;
                }
                return false;
            }
        });


        tvNextLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSocialUser = false;
                Utils.getInstance().gethideKeyboard(LoginEmailActivity.this);

                        checkEmailAndProceed();


            }
        });

        edEmailBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    //ll_facebook_login.setVisibility(View.GONE);
                   // tvNextLogin.setVisibility(View.VISIBLE);
                } else {
                    // ll_facebook_login.setVisibility(View.VISIBLE);
                   // tvNextLogin.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              AppController.getInstance().handleEvent(AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN);
                overridePendingTransition(R.anim.rightin, R.anim.leftout);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidEmail(edEmailBox.getText().toString())) {
                    Intent intent = new Intent(LoginEmailActivity.this, ForgotPasswordActivity.class);
                    intent.putExtra("key", true);
                    intent.putExtra("email_id", edEmailBox.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_up_video_info, R.anim.nochange);
                }

            }
        });

    }

    private static final String TWEET_AUTH_KEY = "auth_key";
    private static final String TWEET_AUTH_SECRET_KEY = "auth_secret_key";
    private static final String TWEET_USER_NAME = "user_name";
    private static final String TWITTER_SHARE = "Twitter_Preferences";

    public void storeTwitterAccessToken(String accessToken, String username, String secretKey) {
        prefs = getSharedPreferences(TWITTER_SHARE, Context.MODE_PRIVATE);

        prefs.edit().putString(TWEET_AUTH_KEY, accessToken).apply();
        prefs.edit().putString(TWEET_AUTH_SECRET_KEY, secretKey).apply();
        prefs.edit().putString(TWEET_USER_NAME, username).apply();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    public void signout() {
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //updateUI(null);
                    }
                });
    }


    public void fetchInitialRecordsForAll() {

        if (Utils.isInternetAvailable(this)) {
            pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
            pDialog.show();
            pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.setCancelable(false);

            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    //  tvFacebookLogin.setText("Login with Facebook");
                    LoginManager.getInstance().logOut();
                }
            });

            if (loginEmailModel != null) {
                loginEmailModel.unRegisterView(this);
                loginEmailModel = null;
            }

            if (fbLoginModel != null) {
                fbLoginModel.unRegisterView(this);
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
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    public void checkEmailAndProceed() {
        if (Utils.isInternetAvailable(this)) {
            if (isValidEmail(edEmailBox.getText().toString())) {
                Utils.getInstance().gethideKeyboard(this);

                pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);

                String email = edEmailBox.getText().toString();
                AppController.getInstance().getModelFacade().getLocalModel().setEmailId(email);
                AppController.getInstance().getModelFacade().getLocalModel().storeEmailId(email);

                if (loginEmailModel != null) {
                    loginEmailModel.unRegisterView(this);
                    loginEmailModel = null;
                }

                loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
                loginEmailModel.registerView(this);
                loginEmailModel.setProgressDialog(pDialog);
                loginEmailModel.loadLoginData(email);
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
    }

    private boolean isValidEmail(String email) {
        if (email.length() == 0) {
            showToastMessage(GlobalConstants.ENTER_EMAIL_AND_PASSWORD);
            return false;
        } else {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                showToastMessage("Invalid Email");
                return false;
            } else
                return matcher.matches();
        }
    }

    public void showAlertDialog(String loginType,String emailId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("We see that you have previously used this email address, "+emailId+", with "+ loginType +" login, would you like to update your profile with this new login method?");

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        // tvFacebookLogin.setText("Login with Facebook");
                        //LoginManager.getInstance().logOut();
                        pDialog.dismiss();
                        if (!isSocialUser) {


                            loginVaultUser();

                        } else {
                            AppController.getInstance().getModelFacade().getLocalModel().setOverride(true);
                            isSocialUser = false;
                            AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);
                            overridePendingTransition(R.anim.rightin, R.anim.leftout);

                        }


                    }
                });

        alertDialogBuilder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        pDialog.dismiss();
                        isSocialUser = false;
                        // tvFacebookLogin.setText("Login with Facebook");
                        LoginManager.getInstance().logOut();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
        if (twitterLoginButton != null) {
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
        //gk CrashManager.initialize(this, GlobalConstants.HOCKEY_APP_ID, null);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (profileTracker != null) {
            profileTracker.stopTracking();
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
                animation = AnimationUtils.loadAnimation(LoginEmailActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    public void showNotificationConfirmationDialog(final Activity mActivity) {

        prefs = mActivity.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.notification_message));

        alertDialogBuilder.setPositiveButton("Allow",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Utils.getInstance().registerWithGCM(mActivity);
                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_CONFIRMATION_DONE, true).apply();
                    }
                });
        alertDialogBuilder.setNegativeButton("Deny",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        Utils.getInstance().unRegisterWithGCM(mActivity);
                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_NOTIFICATION_ALLOW, false).apply();
                        prefs.edit().putBoolean(GlobalConstants.PREF_IS_CONFIRMATION_DONE, true).apply();
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

    private void skipLogin() {
        SharedPreferences prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, GlobalConstants.DEFAULT_USER_ID).apply();
        prefs.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, true).apply();

        fetchInitialRecordsForAll();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void update() {
        System.out.println("login screen");
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              try {
                                  if (loginEmailModel != null && loginEmailModel.getState() == BaseModel.STATE_SUCCESS) {
                                      pDialog.dismiss();
                                      loginEmailModel.unRegisterView(LoginEmailActivity.this);
                                      if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                                          if(isValidPassword(edPassword.getText().toString())) {
                                              if (loginEmailModel != null) {
                                                  String emailId = AppController.getInstance().getModelFacade().getLocalModel().getEmailId();
                                                  if (loginEmailModel.getLoginResult().toLowerCase().contains("fb_exists")) {
                                                      showAlertDialog("Facebook", emailId);
                                                  } else if (loginEmailModel.getLoginResult().toLowerCase().contains("tw_exists")) {
                                                      showAlertDialog("Twitter", emailId);
                                                  } else if (loginEmailModel.getLoginResult().toLowerCase().contains("gm_exists")) {
                                                      showAlertDialog("Google", emailId);
                                                  } else {

                                                      // if (!isSocialUser) {

                                                      HashMap<String, String> stringHashMap = new HashMap<>();
                                                      stringHashMap.put("email", edEmailBox.getText().toString());
                                                      stringHashMap.put("status", loginEmailModel.getLoginResult());
                                                      AppController.getInstance().getModelFacade().getLocalModel().
                                                              setRegisterEmailId(edEmailBox.getText().toString());

                                                      if (loginEmailModel.getLoginResult().toLowerCase().contains("vt_exists")) {
                                                          loginVaultUser();
                                                          //gk AppController.getInstance().handleEvent(AppDefines.EVENT_ID_LOGIN_PASSWORD_SCREEN, stringHashMap);
                                                      } else {
//                                                      HashMap<String, String> hashMap = new HashMap<>();
//                                                      hashMap.put("email_id", edEmailBox.getText().toString());
//                                                      SharedPreferences pref = AppController.getInstance().getApplication().
//                                                              getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
//                                                      pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, false).commit();
//                                                      String email = pref.getString(GlobalConstants.PREF_VAULT_EMAIL, "");
//                                                      String emailBox = edEmailBox.getText().toString();
//                                                      if (emailBox.equals(email)) {
//                                                          pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, true).commit();
//                                                      } else {
//                                                          AppController.getInstance().getModelFacade().getLocalModel().setSelectImageBitmap(null);
//                                                      }
//                                                      AppController.getInstance().getModelFacade()
//                                                              .getLocalModel().setMailChimpRegisterUser(false);
//                                                      AppController.getInstance().handleEvent(AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN, hashMap);

                                                          showToastMessage(GlobalConstants.NOT_REGISTERED);

                                                      }
                                                      overridePendingTransition(R.anim.rightin, R.anim.leftout);
//                                                  } else {
//                                                      if (loginEmailModel.getLoginResult().toLowerCase().contains("vt_exists")) {
//                                                          showAlertDialog("Vault");
//                                                      } else {
//                                                          isSocialUser = false;
//                                                          AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);
//                                                          overridePendingTransition(R.anim.rightin, R.anim.leftout);
//                                                      }
//                                                  }

                                                  }
                                              }
                                              }


                                      } else {
                                          showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                      }
                                  } else if (fetchingAllDataModel != null && fetchingAllDataModel.getState() == BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                                      try {
                                          pDialog.dismiss();
                                          fetchingAllDataModel.unRegisterView(LoginEmailActivity.this);
                                          if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
                                              if (fetchingAllDataModel.getABoolean()) {
                                                  Profile fbProfile = Profile.getCurrentProfile();
                                                  SharedPreferences pref = AppController.getInstance().getApplicationContext().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                                                  long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);

                                                  if (fbProfile != null || userId > 0) {

                                                     AppController.getInstance().handleEvent(AppDefines.EVENT_ID_HOME_SCREEN);

                                                      overridePendingTransition(R.anim.slideup, R.anim.nochange);
                                                      finish();
                                                      /*if (!VideoDataService.isServiceRunning)

                                                          startService(new Intent(LoginEmailActivity.this, VideoDataService.class));*/
                                                      Intent intent = new Intent(LoginEmailActivity.this, TrendingFeaturedVideoService.class);
                                                      startService(intent);
                                                  }
                                              } else {
                                                  showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
                                              }
                                          } else {
                                              showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                                          }


                                      } catch (Exception e) {
                                          e.printStackTrace();
                                          stopService(new Intent(LoginEmailActivity.this, TrendingFeaturedVideoService.class));
                                          VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
                                          pDialog.dismiss();
                                      }
                                  } else if (fbLoginModel != null && fbLoginModel.getState() == BaseModel.STATE_SUCCESS_FETCH_FB_DATA) {
                                      fbLoginModel.unRegisterView(LoginEmailActivity.this);
                                      getFBData();
                                  } else if (mLoginPasswordModel != null && mLoginPasswordModel.getState() ==
                                          BaseModel.STATE_SUCCESS_EMAIL_PASSWORD_DATA) {

                                      mLoginPasswordModel.unRegisterView(LoginEmailActivity.this);
                                      loadEmailAndPasswordData();
                                  }

                              } catch (
                                      Exception e
                                      )

                              {
                                  e.printStackTrace();

                              }
                          }
                      }

        );
    }

    /**
     * Methos used for save fb data at server
     */
    public void getFBData() {
        System.out.println("Result of post user data : " + fbLoginModel.getResultData());
        if (Utils.isInternetAvailable(LoginEmailActivity.this)) {
            if (fbLoginModel.getResultData() != null) {
                if (fbLoginModel.getResultData().contains("success")) {
                    pDialog.dismiss();
                    SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                    pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, fbLoginModel.getUserId()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                    pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                    pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();
                    fetchInitialRecordsForAll();

                } else {
                    try {
                        Gson gson = new Gson();
                        Type classType = new TypeToken<APIResponse>() {
                        }.getType();
                        APIResponse response = gson.fromJson(fbLoginModel.getResultData().trim(), classType);
                        if (response.getReturnStatus() != null) {
                            if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                pDialog.dismiss();
                                showAlertDialog("Vault",response.getEmailID());
                            } else if (response.getReturnStatus().toLowerCase().contains("fb_exists") /*|| response.getReturnStatus().toLowerCase().contains("" +
                                                    "")*/) {
                                pDialog.dismiss();
                                SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, socialUser.getUsername()).apply();
                                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, socialUser.getEmailID()).apply();
                                pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();
                                params.putString("fb_exist", "fb_exist");
                                mFirebaseAnalytics.logEvent("fb_exist", params);

                                fetchInitialRecordsForAll();

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
                            showToastMessage("Can not connect to server. Please try again...");
                        }

                    } catch (Exception e) {
                        LoginManager.getInstance().logOut();
                        e.printStackTrace();
                        pDialog.dismiss();
                        showToastMessage("We are unable to process your request");
                    }
                }
            } else {
                showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);
            }
        } else {
            showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
        }
        pDialog.dismiss();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    AsyncTask<Void, Void, String> mLoginTask;

    private boolean isFBLogin = false;

    public void getFacebookLoginStatus(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            URL image_path = null;
                            try {
                                image_path = new URL("http://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large");
                                System.out.println("Image Path : " + image_path.toString());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            Log.v("LoginActivity", response.toString());

                            socialUser.setEmailID(object.getString("email"));
                            if (image_path != null)
                                socialUser.setImageurl(image_path.toString());
                            socialUser.setUsername(object.getString("name"));
                            socialUser.setPasswd("vault_fb_" + object.getString("id"));
                            socialUser.setGender("gender");
                            socialUser.setAppID(GlobalConstants.APP_ID);
                            socialUser.setAppVersion(GlobalConstants.APP_VERSION);
                            socialUser.setDeviceType(GlobalConstants.DEVICE_TYPE);
                            socialUser.setFname(object.getString("first_name"));
                            socialUser.setLname(object.getString("last_name"));
                            socialUser.setFlagStatus("fb");
                            socialUser.setSocialLoginToken(object.getString("id"));

                            AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
                            AppController.getInstance().getModelFacade().getLocalModel().setFacebookLogin(true);
                            AppController.getInstance().getModelFacade().getLocalModel().setGoogleLogin(false);
                            AppController.getInstance().getModelFacade().getLocalModel().setTwitterLogin(false);
                            isFBLogin = true;
                            checkExistingUserOrNot(socialUser);


                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            // tvFacebookLogin.setText("Login with Facebook");
                            e.printStackTrace();
                        }
                    }
                });


        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday, first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        String image_path = " ";
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            System.out.println("Token gmail : " + acct.getId());

            // firebaseAuthWithGoogle(acct);
            socialUser.setEmailID(acct.getEmail());

            socialUser.setUsername(acct.getDisplayName());
            socialUser.setPasswd(acct.getId());
            //socialUser.setGender("gender");
            socialUser.setAppID(GlobalConstants.APP_ID);
            socialUser.setAppVersion(GlobalConstants.APP_VERSION);
            socialUser.setDeviceType(GlobalConstants.DEVICE_TYPE);
            socialUser.setFname(acct.getFamilyName());
            socialUser.setLname(acct.getGivenName());
            socialUser.setFlagStatus("gm");
            socialUser.setSocialLoginToken(acct.getId());

            if (acct.getPhotoUrl() != null) {
                image_path = acct.getPhotoUrl().toString();
            }
            if (image_path != null) {
                socialUser.setImageurl(image_path);
            } else {
                Drawable image = getResources().getDrawable(R.drawable.defaultimage);
                String defaultImage = image.toString();
                socialUser.setImageurl(String.valueOf(defaultImage));

            }

            AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
            AppController.getInstance().getModelFacade().getLocalModel().setFacebookLogin(false);
            AppController.getInstance().getModelFacade().getLocalModel().setGoogleLogin(true);
            AppController.getInstance().getModelFacade().getLocalModel().setTwitterLogin(false);

            checkExistingUserOrNot(socialUser);


        } else {
            //If login fails
            Toast.makeText(this, "Google sign in was cancelled.", Toast.LENGTH_LONG).show();
        }
    }


    public void login(Result<TwitterSession> result) {

        //Creating a twitter session with result's data
        TwitterSession session = result.data;

        //Getting the username from session
        final String username = session.getUserName();

        Call<com.twitter.sdk.android.core.models.User> call = Twitter.getApiClient(session).getAccountService()
                .verifyCredentials(true, false);
        call.enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
            @Override
            public void failure(TwitterException e) {
                //If any error occurs handle it here
            }

            @Override
            public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                //If it succeeds creating a User object from userResult.data
                try {
                    //If it succeeds creating a User object from userResult.data
                    String image_path = "";
                    com.twitter.sdk.android.core.models.User user = userResult.data;
                    String fName = "";
                    String lName = "";
                    String twitterImage = user.profileImageUrl;
                    String userName = user.screenName;
                    String name = user.name;
                    long id = user.id;
                    if (name.toLowerCase().contains(" ")) {
                        String[] firstandLastName = name.split(" ");
                        fName = firstandLastName[0];
                        lName = firstandLastName[1];
                    } else {
                        fName = name;
                    }

                    image_path = twitterImage.toString();
                    if (image_path != null) {
                        socialUser.setImageurl(image_path);
                    } else {
                        Drawable image = getResources().getDrawable(R.drawable.defaultimage);
                        String defaultImage = image.toString();
                        socialUser.setImageurl(String.valueOf(defaultImage));

                    }

                    if (user.email == null) {
                        socialUser.setEmailID("");
                    }

                    socialUser.setUsername(userName);
                    socialUser.setFname(fName);
                    socialUser.setLname(lName);
                    socialUser.setPasswd(String.valueOf(id));
                    socialUser.setImageurl(twitterImage);
                    socialUser.setAppID(GlobalConstants.APP_ID);
                    socialUser.setAppVersion(GlobalConstants.APP_VERSION);
                    socialUser.setDeviceType(GlobalConstants.DEVICE_TYPE);
                    socialUser.setFlagStatus("tw");
                    socialUser.setSocialLoginToken(String.valueOf(id));
                    AppController.getInstance().getModelFacade().getLocalModel().setUser(socialUser);
                    AppController.getInstance().getModelFacade().getLocalModel().setFacebookLogin(false);
                    AppController.getInstance().getModelFacade().getLocalModel().setGoogleLogin(false);
                    AppController.getInstance().getModelFacade().getLocalModel().setTwitterLogin(true);


                    checkExistingUserOrNot(socialUser);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //This code will fetch the profile image URL
        //Getting the account service of the user logged in
    }


    /**
     * Method used for twitter login
     */
    private void twitterLogin() {
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                login(result);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }

    private boolean isSocialUser = false;

    private void checkExistingUserOrNot(final User socialUser) {
        try {
            mLoginTask = new AsyncTask<Void, Void, String>() {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                    pDialog.show();
                    pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.setCancelable(false);
                }

                @Override
                protected String doInBackground(Void... params) {
                    String email = "";

                    if (socialUser.getEmailID() != null) {
                        email = socialUser.getEmailID();
                    }
                    String socialUserToken = AppController.getInstance().getServiceManager().getVaultService().socialLoginExits(socialUser.getSocialLoginToken(), email);

                    return socialUserToken;
                }

                @Override
                protected void onPostExecute(String result) {

                    System.out.println("Result of post user data : " + result);
                    if (result != null) {
                        if (result.contains("existing_user")) {
                            pDialog.dismiss();
                            mLoginTask = null;
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response != null) {
                                SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, response.getUserID()).apply();
                                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, response.getEmailID()).apply();
                                fetchInitialRecordsForAll();
                            }
                        } else {

                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response != null) {
                                isSocialUser = true;
                                if (response.getReturnStatus().toLowerCase().contains("new_user")) {
                                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);
                                    overridePendingTransition(R.anim.rightin, R.anim.leftout);
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    if (isFBLogin) {
                                        isFBLogin = false;
                                        overrideUserData(socialUser);
                                    } else {
                                        showAlertDialog("Facebook",socialUser.getEmailID());
                                    }
                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    showAlertDialog("Twitter",socialUser.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    showAlertDialog("Google",socialUser.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("vt_exists")) {
                                    showAlertDialog("Vault",socialUser.getEmailID());
                                }
//                                if (socialUser.getEmailID() != null) {
//                                    pDialog.dismiss();
//                                    mLoginTask = null;
//
//                                    isSocialUser = true;
//                                    if (loginEmailModel != null) {
//                                        loginEmailModel.unRegisterView(LoginEmailActivity.this);
//                                    }
//
//                                    loginEmailModel = AppController.getInstance().getModelFacade().getRemoteModel().getLoginEmailModel();
//                                    loginEmailModel.registerView(LoginEmailActivity.this);
//                                    loginEmailModel.setProgressDialog(pDialog);
//                                    loginEmailModel.loadLoginData(socialUser.getEmailID());
//                                } else {
//                                    AppController.getInstance().handleEvent(AppDefines.EVENT_ID_REGISTRATION_SCREEN);
//                                    overridePendingTransition(R.anim.rightin, R.anim.leftout);
//                                }
                            }


                        }

                    }
                }
            };
            mLoginTask.execute();
        } catch (Exception e) {
            pDialog.dismiss();
            mLoginTask = null;
            LoginManager.getInstance().logOut();
            // tvFacebookLogin.setText("Login with Facebook");
            e.printStackTrace();
        }
    }

    AsyncTask<Void, Void, String> mOverrideUserTask;

    private void overrideUserData(final User vaultUser) {

        mOverrideUserTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
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
                        params.putString("fb_exist", "fb_exist");
                        mFirebaseAnalytics.logEvent("fb_exist", params);
                    } else {
                        try {
                            Gson gson = new Gson();
                            Type classType = new TypeToken<APIResponse>() {
                            }.getType();
                            APIResponse response = gson.fromJson(result.trim(), classType);
                            if (response.getReturnStatus() != null) {
                                if (response.getReturnStatus().toLowerCase().contains("vt_exists") || response.getReturnStatus().toLowerCase().contains("false")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Vault",vaultUser.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("gm_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Google",vaultUser.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("tw_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Twitter",vaultUser.getEmailID());
                                } else if (response.getReturnStatus().toLowerCase().contains("fb_exists")) {
                                    pDialog.dismiss();
                                    showAlertDialog("Facebook",vaultUser.getEmailID());
                                }
                            } else {
                                pDialog.dismiss();
                                LoginManager.getInstance().logOut();
                                // tvFacebookLogin.setText("Login with Facebook");
                                // showToastMessage(result);
                                showToastMessage("Can not connect to server. Please try again...");
                            }

                            mOverrideUserTask = null;
                        } catch (Exception e) {
                            LoginManager.getInstance().logOut();
                            e.printStackTrace();
                            pDialog.dismiss();
                            mOverrideUserTask = null;
                            // tvFacebookLogin.setText("Login with Facebook");
                            showToastMessage("We are unable to process your request");
                        }
                    }

                }
            }
        };
        mOverrideUserTask.execute();
    }

    private boolean checkIfAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
            //Check if the Facebook app is disabled
            ApplicationInfo ai = getPackageManager().getApplicationInfo(uri, 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    public void showConfirmSharingDialog(String message, final String playStoreUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Install",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(playStoreUrl));
                        startActivityForResult(intent, 100);

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
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

    private LoginPasswordModel mLoginPasswordModel;

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
                showToastMessage(GlobalConstants.ENTER_EMAIL_AND_PASSWORD);
            } else if (pass.length() < 6) {
//                    edPassword.setError("Minimum 6 characters required!");
                showToastMessage("Password should contain minimum 6 characters!");
            }
        }
        return false;
    }
    public void loginVaultUser() {
        if (Utils.isInternetAvailable(this)) {
            if (isValidPassword(edPassword.getText().toString())) {

                Utils.getInstance().gethideKeyboard(this);
                String password = "";
                String email = "";

                pDialog = new ProgressDialog(LoginEmailActivity.this, R.style.CustomDialogTheme);
                pDialog.show();
                pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(LoginEmailActivity.this));
                pDialog.setCanceledOnTouchOutside(false);
                password = edPassword.getText().toString();
                email    = edEmailBox.getText().toString();

                if (loginEmailModel != null) {
                    loginEmailModel.unRegisterView(this);
                    loginEmailModel = null;
                }


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

    public void loadEmailAndPasswordData() {
        if (!Utils.isInternetAvailable(LoginEmailActivity.this) && mLoginPasswordModel.getProgressDialog().isShowing()) {
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
                            pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, edEmailBox.getText().toString()).apply();
                            pref.edit().putBoolean(GlobalConstants.PREF_VAULT_SKIP_LOGIN, false).apply();

                            // createAccount(email, edPassword.getText().toString());
                            fetchInitialRecordsForAll();
                            params.putString("vt_exist", "vt_exist");
                            mFirebaseAnalytics.logEvent("vt_exist", params);
                        } else {
//                                        edPassword.setError("Password is incorrect!");
                            //gk showToastMessage("Password is incorrect!");

                            AppController.getInstance().getModelFacade().getLocalModel().setOverride(true);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email_id", edEmailBox.getText().toString());
                            SharedPreferences pref = AppController.getInstance().getApplication().
                                    getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                            pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, false).commit();
                            String email = pref.getString(GlobalConstants.PREF_VAULT_EMAIL, "");
                            String emailBox = edEmailBox.getText().toString();
                            if (emailBox.equals(email)) {
                                pref.edit().putBoolean(GlobalConstants.PREF_VAULT_FLAG_STATUS, true).commit();
                            } else {
                                AppController.getInstance().getModelFacade().getLocalModel().setSelectImageBitmap(null);
                            }
                            AppController.getInstance().getModelFacade()
                                    .getLocalModel().setMailChimpRegisterUser(false);
                            AppController.getInstance().handleEvent(AppDefines.EVENT_ID_UPLOAD_PHOTO_SCREEN, hashMap);
                            overridePendingTransition(R.anim.rightin, R.anim.leftout);
                        }
                    }
                } else {

                    Thread.currentThread();
                    Thread.sleep(2000);
                    if (!Utils.isInternetAvailable(LoginEmailActivity.this)) {
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

}


