//
//  Task.h
//  MeetingBuilder
//
//  Created by Richard Shomer on 1/18/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Task : NSManagedObject

@property (nonatomic, retain) NSNumber * taskID;
@property (nonatomic, retain) NSString * title;
@property (nonatomic, retain) NSString * isCompleted;
@property (nonatomic, retain) NSString * desc;
@property (nonatomic, retain) NSString * deadline;
@property (nonatomic, retain) NSString * dateCreated;
@property (nonatomic, retain) NSString * dateAssigned;
@property (nonatomic, retain) NSNumber * assignedTo;
@property (nonatomic, retain) NSNumber * assignedFrom;
@property (nonatomic, retain) NSNumber * createdBy;



@end
