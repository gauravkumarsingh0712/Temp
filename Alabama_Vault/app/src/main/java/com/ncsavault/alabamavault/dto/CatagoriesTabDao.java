package com.ncsavault.alabamavault.dto;

/**
 * Created by gauravkumar.singh on 8/10/2017.
 */

public class CatagoriesTabDao {

    long CategoriesId;
    String CategoriesName;
    String CategoriesUrl;
    long index_position;

    public long getCategoriesId() {
        return CategoriesId;
    }

    public void setCategoriesId(long categoriesId) {
        CategoriesId = categoriesId;
    }

    public String getCategoriesName() {
        return CategoriesName;
    }

    public void setCategoriesName(String categoriesName) {
        CategoriesName = categoriesName;
    }

    public String getCategoriesUrl() {
        return CategoriesUrl;
    }

    public void setCategoriesUrl(String categoriesUrl) {
        CategoriesUrl = categoriesUrl;
    }

    public long getIndex_position() {
        return index_position;
    }

    public void setIndex_position(long index_position) {
        this.index_position = index_position;
    }
}
