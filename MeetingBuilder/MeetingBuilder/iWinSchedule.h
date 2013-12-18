//
//  iWinSchedule.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 12/17/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface iWinSchedule : NSObject

@property (strong, nonatomic) NSMutableArray *scheduledMeetings;


-(void) initializeWithSchedule:(NSMutableArray *)schedule;

@end
