//
//  iWinProfile.h
//  MeetingBuilder
//
//  Created by CSSE Department on 12/16/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface iWinProfile : NSObject
//@property (strong, nonatomic) NSString *userID;
@property (strong, nonatomic) NSString *displayName;
@property (strong, nonatomic) NSString *email;
@property (strong, nonatomic) NSString *phone;
@property (strong, nonatomic) NSString *company;
@property (strong, nonatomic) NSString *title; //position
@property (strong, nonatomic) NSString *location;
-(void) initializeWithDisplayName:(NSString *)displayName withEmail:(NSString *)email withPhone:(NSString *)phone withCompany:(NSString *)company withTitle:(NSString *)title withLocation:(NSString *)location;
@end
