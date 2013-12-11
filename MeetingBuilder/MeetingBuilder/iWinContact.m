//
//  iWinContact.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/11/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinContact.h"

@implementation iWinContact

-(void) initializeWithFirstName:(NSString *)firstName withLastName:(NSString *)lastName withEmail:(NSString *)email
{
    self.firstName = firstName;
    self.lastName = lastName;
    self.email = email;
}

@end
