//
//  iWinViewProfileViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/6/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

//#import "iWinEditProfileViewController.h"
#import "iWinViewProfileViewController.h"
//#import "iWinViewAndAddViewController.h"
#import "iWinAppDelegate.h"
#import "Contact.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewProfileViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) Contact *contact;
@property (nonatomic) NSInteger userID;
//@property (nonatomic) iWinProfile *profile;
//@property (strong, nonatomic) iWinEditProfileViewController *editProfileViewController;
@end

@implementation iWinViewProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
    }
    [self viewDidLoad];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.isEditing = NO;
    
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userID = 1"];
    [request setPredicate:predicate];
    
    NSError *error;
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    self.contact = (Contact*)[result objectAtIndex:0];
    
    self.cancel.hidden = YES;
    [self updateTextUI];
    
}
-(void)updateTextUI
{
//     NSString *url = [NSString stringWithFormat:@"%@ %i", @"http://csse371-04.csse.rose-hulman.edu/User/", self.userID];
//    //    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/"];  //////CONCATENATE userID
//        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
//        NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:30];
//        [urlRequest setHTTPMethod:@"GET"];
//        NSURLResponse * response = nil;
//        NSError * error = nil;
//        NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
//                                              returningResponse:&response
//                                                            error:&error];
//       NSArray *jsonArray;
//        if (error)
//        {
//            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
//            [alert show];
//        }
//        else
//        {
//            NSError *jsonParsingError = nil;
//            jsonArray = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
//        }
//        if (jsonArray.count > 0)
//        {
//            for (NSDictionary* users in jsonArray)
//            {
//                self.displayNameTextField.text = (NSString *) [users objectForKey:@"displayName"];
//                self.emailTextField.text = (NSString *) [users objectForKey:@"email"];
//                self.phoneTextField.text = (NSString *) [users objectForKey:@"phone"];
//                self.companyTextField.text = (NSString *) [users objectForKey:@"company"];
//                self.titleTextField.text = (NSString *) [users objectForKey:@"title"];
//                self.locationTextField.text = (NSString *) [users objectForKey:@"location"];
//
//    //            iWinContact *c = [[iWinContact alloc] init];
//    //            c.userID = (NSInteger)[users objectForKey:@"userID"];
//    //
//    //            //NSString *displayName = (NSString *)[users objectForKey:@"displayName"];
//    //            //NSInteger nWords = 2;
//    //            //NSRange wordRange = NSMakeRange(0, nWords);
//    //            //NSArray *firstAndLastNames = [[displayName componentsSeparatedByString:@" "] subarrayWithRange:wordRange];
//    //            //c.firstName = (NSString *)[firstAndLastNames objectAtIndex:0];
//    //            //c.lastName = (NSString *)[firstAndLastNames objectAtIndex:1];
//    //
//                  c.name = (NSString *)[users objectForKey:@"name"];
//    //            c.email = (NSString *)[users objectForKey:@"email"];
//    //            c.phone = (NSString *)[users objectForKey:@"phone"];
//    //            c.company = (NSString *)[users objectForKey:@"companyc"];
//    //            c.title = (NSString *)[users objectForKey:@"title"];
//    //            c.location = (NSString *)[users objectForKey:@"location"];
//    //            
//    //            [self.userList addObject:c];
//            }
//        }
    
    
    
    //PULL FROM DB

    [self.displayNameTextField setText:[NSString stringWithFormat:@"%@", self.contact.name]];
    self.emailTextField.text =  self.contact.email;
    self.phoneTextField.text = self.contact.phone;
    self.companyTextField.text = self.contact.company;
    self.titleTextField.text = self.contact.title;
    self.locationTextField.text = self.contact.location;
//    
//    [self unUpdateTextFieldUI:self.displayNameTextField];
//    [self unUpdateTextFieldUI:self.companyTextField];
//    [self unUpdateTextFieldUI:self.titleTextField];
//    [self unUpdateTextFieldUI:self.emailTextField];
//    [self unUpdateTextFieldUI:self.phoneTextField];
//    [self unUpdateTextFieldUI:self.locationTextField];
    

}

-(void) updateTextFieldUI: (UITextField *)textField
{
    textField.layer.cornerRadius = 7;
    textField.layer.borderColor = [[UIColor whiteColor] CGColor];   //May not need
}

