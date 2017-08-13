package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.PlaylistDto;
import com.ncsavault.alabamavault.dto.TopTenVideoDto;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravkumar.singh on 14/08/17.
 */
public class PlaylistDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<TopTenVideoDto> albumList;
    public static final int TYPE_LIST_DATA = 2;
    public static final int TYPE_AD = 3;
    private ArrayList<PlaylistDto> mPlaylistDtoArrayList = new ArrayList<>();
    ImageLoader imageLoader;
    public DisplayImageOptions options;
    PlaylistDataClickListener mPlaylistDataClickListener;

    public interface PlaylistDataClickListener
    {
        void onClick(MyViewHolder viewHolder,long playlistId);
    }

    public PlaylistDataAdapter(Context mContext, PlaylistDataClickListener playlistDataClickListener,
                               ArrayList<PlaylistDto> playlistDtoArrayList) {
        this.mContext = mContext;
        this.mPlaylistDtoArrayList = playlistDtoArrayList;
        mPlaylistDataClickListener = playlistDataClickListener;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader = AppController.getInstance().getImageLoader();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mVideoNumber, playlistName;
        public ImageView thumbnail;
        ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            playlistName = (TextView) view.findViewById(R.id.video_name);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
            } else {
                System.out.println("progress bar not showing ");
                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
            }
        }
    }

    public class NativeAdsViewHolder extends RecyclerView.ViewHolder{

        public AdView adView;

        public NativeAdsViewHolder (View itemView) {
            super(itemView);
            adView = (AdView) itemView.findViewById(R.id.adView);
        }

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;
        if(viewType== TYPE_LIST_DATA) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_card, parent, false);

            return new MyViewHolder(itemView);
        }else
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.native_inline_ads_small_scrren_layout, parent, false);

            return new NativeAdsViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position+1) % 5 == 0)
            return TYPE_AD;
        return TYPE_LIST_DATA;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_LIST_DATA:
                displayPlaylistData(holder,position);
                break;
            case TYPE_AD:
                adMobBannerAdvertising(holder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPlaylistDtoArrayList.size();
    }

    private void displayPlaylistData(RecyclerView.ViewHolder holder,int position)
    {
        final MyViewHolder viewHolder = (MyViewHolder)holder;
        String playlistImageUrl = mPlaylistDtoArrayList.get(position).getPlaylistThumbnailUrl();
        String playlistName = mPlaylistDtoArrayList.get(position).getPlaylistName();
        long playlistId = mPlaylistDtoArrayList.get(position).getPlaylistId();


        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(playlistImageUrl,
                viewHolder.thumbnail, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                        viewHolder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                    }
                });



        viewHolder.playlistName.setText(playlistName);
        mPlaylistDataClickListener.onClick(viewHolder,playlistId);

    }

    public void adMobBannerAdvertising(RecyclerView.ViewHolder holder) {

//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        NativeAdsViewHolder vhHeader = (NativeAdsViewHolder)holder;
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("20B52AAB529851184340334B73A36E8B")
                .build();
        vhHeader.adView.loadAd(request);
        // Load the Native Express ad.
    }

}
