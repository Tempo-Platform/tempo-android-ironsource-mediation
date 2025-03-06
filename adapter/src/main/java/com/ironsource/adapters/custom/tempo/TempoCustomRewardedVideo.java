package com.ironsource.adapters.custom.tempo;

// Generic
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
//import androidx.annotation.NonNull; // TODO: Check why IntAds.loadAd uses @NonNull
//import org.json.JSONException;
//import org.json.JSONObject;

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
        public void loadAd(@NonNull AdData adData, @NonNull Activity activity, @NonNull RewardedVideoAdListener listener) {

                // Extract App ID
                String appId = AdapterUtils.extractAppId(adData);

                // Extract CPM Floor
                Float cpmFloor = AdapterUtils.extractCpmFloor(adData);

                // Other properties
                String placementId = ""; // Provided by customer at the time of ShowAd, cannot catch here

                // Create listener for ad events
                TempoAdListener tempoListener = createTempoAdListener(listener);
                activity.runOnUiThread(() -> {
                        rewardedView = new RewardedView(appId, activity);
                        rewardedView.loadAd(activity, tempoListener, cpmFloor, placementId);
                });
        }

        @Override
        public void showAd(@NonNull AdData adData, @NonNull RewardedVideoAdListener ironSourceAdListener) {
                TempoUtils.say("TempoAdapter: showAd (r)", true);
                if (rewardedReady) {
                        rewardedView.showAd();
                } else {
                        ironSourceAdListener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, "Rewarded Ad not ready");
                }
        }

        @Override
        public boolean isAdAvailable(@NonNull AdData adData) {
                TempoUtils.say("TempoAdapter: isAdAvailable (r)", false);
                return rewardedReady;
        }

        /**
         * Create listener instance and configure callbacks
         */
        private TempoAdListener createTempoAdListener(RewardedVideoAdListener listener) {
                return new TempoAdListener() {
                        @Override
                        public void onTempoAdFetchSucceeded() {
                                TempoUtils.shout("TempoAdapter: onRewardedAdFetchSucceeded");
                                rewardedReady = true;
                                listener.onAdLoadSuccess(); // Indicates that rewarded ad was loaded successfully
                                //super.onTempoAdFetchSucceeded();
                        }

                        @Override
                        public void onTempoAdFetchFailed(String reason) {
                                TempoUtils.say("TempoAdapter: onRewardedAdFetchFailed: " + reason);
                                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, reason); // The rewarded ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                                //super.onTempoAdFetchFailed(reason);
                        }

                        @Override
                        public void onTempoAdDisplayed() {
                                TempoUtils.say("TempoAdapter: onRewardedAdDisplayed");
                                listener.onAdOpened();
                                listener.onAdRewarded();
                                //super.onTempoAdDisplayed();
                        }

                        @Override
                        public void onTempoAdShowFailed(String reason) {
                                TempoUtils.say("TempoAdapter: onRewardedAdShowFailed: " + reason);
                                listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, reason);
                                //super.onTempoAdShowFailed(reason);
                        }

                        @Override
                        public void onTempoAdClosed() {
                                TempoUtils.say("TempoAdapter: onRewardedAdClosed");
                                listener.onAdClosed();
                                rewardedReady = false;
                                //super.onTempoAdClosed();
                        }

                        @Override
                        public String getTempoAdapterVersion() {
                                TempoUtils.say("TempoAdapter: getTempoAdapterVersion (rewarded, SDK=" + Constants.SDK_VERSION + ", Adapter=" + AdapterConstants.ADAPTER_VERSION + ")");
                                return AdapterConstants.ADAPTER_VERSION;
                        }

                        @Override
                        public String getTempoAdapterType() {
                                TempoUtils.say("TempoAdapter: getTempoAdapterType (rewarded, Type: " + AdapterConstants.ADAPTER_TYPE + ")");
                                return AdapterConstants.ADAPTER_TYPE;
                        }
                };
        }
}