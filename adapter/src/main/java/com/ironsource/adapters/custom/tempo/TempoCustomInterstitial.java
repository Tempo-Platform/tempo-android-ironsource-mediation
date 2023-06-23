package com.ironsource.adapters.custom.tempo;

import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;

import android.app.Activity;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.tempoplatform.ads.InterstitialView;
import com.tempoplatform.ads.TempoUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Keep
@SuppressWarnings("unused")
public class TempoCustomInterstitial extends BaseInterstitial<TempoCustomAdapter> {

    private InterstitialView interstitialView;
    private boolean interstitialReady;

    public TempoCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
        TempoUtils.Say("TempoAdapter: init interstitial");
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {

        // Get App ID
        String appId = "";
        JSONObject obj = new JSONObject(adData.getConfiguration());
        try {
            appId = obj.getString("appId");
        } catch (JSONException e) {
            TempoUtils.Warn("TempoAdapter: Could not get AppID from adData", true);
        }

        // Get CPM Floor
        String cpmFloorStr;
        try {
            // Confirm string is legit decimal value
            cpmFloorStr = obj.getString("cpmFloor");
            double decimalNumber = Double.parseDouble(cpmFloorStr);
            cpmFloorStr = String.valueOf(decimalNumber);
            TempoUtils.Say("TempoAdapter: CPMFloor=" + cpmFloorStr, true);
        } catch (JSONException e) {
            TempoUtils.Warn("TempoAdapter: Could not get CPMFloor from adData", true);
            cpmFloorStr = "0";
        }
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;

        // Other properties must to be determined
        String location = null; // TODO: Currently blank
        String placementId = ""; // TODO: Get PlacementID - given by customer at time of ShowAd. Have contacted IronSource.

        com.tempoplatform.ads.InterstitialAdListener tempoListener = new com.tempoplatform.ads.InterstitialAdListener() {
            @Override
            public void onInterstitialAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdFetchSucceeded",true);
                listener.onAdLoadSuccess(); // Indicates that interstitial ad was loaded successfully
                interstitialReady = true;
                //super.onInterstitialAdFetchSucceeded();
            }

            @Override
            public void onInterstitialAdFetchFailed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdFetchFailed",true);
                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The interstitial ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                //super.onInterstitialAdFetchFailed();
            }

            @Override
            public void onInterstitialAdDisplayed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdDisplayed",true);
                listener.onAdShowSuccess();
                //super.onInterstitialAdDisplayed();
            }

            @Override
            public void onInterstitialAdClosed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdClosed",true);
                listener.onAdClosed();
                interstitialReady = false;
                //super.onInterstitialAdClosed();
            }

            @Override
            public String onVersionExchange(String sdkVersion) {
                TempoUtils.Say("TempoAdapter: onVersionExchange (interstitial, SDK=" + sdkVersion + ", Adapter=" + TempoCustomAdapter.ADAPTER_VERSION + ")");
                TempoCustomAdapter.dynSdkVersion = sdkVersion;
                return TempoCustomAdapter.ADAPTER_VERSION;
            }

            @Override
            public String onGetAdapterType() {
                TempoUtils.Say("TempoAdapter: onGetAdapterType (Interstitial, Type: " + TempoCustomAdapter.ADAPTER_TYPE + ")");
                return TempoCustomAdapter.ADAPTER_TYPE;
            }
        };

        final String finalAppId = appId; // Variable used in lambda expression should be final or effectively final
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(finalAppId, activity);
            if (location != null) {
                interstitialView.loadAd(activity, tempoListener, cpmFloor, placementId, location);
            } else {
                interstitialView.loadAd(activity, tempoListener, cpmFloor, placementId);
            }
        });
    }

    @Override
    public void showAd(AdData adData, InterstitialAdListener ironSourceAdlistener) {
        TempoUtils.Say("TempoAdapter: showAd (i)", true);
        if (interstitialReady)  {
            interstitialView.showAd();
        } else {
            ironSourceAdlistener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, "Interstitial Ad not ready");
        }
    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        TempoUtils.Say("TempoAdapter: isAdAvailable (i)", false);
        return interstitialReady;
    }
}
