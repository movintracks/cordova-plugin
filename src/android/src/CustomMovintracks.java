package com.movintracks.cordovamovintracks;

import android.util.Log;
import android.app.Application;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import io.movintracks.library.Movintracks;

public class CustomMovintracks extends Movintracks{

    private final String TAG = CustomMovintracks.class.getSimpleName();

    /**
     *
     * @param application
     * @param rootServer
     * @param apiKey
     * @param apiSecret
     * @param googleProjectNumber
     * @param googleAPIKey
     * @param bExecuteIntent
     */
    public CustomMovintracks(Application application, String rootServer, String apiKey, String apiSecret, String googleProjectNumber, String googleAPIKey, boolean bExecuteIntent) {
        super(application, rootServer, apiKey, apiSecret, googleProjectNumber, googleAPIKey, bExecuteIntent);
    }

    /**
     * Function called by Movintracks SDK to notify the app that a geofence is not available.
     */
    @Override
    public void geofenceNotAvailable(){
        Log.d(TAG, "geofenceNotAvailable()");
        PluginResult result = new PluginResult(PluginResult.Status.OK);

        // result.setKeepCallback(false);
        if (MovintracksApp.geofenceNotAvailableCallback != null) {
            MovintracksApp.geofenceNotAvailableCallback.sendPluginResult(result);
            // callbackContext = null;
        }
    }

	/**
     * Function to notify the app that bluetooth is disabled, so the SDK won't be able to listen beacons. Parameter message is the message configured in Zones on Movintracks Dashboard.
     */
    @Override
    public void bluetoothDisabled(String message){
		Log.d(TAG, "bluetoothDisabled()");
        try{
            JSONObject json = new JSONObject();
            json.put(LocalConstants.KEY_MESSAGE, message);
            PluginResult result = new PluginResult(PluginResult.Status.OK, json.toString());

            // result.setKeepCallback(false);
            if (MovintracksApp.bluetoothDisabledCallback != null) {
                MovintracksApp.bluetoothDisabledCallback.sendPluginResult(result);
                // callbackContext = null;
            }
        }catch(JSONException e){
            Utils.error(MovintracksApp.bluetoothDisabledCallback, e.getMessage());
        }
    }
	
	/**
     * Function to notify the app that the device don't have low energy bluetooth capabilities.
     */
    @Override
    public void bluetoothLENotAvailable(){
        Log.d(TAG, "bluetoothLENotAvailable()");
        PluginResult result = new PluginResult(PluginResult.Status.OK);

        // result.setKeepCallback(false);
        if (MovintracksApp.bluetoothLENotAvailableCallback != null) {
            MovintracksApp.bluetoothLENotAvailableCallback.sendPluginResult(result);
            // callbackContext = null;
        }
    }
}
