package com.ncsavault.alabamavault.models;

/**
 * Created by gauravkumar.singh on 7/19/2016.
 */
public class RemoteModel implements IModel {

    /**
     * Instance of Banner Model data
     */
    BannerDataModel bannerDataModel;
    /**
     * Instance of LoginEmail Medel
     */
    LoginEmailModel loginEmailModel;

    /**
     * Instance of Fatch all data Model
     */
    FetchingAllDataModel fetchingAllDataModel;

    /**
     * Instance of FB lOGIN Data
     */
    FBLoginModel fbLoginModel;

    /**
     * Instance of Mailchimp data.
     */
    MailChimpDataModel mailChimpDataModel;

    /**
     * Instance of vault user data model
     */
    UserDataModel userDataModel;

    /**
     * Instance of user profile data model
     */
    UserProfileModel userProfileModel;

    /**
     * Instance of change password screen model
     */
    ChangePasswordModel changePasswordModel;

    /**
     * Instance of contact screen model
     */
    CreateTaskOnAsanaModel createTaskOnAsanaModel;

    /**
     * Instance of video data task model
     */
    VideoDataTaskModel videoDataTaskModel;

    /**
     * Instance of fragment task model
     */
    FragmentDataTaskModel fragmentDataTaskModel;

    /**
     * Instance of login password model
     */
    LoginPasswordModel loginPasswordModel;

    @Override
    public void initialize() {

        // homeModel = new HomeModel();
        bannerDataModel = new BannerDataModel();
        loginEmailModel = new LoginEmailModel();
        fetchingAllDataModel = new FetchingAllDataModel();
        fbLoginModel = new FBLoginModel();
        mailChimpDataModel = new MailChimpDataModel();
        userDataModel = new UserDataModel();
        userProfileModel = new UserProfileModel();
        changePasswordModel = new ChangePasswordModel();
        createTaskOnAsanaModel = new CreateTaskOnAsanaModel();
        videoDataTaskModel = new VideoDataTaskModel();
        fragmentDataTaskModel = new FragmentDataTaskModel();
        loginPasswordModel = new LoginPasswordModel();

    }

    @Override
    public void destroy() {

    }

    public LoginEmailModel getLoginEmailModel() {
        return loginEmailModel;
    }

    public FetchingAllDataModel getFetchingAllDataModel() {
        return fetchingAllDataModel;
    }

    public FBLoginModel getFbLoginModel() {
        return fbLoginModel;
    }

    public BannerDataModel getBannerDataModel() {
        return bannerDataModel;
    }

    public MailChimpDataModel getMailChimpDataModel() {
        return mailChimpDataModel;
    }

    public UserDataModel getUserDataModel() {
        return userDataModel;
    }

    public UserProfileModel getUserProfileModel() {
        return userProfileModel;
    }

    public ChangePasswordModel getChangePasswordModel() {
        return changePasswordModel;
    }

    public CreateTaskOnAsanaModel getCreateTaskOnAsanaModel() {
        return createTaskOnAsanaModel;
    }

    public VideoDataTaskModel getVideoDataTaskModel() {
        return videoDataTaskModel;
    }

    public FragmentDataTaskModel getFragmentDataTaskModel() {
        return fragmentDataTaskModel;
    }

    public LoginPasswordModel getLoginPasswordModel() {
        return loginPasswordModel;
    }


}
