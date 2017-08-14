package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.VideoDetailActivity;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;


public class HorizontalPagerAdapter extends PagerAdapter {

    Context context;
    int adapterType = 1;
    public static final int ADAPTER_TYPE_TOP = 1;
    public static final int ADAPTER_TYPE_BOTTOM = 2;
    ArrayList<VideoDTO> trendingVideosList = new ArrayList<>();

    public HorizontalPagerAdapter(Context context, ArrayList<VideoDTO> trendingVideosList) {
        this.context = context;
        this.trendingVideosList = trendingVideosList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cover, null);
        try {

            LinearLayout linMain = (LinearLayout) view.findViewById(R.id.linMain);
            ImageView imageCover = (ImageView) view.findViewById(R.id.imageCover);
            linMain.setTag(position);


            Glide.with(context)
                    .load(trendingVideosList.get(position).getVideoStillUrl())
                    .placeholder(HomeScreen.listItems[position])
                    .into(imageCover);


            imageCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, VideoDetailActivity.class);
                    context.startActivity(intent);
                }
            });

            container.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (trendingVideosList.size() > 0) {
            return trendingVideosList.size();
        }
        return HomeScreen.listItems.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}