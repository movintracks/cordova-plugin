package com.movintracks.cordovamovintracks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import io.movintracks.library.Constants;
import io.movintracks.library.Movintracks;
import io.movintracks.library.interfaces.ActionInterface;
import io.movintracks.library.interfaces.IMovintracks;

public class MovintracksPlugin extends CordovaPlugin{

    private final String TAG = MovintracksPlugin.class.getSimpleName();

    private final String ACTION_DOWNLOAD_DATA = "downloadData";
    private final String ACTION_INIT_CUSTOM_MOVINTRACKS = "initCustomMovintracks";
    private final String ACTION_GET_AVAILABLE_BEACONS = "getBeaconsAvailable";
    private final String ACTION_SET_LAUNCH_WEB_CALLBACK = "launchWebView";
    private final String ACTION_SET_CUSTOM_CALLBACK = "customCallBackAction";
    private final String ACTION_SET_GEOFENCE_NOT_AVAILABLE_CALLBACK = "geofenceNotAvailableCallBackAction";
    private final String ACTION_SET_BT_DISABLED_CALLBACK = "bluetoothDisabledCallBackAction";
    private final String ACTION_SET_BT_LE_NOT_AVAILABLE_CALLBACK = "bluetoothLENotAvailableCallBackAction";

    private Movintracks movintracks;

