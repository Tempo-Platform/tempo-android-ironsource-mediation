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

@Keep
@SuppressWarnings("unused")
public class TempoCustomRewardedVideo extends BaseRewardedVideo <TempoCustomAdapter> {

        public String dynSdkVersion = "1.0.1"; // TODO: What's the relationship with base class version?

        private RewardedView rewardedView;
        private boolean rewardedReady;

        public TempoCustomRewardedVideo(NetworkSettings networkSettings) {
                super(networkSettings);
                Log.d(TEST_LOG, "TempoCustomRewardedVideo initialised");
        }

        @Override
        public void loadAd(AdData adData, Activity activity, RewardedVideoAdListener listener) {
                String AppId = "8";// TODO: Get AppID
                String location = "US"; // TODO: Get Location
                String placementId = "InterstitialPlacementID"; // TODO: Get PlacementID
                String cpmFloorStr = "20"; // TODO: Get CPM
                Log.e(TEST_LOG, "TempoCustomAdapter created: " + AppId + " | " + location + " | " + placementId + " | " + cpmFloorStr );
                Float cpmFloor = cpmFloorStr != null ? Float.parseFloat(cpmFloorStr) : 0.0F;

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
                                listener.onAdLoadFailed(ADAPTER_ERROR_TYPE_NO_FILL, ADAPTER_ERROR_INTERNAL, null); // The interstitial ad failed to load. Use ironSource ErrorTypes (No Fill / Other)
                                listener.onAdShowFailed(ADAPTER_ERROR_INTERNAL, null); // The ad could not be displayed
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
//                /.dynSdkVersion = sdkVersion;
                                dynSdkVersion = sdkVersion;
                                return TempoCustomAdapter.ADAPTER_VERSION;
                                //return super.getAdapterVersion(); // TODO: Why doesn't this work??
                        }
                };

                activity.runOnUiThread(() -> {
                        rewardedView = new RewardedView("8", activity);
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