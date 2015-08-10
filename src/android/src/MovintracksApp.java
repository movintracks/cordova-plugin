package com.movintracks.cordovamovintracks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;

import org.json.JSONException;
import org.json.JSONObject;

import com.movintracks.cordovamovintracks.WebViewActivity;

import io.movintracks.library.Constants;
import io.movintracks.library.Movintracks;
import io.movintracks.library.interfaces.ActionInterface;
import io.movintracks.library.interfaces.IMovintracks;

public class MovintracksApp extends Application implements IMovintracks {

	private final String TAG = "MovintracksApp";

	public static HashMap<String, CallbackContext> customCallbacksMap;
	public static CallbackContext launchWebViewCallback;
    public static CallbackContext geofenceNotAvailableCallback;
    public static CallbackContext bluetoothDisabledCallback;
    public static CallbackContext bluetoothLENotAvailableCallback;

	private static MovintracksApp sInstance;
	public static Movintracks movintracks;
	public static Activity mActivity;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(TAG, "onCreate() called!!!");
		
		sInstance = this;

		customCallbacksMap = new HashMap<String, CallbackContext>();

		boolean isCustom = Utils.getIsMovintracksCustom(this);

		try{
			String[] params = Utils.getMovintracksData(this);
			Log.d(TAG, "Initiating Movintracks in onCreate()");
			initMovintracks(params [0], params [1], params [2], params [3], params [4], isCustom);
		} catch (JSONException e){
			Log.e(TAG, "JSON configuration in file is wrong: " + e.getMessage());
		} catch (IOException e){
			Log.e(TAG, "Error opening configuration file: " + e.getMessage());
		}
	}

	private void initMovintracks(String rootServer, String apiKey, String apiSecret, String googleProjectNumber, String googleAPIKey, boolean isCustom){
		Utils.saveMovintracksData(sInstance, rootServer, apiKey, apiSecret, googleProjectNumber, googleAPIKey);
		try {
			if(isCustom)
				movintracks = new CustomMovintracks(sInstance, rootServer, apiKey, apiSecret, googleProjectNumber, googleAPIKey, false);
			else
				movintracks = new Movintracks(sInstance, rootServer, apiKey, apiSecret, googleProjectNumber, googleAPIKey, false);

			sInstance.getGoogleCloudMessageId();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getGoogleCloudMessageId(){
		// Check device for Play Services APK. If check succeeds, proceed with GCM registration.
		if (checkPlayServices()) {
			final String regid = getRegistrationId();
			if (regid==null || regid.isEmpty()) {
				new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... strings) {
						GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(MovintracksApp.this);
						String regid="";
						try {
							regid=gcm.register(strings[0]);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return regid;
					}

					@Override
					protected void onPostExecute(String regid) {
						super.onPostExecute(regid);
						movintracks.setGoogleCloudRegistrationId(regid);
						storeRegistrationId(regid);
					}
				}.execute(Constants.GCM.SENDER_ID);
			} else {
				movintracks.setGoogleCloudRegistrationId(regid);
			}//*/
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
			movintracks.setGoogleCloudRegistrationId("");
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			/*if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity,	PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				//finish();
			}//*/
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId() {
		final SharedPreferences prefs = getGcmPreferences();
		String registrationId = prefs.getString(LocalConstants.PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(LocalConstants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param regId registration ID
	 */
	private void storeRegistrationId(String regId) {
		final SharedPreferences prefs = getGcmPreferences();
		int appVersion = getAppVersion();
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(LocalConstants.PROPERTY_REG_ID, regId);
		editor.putInt(LocalConstants.PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences() {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(this.getClass().getSimpleName(), this.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private int getAppVersion() {
		try {
			PackageInfo packageInfo = this.getPackageManager()
				.getPackageInfo(this.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}


	
 // ************************* Métodos genéricos *************************
    public static MovintracksApp getInstance() {
        return sInstance;
    }
    public static Movintracks getMovintracks () {
    	return movintracks;
    }
    public static Activity getMainActivity() {
        return mActivity;
    }
    public static void setMainActivity(Activity activity) {
    	mActivity = activity;
    }
    
    public static void showToast (Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}

	/************************************************************************************************************************
     * Methods required by IMovintracks interface
     ************************************************************************************************************************/
    @Override
    public void launchWebView (String url) {
        Log.d(TAG, "launchWebView() => " + url);
        Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra(WebViewActivity.KEY_URL_WEBVIEW, url);
		intent.putExtra(Constants.ExtraIntent.IS_ACTION_ACTIVITY, true);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
    }

    @Override
    public void customCallBackAction(final ActionInterface _action, String name, HashMap<String, String> params) {
        Log.d(TAG, "customCallBackAction() => " + name);
        CallbackContext customCallback = customCallbacksMap.get(name);
        if(customCallback != null){
	        try{
	            JSONObject json = new JSONObject(params);
	            json.put(LocalConstants.KEY_NAME, name);
	            PluginResult result = new PluginResult(PluginResult.Status.OK, json.toString());
	            result.setKeepCallback(true);
	            
	            if (customCallback != null) {
	            	Log.d(TAG, "Sending result of customCallbackAction=> " + json.toString());
	                customCallback.sendPluginResult(result);
	                // callbackContext = null;
	            }else
	            	Log.d(TAG, "customCallbackAction is null!!!");

	            _action.finish();
	        }catch(JSONException e){
	        	Log.d(TAG, "customCallBackAction() => Error!!!" + e.getMessage());
	            Utils.error(customCallback, e.getMessage());
	        }
	    }else{
	    	Log.d(TAG, "customCallback not registered for given name");
	    }
    }
}
