//
//  iWinViewAndAddNotesViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndAddNotesViewController.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinViewAndAddNotesViewController ()
@property (nonatomic) NSInteger noteID;
@property (nonatomic) NSString *noteDate;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSArray *sharedUserIDs;
@property (nonatomic) BOOL inEditMode;
@property (nonatomic) iWinAddUsersViewController *userViewController;

@end

@implementation iWinViewAndAddNotesViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withNoteID:(NSInteger)noteID withUserID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    self.userID = userID;
    if (self) {
        // Custom initialization
        self.noteID = noteID;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.noteField.layer.borderColor = [[UIColor blackColor] CGColor];
    self.noteField.layer.borderWidth = 0.7f;
    self.noteField.layer.cornerRadius = 15.0f;
    self.inEditMode = YES;
    self.deleteButton.hidden = YES;
    
    
    // change page according to wether or not it is an existing note
    // if existing note - change out of edit mode
    if (self.noteID != -1) {
        [self toggleEditModes];
        [self loadNoteIntoView];
    }
}

-(void)toggleEditModes
{
    if (self.inEditMode) {
        [self.saveButton setTitle:@"Edit" forState:UIControlStateNormal];
        [self.saveButton setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        self.titleField.userInteractionEnabled = NO;
        self.noteField.userInteractionEnabled = NO;
        [self.titleField setBorderStyle:UITextBorderStyleNone];
        self.noteField.layer.borderWidth = 0.0f;
        self.deleteButton.hidden = YES;
        self.inEditMode = NO;
    }
    else {
        [self.saveButton setTitle:@"Save" forState:UIControlStateNormal];
        [self.saveButton setTitleColor:[UIColor greenColor] forState:UIControlStateNormal];
        self.titleField.userInteractionEnabled = YES;
        self.noteField.userInteractionEnabled = YES;
        [self.titleField setBorderStyle:UITextBorderStyleRoundedRect];
        self.noteField.layer.borderWidth = 0.7f;
        if (self.noteID == -1) {
            self.deleteButton.hidden = YES;
        }
        else {
            self.deleteButton.hidden = NO;
        }
        
        self.inEditMode = YES;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void)loadNoteIntoView
{
    // retreive notes from db
    
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Note/%d", self.noteID];
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data =[NSURLConnection sendSynchronousRequest:urlRequest
                                         returningResponse:&response
                                                     error:&error];
    if (error) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Failure" message:@"Could not load your note" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSError *jsonParsingError = nil;
        NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingAllowFragments|NSJSONReadingMutableContainers error:&jsonParsingError];
        
        self.titleField.text = [deserializedDictionary objectForKey:@"title"];
        self.noteField.text = [deserializedDictionary objectForKey:@"content"];
    }
}


-(IBAction)saveButton:(id)sender
{
    if (self.inEditMode) {
        // if new note
        if (self.noteID == -1) {
            // save note from scratch
            [self saveNote];
        }
        else {
            // save note for update
            NSString *title = [[self.titleField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
            NSString *content = [[self.noteField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
            [self updateNote:NO : @"title" : title];
            [self updateNote:YES : @"content" : content];
        }
    }
    else {
        [self toggleEditModes];
    }
}

// Override to support editing the table view.
- (IBAction)confirmDeleteAlert
{
        UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this note?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
        [deleteAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        [self deleteNote];
        [self.addNoteDelegate refreshNoteList];
    }
}

-(void)deleteNote
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Note/%d", self.noteID];
    
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [urlRequest setHTTPMethod:@"DELETE"];
    NSURLResponse * response = nil;
    NSError * error = nil;
    [NSURLConnection sendSynchronousRequest:urlRequest
                          returningResponse:&response
                                      error:&error];

    [self.addNoteDelegate refreshNoteList];
    [self dismissViewControllerAnimated:YES completion:Nil];
}

- (void)saveNote
{

    NSString *title = [[self.titleField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    NSString *content = [[self.noteField text] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
    
    NSDateFormatter* df = [[NSDateFormatter alloc]init];
    [df setDateFormat:@"MM/dd/yyyy hh:mm a"];
    NSString *date = [df stringFromDate:[NSDate date]];
    
    //create note in database
    NSArray *keys = [NSArray arrayWithObjects:@"createdBy", @"title", @"description", @"content", @"dateCreated", nil];
    NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.userID], title, @"HOLDER", content, date, nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSData *jsonData;
    NSString *jsonString;
    
    
    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
    {
        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Note/"];
    
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
    if (error) {
        [self noteCreationAlert:YES];
    }
    else
    {
        [self.addNoteDelegate refreshNoteList];
        [self dismissViewControllerAnimated:YES completion:Nil];
    }

}


- (void)updateNote:(BOOL)returnAndRefresh : (NSString*)field : (NSString*)value
{
    
    //create note in database
    NSArray *keys = [NSArray arrayWithObjects:@"noteID", @"field", @"value", nil];
    NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:self.noteID], field, value, nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSData *jsonData;
    NSString *jsonString;
    
    
    if ([NSJSONSerialization isValidJSONObject:jsonDictionary])
    {
        jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:0 error:nil];
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Note/"];
    
    NSMutableURLRequest * urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
    [urlRequest setHTTPMethod:@"PUT"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [urlRequest setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [urlRequest setValue:[NSString stringWithFormat:@"%d", [jsonData length]] forHTTPHeaderField:@"Content-length"];
    [urlRequest setHTTPBody:jsonData];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data =[NSURLConnection sendSynchronousRequest:urlRequest
                                         returningResponse:&response
                                                     error:&error];
    if (error) {
        [self noteCreationAlert:YES];
    }
    else if (returnAndRefresh)
    {
        [self.addNoteDelegate refreshNoteList];
        [self dismissViewControllerAnimated:YES completion:Nil];
    }
    
}

- (IBAction)cancelButton:(id)sender
{
      [self dismissViewControllerAnimated:YES completion:Nil];
}

- (void)loadSharedUsersIntoTable
{
    NSArray *names = [NSArray arrayWithObjects:@"John McCormack", @"Dharmin Shah", @"Gordon Hazzard", nil];
    self.sharedUserIDs = [NSArray arrayWithObjects:@"1", @"2", @"3", nil];
    
    // TO DO: parse JSON and set fille names and sharedUserIDs arrays
    
    // load names into list
    for (NSString *name in names) {
  //      self.userViewController.userListTableView
        
    //    [self.noteList addObject:];
     //   [self.noteIDs addObject:];
    }
}


- (IBAction)saveSharedNoteUsers:(id)sender
{
    
}

- (IBAction)shareNotesButton:(id)sender {
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"ShareNotes" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}


- (IBAction)mergeNotesButton:(id)sender {
    
}


-(void)saveMergeClicked{
    [self dismissViewControllerAnimated:YES completion:Nil];

}
-(void)cancelMergeClicked
{
    [self dismissViewControllerAnimated:YES completion:Nil];
}

- (void)noteCreationAlert:(BOOL)error
{
    NSString *title;
    NSString *message;
    if (error) {
        title = @"Error";
        message = @"Failed to save note";
    }
    else
    {
        title = @"Success";
        message = @"Note has been saved";
    }
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    [alert show];
}

@end