-(void) unUpdateTextFieldUI: (UITextField *)textField
{
    textField.layer.cornerRadius = 7;
    textField.layer.borderColor = [[UIColor whiteColor] CGColor];   //May not need
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) saveChanges
{
    

//    NSArray *fields = [NSArray arrayWithObjects:@"displayName", @"email", @"phone", @"company", @"title", @"location",nil];
//    NSArray *values = [NSArray arrayWithObjects:self.displayNameTextField.text, self.emailTextField.text, self.phoneTextField.text, self.companyTextField.text, self.titleTextField.text, self.locationTextField.text,nil];
//    
//    NSArray *keys = [NSArray arrayWithObjects:@"userID", @"field", @"value", nil];
//
//    for (int i = 0; i < 6; i++) {
//    
//    NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.userID], fields[i], values[i], nil];
//
//        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
//        NSData *jsonData;
//        NSString *jsonString;
//    
//        if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
//        {
//            jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
//            jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
//        }
//        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/"];
//    
//        NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
//        [urlRequest setHTTPMethod:@"POST"];
//        [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
//        [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
//        [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
//        [urlRequest setHTTPBody:jsonData];
//        NSURLResponse * response = nil;
//        NSError * error = nil;
//        [NSURLConnection sendSynchronousRequest:urlRequest
//                              returningResponse:&response
//                                          error:&error];
//    
//    
//    }
    // SAVE TO DB
    
    
    iWinAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
    NSManagedObjectContext *context = [appDelegate managedObjectContext];
    NSError *error;
    
    NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Contact" inManagedObjectContext:context];
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    [request setEntity:entityDesc];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"userID = %@", self.contact.userID];
    [request setPredicate:predicate];
    
    NSArray *result = [context executeFetchRequest:request
                                             error:&error];
    
    self.contact = (Contact*)[result objectAtIndex:0];
    
//    NSInteger nWords = 2;
//    NSRange wordRange = NSMakeRange(0, nWords);
//    NSArray *firstAndLastNames = [[self.displayNameTextField.text componentsSeparatedByString:@" "] subarrayWithRange:wordRange];
    
 
    [self.contact setValue:self.displayNameTextField.text forKey:@"name"];
    [self.contact setValue:self.emailTextField.text forKey:@"email"];
    [self.contact setValue:self.phoneTextField.text forKey:@"phone"];
    [self.contact setValue:self.companyTextField.text forKey:@"company"];
    [self.contact setValue:self.locationTextField.text forKey:@"location"];
    [self.contact setValue:self.titleTextField.text forKey:@"title"];
    [context save:&error];
    
   

}

-(IBAction)onCancel:(id)sender
{
    //PULL FROM DB
    
    
    self.displayNameTextField.userInteractionEnabled = NO;
    self.companyTextField.userInteractionEnabled = NO;
    self.titleTextField.userInteractionEnabled = NO;
    self.emailTextField.userInteractionEnabled = NO;
    self.phoneTextField.userInteractionEnabled = NO;
    self.locationTextField.userInteractionEnabled = NO;

    [self updateTextUI];
    
//    [self unUpdateTextFieldUI:self.displayNameTextField];
//    [self unUpdateTextFieldUI:self.companyTextField];
//    [self unUpdateTextFieldUI:self.titleTextField];
//    [self unUpdateTextFieldUI:self.emailTextField];
//    [self unUpdateTextFieldUI:self.phoneTextField];
//    [self unUpdateTextFieldUI:self.locationTextField];
    
    
//    self.displayNameTextField.text =  [NSString stringWithFormat:@"%@ %@", self.contact.firstName, self.contact.lastName];
//    self.emailTextField.text =  self.contact.email;
//    self.phoneTextField.text = self.contact.phone;
//    self.companyTextField.text = self.contact.company;
//    self.titleTextField.text = self.contact.title;
//    self.locationTextField.text = self.contact.location;
    
    [self.editProfile setTintColor:[UIColor blueColor]];
    self.isEditing = NO;
    [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
    self.cancel.hidden = YES;
    
}

-(IBAction)onEditProfile:(id)sender
{
    if (self.isEditing) {
        [self saveChanges];
        self.displayNameTextField.userInteractionEnabled = NO;
        self.companyTextField.userInteractionEnabled = NO;
        self.titleTextField.userInteractionEnabled = NO;
        self.emailTextField.userInteractionEnabled = NO;
        self.phoneTextField.userInteractionEnabled = NO;
        self.locationTextField.userInteractionEnabled = NO;
        
        [self unUpdateTextFieldUI:self.displayNameTextField];
        [self unUpdateTextFieldUI:self.companyTextField];
        [self unUpdateTextFieldUI:self.titleTextField];
        [self unUpdateTextFieldUI:self.emailTextField];
        [self unUpdateTextFieldUI:self.phoneTextField];
        [self unUpdateTextFieldUI:self.locationTextField];
        
        
        [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
        [self.editProfile setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        [self saveChanges];
        self.cancel.hidden = YES;
        self.isEditing = NO;
        
    } else{
        self.isEditing = YES;
        self.displayNameTextField.userInteractionEnabled = YES;
        self.companyTextField.userInteractionEnabled = YES;
        self.titleTextField.userInteractionEnabled = YES;
        self.emailTextField.userInteractionEnabled = YES;
        self.phoneTextField.userInteractionEnabled = YES;
        self.locationTextField.userInteractionEnabled = YES;
        
        [self updateTextFieldUI:self.displayNameTextField];
        [self updateTextFieldUI:self.companyTextField];
        [self updateTextFieldUI:self.titleTextField];
        [self updateTextFieldUI:self.emailTextField];
        [self updateTextFieldUI:self.phoneTextField];
        [self updateTextFieldUI:self.locationTextField];
  
        [self.editProfile setTitle:@"Save" forState:UIControlStateNormal];
        [self.editProfile setTitleColor:[UIColor greenColor] forState:UIControlStateNormal];
        self.cancel.hidden = NO;
        
    }
}
@end
