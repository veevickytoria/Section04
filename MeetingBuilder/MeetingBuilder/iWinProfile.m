//
//  iWinProfile.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/16/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinProfile.h"

@implementation iWinProfile
-(void) initializeWithDisplayName:(NSString *)displayName withEmail:(NSString *)email withPhone:(NSString *)phone withCompany:(NSString *)company withTitle:(NSString *)title withLocation:(NSString *)location
{
    self.displayName = displayName;
    self.email = email;
    self.phone = phone;
    self.company = company;
    self.title = title;
    self.location = location;
}
@end
