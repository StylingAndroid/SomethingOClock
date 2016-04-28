package com.stylingandroid.something.oclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;
import com.stylingandroid.something.oclock.common.CommonData;
import com.stylingandroid.something.oclock.data.DataMapResult;
import com.stylingandroid.something.oclock.data.LocalDataMap;
import com.stylingandroid.something.oclock.data.UriResult;
import com.stylingandroid.something.oclock.renderer.InsetCalculator;
import com.stylingandroid.something.oclock.renderer.TextLayout;

public class SomethingOClockFace extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private boolean ambient;
        private InsetCalculator insetCalculator;
        private TextLayout textLayout;

        private GoogleApiClient googleApiClient;

        private boolean lowBitAmbient;

        private int activeBackgroundColour;
        private int ambientBackgroundColour;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            Context context = SomethingOClockFace.this;
            setWatchFaceStyle(new WatchFaceStyle.Builder(SomethingOClockFace.this)
                                      .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                                      .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                                      .setShowSystemUiTime(false)
                                      .setAcceptsTapEvents(false)
                                      .build());

            activeBackgroundColour = ContextCompat.getColor(context, R.color.background);
            ambientBackgroundColour = Color.BLACK;
            int textColour = ContextCompat.getColor(context, R.color.digital_text);

            textLayout = TextLayout.newInstance(textColour);

            googleApiClient = new GoogleApiClient.Builder(SomethingOClockFace.this)
                    .addConnectionCallbacks(googleConnectionCallbacks)
                    .addApi(Wearable.API)
                    .build();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                googleApiClient.connect();
            } else {
                if (googleApiClient != null && googleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(googleApiClient, dataListener);
                    googleApiClient.disconnect();
                }
            }
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            boolean isRound = insets.isRound();
            insetCalculator = InsetCalculator.newInstance(SomethingOClockFace.this, isRound);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            lowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (ambient != inAmbientMode) {
                ambient = inAmbientMode;
                if (lowBitAmbient) {
                    textLayout.setAntiAlias(!inAmbientMode);
                }
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            if (ambient) {
                canvas.drawColor(ambientBackgroundColour);
            } else {
                canvas.drawColor(activeBackgroundColour);
            }
            boolean hasChanged = insetCalculator.hasBoundsChanged(bounds);
            Rect insetBounds = insetCalculator.getInsetBounds(bounds);
            if (hasChanged) {
                textLayout.invalidateLayout();
            }
            textLayout.draw(canvas, insetBounds);
        }

        private GoogleApiClient.ConnectionCallbacks googleConnectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                final LocalDataMap localDataMap = LocalDataMap.newInstance(SomethingOClockFace.this, googleApiClient);
                fetchConfigDataMap(localDataMap);
                localDataMap.getLocalStorageUri(new ResultCallback<UriResult>() {
                    @Override
                    public void onResult(@NonNull UriResult uriResult) {
                        Wearable.DataApi.addListener(googleApiClient, dataListener, uriResult.getUri(), DataApi.FILTER_LITERAL);
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {
                //NO-OP
            }
        };

        public void fetchConfigDataMap(LocalDataMap localDataMap) {
            localDataMap.fetchConfig(new ResultCallback<DataMapResult>() {
                @Override
                public void onResult(@NonNull DataMapResult dataMapResult) {
                    if (dataMapResult.getStatus().isSuccess()) {
                        DataMap dataMap = dataMapResult.getDataMap();
                        decode(dataMap);
                    }
                }
            });
        }

        private DataApi.DataListener dataListener = new DataApi.DataListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEventBuffer) {
                for (DataEvent event : dataEventBuffer) {
                    Uri uri = event.getDataItem().getUri();
                    if (uri.getPath().equals(LocalDataMap.PATH_WORDS)) {
                        decode(event.getDataItem());
                    }
                }
            }
        };

        private void decode(DataItem dataItem) {
            Uri uri = dataItem.getUri();
            String path = uri.getPath();
            if (path.equals(LocalDataMap.PATH_WORDS)) {
                DataMap dataMap = LocalDataMap.dataMapFrom(dataItem);
                decode(dataMap);
            }
        }

        private void decode(DataMap dataMap) {
            String word = dataMap.getString(CommonData.KEY_WORD);
            updateWord(word);
        }

        private void updateWord(String word) {
            textLayout.setTimeText(word);
            invalidate();
        }
    }
}
