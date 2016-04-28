package com.stylingandroid.something.oclock;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.stylingandroid.something.oclock.common.CommonData;
import com.stylingandroid.something.oclock.data.LocalDataMap;

import java.util.List;

public class DataListenerService extends WearableListenerService {
    private static final String TAG = "DataListenerService";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        ConnectionResult connectionResult = googleApiClient.blockingConnect();
        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Error connecting to GoogleApiClient: " + connectionResult.getErrorMessage());
            return;
        }

        LocalDataMap localDataMap = LocalDataMap.newInstance(this, googleApiClient);

        for (DataEvent dataEvent : events) {
            DataItem dataItem = dataEvent.getDataItem();
            parseDataItem(localDataMap, dataItem);
        }
    }

    private void parseDataItem(LocalDataMap localDataMap, DataItem dataItem) {
        Uri uri = dataItem.getUri();
        if (uri.getPath().equals(CommonData.PATH_WORDS)) {
            parseWords(localDataMap, dataItem);
        }
    }

    private void parseWords(LocalDataMap localDataMap, DataItem dataItem) {
        DataMap newDataMap = LocalDataMap.dataMapFrom(dataItem);
        String word = newDataMap.getString(CommonData.KEY_WORD);
        localDataMap.overwriteConfig(word, new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
            }
        });
    }
}
