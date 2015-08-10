//
//  Movintracks.h
//  Movintracks
//
//  Copyright (c) 2014 Movintracks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <PassKit/PassKit.h>

#define kModalViewEndNotification		@"kModalViewEndNotification"
#define kShowModalViewNotification		@"kShowModalViewControllerNotification"
#define kNumberBeaconsNotification		@"kNumberBeaconsNotification"
#define kListBeaconsNotification		@"ListBeaconsNotification"

#import "MTDefines.h"


@interface MTMovintracks : NSObject

/**
 * Get MTMovintracks Singletoon instance created with init.
 */
+ (MTMovintracks*) getInstance;

/**
 * The customer ID used on Movintracks API
 * @deprecated Use getDeviceId instead.
 */
@property(readonly) NSString* customerID __deprecated;

/**
 * Create a Movintracks instance with basic configuration of the services
 *
 * @param rootServer Url to Movintracks API server, usually http://app.movintracks.io
 * @param key	User identifier key, can be found in Profile > API auth on Movintracks Dashboard
 * @param secret User secret to sign all information, it's found on the same section that apiKey
 * @param launchingOptions	The launching options of the application, used to recover information from the notification (if the notification is the reason to launch)
 * @return MTMovintracks instance configured
 */
- (id) initWithRootServer: (NSString*) rootServer ApiKey: (NSString*) key andApiSecret: (NSString*) secret withLaunchingOptions: (NSDictionary*) launchingOptions;

/**
 * @name Remote push notifications
 * @{
 */

/**
 * Set the push token to receive push notifications
 *
 * @param token A NSData with the token, or nil if there is some error.
 */
- (void) setPushToken: (NSData*) token;

/**
 * Set the push token to receive push notifications
 *
 * @param token A string with the token, or nil if there is some error.
 */
- (void) setPushTokenFromString: (NSString*) token;

/**
 * Used to manage the silent notifications to download data.
 *
 * @param userInfo A dictionary that contains information related to the remote notification, potentially including a badge number for the app icon, an alert sound, an alert message to display to the user, a notification identifier, and custom data
 */
- (BOOL) applicationDidReceiveRemoteNotification: (NSDictionary*)userInfo;
//! @}

/** \name Custom callback methods
 * @{
 */
/**
 *  Register the block code as a callback for the custom actions
 *
 * @param callback A code block to run when this action name happens
 * @param name The name of this callback, for an easy detection
 */
- (void) registerCallback: (MTCustomActionCallback) callback withName: (NSString*) name;

/**
 *  Delete the callback in the list of custom callbacks callable
 *
 * @param name The name of the action will be deleted
 */
- (void) deleteCallbackWithName: (NSString*) name;
//! @}

/**
 * @name Data managers
 * @{
 */
/**
 * Check conditionals to download data and download data if the conditions are true
 */
- (BOOL) checkAndDownload;

/**
 * Force download
 */
- (void) forceDownloadData;
/** @} */

/**
 * @name Beacon ranging
 * @{
 */
/**
 * Start ranging beacons
 */
- (void) startRanging;

/**
 * Stop ranging beacons
 */
- (void) stopRanging;
/** @} */

/**
 * @name Beacon Monitoring
 * @{
 */
/**
 * Start monitoring beacons
 */
- (void) startMonitoring;

/**
 * Stop monitoring beacons
 */
- (void) stopMonitoring;
/** @} */

/**
 * @name Application delegate custom implementations
 * @{
 */

/**
 *  Must be called from AppDelegate in the method application:didReceiveLocalNotification:
 *
 *  @param notification THe local notification
 */
- (void)applicationDidReceiveLocalNotification:(UILocalNotification*)notification;

/**
 *  This method is used to continue a campaign when the app is moved to background. 
 */
- (void) applicationDidBecomeActive;
/** @} */

/**
 * Returns the device id.
 */
-(NSNumber*)getDeviceId;

@end

