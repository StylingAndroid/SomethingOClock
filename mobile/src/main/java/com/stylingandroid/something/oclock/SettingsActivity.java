package com.stylingandroid.something.oclock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.stylingandroid.something.oclock.adapter.WordClickHandler;
import com.stylingandroid.something.oclock.adapter.WordsAdapter;
import com.stylingandroid.something.oclock.common.CommonData;
import com.stylingandroid.something.oclock.data.DataClient;
import com.stylingandroid.something.oclock.data.DataClientLoader;

public class SettingsActivity extends AppCompatActivity implements WordClickHandler, DataClientLoader.Callback {
    private static final String TAG = "SettingsActivity";

    private RecyclerView recyclerView;
    private DataClient dataClient;
    private DataClientLoader dataClientLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupToolbar();
        setupRecyclerView();

        dataClientLoader = DataClientLoader.newInstance();
        dataClientLoader.loadDataClient(this, this);
    }

    @Override
    protected void onDestroy() {
        dataClientLoader.closeDataClient(this);
        super.onDestroy();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.words_list);
        String[] words = CommonData.getTimeStrings(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(WordsAdapter.newInstance(words, this));
    }

    @Override
    public void wordSelected(String word) {
        if (dataClient != null) {
            Snackbar.make(recyclerView, word, Snackbar.LENGTH_LONG).show();
            dataClient.setWord(word, wordDataItemCallback);
        }
    }

    @Override
    public void connected(DataClient newDataClient) {
        this.dataClient = newDataClient;
        Log.v(TAG, "Connected");
        Snackbar.make(recyclerView, "Connected", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void suspended(int reason) {
        Log.v(TAG, "Suspended");
        Snackbar.make(recyclerView, "Connection suspended: " + reason, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void failed(String reason) {
        Log.v(TAG, "Failed");
        Snackbar.make(recyclerView, "Connection failed: " + reason, Snackbar.LENGTH_INDEFINITE).show();
    }

    private ResultCallback<DataApi.DataItemResult> wordDataItemCallback = new ResultCallback<DataApi.DataItemResult>() {
        @Override
        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
            Snackbar.make(recyclerView, "Word changed", Snackbar.LENGTH_LONG).show();
        }
    };
}
