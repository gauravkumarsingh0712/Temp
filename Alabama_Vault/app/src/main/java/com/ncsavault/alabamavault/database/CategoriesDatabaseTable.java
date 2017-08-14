package com.ncsavault.alabamavault.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ncsavault.alabamavault.dto.CatagoriesTabDao;


import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 14/08/17.
 */

public class CategoriesDatabaseTable {

    public static final String CATEGORIES_DATA_TABLE = "categories_data_table";
    //Primary Key Column
    public static final String KEY_ID = "id";

    //Playlist Columns
    public static final String KEY_CATEGEROIES_ID = "cateroies_id";
    public static final String KEY_CATEGORIES_NAME = "categories_name";
    public static final String KEY_CATEGORIES_INDEX_POSITION = "categories_index_position";
    public static final String KEY_CATEGORIES_URL = "categories_url";


    public static final String CREATE_CATEGORIES = "CREATE TABLE "
            + CATEGORIES_DATA_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_CATEGEROIES_ID + " INTEGER," + KEY_CATEGORIES_NAME
            + " TEXT," + KEY_CATEGORIES_INDEX_POSITION + " INTEGER,"
            + KEY_CATEGORIES_URL  + " TEXT )";

    public static void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_CATEGORIES);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_DATA_TABLE);
        onCreate(database);
    }

    private static CategoriesDatabaseTable sInstance;

    public static synchronized CategoriesDatabaseTable getInstance() {
        if (sInstance == null) {
            sInstance = new CategoriesDatabaseTable();
        }
        return sInstance;
    }

    public void insertCategoriesTabData(ArrayList<CatagoriesTabDao> catagoriesTabDaoArrayList, SQLiteDatabase database){
        try {
            database.enableWriteAheadLogging();
            ContentValues categoriesListValues;

            for (CatagoriesTabDao catagoriesTabDao : catagoriesTabDaoArrayList) {
                categoriesListValues = new ContentValues();
                categoriesListValues.put(KEY_CATEGEROIES_ID, catagoriesTabDao.getCategoriesId());
                categoriesListValues.put(KEY_CATEGORIES_NAME, catagoriesTabDao.getCategoriesName());
                categoriesListValues.put(KEY_CATEGORIES_INDEX_POSITION, catagoriesTabDao.getIndex_position());
                categoriesListValues.put(KEY_CATEGORIES_URL, catagoriesTabDao.getCategoriesUrl());

                database.insert(CATEGORIES_DATA_TABLE, null, categoriesListValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CatagoriesTabDao> getAllLocalCategoriesData(SQLiteDatabase database){
        try {
            ArrayList<CatagoriesTabDao> catagoriesTabDaoArrayList = new ArrayList<>();
            database.enableWriteAheadLogging();
            String selectQuery = "SELECT * FROM "+CATEGORIES_DATA_TABLE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            CatagoriesTabDao categoriesTabDTO = null;
            if(cursor != null)
                if (cursor.moveToFirst()) {
                    do {
                        categoriesTabDTO = new CatagoriesTabDao();
                        categoriesTabDTO.setCategoriesId(cursor.getLong(cursor.getColumnIndex(KEY_CATEGEROIES_ID)));
                        categoriesTabDTO.setCategoriesName(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_NAME)));
                        categoriesTabDTO.setIndex_position(cursor.getLong(cursor.getColumnIndex(KEY_CATEGORIES_INDEX_POSITION)));
                        categoriesTabDTO.setCategoriesUrl(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIES_URL)));

                        catagoriesTabDaoArrayList.add(categoriesTabDTO);
                    }while (cursor.moveToNext());
                }

            cursor.close();
            return catagoriesTabDaoArrayList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeAllCategoriesTabData(SQLiteDatabase database){
        try {
            database.enableWriteAheadLogging();
            database.execSQL("DELETE FROM " + CATEGORIES_DATA_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