    @Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "[[[[[[[[[execute]]]]]]]]] JSONArray => " + args.toString());
        Log.w(TAG, "----------------------------------- ACTION ===> " + action);
        if (ACTION_INIT_CUSTOM_MOVINTRACKS.equals(action)) {
            Log.d(TAG, "---------- ACTION_INIT_CUSTOM_MOVINTRACKS ----------");
            // initMovintracks(args, callbackContext, true);
            return true;
        }else if (ACTION_DOWNLOAD_DATA.equals(action)) {
            Log.d(TAG, "---------- ACTION_DOWNLOAD_DATA ----------");
            downloadData(callbackContext);
            return true;
        }else if(ACTION_GET_AVAILABLE_BEACONS.equals(action)){
            Log.d(TAG, "---------- ACTION_GET_AVAILABLE_BEACONS ----------");
            getBeaconsAvailable(callbackContext);
            return true;
        }else if(ACTION_SET_LAUNCH_WEB_CALLBACK.equals(action)){
            Log.d(TAG, "---------- ACTION_SET_LAUNCH_WEB_CALLBACK ----------");
            setLaunchWebViewCallback(callbackContext);
            return true;
        }else if(ACTION_SET_CUSTOM_CALLBACK.equals(action)){
            Log.d(TAG, "---------- ACTION_SET_CUSTOM_CALLBACK ----------");
            setCustomCallback(args, callbackContext);
            return true;
        }else if(ACTION_SET_GEOFENCE_NOT_AVAILABLE_CALLBACK.equals(action)){
            Log.d(TAG, "---------- ACTION_SET_GEOFENCE_NOT_AVAILABLE_CALLBACK ----------");
            setGeofenceNotAvailableCallback(callbackContext);
            return true;
        }else if(ACTION_SET_BT_DISABLED_CALLBACK.equals(action)){
            Log.d(TAG, "---------- ACTION_SET_BT_DISABLED_CALLBACK ----------");
            setBluetoothDisabledCallback(callbackContext);
            return true;
        }else if(ACTION_SET_BT_LE_NOT_AVAILABLE_CALLBACK.equals(action)){
            Log.d(TAG, "---------- ACTION_SET_BT_LE_NOT_AVAILABLE_CALLBACK ----------");
            setBluetoothLENotAvailableCallback(callbackContext);
            return true;
        }
        return false;
    }

    private void initMovintracks(JSONArray args, CallbackContext callbackContext, boolean isCustom){
        try {
            JSONObject jsonConfiguration;
            try {
                jsonConfiguration = args.getJSONObject(0);
            } catch (JSONException e) {
                Utils.error(callbackContext, "Configuration not specified or not specified correctly.");
                return;
            }

            String rootServer, apiKey, apiSecret, googleProjectNumber, googleAPIKey;
            try{
                rootServer = jsonConfiguration.getString(LocalConstants.KEY_ROOT_SERVER);
            }catch(JSONException e){
                Utils.error(callbackContext, "Root server not specified or not specified correctly.");
                return;
            }

            try{
                apiKey = jsonConfiguration.getString(LocalConstants.KEY_API_KEY);
            }catch(JSONException e){
                Utils.error(callbackContext, "API key not specified or not specified correctly.");
                return;
            }

            try{
                apiSecret = jsonConfiguration.getString(LocalConstants.KEY_API_SECRET);
            }catch(JSONException e){
                Utils.error(callbackContext, "API secret not specified or not specified correctly.");
                return;
            }

            try{
                googleProjectNumber = jsonConfiguration.getString(LocalConstants.KEY_GOOGLE_PROJECT_NUMBER);
            }catch(JSONException e){
                Utils.error(callbackContext, "Google project number not specified or not specified correctly.");
                return;
            }

            try{
                googleAPIKey = jsonConfiguration.getString(LocalConstants.KEY_GOOGLE_API_KEY);
            }catch(JSONException e){
                Utils.error(callbackContext, "Google API key not specified or not specified correctly.");
                return;
            }

            // MovintracksApp.initMovintracks(rootServer, apiKey, apiSecret, googleProjectNumber, googleAPIKey, isCustom);

            callbackContext.success();
        } catch (Exception e) {
            Utils.error(callbackContext, e.getMessage());
        }
    }

    private void downloadData(final CallbackContext callbackContext){
        try {
            Movintracks.getInstance().getBrainManager().downloadData();
        } catch (Exception e) {
            Utils.error(callbackContext, "Error while downloading data");
            return;
        }
        callbackContext.success();
    }

    private void getBeaconsAvailable(final CallbackContext callbackContext){
        try {
            int iBeaconsAvailable = Movintracks.getInstance().beaconsAvailable();
            JSONObject json = new JSONObject();
            json.put(LocalConstants.KEY_BEACONS_AVAILABLE, iBeaconsAvailable);
            callbackContext.success(json.toString());
            Log.e(TAG, String.format("----------------------------------------> iBeaconsAvailable: %d", iBeaconsAvailable));
        } catch (JSONException e) {
            Utils.error(callbackContext, "Error while obtaining available beacons");
            return;
        }
    }

    private void setLaunchWebViewCallback(final CallbackContext callbackContext){
        MovintracksApp.launchWebViewCallback = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void setCustomCallback(JSONArray config, final CallbackContext callbackContext){
        JSONObject jsonConfiguration;
        try {
            jsonConfiguration = config.getJSONObject(0);
        } catch (JSONException e) {
            Utils.error(callbackContext, "Configuration not specified or not specified correctly.");
            return;
        }

        String nameOfCustomCallback;
        try{
            nameOfCustomCallback = jsonConfiguration.getString(LocalConstants.KEY_NAME);
        }catch(JSONException e){
            Utils.error(callbackContext, "Callback name not specified or not specified correctly.");
            return;
        }

        Log.d(TAG, "setCustomCallback(String nameOfCustomCallback => " + nameOfCustomCallback);
        MovintracksApp.customCallbacksMap.put(nameOfCustomCallback, callbackContext);
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void setGeofenceNotAvailableCallback(final CallbackContext callbackContext){
        MovintracksApp.geofenceNotAvailableCallback = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void setBluetoothDisabledCallback(final CallbackContext callbackContext){
        MovintracksApp.bluetoothDisabledCallback = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }

    private void setBluetoothLENotAvailableCallback(final CallbackContext callbackContext){
        MovintracksApp.bluetoothLENotAvailableCallback = callbackContext;
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}
