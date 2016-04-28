package com.stylingandroid.something.oclock.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.stylingandroid.something.oclock.R;
import com.stylingandroid.something.oclock.common.CommonData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MissingDataPopulator {

    private static final Status SUCCESS = new Status(0);

    private final Context context;

    public MissingDataPopulator(Context context) {
        this.context = context;
    }

    public boolean populateMissingKeys(@NonNull DataMap dataMap) {
        boolean shouldSave = false;
        if (!dataMap.containsKey(CommonData.KEY_WORDS)) {
            String[] wordsArray = context.getResources().getStringArray(R.array.default_words);
            ArrayList<String> words = new ArrayList<>(Arrays.asList(wordsArray));
            dataMap.putStringArrayList(CommonData.KEY_WORDS, words);
            shouldSave = true;
        }
        if (!dataMap.containsKey(CommonData.KEY_WORD)) {
            List<String> words = dataMap.getStringArrayList(CommonData.KEY_WORDS);
            String defaultWord = words.get(0);
            dataMap.putString(CommonData.KEY_WORD, defaultWord);
            shouldSave = true;
        }
        return shouldSave;
    }

    public ResultCallback<DataApi.DataItemResult> getPopulator(LocalDataMap localDataMap, ResultCallback<DataMapResult> callback) {
        return new Populator(localDataMap, callback);
    }

    private final class Populator implements ResultCallback<DataApi.DataItemResult> {
        private final LocalDataMap localDataMap;
        private final ResultCallback<DataMapResult> callback;

        private Populator(LocalDataMap localDataMap, ResultCallback<DataMapResult> callback) {
            this.localDataMap = localDataMap;
            this.callback = callback;
        }

        @Override
        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
            final DataMap dataMap;
            if (hasConfig(dataItemResult)) {
                DataItem dataItem = dataItemResult.getDataItem();
                dataMap = LocalDataMap.dataMapFrom(dataItem);
            } else {
                dataMap = new DataMap();
            }
            if (populateMissingKeys(dataMap)) {
                localDataMap.writeConfig(dataMap, new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        callback.onResult(new PopulatorResult(dataMap));
                    }
                });
                return;
            }
            callback.onResult(new PopulatorResult(dataMap));
        }

        private boolean hasConfig(@NonNull DataApi.DataItemResult dataItemResult) {
            return dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null;
        }

        private final class PopulatorResult implements DataMapResult {
            private final DataMap dataMap;

            private PopulatorResult(DataMap dataMap) {
                this.dataMap = dataMap;
            }

            @Override
            public DataMap getDataMap() {
                return dataMap;
            }

            @Override
            public Status getStatus() {
                return SUCCESS;
            }

        }
    }
}
