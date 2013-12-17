//
//  iWinContact.h
//  MeetingBuilder
//
//  Created by CSSE Department on 12/11/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface iWinContact : NSObject

@property (strong, nonatomic) NSString *firstName;
@property (strong, nonatomic) NSString *lastName;
@property (strong, nonatomic) NSString *email;
@property (nonatomic) NSInteger userID;
@property (strong, nonatomic) NSString *phone;
@property (strong, nonatomic) NSString *company;
@property (strong, nonatomic) NSString *title;
@property (strong, nonatomic) NSString *location;

-(void) initializewithID:(NSInteger)userID withFirstName:(NSString *)firstName withLastName:(NSString *)lastName withEmail:(NSString *)email;
@end
