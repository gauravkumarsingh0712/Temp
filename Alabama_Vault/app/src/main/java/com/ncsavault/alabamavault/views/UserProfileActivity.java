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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.UserProfileModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by gauravkumar.singh on 17-04-2017.
 */
public class UserProfileActivity extends PermissionActivity implements TextWatcher, AbstractView {

    private TextView tvEditHeader;
    private TextView tvCloseHeader;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvBio;
    private TextView tvFacebookStatus;
    private TextView tvTwitterStatus;
    private TextView tvChangePassword;
    private EditText edFirstName;
    private EditText edLastName;
    private EditText edBio;
    private TextView tvLogout;
    private TwitterLoginButton twitterLoginButton;

    ImageView imgUserProfile;
    ProgressBar pBar;
    ScrollView scrollView;

    String username = "";

    private boolean isValidFields = true;

    private DisplayImageOptions options;

    private User responseUser = null;

    private Uri selectedImageUri = null;
    private Uri outputFileUri = null;
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 100;
    File sdImageMainDirectory;

    private boolean isEditing = false;
    AlertDialog alertDialog = null;
    ProgressDialog pDialog;
    private static CallbackManager callbackManager;
    Animation animation;
    private boolean isBackToSplashScreen = false;
    private boolean askAgainForMustPermissions = false;
    private boolean goToSettingsScreen = false;

    private UserProfileModel mUserProfileModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        File cacheDir = StorageUtils.getCacheDirectory(UserProfileActivity.this);
        ImageLoaderConfiguration config;
        config = new ImageLoaderConfiguration.Builder(UserProfileActivity.this)
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
        initialiseAllData();


    }

    public void initialiseAllData() {
        initViews();
        initData();
        initializeFacebookUtils();
        initListener();
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

    public void initViews() {
        edFirstName = (EditText) findViewById(R.id.ed_first_name);
        edLastName = (EditText) findViewById(R.id.ed_last_name);
        edBio = (EditText) findViewById(R.id.ed_bio);

        imgUserProfile = (ImageView) findViewById(R.id.imgUserProfile);

        tvCloseHeader = (TextView) findViewById(R.id.tv_close);
        tvEditHeader = (TextView) findViewById(R.id.tv_edit);

        tvFirstName = (TextView) findViewById(R.id.tvFirstName);
        tvLastName = (TextView) findViewById(R.id.tvLastName);
        tvBio = (TextView) findViewById(R.id.tvBioText);

        tvFacebookStatus = (TextView) findViewById(R.id.tv_facebook_status);
        tvTwitterStatus = (TextView) findViewById(R.id.tv_twitter_status);
        tvChangePassword = (TextView) findViewById(R.id.tv_change_password);

        tvLogout = (TextView) findViewById(R.id.tv_logout);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        pBar = (ProgressBar) findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.progress_large_material, null));
        }
//        edBio.setScroller(new Scroller(this));
        edBio.setMaxLines(2);
        edBio.setVerticalScrollBarEnabled(true);

        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);

        edFirstName.setTag(false);
        edLastName.setTag(false);

    }

    public void initializeFacebookUtils() {
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        System.out.println("Facebook login successful");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        try {
                                            /*URL image_path;
                                            try {
                                                image_path = new URL("http://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large");
                                                System.out.println("Image Path : " + image_path.toString());
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }*/
                                            Log.v("LoginActivity", response.toString());

                                            tvFacebookStatus.setText(object.getString("name"));
                                        } catch (Exception e) {
                                            LoginManager.getInstance().logOut();
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender, birthday, first_name, last_name");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        showAlert();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showAlert();
                    }

                    private void showAlert() {
                        showToastMessage(GlobalConstants.FACEBOOK_LOGIN_CANCEL);
                       /* new AlertDialog.Builder(ProfileUpdateActivity.this)
                                .setTitle("Cancelled")
                                .setMessage("Process was cancelled")
                                .setPositiveButton("Ok", null)
                                .show();*/
                    }
                });

       /* profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {

            }
        };*/
    }

    @SuppressWarnings("deprecation")
    public void initData() {
        try {
            prefs = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
            //set drawable from stream
            imgUserProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));

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

            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(screenWidth / 2, screenWidth / 2);
            imgUserProfile.setLayoutParams(lp);

            if (Utils.isInternetAvailable(this))
                loadUserDataFromServer();
            else
                loadUserDataFromLocal();

