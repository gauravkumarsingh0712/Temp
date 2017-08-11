package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ncsavault.alabamavault.R;

import java.util.ArrayList;

/**
 * Created by gauravkumar.singh on 5/5/2017.
 */

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ViewHolder> {

    ArrayList<String> alName;
    Context context;

    public YearAdapter(Context context, ArrayList<String> alName) {
        super();
        this.context = context;
        this.alName = alName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.year_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvSpecies.setText(alName.get(i));
    }

    @Override
    public int getItemCount() {
        return alName.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tvSpecies;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSpecies = (TextView) itemView.findViewById(R.id.textView17);

        }

    }

}
