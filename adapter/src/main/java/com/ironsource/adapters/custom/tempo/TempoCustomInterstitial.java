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
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.tempoplatform.ads.Constants;
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
        //TempoUtils.Say("TempoAdapter: init interstitial");
    }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {

        // Get App ID
        String appId = "";
        JSONObject obj = new JSONObject(adData.getConfiguration());
        try {
            appId = obj.getString(AdapterConstants.PARAM_APP_ID);
        } catch (JSONException e) {
            TempoUtils.Warn("TempoAdapter: Could not get AppID from adData", true);
        }

        // Get CPM Floor
        Float cpmFloor;
        try {
            // Confirm string is legit decimal value
            String cpmFloorStr = obj.getString(AdapterConstants.PARAM_CPM_FLR);
            double decimalNumber = Double.parseDouble(cpmFloorStr);
            cpmFloorStr = String.valueOf(decimalNumber);
            cpmFloor = Float.parseFloat(cpmFloorStr);
            TempoUtils.Say("TempoAdapter: loadAd (r) CPMFloor=" + cpmFloor, true);
        } catch (JSONException e) {
            TempoUtils.Warn("TempoAdapter: Could not get CPMFloor from adData", true);
            cpmFloor = 0.0F;
        }

        // Other properties must to be determined
        String placementId = ""; // Purely placer, given by customer at time of ShowAd, cannot catch

        com.tempoplatform.ads.TempoAdListener tempoListener = new com.tempoplatform.ads.TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdFetchSucceeded");
                listener.onAdLoadSuccess(); // Indicates that interstitial ad was loaded successfully
                interstitialReady = true;
                //super.onInterstitialAdFetchSucceeded();
            }

            @Override
            public void onTempoAdFetchFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onInterstitialAdFetchFailed: " + reason);
                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The interstitial ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                //super.onInterstitialAdFetchFailed();
            }

            @Override
            public void onTempoAdDisplayed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdDisplayed");
                listener.onAdOpened();
                //super.onInterstitialAdDisplayed();
            }

            @Override
            public void onTempoAdShowFailed(String reason) {
                TempoUtils.Say("TempoAdapter: onInterstitialAdShowFailed: " + reason);
                listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, reason);
            }

            @Override
            public void onTempoAdClosed() {
                TempoUtils.Say("TempoAdapter: onInterstitialAdClosed");
                listener.onAdClosed();
                interstitialReady = false;
                //super.onInterstitialAdClosed();
            }

            @Override
            public String getTempoAdapterVersion() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterVersion (interstitial, SDK=" + Constants.SDK_VERSION + ", Adapter=" + AdapterConstants.ADAPTER_VERSION + ")");
                return AdapterConstants.ADAPTER_VERSION;
            }

            @Override
            public String getTempoAdapterType() {
                TempoUtils.Say("TempoAdapter: getTempoAdapterType (interstitial, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                return AdapterConstants.ADAPTER_TYPE;
            }
        };

        final String finalAppId = appId; // Variable used in lambda expression should be final or effectively final
        Float finalCpmFloor = cpmFloor;
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(finalAppId, activity);
            interstitialView.loadAd(activity, tempoListener, finalCpmFloor, placementId);
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
