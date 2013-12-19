//
//  iWinMeeting.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 12/17/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface iWinMeeting : NSObject

@property (strong, nonatomic) NSString *title;
@property (strong, nonatomic) NSString *description;
@property (strong, nonatomic) NSDate *date;
@property (nonatomic) NSTimeInterval *meetingTime;

-(void) initializeWithTitle:(NSString *)title withDescription:(NSString *)desc withDate:(NSDate *)date withTimespan:(NSTimeInterval *)time;

@end
