package com.ncsavault.alabamavault.fragments.views;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.defines.AppDefines;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.models.BaseModel;
import com.ncsavault.alabamavault.models.UserProfileModel;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.service.VideoDataService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.AbstractView;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.LoginEmailActivity;
import com.ncsavault.alabamavault.views.UserProfileActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twitter.sdk.android.Twitter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class ProfileFragment extends BaseFragment implements AbstractView {

    private static Context mContext;
    private SwitchCompat mSwitchCompat;
    OnFragmentTouched listener;
    private ImageView mPlayerBackgroundImage,mUserProfileImage;
    private TextView mFirstName,mLastName,mEmailId,mTwitterEmailId,mFacebookEmailId,mPushNotification;
    private EditText edFirstName,edLastName;
    private Button mResetPasswordButton,mLogoutButton;
    private DisplayImageOptions options;
    private User responseUser = null;
    ProgressDialog pDialog;
    private UserProfileModel mUserProfileModel;
    private ProgressBar pBar;
    private Uri selectedImageUri = null;
    private Uri outputFileUri = null;
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 100;
    File sdImageMainDirectory;
    AlertDialog alertDialog = null;
    private final CharSequence[] alertListItems = {"Take from camera", "Select from gallery"};
    private final String[] MEDIA_AND_CAMERA_PERMISSIONS_LIST = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private LinearLayout edLinearLayout,tvLinearLayout;
    private boolean isValidFields = true;
    private boolean isEditing = true;

    public static Fragment newInstance(Context context, int centerX, int centerY) {
        mContext = context;
        Bundle args = new Bundle();
        args.putInt("cx", centerX);
        args.putInt("cy", centerY);
        Fragment frag = new ProfileFragment();
        frag.setArguments(args);
        return frag;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.user_profile_screen_layout, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwitchCompat = (SwitchCompat) view.findViewById(R.id.toggle_button);
        mPlayerBackgroundImage = (ImageView) view.findViewById(R.id.background_profile_image);
        mUserProfileImage = (ImageView) view.findViewById(R.id.imgUserProfile);
        mFirstName = (TextView) view.findViewById(R.id.tv_first_name);
        mLastName = (TextView) view.findViewById(R.id.tv_last_name);
        mEmailId = (TextView) view.findViewById(R.id.email_id);
        mTwitterEmailId = (TextView) view.findViewById(R.id.twitter_email_id);
        mFacebookEmailId = (TextView) view.findViewById(R.id.facebook_email_id);
        mPushNotification = (TextView) view.findViewById(R.id.tv_push_view);

        mResetPasswordButton = (Button) view.findViewById(R.id.tv_reset_password);
        mLogoutButton = (Button) view.findViewById(R.id.tv_logout);

        edFirstName = (EditText) view.findViewById(R.id.ed_first_name);
        edLastName = (EditText) view.findViewById(R.id.ed_last_name);

        pBar = (ProgressBar) view.findViewById(R.id.progressbar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            pBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
        } else {
            System.out.println("progress bar not showing ");
            pBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(),
                    R.drawable.progress_large_material, null));
        }

        edLinearLayout = (LinearLayout) view.findViewById(R.id.edit_linear_layout);
        tvLinearLayout = (LinearLayout) view.findViewById(R.id.text_linear_layout);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();


        if (Utils.isInternetAvailable(mContext))
            loadUserDataFromServer();

        initializeFacebookUtils();
        initListener();

    }

    private void initData()
    {
        Profile fbProfile = Profile.getCurrentProfile();
        if (fbProfile != null) {
            mFacebookEmailId.setText(fbProfile.getName());
        }
    }

    private void initListener()
    {
        mUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(mContext))
                    try {
                        //Marshmallow permissions for write external storage.
                      //gk  if (haveAllMustPermissions(writeExternalStorage, PERMISSION_REQUEST_MUST)) {

                            openImageIntent();

                    //gk    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                else
                    ((HomeScreen)mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
        });

        mResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(isEditing)
