package com.ncsavault.alabamavault.fragments.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.SavedVideoAdapter;

/**
 * Created by gauravkumar.singh on 6/12/2017.
 */

public class SavedVideoFragment extends Fragment {

    private static Context mContext;
    RecyclerView mRecyclerView;
    public static Fragment newInstance(Context context) {
        Fragment frag = new SavedVideoFragment();
        mContext = context;
        Bundle args = new Bundle();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.saved_video_fragment_layout, container, false);


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.saved_video_recycler_view);

        SavedVideoAdapter savedVideoAdapter = new SavedVideoAdapter(mContext);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(savedVideoAdapter);
    }


}

