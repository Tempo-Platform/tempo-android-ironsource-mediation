package com.ironsource.adapters.custom.tempo;

// Generic
import android.content.Context;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// ironSource SDK
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;

// Tempo SDKs
import com.tempoplatform.ads.Constants;
import com.tempoplatform.ads.TempoUtils;

@Keep
@SuppressWarnings("unused")
public class TempoCustomAdapter extends BaseAdapter {

    public TempoCustomAdapter() {
        TempoUtils.Say("TempoAdapter: created");
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
        return Constants.SDK_VERSION;
    }

    @NonNull
    @Override
    public String getAdapterVersion() {
        return AdapterConstants.ADAPTER_VERSION;
    }

}
