package com.stylingandroid.something.oclock.renderer;

import android.content.Context;
import android.graphics.Rect;

import com.stylingandroid.something.oclock.R;

public class InsetCalculator {
    private final boolean isRound;
    private final Rect insetBounds;
    private final Rect previousBounds;
    private final int squareInset;

    public static InsetCalculator newInstance(Context context, boolean isRound) {
        Rect insetBounds = new Rect();
        Rect previousBounds = new Rect();
        int squareInset = (int) context.getResources().getDimension(R.dimen.digital_x_offset);
        return new InsetCalculator(isRound, insetBounds, previousBounds, squareInset);
    }

    public InsetCalculator(boolean isRound, Rect insetBounds, Rect previousBounds, int squareInset) {
        this.isRound = isRound;
        this.insetBounds = insetBounds;
        this.previousBounds = previousBounds;
        this.squareInset = squareInset;
    }

    public Rect getInsetBounds(Rect bounds) {
        boolean hasChanged = hasBoundsChanged(bounds);
        if (hasChanged) {
            calculateInsetBounds(bounds);
            previousBounds.set(bounds);
        }
        return insetBounds;
    }

    public boolean hasBoundsChanged(Rect bounds) {
        return !bounds.equals(previousBounds);
    }

    private void calculateInsetBounds(Rect bounds) {
        int inset = getInset(bounds);
        insetBounds.set(bounds);
        insetBounds.inset(inset, inset);
    }

    private int getInset(Rect bounds) {
        if (isRound) {
            return calculateRoundInset(bounds);
        }
        return squareInset;
    }

    private int calculateRoundInset(Rect bounds) {
        double radius = bounds.width() / 2f;
        double shortSide = Math.sqrt((radius * radius) / 2f);
        return (int) (radius - shortSide);
    }
}
