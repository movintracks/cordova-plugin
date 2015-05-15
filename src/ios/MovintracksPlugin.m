//
//  MovintracksPlugin.m
//  MovintracksPlugin
//
//  Created by Alberto on 10/4/15.
//
//

#import "MovintracksPlugin.h"

//For notifications and show modal view
#import "AppDelegate.h"
#import "AppDelegate+Movintracks.h"

//For use CLBeacon
#import <CoreLocation/CoreLocation.h>

//JSON Keys
#define kKeyNumberOfBeacons         @"availableBeacons"
#define kKeyCallbackCustomAppName   @"name"
#define kKeyDeviceId                @"deviceId"

@interface MovintracksPlugin ()

@property (nonatomic, readwrite) CDVInvokedUrlCommand *getBeaconsCommand;
@property (nonatomic, readwrite) CDVInvokedUrlCommand *getListBeaconsCommand;
@property (nonatomic, readwrite) CDVInvokedUrlCommand *getDeviceId;

@end

@implementation MovintracksPlugin


#pragma mark - Public Methods to connect with Cordova.

- (void) initMovintracks:(CDVInvokedUrlCommand *)command {
    NSString *filePath = [[NSBundle mainBundle] pathForResource:kSDKInitFileName ofType:kSDKInitFileNameExtension];
    NSString *jsonString = [[NSString alloc] initWithContentsOfFile:filePath encoding:NSUTF8StringEncoding error:NULL];
    NSError *jsonError;
    NSMutableDictionary *dataJSON = [NSJSONSerialization JSONObjectWithData:
                                     [jsonString dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableContainers error:&jsonError];
    NSString *kPlattformBaseURL = (NSString *)dataJSON[kSDKRootServer];
    NSString *kMovintracksUserKey = (NSString *)dataJSON[kSDKAPIKey];;
    NSString *kMovintracksUserSecret  = (NSString *)dataJSON[kSDKAPISecret];
    
    if(dataJSON == nil){
        [self sendErrorResponseforCommand:command withMessage:@"Configuration not specified or not specified correctly."];
    }
    if(kPlattformBaseURL == nil || [kPlattformBaseURL isEqualToString:@""]){
        [self sendErrorResponseforCommand:command withMessage:@"Root server not specified or not specified correctly."];
    }
    if(kMovintracksUserKey == nil || [kMovintracksUserKey isEqualToString:@""]){
        [self sendErrorResponseforCommand:command withMessage:@"API key not specified or not specified correctly."];
    }
    if(kMovintracksUserSecret == nil || [kMovintracksUserSecret isEqualToString:@""]){
        [self sendErrorResponseforCommand:command withMessage:@"API secret not specified or not specified correctly."];
    }
    
    UIApplication *selfApp = [UIApplication sharedApplication];
    [self configureMovintracks:nil forApplication:selfApp platformBase:kPlattformBaseURL userKey:kMovintracksUserKey userSecret:kMovintracksUserSecret];
    
    [self sendSuccessResponseforCommand:command];
}

- (void) downloadData:(CDVInvokedUrlCommand *)command {
    @try {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.movintracks forceDownloadData];
            
            [self sendSuccessResponseforCommand:command];
        });
    }
    @catch (NSException *exception) {
        [self sendErrorResponseforCommand:command withMessage:@"Error while downloading data"];
    }
}

- (void) getBeaconsAvailable:(CDVInvokedUrlCommand *)command {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(numberOfBeaconsListened:) name:kNumberBeaconsNotification object:nil];
    
    [_movintracks startRanging];
    self.getBeaconsCommand = command;
}

- (void) getListBeacons:(CDVInvokedUrlCommand *)command {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(beaconsList:) name:kListBeaconsNotification object:nil];
    
    [_movintracks startRanging];
    self.getListBeaconsCommand = command;
}

-  (void) getDeviceId:(CDVInvokedUrlCommand *)command {
    NSString *dataSTR = [[_movintracks getDeviceId] stringValue];
    NSDictionary *jsonObj = [[NSDictionary alloc] initWithObjectsAndKeys:
                             dataSTR, kKeyDeviceId,
                             nil
                            ];
    self.getDeviceId = command;
    [self sendJSONResponseforCommand:_getDeviceId andKeepCommand:nil withStatus:CDVCommandStatus_OK andDictionary:jsonObj];
}

- (void) customCallBackAction:(CDVInvokedUrlCommand *)command {
    NSDictionary *dataJSON = (NSDictionary *)[command.arguments objectAtIndex:0];
    NSString *nameOfCallback = (NSString *)dataJSON[kKeyCallbackCustomAppName];
    
    if(dataJSON == nil){
        [self sendErrorResponseforCommand:command withMessage:@"Custom callback name not specified."];
    }
    if(nameOfCallback == nil || [nameOfCallback isEqualToString:@""]){
        [self sendErrorResponseforCommand:command withMessage:@"Custom callback name not specified."];
    }
    
    [[MTMovintracks getInstance] registerCallback:^(NSDictionary *arguments, MTOnCompletionHandler handler) {
        NSMutableDictionary *jsonData = [NSMutableDictionary dictionaryWithDictionary:arguments];
        [jsonData setValue:nameOfCallback forKey:kKeyCallbackCustomAppName];
        NSLog(@"MovintracksPlugin -  Custom App Callback with params %@", jsonData);
        
        [self sendJSONResponseforCommand:command andKeepCommand:[NSNumber numberWithBool:YES] withStatus:CDVCommandStatus_OK andDictionary:jsonData];
        
        handler();
    } withName:nameOfCallback];
}

