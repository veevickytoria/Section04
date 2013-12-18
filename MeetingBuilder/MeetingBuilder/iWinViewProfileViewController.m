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
@property (nonatomic) NSString *pageName;
@property (nonatomic) BOOL isEditing;
@property (nonatomic) Contact *contact;
//@property (nonatomic) iWinProfile *profile;
//@property (strong, nonatomic) iWinEditProfileViewController *editProfileViewController;
@end

@implementation iWinViewProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withPageName:(NSString *)pageName inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        //self.pageName = pageName;
        //self.isEditing = isEditing;
    }
    [self viewDidLoad];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self updateButtonUI:self.editProfile];
    
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
    
    //[self updateButtonUI:self.editProfile];
}
-(void) updateTextUI
{
    
    //Need to make fonts bigger


    self.displayNameTextView.text =  [NSString stringWithFormat:@"%@ %@", self.contact.firstName, self.contact.lastName];
    self.emailTextView.text =  self.contact.email;
    self.phoneTextView.text = self.contact.phone;
    self.companyTextView.text = self.contact.company;
    self.titleTextView.text = self.contact.title;
    self.locationTextView.text = self.contact.location;
    

}

-(void) updateButtonUI: (UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
    [button setTintColor:[UIColor blueColor]];
}

-(void) updateTextViewUI: (UITextView *)textView
{
    textView.layer.cornerRadius = 7;
    textView.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    textView.layer.borderWidth = 1.0f;
    //[textView setTintColor:[UIColor blueColor]];
}

-(void) unUpdateTextViewUI: (UITextView *)textView
{
    textView.layer.cornerRadius = 7;
    textView.layer.borderColor = [[UIColor whiteColor] CGColor];
    textView.layer.borderWidth = 1.0f;
    //[textView setTintColor:[UIColor blueColor]];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//-(IBAction)onChangePicture:(id)sender
//{
//    
//}

-(void) saveChanges
{
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
    
    NSInteger nWords = 2;
    NSRange wordRange = NSMakeRange(0, nWords);
    NSArray *firstAndLastNames = [[self.displayNameTextView.text componentsSeparatedByString:@" "] subarrayWithRange:wordRange];
    
 
    [self.contact setValue:(NSString *)[firstAndLastNames objectAtIndex:0] forKey:@"firstName"];
    [self.contact setValue:(NSString *)[firstAndLastNames objectAtIndex:1] forKey:@"lastName"];
    [self.contact setValue:self.emailTextView.text forKey:@"email"];
    [self.contact setValue:self.phoneTextView.text forKey:@"phone"];
    [self.contact setValue:self.companyTextView.text forKey:@"company"];
    [self.contact setValue:self.locationTextView.text forKey:@"location"];
    [self.contact setValue:self.titleTextView.text forKey:@"title"];
    [context save:&error];
        
   

}

-(IBAction)onCancel:(id)sender
{
    [self.displayNameTextView setEditable:NO];
    [self.titleTextView setEditable:NO];
    [self.companyTextView setEditable:NO];
    [self.locationTextView setEditable:NO];
    [self.emailTextView setEditable:NO];
    [self.phoneTextView setEditable:NO];
    
    [self unUpdateTextViewUI:self.displayNameTextView];
    [self unUpdateTextViewUI:self.companyTextView];
    [self unUpdateTextViewUI:self.titleTextView];
    [self unUpdateTextViewUI:self.emailTextView];
    [self unUpdateTextViewUI:self.phoneTextView];
    [self unUpdateTextViewUI:self.locationTextView];
    
    self.displayNameTextView.text =  [NSString stringWithFormat:@"%@ %@", self.contact.firstName, self.contact.lastName];
    self.emailTextView.text =  self.contact.email;
    self.phoneTextView.text = self.contact.phone;
    self.companyTextView.text = self.contact.company;
    self.titleTextView.text = self.contact.title;
    self.locationTextView.text = self.contact.location;
    
    [self.editProfile setTintColor:[UIColor blueColor]];
    self.isEditing = NO;
    [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
    self.cancel.hidden = YES;
    
}

-(IBAction)onEditProfile:(id)sender
{
    if (self.isEditing) {
        [self saveChanges];
        [self.displayNameTextView setEditable:NO];
        [self.titleTextView setEditable:NO];
        [self.companyTextView setEditable:NO];
        [self.locationTextView setEditable:NO];
        [self.emailTextView setEditable:NO];
        [self.phoneTextView setEditable:NO];
        
        [self unUpdateTextViewUI:self.displayNameTextView];
        [self unUpdateTextViewUI:self.companyTextView];
        [self unUpdateTextViewUI:self.titleTextView];
        [self unUpdateTextViewUI:self.emailTextView];
        [self unUpdateTextViewUI:self.phoneTextView];
        [self unUpdateTextViewUI:self.locationTextView];
        
        
        [self.editProfile setTitle:@"Edit Profile" forState:UIControlStateNormal];
        [self.editProfile setTintColor:[UIColor blueColor]];
        [self saveChanges];
        self.cancel.hidden = YES;
        self.isEditing = NO;
        
    } else{
        self.isEditing = YES;
        [self.displayNameTextView setEditable:YES];
        [self.titleTextView setEditable:YES];
        [self.companyTextView setEditable:YES];
        [self.locationTextView setEditable:YES];
        [self.emailTextView setEditable:YES];
        [self.phoneTextView setEditable:YES];
        
        [self updateTextViewUI:self.displayNameTextView];
        [self updateTextViewUI:self.companyTextView];
        [self updateTextViewUI:self.titleTextView];
        [self updateTextViewUI:self.emailTextView];
        [self updateTextViewUI:self.phoneTextView];
        [self updateTextViewUI:self.locationTextView];

        
        
        [self.editProfile setTitle:@"Save" forState:UIControlStateNormal];
        [self.editProfile setTintColor:[UIColor greenColor]];
        [self updateButtonUI:self.cancel];
        self.cancel.hidden = NO;
   
        //Make cancel button visible
        
    }
}
@end
