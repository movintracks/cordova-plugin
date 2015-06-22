Movintracks Cordova Sample
==========================

This sample shows how to use Movintracks SDK in Cordova.
 
Installation
------------
First, you need to add the platforms you wish to use:

    $ cordova platform add ios
    $ cordova platform add android
    
Then, add the Movintracks Cordova Plugin. 

With Phonegap:

    $ phonegap local plugin add https://github.com/movintracks/cordova-plugin --variable FACEBOOK_ID=<your fb app id>
    
With Cordova:

    $ cordova plugin add https://github.com/movintracks/cordova-plugin --variable FACEBOOK_ID=<your fb app id>

`FACEBOOK_ID` must be defined for the plugin to be installed.

Usage
----------

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

### Required changes for Android 
- Add to AndroidManifest.xml, in the `<application>` tag:

        android:name="com.movintracks.cordovamovintracks.MovintracksApp"

- If you would like to use Twitter, add your own Twitter keys to `assets/oauth_consumer.properties`.

### Required changes for iOS
- Open Xcode project and go to AppDelegate.m
- Add `#import "AppDelegate+Movintracks.h"`
- Add to `didReceiveLocalNotification` method:

		[self.movintracks applicationDidReceiveLocalNotification:notification];

#### Compile and run 
    
To build and install the app on Android:

    $ ANDROID_BUILD=gradle cordova run android
    
To build the app on iOS:

    $ cordova build ios
    
Then open and run the generated project using Xcode. 