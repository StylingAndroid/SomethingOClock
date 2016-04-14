package com.stylingandroid.something.oclock;

import android.content.Context;
import android.content.res.Resources;

import com.stylingandroid.customoclock.common.R;

public final class CommonData {

    public static final String KEY_WORD = "word";
    public static final String KEY_WORDS = "words";

    public static final String PATH_WORDS = "/mobile";

    private CommonData() {
        //NO-OP
    }

    public static String[] getTimeStrings(Context context) {
        Resources resources = context.getResources();
        return resources.getStringArray(R.array.default_words);
    }
}
