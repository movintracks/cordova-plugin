package com.movintracks.cordovamovintracks;

import android.content.SharedPreferences;
import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import java.io.IOException;
import org.json.JSONObject;
import org.apache.cordova.CallbackContext;
    
public class Utils{
    
    public static JSONObject getJSONError(String errorMessage) throws JSONException{
        JSONObject json = new JSONObject();
        json.put(LocalConstants.KEY_ERROR, errorMessage);
        return json;
    }

    public static void error(CallbackContext callbackContext, String errorMessage){
        callbackContext.error(String.format("{\"%s\":\"%s\"}", LocalConstants.KEY_ERROR, errorMessage));
    }

    public static String[] getMovintracksData(Context context) throws IOException, JSONException{
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = context.getResources().getAssets().open(LocalConstants.MOVINTRACKS_JSON, Context.MODE_WORLD_READABLE);
        InputStreamReader isr = new InputStreamReader(fIn);
        BufferedReader input = new BufferedReader(isr);
        String line = "";
        while ((line = input.readLine()) != null) {
            returnString.append(line);
        }
        isr.close();
        fIn.close();
        input.close();

        JSONObject json = new JSONObject(returnString.toString());
        String[] params = new String[5];
        SharedPreferences prefs = context.getSharedPreferences(LocalConstants.PREFERENCES_MOVINTRACKS, Context.MODE_PRIVATE);
        params[0] = json.getString(LocalConstants.KEY_ROOT_SERVER);
        params[1] = json.getString(LocalConstants.KEY_API_KEY);
        params[2] = json.getString(LocalConstants.KEY_API_SECRET);
        params[3] = json.getString(LocalConstants.KEY_GOOGLE_PROJECT_NUMBER);
        params[4] = json.getString(LocalConstants.KEY_GOOGLE_API_KEY);
        return (params[0] != null && params[1] != null && params[2] != null) ? params : null;
    }

    public static void saveMovintracksData(Context context, String rootServer, String apiKey, String apiSecret, String googleProjectNumber, String googleAPIKey){
        SharedPreferences.Editor editor = context.getSharedPreferences(LocalConstants.PREFERENCES_MOVINTRACKS, Context.MODE_PRIVATE).edit();
        editor.putString(LocalConstants.KEY_ROOT_SERVER, rootServer);
        editor.putString(LocalConstants.KEY_API_KEY, apiKey);
        editor.putString(LocalConstants.KEY_API_SECRET, apiSecret);
        editor.putString(LocalConstants.KEY_GOOGLE_PROJECT_NUMBER, googleProjectNumber);
        editor.putString(LocalConstants.KEY_GOOGLE_API_KEY, googleAPIKey);
        editor.commit();
    }

    public static boolean getIsMovintracksCustom(Context context){
        SharedPreferences prefs = context.getSharedPreferences(LocalConstants.PREFERENCES_MOVINTRACKS, Context.MODE_PRIVATE);
        return prefs.getBoolean(LocalConstants.KEY_IS_CUSTOM, false);
    }

    public static void setIsMovintracksCustom(Context context, boolean isCustom){
        SharedPreferences.Editor editor = context.getSharedPreferences(LocalConstants.PREFERENCES_MOVINTRACKS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LocalConstants.KEY_IS_CUSTOM, isCustom);
        editor.commit();
    }
}