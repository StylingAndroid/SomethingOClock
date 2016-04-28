package com.stylingandroid.something.oclock.data;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.stylingandroid.something.oclock.common.CommonData;

public class LocalDataMap {

    public static final String PATH_WORDS = "/wear";

    private final GoogleApiClient googleApiClient;

    private final LocalUriFetcher localUriFetcher;
    private final MissingDataPopulator missingDataPopulator;

    public static LocalDataMap newInstance(Context context, GoogleApiClient googleApiClient) {
        LocalUriFetcher localUriFetcher = new LocalUriFetcher(googleApiClient);
        MissingDataPopulator missingDataPopulator = new MissingDataPopulator(context);
        return new LocalDataMap(googleApiClient, localUriFetcher, missingDataPopulator);
    }

    public LocalDataMap(GoogleApiClient googleApiClient, LocalUriFetcher localUriFetcher, MissingDataPopulator missingDataPopulator) {
        this.googleApiClient = googleApiClient;
        this.localUriFetcher = localUriFetcher;
        this.missingDataPopulator = missingDataPopulator;
    }

    public static DataMap dataMapFrom(DataItem dataItem) {
        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
        return dataMapItem.getDataMap();
    }

    public void overwriteConfig(final String word, final ResultCallback<DataApi.DataItemResult> callback) {
        fetchConfig(new ResultCallback<DataMapResult>() {
            @Override
            public void onResult(@NonNull DataMapResult dataMapResult) {
                if (dataMapResult.getStatus().isSuccess()) {
                    DataMap currentDataMap = dataMapResult.getDataMap();
                    DataMap newDataMap = new DataMap();
                    newDataMap.putAll(currentDataMap);
                    newDataMap.putString(CommonData.KEY_WORD, word);
                    missingDataPopulator.populateMissingKeys(newDataMap);
                    writeConfig(newDataMap, callback);
                }
            }
        });
    }

    public void fetchConfig(final ResultCallback<DataMapResult> callback) {
        getLocalStorageUri(new ResultCallback<UriResult>() {
            @Override
            public void onResult(@NonNull UriResult uriResult) {
                Uri uri = uriResult.getUri();
                ResultCallback<DataApi.DataItemResult> populator = missingDataPopulator.getPopulator(LocalDataMap.this, callback);
                Wearable.DataApi.getDataItem(googleApiClient, uri).setResultCallback(populator);
            }
        });
    }

    public void getLocalStorageUri(ResultCallback<UriResult> callback) {
        localUriFetcher.getLocalStorageUri(PATH_WORDS, callback);
    }

    public void writeConfig(DataMap dataMap, ResultCallback<DataApi.DataItemResult> callback) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_WORDS);
        putDataMapRequest.setUrgent();
        putDataMapRequest.getDataMap().putAll(dataMap);
        Wearable.DataApi.putDataItem(googleApiClient, putDataMapRequest.asPutDataRequest())
                .setResultCallback(callback);
    }
}
