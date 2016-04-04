package com.stylingandroid.something.oclock.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;

class Text {
    private final Paint textPaint;
    private final String text;
    private final int xOffset;
    private final int yOffset;

    public Text(Paint textPaint, String text, int xOffset, int yOffset) {
        this.textPaint = textPaint;
        this.text = text;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void draw(Canvas canvas) {
        canvas.drawText(text, xOffset, yOffset, textPaint);
    }

    public void setAntiAlias(boolean antiAlias) {
        textPaint.setAntiAlias(antiAlias);
    }
}
