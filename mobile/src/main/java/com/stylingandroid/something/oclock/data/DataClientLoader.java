package com.stylingandroid.something.oclock.data;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.stylingandroid.something.oclock.common.CommonData;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class DataClientLoader implements GoogleApiClient.OnConnectionFailedListener {
    private static final String WEAR_API_UNAVAILABLE = "Wear API Unavailable";

    private final Set<Callback> callbacks;

    private GoogleApiClient googleApiClient = null;

    public static DataClientLoader newInstance() {
        Set<Callback> callbacks = Collections.newSetFromMap(new WeakHashMap<Callback, Boolean>());
        return new DataClientLoader(callbacks);
    }

    DataClientLoader(Set<Callback> callbacks) {
        this.callbacks = callbacks;
    }

    public void loadDataClient(FragmentActivity activity, Callback callback) {
        callbacks.add(callback);
        if (googleApiClient != null) {
            return;
        }
        createConnection(activity);
    }

    public void closeDataClient(Callback callback) {
        callbacks.remove(callback);
        googleApiClient.disconnect();
    }

    private void createConnection(FragmentActivity activity) {
        String[] defaultWords = CommonData.getTimeStrings(activity);
        ConnectionCallbacks connectionCallbacks = new ConnectionCallbacks(defaultWords);
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(activity, this)
                .addApi(Wearable.API)
                .build();
        googleApiClient.connect();
    }

    private final class ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
        private final String[] words;

        private ConnectionCallbacks(String[] words) {
            this.words = words;
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            DataClient dataClient = DataClient.newInstance(googleApiClient, words);
            for (Callback callback : callbacks) {
                callback.connected(dataClient);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            for (Callback callback : callbacks) {
                callback.suspended(i);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String message = connectionResult.getErrorMessage();
        if (TextUtils.isEmpty(message) && connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            message = WEAR_API_UNAVAILABLE;
        }
        for (Callback callback : callbacks) {
            callback.failed(message);
        }
    }

    public interface Callback {
        void connected(DataClient dataClient);

        void suspended(int reason);

        void failed(String reason);
    }
}
