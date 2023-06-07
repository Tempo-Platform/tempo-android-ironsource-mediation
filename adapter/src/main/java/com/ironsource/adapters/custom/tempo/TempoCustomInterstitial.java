package com.ironsource.adapters.custom.tempo;

import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;
import static com.tempoplatform.ads.Constants.TEST_LOG;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.tempoplatform.ads.InterstitialView;

import org.json.JSONException;
import org.json.JSONObject;

@Keep
@SuppressWarnings("unused")
public class TempoCustomInterstitial extends BaseInterstitial<TempoCustomAdapter> {

    private InterstitialView interstitialView;
    private boolean interstitialReady;

    public TempoCustomInterstitial(NetworkSettings networkSettings) {
        super(networkSettings);
        Log.d(TEST_LOG, "TempoCustomInterstitial initialised *");
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {

        // Get App ID
        String appId = "";
        JSONObject obj = new JSONObject(adData.getConfiguration());
        try {
            appId = obj.getString("appId");
        } catch (JSONException e) {
            Log.e(TEST_LOG, "Could not get AppId from adData");
        }

        // Get CPM Floor
        String cpmFloorStr;
        try {
            // Confirm string is legit decimal value
            cpmFloorStr = obj.getString("cpmFloor");
            double decimalNumber = Double.parseDouble(cpmFloorStr);
            cpmFloorStr = String.valueOf(decimalNumber);
            Log.d(TEST_LOG, "cpmFloor is " + cpmFloorStr);
        } catch (JSONException e) {
            Log.d(TEST_LOG, "Could not get cpmFloor from adData");
            cpmFloorStr = "0";
        }
        Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;

        // Get Ad Unit ID
        String adUnitId = "?";
        try {
            adUnitId = obj.getString("adUnitId");
        } catch (JSONException e) {
            Log.d(TEST_LOG, "Could not get adUnitId from adData");
        }

        // Other properties must to be determined
        String location = null; // TODO: Currently blank like in AppLovin, hard-coded to 'US' in SDK's second load iteration.
        String placementId = ""; // TODO: Get PlacementID - unclear how to get this, given by customer at time of ShowAd. Have contacted IronSource.

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
                //listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, null); // The ad could not be displayed
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
                TempoCustomAdapter.dynSdkVersion = sdkVersion;
                return TempoCustomAdapter.ADAPTER_VERSION;
            }

            @Override
            public String onGetAdapterType() {
                Log.d(TEST_LOG, "Adapter Type requested (I)");
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
