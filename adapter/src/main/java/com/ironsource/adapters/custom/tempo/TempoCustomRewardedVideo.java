package com.ironsource.adapters.custom.tempo;

import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
import static com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors.ADAPTER_ERROR_INTERNAL;
import static com.tempoplatform.ads.Constants.TEST_LOG;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Keep;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrors;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.tempoplatform.ads.RewardedView;

import org.json.JSONException;
import org.json.JSONObject;

@Keep
@SuppressWarnings("unused")
public class TempoCustomRewardedVideo extends BaseRewardedVideo <TempoCustomAdapter> {

        private RewardedView rewardedView;
        private boolean rewardedReady;

        public TempoCustomRewardedVideo(NetworkSettings networkSettings) {
                super(networkSettings);
                Log.d(TEST_LOG, "TempoCustomRewardedVideo initialised *");
        }

        @Override
        public void loadAd(AdData adData, Activity activity, RewardedVideoAdListener listener) {

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

                com.tempoplatform.ads.RewardedAdListener tempoListener = new com.tempoplatform.ads.RewardedAdListener() {
                        @Override
                        public void onRewardedAdFetchSucceeded() {
                                Log.d(TEST_LOG, "Rewarded ad fetch succeeded");
                                super.onRewardedAdFetchSucceeded();
                                listener.onAdLoadSuccess(); // Indicates that interstitial ad was loaded successfully
                                rewardedReady = true;
                        }

                        @Override
                        public void onRewardedAdFetchFailed() {
                                Log.d(TEST_LOG, "Rewarded ad fetch failed");
                                super.onRewardedAdFetchFailed();
                                int adapterErrorCode = ADAPTER_ERROR_INTERNAL;
                                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The interstitial ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                                //listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, null); // The ad could not be displayed
                        }

                        @Override
                        public void onRewardedAdDisplayed() {
                                Log.d(TEST_LOG, "Rewarded ad fetch displayed");
                                super.onRewardedAdDisplayed();
                                listener.onAdShowSuccess();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                                Log.d(TEST_LOG, "Rewarded ad closed");
                                super.onRewardedAdClosed();
                                listener.onAdClosed();
                                rewardedReady = false;
                        }

                        @Override
                        public String onVersionExchange(String sdkVersion) {
                                Log.d(TEST_LOG, "Version exchange triggered");
                                TempoCustomAdapter.dynSdkVersion = sdkVersion;
                                return TempoCustomAdapter.ADAPTER_VERSION;
                        }

                        @Override
                        public String onGetAdapterType() {
                                Log.d(TEST_LOG, "Adapter Type requested (R)");
                                return TempoCustomAdapter.ADAPTER_TYPE;
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
                Log.d(TEST_LOG, "ShowAd called (" + rewardedReady + "): " + adData.getConfiguration());
                if (rewardedReady)  {
                        rewardedView.showAd();
                } else {
                        ironSourceAdlistener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, "Interstitial Ad not ready");
                }
        }

        @Override
        public boolean isAdAvailable(AdData adData) {
                Log.d(TEST_LOG, "IsAdAvailable called: " + rewardedReady);
                return rewardedReady;
        }
}