#pragma mark - Private Methods.

/**
 *  Method to configure the movintracks SDK
 */
- (void)configureMovintracks:(NSDictionary *)launchOptions
              forApplication:(UIApplication *)application
                platformBase:(NSString *)platformURL
                     userKey:(NSString *)userKey
                  userSecret:(NSString *)userSecret {
    self.movintracks = [[MTMovintracks alloc] initWithRootServer:platformURL ApiKey:userKey andApiSecret:userSecret withLaunchingOptions:launchOptions];
    
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    [appDelegate setMovintracks:self.movintracks];
    
    // Check if we are using ios8
    if ([application respondsToSelector:@selector(registerForRemoteNotifications)]){
        // Register remote notifications in iOS8
        [application registerForRemoteNotifications];
        
        // Ask user enable notifications in notification manager
        [application registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeSound|UIUserNotificationTypeAlert categories: nil]];
    } else {
        // Register to obtain push silent notifications
        [application registerForRemoteNotificationTypes:UIRemoteNotificationTypeNewsstandContentAvailability];
    }
    
    //Register for notification events
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(openModalView:)
                                                 name:kShowModalViewNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(movintracksApplicationDidRegisterForRemoteNotificationsWithDeviceToken:)
                                                 name:CDVRemoteNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(movintracksApplicationDidFailToRegisterForRemoteNotificationsWithError:)
                                                 name:CDVRemoteNotificationError object:nil];
}

/** Actions for modal view notification.
 */
- (void)openModalView:(NSNotification*)notification {
    NSLog(@"MovintracksPlugin - Open Campaign");
    UIViewController *view = notification.object;
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    [appDelegate.viewController presentViewController: view animated:YES completion:nil];
}

/** Show log with visible beacons info.
 */
- (void)numberOfBeaconsListened:(NSNotification*)notification {
    if (notification.object && [notification.object isKindOfClass:[NSNumber class]]){
        NSLog(@"MovintracksPlugin - Visible Beacons: %@", notification.object);
        NSString *dataSTR = notification.object;
        NSDictionary *jsonObj = [[NSDictionary alloc] initWithObjectsAndKeys:
                                    dataSTR, kKeyNumberOfBeacons,
                                    nil
                                 ];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:kNumberBeaconsNotification object:nil];
        
        [self sendJSONResponseforCommand:_getBeaconsCommand andKeepCommand:nil withStatus:CDVCommandStatus_OK andDictionary:jsonObj];
    } else {
        [self sendErrorResponseforCommand:_getBeaconsCommand withMessage:@"Error while obtaining available beacons"];
    }
}

/** Show log with beacons info.
 */
- (void)beaconsList:(NSNotification*)notification {
    if (notification) {
        NSArray *beacons = notification.object;
        for (CLBeacon *beacon in beacons) { 
            NSLog(@"MovintracksPlugin - Beacon detected with UUID:%@, Major:%@, Minor:%@, Proximity:%ld and Accuracy:%f", beacon.proximityUUID, beacon.major, beacon.minor, (long)beacon.proximity, beacon.accuracy);
        }
        
        [[NSNotificationCenter defaultCenter] removeObserver:self name:kListBeaconsNotification object:nil];
        
        [self sendSuccessResponseforCommand:_getListBeaconsCommand];
    } else {
        [self sendErrorResponseforCommand:_getListBeaconsCommand withMessage:@"Error while obtaining list of beacons"];
    }
}

#pragma mark - Notifications

- (void)movintracksApplicationDidRegisterForRemoteNotificationsWithDeviceToken:(NSNotification*)notification{
    if (notification) {
        NSString *token = (NSString *)notification.object;
        NSLog(@"MovintracksPlugin - didRegisterForRemoteNotificationsWithDeviceToken: %@", token);
        [self.movintracks setPushTokenFromString:token];
    }
}

- (void)movintracksApplicationDidFailToRegisterForRemoteNotificationsWithError:(NSNotification*)notification{
    NSLog(@"MovintracksPlugin - didFailToRegisterForRemoteNotificationsWithError");
    [self.movintracks setPushToken:nil];
}

#pragma mark - Helpers

/** Method to generate and send ok response to javascript
 */
- (void)sendSuccessResponseforCommand:(CDVInvokedUrlCommand *) command {
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

/** Method to generate and send string error response to javascript
 */
- (void)sendErrorResponseforCommand:(CDVInvokedUrlCommand *) command withMessage:(NSString *) message {
    CDVPluginResult *pluginResult =  [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

/** Method to generate json (ok, error...) response to javascript
 */
- (void)sendJSONResponseforCommand:(CDVInvokedUrlCommand *) command andKeepCommand:(NSNumber*)keepComand withStatus:(CDVCommandStatus) status andDictionary:(NSDictionary *) jsonObj {
    CDVPluginResult *pluginResult = [CDVPluginResult
                                     resultWithStatus    : status
                                     messageAsDictionary : jsonObj
                                     ];
    if(keepComand){
        //Save context of command for use several times (recursive campaigns)
        [pluginResult setKeepCallback:keepComand];
    }
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
