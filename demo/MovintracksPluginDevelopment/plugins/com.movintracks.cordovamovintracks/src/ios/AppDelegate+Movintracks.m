//
//  AppDelegate+Movintracks.m
//  Movintracks
//
//  Created by Alberto Lobo Muñoz on 14/4/15.
//
//

#import "AppDelegate+Movintracks.h"
#import <objc/runtime.h>

static char MovintracksSDKKey;

@implementation AppDelegate (Movintracks)

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    NSLog(@"MovintracksPlugin - applicationDidBecomeActive");
    [self.movintracks applicationDidBecomeActive];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler{
    NSLog(@"MovintracksPlugin - didReceiveRemoteNotification fetchCompletionHandler");
    BOOL r=[self.movintracks applicationDidReceiveRemoteNotification: userInfo];
    completionHandler(r);
}

// The accessors use an Associative Reference since you can't define a iVar in a category
// http://developer.apple.com/library/ios/#documentation/cocoa/conceptual/objectivec/Chapters/ocAssociativeReferences.html
- (MTMovintracks *)movintracks
{
    return objc_getAssociatedObject(self, &MovintracksSDKKey);
}

- (void)setMovintracks:(MTMovintracks *)movintracks
{
    objc_setAssociatedObject(self, &MovintracksSDKKey, movintracks, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
}

- (void)dealloc
{
    self.movintracks = nil; //clear the association and release the object
}

@end
