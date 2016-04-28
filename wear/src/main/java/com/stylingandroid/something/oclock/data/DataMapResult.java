package com.stylingandroid.something.oclock.data;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.wearable.DataMap;

public interface DataMapResult extends Result {
    DataMap getDataMap();
}
