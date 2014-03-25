//
//  iWinMergeNoteViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 1/23/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinMergeNoteViewController.h"
#import "iWinBackEndUtility.h"
#import "iWinConstants.h"

@interface iWinMergeNoteViewController ()
@property (nonatomic) iWinBackEndUtility *backendUtility;
@property (nonatomic) NSMutableArray *notesForTable;
@property (nonatomic) NSString *noteContent;
@property (nonatomic) NSInteger currentNoteID;
@property (nonatomic) NSInteger currentUserRowIndex;
@property (nonatomic) NSInteger userID;
@end

@implementation iWinMergeNoteViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil noteContent:(NSString *)content userNames:(NSMutableArray *)names notes:(NSMutableArray *)notes noteID:(NSInteger)noteID userID:(NSInteger)userID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    self.noteContent = content;
    self.notes = notes;
    self.names = names;
    self.currentNoteID = noteID;
    self.userID = userID;
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.notesForTable = [[NSMutableArray alloc] init];
    [self refreshNoteList];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)refreshNoteList
{
    [self.userListTable reloadData];
    [self.noteListTable reloadData];
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    NSInteger index = indexPath.row;
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    // Configure the cell.
    if ([tableView isEqual:self.userListTable])
    {
        cell.textLabel.text = [self.names objectAtIndex:index];
    }
    else if (self.notesForTable.count > 0) {
        cell.textLabel.text = [self.notesForTable objectAtIndex:index];
    }
    return cell;
}

- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if ([tableView isEqual:self.userListTable])
    {
        return self.names.count;
    }
    return self.notesForTable.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSMutableArray *noteDictionaries;
    
    if ([tableView isEqual:self.userListTable])
    {
        self.currentUserRowIndex = indexPath.row;
        noteDictionaries = [self.notes objectAtIndex:self.currentUserRowIndex];
        self.notesForTable = [[NSMutableArray alloc]init];
        for (NSDictionary *d in noteDictionaries) {
            [self.notesForTable addObject:[d objectForKey:@"noteTitle"]];
        }
        [self.noteListTable reloadData];
    }
    // otherwise perform the merge
    else {
        noteDictionaries = [self.notes objectAtIndex:self.currentUserRowIndex];
        NSInteger noteID = [[[noteDictionaries objectAtIndex:indexPath.row] objectForKey:@"noteID"] integerValue];
        
        // first get note content of the note to merge with
        NSString *url = [NSString stringWithFormat:@"%@/Note/%d", DATABASE_URL, noteID];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        NSString *mergerNoteContent = [deserializedDictionary objectForKey:@"content"];
        
        // merge strings
        NSString *merged = [NSString stringWithFormat:@"%@\n\n-----\n\n%@",self.noteContent, mergerNoteContent];
        
    
        // next update the orignal note
        NSArray *keys = [NSArray arrayWithObjects:@"noteID", @"field", @"value", nil];
        NSArray *objects = [NSArray arrayWithObjects:[NSString stringWithFormat:@"%d", self.currentNoteID], @"content", merged, nil];
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        
        NSDictionary *deserializedDictionary2 = [self.backendUtility putRequestForUrl:[NSString stringWithFormat: @"%@/Note/", DATABASE_URL] withDictionary:jsonDictionary];
        if (deserializedDictionary2) {
            [self.mergeNoteDelegate loadNoteIntoView];
            [self dismissViewControllerAnimated:YES completion:Nil];
        }
        
        // remove shared note from user
        NSString *unshareUrl = [NSString stringWithFormat:@"%@/Note/Sharing/%d/%d", DATABASE_URL, noteID, self.userID];
        [self.backendUtility deleteRequestForUrl:unshareUrl];

    }
}


@end
