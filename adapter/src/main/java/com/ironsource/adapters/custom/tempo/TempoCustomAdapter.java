package com.ironsource.adapters.custom.tempo;

import static com.ironsource.adapters.custom.tempo.BuildConfig.DEBUG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;

@Keep
@SuppressWarnings("unused")
public class TempoCustomAdapter extends BaseAdapter {
    private static final String LOG_TAG = TempoCustomAdapter.class.getSimpleName();

    public TempoCustomAdapter() {
        if (DEBUG) {
            Log.v(LOG_TAG, "ctor");
        }
    }

    @Override
    public void init(@NonNull AdData adData, @NonNull Context context, @Nullable NetworkInitializationListener listener) {
        if (DEBUG) {
            Log.v(LOG_TAG, "init: " + adData.getConfiguration());
        }

        if (listener != null) {
            listener.onInitSuccess();
        }

    }

    @Override
    public String getNetworkSDKVersion() {
        return "0.0.4";
    }

    @NonNull
    @Override
    public String getAdapterVersion() {
        return "0.0.4";
    }
}
