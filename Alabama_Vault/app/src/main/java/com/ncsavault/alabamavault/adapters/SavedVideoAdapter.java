package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class SavedVideoAdapter extends RecyclerView.Adapter<SavedVideoAdapter.SavedVideoViewHolder> {

private Context mContext;

    public SavedVideoAdapter(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public SavedVideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.saved_video_layout_adapter, parent, false);
        SavedVideoViewHolder viewHolder = new SavedVideoViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SavedVideoViewHolder holder, int position) {

     //   holder.videoNametextView.setText("Video Name");

    }



    public static class SavedVideoViewHolder extends RecyclerView.ViewHolder {

        ImageView videoImageView;
        TextView videoNametextView;

        public SavedVideoViewHolder(View view) {
            super(view);
//            videoImageView = (ImageView) view.findViewById(R.id.video_imageView);
//            videoNametextView = (TextView) view.findViewById(R.id.video_name_text);

        }
    }
}
