package com.ironsource.adapters.custom.tempo;

import static com.ironsource.adapters.custom.tempo.BuildConfig.DEBUG;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.tempoplatform.ads.AdListener;
import com.tempoplatform.ads.InterstitialView;

@Keep
@SuppressWarnings("unused")
public class TempoCustomInterstitial extends BaseInterstitial<TempoCustomAdapter> {
    private static final String LOG_TAG = TempoCustomInterstitial.class.getSimpleName();
    private InterstitialView interstitialView = new InterstitialView();
    private boolean ready;

    public TempoCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);

        if (DEBUG) {
            Log.v(LOG_TAG, "ctor");
        }
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {
        System.out.println("loadAd called");
        if (DEBUG) {
            Log.v(LOG_TAG, "loadAd: " + adData.getConfiguration());
        }
        interstitialView.initialize(activity, new AdListener() {
            @Override
            public void onAdFetchSucceeded() {
                super.onAdFetchSucceeded();
                listener.onAdLoadSuccess();
            }

            @Override
            public void onAdFetchFailed() {
                super.onAdFetchFailed();
                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null);
                listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, null);
            }

            @Override
            public void onInterstitialDisplayed() {
                super.onInterstitialDisplayed();
                listener.onAdShowSuccess();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                listener.onAdClosed();
            }
        });
        listener.onAdLoadSuccess();
        this.ready = true;
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        System.out.println("isAdAvailable called");
        if (DEBUG) {
            Log.v(LOG_TAG, "isAdAvailable: " + adData.getConfiguration());
        }
        return ready;
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull InterstitialAdListener listener) {
        System.out.println("Show AD called");
        if (DEBUG) {
            Log.v(LOG_TAG, "showAd: " + adData.getConfiguration());
        }
        interstitialView.show();
    }
}
