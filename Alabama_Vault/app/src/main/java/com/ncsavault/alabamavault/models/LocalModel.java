package com.ncsavault.alabamavault.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;

import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 7/19/2016.
 */
public class LocalModel implements IModel {


    private int mDisplayHeight = 0;
    private int mDisplayWidth = 0;
    private int mListViewHeight = 0;
    private String firstName;
    private String lastName;
    private String emailId;
    private String videoId;
    private User user;
    private boolean isOverride;
    private boolean isTwitterLogin;
    private long tabId;

    public boolean isBannerActivated() {
        return isBannerActivated;
    }

    public void setBannerActivated(boolean bannerActivated) {
        isBannerActivated = bannerActivated;
    }

    private boolean isBannerActivated;

    public long getTabId() {
        return tabId;
    }

    public void setTabId(long tabId) {
        this.tabId = tabId;
    }

    public String getNotificationVideoId() {
        return notificationVideoId;
    }

    public void setNotificationVideoId(String notificationVideoId) {
        this.notificationVideoId = notificationVideoId;
    }

    private String notificationVideoId;

    public boolean isGoogleLogin() {
        return isGoogleLogin;
    }

    public void setGoogleLogin(boolean googleLogin) {
        isGoogleLogin = googleLogin;
    }

    public boolean isFacebookLogin() {
        return isFacebookLogin;
    }

    public void setFacebookLogin(boolean facebookLogin) {
        isFacebookLogin = facebookLogin;
    }

    private boolean isGoogleLogin;
    private boolean isFacebookLogin;
    private Bitmap selectImageBitmap;

    public Bitmap getSelectImageBitmap() {
        return selectImageBitmap;
    }

    public void setSelectImageBitmap(Bitmap selectImageBitmap) {
        this.selectImageBitmap = selectImageBitmap;
    }


    public boolean isOverride() {
        return isOverride;
    }

    public void setOverride(boolean override) {
        isOverride = override;
    }

    public boolean isTwitterLogin() {
        return isTwitterLogin;
    }

    public void setTwitterLogin(boolean twitterLogin) {
        isTwitterLogin = twitterLogin;
    }




    public String getRegisteredEmailIdForgot() {
        return registeredEmailIdForgot;
    }

