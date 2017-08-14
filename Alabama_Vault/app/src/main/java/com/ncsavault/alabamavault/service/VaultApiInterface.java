package com.ncsavault.alabamavault.service;



import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.dto.MailChimpData;

import com.ncsavault.alabamavault.dto.PlaylistDto;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.User;
import com.ncsavault.alabamavault.dto.VideoDTO;

import java.util.ArrayList;

public interface VaultApiInterface {

    public ArrayList<VideoDTO> getVideosListFromServer(String url) throws BusinessException;
    public VideoDTO getVideosDataFromServer(String url) throws BusinessException;
    public String postFavoriteStatus(long userId, long videoId, long playListId, boolean status) throws BusinessException;
    public String postSharingInfo(String videoId) throws BusinessException;
    public String validateEmail(String emailId) throws BusinessException;
    public String validateUsername(String userName) throws BusinessException;
    public String postUserData(User user) throws BusinessException;
    public String validateUserCredentials(String emailId, String password) throws BusinessException;
    public String getUserData(long userId, String emailId) throws BusinessException;
    public String updateUserData(User updatedUser) throws BusinessException;

    public String validateSocialLogin(String emailId, String flagStatus) throws BusinessException;
    public String changeUserPassword(long emailId, String oldPassword, String newPassword) throws BusinessException;

    public String sendPushNotificationRegistration(String url, String regId, String deviceId, boolean isAllowed) throws BusinessException;
    public String createTaskOnAsana(String nameAndEmail, String taskNotes, String type) throws BusinessException;
    public String createTagForAsanaTask(String tagId, String taskId) throws BusinessException;

    public ArrayList<TabBannerDTO> getAllTabBannerData() throws BusinessException;
    public TabBannerDTO getTabBannerDataById(long bannerId, String tabName, long tabId) throws BusinessException;

    public String postMailChimpData(MailChimpData mailChimpData) throws BusinessException;
    public String forgotPassword(String emailId,boolean isResetPassword) throws BusinessException;
    public String confirmPassword(long userID, String newPass) throws BusinessException;
    public String socialLoginExits(String tokenId,String email) throws BusinessException;
    public ArrayList<CatagoriesTabDao> getCategoriesData(String url) throws BusinessException;
    public ArrayList<PlaylistDto> getPlaylistData(String url) throws BusinessException;
    public ArrayList<VideoDTO> getNewVideoData(String url) throws BusinessException;

    public ArrayList<VideoDTO> getTrendingVideoData(String url) throws BusinessException;

}