////            boolean isTwitter = prefs.getBoolean(TWITTER_LINKING, false);
////            boolean isFacebook = prefs.getBoolean(FACEBOOK_LINKING, false);
//
//           // if (isTwitter) {
//                TwitterSession session =
//                        Twitter.getSessionManager().getActiveSession();
//                if (session != null) {
//                    tvTwitterStatus.setText("@" + session.getUserName());
//                }
//           // }
//          //  if (isFacebook) {
//                Profile fbProfile = Profile.getCurrentProfile();
//                if (fbProfile != null) {
//                    tvFacebookStatus.setText(fbProfile.getName());
//                }
//         //   }

            Profile fbProfile = Profile.getCurrentProfile();
            if (fbProfile != null) {
                tvFacebookStatus.setText(fbProfile.getName());
            }

            TwitterSession session =
                    Twitter.getSessionManager().getActiveSession();

            if (session != null) {
//                TwitterAuthToken authToken = session.getAuthToken();
//                String token = authToken.token;
//                String secret = authToken.secret;
                tvTwitterStatus.setText("@" + session.getUserName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SharedPreferences prefs;
    private String TWITTER_LINKING = "is_twitter";
    private String FACEBOOK_LINKING = "is_facebook";

    public void initListener() {

        tvTwitterStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(getApplicationContext())) {

                    boolean installedTwitterApp = checkIfAppInstalled("com.twitter.android");
                    if (!installedTwitterApp) {

                        String twitterPlayStoreUrl = "https://play.google.com/store/apps/details?id=com.twitter.android&hl=en";
                        showConfirmSharingDialog("Twitter app is not installed would you like to install it now?", twitterPlayStoreUrl);


                    } else {
                        // prefs.edit().putBoolean(TWITTER_LINKING, true).apply();
                        TwitterSession session =
                                Twitter.getSessionManager().getActiveSession();
                        if (session == null) {
                            twitterLoginButton.performClick();
                        }else
                        {
                           Twitter.logOut();
                            tvTwitterStatus.setText("Link Twitter Accout");
                        }
                    }


                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {

//                Toast.makeText(ProfileUpdateActivity.this,"Twitter Login Done",Toast.LENGTH_SHORT).show();
                tvTwitterStatus.setText("@" + twitterSessionResult.data.getUserName());
            }

            @Override
            public void failure(TwitterException e) {

            }
        });

        tvFacebookStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(getApplicationContext())) {

                    // prefs.edit().putBoolean(FACEBOOK_LINKING, true).apply();
                    if (Profile.getCurrentProfile() == null) {
                        LoginManager.getInstance().logInWithReadPermissions(UserProfileActivity.this,
                                Arrays.asList(GlobalConstants.FACEBOOK_PERMISSION));
                    }else
                    {

                        LoginManager.getInstance().logOut();
                        tvFacebookStatus.setText("Link Facebook Account");
                    }
                    // LoginManager.getInstance().logInWithReadPermissions(UserProfileActivity.this, Arrays.asList("public_profile"));

                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        edBio.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v("TAG", "CHILD TOUCH");

                if (v.getId() == R.id.ed_bio) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Twitter.logOut();

                stopService(new Intent(UserProfileActivity.this, TrendingFeaturedVideoService.class));
//                VideoDataFetchingService.isServiceRunning = false;
                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }
                SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, "").apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, "").apply();

                VaultDatabaseHelper.getInstance(getApplicationContext()).removeAllRecords();
                Intent intent = new Intent(UserProfileActivity.this, LoginEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppController.getInstance().handleEvent(AppDefines.EVENT_ID_CHANGE_PASSWORD_SCREEN);
            }
        });


        tvEditHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(getApplicationContext())) {
                    if (isEditing) {
                        if (!isValidText(edFirstName.getText().toString())) {
                            isValidFields = false;
//                            edFirstName.setError("Invalid! Minimum 3 characters");
                            showToastMessage("First Name should have minimum 3 characters");
                        } else if (!isValidText(edLastName.getText().toString())) {
                            isValidFields = false;
//                            edLastName.setError("Invalid! Minimum 3 characters");
                            showToastMessage("Last Name should have minimum 3 characters");
                        }


                        if (isValidFields) {
                            //make a server call for updating the data along with video
                            Utils.getInstance().gethideKeyboard(UserProfileActivity.this);
                            tvEditHeader.setText("Edit");

                            tvFirstName.setText(edFirstName.getText().toString());
                            tvLastName.setText(edLastName.getText().toString());
                            tvBio.setHint("bio");
                            tvBio.setHintTextColor(Color.WHITE);
                            tvBio.setText(edBio.getText().toString().trim());

                            edFirstName.setVisibility(View.GONE);
                            edLastName.setVisibility(View.GONE);
                            edBio.setVisibility(View.GONE);
                            isEditing = false;
                            edFirstName.setBackground(null);
                            edLastName.setBackground(null);
                            edBio.setBackground(null);

                            tvFirstName.setVisibility(View.VISIBLE);
                            tvLastName.setVisibility(View.VISIBLE);
                            tvBio.setVisibility(View.VISIBLE);
                            updateUserData();
                        }
                        isValidFields = true;
                    } else {
                        isEditing = true;
                        edFirstName.setFocusable(true);
                        tvEditHeader.setText("Done");
                        edFirstName.setVisibility(View.VISIBLE);
                        edLastName.setVisibility(View.VISIBLE);
                        edBio.setVisibility(View.VISIBLE);

                        tvFirstName.setVisibility(View.GONE);
                        tvLastName.setVisibility(View.GONE);
                        tvBio.setVisibility(View.GONE);

                        edFirstName.setBackgroundResource(R.drawable.edittext_selector);
                        edLastName.setBackgroundResource(R.drawable.edittext_selector);
                        edBio.setBackgroundResource(R.drawable.edittext_selector);
                    }
                } else {
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        imgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(getApplicationContext()))
                    try {
                        //Marshmallow permissions for write external storage.
                        if (haveAllMustPermissions(writeExternalStorage, PERMISSION_REQUEST_MUST)) {
//                            if (responseUser != null && responseUser.getFlagStatus().toLowerCase().equals("fb") || responseUser.getFlagStatus().toLowerCase().equals("gm")) {
//                                return;
//                            }
                            openImageIntent();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                else
                    showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
        });

        tvCloseHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getInstance().gethideKeyboard(UserProfileActivity.this);
                if (isEditing) {
                    showConfirmationDialog();
                } else {
                    finish();
                }
            }
        });

        edFirstName.addTextChangedListener(this);
        edLastName.addTextChangedListener(this);
        edBio.addTextChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (isEditing) {
            showConfirmationDialog();
        } else {
            super.onBackPressed();

        }
    }

    public void showConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Do you want to save changes you made?");
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!isValidText(edFirstName.getText().toString())) {
                            isValidFields = false;
//                            edFirstName.setError("Invalid! Minimum 3 characters");
                            showToastMessage("First Name should have minimum 3 characters");
                        } else if (!isValidText(edLastName.getText().toString())) {
                            isValidFields = false;
//                            edLastName.setError("Invalid! Minimum 3 characters");
                            showToastMessage("Last Name should have minimum 3 characters");
                        }

                        if (isValidFields) {
                            Utils.getInstance().gethideKeyboard(UserProfileActivity.this);
                            tvEditHeader.setText("Edit");

                            tvFirstName.setText(edFirstName.getText().toString());
                            tvLastName.setText(edLastName.getText().toString());
                            tvBio.setText(edBio.getText().toString());

                            edFirstName.setVisibility(View.GONE);
                            edLastName.setVisibility(View.GONE);
                            edBio.setVisibility(View.GONE);
                            isEditing = false;
                            edFirstName.setBackground(null);
                            edLastName.setBackground(null);
                            edBio.setBackground(null);

                            tvFirstName.setVisibility(View.VISIBLE);
                            tvLastName.setVisibility(View.VISIBLE);
                            tvBio.setVisibility(View.VISIBLE);
//                            isEdited = false;
                            //make a server call for updating the data along with video
                            updateUserData();
                        }
                        isValidFields = true;
                        alertDialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
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


    public void loadUserDataFromLocal() {
        try {
            responseUser = AppController.getInstance().getModelFacade().getLocalModel().getUserData();
            if (responseUser != null) {
                if (responseUser.getUserID() > 0) {
                    tvFirstName.setText(responseUser.getFname());
                    tvLastName.setText(responseUser.getLname());
                    if (responseUser.getBiotext() != null)
                        if (responseUser.getBiotext().length() > 0)
                            tvBio.setText(responseUser.getBiotext());

                    username = responseUser.getUsername();

                    edFirstName.setText(responseUser.getFname());
                    edLastName.setText(responseUser.getLname());
                    if (responseUser.getBiotext() != null)
                        if (responseUser.getBiotext().length() > 0)
                            edBio.setText(responseUser.getBiotext());

                    if (!responseUser.getFlagStatus().toLowerCase().equals("vt")) {
                        tvChangePassword.setVisibility(View.GONE);
//                        tvEditHeader.setVisibility(View.GONE);
//                        edBio.setVisibility(View.GONE);
                    } else {
                        tvEditHeader.setVisibility(View.VISIBLE);
//                        tvEditHeader.setVisibility(View.VISIBLE);
//                        edBio.setVisibility(View.VISIBLE);
                    }

                    if (responseUser.getImageurl().length() > 0) {
                        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(responseUser.getImageurl(), imgUserProfile, options, new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                pBar.setVisibility(View.VISIBLE);
                                try {
                                    /*InputStream istr = getAssets().open("placeholder.jpg");
                                    //set drawable from stream
                                    imgUserProfile.setImageDrawable(Drawable.createFromStream(istr, null));*/

                                    imgUserProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
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
                                    imgUserProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
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
                } else {
//                                Toast.makeText(ProfileUpdateActivity.this, "Error loading information!!! Please try again later.", Toast.LENGTH_LONG).show();
                    showToastMessage("Error loading information");
                }
            } else {
//                            Toast.makeText(ProfileUpdateActivity.this, "Error loading information!!! Please try again later.", Toast.LENGTH_LONG).show();
                showToastMessage("Error loading information");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadUserDataFromServer() {

        pDialog = new ProgressDialog(UserProfileActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UserProfileActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
        final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");

        if (mUserProfileModel != null) {
            mUserProfileModel.unRegisterView(this);
        }
        mUserProfileModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserProfileModel();
        mUserProfileModel.registerView(this);
        mUserProfileModel.setProgressDialog(pDialog);
        mUserProfileModel.loadFetchData(email, userId);
    }

    public void updateUserData() {

        pDialog = new ProgressDialog(UserProfileActivity.this, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog(UserProfileActivity.this));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        if (responseUser != null) {
            responseUser.setUsername(username);
            responseUser.setFname(tvFirstName.getText().toString());
            responseUser.setLname(tvLastName.getText().toString());
            responseUser.setBiotext(tvBio.getText().toString());
        }

        try {
            SharedPreferences pref = getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
            final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
            final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");
            if (responseUser != null) {
                if (selectedImageUri != null) {
                    Bitmap selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, UserProfileActivity.this);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, UserProfileActivity.this, sdImageMainDirectory);
                    String convertedImage = ConvertBitmapToBase64Format(selectedBitmap);
                    responseUser.setImageurl(convertedImage);
                }
            }

            if (mUserProfileModel != null) {
                mUserProfileModel.unRegisterView(this);
            }
            mUserProfileModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserProfileModel();
            mUserProfileModel.registerView(this);
            mUserProfileModel.setProgressDialog(pDialog);
            mUserProfileModel.loadUserProfileData(responseUser, email, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isValidText(String str) {
        return str != null && str.length() >= 3;
    }


    public String ConvertBitmapToBase64Format(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }


    private final CharSequence[] alertListItems = {"Take from camera", "Select from gallery"};
    private final String[] MEDIA_AND_CAMERA_PERMISSIONS_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    /**
     * Method to choose an image and convert it to bitmap to set an profile picture
     * of the new user at the time of registration
     **/
    private void getUserChooserOptions() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UserProfileActivity.this);
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

        getUserChooserOptions();
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
                    Bitmap selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, UserProfileActivity.this);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap, selectedImageUri, UserProfileActivity.this, sdImageMainDirectory);

                        /*Drawable drawable = new BitmapDrawable(getResources(), selectedBitmap);
                        imgUserProfile.setImageDrawable(drawable);*/

                    imgUserProfile.setImageBitmap(selectedBitmap);

                    isEditing = true;
                    edFirstName.setFocusable(true);
                    tvEditHeader.setText("Done");
                    edFirstName.setVisibility(View.VISIBLE);
                    edLastName.setVisibility(View.VISIBLE);
                    edBio.setVisibility(View.VISIBLE);

                    tvFirstName.setVisibility(View.GONE);
                    tvLastName.setVisibility(View.GONE);
                    tvBio.setVisibility(View.GONE);

                    edFirstName.setBackgroundResource(R.drawable.edittext_selector);
                    edLastName.setBackgroundResource(R.drawable.edittext_selector);
                    edBio.setBackgroundResource(R.drawable.edittext_selector);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        if (twitterLoginButton != null) {
            twitterLoginButton.onActivityResult(requestCode, resultCode,
                    data);
        }
        if (requestCode == 500) {
            isBackToSplashScreen = true;
        }

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

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
                animation = AnimationUtils.loadAnimation(UserProfileActivity.this,
                        R.anim.abc_fade_out);

                text.setAnimation(animation);
                text.setVisibility(View.GONE);
            }
        }, 2000);
    }

    @Override
    public void update() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mUserProfileModel != null && mUserProfileModel.getState() == BaseModel.STATE_SUCCESS) {
                    mUserProfileModel.unRegisterView(UserProfileActivity.this);
                    try {
                        pDialog.dismiss();
                        if (mUserProfileModel.getUserProfileResult()) {
                            showToastMessage("Profile updated successfully");
                        } else {
                            showToastMessage("Error updating information");
                            loadUserDataFromLocal();
                        }
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

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mUserProfileModel != null && mUserProfileModel.getState() == BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                    mUserProfileModel.unRegisterView(UserProfileActivity.this);
                    loadData();
                }
            }
        });
    }

    public void loadData() {
        try {
            if (Utils.isInternetAvailable(UserProfileActivity.this)) {
                if (mUserProfileModel.getFetchingResult() != null) {
                    Gson gson = new Gson();
                    Type classType = new TypeToken<User>() {
                    }.getType();
                    responseUser = gson.fromJson(mUserProfileModel.getFetchingResult().trim(), classType);
                    if (responseUser != null) {
                        if (responseUser.getUserID() > 0) {

                            AppController.getInstance().getModelFacade().getLocalModel().storeUserDataInPreferences(responseUser);
                            username = responseUser.getUsername();
                            tvFirstName.setText(responseUser.getFname());
                            tvLastName.setText(responseUser.getLname());

                            if (responseUser.getBiotext() != null)
                                if (responseUser.getBiotext().length() > 0)
                                    tvBio.setText(responseUser.getBiotext());

                            edFirstName.setText(responseUser.getFname());
                            edLastName.setText(responseUser.getLname());
                            if (responseUser.getBiotext() != null)
                                if (responseUser.getBiotext().length() > 0)
                                    edBio.setText(responseUser.getBiotext());

                            if (!responseUser.getFlagStatus().toLowerCase().equals("vt")) {
                                tvChangePassword.setVisibility(View.GONE);
//                                tvEditHeader.setVisibility(View.GONE);
//                                edBio.setVisibility(View.GONE);
                            } else {
                                tvEditHeader.setVisibility(View.VISIBLE);
//                                tvEditHeader.setVisibility(View.VISIBLE);
//                                edBio.setVisibility(View.VISIBLE);
                            }

//                            if (responseUser.getFlagStatus().toLowerCase().equals("fb") || responseUser.getFlagStatus().toLowerCase().equals("gm")
//                                    || responseUser.getFlagStatus().toLowerCase().equals("tw")) {
//                                tvEditHeader.setVisibility(View.GONE);
//                                edBio.setVisibility(View.GONE);
//                            } else {
//                                tvEditHeader.setVisibility(View.VISIBLE);
//                                edBio.setVisibility(View.VISIBLE);
//                            }


//                                btnLogOut.setText("Log Out " + responseUser.getUsername());
                            if (responseUser.getImageurl().length() > 0) {
                                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(responseUser.getImageurl(), imgUserProfile, options, new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String imageUri, View view) {
                                        pBar.setVisibility(View.VISIBLE);
                                        try {
                                                /*InputStream istr = getAssets().open("placeholder.jpg");
                                                //set drawable from stream
                                                imgUserProfile.setImageDrawable(Drawable.createFromStream(istr, null));*/
                                            imgUserProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
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
                                            imgUserProfile.setImageDrawable(getResources().getDrawable(R.drawable.camera_background));
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
                        } else {
//                                Toast.makeText(ProfileUpdateActivity.this, "Error loading information!!! Please try again later.", Toast.LENGTH_LONG).show();
                            showToastMessage("Error loading information");
                        }
                    } else {
//                            Toast.makeText(ProfileUpdateActivity.this, "Error loading information!!! Please try again later.", Toast.LENGTH_LONG).show();
                        showToastMessage("Error loading information");
                    }

                } else {
                    showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);

                }

            } else {
                showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
            pDialog.dismiss();
        } catch (Exception e) {
            pDialog.dismiss();
            e.printStackTrace();
        }

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
}
