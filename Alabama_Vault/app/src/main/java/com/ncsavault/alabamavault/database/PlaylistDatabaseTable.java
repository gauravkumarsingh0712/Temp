package com.ncsavault.alabamavault.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.dto.PlaylistDto;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 14/08/17.
 */

public class PlaylistDatabaseTable {

    public static final String PLAYLIST_DATA_TABLE = "playlist_data_table";
    //Primary Key Column
    public static final String KEY_ID = "id";

    //Playlist Columns
    public static final String KEY_PLAYLIST_NAME = "playlist_name";
    public static final String KEY_PLAYLIST_ID = "playlist_id";
    public static final String KEY_PLAYLIST_THUMB_URL = "playlist_thumbnail_url";
    public static final String KEY_PLAYLIST_SHORT_DESC = "playlist_short_desc";
    public static final String KEY_PLAYLIST_LONG_DESC = "playlist_long_desc";
    public static final String KEY_PLAYLIST_TAGS = "playlist_tags";
    public static final String KEY_PLAYLIST_REFERENCE_ID = "playlist_reference_id";


    public static final String CREATE_PLAYLIST = "CREATE TABLE "
            + PLAYLIST_DATA_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_PLAYLIST_NAME + " TEXT," + KEY_PLAYLIST_ID + " INTEGER," + KEY_PLAYLIST_THUMB_URL
            + " TEXT," + KEY_PLAYLIST_SHORT_DESC + " TEXT," + KEY_PLAYLIST_LONG_DESC + " TEXT," +
            KEY_PLAYLIST_TAGS + " TEXT," + KEY_PLAYLIST_REFERENCE_ID + " TEXT )";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PLAYLIST);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + PLAYLIST_DATA_TABLE);
        onCreate(database);
    }

    private static PlaylistDatabaseTable sInstance;

    public static synchronized PlaylistDatabaseTable getInstance() {
        if (sInstance == null) {
            sInstance = new PlaylistDatabaseTable();
        }
        return sInstance;
    }

    public void insertPlaylistTabData(ArrayList<PlaylistDto> playlistDtoArrayList, SQLiteDatabase database) {
        try {
            database.enableWriteAheadLogging();
            ContentValues initialValues;

            for (PlaylistDto playlistDto : playlistDtoArrayList) {
                initialValues = new ContentValues();
                initialValues.put(VideoTable.KEY_PLAYLIST_NAME, playlistDto.getPlaylistName());
                initialValues.put(VideoTable.KEY_PLAYLIST_ID, playlistDto.getPlaylistId());
                initialValues.put(VideoTable.KEY_PLAYLIST_THUMB_URL, playlistDto.getPlaylistThumbnailUrl());
                initialValues.put(VideoTable.KEY_PLAYLIST_SHORT_DESC, playlistDto.getPlaylistShortDescription());
                initialValues.put(VideoTable.KEY_PLAYLIST_LONG_DESC, playlistDto.getPlaylistLongDescription());
                initialValues.put(VideoTable.KEY_PLAYLIST_TAGS, playlistDto.getPlaylistTags());
                initialValues.put(VideoTable.KEY_PLAYLIST_REFERENCE_ID, playlistDto.getPlaylistReferenceId());

                database.insert(PLAYLIST_DATA_TABLE, null, initialValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PlaylistDto> getAllLocalPlaylistData(SQLiteDatabase database) {
        try {
            ArrayList<PlaylistDto> playlistDaoArrayList = new ArrayList<>();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM " + PLAYLIST_DATA_TABLE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            PlaylistDto playlistDto = null;
            if (cursor != null)
                if (cursor.moveToFirst()) {
                    do {
                        playlistDto = new PlaylistDto();
                        playlistDto.setPlaylistName(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_NAME)));
                        playlistDto.setPlaylistId(cursor.getLong(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_ID)));
                        playlistDto.setPlaylistThumbnailUrl(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_THUMB_URL)));
                        playlistDto.setPlaylistShortDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_SHORT_DESC)));
                        playlistDto.setPlaylistLongDescription(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_LONG_DESC)));
                        playlistDto.setPlaylistTags(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_TAGS)));
                        playlistDto.setPlaylistReferenceId(cursor.getString(cursor.getColumnIndex(VideoTable.KEY_PLAYLIST_REFERENCE_ID)));

                        playlistDaoArrayList.add(playlistDto);
                    } while (cursor.moveToNext());
                }

            cursor.close();
            return playlistDaoArrayList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new ArrayList<PlaylistDto>();
        }
    }

    public void removeAllPlaylistTabData(SQLiteDatabase database) {
        try {
            database.enableWriteAheadLogging();
            database.execSQL("DELETE FROM " + PLAYLIST_DATA_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

