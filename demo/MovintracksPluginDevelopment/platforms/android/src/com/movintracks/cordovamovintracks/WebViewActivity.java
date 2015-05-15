/**
 * 2014 Created by: Laura Guti�rrez
 * <p>
 * As long as you retain this notice you can do whatever you want with this stuff. If we meet some
 * day, and you think this stuff is worth it, you can buy me a beer in return
 */
package com.movintracks.cordovamovintracks;

import com.movintracks.cordovamovintracks.MovintracksApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.res.Resources;


public class WebViewActivity extends Activity {
	public static String KEY_URL_WEBVIEW = "urlWebView";
	
	private ProgressBar spinner;
	private WebView webView;
	
	private String url;

	private String ERROR_WEB_VIEW = "An error occurred. Please, check your internet connection and try again.";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String package_name = getApplication().getPackageName();
 		Resources resources = getApplication().getResources();
 		setContentView(resources.getIdentifier("activity_webview", "layout", package_name));
		
		// Obtener la url que debe mostrarse en el navegador
		Intent intent = getIntent();
		if (intent == null) return;
		
		Bundle extras = intent.getExtras();
		if (extras != null) this.url = extras.getString(KEY_URL_WEBVIEW);
			
		int spinnerId = resources.getIdentifier("webview_spinner", "id", package_name);
		spinner = (ProgressBar) findViewById(spinnerId);
		spinner.setVisibility(View.GONE);
		
		int webViewId = resources.getIdentifier("webView", "id", package_name);
		webView = (WebView) findViewById(webViewId);
		webView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN) {
		            switch(keyCode) {
			            case KeyEvent.KEYCODE_BACK:
			                if(webView.canGoBack())  {
			                	webView.goBack();
			                	return true;
			                }
		            }
				}
				return false;
			}
		});
		
		try {
			webView.clearCache(false);
			webView.destroyDrawingCache();
			webView.getSettings().setJavaScriptEnabled(true);
	        webView.getSettings().setPluginState(PluginState.ON);
	        webView.getSettings().setBuiltInZoomControls(true);
	        
	        // Advertir que no debe abrirse otro navegador
	        webView.setWebChromeClient(new WebChromeClient());
	        webView.setWebViewClient(new WebViewClientView());
	        webView.loadUrl(this.url);
	        
	    } catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, ERROR_WEB_VIEW, Toast.LENGTH_SHORT).show();
		}
	}

	private class WebViewClientView extends WebViewClient {
		public WebViewClientView() {
			spinner.setVisibility(View.VISIBLE);
		}
		
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
        	Log.d("WebViewActivity", "URL: " + url);
        	
        	if (url.contains("oauth://t4jsample")) {
        		MovintracksApp.movintracks.getOAuthListener().finishOAuth(url);
        		finish();
        	} else 
        		view.loadUrl(url);
        	
        	return true;
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            spinner.setVisibility(View.GONE);
        }
    }
}