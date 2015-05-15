//
//  MTDefines.h
//  MovintracksAcme
//
//  Created by jaume on 11/04/14.
//  Copyright (c) 2014 Movintracks. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <PassKit/PassKit.h>
#import <AddressBook/AddressBook.h>

#define kLocalizedTable @"MTMovintracks"
#define kSdkVersionText	@"1.5"

typedef void (^MTOnFacebookWebLogin)(BOOL); /**< Code block to manage When user was login to webview */
typedef void (^MTRequestCompletionHandler)(BOOL, NSError*); /**< Code block to manage when a request was completed */
typedef void (^MTOnCompletionHandler)(); /**< Code block to manage Completion Handlers for the actions */
typedef void (^MTCustomActionCallback)(NSDictionary* arguments, MTOnCompletionHandler handler); /**< Code block to manage custom actions for the application */

typedef enum : NSUInteger {
	MTDownloadDataZones,
	MTDownloadDataPoints,
	MTDownloadDataSocialProfiles,
	MTDownloadDataCampaigns,
	MTDownloadDataApp,
} MTDownloadDataStep;

@class MTBrainController;
@class MTCampaign;
@class MTPoint;
@class UIView;
@class MTAbstractSocialNetwork;

/**
 * The protocol that should be implemented by a trigger
 */
@protocol MTTriggerProtocol<NSObject, NSCoding>
/**
 * Check the trigger and return true if the trigger conditions was compied
 */
- (BOOL) checkTrigger;
@optional
/**
 * Only implement on TriggerAction, used to check if the action was request before to the user.
 */
- (BOOL) isRequest;
@end

/**
 * The protocol that an Action should implement
 */
@protocol  MTActionProtocol<NSObject, NSCoding>
/**
 * Run the action and notify to the app using the blockCode completed
 *
 * @param completed The block code to notify the \ref MTCampaignManager that has completed the action
 */
- (void) performActionOnComplete: (MTOnCompletionHandler) completed;
@optional
/**
 * @brief used to check if the action has all the data
 *
 * Used in Actions that require to download some data and cannot be executed when the data isn't downloaded. Used in \ref MTCampaignManager to don't launch any campaign if all data is not available
 */
- (BOOL) hasData;

/**
 * Dismiss the action if it is showing something
 */
- (void) dismiss;
@end

/**
 * The protocol of a Social profile
 */
@protocol MTSocialProfileProtocol <NSObject, NSCoding>

/**
 * Obtain information about the social network used in that profile
 */
@property(nonatomic, readonly)	MTAbstractSocialNetwork* socialNetwork;

/**
 * Say if the profile following action was executed before
 */
@property(nonatomic) BOOL isRequestBefore;

/**
 * Indicates if there is some actions that requires the cache data
 */
@property(nonatomic) BOOL isRequiredCache;

/**
 * Get the name of the protocol
 */
- (NSString*) getName;
/**
 * Get the url to a profile ID
 */
- (NSString*) getUrl;
/**
 * Check if the user follow this social profile
 */
- (BOOL) isFollowing;

/**
 * Follow this social profile, the brain is required in case of Facebook, that there is no option to follow anybody using API.
 */
- (void) followWithBrain: (MTBrainController*) brain andCompletionHandler: (MTOnCompletionHandler) onCompletion;

@optional
/**
 * Follow this social profile, use this method when there is not needed interaction with user
 */
- (void) followWithCompletionHandler: (MTOnCompletionHandler) onCompletion;
@end

/**
 * The delegate to use on actions
 */
@protocol MTBrainControllerDelegate <NSObject>

@property (nonatomic)	BOOL isDebug;

/**
 * Call when the library detect there is no bluetooth enabled
 *
 * @param message It's the message to show to the user
 */
- (void) bluetoothDisabledWithMessage: (NSString*) message;

/** \name Methods used in actions delegate
 * @{
 */
/**
 * Open a webview with an URL
 *
 * @param url Url to be opened on the application
 * @param onCompletion the completion handler to call when the open url was ended
 */