    public void setRegisteredEmailIdForgot(String registeredEmailIdForgot) {
        this.registeredEmailIdForgot = registeredEmailIdForgot;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isRegisteredEmailIdForgot() {
        return isRegisteredEmailIdForgot;
    }

    public void setRegisteredEmailIdForgot(boolean registeredEmailIdForgot) {
        isRegisteredEmailIdForgot = registeredEmailIdForgot;
    }

    private String registeredEmailIdForgot;
    private boolean isRegisteredEmailIdForgot;

    public Uri getUriUrl() {
        return uriUrl;
    }

    public void setUriUrl(Uri uriUrl) {
        this.uriUrl = uriUrl;
    }

    private Uri uriUrl;

    private ArrayList<String> API_URLS = new ArrayList<>();

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    private String videoUrl;


    public LocalModel()
    {
    }

    public String getRegisterEmailId() {
        return registerEmailId;
    }

    public void setRegisterEmailId(String registerEmailId) {
        this.registerEmailId = registerEmailId;
    }


    private String registerEmailId;

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getmListViewHeight() {
        return mListViewHeight;
    }

    public void setmListViewHeight(int mListViewHeight) {
        this.mListViewHeight = mListViewHeight;
    }

    public int getmDisplayWidth() {
        return mDisplayWidth;
    }

    public void setmDisplayWidth(int mDisplayWidth) {
        this.mDisplayWidth = mDisplayWidth;
    }

    public int getmDisplayHeight() {
        return mDisplayHeight;
    }

    public void setmDisplayHeight(int mDisplayHeight) {
        this.mDisplayHeight = mDisplayHeight;
    }

    public ArrayList<String> getAPI_URLS() {
        return API_URLS;
    }

    public void setAPI_URLS(ArrayList<String> API_URLS) {
        this.API_URLS = API_URLS;
    }


    public void storeFnameAndLname(String firstName,String lastName)
    {
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME,firstName ).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, lastName).commit();
    }

    public void storeEmailId(String emailId)
    {
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL,emailId).commit();
    }

    public String getDeviceId(){
        /**/
        String deviceID = Settings.Secure.getString(AppController.getInstance().getApplication().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return deviceID;
    }

    public long getUserId(){
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        long userId = pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0);
        return userId;
    }

    public String getFName(){
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        String firstName = pref.getString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, "");
        return firstName;
    }

    public String getLName(){
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        String lastName = pref.getString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, "");
        return lastName;
    }

    public String getEmailAddress(){
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        String email = pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, "");
        return email;
    }


    public boolean getMailChimpRegisterUser() {
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        boolean mailChimpRegisterUser = pref.getBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP,false);
        return mailChimpRegisterUser;
    }

    public void setMailChimpRegisterUser(boolean registerUserValue) {
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        pref.edit().putBoolean(GlobalConstants.PREF_JOIN_MAIL_CHIMP, registerUserValue).commit();
    }


    public void storeUserDataInPreferences(User userDto){
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        pref.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, userDto.getUserID()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_EMAIL, userDto.getEmailID()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, userDto.getUsername()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, userDto.getFname()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, userDto.getLname()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_BIO_TEXT, userDto.getBiotext()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_IMAGE_URL, userDto.getImageurl()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_GENDER, userDto.getGender()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FLAG_STATUS, userDto.getFlagStatus()).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_PASSWORD, userDto.getPasswd()).commit();
        pref.edit().putInt(GlobalConstants.PREF_VAULT_USER_AGE, userDto.getAge()).commit();
        //pref.edit().putString(GlobalConstants.PREF_JOIN_MAIL_CHIMP, userDto.getIsRegisteredUser()).commit();

    }

    public User getUserData() {
        User userDto = new User();
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        userDto.setUserID(pref.getLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0));
        userDto.setUsername(pref.getString(GlobalConstants.PREF_VAULT_USER_NAME, ""));
        userDto.setEmailID(pref.getString(GlobalConstants.PREF_VAULT_USER_EMAIL, ""));
        userDto.setFname(pref.getString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, ""));
        userDto.setLname(pref.getString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, ""));
        userDto.setBiotext(pref.getString(GlobalConstants.PREF_VAULT_USER_BIO_TEXT, ""));
        userDto.setGender(pref.getString(GlobalConstants.PREF_VAULT_USER_GENDER, ""));
        userDto.setAge(pref.getInt(GlobalConstants.PREF_VAULT_USER_AGE, 0));
        userDto.setImageurl(pref.getString(GlobalConstants.PREF_VAULT_USER_IMAGE_URL, ""));
        userDto.setFlagStatus(pref.getString(GlobalConstants.PREF_VAULT_USER_FLAG_STATUS, ""));
        userDto.setPasswd(pref.getString(GlobalConstants.PREF_VAULT_USER_PASSWORD, ""));
        userDto.setAppID(GlobalConstants.APP_ID);
        //userDto.setIsRegisteredUser(pref.getString(GlobalConstants.PREF_JOIN_MAIL_CHIMP, ""));
        return userDto;
    }

    public void updateUserData(String Username, String FName,String LName, String BioText, String ImageUrl){
        // update 5 fields in the preferences Username, FName, LName, BioText, ImageUrl
        SharedPreferences pref = AppController.getInstance().getApplication().getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_NAME, Username).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_FIRST_NAME, FName).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_LAST_NAME, LName).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_BIO_TEXT, BioText).commit();
        pref.edit().putString(GlobalConstants.PREF_VAULT_USER_IMAGE_URL, ImageUrl).commit();
    }


    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }
}
