package com.ironsource.adapters.custom.tempo;

import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.tempoplatform.ads.TempoUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AdapterUtils {

    /**
     * Get Ad ID property value from IS adData
     */
    protected static String extractAppId(AdData adData) {
        String appId = "";
        try {
            JSONObject obj = new JSONObject(adData.getConfiguration());
            appId = obj.getString(AdapterConstants.PARAM_APP_ID);
        } catch (JSONException e) {
            TempoUtils.warn("TempoAdapter: Could not get AppID from adData", true);
        }
        return appId;
    }

    /**
     * Get CMP Floor property value from IS adData
     */
    protected static Float extractCpmFloor(AdData adData) {
        Float cpmFloor = 0.0F;
        try {
            JSONObject obj = new JSONObject(adData.getConfiguration());
            String cpmFloorStr = obj.getString(AdapterConstants.PARAM_CPM_FLR);
            cpmFloor = parseCpmFloor(cpmFloorStr);
        } catch (JSONException e) {
            TempoUtils.warn("TempoAdapter: Could not get CPMFloor from adData", true);
        }
        return cpmFloor;
    }


    /**
     * Parse CPM Floor string to get float value
     */
    protected static Float parseCpmFloor(String cpmFloorStr) {
        try {
            double decimalNumber = Double.parseDouble(cpmFloorStr);
            return Float.parseFloat(String.valueOf(decimalNumber));
        } catch (NumberFormatException e) {
            TempoUtils.warn("TempoAdapter: Invalid CPM floor value: " + cpmFloorStr, true);
            return 0.0F;
        }
    }
}
