//
//  iWinNoteListViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinNoteListViewController.h"
#import "iWinViewAndAddNotesViewController.h"
#import "iWinBackEndUtility.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinNoteListViewController ()

@property (strong, nonatomic) NSMutableArray *noteList;
@property (strong, nonatomic) NSMutableArray *noteIDs;
@property (nonatomic) NSInteger userID;
@property (nonatomic) NSInteger selectedNote;
@property (nonatomic) iWinBackEndUtility *backendUtility;
@property (strong, nonatomic) iWinViewAndAddNotesViewController *createNoteVC;
@end


@implementation iWinNoteListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withUserID:(NSInteger) userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.userID = userID;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    [self refreshNoteList];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)refreshNoteList
{
    self.noteList = [[NSMutableArray alloc] init];
    self.noteIDs = [[NSMutableArray alloc] init];
    
    //populate note list
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/User/Notes/%d", self.userID];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
    
    if (!deserializedDictionary) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Failure" message:@"Could not load your notes" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
        [alert show];
    }
    else
    {
        NSArray *jsonArray = [deserializedDictionary objectForKey:@"notes"];
        if (jsonArray.count > 0)
        {
            for (NSDictionary* note in jsonArray)
            {
                [self.noteList addObject:[note objectForKey:@"noteTitle"]];
                [self.noteIDs addObject:[note objectForKey:@"noteID"]];
            }
        }
        [self.noteTable reloadData];
    }
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        //add code here for when you hit delete
        self.selectedNote = indexPath.row;
        UIAlertView *deleteAlertView = [[UIAlertView alloc] initWithTitle:@"Confirm Delete" message:@"Are you sure you want to delete this note?" delegate:self cancelButtonTitle:@"No, just kidding!" otherButtonTitles:@"Yes, please", nil];
        [deleteAlertView show];
    }
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        [self deleteNote];
        self.selectedNote = -1;
    }
}

-(void)deleteNote
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Note/%d", [[self.noteIDs objectAtIndex:self.selectedNote] integerValue]];
    NSError * error = [self.backendUtility deleteRequestForUrl:url];
    
    if (!error)
        [self refreshNoteList];
}

-(IBAction) onCreateNewNote
{
    [self enterNoteEditCreationView:-1];
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"NoteCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"NoteCell"];
    }
    
    cell.textLabel.text = (NSString *)[self.noteList objectAtIndex:indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.noteList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [self enterNoteEditCreationView:[[self.noteIDs objectAtIndex:indexPath.row] integerValue]];
}

-(void)enterNoteEditCreationView:(NSInteger)noteID {
    self.createNoteVC = [[iWinViewAndAddNotesViewController alloc] initWithNibName:@"iWinViewAndAddNotesViewController" bundle:nil withNoteID:noteID withUserID:self.userID];
    [self.createNoteVC setModalPresentationStyle:UIModalPresentationPageSheet];
    self.createNoteVC.addNoteDelegate = self;
    [self.createNoteVC setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    [self presentViewController:self.createNoteVC animated:YES completion:nil];
    self.createNoteVC.view.superview.bounds = CGRectMake(0,0,768,1003);
}

@end
