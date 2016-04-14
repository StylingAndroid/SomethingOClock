package com.stylingandroid.something.oclock.data;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.stylingandroid.something.oclock.CommonData;

import java.util.ArrayList;
import java.util.Arrays;

public class DataClient {

    private final GoogleApiClient googleApiClient;

    private String word;
    private final ArrayList<String> words;

    public static DataClient newInstance(GoogleApiClient googleApiClient, String[] words) {
        ArrayList<String> wordsList = new ArrayList<>(Arrays.asList(words));
        return new DataClient(googleApiClient, wordsList);
    }

    DataClient(GoogleApiClient googleApiClient, ArrayList<String> words) {
        this.googleApiClient = googleApiClient;
        this.words = words;
    }

    public void setWord(@NonNull String newWord, ResultCallback<DataApi.DataItemResult> callback) {
        this.word = newWord;
        if (!words.contains(newWord)) {
            words.add(newWord);
        }
        sendData(callback);
    }

    private void sendData(ResultCallback<DataApi.DataItemResult> callback) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(CommonData.PATH_WORDS);
        putDataMapRequest.setUrgent();
        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putString(CommonData.KEY_WORD, word);
        dataMap.putStringArrayList(CommonData.KEY_WORDS, words);
        PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(googleApiClient, putDataRequest);
        pendingResult.setResultCallback(callback);
    }

}
