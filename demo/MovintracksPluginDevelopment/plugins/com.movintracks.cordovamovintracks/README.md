Movintracks Cordova Plugin
==========================

Integrate Movintracks sdk in your Cordova app easily! Uses Movintracks SDK v1.4

Supported platforms
-------------------
- This plugin requires Cordova 3.0 or higher
- This plugin supports Android and iOS at the moment
 
Installation
------------
You can add the plugin to an existing project by executing either one of the following commands:

        - phonegap local plugin add https://bitbucket.org/movintracksmobile/phonegapp-plugin --variable FACEBOOK_ID=<your fb app id> (when using Phonegap)
        - cordova plugin add https://bitbucket.org/movintracksmobile/phonegapp-plugin --variable FACEBOOK_ID=<your fb app id> (when using Cordova)

The FACEBOOK_ID variable has to be defined always. If not set, plugin won't install

How to use
----------

There are several options available at the javascript layer, once the movintracks module has been imported:


- #### Module initialization 

        var movintracks = cordova.require("cordova/plugin/movintracks");

-  #### Movintracks instantation:  
        movintracks.init(
            function() {
                console.log('Movintracks SDK Demo Init Ok');
            },
            function() {
                console.log('Movintracks SDK Demo Init Error');
            });

In the plugin root folder there is a file called 'movintracks.json'. That file is added to android and iOS projects and must be configured. The file must contain a json with the Movintracks configuration. It will be used to instantiate the SDK. The plugin will throw an exception when loading in case the file is not properly configured. The parameters are:
    - rootServer: Url to Movintracks API server, usually https://api.movintracks.io
    - apiKey: User identifier key, can be found in Profile > API auth in Movintracks Dashboard
    - apiSecret: User secret to sign all information, it's found in the same section that apiKey
    - googleProjectNumber: Google project number for push notifications
    - googleAPIKey: Google API Key for push notifications


  > Example 
 
        {"rootServer": "https://api.movintracks.io/", "apiKey": "", "apiSecret": "", "googleProjectNumber": "", "googleAPIKey": ""}
> ** Don't forget add https://api.movintracks.io/ to your whitelist **


- ####  Force download data to refresh data of Movintracks campaigns:  
        movintracks.downloadData();

- ####  Get number of available beacons in the surroundings: 
        movintracks.getBeaconsAvailable();

- ####  Set callback for 'custom callback' SDK feature: 
        var customCallBackName = '{"name":"<defined callback id>"}';
        var customCallBackNameJson = JSON.parse(customCallBackName);
        movintracks.customCallBackAction(
	            customCallBackNameJson,
	            
	            // json contains the campaign name, under the key 'name'
	            // and the campaign parameters, under their respectives keys
	            function (json){},  //Success callback
	            
	            // error is a json that contains the reason for failure
	            function (error){}   //Failure callback
	        )

-  #### For a complete example check demo folder.

#### Compile and run 
 The following command will compile and run the application for each platform.
 
        cordova run
In order to just compile, the command is:

        cordova build
It is possible to discriminate by platform, adding it as the last term of the sentence:

        cordova build android           //This will compile the application only for Android


### Required modifications for Android 
- The following code has to be added to AndroidManifest.xml, within the ```<application>``` tag:

        android:name="com.movintracks.cordovamovintracks.MovintracksApp"

- Edit the file assets/oauth_consumer.properties and change the application keys for Twitter.

- For the plugin to compile properly for the Android platform, it is necessary to run cordova using gradle, like this:

        ANDROID_BUILD=gradle cordova run android

### Required modifications for iOS
- Open Xcode project and go to AppDelegate.m
- Add #import "AppDelegate+Movintracks.h"
- The following code has to be added to didReceiveLocalNotification method.

		[self.movintracks applicationDidReceiveLocalNotification:notification];

- For configure correctly movintracks.json file follow these steps:
    * Select the project target
    * Select the Build Phases
    * Expand the Compile Source
    * Remove the movintracks.json file
    * Expand the Copy Bundle Resources
    * Add movintracks.json file

### Optional modifications for iOS 
- If the project needs to do direct calls to the same libraries that SDK-Movintracks uses (AFNetworking, Reachability, NSData+MD5Digest, MWFeedParser), open xcode project and import manually their headers files included in sdk-ios/include/ExternalLibs. 
