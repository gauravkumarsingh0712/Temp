package com.ncsavault.alabamavault.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;
import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 6/5/2017.
 */

public class UserSettingAdapter extends BaseAdapter {

    private ArrayList mData;
    private Context mContext;

    public UserSettingAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.saved_notification_messages_adapter, parent, false);
            holder = new ViewHolder();
//            holder.imageTitle = (TextView) row.findViewById(R.id.textView5);
//            holder.image = (ImageView) row.findViewById(R.id.imageView6);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        return row;
    }

}
