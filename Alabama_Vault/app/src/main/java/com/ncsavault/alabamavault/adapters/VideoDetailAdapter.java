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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.controllers.AppController;

import com.ncsavault.alabamavault.database.VaultDatabaseHelper;
import com.ncsavault.alabamavault.dto.VideoDTO;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by gauravkumar.singh on 14/08/17.
 */

public class VideoDetailAdapter extends RecyclerView.Adapter<VideoDetailAdapter.VideoViewHolder> {

    private Context mContext;
    private ArrayList<VideoDTO> mVideoDtoArrayList = new ArrayList<>();
    ImageLoader imageLoader;
    public DisplayImageOptions options;
    private VideoClickListener mVideoClickListener;

    public interface VideoClickListener
    {
        void onClick(VideoDetailAdapter.VideoViewHolder videoViewHolder,int position);
    }

    public VideoDetailAdapter(Context context,ArrayList<VideoDTO> videoDtoArrayList,
                              VideoClickListener videoClickListener) {
        super();
        mContext = context;
        mVideoDtoArrayList = videoDtoArrayList;
        mVideoClickListener = videoClickListener;


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
        return mVideoDtoArrayList.size();
    }

    @Override
    public VideoDetailAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_video_layout_adapter, parent, false);
        VideoViewHolder viewHolder = new VideoViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VideoDetailAdapter.VideoViewHolder viewHolder, int position) {

        //   holder.videoNametextView.setText("Video Name");
        VideoDTO newVideoDto = mVideoDtoArrayList.get(position);

        String videoImageUrl = newVideoDto.getVideoStillUrl();
        String videoName = newVideoDto.getVideoName();
        String videDescription = newVideoDto.getVideoShortDescription();
        long videoDuration = newVideoDto.getVideoDuration();


        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(videoImageUrl,
                viewHolder.videoImageView, options, new ImageLoadingListener() {
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

        viewHolder.videoNameTextView.setText(videoName);
        viewHolder.videoDescriptionTextView.setText(videDescription);

        if(videoDuration != 0) {
            viewHolder.videoDurationTextView.setText(convertSecondsToHMmSs(videoDuration));
        }

        if (newVideoDto.isVideoIsFavorite()) {
            viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
        } else {
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
        }

        if (VaultDatabaseHelper.getInstance(mContext.getApplicationContext()).
                isFavorite(newVideoDto.getVideoId())) {
            viewHolder.savedVideoImageView.setImageResource(R.drawable.saved_video_img);
        } else {
            viewHolder.savedVideoImageView.setImageResource(R.drawable.video_save);
        }

        mVideoClickListener.onClick(viewHolder,position);

    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {

        public ImageView videoImageView,savedVideoImageView;
        TextView videoNameTextView,videoDescriptionTextView,videoDurationTextView;
        private ProgressBar progressBar;
        public RelativeLayout videoRelativeLayout;
        public LinearLayout mLayoutSavedImage;

        public VideoViewHolder(View view) {
            super(view);
            videoImageView = (ImageView) view.findViewById(R.id.imgVideoThumbNail);
            savedVideoImageView = (ImageView) view.findViewById(R.id.save_video_image);
            videoNameTextView = (TextView) view.findViewById(R.id.tv_video_name);
            videoDescriptionTextView = (TextView) view.findViewById(R.id.tv_video_description);
            videoDurationTextView = (TextView) view.findViewById(R.id.tv_video_duration);
            progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
            mLayoutSavedImage = (LinearLayout) view.findViewById(R.id.layout_saved_image);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                progressBar.setIndeterminateDrawable(mContext.getResources().getDrawable(R.drawable
                        .circle_progress_bar_lower));
            } else {
                System.out.println("progress bar not showing ");
                progressBar.setIndeterminateDrawable(ResourcesCompat.getDrawable(mContext.getResources(),
                        R.drawable.progress_large_material, null));
            }

            videoRelativeLayout = (RelativeLayout)view.findViewById(R.id.save_video_main_layout);

        }
    }

    public String convertSecondsToHMmSs(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        String duration = "";
        if (minutes > 60) {
            // convert the minutes to Hours:Minutes:Seconds
        } else {
            /*
             * if (minutes < 10) duration += "0" + minutes; else duration +=
			 * minutes;
			 */
            duration += minutes;
            if (seconds < 10)
                duration += ":0" + seconds;
            else
                duration += ":" + seconds;
        }
        return duration;
    }
}
