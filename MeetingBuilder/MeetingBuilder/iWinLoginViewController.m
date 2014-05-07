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
#import "RememberMe.h"
#import "iWinBackEndUtility.h"
#include <CommonCrypto/CommonDigest.h>
#import "iWinConstants.h"
#import <Parse/Parse.h>

@interface iWinLoginViewController ()
@property (strong, nonatomic) iWinAppDelegate *appDelegate;
@property (strong, nonatomic) NSManagedObjectContext *context;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@end

NSString* const LOGIN_URL = @"%@/User/Login";
NSString* const USERNAME_NOT_FOUND_MESSAGE = @"Username/Password not found";
NSString* const VALID_VALUE_MESSAGE = @"Enter valid values";
NSString* const SHOULD_NOTIFY_KEY = @"shouldNotify";
NSString* const WHEN_TO_NOTIFY_KEY = @"whenToNotify";

@implementation iWinLoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.appDelegate = [[UIApplication sharedApplication] delegate];
    self.context = [self.appDelegate managedObjectContext];
    NSArray *results = [self getRememberMeInfo];
    if ([results count] > 0)
    {
        RememberMe *rm = (RememberMe*)[results objectAtIndex:0];
        self.userNameField.text = rm.email;
        self.passwordField.text = rm.password;
    }
    self.backendUtility = [[iWinBackEndUtility alloc] init];
}

-(NSArray*) getRememberMeInfo
{
    NSFetchRequest *request = [[NSFetchRequest alloc]initWithEntityName:REMEMBER_ME_ENTITY];
    NSError *error = nil;
    return [self.context executeFetchRequest:request error:&error];
}

-(void) deleteRememberMeInfo
{
    NSError *error;
    for (NSManagedObject * rm in [self getRememberMeInfo])
    {
        [self.context deleteObject:rm];
    }
    [self.context save:&error];
}

- (IBAction)onClickLogin:(id)sender
{
    
    NSString *email = [[self.userNameField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *password = [self sha256HashFor:[self.passwordField text]];
    
    
    
    if (password.length > 0 && email.length>0)
    {
        NSArray *keys = [NSArray arrayWithObjects:EMAIL_KEY, PASSWORD_KEY, nil];
        NSArray *objects = [NSArray arrayWithObjects:email, password,nil];
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        
        NSString *url = [NSString stringWithFormat:LOGIN_URL, DATABASE_URL];
        NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];

        NSInteger userID = -1;
        if (deserializedDictionary)
        {
            if ([deserializedDictionary objectForKey:USER_ID_KEY])
            {
                userID = [[deserializedDictionary objectForKey:USER_ID_KEY] integerValue];
                [self createContactWithID:userID withEmail:email withPassword:password];
                if ([self.rememberSwitch isOn])
                {
                    [self deleteRememberMeInfo];
                    NSManagedObject *newRememberMe = [NSEntityDescription insertNewObjectForEntityForName:REMEMBER_ME_ENTITY inManagedObjectContext:self.context];
                    NSError *error;
                    [newRememberMe setValue:email forKey:EMAIL_KEY];
                    [newRememberMe setValue:self.passwordField.text forKey:PASSWORD_KEY];
                    [self.context save:&error];
                    
                }
                else
                {
                    [self deleteRememberMeInfo];
                }
                
                [self.loginDelegate login:userID];
                
                NSString *url = [NSString stringWithFormat:USER_ID_URL, DATABASE_URL,userID];
                url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
                NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
                
                [PFUser logInWithUsername:[deserializedDictionary objectForKey:NAME_KEY] password:[self sha256HashFor:[self.passwordField text]]];
                
                [PFAnalytics trackAppOpenedWithLaunchOptions:nil];
                PFUser* pUser = [PFUser currentUser];
                if (pUser != nil)
                {
                    NSArray *keys = [NSArray arrayWithObjects:@"user", @"userId", nil];
                    NSArray *objects = [NSArray arrayWithObjects:pUser, [pUser objectId], nil];
                    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
                    
                    PFInstallation* pInstall = [PFInstallation currentInstallation];
                    [pInstall setValuesForKeysWithDictionary:jsonDictionary];
                    
                    [pInstall saveEventually];
                }
                
            }
            else
            {
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:USERNAME_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
                [alert show];
            }
        }
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:VALID_VALUE_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
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
    NSArray *result = [self getEntity:CONTACT_ENTITY withID:userID];
    NSError *error;
    if ([result count] == 0)
    {
        NSManagedObject *newContact = [NSEntityDescription insertNewObjectForEntityForName:CONTACT_ENTITY inManagedObjectContext:self.context];
        [newContact setValue:[NSNumber numberWithInt:userID] forKey:USER_ID_KEY];
        [newContact setValue:email forKey:EMAIL_KEY];
        [self.context save:&error];
    }
    else
    {
        Contact *updateContact = (Contact*)[result objectAtIndex:0];
        [updateContact setValue:email forKey:EMAIL_KEY];
        [self.context save:&error];
    }
    [self createSettingsWithID:userID withEmail:email withPassword:password];
}

-(void)createSettingsWithID:(NSInteger)userID withEmail:(NSString*)email withPassword:(NSString*)password
{
    NSArray *result = [self getEntity:SETTINGS_ENTITY withID:userID];
    NSError *error;
    if ([result count] == 0)
    {
        NSManagedObject *newSetting = [NSEntityDescription insertNewObjectForEntityForName:SETTINGS_ENTITY inManagedObjectContext:self.context];
        NSError *error;
        [newSetting setValue:[NSNumber numberWithInt:userID] forKey:USER_ID_KEY];
        [newSetting setValue:email forKey:EMAIL_KEY];
        [newSetting setValue:password forKey:PASSWORD_KEY];
        [newSetting setValue:[NSNumber numberWithInt:1] forKey:SHOULD_NOTIFY_KEY];
        [newSetting setValue:[NSNumber numberWithInt:2] forKey:WHEN_TO_NOTIFY_KEY];
        [self.context save:&error];
    }
    else
    {
        Settings *updateSettings = (Settings*)[result objectAtIndex:0];
        [updateSettings setValue:email forKey:EMAIL_KEY];
        [updateSettings setValue:password forKey:PASSWORD_KEY];
        [updateSettings setValue:[NSNumber numberWithInt:1] forKey:SHOULD_NOTIFY_KEY];
        [updateSettings setValue:[NSNumber numberWithInt:2] forKey:WHEN_TO_NOTIFY_KEY];
        [self.context save:&error];
    }
}


-(NSArray*)getEntity:(NSString*)entity withID:(NSInteger)userID
{
    NSEntityDescription *entityDesc;
    if ([entity isEqualToString:SETTINGS_ENTITY])
    {
        entityDesc = [NSEntityDescription entityForName:SETTINGS_ENTITY inManagedObjectContext:self.context];
    }
    else
    {
        entityDesc = [NSEntityDescription entityForName:CONTACT_ENTITY inManagedObjectContext:self.context];
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

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if ([textField isEqual:self.userNameField])
    {
        [self.passwordField becomeFirstResponder];
    }
    else
    {
        [textField resignFirstResponder];
        [self onClickLogin:nil];
    }
    return YES;
}

@end
