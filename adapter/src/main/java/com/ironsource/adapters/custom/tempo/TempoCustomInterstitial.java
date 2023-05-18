package com.ironsource.adapters.custom.tempo;

import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;
import static com.tempoplatform.ads.Constants.TEST_LOG;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.tempoplatform.ads.InterstitialView;

@Keep
@SuppressWarnings("unused")
public class TempoCustomInterstitial extends BaseInterstitial<TempoCustomAdapter> {

    public String dynSdkVersion = "1.0.1"; // TODO: What's the relationship with base class version?

    private InterstitialView interstitialView;
    private boolean interstitialReady;

    public TempoCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
        Log.d(TEST_LOG, "TempoCustomInterstitial initialised");
    }


    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {
        String AppId = "8";// TODO: Get AppID
        String location = "US"; // TODO: Get Location
        String placementId = "InterstitialPlacementID"; // TODO: Get PlacementID
        String cpmFloorStr = "20"; // TODO: Get CPM
        Log.e(TEST_LOG, "TempoCustomAdapter created: " + AppId + " | " + location + " | " + placementId + " | " + cpmFloorStr );
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;

        com.tempoplatform.ads.InterstitialAdListener tempoListener = new com.tempoplatform.ads.InterstitialAdListener() {
            @Override
            public void onInterstitialAdFetchSucceeded() {
                Log.d(TEST_LOG, "Interstitial ad fetch succeeded");
                super.onInterstitialAdFetchSucceeded();
                listener.onAdLoadSuccess(); // Indicates that interstitial ad was loaded successfully
                interstitialReady = true;
            }

            @Override
            public void onInterstitialAdFetchFailed() {
                Log.d(TEST_LOG, "Interstitial ad fetch failed");
                super.onInterstitialAdFetchFailed();
                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The interstitial ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, null); // The ad could not be displayed
            }

            @Override
            public void onInterstitialAdDisplayed() {
                Log.d(TEST_LOG, "Interstitial ad fetch displayed");
                super.onInterstitialAdDisplayed();
                listener.onAdShowSuccess();
            }

            @Override
            public void onInterstitialAdClosed() {
                Log.d(TEST_LOG, "Interstitial ad closed");
                super.onInterstitialAdClosed();
                listener.onAdClosed();
                interstitialReady = false;
            }

            @Override
            public String onVersionExchange(String sdkVersion) {
                Log.d(TEST_LOG, "Version exchange triggered");
//                /.dynSdkVersion = sdkVersion;
                dynSdkVersion = sdkVersion;
                return TempoCustomAdapter.ADAPTER_VERSION;
                //return super.getAdapterVersion(); // TODO: Why doesn't this work??
            }
        };

        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView("8", activity);
            if (location != null) {
                interstitialView.loadAd(activity, tempoListener, cpmFloor, placementId, location);
            } else {
                interstitialView.loadAd(activity, tempoListener, cpmFloor, placementId);
            }
        });

    }

    @Override
    public void showAd(AdData adData, InterstitialAdListener ironSourceAdlistener) {
        Log.d(TEST_LOG, "ShowAd called (" + interstitialReady + "): " + adData.getConfiguration());
        if (interstitialReady)  {
            interstitialView.showAd();
        } else {
            ironSourceAdlistener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, "Interstitial Ad not ready");
        }
    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        Log.d(TEST_LOG, "IsAdAvailable called: " + interstitialReady);
        return interstitialReady;
    }
}


// https://developers.is.com/ironsource-mobile/android/custom-adapter-integration-android/#step-3