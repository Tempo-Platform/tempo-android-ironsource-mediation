package com.ironsource.adapters.custom.tempo;

// Generic
import android.app.Activity;
import androidx.annotation.Keep;
//import androidx.annotation.NonNull; // TODO: Check why IntAds.loadAd uses @NonNull
import org.json.JSONException;
import org.json.JSONObject;

// ironSource
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;
import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.model.NetworkSettings;

// Tempo SDK
import com.tempoplatform.ads.Constants;
import com.tempoplatform.ads.RewardedView;
import com.tempoplatform.ads.TempoAdListener;
import com.tempoplatform.ads.TempoUtils;

@Keep
@SuppressWarnings("unused")
public class TempoCustomRewardedVideo extends BaseRewardedVideo <TempoCustomAdapter> {

        private RewardedView rewardedView;
        private boolean rewardedReady;

        public TempoCustomRewardedVideo(NetworkSettings networkSettings) {
                super(networkSettings);
        }

        @Override
        public void loadAd(AdData adData, Activity activity, RewardedVideoAdListener listener) {

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

                TempoAdListener tempoListener = new TempoAdListener() {
                        @Override
                        public void onTempoAdFetchSucceeded() {
                                TempoUtils.Say("TempoAdapter: onRewardedAdFetchSucceeded");
                                listener.onAdLoadSuccess(); // Indicates that rewarded ad was loaded successfully
                                rewardedReady = true;
                                //super.onTempoAdFetchSucceeded();

                        }

                        @Override
                        public void onTempoAdFetchFailed(String reason) {
                                TempoUtils.Say("TempoAdapter: onRewardedAdFetchFailed: " + reason);
                                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The rewarded ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                                //super.onTempoAdFetchFailed(reason);
                        }

                        @Override
                        public void onTempoAdDisplayed() {
                                TempoUtils.Say("TempoAdapter: onRewardedAdDisplayed");
                                listener.onAdOpened();
                                listener.onAdRewarded();
                                //super.onTempoAdDisplayed();
                        }

                        @Override
                        public void onTempoAdShowFailed(String reason) {
                                TempoUtils.Say("TempoAdapter: onRewardedAdShowFailed: " + reason);
                                listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, reason);
                                //super.onTempoAdShowFailed(reason);
                        }

                        @Override
                        public void onTempoAdClosed() {
                                TempoUtils.Say("TempoAdapter: onRewardedAdClosed");
                                listener.onAdClosed();
                                rewardedReady = false;
                                //super.onTempoAdClosed();
                        }

                        @Override
                        public String getTempoAdapterVersion() {
                                TempoUtils.Say("TempoAdapter: getTempoAdapterVersion (rewarded, SDK=" + Constants.SDK_VERSION + ", Adapter=" + AdapterConstants.ADAPTER_VERSION + ")");
                                return AdapterConstants.ADAPTER_VERSION;
                        }

                        @Override
                        public String getTempoAdapterType() {
                                TempoUtils.Say("TempoAdapter: getTempoAdapterType (rewarded, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                                return AdapterConstants.ADAPTER_TYPE;
                        }
                };

                final String finalAppId = appId; // Variable used in lambda expression should be final or effectively final
                Float finalCpmFloor = cpmFloor;
                activity.runOnUiThread(() -> {
                        rewardedView = new RewardedView(finalAppId, activity);
                        rewardedView.loadAd(activity, tempoListener, finalCpmFloor, placementId);
                });
        }

        @Override
        public void showAd(AdData adData, RewardedVideoAdListener ironSourceAdlistener) {
                TempoUtils.Say("TempoAdapter: showAd (r)", true);
                if (rewardedReady)  {
                        rewardedView.showAd();
                } else {
                        ironSourceAdlistener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, "Rewarded Ad not ready");
                }
        }

        @Override
        public boolean isAdAvailable(AdData adData) {
                TempoUtils.Say("TempoAdapter: isAdAvailable (r)", false);
                return rewardedReady;
        }
}