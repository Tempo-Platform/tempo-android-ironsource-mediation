
package com.ironsource.ironsourceui;

import static com.tempoplatform.ads.Constants.TEST_LOG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoManualListener;
import com.ironsource.mediationsdk.utils.IronSourceUtils;
import com.tempoplatform.ads.Constants;


public class IronSourceDemo extends Activity implements ImpressionDataListener {

    private final String TAG = "IronSourceDemo";

    // From IronSource portal
    private final String APP_KEY_PROD = "1bcfe2ae5";
    private final String APP_KEY_DEV = "1a6ad0b75";

    private Button mInterstitialLoadButton;
    private Button mInterstitialShowButton;
    private Button mRewardedLoadButton;
    private Button mRewardedShowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Log.e(TEST_LOG, "onCreate");

        //The integrationHelper is used to validate the integration. Remove the integrationHelper before going live!
        IntegrationHelper.validateIntegration(this);
        initUIElements(); // Setup buttons and UI look
        startIronSourceInitTask(); // Set init tasks in motion
        IronSource.getAdvertiserId(this);

        //Network Connectivity Status
        IronSource.shouldTrackNetworkState(this, true);
        IntegrationHelper.validateIntegration(this);
    }

    /**
     * initialize the UI elements of the activity
     */
    private void initUIElements() {

        // LOAD Rewarded Button(s)
        mRewardedLoadButton = (Button)findViewById(R.id.rw_button_1);

        if(mRewardedLoadButton == null) {
            Log.e(TEST_LOG, "=================================> mRewardedLoadButton null!: ");
        }
        else{

            mRewardedLoadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TEST_LOG, "LoadRewarded clicked: ");
                    IronSource.loadRewardedVideo();
                }
            });
        }



        // SHOW Interstitial Button
        mRewardedShowButton = (Button)findViewById(R.id.rw_button_2);

        if(mRewardedShowButton == null) {
            Log.e(TEST_LOG, "=================================> mRewardedShowButton null!: ");
        }
        else{
            mRewardedShowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TEST_LOG, "ShowRewarded clicked: " + IronSource.isRewardedVideoAvailable());
                    if (IronSource.isRewardedVideoAvailable()) {
                        IronSource.showRewardedVideo("SJB2");
                    }
                    else{
                        Log.e(TEST_LOG, "Rewarded NOT AVAILABLE!");
                    }
                }
            });
        }


        // LOAD Interstitial Button
        mInterstitialLoadButton = (Button) findViewById(R.id.is_button_1);
        mInterstitialLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TEST_LOG, "LoadInterstitial clicked");
                IronSource.loadInterstitial();
            }
        });
        // SHOW Interstitial Button
        mInterstitialShowButton = (Button) findViewById(R.id.is_button_2);
        mInterstitialShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TEST_LOG, "ShowInterstitial clicked: " + IronSource.isInterstitialReady());
                if (IronSource.isInterstitialReady()) {
                    IronSource.showInterstitial("SJB");
                }
                else{
                    Log.e(TEST_LOG, "Interstitial NOT READY!");
                }
            }
        });

        // UI dressings
        TextView versionTV = (TextView) findViewById(R.id.version_txt);
        versionTV.setText(getResources().getString(R.string.version) + " " + IronSourceUtils.getSDKVersion());
    }

    /**
     * Trigger initial IronSource tasks after getting user/advertising ID
     */
    private void startIronSourceInitTask(){
        String advertisingId = IronSource.getAdvertiserId(this);
        Log.e(TEST_LOG, "startIronSourceInitTask: " + advertisingId + " (advertisingId)");
        // We're using an advertisingId as the 'userId'
        initIronSource(getAppKey(), advertisingId);
    }

    /**
     * Initial IronSource tasks
     */
    private void initIronSource(String appKey, String userId) {
        Log.e(TEST_LOG, "initIronSource |  AppKey: " + appKey + " | UserId: " + userId);

        // Initialise listeners
        SetUpLevelPlayInterstitial();
        SetUpLevelPlayRewarded();

        // Add the Impression Data listener
        IronSource.addImpressionDataListener(this);

        // Set the IronSource user id
        IronSource.setUserId(userId);

        // Init the IronSource SDK
        //IronSource.init(this, appKey);

        // Update UI element states
        updateButtonsState();

        //IronSource.setMetaData("is_test_suite", "enable");
        IronSource.init(this, appKey);
    }

    /**
     *  Set the INTERSTITIAL ad listeners
     */
    private void SetUpLevelPlayInterstitial() {
        Log.e(TEST_LOG, "setLevelPlayInterstitialListener");
        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {

            // Invoked when the interstitial ad was loaded successfully.
            // AdInfo parameter includes information about the loaded ad
            @Override
            public void onAdReady(AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdReady (LevelPlay - Interstitial) ********** ");
                handleShowInterstitialButtonState(true);

                String AdUnit = adInfo.getAdUnit();
                String AuctionId = adInfo.getAuctionId();
                String AdNetwork = adInfo.getAdNetwork();
                String Ab = adInfo.getAb();
                String Country = adInfo.getCountry();
                String InstanceId = adInfo.getInstanceId();
                String InstanceName = adInfo.getInstanceName();
                String SegmentName = adInfo.getSegmentName();
                Double Revenue = adInfo.getRevenue();
                String Precision = adInfo.getPrecision();
                String EncryptedCPM = adInfo.getEncryptedCPM();

                Log.e(TEST_LOG, "AD INFO (Interstitial):" +
                        "\n - AdUnit: " + AdUnit +
                        "\n - AuctionId: " + AuctionId +
                        "\n - AdNetwork: " + AdNetwork +
                        "\n - Ab: " + Ab +
                        "\n - Country: " + Country +
                        "\n - InstanceId: " + InstanceId +
                        "\n - InstanceName: " + InstanceName +
                        "\n - SegmentName: " + SegmentName +
                        "\n - Revenue: " + Revenue +
                        "\n - Precision: " + Precision +
                        "\n - EncryptedCPM: " + EncryptedCPM);

            }

            // Indicates that the ad failed to be loaded
            @Override
            public void onAdLoadFailed(IronSourceError error){
                Log.e(TEST_LOG, "*********** onAdLoadFailed (LevelPlay - Interstitial) [" + error + "] ********** ");
                handleShowInterstitialButtonState(false);
            }

            // Invoked when the Interstitial Ad Unit has opened, and user left the application screen.
            // This is the impression indication.
            @Override
            public void onAdOpened(AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdOpened (LevelPlay - Interstitial) ********** ");
            }

            // Invoked when the interstitial ad closed and the user went back to the application screen.
            @Override
            public void onAdClosed(AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdClosed (LevelPlay - Interstitial) ********** ");
                handleShowInterstitialButtonState(false);
            }

            // Invoked when the ad failed to show
            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdShowFailed (LevelPlay - Interstitial)  [" + error + "] ********** ");
                handleShowInterstitialButtonState(false);
            }


            // Invoked when end user clicked on the interstitial ad
            @Override
            public void onAdClicked(AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdClicked (LevelPlay - Interstitial) ********** ");
            }

            // Invoked before the interstitial ad was opened, and before the InterstitialOnAdOpenedEvent is reported.
            // This callback is not supported by all networks, and we recommend using it only if
            // it's supported by all networks you included in your build.
            @Override
            public void onAdShowSucceeded(AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdShowSucceeded (LevelPlay - Interstitial) ********** ");
                String AdUnit = adInfo.getAdUnit();
                String AuctionId = adInfo.getAuctionId();
                String AdNetwork = adInfo.getAdNetwork();
                String Ab = adInfo.getAb();
                String Country = adInfo.getCountry();
                String InstanceId = adInfo.getInstanceId();
                String InstanceName = adInfo.getInstanceName();
                String SegmentName = adInfo.getSegmentName();
                Double Revenue = adInfo.getRevenue();
                String Precision = adInfo.getPrecision();
                String EncryptedCPM = adInfo.getEncryptedCPM();

                Log.e(TEST_LOG, "AD INFO (Interstitial - onAdShowSucceeded):" +
                        "\n - AdUnit: " + AdUnit +
                        "\n - AuctionId: " + AuctionId +
                        "\n - AdNetwork: " + AdNetwork +
                        "\n - Ab: " + Ab +
                        "\n - Country: " + Country +
                        "\n - InstanceId: " + InstanceId +
                        "\n - InstanceName: " + InstanceName +
                        "\n - SegmentName: " + SegmentName +
                        "\n - Revenue: " + Revenue +
                        "\n - Precision: " + Precision +
                        "\n - EncryptedCPM: " + EncryptedCPM);
            }
        });
    }

    /**
     *  Set the REWARDED ad listeners
     */
    private void SetUpLevelPlayRewarded() {
        Log.e(TEST_LOG, "setLevelPlayRewardedVideoListener");
        IronSource.setLevelPlayRewardedVideoManualListener(new LevelPlayRewardedVideoManualListener() {

            // Indicates that there's an available ad.
            // The adInfo object includes information about the ad that was loaded successfully
            // Use this callback instead of onRewardedVideoAvailabilityChanged(true)
            @Override
            public void onAdReady(AdInfo adInfo){

                Log.e(TEST_LOG, "*********** onAdReady (LevelPlay - Rewarded) ********** ");
                handleShowRewardedButtonState(true);

                String AdUnit = adInfo.getAdUnit();
                String AuctionId = adInfo.getAuctionId();
                String AdNetwork = adInfo.getAdNetwork();
                String Ab = adInfo.getAb();
                String Country = adInfo.getCountry();
                String InstanceId = adInfo.getInstanceId();
                String InstanceName = adInfo.getInstanceName();
                String SegmentName = adInfo.getSegmentName();
                Double Revenue = adInfo.getRevenue();
                String Precision = adInfo.getPrecision();
                String EncryptedCPM = adInfo.getEncryptedCPM();

                Log.e(TEST_LOG, "AD INFO (Rewarded):" +
                        "\n - AdUnit: " + AdUnit +
                        "\n - AuctionId: " + AuctionId +
                        "\n - AdNetwork: " + AdNetwork +
                        "\n - Ab: " + Ab +
                        "\n - Country: " + Country +
                        "\n - InstanceId: " + InstanceId +
                        "\n - InstanceName: " + InstanceName +
                        "\n - SegmentName: " + SegmentName +
                        "\n - Revenue: " + Revenue +
                        "\n - Precision: " + Precision +
                        "\n - EncryptedCPM: " + EncryptedCPM);
            }

            // Invoked when the rewarded video failed to load
            @Override
            public void onAdLoadFailed(IronSourceError error){
                Log.e(TEST_LOG, "*********** onAdLoadFailed (LevelPlay - Rewarded) [" + error + "] ********** ");
                handleShowRewardedButtonState(false);
            }

            // The Rewarded Video ad view has opened. Your activity will loose focus
            @Override
            public void onAdOpened(AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdOpened (LevelPlay - Rewarded) ********** ");
            }

            // The Rewarded Video ad view is about to be closed. Your activity will regain its focus
            @Override
            public void onAdClosed(AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdClosed (LevelPlay - Rewarded) ********** ");
                handleShowRewardedButtonState(false);
            }

            // The user completed to watch the video, and should be rewarded.
            // The placement parameter will include the reward data.
            // When using server-to-server callbacks, you may ignore this event and wait for the ironSource server callback
            @Override
            public void onAdRewarded(Placement placement, AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdRewarded (LevelPlay - Rewarded) ********** ");
            }

            // The rewarded video ad was failed to show
            @Override
            public void onAdShowFailed(IronSourceError error, AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdShowFailed (LevelPlay - Rewarded)  [" + error + "] ********** ");
                handleShowRewardedButtonState(false);
            }

            // Invoked when the video ad was clicked.
            // This callback is not supported by all networks, and we recommend using it
            // only if it's supported by all networks you included in your build
            @Override
            public void onAdClicked(Placement placement, AdInfo adInfo){
                Log.e(TEST_LOG, "*********** onAdClicked (LevelPlay - Rewarded) ********** ");
            }
        });
    }

    /**
     * Handle the button state according to the status of the IronSource products
     */
    private void updateButtonsState() {
        handleLoadInterstitialButtonState(true);
        handleLoadRewardedButtonState(true);
        handleShowInterstitialButtonState(false);
        handleShowRewardedButtonState(false);

        //handleVideoButtonState(IronSource.isRewardedVideoAvailable());
        //handleOfferwallButtonState(IronSource.isOfferwallAvailable());
    }

    /**
     * Set the Interstitial button state according to the product's state
     *
     * @param available if the interstitial is available
     */
    public void handleLoadInterstitialButtonState(final boolean available) {
        Log.d(TAG, "handleInterstitialButtonState | available: " + available);
        final String text;
        final int color;
        if (available) {
            color = Color.BLUE;
            text = getResources().getString(R.string.load) + " " + getResources().getString(R.string.is);
        } else {
            color = Color.BLACK;
            text = getResources().getString(R.string.initializing) + " " + getResources().getString(R.string.is);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.e(TEST_LOG, "handleLoadInterstitialButtonState | Color: " + color + ", text: " + text + ", available: " + available);
                mInterstitialLoadButton.setTextColor(color);
                mInterstitialLoadButton.setText(text);
                mInterstitialLoadButton.setEnabled(available);
            }
        });

    }

    /**
     * Set the Show Interstitial button state according to the product's state
     *
     * @param available if the interstitial is available
     */
    public void handleShowInterstitialButtonState(final boolean available) {
        final int color;
        if (available) {
            color = Color.BLUE;
        } else {
            color = Color.BLACK;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.e(TEST_LOG, "handleInterstitialShowButtonState | Color: " + color + ", available: " + available);
                mInterstitialShowButton.setTextColor(color);
                mInterstitialShowButton.setEnabled(available);
            }
        });
    }

    /**
     * Set the Rewarded button state according to the product's state
     *
     * @param available if the interstitial is available
     */
    public void handleLoadRewardedButtonState(final boolean available) {
        Log.d(TAG, "handleInterstitialButtonState | available: " + available);
        final String text;
        final int color;
        if (available) {
            color = Color.BLUE;
            text = getResources().getString(R.string.load) + " " + getResources().getString(R.string.rw);
        } else {
            color = Color.BLACK;
            text = getResources().getString(R.string.initializing) + " " + getResources().getString(R.string.rw);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.e(TEST_LOG, "handleLoadInterstitialButtonState | Color: " + color + ", text: " + text + ", available: " + available);
                mRewardedLoadButton.setTextColor(color);
                mRewardedLoadButton.setText(text);
                mRewardedLoadButton.setEnabled(available);
            }
        });

    }

    /**
     * Set the Show Rewarded button state according to the product's state
     *
     * @param available if the interstitial is available
     */
    public void handleShowRewardedButtonState(final boolean available) {
        final int color;
        if (available) {
            color = Color.BLUE;
        } else {
            color = Color.BLACK;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.e(TEST_LOG, "handleInterstitialShowButtonState | Color: " + color + ", available: " + available);
                mRewardedShowButton.setTextColor(color);
                mRewardedShowButton.setEnabled(available);
            }
        });
    }

    /**
     * Returns appropriate key determined by TempoSDK environment
     */
    private String getAppKey() {
        if(Constants.isProd()) {
            return APP_KEY_PROD;
        }
        else {
            return APP_KEY_DEV;
        }
    }

    // Needed...?
    @Override
    public void onImpressionSuccess(ImpressionData impressionData) {
        // The onImpressionSuccess will be reported when the rewarded video and interstitial ad is opened.
        // For banners, the impression is reported on load success.
        if (impressionData != null) {
            Log.d(TAG, "onImpressionSuccess " + impressionData);
        }
    }

    public void showRewardDialog(Placement placement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(IronSourceDemo.this);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setTitle(getResources().getString(R.string.rewarded_dialog_header));
        builder.setMessage(getResources().getString(R.string.rewarded_dialog_message) + " " + placement.getRewardAmount() + " " + placement.getRewardName());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.e(TEST_LOG, "onPointerCaptureChanged");
        super.onPointerCaptureChanged(hasCapture);
    }
    @Override
    protected void onResume() {
        Log.e(TEST_LOG, "onResume");
        super.onResume();
        // call the IronSource onResume method
        IronSource.onResume(this);
        updateButtonsState();
    }
    @Override
    protected void onPause() {
        Log.e(TEST_LOG, "onPause");
        super.onPause();
        // call the IronSource onPause method
        IronSource.onPause(this);
        updateButtonsState();
    }




}


