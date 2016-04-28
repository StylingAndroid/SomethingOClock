package com.stylingandroid.something.oclock.data;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

class LocalUriFetcher {

    private static final Status SUCCESS = new Status(0);

    private final GoogleApiClient googleApiClient;

    public LocalUriFetcher(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void getLocalStorageUri(final String path, final ResultCallback<UriResult> callback) {
        Wearable.NodeApi.getLocalNode(googleApiClient).setResultCallback(new ResultCallback<NodeApi.GetLocalNodeResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetLocalNodeResult getLocalNodeResult) {
                String localNode = getLocalNodeResult.getNode().getId();
                Uri uri = new Uri.Builder()
                        .scheme("wear")
                        .path(path)
                        .authority(localNode)
                        .build();
                callback.onResult(new LocalStorageUriResult(uri));
            }
        });
    }

    private class LocalStorageUriResult implements UriResult {
        private final Uri uri;

        LocalStorageUriResult(Uri uri) {
            this.uri = uri;
        }

        @Override
        public Uri getUri() {
            return uri;
        }

        @Override
        public Status getStatus() {
            return SUCCESS;
        }
    }
}
