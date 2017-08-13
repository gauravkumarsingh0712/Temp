package com.ncsavault.alabamavault.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.adapters.SavedVideoAdapter;

public class VideoDetailActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_video_fragment_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.saved_video_recycler_view);

//        SavedVideoAdapter savedVideoAdapter = new SavedVideoAdapter(this);
//        mRecyclerView.setHasFixedSize(true);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(llm);
//        mRecyclerView.setAdapter(savedVideoAdapter);
    }

}
