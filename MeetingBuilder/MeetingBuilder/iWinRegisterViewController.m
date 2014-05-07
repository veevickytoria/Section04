//
//  iWinRegisterViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinRegisterViewController.h"
#include <CommonCrypto/CommonDigest.h>
#import "iWinAppDelegate.h"
#import "iWinBackEndUtility.h"
#import "Contact.h"
#import "iWinConstants.h"
#import <Parse/Parse.h>

@interface iWinRegisterViewController ()
@property (strong, nonatomic) iWinAppDelegate *appDelegate;
@property (strong, nonatomic) NSManagedObjectContext *context;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;
@end

const int MIN_PHONE_LENGTH = 8;
const int MAX_PHONE_LENGTH = 12;
NSString* const PHONE_SEPARATOR = @"-";
NSString* const AT_RATE_SYMBOL = @"@";
NSString* const INVALID_NAME_MESSAGE = @"Please enter your name!";
NSString* const INVALID_PASSWORD_MESSAGE = @"Password must be at least 6 characters!";
NSString* const INVALID_CONFIRM_PASSWORD_MESSAGE = @"Please enter matching passwords!";
NSString* const INVALID_PHONE_MESSAGE = @"Please enter a valid phone number!";
NSString* const INVALID_EMAIL_MESSAGE = @"Please enter a valid email address!";
NSString* const EMAIL_EXISTS_MESSAGE = @"An account with the email already exists.";
NSString* const USERS_LIST_URL = @"%@/User/Users";
NSString* const USER_URL = @"%@/User/";
NSString* const PHONE_KEY = @"phone";
NSString* const COMPANY_KEY = @"company";


@implementation iWinRegisterViewController

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
    self.backendUtility = [[iWinBackEndUtility alloc] init];
}

- (BOOL)validatePhoneNumber
{
    NSCharacterSet* notDigits = [[NSCharacterSet decimalDigitCharacterSet] invertedSet];
    if (self.phoneNumberField.text.length == 0) {
        return YES;
    }
    if (self.phoneNumberField.text.length > MAX_PHONE_LENGTH || self.phoneNumberField.text.length < MIN_PHONE_LENGTH) {
        return NO;
    }
    NSArray *phoneParts = [self.phoneNumberField.text componentsSeparatedByString:PHONE_SEPARATOR];
    for (NSString *stringPart in phoneParts) {
        if ([stringPart rangeOfCharacterFromSet:notDigits].location != NSNotFound)
        {
            return NO;
        }
    }
    return YES;

}

