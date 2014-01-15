//
//  Settings.h
//  MeetingBuilder
//
//  Created by CSSE Department on 1/15/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Settings : NSManagedObject

@property (nonatomic, retain) NSString * email;
@property (nonatomic, retain) NSString * password;
@property (nonatomic, retain) NSNumber * userID;

@end
