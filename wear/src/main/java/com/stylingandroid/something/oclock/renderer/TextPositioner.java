package com.stylingandroid.something.oclock.renderer;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

final class TextPositioner {
    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final int DEFAULT_TEXT_SIZE = 30;
    private static final float LINE_HEIGHT_FACTOR = 1.1f;
    private final String text;
    private final Paint textPaint;
    private final Rect boundsRect;
    private int xOffset;
    private int yOffset;

    public static TextPositioner newInstance(String text, int width, int textColour) {
        Paint textPaint = createTextPaint(textColour);
        adjustTextSize(text, textPaint, width);
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        return new TextPositioner(text, textPaint, rect);
    }

    private static Paint createTextPaint(int textColour) {
        Paint paint = new Paint();
        paint.setColor(textColour);
        paint.setTypeface(NORMAL_TYPEFACE);
        paint.setAntiAlias(true);
        paint.setTextSize(DEFAULT_TEXT_SIZE);
        return paint;
    }

    private static void adjustTextSize(String text, Paint textPaint, int width) {
        float textWidth = textPaint.measureText(text);
        float scaleFactor = width / textWidth;
        float newSize = textPaint.getTextSize() * scaleFactor;
        textPaint.setTextSize(newSize);
    }

    TextPositioner(String text, Paint textPaint, Rect boundsRect) {
        this.text = text;
        this.textPaint = textPaint;
        this.xOffset = 0;
        this.yOffset = 0;
        this.boundsRect = boundsRect;
    }

    void setPosition(int horizontalOffset, int verticalOffset) {
        xOffset = horizontalOffset;
        yOffset = verticalOffset + getBaselineOffset();
    }

    int getBaselineOffset() {
        Rect newBoundsRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), newBoundsRect);
        return Math.abs(newBoundsRect.top);
    }

    int getHeight() {
        return boundsRect.height();
    }

    float getLineHeight() {
        return getHeight() * LINE_HEIGHT_FACTOR;
    }

    Text createText() {
        return new Text(textPaint, text, xOffset, yOffset);
    }

    float adjustSize(float scaleFactor, int xAdjustment) {
        xOffset += xAdjustment;
        float newSize = textPaint.getTextSize() * scaleFactor;
        textPaint.setTextSize(newSize);
        textPaint.getTextBounds(text, 0, text.length(), boundsRect);
        return getLineHeight();
    }

    void adjustVerticalPosition(int yAdjustment) {
        yOffset += yAdjustment;
    }
}
