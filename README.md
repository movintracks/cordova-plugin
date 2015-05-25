Movintracks Cordova Plugin
==========================

A cordova plugin for the Movintracks SDK that brings the power of interactive campaigns with beacons to HTML5 mobile apps.

- Cordova 3.0+
- Android and iOS
 
 
Get your license here: http://movintracks.io/pricing/ 

A 2 minute tutorial on Movintracks: https://www.youtube.com/watch?v=9voTkCJH9jk 

Watch what others are doing with beacons at iBeaconsblog weekly news: https://www.youtube.com/playlist?list=PLF0aZiQ6l2beFt9tghPM0ggND_vuIcEK6
 
Installation
------------
Add the plugin to a project.
With Phonegap:

    $ phonegap local plugin add https://github.com/movintracks/cordova-plugin --variable FACEBOOK_ID=<your fb app id>
    
With Cordova:

    $ cordova plugin add https://github.com/movintracks/cordova-plugin --variable FACEBOOK_ID=<your fb app id>

`FACEBOOK_ID` must be defined for the plugin to be installed.

Usage
----------
There are several options available at the JavaScript layer after importing the Movintracks module.

#### Module initialization 

    var movintracks = cordova.require("cordova/plugin/movintracks");

#### Movintracks instantiation  

    movintracks.init(
		function() {
		    console.log('Movintracks SDK Demo Init Success');
		},
		function() {
		    console.log('Movintracks SDK Demo Init Error');
		}
	);

After installation, a file `movintracks.json` is copied automatically from the plugin root folder to each platform specific folder (i.e. a copy for Android, a copy for iOS). This file contains certain settings required to initialize the SDK. **Only `movintracks.json` in platform specific folders have to be modified**.

- **rootServer**: Url to Movintracks API server, usually https://api.movintracks.io
- **apiKey**: Movintracks API Key. If you are using the Movintracks dashboard, find it in [Profile > SDK & API](https://dashboard.movintracks.io/#/dashboard/apps/) 
- **apiSecret**: Movintracks Secret Key
- **googleProjectNumber**: Google project number for push notifications
- **googleAPIKey**: Google API Key for push notifications

Example 
 
    {
    "rootServer": "https://api.movintracks.io/", 
    "apiKey": "", 
    "apiSecret": "", 
    "googleProjectNumber": "", 
    "googleAPIKey": ""
    }
        
**Don't forget add https://api.movintracks.io to your whitelist**

For Android, you will find the file at: `yourProject/platforms/android/assets/`

For iOS:

  1. Select the project target
  2. Select the Build Phases
  3. Expand the Compile Source
  4. Remove the movintracks.json file
  5. Expand the Copy Bundle Resources
  6. Add movintracks.json file

####  Force download data  
    movintracks.downloadData();

####  Get number of beacons available
    movintracks.getBeaconsAvailable();
        
####  Get the device ID
    movintracks.getDeviceId();

####  Set callback for 'custom callback' SDK feature
	var customCallBackName = {"name":"<callback id>"};
	movintracks.customCallBackAction(
	    customCallBackName,
	    
	    /**
	     * success callback
	     * @param response: a dictionary like {name: "", param1: 1, param2: 2}
	     */
	    function (response) {},
	    	    
	    /*
	     * error callback
	     */
	    function (error) {}
	)

Find a demo project in `/demo`

#### Compile and run 

Build and run the application for all platforms.
 
    $ cordova run
    
Or just build, optionally targetting only one platform:

    $ cordova build [ios]
    
Compiling for Android requires Gradle:

    $ ANDROID_BUILD=gradle cordova run android

### Required changes for Android 
- Add to AndroidManifest.xml, in the `<application>` tag:

        android:name="com.movintracks.cordovamovintracks.MovintracksApp"

- Add these imports to `MainActivity.java`:

        import com.facebook.UiLifecycleHelper;
        import android.content.Intent;

- Add to `MainActivity.java`, before the `onCreate()` method:

        private UiLifecycleHelper uiHelper;
        
- Add to `MainActivity.java`, in the `onCreate()` method:

        uiHelper = new UiLifecycleHelper(this, null);

- Add to `MainActivity.java`, after the `onCreate(...)` method:

        @Override
        public void onResume() {
            super.onResume();
            uiHelper.onResume();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onPause() {
            super.onPause();
            uiHelper.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            uiHelper.onDestroy();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            uiHelper.onSaveInstanceState(outState);
        }

- If you would like to use Twitter, add your own Twitter keys to `assets/oauth_consumer.properties`.


### Required changes for iOS
- Open Xcode project and go to AppDelegate.m
- Add `#import "AppDelegate+Movintracks.h"`
- Add to `didReceiveLocalNotification` method:

		[self.movintracks applicationDidReceiveLocalNotification:notification];

#### Optional changes for iOS 
If code in the client app calls directly components in libraries used by the Movintracks SDK (e.g. AFNetworking, NSData+MD5Digest, MWFeedParser), open the xcode project and import manually their header files included in `sdk-ios/include/ExternalLibs`. 
