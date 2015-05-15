//
//  AppDelegate+Movintracks.h
//  Movintracks
//
//  Created by Alberto Lobo Muñoz on 14/4/15.
//
//  Implements needed appdelegate methods for Movintracks SDK

#import "AppDelegate.h"
#import "MTMovintracks.h"

@interface AppDelegate (Movintracks)

@property (nonatomic, strong) MTMovintracks* movintracks;

- (void)applicationDidBecomeActive:(UIApplication *)application;
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler;

@end