//                {
//                    edLinearLayout.setVisibility(View.VISIBLE);
//                    tvLinearLayout.setVisibility(View.GONE);
//                    isEditing = false;
//                    isValidFields = true;
//                }else
//                {
//                    if(isValidFields) {
//                        edLinearLayout.setVisibility(View.GONE);
//                        tvLinearLayout.setVisibility(View.VISIBLE);
//
//                        mFirstName.setText(edFirstName.getText().toString());
//                        mLastName.setText(edLastName.getText().toString());
//                        updateUserData();
//                    }
//                }
                AppController.getInstance().handleEvent(AppDefines.EVENT_ID_CHANGE_PASSWORD_SCREEN);

            }
        });

        mFacebookEmailId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isInternetAvailable(mContext.getApplicationContext())) {

                    // prefs.edit().putBoolean(FACEBOOK_LINKING, true).apply();
                    if (Profile.getCurrentProfile() == null) {
                        LoginManager.getInstance().logInWithReadPermissions((HomeScreen)mContext,
                                Arrays.asList(GlobalConstants.FACEBOOK_PERMISSION));
                    }else
                    {

                        LoginManager.getInstance().logOut();
                        mFacebookEmailId.setText("Link Facebook Account");
                    }
                    // LoginManager.getInstance().logInWithReadPermissions(UserProfileActivity.this, Arrays.asList("public_profile"));

                } else {
                    ((HomeScreen)mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                }
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Twitter.logOut();

                mContext.stopService(new Intent(mContext, TrendingFeaturedVideoService.class));
//                VideoDataFetchingService.isServiceRunning = false;
                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }
                SharedPreferences pref = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME,
                        Context.MODE_PRIVATE);
                pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, "").apply();
                pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, "").apply();

                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();
                Intent intent = new Intent(mContext, LoginEmailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }

    private static CallbackManager callbackManager;
    public void initializeFacebookUtils() {
        FacebookSdk.sdkInitialize(mContext.getApplicationContext());

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

                                            mFacebookEmailId.setText(object.getString("name"));
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
                        ((HomeScreen)mContext).showToastMessage(GlobalConstants.FACEBOOK_LOGIN_CANCEL);
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


    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator +
                GlobalConstants.PROFILE_PIC_DIRECTORY + File.separator);
        root.mkdirs();
        Random randomNumber = new Random();
        final String fname = GlobalConstants.PROFILE_PIC_DIRECTORY + "_" + randomNumber.nextInt(1000) + 1;
        sdImageMainDirectory = new File(root, fname);

        getUserChooserOptions();
    }

    /**
     * Method to choose an image and convert it to bitmap to set an profile picture
     * of the new user at the time of registration
     **/
    private void getUserChooserOptions() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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

        AlertDialog dialog = builder.create();
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
                outputFileUri = FileProvider.getUriForFile(mContext.getApplicationContext(),
                        mContext.getPackageName() + ".provider",
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

    private boolean checkIfAppInstalled(String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
            //Check if the Facebook app is disabled
            ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(uri, 0);
            app_installed = ai.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }

        return app_installed;
    }

    public void showConfirmSharingDialog(String message, final String playStoreUrl) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
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

    public void loadUserDataFromServer() {

        pDialog = new ProgressDialog(mContext, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog((HomeScreen)mContext));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);

        SharedPreferences pref = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
        final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");


        mUserProfileModel = AppController.getInstance().getModelFacade().getRemoteModel().getUserProfileModel();
        mUserProfileModel.registerView(this);
        mUserProfileModel.setProgressDialog(pDialog);
        mUserProfileModel.loadFetchData(email, userId);
    }

    public void updateUserData() {

        pDialog = new ProgressDialog(mContext, R.style.CustomDialogTheme);
        pDialog.show();
        pDialog.setContentView(Utils.getInstance().setViewToProgressDialog((HomeScreen)mContext));
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        if (responseUser != null) {
           //gk responseUser.setUsername(username);
            responseUser.setFname(edFirstName.getText().toString());
            responseUser.setLname(edLastName.getText().toString());
           //gk responseUser.setBiotext(tvBio.getText().toString());
        }

        try {
            SharedPreferences pref = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
            final long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
            final String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");
            if (responseUser != null) {
                if (selectedImageUri != null) {
                    Bitmap selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, (HomeScreen)mContext);
                    selectedBitmap = Utils.getInstance().
                            rotateImageDetails(selectedBitmap, selectedImageUri,
                                    (HomeScreen)mContext, sdImageMainDirectory);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {

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
                    Bitmap selectedBitmap = Utils.getInstance().decodeUri(selectedImageUri, (HomeScreen)mContext);
                    selectedBitmap = Utils.getInstance().rotateImageDetails(selectedBitmap,
                            selectedImageUri, (HomeScreen)mContext, sdImageMainDirectory);

                        /*Drawable drawable = new BitmapDrawable(getResources(), selectedBitmap);
                        imgUserProfile.setImageDrawable(drawable);*/

                    mUserProfileImage.setImageBitmap(selectedBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    public String ConvertBitmapToBase64Format(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentTouched) {
            listener = (OnFragmentTouched) activity;
        }
    }

    @Override
    public void update() {
        ((HomeScreen)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mUserProfileModel != null && mUserProfileModel.getState() == BaseModel.STATE_SUCCESS) {
                    mUserProfileModel.unRegisterView(ProfileFragment.this);
                    try {
                        pDialog.dismiss();
                        if (mUserProfileModel.getUserProfileResult()) {
                            ((HomeScreen )mContext).showToastMessage("Profile updated successfully");
                        } else {
                            ((HomeScreen )mContext).showToastMessage("Error updating information");
                           //gk loadUserDataFromLocal();
                        }
                        final File root = new File(Environment.getExternalStorageDirectory() +
                                File.separator + GlobalConstants.PROFILE_PIC_DIRECTORY + File.separator);
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
                } else if (mUserProfileModel != null && mUserProfileModel.getState() ==
                        BaseModel.STATE_SUCCESS_FETCH_ALL_DATA) {
                    mUserProfileModel.unRegisterView(ProfileFragment.this);
                    loadData();
                }
            }
        });
    }


    public void loadData() {
        try {
            if (Utils.isInternetAvailable(mContext)) {
                if (mUserProfileModel.getFetchingResult() != null) {
                    Gson gson = new Gson();
                    Type classType = new TypeToken<User>() {
                    }.getType();
                    responseUser = gson.fromJson(mUserProfileModel.getFetchingResult().trim(), classType);
                    if (responseUser != null) {
                        if (responseUser.getUserID() > 0) {

                            AppController.getInstance().getModelFacade().getLocalModel().
                                    storeUserDataInPreferences(responseUser);
                          //  username = responseUser.getUsername();
                            mFirstName.setText(responseUser.getFname());
                            mLastName.setText(responseUser.getLname());

                            mEmailId.setText(responseUser.getEmailID());

                            edFirstName.setText(responseUser.getFname());
                            edLastName.setText(responseUser.getLname());

                            if (!responseUser.getFlagStatus().toLowerCase().equals("vt")) {

                            } else {

                            }

                            if (responseUser.getImageurl().length() > 0) {
                                com.nostra13.universalimageloader.core.ImageLoader.getInstance().
                                        displayImage(responseUser.getImageurl(), mUserProfileImage, options,
                                        new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String imageUri, View view) {
                                        pBar.setVisibility(View.VISIBLE);
                                        try {
                                            mUserProfileImage.setImageDrawable(getResources().
                                                    getDrawable(R.drawable.camera_background));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                        pBar.setVisibility(View.GONE);
                                        try {
                                            mUserProfileImage.setImageDrawable(getResources().
                                                    getDrawable(R.drawable.camera_background));
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
                          // GK showToastMessage("Error loading information");
                        }
                    } else {
                       //GK showToastMessage("Error loading information");
                    }

                } else {
                    //GK showToastMessage(GlobalConstants.MSG_CONNECTION_TIMEOUT);

                }

            } else {
                //GK showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            }
            pDialog.dismiss();
        } catch (Exception e) {
            pDialog.dismiss();
            e.printStackTrace();
        }

    }



}
