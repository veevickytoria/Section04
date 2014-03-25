//
//  iWinViewAndAddNotesViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndAddNotesViewController.h"
#import "iWinBackEndUtility.h"
#import <QuartzCore/QuartzCore.h>
#import "Contact.h"
#import "iWinConstants.h"

@interface iWinViewAndAddNotesViewController ()
@property (nonatomic) NSInteger noteID;
@property (nonatomic) NSString *noteDate;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSMutableArray *usersSharingWithMe;
@property (nonatomic) BOOL inEditMode;
@property (nonatomic) iWinAddUsersViewController *userViewController;
@property (nonatomic) iWinMergeNoteViewController *mergeViewController;
@property (nonatomic) iWinBackEndUtility *backendUtility;
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
    self.usersSharingWithMe = [[NSMutableArray alloc] init];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
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
    
    NSString *url = [NSString stringWithFormat:@"%@/Note/%d", DATABASE_URL,self.noteID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Failure" message:@"Could not load your note" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
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
    NSString *url = [NSString stringWithFormat:@"%@/Note/%d", DATABASE_URL,self.noteID];
    NSError * error = [self.backendUtility deleteRequestForUrl:url];
    
    if (!error)
    {
        [self.addNoteDelegate refreshNoteList];
        [self dismissViewControllerAnimated:YES completion:Nil];
    }
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
    
    NSString *url = [NSString stringWithFormat:@"%@/Note/", DATABASE_URL];
    
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    
    if (!deserializedDictionary) {
        [self noteCreationAlert:@"Error" : @"Could not load your notes!"];
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
    
    NSString *url = [NSString stringWithFormat:@"%@/Note/", DATABASE_URL];
    
    NSDictionary *deserializedDictionary = [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    if (!deserializedDictionary) {
        [self noteCreationAlert:@"Error" : @"Could not update your note!"];
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


- (IBAction)saveSharedNoteUsers:(id)sender
{
    
}

- (IBAction)shareNotesButton:(id)sender {
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"ShareNotes" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}


- (IBAction)mergeNotesButton:(id)sender {
    NSMutableArray *names = [[NSMutableArray alloc] init];
    NSMutableArray *notes = [[NSMutableArray alloc] init];
    
    NSString *url = [NSString stringWithFormat:@"%@/User/Sharing/%d", DATABASE_URL,self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    // parse json into variables --> Array of dictionaries
    for (NSDictionary *sharingUser in deserializedDictionary ) {
        [self.usersSharingWithMe addObject:sharingUser];
        // then for each user - put there userName into table view
        [names addObject:[sharingUser objectForKey:@"userName"]];
        [notes addObject:[sharingUser objectForKey:@"notes"]];
    }
    
    // create merge notes controller
    self.mergeViewController = [[iWinMergeNoteViewController alloc] initWithNibName:@"iWinMergeNoteViewController" bundle:nil noteContent:self.noteField.text userNames:names notes:notes noteID:self.noteID userID:self.userID];
    [self.mergeViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.mergeViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    self.mergeViewController.mergeNoteDelegate = self;
    [self presentViewController:self.mergeViewController animated:YES completion:nil];
    self.mergeViewController.view.superview.bounds = CGRectMake(MODAL_XOFFSET, MODAL_YOFFSET, MODAL_WIDTH, MODAL_HEIGHT);
}


-(void)saveMergeClicked{
    [self dismissViewControllerAnimated:YES completion:Nil];
    
}
-(void)cancelMergeClicked
{
    [self dismissViewControllerAnimated:YES completion:Nil];
}

- (void)noteCreationAlert:(NSString*)title : (NSString*)message
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    [alert show];
}

-(void) selectedUsers:(NSMutableArray *)userList
{
    NSArray *keys = [NSArray arrayWithObjects:@"noteID", @"users", nil];
    NSMutableArray *users = [[NSMutableArray alloc] init];
    for (Contact *c in userList) {
        NSArray *uID = [NSArray arrayWithObjects:@"userID", nil];
        NSArray *uVal = [NSArray arrayWithObjects:[c.userID stringValue], nil];
        NSDictionary *u = [NSDictionary dictionaryWithObjects:uVal forKeys:uID];
        [users addObject:u];
    }
    
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.noteID] stringValue], users, nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility postRequestForUrl:[NSString stringWithFormat:@"%@/Note/Sharing/", DATABASE_URL] withDictionary: jsonDictionary];
}

@end
