//
//  iWinAppDelegate.h
//  MeetingBuilder
//
//  Created by CSSE Department on 9/25/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface iWinAppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

- (void)saveContext;
- (NSURL *)applicationDocumentsDirectory;

@end
