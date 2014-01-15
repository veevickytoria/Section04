//
//  iWinViewAndChangeSettingsViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 11/4/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndChangeSettingsViewController.h"
#import "iWinAppDelegate.h"
#import "Contact.h"
#import "Settings.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewAndChangeSettingsViewController ()
@property (nonatomic) BOOL isEditing;
@property (nonatomic) Contact *contact;
@property (nonatomic) Settings *settings;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSArray *options;
@property (nonatomic) UIAlertView *deleteAlertView;
@end


@implementation iWinViewAndChangeSettingsViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withID:(NSInteger) userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.userID = userID;
    }
    return self;
}

-(IBAction)changeSwitch:(id)sender
{
    
}

-(NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

-(NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return [self.options count];
}

-(NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [self.options objectAtIndex:row];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.cancelButton.hidden = YES;
    [self showFields:NO];
    [self enableInteraction:NO];
    self.options = [[NSArray alloc] initWithObjects:@"At time of event",@"5 minutes before",@"15 minutes before", @"30 minutes before", @"1 hour before", @"2 hours before", @"1 day before",@"2 days before", nil];
 //   self.whenToNotifyPicker.
 //   self.whenToNotifyPicker.numberOfComponents = 5;
 //   self.whenToNotifyPicker.
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
    
    
    
    NSEntityDescription *entityDesc1 = [NSEntityDescription entityForName:@"Settings" inManagedObjectContext:context];
    
    NSFetchRequest *request1 = [[NSFetchRequest alloc] init];
    [request1 setEntity:entityDesc1];
    
    NSPredicate *predicate1 = [NSPredicate predicateWithFormat:@"userID = 1"];
    [request1 setPredicate:predicate1];
    
    NSError *error1;
    NSArray *result1 = [context executeFetchRequest:request1
                                             error:&error1];
    self.settings = (Settings*)[result1 objectAtIndex:0];
    //
    //
    //
    
    self.emailTextField.text = self.contact.email;
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)onCancel:(id)sender
{
    [self.saveAndEditButton setTintColor:[UIColor blueColor]];
    [self showFields:NO];
    [self clearFields];
    [self enableInteraction:NO];
    self.isEditing = NO;
    self.oldPasswordTextField.text = @"********";
    [self.saveAndEditButton setTitle:@"Change Email/Password" forState:UIControlStateNormal];
    self.cancelButton.hidden = YES;
    //Pull Email from DB
}

-(IBAction)onDelete:(id)sender
{
    self.deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this contact?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
    [self.deleteAlertView show];
}


-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
            //Perform deletion
    }
   
}

-(IBAction)onEdit:(id)sender
{
    if (self.isEditing) {
        ///Saving the info
        
        
        if ([self.oldPasswordTextField.text isEqual:self.settings.password] && [self.passwordTextField.text isEqual:self.confirmPasswordTextField.text]) {
          
            [self showFields:NO];
            [self.saveAndEditButton setTitle:@"Change Email/Password" forState:UIControlStateNormal];
            [self.saveAndEditButton setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
            [self saveChanges];
            [self enableInteraction:NO];
            self.cancelButton.hidden = YES;
            self.isEditing = NO;
        } else {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Old password or confirmation password did not match." delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
            [self clearFields];
        }
        
    } else{
        self.isEditing = YES;
        [self showFields:YES];
        [self clearFields];
        [self.saveAndEditButton setTitle:@"Save" forState:UIControlStateNormal];
        [self.saveAndEditButton setTitleColor:[UIColor greenColor] forState:UIControlStateNormal];
        self.cancelButton.hidden = NO;
        [self enableInteraction:YES];
    }
}
    
-(void) saveChanges
{
    //Push new password and email to DB only if old password matches.
    //You must enter old password for any change to take affect.
    self.contact.email = self.emailTextField.text;
    self.settings.password = self.confirmPasswordTextField.text;
    self.settings.password = self.confirmPasswordTextField.text;
    
}

-(void) showFields: (BOOL) show
{
    if(show) {
        self.oldPasswordTextField.text = @"";
        self.oldPasswordLabel.text = @"Old Password:";
        self.confirmPasswordTextField.hidden = NO;
        self.passwordTextField.hidden = NO;
        //self.passwordLabel.text = @"Password";
        //self.oldPasswordTextField.hidden = NO;
        self.confirmPasswordLabel.hidden = NO;
        self.oldPasswordLabel.hidden = NO;
        self.passwordLabel.hidden = NO;
        
    } else{
        self.oldPasswordTextField.text = @"********";
        self.oldPasswordLabel.text = @"Password:";
        self.confirmPasswordTextField.hidden = YES;
        self.passwordTextField.hidden = YES;
       // self.oldPasswordTextField.hidden = YES;
        self.confirmPasswordLabel.hidden = YES;
       // self.oldPasswordLabel.hidden = YES;
        self.passwordLabel.hidden = YES;
    }
    
}

-(void) enableInteraction: (BOOL) enable
{
    if (enable){
        self.emailTextField.userInteractionEnabled = YES;
        self.confirmPasswordTextField.userInteractionEnabled = YES;
        self.passwordTextField.userInteractionEnabled = YES;
        self.oldPasswordTextField.userInteractionEnabled = YES;
        [self.emailTextField setBorderStyle:UITextBorderStyleRoundedRect];
        [self.oldPasswordTextField setBorderStyle:UITextBorderStyleRoundedRect];
    }else{
        self.emailTextField.userInteractionEnabled = NO;
        self.confirmPasswordTextField.userInteractionEnabled = NO;
        self.passwordTextField.userInteractionEnabled = NO;
        self.oldPasswordTextField.userInteractionEnabled = NO;
        [self.emailTextField setBorderStyle:UITextBorderStyleNone];
        [self.oldPasswordTextField setBorderStyle:UITextBorderStyleNone];


    }
}

- (void) clearFields
{
    self.oldPasswordTextField.text = @"";
    self.passwordTextField.text = @"";
    self.confirmPasswordTextField.text = @"";
}

@end
