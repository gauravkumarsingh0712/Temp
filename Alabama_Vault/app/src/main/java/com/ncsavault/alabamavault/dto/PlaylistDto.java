package com.ncsavault.alabamavault.dto;

/**
 * Created by gauravkumar.singh on 8/11/2017.
 */

public class PlaylistDto {

    public long playlistId;
    public String playlistName;
    public String playlistType;
    public String playlistReferenceId;
    public String playlistThumbnailUrl;
    public String playlistShortDescription;
    public String playlistLongDescription;
    public String playlistTags;

    public long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(long playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistType() {
        return playlistType;
    }

    public void setPlaylistType(String playlistType) {
        this.playlistType = playlistType;
    }

    public String getPlaylistReferenceId() {
        return playlistReferenceId;
    }

    public void setPlaylistReferenceId(String playlistReferenceId) {
        this.playlistReferenceId = playlistReferenceId;
    }

    public String getPlaylistThumbnailUrl() {
        return playlistThumbnailUrl;
    }

    public void setPlaylistThumbnailUrl(String playlistThumbnailUrl) {
        this.playlistThumbnailUrl = playlistThumbnailUrl;
    }

    public String getPlaylistShortDescription() {
        return playlistShortDescription;
    }

    public void setPlaylistShortDescription(String playlistShortDescription) {
        this.playlistShortDescription = playlistShortDescription;
    }

    public String getPlaylistLongDescription() {
        return playlistLongDescription;
    }

    public void setPlaylistLongDescription(String playlistLongDescription) {
        this.playlistLongDescription = playlistLongDescription;
    }

    public String getPlaylistTags() {
        return playlistTags;
    }

    public void setPlaylistTags(String playlistTags) {
        this.playlistTags = playlistTags;
    }

}
