package com.stylingandroid.something.oclock.renderer;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.ColorInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextLayout {
    private static final String[] DEFAULT_TEXT = {"something", "o'clock"};

    private final List<String> lines;
    private final List<Text> texts;
    private final int textColour;

    public static TextLayout newInstance(@ColorInt int textColour) {
        List<String> lines = new ArrayList<>(Arrays.asList(DEFAULT_TEXT));
        List<Text> texts = new ArrayList<>();
        return new TextLayout(lines, texts, textColour);
    }

    TextLayout(List<String> lines, List<Text> texts, int textColour) {
        this.lines = lines;
        this.texts = texts;
        this.textColour = textColour;
    }

    public void invalidateLayout() {
        texts.clear();
    }

    public void draw(Canvas canvas, Rect bounds) {
        if (isLayoutInvalid()) {
            build(bounds);
        }
        for (Text text : texts) {
            text.draw(canvas);
        }
    }

    private void build(Rect bounds) {
        int width = bounds.width();

        TextPositioner[] positioners = constructPositioners(width);
        layout(positioners, bounds);

        for (TextPositioner positioner : positioners) {
            texts.add(positioner.createText());
        }
    }

    private boolean isLayoutInvalid() {
        return lines.size() != texts.size();
    }

    public void setAntiAlias(boolean antiAlias) {
        for (Text text : texts) {
            text.setAntiAlias(antiAlias);
        }
    }

    public void setTimeText(String timeText) {
        lines.remove(0);
        lines.add(0, timeText);
        invalidateLayout();
    }

    private TextPositioner[] constructPositioners(int width) {
        TextPositioner[] positioners = new TextPositioner[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            positioners[i] = TextPositioner.newInstance(line, width, textColour);
        }
        return positioners;
    }

    private void layout(TextPositioner[] positioners, Rect bounds) {
        int yOffset = bounds.top;
        for (TextPositioner positioner : positioners) {
            positioner.setPosition(bounds.left, yOffset);
            yOffset += positioner.getLineHeight();
        }
        if (yOffset > bounds.bottom) {
            adjustHeight(positioners, bounds, yOffset);
        } else {
            centreVertical(positioners, bounds, yOffset);
        }
    }

    private void adjustHeight(TextPositioner[] positioners, Rect bounds, float yOffset) {
        float scaleFactor = (float) bounds.bottom / yOffset;
        float adjustmentFactor = (1f - scaleFactor) / 2f;
        int xAdjustment = (int) (bounds.width() * adjustmentFactor);
        for (TextPositioner positioner : positioners) {
            positioner.adjustSize(scaleFactor, xAdjustment);
        }
    }

    private void centreVertical(TextPositioner[] positioners, Rect bounds, int yOffset) {
        int yAdjustment = (bounds.bottom - yOffset) / 2;
        for (TextPositioner positioner : positioners) {
            positioner.adjustVerticalPosition(yAdjustment);
        }
    }
}
