package com.ncsavault.alabamavault.database;

import android.database.sqlite.SQLiteDatabase;


public class TrendingVideoTable {

    public static final String TRENDING_VIDEO_TABLE = "trending_video_data";

    public static final String KEY_ID = "id";
    public static final String KEY_VIDEO_ID = "video_id";
    public static final String KEY_VIDEO_NAME = "video_name";
    public static final String KEY_VIDEO_SHORT_DESC = "video_short_desc";
    public static final String KEY_VIDEO_LONG_DESC = "video_long_desc";
    public static final String KEY_VIDEO_SHORT_URL = "video_short_url";
    public static final String KEY_VIDEO_LONG_URL = "video_long_url";
    public static final String KEY_VIDEO_THUMB_URL = "video_thumbnail_url";
    public static final String KEY_VIDEO_STILL_URL = "video_still_url";
    public static final String KEY_VIDEO_COVER_URL = "video_cover_url";
    public static final String KEY_VIDEO_WIDE_STILL_URL = "video_wide_still_url";
    public static final String KEY_VIDEO_BADGE_URL = "video_badge_url";
    public static final String KEY_VIDEO_DURATION = "video_duration";
    public static final String KEY_VIDEO_TAGS = "video_tags";
    public static final String KEY_VIDEO_IS_FAVORITE = "video_is_favorite";
    public static final String KEY_VIDEO_INDEX = "video_index";
    public static final String KEY_VIDEO_SOCIAL_URL = "video_social_url";


    public static final String CREATE_TRENDING_VIDEOS_TABLE = "CREATE TABLE "
            + TRENDING_VIDEO_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_VIDEO_ID + " INTEGER," + KEY_VIDEO_NAME
            + " TEXT," + KEY_VIDEO_SHORT_DESC + " TEXT," + KEY_VIDEO_LONG_DESC
            + " TEXT," + KEY_VIDEO_SHORT_URL + " TEXT," + KEY_VIDEO_LONG_URL + " TEXT,"
            + KEY_VIDEO_THUMB_URL + " TEXT," + KEY_VIDEO_STILL_URL + " TEXT," + KEY_VIDEO_COVER_URL + " TEXT," + KEY_VIDEO_WIDE_STILL_URL + " TEXT," + KEY_VIDEO_BADGE_URL + " TEXT," + KEY_VIDEO_DURATION
            + " INTEGER," + KEY_VIDEO_TAGS + " TEXT," + KEY_VIDEO_IS_FAVORITE + " INTEGER," + KEY_VIDEO_INDEX + " INTEGER,"
            +  " TEXT," + KEY_VIDEO_SOCIAL_URL + " TEXT" + ")";

    public static final String selectAllVideos = "SELECT * FROM "
            + TRENDING_VIDEO_TABLE ;



    public static void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TRENDING_VIDEOS_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + CREATE_TRENDING_VIDEOS_TABLE);
        onCreate(database);
    }
}
