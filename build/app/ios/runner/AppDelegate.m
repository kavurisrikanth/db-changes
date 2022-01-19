#include "AppDelegate.h"
#include "GeneratedPluginRegistrant.h"
@implementation AppDelegate
- (BOOL)application:(UIApplication *)application
didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
[Gen
eratedPluginRegistrant registerWithRegistry:self];
return [super application:application didFinishLaunchingWithOptions:launchOptions];
}
@end