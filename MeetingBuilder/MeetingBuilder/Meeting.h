//
//  Meeting.h
//  MeetingBuilder
//
//  Created by CSSE Department on 1/15/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Meeting : NSManagedObject

@property (nonatomic, retain) NSString * attendance;
@property (nonatomic, retain) NSString * datetime;
@property (nonatomic, retain) NSString * location;
@property (nonatomic, retain) NSString * title;
@property (nonatomic, retain) NSNumber * userID;
@property (nonatomic, retain) NSNumber * meetingID;
@property (nonatomic, retain) NSString * endDatetime;
@property (nonatomic, retain) NSString * meetingDesc;

@end
