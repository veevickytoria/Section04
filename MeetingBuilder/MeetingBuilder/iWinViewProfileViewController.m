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
//@property (nonatomic) iWinProfile *profile;
//@property (strong, nonatomic) iWinEditProfileViewController *editProfileViewController;
@end

@implementation iWinViewProfileViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    [self viewDidLoad];
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self updateButtonUI:self.editProfile];
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
    
    //[self updateButtonUI:self.editProfile];
}
-(void)updateTextUI
{
    
    //Need to make fonts bigger


    [self.displayNameTextField setText:[NSString stringWithFormat:@"%@ %@", self.contact.firstName, self.contact.lastName]];
    self.emailTextField.text =  self.contact.email;
    self.phoneTextField.text = self.contact.phone;
    self.companyTextField.text = self.contact.company;
    self.titleTextField.text = self.contact.title;
    self.locationTextField.text = self.contact.location;
    
    [self unUpdateTextFieldUI:self.displayNameTextField];
    [self unUpdateTextFieldUI:self.companyTextField];
    [self unUpdateTextFieldUI:self.titleTextField];
    [self unUpdateTextFieldUI:self.emailTextField];
    [self unUpdateTextFieldUI:self.phoneTextField];
    [self unUpdateTextFieldUI:self.locationTextField];
    

}

-(void) updateButtonUI: (UIButton *)button
{
    button.layer.cornerRadius = 7;
    button.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    button.layer.borderWidth = 1.0f;
    [button setTintColor:[UIColor blueColor]];
}

-(void) updateTextFieldUI: (UITextField *)textField
{
    textField.layer.cornerRadius = 7;
    textField.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    textField.layer.borderWidth = 1.0f;
    //[textView setTintColor:[UIColor blueColor]];
}

-(void) unUpdateTextFieldUI: (UITextField *)textField
{
    textField.layer.cornerRadius = 7;
    textField.layer.borderColor = [[UIColor whiteColor] CGColor];
    textField.layer.borderWidth = 1.0f;
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
    NSArray *firstAndLastNames = [[self.displayNameTextField.text componentsSeparatedByString:@" "] subarrayWithRange:wordRange];
    
 
    [self.contact setValue:(NSString *)[firstAndLastNames objectAtIndex:0] forKey:@"firstName"];
    [self.contact setValue:(NSString *)[firstAndLastNames objectAtIndex:1] forKey:@"lastName"];
    [self.contact setValue:self.emailTextField.text forKey:@"email"];
    [self.contact setValue:self.phoneTextField.text forKey:@"phone"];
    [self.contact setValue:self.companyTextField.text forKey:@"company"];
    [self.contact setValue:self.locationTextField.text forKey:@"location"];
    [self.contact setValue:self.titleTextField.text forKey:@"title"];
    [context save:&error];
        
   

}

-(IBAction)onCancel:(id)sender
{
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
    
    self.displayNameTextField.text =  [NSString stringWithFormat:@"%@ %@", self.contact.firstName, self.contact.lastName];
    self.emailTextField.text =  self.contact.email;
    self.phoneTextField.text = self.contact.phone;
    self.companyTextField.text = self.contact.company;
    self.titleTextField.text = self.contact.title;
    self.locationTextField.text = self.contact.location;
    
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
        [self.editProfile setTintColor:[UIColor blueColor]];
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
        [self.editProfile setTintColor:[UIColor greenColor]];
        [self updateButtonUI:self.cancel];
        self.cancel.hidden = NO;
        
    }
}
@end