- (void) openURL: (NSURL*) url withCompletionHandler: (MTOnCompletionHandler) onCompletion;

/**
 * Open the object passbook
 *
 * @param passbook object with the information of the passbook
 * @param url with the html view of the passbook
 * @param onCompletion the completion handler to call when this view was clossed
 */
- (void) openPassbook: (PKPass*) passbook failbackUrl: (NSURL*) url withCompletionHandler: (MTOnCompletionHandler) onCompletion;

/**
 * Open a view with information of a vcard information
 *
 * @param person The VCard information using the apple Class ABRecordRef
 * @param onCompletion the completion handler to call when this view was clossed
 */
- (void) openPerson: (ABRecordRef) person withCompletionHandler: (MTOnCompletionHandler) onCompletion;

/**
 * Open a webview to made like in facebook at the page url.
 *
 * @param page The page url using string
 * @param appId An nsstring with the facebook Application ID
 * @param onCompletion the completion handler to call when this view was clossed
 */
- (void) facebookLike: (NSString*) page withAppId: (NSString*) appId andCompletionHandler: (MTOnCompletionHandler) onCompletion;

/**
 * Open a pickImageViewController to publish this image on social profiles
 *
 * @param onCompletion a block code to call when the user select an image
 * @param socialNetworkName The name of the social network to show it on the alert View
 */
- (void)pickImageForNetwork: (NSString*) socialNetworkName withCompletionBlock: (void(^)(UIImage*)) onCompletion;

//! @}

/** \name Download data delegates
 * @{
 */
/**
 * Method used to understand when the data was correctly download, or happens some error
 *
 * @param dataDownload The step in where happen this error, or if not error, it's the last step do download data
 * @param error The error that happens downloading data or null if the download was correctly. 
 */
- (void) downloadedData: (MTDownloadDataStep) dataDownload WithError: (NSError*) error;

/**
 * @}
 */
/** \name Methods used to show and manage notifications of a campaign
 * @{
 */

/**
 * Used when the campaign is authorized to schedule the time of the aftertime trigger
 *
 * @param campaign Campaign to check and notify using the correct policy
 * @param point The point that launch this campaign, used in save_data delivery
 * @param launchTime If the notification will be launched with a delay it should be the time to launch the notification, otherwise nil
 */
- (void) launchNotification: (MTCampaign*) campaign inPoint: (MTPoint*) point withAfterTime: (NSDate*) launchTime;

/**
 * Used to notify to user about a new campaign or a campaign is current running
 *
 * @param campaign Campaign to notify
 * @param actionNum number of action to launch when user click this campaign.
 * @param point The point that launch this campaign, used in save_data delivery
 */
- (void) launchNotification: (MTCampaign*) campaign forAction: (NSUInteger) actionNum inPoint: (MTPoint*) point;

//! @}

/**
 * Check the conditions to download data and download if needed
 */

- (void) checkAndDownload;

@optional
/** \name Custom app callback methods
 * @{
 */
/**
 * Launch the custom callback with
 *
 * @param name The name of the callback
 * @param arguments A list of dictionaries key-value with all arguments put in the dashboard
 * @param handler The call to say the action was finished
 */
- (void) callCustomCallbackWithName: (NSString*) name arguments: (NSDictionary*) arguments andCompleteHandler: (MTOnCompletionHandler) handler;
//! @}
/** \name CLLocation delegate methods
 * @{
 */

/**
 * The beacons list ranged in the device
 *
 * @param listBeacons an array of beacons listened
 */
- (void) didRangeBeacons: (NSArray*) listBeacons;

//! @}
@end

/**
 * The protocol used on model to obtain an Identifier from the JSON API object
 */
@protocol MTModelUpdateProtocol <NSObject>

/**
 * Obtain the identifier from a API JSON dictionary of this object. 
 */
+ (NSUInteger) getIDFromDict: (NSDictionary*) dict;

@end
