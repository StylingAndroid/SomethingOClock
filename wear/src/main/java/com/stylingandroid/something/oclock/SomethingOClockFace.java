package com.stylingandroid.something.oclock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

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
    }
}
