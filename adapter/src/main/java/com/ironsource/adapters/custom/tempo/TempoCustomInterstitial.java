package com.ironsource.adapters.custom.tempo;

// Generic
import android.app.Activity;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
//import org.json.JSONException;
//import org.json.JSONObject;

// ironSource
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;
import com.ironsource.mediationsdk.adunit.adapter.BaseInterstitial;
import com.ironsource.mediationsdk.adunit.adapter.listener.InterstitialAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.model.NetworkSettings;

// Tempo SDK
import com.tempoplatform.ads.Constants;
import com.tempoplatform.ads.InterstitialView;
import com.tempoplatform.ads.TempoAdListener;
import com.tempoplatform.ads.TempoUtils;

@Keep
@SuppressWarnings("unused")
public class TempoCustomInterstitial extends BaseInterstitial<TempoCustomAdapter> {

    private InterstitialView interstitialView;
    private boolean interstitialReady;

    public TempoCustomInterstitial(NetworkSettings networkSettings) { super(networkSettings); }

    @Override
    public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull InterstitialAdListener listener) {

        // Extract App ID
        String appId = AdapterUtils.extractAppId(adData);

        // Extract CPM Floor
        Float cpmFloor = AdapterUtils.extractCpmFloor(adData);

        // Other properties
        String placementId = ""; // Provided by customer at the time of ShowAd, cannot catch here

        // Create listener for ad events
        TempoAdListener tempoListener = createTempoAdListener(listener);

        // Load the ad on the UI thread
        activity.runOnUiThread(() -> {
            interstitialView = new InterstitialView(appId, activity);
            interstitialView.loadAd(activity, tempoListener, cpmFloor, placementId);
        });
    }

    @Override
    public void showAd(@NonNull AdData adData, @NonNull InterstitialAdListener ironSourceAdlistener) {
        TempoUtils.say("TempoAdapter: showAd (i)", true);
        if (interstitialReady)  {
            interstitialView.showAd();
        } else {
            ironSourceAdlistener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, "Interstitial Ad not ready");
        }
    }

    @Override
    public boolean isAdAvailable(@NonNull AdData adData) {
        TempoUtils.say("TempoAdapter: isAdAvailable (i)", false);
        return interstitialReady;
    }

    /**
     * Create listener instance and configure callbacks
     */
    private TempoAdListener createTempoAdListener(InterstitialAdListener listener) {
        return new TempoAdListener() {
            @Override
            public void onTempoAdFetchSucceeded() {
                TempoUtils.say("TempoAdapter: onInterstitialAdFetchSucceeded");
                listener.onAdLoadSuccess(); // Indicates that interstitial ad was loaded successfully
                interstitialReady = true;
                //super.onTempoAdFetchSucceeded();
            }

            @Override
            public void onTempoAdFetchFailed(String reason) {
                TempoUtils.say("TempoAdapter: onInterstitialAdFetchFailed: " + reason);
                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The interstitial ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                //super.onTempoAdFetchFailed(reason);
            }

            @Override
            public void onTempoAdDisplayed() {
                TempoUtils.say("TempoAdapter: onInterstitialAdDisplayed");
                listener.onAdOpened();
                //super.onTempoAdDisplayed();
            }

            @Override
            public void onTempoAdShowFailed(String reason) {
                TempoUtils.say("TempoAdapter: onInterstitialAdShowFailed: " + reason);
                listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, reason);
                //super.onTempoAdShowFailed(reason);
            }

            @Override
            public void onTempoAdClosed() {
                TempoUtils.say("TempoAdapter: onInterstitialAdClosed");
                listener.onAdClosed();
                interstitialReady = false;
                //super.onTempoAdClosed();
            }

            @Override
            public String getTempoAdapterVersion() {
                TempoUtils.say("TempoAdapter: getTempoAdapterVersion (interstitial, SDK=" + Constants.SDK_VERSION + ", Adapter=" + AdapterConstants.ADAPTER_VERSION + ")");
                return AdapterConstants.ADAPTER_VERSION;
            }

            @Override
            public String getTempoAdapterType() {
                TempoUtils.say("TempoAdapter: getTempoAdapterType (interstitial, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                return AdapterConstants.ADAPTER_TYPE;
            }
        };
    }
}
