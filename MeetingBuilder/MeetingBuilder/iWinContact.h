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
-(void) initializeWithFirstName:(NSString *)firstName withLastName:(NSString *)lastName withEmail:(NSString *)email;
@end
