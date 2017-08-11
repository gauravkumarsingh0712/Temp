package com.ncsavault.alabamavault.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ncsavault.alabamavault.R;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 5/4/2017.
 */

public class RelativeVideosAdapter extends BaseAdapter {

    private ArrayList mData;
    private Context mContext;

    public RelativeVideosAdapter(Context context, ArrayList data) {
        mContext = context;
        mData = data;

    }

    @Override
    public int getCount() {
        return mData.size();
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
        RelativeVideosAdapter.ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.relative_videos_layout, parent, false);
            holder = new RelativeVideosAdapter.ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.textView12);
            holder.image = (ImageView) row.findViewById(R.id.imageView11);
            row.setTag(holder);
        } else {
            holder = (RelativeVideosAdapter.ViewHolder) row.getTag();
        }


        holder.imageTitle.setText("VIDEO NAME");
        Glide.with(mContext)
                .load(mData.get(position))
                .into(holder.image);
        return row;
    }

}