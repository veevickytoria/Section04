//
//  iWinPopulateDatabase.m
//  MeetingBuilder
//
//  Created by CSSE Department on 12/17/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinPopulateDatabase.h"
#import "iWinAppDelegate.h"

@implementation iWinPopulateDatabase

-(void)populateContacts
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    NSManagedObject *newContact = [NSEntityDescription insertNewObjectForEntityForName:@"Contact" inManagedObjectContext:context];
    NSError *error;
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    
    if (result.count <= 1)
    {
        [newContact setValue:@"Dharmin" forKey:@"firstName"];
        [newContact setValue:@"Shah" forKey:@"lastName"];
        [newContact setValue:@"shahdk@rose-hulman.edu" forKey:@"email"];
        [newContact setValue:@"iWin LLC" forKey:@"company"];
        [newContact setValue:@"(812)345-9876" forKey:@"phone"];
        [newContact setValue:@"Product Owner" forKey:@"title"];
        [newContact setValue:@"Terre Haute, IN" forKey:@"location"];
        
        [newContact setValue:[NSNumber numberWithInt:1] forKey:@"userID"];
        [context save:&error];
        
        newContact = [NSEntityDescription insertNewObjectForEntityForName:@"Contact" inManagedObjectContext:context];
        [newContact setValue:@"Rain" forKey:@"firstName"];
        [newContact setValue:@"Dartt" forKey:@"lastName"];
        [newContact setValue:@"darttrf@rose-hulman.edu" forKey:@"email"];
        [newContact setValue:[NSNumber numberWithInt:2] forKey:@"userID"];
        [context save:&error];
        
        newContact = [NSEntityDescription insertNewObjectForEntityForName:@"Contact" inManagedObjectContext:context];
        [newContact setValue:@"Brian" forKey:@"firstName"];
        [newContact setValue:@"Padilla" forKey:@"lastName"];
        [newContact setValue:@"padillbt@rose-hulman.edu" forKey:@"email"];
        [newContact setValue:[NSNumber numberWithInt:3] forKey:@"userID"];
        [context save:&error];
    }
}

@end
