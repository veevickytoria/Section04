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
        [newContact setValue:@"Dharmin Shah" forKey:@"name"];
        [newContact setValue:@"shahdk@rose-hulman.edu" forKey:@"email"];
        [newContact setValue:@"iWin LLC" forKey:@"company"];
        [newContact setValue:@"(812)345-9876" forKey:@"phone"];
        [newContact setValue:@"Product Owner" forKey:@"title"];
        [newContact setValue:@"Terre Haute, IN" forKey:@"location"];
        
        [newContact setValue:[NSNumber numberWithInt:1] forKey:@"userID"];
        [context save:&error];
        
        newContact = [NSEntityDescription insertNewObjectForEntityForName:@"Contact" inManagedObjectContext:context];
        [newContact setValue:@"Rain Dartt" forKey:@"name"];
        [newContact setValue:@"darttrf@rose-hulman.edu" forKey:@"email"];
        [newContact setValue:[NSNumber numberWithInt:2] forKey:@"userID"];
        [context save:&error];
        
        newContact = [NSEntityDescription insertNewObjectForEntityForName:@"Contact" inManagedObjectContext:context];
        [newContact setValue:@"Brian Padilla" forKey:@"name"];
        [newContact setValue:@"padillbt@rose-hulman.edu" forKey:@"email"];
        [newContact setValue:[NSNumber numberWithInt:3] forKey:@"userID"];
        [context save:&error];
    }
}


-(void)populateSettings
{
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    NSManagedObject *newSettings = [NSEntityDescription insertNewObjectForEntityForName:@"Settings" inManagedObjectContext:context];
    NSError *error;
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Settings" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    
    if (result.count <= 1)
    {
        
        [newSettings setValue:@"shahdk@rose-hulman.edu" forKey:@"email"];
        [newSettings setValue:@"123456" forKey:@"password"];
        [newSettings setValue:[NSNumber numberWithInt:1] forKey:@"whenToNotify"];
        [newSettings setValue:[NSNumber numberWithInt:1] forKey:@"shouldNotify"];
        [newSettings setValue:[NSNumber numberWithInt:1] forKey:@"userID"];

        [context save:&error];
    }
}

@end
