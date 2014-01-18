//
//  iWinLoginViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinLoginViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "Settings.h"
#import "Contact.h"
#import "iWinAppDelegate.h"
#include <CommonCrypto/CommonDigest.h>

@interface iWinLoginViewController ()
@property (strong, nonatomic) iWinAppDelegate *appDelegate;
@property (strong, nonatomic) NSManagedObjectContext *context;
@end

@implementation iWinLoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.appDelegate = [[UIApplication sharedApplication] delegate];
    
    self.context = [self.appDelegate managedObjectContext];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onClickLogin:(id)sender
{
    
    NSString *email = [[self.userNameField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *password = [self sha256HashFor:[self.passwordField text]];
//    [self.loginDelegate login:1];
    
//    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/5331"];
//    
//    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
//    [urlRequest setHTTPMethod:@"DELETE"];
//    NSURLResponse * response = nil;
//    NSError * error = nil;
//    [NSURLConnection sendSynchronousRequest:urlRequest
//                          returningResponse:&response
//                                      error:&error];
    
    if (password.length > 0 && email.length>0)
    {
        //register
        NSArray *keys = [NSArray arrayWithObjects:@"email", @"password", nil];
        NSArray *objects = [NSArray arrayWithObjects:email, password,nil];
        
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        NSData *jsonData;
        NSString *jsonString;
        
        if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
        {
            jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
            jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        }
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Login"];
        
        NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
        [urlRequest setHTTPMethod:@"POST"];
        [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
        [urlRequest setHTTPBody:jsonData];
        NSURLResponse * response = nil;
        NSError * error = nil;
        NSData * data =[NSURLConnection sendSynchronousRequest:urlRequest
                                             returningResponse:&response
                                                         error:&error];
        NSInteger userID = -1;
        if (error) {
            // Handle error.
        }
        else
        {
            NSError *jsonParsingError = nil;
            NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
            

            if ([deserializedDictionary objectForKey:@"userID"])
            {
                userID = [[deserializedDictionary objectForKey:@"userID"] integerValue];
                [self createContactWithID:userID withEmail:email withPassword:self.passwordField.text];
                [self.loginDelegate login:userID];
            }
            else
            {
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Username/Password not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
                [alert show];
            }
        }
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Enter valid values" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    
}

-(NSString*)sha256HashFor:(NSString*)input
{
    const char* str = [input UTF8String];
    unsigned char result[CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(str, strlen(str), result);
    
    NSMutableString *ret = [NSMutableString stringWithCapacity:CC_SHA256_DIGEST_LENGTH*2];
    for(int i = 0; i<CC_SHA256_DIGEST_LENGTH; i++)
    {
        [ret appendFormat:@"%02x",result[i]];
    }
    return ret;
}

- (IBAction)onClickJoinUs:(id)sender
{
    [self.loginDelegate joinUs];
}

-(void)createContactWithID:(NSInteger)userID withEmail:(NSString*)email withPassword:(NSString*)password
{
    NSArray *result = [self getEntity:@"Contact" withID:userID];
    NSError *error;
    if ([result count] == 0)
    {
        NSManagedObject *newSetting = [NSEntityDescription insertNewObjectForEntityForName:@"Contact" inManagedObjectContext:self.context];
        [newSetting setValue:[NSNumber numberWithInt:userID] forKey:@"userID"];
        [newSetting setValue:email forKey:@"email"];
        [self.context save:&error];
    }
    else
    {
        Contact *updateContact = (Contact*)[result objectAtIndex:0];
        [updateContact setValue:email forKey:@"email"];
        [self.context save:&error];
    }
    [self createSettingsWithID:userID withEmail:email withPassword:password];
}

-(void)createSettingsWithID:(NSInteger)userID withEmail:(NSString*)email withPassword:(NSString*)password
{
    NSArray *result = [self getEntity:@"Settings" withID:userID];
    NSError *error;
    if ([result count] == 0)
    {
        NSManagedObject *newSetting = [NSEntityDescription insertNewObjectForEntityForName:@"Settings" inManagedObjectContext:self.context];
        NSError *error;
        [newSetting setValue:[NSNumber numberWithInt:userID] forKey:@"userID"];
        [newSetting setValue:email forKey:@"email"];
        [newSetting setValue:password forKey:@"password"];
        [self.context save:&error];
    }
    else
    {
        Contact *updateSettings = (Contact*)[result objectAtIndex:0];
        [updateSettings setValue:email forKey:@"email"];
        [updateSettings setValue:password forKey:@"password"];
        [self.context save:&error];
    }
}


-(NSArray*)getEntity:(NSString*)entity withID:(NSInteger)userID
{
    NSEntityDescription *entityDesc;
    if ([entity isEqualToString:@"Settings"])
    {
        entityDesc = [NSEntityDescription entityForName:@"Settings" inManagedObjectContext:self.context];
    }
    else
    {
        entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:self.context];
    }
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userID = %d", userID];
    [request setPredicate:predicate];
    
    NSError *error;
    NSArray *result = [self.context executeFetchRequest:request
                                             error:&error];
    return result;
}

@end
