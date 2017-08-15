package com.ncsavault.alabamavault.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.TabBannerDTO;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.fragments.views.VideoDetailFragment;
import com.ncsavault.alabamavault.globalconstants.GlobalConstants;
import com.ncsavault.alabamavault.service.TrendingFeaturedVideoService;
import com.ncsavault.alabamavault.utils.Utils;
import com.ncsavault.alabamavault.views.HomeScreen;
import com.ncsavault.alabamavault.views.LoginEmailActivity;
import com.ncsavault.alabamavault.views.VideoInfoActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import me.crosswall.lib.coverflow.CoverFlow;
import me.crosswall.lib.coverflow.core.PagerContainer;


/**
 * Created by gauravkumar.singh on 5/19/2017.
 */

public class FilterSubtypesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mContext;
    private List<VideoDTO> albumList;
    // Header view type
    private static final int HEADER_VIEW = 0;
    private static final int BANNER_VIEW = 1;
    public static final int TYPE_LOW = 2;
    public static final int TYPE_HIGH = 3;
    // The Native Express ad view type.
    private int mResId;
    private int mNativeResId;
    ImageLoader imageLoader;
    public static DisplayImageOptions options;

    public ArrayList<VideoDTO> trendingVideoList=new ArrayList<>();
    BannerClickListener bannerClickListener;
    AsyncTask<Void, Void, Void> mPostTask;
    private boolean isFavoriteChecked;
    private String postResult;


    public FilterSubtypesAdapter(Activity mContext, List<VideoDTO> albumList, ArrayList<VideoDTO> trendingVideoList, BannerClickListener bannerClickListener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.trendingVideoList=trendingVideoList;
        getScreenDimensions();
//        screenSize  = mContext.getResources().getConfiguration().screenLayout &
//                Configuration.SCREENLAYOUT_SIZE_MASK;
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader = AppController.getInstance().getImageLoader();
        this.bannerClickListener = bannerClickListener;


    }

    public interface BannerClickListener
    {
        void onClick(FilterSubtypesAdapter.BannerViewHolder videoViewHolder,int position);
    }


    class VHHeader extends RecyclerView.ViewHolder {

        private ViewPager pager;
        private TextView topTenText;

        public VHHeader(View itemView) {
            super(itemView);
            PagerContainer pagerContainer = (PagerContainer) itemView.findViewById(R.id.pager_container);
            pagerContainer.setOverlapEnabled(true);

            pager = pagerContainer.getViewPager();
            //pager = (ViewPager) itemView.findViewById(R.id.pager_introduction);
            //  topTenText = (TextView) itemView.findViewById(R.id.top_ten_text_view);

        }

    }

    private int displayHeight = 0, displayWidth = 0;

    public void getScreenDimensions() {

        Point size = new Point();
        WindowManager w = HomeScreen.activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            displayHeight = size.y;
            displayWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            displayHeight = d.getHeight();
            displayWidth = d.getWidth();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == HEADER_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_view, parent, false);

            return new VHHeader(view);
        } else if (viewType == BANNER_VIEW) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_banner_layout, parent, false);

            return new BannerViewHolder(view);
        } else if (viewType == TYPE_HIGH) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.native_inline_ads_small_scrren_layout, parent, false);

            return new SubtypeViewHolder(view);

        } else if (viewType == TYPE_LOW) {

            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.home_screen_menu_item, parent, false);

            return new MyViewHolder(view);
        }

        return null;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_LOW:

                try {
                    final MyViewHolder vhHeader = (MyViewHolder) holder;
//
                   // if (albumList.size() > 0) {

                       final VideoDTO videoDTO = (VideoDTO) albumList.get(position);
                        com.nostra13.universalimageloader.core.ImageLoader.getInstance().
                                displayImage(videoDTO.getVideoStillUrl(),
                                vhHeader.videoImage, options, new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String s, View view) {
                                        vhHeader.progressBar.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                                        vhHeader.progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                        vhHeader.progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {
                                        vhHeader.progressBar.setVisibility(View.GONE);
                                    }
                                });

                        vhHeader.mVideoName.setText(videoDTO.getVideoName());

                    vhHeader.videoImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Utils.isInternetAvailable(mContext)) {
                                if (videoDTO.getVideoLongUrl() != null) {
                                    if (videoDTO.getVideoLongUrl().length() > 0
                                            && !videoDTO.getVideoLongUrl().toLowerCase().equals("none")) {
                                        String videoCategory = GlobalConstants.FEATURED;
                                        Intent intent = new Intent(mContext, VideoInfoActivity.class);
                                        intent.putExtra(GlobalConstants.KEY_CATEGORY, videoCategory);
                                        intent.putExtra(GlobalConstants.VIDEO_OBJ, videoDTO);
                                        GlobalConstants.LIST_FRAGMENT = new VideoDetailFragment();
                                        GlobalConstants.LIST_ITEM_POSITION = position;
                                        mContext.startActivity(intent);
                                        ((HomeScreen)mContext).overridePendingTransition(R.anim.slide_up_video_info,
                                                R.anim.nochange);
                                    } else {
                                        ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    }
                                } else {
                                    ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                }
                            } else {
                                ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
                            }
                        }
                    });

                    vhHeader.savedImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (videoDTO.isVideoIsFavorite() && ((videoDTO
                                    .getVideoLongUrl().length() == 0 || videoDTO.getVideoLongUrl()
                                    .toLowerCase().equals("none")))) {
                                markFavoriteStatus(vhHeader,position);
                            } else {
                                if (videoDTO.getVideoLongUrl().length() > 0 && !videoDTO.getVideoLongUrl().toLowerCase().
                                        equals("none")) {
                                    markFavoriteStatus(vhHeader,position);
                                } else {
                                  ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_INFO_AVAILABLE);
                                    vhHeader.savedImage.setImageResource(R.drawable.video_save);
                                }
                            }

                            notifyDataSetChanged();
                        }
                    });

                   // }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case TYPE_HIGH:
                adMobBannerAdvertising(holder);
                break;
            case HEADER_VIEW:
                setHorizentalPager(holder);
                break;
            case BANNER_VIEW:
                setBanner(holder, position);
                break;
        }

