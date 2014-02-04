//
//  iWinMergeNoteViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 1/23/14.
//  Copyright (c) 2014 CSSE371. All rights reserved.
//

#import "iWinMergeNoteViewController.h"
#import "iWinBackEndUtility.h"

@interface iWinMergeNoteViewController ()
@property (nonatomic) iWinBackEndUtility *backendUtility;
@property (nonatomic) NSMutableArray *notesForTable;
@property (nonatomic) NSString *noteContent;
@property (nonatomic) NSInteger currentNoteID;
@end

@implementation iWinMergeNoteViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil noteContent:(NSString *)content userNames:(NSMutableArray *)names notes:(NSMutableArray *)notes noteID:(NSInteger)noteID
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    self.noteContent = content;
    self.notes = notes;
    self.names = names;
    self.currentNoteID = noteID;
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
    NSInteger i = indexPath.row;
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    // Configure the cell.
    if ([tableView isEqual:self.userListTable])
    {
        cell.textLabel.text = [self.names objectAtIndex:indexPath.row];
    }
    else if (self.notesForTable.count > 0) {
        cell.textLabel.text = [self.notesForTable objectAtIndex:indexPath.row];
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
    NSMutableArray *noteDictionaries = [self.notes objectAtIndex:indexPath.row];
    if ([tableView isEqual:self.userListTable])
    {
        self.notesForTable = [[NSMutableArray alloc]init];
        for (NSDictionary *d in noteDictionaries) {
            [self.notesForTable addObject:[d objectForKey:@"noteTitle"]];
        }
        [self.noteListTable reloadData];
    }
    // otherwise perform the merge
    else {
        
         NSInteger noteID = [[[noteDictionaries objectAtIndex:indexPath.row] objectForKey:@"noteID"] integerValue];
        
        // first get note content of the note to merge with
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Note/%d", noteID];
        NSDictionary *deserializedDictionary = [self.backendUtility getRequestForUrl:url];
        NSString *mergerNoteContent = [deserializedDictionary objectForKey:@"content"];
        
        // merge strings
        NSString *merged = [NSString stringWithFormat:@"%@\n\n-----\n\n%@",self.noteContent, mergerNoteContent];
        
    
        // next update the orignal note
        NSArray *keys = [NSArray arrayWithObjects:@"noteID", @"field", @"value", nil];
        NSArray *objects = [NSArray arrayWithObjects:[NSString stringWithFormat:@"%d", self.currentNoteID], @"content", merged, nil];
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        
        NSDictionary *deserializedDictionary2 = [self.backendUtility putRequestForUrl:@"http://csse371-04.csse.rose-hulman.edu/Note/" withDictionary:jsonDictionary];
        if (deserializedDictionary2) {
            [self.mergeNoteDelegate loadNoteIntoView];
            [self dismissViewControllerAnimated:YES completion:Nil];
        }
    }
}


@end
