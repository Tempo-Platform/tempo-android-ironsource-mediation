package com.ironsource.adapters.custom.tempo;

import static com.tempoplatform.ads.Constants.TEST_LOG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.tempoplatform.ads.TempoUtils;

@Keep
@SuppressWarnings("unused")
public class TempoCustomAdapter extends BaseAdapter {

    // Version references
    public static String dynSdkVersion = "1.0.4";
    public static final String ADAPTER_VERSION = "1.0.5"; // current 1.0.5
    public static final String ADAPTER_TYPE = "IRONSOURCE";

    // Log tag for debugging
    //private static final String LOG_TAG = TempoCustomAdapter.class.getSimpleName();

    public TempoCustomAdapter() {
        //Log.d(TEST_LOG, "TempoCustomAdapter.TempoCustomAdapter created!");
        TempoUtils.Say("TempoAdapter: created", true);
    }

    @Override
    public void init(@NonNull AdData adData, @NonNull Context context, @Nullable NetworkInitializationListener listener) {
        //Log.d(TEST_LOG, "TempoCustomAdapter.init: " + adData.getConfiguration());

        // TODO: some init-success-condition, or is this enough?
        if (listener != null) {
            // Initialization completed successfully
            listener.onInitSuccess();
            TempoUtils.Say("TempoAdapter: init adapter", true);
        } else {
            // Initialization failed
            TempoUtils.Shout("TempoAdapter: init adapter failed", true);
        }
    }

    @Override
    public String getNetworkSDKVersion() {
        return dynSdkVersion;
    }

    @NonNull
    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

}
