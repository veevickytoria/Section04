//
//  Meeting.h
//  MeetingBuilder
//
//  Created by X. Ding on 12/18/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Meeting : NSManagedObject

@property (nonatomic, retain) NSString * title;
@property (nonatomic, retain) NSNumber * userID;
@property (nonatomic, retain) NSString * location;
@property (nonatomic, retain) NSString * datetime;
@property (nonatomic, retain) NSString * attendance;

@end
