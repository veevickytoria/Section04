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
@end

@implementation iWinMergeNoteViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil noteContent:(NSString *)content userNames:(NSMutableArray *)names notes:(NSMutableArray *)notes
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    self.noteContent = content;
    self.notes = notes;
    self.names = names;
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
    return self.notes.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSMutableArray *noteDictionaries = [self.notes objectAtIndex:indexPath.row];
    if ([tableView isEqual:self.userListTable])
    {
        for (NSDictionary *d in noteDictionaries) {
            [self.notesForTable addObject:[d objectForKey:@"noteTitle"]];
        }
        [self refreshNoteList];
    }
    // otherwise perform the merge
    else {
        NSInteger noteID = [[[noteDictionaries objectAtIndex:indexPath.row] objectForKey:@"noteID"] integerValue];
        NSArray *keys = [NSArray arrayWithObjects:@"noteID", @"field", @"value", nil];
        NSArray *objects = [NSArray arrayWithObjects:[NSNumber numberWithInt:noteID], @"content", self.noteContent, nil];
        NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
        
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Note/"];
        
        NSDictionary *deserializedDictionary = [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
        
        if (deserializedDictionary) {
            [self dismissViewControllerAnimated:YES completion:Nil];
        }
    }
}


@end
