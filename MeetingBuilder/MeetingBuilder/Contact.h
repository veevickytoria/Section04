//
//  Contact.h
//  MeetingBuilder
//
//  Created by CSSE Department on 1/17/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Contact : NSManagedObject

@property (nonatomic, retain) NSString * company;
@property (nonatomic, retain) NSString * email;
@property (nonatomic, retain) NSString * location;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSString * phone;
@property (nonatomic, retain) NSString * title;
@property (nonatomic, retain) NSNumber * userID;

@end
