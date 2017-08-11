package com.ncsavault.alabamavault.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.ncsavault.alabamavault.views.HomeScreen;
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


    public FilterSubtypesAdapter(Activity mContext, List<VideoDTO> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
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

        } else {

            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.home_screen_menu_item, parent, false);

            return new MyViewHolder(view);
        }
    }


    /**
     * The {@link RecyclerViewAdapter.MenuItemViewHolder} class.
     * Provides a reference to each view in the menu item view.
     */
    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView menuItemName;
        private ImageView menuItemImage;

        MenuItemViewHolder(View view) {
            super(view);
//            menuItemImage = (ImageView) view.findViewById(R.id.menu_item_image);
//            menuItemName = (TextView) view.findViewById(R.id.menu_item_name);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_LOW:

                try {
                    final MyViewHolder vhHeader = (MyViewHolder) holder;
//
                    if (albumList.size() > 0) {

                        VideoDTO videoDTO = (VideoDTO) albumList.get(position);
                        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(videoDTO.getVideoStillUrl(),
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

                    }

//                    vhHeader.videoImage.

//                    ArrayList<VideoDTO> dataList = LocalModel.getInstance().getDataList();
//                    VideoDTO videoDTO = dataList.get(position);
//                    vhHeader.mVideoName.setText(dataList.get(position).getVideoName());
//                    ImageView image = vhHeader.videoImage;
//                    final String imageUrl = dataList.get(position).getVideoStillUrl();
//                    LocalModel.getInstance().setVideoDTO(videoDTO);
//
                    vhHeader.videoImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, vhHeader.videoImage, "contact_details");
////                                intent.putExtra("contact_details", imageUrl);
////                                ActivityCompat.startActivity(mContext,intent, options.toBundle());
//                                PlayerProfileScreen.launch(mContext, vhHeader.videoImage, imageUrl);
//
//                            } else {
//                                Intent intent = new Intent(mContext,PlayerProfileScreen.class);
//                                mContext.startActivity(intent);
//                            }
                        }
                    });
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

                break;
        }

//        holder.mVideoNumber.setText(album.getVideoNumer());
//        holder.mVideoName.setText(album.getName() + " songs");
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

        HorizontalPagerAdapter adapter = new HorizontalPagerAdapter(mContext);
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

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mVideoName;
        public ImageView videoImage;
        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            mVideoName = (TextView) view.findViewById(R.id.video_name);
            videoImage = (ImageView) view.findViewById(R.id.video_image);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateDrawable(AppController.getInstance().getApplication().getResources().getDrawable(R.drawable.circle_progress_bar_lower));
            } else{
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

        public AdView adView;

        public BannerViewHolder(View itemView) {
            super(itemView);
        }

    }

    public void adMobBannerAdvertising(RecyclerView.ViewHolder holder) {

//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        SubtypeViewHolder vhHeader = (SubtypeViewHolder) holder;
        AdRequest request = new AdRequest.Builder()
                .addTestDevice("20B52AAB529851184340334B73A36E8B")
                .build();
        vhHeader.adView.loadAd(request);
        // Load the Native Express ad.
    }

}