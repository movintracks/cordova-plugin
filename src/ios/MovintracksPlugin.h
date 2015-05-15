//
//  MovintracksPlugin.h
//  MovintracksPlugin
//
//  Created by Alberto on 10/4/15.
//
//

#import <Cordova/CDVPlugin.h>
#import "MTMovinTracks.h"

#define kSDKInitFileName                @"movintracks"
#define kSDKInitFileNameExtension       @"json"
#define kSDKRootServer                  @"rootServer"
#define kSDKAPIKey                      @"apiKey"
#define kSDKAPISecret                   @"apiSecret"

@interface MovintracksPlugin : CDVPlugin{
    
}

@property (strong, nonatomic) MTMovintracks* movintracks;


#pragma mark - Public Methods to connect with Cordova.

/** Method to initialize the sdk from javascript (use movintracks.json file)
 @param command
 @see javascript method: movintracks.init = function(successCallback, failureCallback) {
 cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "initMovintracks"); 
 };
 */
- (void) initMovintracks:(CDVInvokedUrlCommand *)command;

/** Method to download the data from the Movintracks plattform.
 @param command
 @see javascript method: movintracks.downloadData = function(successCallback, failureCallback) {
 cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "downloadData", []);
 };
 */
- (void) downloadData:(CDVInvokedUrlCommand *)command;

/** Method to get number of available beacons
 @param command
 @see javascript method: movintracks.getBeaconsAvailable = function(successCallback, failureCallback) {
 cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "getBeaconsAvailable", []);
 };
 */
-  (void) getBeaconsAvailable:(CDVInvokedUrlCommand *)command;

/** Method to get all data of available beacons
 @param command
 @see javascript method: movintracks.getListBeacons = function(successCallback, failureCallback) {
 cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "getListBeacons", []);
 };
 */
-  (void) getListBeacons:(CDVInvokedUrlCommand *)command;

/** Method to register a custom callback for a custom callback campaign
 @param command
 @see javascript method: movintracks.customCallBackAction = function(successCallback, failureCallback) {
 cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "customCallBackAction", []);
 };
 */
-  (void) customCallBackAction:(CDVInvokedUrlCommand *)command;

/** Method to get the device id
 @param command
 @see javascript method: movintracks.getDeviceId = function(successCallback, failureCallback) {
 cordova.exec(successCallback, failureCallback, "MovintracksPlugin", "getDeviceId", []);};
 */
-  (void) getDeviceId:(CDVInvokedUrlCommand *)command;

@end
