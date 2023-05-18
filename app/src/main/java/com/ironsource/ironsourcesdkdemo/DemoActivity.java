
package com.ironsource.ironsourcesdkdemo;

import static com.tempoplatform.ads.Constants.TEST_LOG;

import com.ironsource.adapters.custom.tempo.TempoCustomAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ironsource.adapters.supersonicads.SupersonicConfig;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.impressionData.ImpressionData;
import com.ironsource.mediationsdk.impressionData.ImpressionDataListener;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.ironsource.mediationsdk.sdk.LevelPlayRewardedVideoListener;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.ironsource.mediationsdk.utils.IronSourceUtils;


public class DemoActivity extends Activity implements LevelPlayRewardedVideoListener, RewardedVideoListener, OfferwallListener, LevelPlayInterstitialListener, InterstitialListener, ImpressionDataListener {

    private final String TAG = "DemoActivity";

    // TODO find your app key in IronSource portal
    private final String APP_KEY = "1a0348c9d";
    private final String FALLBACK_USER_ID = "myUserId";
    private Button mVideoButton;
    private Button mInterstitialLoadButton;
    private Button mInterstitialShowButton;
    private Button mOfferwallButton;
    private Placement mPlacement;
    private FrameLayout mBannerParentLayout;
    private IronSourceBannerLayout mIronSourceBannerLayout;



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
        // Rewarded Button(s)
        mVideoButton = (Button) findViewById(R.id.rv_button);
        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TEST_LOG, "RewardedAd clicked: " + IronSource.isRewardedVideoAvailable());
                if (IronSource.isRewardedVideoAvailable())
                    IronSource.showRewardedVideo();
                else{
                    Log.e(TEST_LOG, "RewardedAd NOT AVAILABLE!");
                }
            }
        });
        Log.e(TEST_LOG, "initUIElements called (Rewarded button set up)");


        // Interstitial Button(s)
        mInterstitialLoadButton = (Button) findViewById(R.id.is_button_1);
        mInterstitialLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TEST_LOG, "LoadInterstitial clicked");
                IronSource.loadInterstitial();
            }
        });
        Log.e(TEST_LOG, "initUIElements called (Load Interstitial button set up)");
        mInterstitialShowButton = (Button) findViewById(R.id.is_button_2);
        mInterstitialShowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TEST_LOG, "LoadInterstitial clicked: " + IronSource.isInterstitialReady());
                if (IronSource.isInterstitialReady()) {
                    IronSource.showInterstitial();
                }
                else{
                    Log.e(TEST_LOG, "Interstitial NOT READY!");
                }
            }
        });
        Log.e(TEST_LOG, "initUIElements called (Show Interstitial button set up)");


        // Offerwall Button (whatever the hell this is...?
        mOfferwallButton = (Button) findViewById(R.id.ow_button);
        mOfferwallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show the offerwall
                if (IronSource.isOfferwallAvailable())
                    IronSource.showOfferwall();
            }
        });
        Log.e(TEST_LOG, "initUIElements called (Show Interstitial button set up)");

        // UI dressings
        TextView versionTV = (TextView) findViewById(R.id.version_txt);
        versionTV.setText(getResources().getString(R.string.version) + " " + IronSourceUtils.getSDKVersion());
        mBannerParentLayout = (FrameLayout) findViewById(R.id.banner_footer);
    }

    /**
     * Trigger initial IronSource tasks after getting user/advertising ID
     */
    private void startIronSourceInitTask(){
        String advertisingId = IronSource.getAdvertiserId(DemoActivity.this);
        Log.e(TEST_LOG, "startIronSourceInitTask: " + advertisingId + " (advertisingId)");
        // We're using an advertisingId as the 'userId'
        initIronSource(APP_KEY, advertisingId);
    }

    /**
     * Initial IronSource tasks
     */
    private void initIronSource(String appKey, String userId) {
        Log.e(TEST_LOG, "initIronSource |  AppKey: " + appKey + " | UserId: " + userId);

        // Set the IronSource rewarded video listener
        //IronSource.setRewardedVideoListener(this);
        IronSource.setLevelPlayRewardedVideoListener(this);

        // Set the interstitial listener
        //IronSource.setInterstitialListener(this);
        IronSource.setLevelPlayInterstitialListener(this);

        // Set the IronSource offerwall listener
        //IronSource.setOfferwallListener(this);

//        // set client side callbacks for the offerwall
//        SupersonicConfig.getConfigObj().setClientSideCallbacks(true);

        // Add the Impression Data listener
        IronSource.addImpressionDataListener(this);

        // Set the IronSource user id
        IronSource.setUserId(userId);

        // Init the IronSource SDK
        IronSource.init(this, appKey);

        // Update UI element states
        updateButtonsState();

//        // In order to work with IronSourceBanners you need to add Providers who support banner ad unit and uncomment next line
//         createAndloadBanner();
    }

    /**
     * Handle the button state according to the status of the IronSource products
     */
    private void updateButtonsState() {
        Log.e(TEST_LOG, "updateButtonsState |  IronSource.isRewardedVideoAvailable(): " + IronSource.isRewardedVideoAvailable());

        handleVideoButtonState(IronSource.isRewardedVideoAvailable());
        //handleOfferwallButtonState(IronSource.isOfferwallAvailable());
        handleLoadInterstitialButtonState(true);
        handleInterstitialShowButtonState(false);
    }

    /**
     * Set the Rewareded Video button state according to the product's state
     *
     * @param available if the video is available
     */
    public void handleVideoButtonState(final boolean available) {
        final String text;
        final int color;
        if (available) {
            color = Color.BLUE;
            text = getResources().getString(R.string.show) + " " + getResources().getString(R.string.rv);
        } else {
            color = Color.BLACK;
            text = getResources().getString(R.string.initializing) + " " + getResources().getString(R.string.rv);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TEST_LOG, "handleVideoButtonState | Color: " + color + ", text: " + text + ", available: " + available);
                mVideoButton.setTextColor(color);
                mVideoButton.setText(text);
                mVideoButton.setEnabled(available);
            }
        });
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
                Log.e(TEST_LOG, "handleLoadInterstitialButtonState | Color: " + color + ", text: " + text + ", available: " + available);
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
    public void handleInterstitialShowButtonState(final boolean available) {
        final int color;
        if (available) {
            color = Color.BLUE;
        } else {
            color = Color.BLACK;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TEST_LOG, "handleInterstitialShowButtonState | Color: " + color + ", available: " + available);
                mInterstitialShowButton.setTextColor(color);
                mInterstitialShowButton.setEnabled(available);
            }
        });
    }


    // --------- IronSource Rewarded Video Listener ---------

    @Override
    public void onRewardedVideoAdOpened() {
        // called when the video is opened
        Log.d(TAG, "onRewardedVideoAdOpened");
    }

    @Override
    public void onRewardedVideoAdClosed() {
        // called when the video is closed
        Log.d(TAG, "onRewardedVideoAdClosed");
        // here we show a dialog to the user if he was rewarded
        if (mPlacement != null) {
            // if the user was rewarded
            showRewardDialog(mPlacement);
            mPlacement = null;
        }
    }

    @Override
    public void onRewardedVideoAvailabilityChanged(boolean b) {
        // called when the video availbility has changed
        Log.d(TAG, "onRewardedVideoAvailabilityChanged" + " " + b);
        handleVideoButtonState(b);
    }

    @Override
    public void onRewardedVideoAdStarted() {
        // called when the video has started
        Log.d(TAG, "onRewardedVideoAdStarted");
    }

    @Override
    public void onRewardedVideoAdEnded() {
        // called when the video has ended
        Log.d(TAG, "onRewardedVideoAdEnded");
    }

    @Override
    public void onRewardedVideoAdRewarded(Placement placement) {
        // called when the video has been rewarded and a reward can be given to the user
        Log.d(TAG, "onRewardedVideoAdRewarded" + " " + placement);
        mPlacement = placement;

    }

    @Override
    public void onRewardedVideoAdShowFailed(IronSourceError ironSourceError) {
        // called when the video has failed to show
        // you can get the error data by accessing the IronSourceError object
        // IronSourceError.getErrorCode();
        // IronSourceError.getErrorMessage();
        Log.d(TAG, "onRewardedVideoAdShowFailed" + " " + ironSourceError);
    }

    @Override
    public void onRewardedVideoAdClicked(Placement placement) {

    }


    // --------- IronSource Offerwall Listener ---------

    @Override
    public void onOfferwallAvailable(boolean available) {
        handleOfferwallButtonState(available);
    }

    @Override
    public void onOfferwallOpened() {
        // called when the offerwall has opened
        Log.d(TAG, "onOfferwallOpened");
    }

    @Override
    public void onOfferwallShowFailed(IronSourceError ironSourceError) {
        // called when the offerwall failed to show
        // you can get the error data by accessing the IronSourceError object
        ironSourceError.getErrorCode();
        ironSourceError.getErrorMessage();
        Log.d(TAG, "onOfferwallShowFailed" + " " + ironSourceError);
    }

    @Override
    public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {
        Log.d(TAG, "onOfferwallAdCredited" + " credits:" + credits + " totalCredits:" + totalCredits + " totalCreditsFlag:" + totalCreditsFlag);
        return false;
    }

    @Override
    public void onGetOfferwallCreditsFailed(IronSourceError ironSourceError) {
        // you can get the error data by accessing the IronSourceError object
        // IronSourceError.getErrorCode();
        // IronSourceError.getErrorMessage();
        Log.d(TAG, "onGetOfferwallCreditsFailed" + " " + ironSourceError);
    }

    @Override
    public void onOfferwallClosed() {
        // called when the offerwall has closed
        Log.d(TAG, "onOfferwallClosed");
    }

    // --------- IronSource Interstitial Listener ---------

    @Override
    public void onInterstitialAdClicked() {
        // called when the interstitial has been clicked
        Log.d(TAG, "onInterstitialAdClicked");
    }

    @Override
    public void onInterstitialAdReady() {
        // called when the interstitial is ready
        Log.d(TAG, "onInterstitialAdReady");
        handleInterstitialShowButtonState(true);
    }

    @Override
    public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
        // called when the interstitial has failed to load
        // you can get the error data by accessing the IronSourceError object