- (NSString*)validateRegistration
{
    NSString *name = [[self.nameField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *email = [[self.emailField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *password = [self.passwordField text];
    NSString *confirmPassword = [self.confirmPasswordField text];
    NSString *emailSymbol = AT_RATE_SYMBOL;
    
    if (name.length == 0) {
        return INVALID_NAME_MESSAGE;
    }
    if (password.length < 6) {
        return INVALID_PASSWORD_MESSAGE;
    }
    if (![password isEqualToString:confirmPassword]) {
        return INVALID_CONFIRM_PASSWORD_MESSAGE;
    }
    if (![self validatePhoneNumber]) {
        return INVALID_PHONE_MESSAGE;
    }
    if (email.length == 0 || [email rangeOfString:emailSymbol].location == NSNotFound) {
        return INVALID_EMAIL_MESSAGE;
    }
    if ([self emailExists:email]){
        return EMAIL_EXISTS_MESSAGE;
    }
    return EMPTY_STRING;
}

-(BOOL)emailExists:(NSString*)email
{
    NSString *url = [NSString stringWithFormat:USERS_LIST_URL, DATABASE_URL];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    NSArray *jsonArray;
    if (!deserializedDictionary)
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:USER_NOT_FOUND_MESSAGE delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        jsonArray = [deserializedDictionary objectForKey:USERS_KEY];
    }
    if (jsonArray.count > 0)
    {
        for (NSDictionary* users in jsonArray)
        {
            NSString* userEmail = (NSString *)[users objectForKey:EMAIL_KEY];
            if ([email isEqualToString:userEmail])
                return YES;
        }
    }
    return NO;
}

- (IBAction)onClickRegister:(id)sender
{
    NSString *error = [self validateRegistration];
    if (error.length == 0)
    {
        NSString *email = [[self.emailField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        NSString *password = [self sha256HashFor: [self.passwordField text]];
        NSString *url = [NSString stringWithFormat:USER_URL, DATABASE_URL];
        
        NSArray *keys = [NSArray arrayWithObjects:NAME_KEY, PASSWORD_KEY, EMAIL_KEY, PHONE_KEY, COMPANY_KEY, TITLE_KEY, LOCATION_KEY, nil];
        NSArray *objects = [NSArray arrayWithObjects:self.nameField.text, password, email, self.phoneNumberField.text, self.companyField.text, self.titleField.text, self.locationField.text,nil];
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];        
        NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
        NSInteger userID = -1;
        if (deserializedDictionary) {
            userID = [[deserializedDictionary objectForKey:USER_ID_KEY] integerValue];
            [self createContactWithID:userID withEmail:email withPassword:self.passwordField.text];
        }
        
        [self deleteRememberMeInfo];
        NSManagedObject *newRememberMe = [NSEntityDescription insertNewObjectForEntityForName:REMEMBER_ME_ENTITY inManagedObjectContext:self.context];
        NSError *error;
        [newRememberMe setValue:email forKey:EMAIL_KEY];
        [newRememberMe setValue:self.passwordField.text forKey:PASSWORD_KEY];
        [self.context save:&error];
        
        [self.registerDelegate onRegister:userID];
        
        [self parseSDKRegistration:userID];
        
    }
    else
    {
        [self failRegisterValidation:error];
    }

}

-(void) parseSDKRegistration:(NSInteger)userID
{
    PFUser* pUser = [[PFUser alloc] init];
    [pUser setEmail:[[self.emailField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]];
    [pUser setUsername:self.nameField.text];
    [pUser setPassword:[self sha256HashFor: [self.passwordField text]]];
    
    
    NSArray *keys = [NSArray arrayWithObjects:@"backendId", nil];
    NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:userID], nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [pUser setValuesForKeysWithDictionary:jsonDictionary];
    
    [pUser signUpInBackground];
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

-(void)createContactWithID:(NSInteger)userID withEmail:(NSString*)email withPassword:(NSString*)password
{
    NSArray *result = [self getEntity:CONTACT_ENTITY withID:userID];
    NSError *error;
    if ([result count] == 0)
    {
        NSManagedObject *newSetting = [NSEntityDescription insertNewObjectForEntityForName:CONTACT_ENTITY inManagedObjectContext:self.context];
        [newSetting setValue:[NSNumber numberWithInt:userID] forKey:USER_ID_KEY];
        [newSetting setValue:email forKey:EMAIL_KEY];
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
        [self.context save:&error];
    }
    else
    {
        Contact *updateSettings = (Contact*)[result objectAtIndex:0];
        [updateSettings setValue:email forKey:EMAIL_KEY];
        [updateSettings setValue:password forKey:PASSWORD_KEY];
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

- (void)failRegisterValidation:(NSString *)error
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:ERROR_MESSAGE message:error delegate:self cancelButtonTitle:OK_BUTTON otherButtonTitles: nil];
    [alert show];
}

- (IBAction)onClickCancel:(id)sender
{
    [self.registerDelegate onCancel];
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    if ([textField isEqual:self.nameField])
    {
        [self.emailField becomeFirstResponder];
    }
    else if ([textField isEqual:self.emailField])
    {
        [self.locationField becomeFirstResponder];
    }
    else if ([textField isEqual:self.locationField])
    {
        [self.companyField becomeFirstResponder];
    }
    else if ([textField isEqual:self.companyField])
    {
        [self.phoneNumberField becomeFirstResponder];
    }
    else if ([textField isEqual:self.phoneNumberField])
    {
        [self.titleField becomeFirstResponder];
    }
    else if ([textField isEqual:self.titleField])
    {
        [self.passwordField becomeFirstResponder];
    }
    else if ([textField isEqual:self.passwordField])
    {
        [self.confirmPasswordField becomeFirstResponder];
    }
    else
    {
        [textField resignFirstResponder];
        [self onClickRegister:nil];
    }
    return YES;
}


@end
