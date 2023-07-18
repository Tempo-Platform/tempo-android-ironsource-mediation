package com.ironsource.adapters.custom.tempo;

import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;

import android.app.Activity;

import androidx.annotation.Keep;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.tempoplatform.ads.Constants;
import com.tempoplatform.ads.RewardedView;
import com.tempoplatform.ads.TempoUtils;

import org.json.JSONException;
import org.json.JSONObject;

@Keep
@SuppressWarnings("unused")
public class TempoCustomRewardedVideo extends BaseRewardedVideo <TempoCustomAdapter> {

        private RewardedView rewardedView;
        private boolean rewardedReady;

        public TempoCustomRewardedVideo(NetworkSettings networkSettings) {
                super(networkSettings);
                TempoUtils.Say("TempoAdapter: init rewarded");
        }

        @Override
        public void loadAd(AdData adData, Activity activity, RewardedVideoAdListener listener) {
                TempoUtils.Say("TempoAdapter: loadAd (r)", true);

                // Get App ID
                String appId = "";
                JSONObject obj = new JSONObject(adData.getConfiguration());
                try {
                        appId = obj.getString(AdapterConstants.PARAM_APP_ID);
                } catch (JSONException e) {
                        TempoUtils.Warn("TempoAdapter: Could not get AppID from adData", true);
                }

                // Get CPM Floor
                String cpmFloorStr;
                try {
                        // Confirm string is legit decimal value
                        cpmFloorStr = obj.getString(AdapterConstants.PARAM_CPM_FLR);
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

                com.tempoplatform.ads.TempoAdListener tempoListener = new com.tempoplatform.ads.TempoAdListener() {
                        @Override
                        public void onTempoAdFetchSucceeded() {
                                TempoUtils.Say("TempoAdapter: onRewardedAdFetchSucceeded",true);
                                listener.onAdLoadSuccess(); // Indicates that rewarded ad was loaded successfully
                                rewardedReady = true;
                                //super.onRewardedAdFetchSucceeded();
                        }

                        @Override
                        public void onTempoAdFetchFailed() {
                                TempoUtils.Say("TempoAdapter: onRewardedAdFetchFailed",true);
                                super.onTempoAdFetchFailed();
                                int adapterErrorCode = ADAPTER_ERROR_INTERNAL;
                                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The rewarded ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                                //super.onRewardedAdFetchFailed();
                        }

                        @Override
                        public void onTempoAdDisplayed() {
                                TempoUtils.Say("TempoAdapter: onRewardedAdDisplayed",true);
                                listener.onAdShowSuccess();
                                //super.onRewardedAdDisplayed();
                        }

                        @Override
                        public void onTempoAdClosed() {
                                TempoUtils.Say("TempoAdapter: onRewardedAdClosed",true);
                                listener.onAdClosed();
                                rewardedReady = false;
                                //super.onRewardedAdClosed();
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
                activity.runOnUiThread(() -> {
                        rewardedView = new RewardedView(finalAppId, activity);
                        if (location != null) {
                                rewardedView.loadAd(activity, tempoListener, cpmFloor, placementId, location);
                        } else {
                                rewardedView.loadAd(activity, tempoListener, cpmFloor, placementId);
                        }
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