//        holder.mVideoNumber.setText(album.getVideoNumer());
//        holder.mVideoName.setText(album.getName() + " songs");
    }

    private void setBanner(RecyclerView.ViewHolder holder, int position) {
        final BannerViewHolder viewholer = (BannerViewHolder) holder;
        if(AppController.getInstance().getModelFacade().getLocalModel().isBannerActivated()) {
            VideoDTO videoDTO = albumList.get(position);
            viewholer.imageviewBanner.setVisibility(View.VISIBLE);
            viewholer.bannerLayout.setVisibility(View.VISIBLE);

            Glide.with(mContext)
                    .load(videoDTO.getVideoStillUrl())
                    .placeholder(R.drawable.alabama_vault_logo)
                    .into(viewholer.imageviewBanner);

            bannerClickListener.onClick((BannerViewHolder) holder, position);
        }else
        {
            viewholer.imageviewBanner.setVisibility(View.GONE);
            viewholer.bannerLayout.setVisibility(View.GONE);
        }

    }


//    @Override
//    public int getItemViewType(int position) {
//        if(isPositionHeader(position))
//        {
//            return TabletScreen.TYPE_LOW;
//        }
//        return (position % ITEMS_PER == 0  ? TabletScreen.TYPE_HIGH : TabletScreen.TYPE_LOW);
//    }

    @Override
    public int getItemViewType(int position) {
//         if(isPositionHeader(position))
//        {
//            return TabletScreen.TYPE_LOW;
//        }
        if (isPositionHeader(position))
            return HEADER_VIEW;

        if (isPositionBanner(position))
            return BANNER_VIEW;

        if ((position+1) % 3 == 0)
            return TYPE_HIGH;

        return TYPE_LOW;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionBanner(int position) {
        return position == 1;
    }

    private void setHorizentalPager(final RecyclerView.ViewHolder holder) {
        final VHHeader vhHeader = (VHHeader) holder;
//        vhHeader.pager.setClipChildren(false);
//        vhHeader.pager.setOffscreenPageLimit(3);
        float margin = 0;
        int screenSize = mContext.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            margin = (float) (-displayWidth / 3.5);
        } else {
            margin = (float) (-displayWidth / 3.9);
        }

        new CoverFlow.Builder().with(vhHeader.pager)
                .scale(0.2f)
                .pagerMargin(margin)
                .spaceSize(0f)
                .build();
        System.out.println("ddfkjdfj :" + vhHeader.pager.getWidth());

//               int screenSize  = mContext.getResources().getConfiguration().screenLayout &
//                Configuration.SCREENLAYOUT_SIZE_MASK;
//        if(screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL)
//        {
//            displayWidth = (int) ((double)displayWidth/3.3);
//        }else
//        {
//            displayWidth = (int) ((double)displayWidth/2.35);
//        }

        // vhHeader.pager.setPageTransformer(false, new CarouselEffectTransformer(mContext,displayWidth));

        HorizontalPagerAdapter adapter = new HorizontalPagerAdapter(mContext, trendingVideoList);
        vhHeader.pager.setAdapter(adapter);
        vhHeader.pager.setOffscreenPageLimit(adapter.getCount());
        vhHeader.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                }
            }
        });
    }


    @Override
    public int getItemCount() {

        return albumList.size();
    }


    protected class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mVideoName;
        public ImageView videoImage,savedImage;
        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            mVideoName = (TextView) view.findViewById(R.id.video_name);
            videoImage = (ImageView) view.findViewById(R.id.video_image);
            savedImage = (ImageView) view.findViewById(R.id.saved_imageview);

            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateDrawable(AppController.getInstance().getApplication().getResources().getDrawable(R.drawable.circle_progress_bar_lower));
            } else {
                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(AppController.getInstance().getApplication().getResources(), R.drawable.progress_large_material, null));
            }
        }
    }


    public class SubtypeViewHolder extends RecyclerView.ViewHolder {

        public AdView adView;

        public SubtypeViewHolder(View itemView) {
            super(itemView);
            adView = (AdView) itemView.findViewById(R.id.adView);
        }

    }

    public class BannerViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageviewBanner;
        public LinearLayout bannerLayout;

        public BannerViewHolder(View itemView) {
            super(itemView);
            imageviewBanner = (ImageView) itemView.findViewById(R.id.imageview_banner);
            bannerLayout = (LinearLayout) itemView.findViewById(R.id.banner_layout);
        }

    }

    public void adMobBannerAdvertising(RecyclerView.ViewHolder holder) {

        SubtypeViewHolder vhHeader = (SubtypeViewHolder) holder;
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("20B52AAB529851184340334B73A36E8B")
                .build();
        vhHeader.adView.loadAd(request);
        // Load the Native Express ad.
    }




    public void markFavoriteStatus(final MyViewHolder viewHolder, final int pos) {
        if (Utils.isInternetAvailable(mContext)) {
            if (AppController.getInstance().getModelFacade().getLocalModel().getUserId() ==
                    GlobalConstants.DEFAULT_USER_ID) {
                viewHolder.savedImage.setBackgroundResource(R.drawable.video_save);
                showConfirmLoginDialog(GlobalConstants.LOGIN_MESSAGE);
            } else {
                System.out.println("favorite position : " + pos);
                if (albumList.get(pos).isVideoIsFavorite()) {
                    isFavoriteChecked = false;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (0, albumList.get(pos).getVideoId());
                    albumList.get(pos).setVideoIsFavorite(false);
                    viewHolder.savedImage.setImageResource(R.drawable.video_save);
                } else {
                    isFavoriteChecked = true;
                    VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                            (1, albumList.get(pos).getVideoId());
                    albumList.get(pos).setVideoIsFavorite(true);
                    viewHolder.savedImage.setImageResource(R.drawable.saved_video_img);
                }

                mPostTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            postResult = AppController.getInstance().getServiceManager().getVaultService().
                                    postFavoriteStatus(AppController.getInstance().getModelFacade().getLocalModel()
                                                    .getUserId(), albumList.get(pos).getVideoId(),
                                            albumList.get(pos).getPlaylistId(),
                                            isFavoriteChecked);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {
                            System.out.println("favorite position 111 : " + pos);
                            if (isFavoriteChecked) {
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag(1,
                                        albumList.get(pos).getVideoId());
                                // firebase analytics favoride video
//                                params.putString(FirebaseAnalytics.Param.ITEM_ID, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.ITEM_NAME, arrayListVideoDTOs.get(pos).getVideoName());
//                                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video_favorite");
//                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, params);

                            }else{
                                VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).setFavoriteFlag
                                        (0, albumList.get(pos).getVideoId());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                mPostTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else {
            ((HomeScreen) mContext).showToastMessage(GlobalConstants.MSG_NO_CONNECTION);
            viewHolder.savedImage.setBackgroundResource(R.drawable.video_save);
        }
    }

    public void showConfirmLoginDialog(String message) {
        AlertDialog alertDialog = null;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder
                .setMessage(message);
        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        mContext.stopService(new Intent(mContext, TrendingFeaturedVideoService.class));

                        VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).removeAllRecords();

                        SharedPreferences prefs = mContext.getSharedPreferences(GlobalConstants.PREF_PACKAGE_NAME,
                                Context.MODE_PRIVATE);
                        prefs.edit().putLong(GlobalConstants.PREF_VAULT_USER_ID_LONG, 0).commit();
//                        prefs.edit().putBoolean(GlobalConstants.PREF_PULL_OPTION_HEADER, false).commit();

                        Intent intent = new Intent(mContext, LoginEmailActivity.class);
                        mContext.startActivity(intent);
                        ((HomeScreen)mContext).finish();
//                        context.finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        nbutton.setAllCaps(false);
        nbutton.setTextColor(mContext.getResources().getColor(R.color.apptheme_color));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        pbutton.setTextColor(mContext.getResources().getColor(R.color.apptheme_color));
        pbutton.setAllCaps(false);
    }

}