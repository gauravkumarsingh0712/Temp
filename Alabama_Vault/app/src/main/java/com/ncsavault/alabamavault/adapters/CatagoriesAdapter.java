package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.CatagoriesTabDao;
import com.ncsavault.alabamavault.fragments.views.PlaylistFragment;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 8/4/2017.
 */

public class CatagoriesAdapter extends RecyclerView.Adapter<CatagoriesAdapter.CatagoriesAdapterViewHolder> {

    public Context mContext;
    private OnClickInterface mOnClickInterface;
    private ArrayList<CatagoriesTabDao> mCatagoriesTabList = new ArrayList<>();
    ImageLoader imageLoader;
    public DisplayImageOptions options;


    public interface OnClickInterface
    {
        void onClick(CatagoriesAdapterViewHolder v,long tabPosition);
    }


    public CatagoriesAdapter(Context context, OnClickInterface onClickInterface,ArrayList<CatagoriesTabDao> CatagoriesTabList) {
        super();
        this.mContext = context;
        mOnClickInterface = onClickInterface;
        mCatagoriesTabList = CatagoriesTabList;

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader = AppController.getInstance().getImageLoader();
    }

    @Override
    public int getItemCount() {
        return mCatagoriesTabList.size();
    }

    @Override
    public CatagoriesAdapter.CatagoriesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catagories_tab_layout, parent, false);
        CatagoriesAdapterViewHolder viewHolder = new CatagoriesAdapterViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CatagoriesAdapterViewHolder viewHolder, int position) {

        //CatagoriesTabDao catagoriesTabDao = mCatagoriesTabList.get(position);
        String catagoriesTabImageUrl = mCatagoriesTabList.get(position).getCategoriesUrl();
        String catagoriesTabName = mCatagoriesTabList.get(position).getCategoriesName();
        long categoriesId = mCatagoriesTabList.get(position).getCategoriesId();


        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(catagoriesTabImageUrl,
                viewHolder.playlistImageView, options, new ImageLoadingListener() {
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

        viewHolder.playlistTabNametextView.setText(catagoriesTabName);

        mOnClickInterface.onClick(viewHolder,categoriesId);
    }



    public class CatagoriesAdapterViewHolder extends RecyclerView.ViewHolder {

        public ImageView playlistImageView;
        TextView playlistTabNametextView;
        private ProgressBar progressBar;

        public CatagoriesAdapterViewHolder(View view) {
            super(view);
            playlistImageView = (ImageView) view.findViewById(R.id.tv_playlist_image);
            playlistTabNametextView = (TextView) view.findViewById(R.id.tv_playlist_name);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable.circle_progress_bar_lower));
            } else {
                System.out.println("progress bar not showing ");
                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.progress_large_material, null));
            }

        }
    }
}