//         IronSourceError.getErrorCode();
//         IronSourceError.getErrorMessage();
        Log.d(TAG, "onInterstitialAdLoadFailed" + " " + ironSourceError);
        handleInterstitialShowButtonState(false);
    }

    @Override
    public void onInterstitialAdOpened() {
        // called when the interstitial is shown
        Log.d(TAG, "onInterstitialAdOpened");
    }

    @Override
    public void onInterstitialAdClosed() {
        // called when the interstitial has been closed
        Log.d(TAG, "onInterstitialAdClosed");
        handleInterstitialShowButtonState(false);
    }

    @Override
    public void onInterstitialAdShowSucceeded() {
        // called when the interstitial has been successfully shown
        Log.d(TAG, "onInterstitialAdShowSucceeded");
    }

    @Override
    public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
        // called when the interstitial has failed to show
        // you can get the error data by accessing the IronSourceError object
        // IronSourceError.getErrorCode();
        // IronSourceError.getErrorMessage();
        Log.d(TAG, "onInterstitialAdShowFailed" + " " + ironSourceError);
        handleInterstitialShowButtonState(false)  ;
    }

    // --------- Impression Data Listener ---------
    @Override
    public void onImpressionSuccess(ImpressionData impressionData) {
        // The onImpressionSuccess will be reported when the rewarded video and interstitial ad is opened.
        // For banners, the impression is reported on load success.
        if (impressionData != null) {
            Log.d(TAG, "onImpressionSuccess " + impressionData);
        }
    }

    public void showRewardDialog(Placement placement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DemoActivity.this);
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



    // ============================================>>.???
    @Override
    public void onAdReady(AdInfo adInfo) {

    }

    @Override
    public void onAdLoadFailed(IronSourceError ironSourceError) {

    }

    @Override
    public void onAdOpened(AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdOpened");
    }

    @Override
    public void onAdShowSucceeded(AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdShowSucceeded");
    }

    @Override
    public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdShowFailed");
    }

    @Override
    public void onAdClicked(Placement placement, AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdClicked");
    }

    @Override
    public void onAdRewarded(Placement placement, AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdRewarded");
    }

    @Override
    public void onAdClicked(AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdClicked");
    }

    @Override
    public void onAdClosed(AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdClosed");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.e(TEST_LOG, "onPointerCaptureChanged");
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onAdAvailable(AdInfo adInfo) {
        Log.e(TEST_LOG, "onAdAvailable");
    }

    @Override
    public void onAdUnavailable() {
        Log.e(TEST_LOG, "onAdUnavailable");
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



    /**  ======== NOT NEEDED ====== **/


    /**
     * Creates and loads IronSource Banner
     */
    private void createAndloadBanner() {
        // choose banner size
        ISBannerSize size = ISBannerSize.BANNER;

        // instantiate IronSourceBanner object, using the IronSource.createBanner API
        mIronSourceBannerLayout = IronSource.createBanner(this, size);

        // add IronSourceBanner to your container
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mBannerParentLayout.addView(mIronSourceBannerLayout, 0, layoutParams);

        if (mIronSourceBannerLayout != null) {
            // set the banner listener
            mIronSourceBannerLayout.setBannerListener(new BannerListener() {
                @Override
                public void onBannerAdLoaded() {
                    Log.d(TAG, "onBannerAdLoaded");
                    // since banner container was "gone" by default, we need to make it visible as soon as the banner is ready
                    mBannerParentLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBannerAdLoadFailed(IronSourceError error) {
                    Log.d(TAG, "onBannerAdLoadFailed" + " " + error);
                }

                @Override
                public void onBannerAdClicked() {
                    Log.d(TAG, "onBannerAdClicked");
                }

                @Override
                public void onBannerAdScreenPresented() {
                    Log.d(TAG, "onBannerAdScreenPresented");
                }

                @Override
                public void onBannerAdScreenDismissed() {
                    Log.d(TAG, "onBannerAdScreenDismissed");
                }

                @Override
                public void onBannerAdLeftApplication() {
                    Log.d(TAG, "onBannerAdLeftApplication");
                }
            });

            // load ad into the created banner
            IronSource.loadBanner(mIronSourceBannerLayout);
        } else {
            Toast.makeText(DemoActivity.this, "IronSource.createBanner returned null", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Destroys IronSource Banner and removes it from the container
     *
     */
    private void destroyAndDetachBanner() {
        IronSource.destroyBanner(mIronSourceBannerLayout);
        if (mBannerParentLayout != null) {
            mBannerParentLayout.removeView(mIronSourceBannerLayout);
        }
    }


    /**
     * Set the Rewareded Video button state according to the product's state
     *
     * @param available if the offerwall is available
     */
    public void handleOfferwallButtonState(final boolean available) {
        final String text;
        final int color;
        if (available) {
            color = Color.BLUE;
            text = getResources().getString(R.string.show) + " " + getResources().getString(R.string.ow);
        } else {
            color = Color.BLACK;
            text = getResources().getString(R.string.initializing) + " " + getResources().getString(R.string.ow);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOfferwallButton.setTextColor(color);
                mOfferwallButton.setText(text);
                mOfferwallButton.setEnabled(available);

            }
        });

    }


}


