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
@end

@implementation iWinMergeNoteViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.backendUtility = [[iWinBackEndUtility alloc] init];
    self.names = [[NSMutableArray alloc] init];
    self.notes = [[NSMutableArray alloc] init];
    [self refreshNoteList];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)refreshNoteList
{
  //  [self.userListTable reloadData];
 //   [self.noteListTable reloadData];
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
//    CustomSubtitledCell *cell = (CustomSubtitledCell *)[[tableView dequeueReusableCellWithIdentifier:@"AttendeeCell"];
//    if (cell == nil)
//    {
//        cell = [[CustomSubtitledCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"AttendeeCell"];
//    }
//    [cell initCell];
//    cell.subTitledDelegate = self;
//    
//    Contact *c = nil;
//    
//    if ([tableView isEqual:self.searchDisplayController.searchResultsTableView])
//    {
//        c = (Contact *)[self.filteredList objectAtIndex:indexPath.row];
//        cell.deleteButton.hidden = YES;
//    }
//    else
//    {
//        c = (Contact *)[self.attendeeList objectAtIndex:indexPath.row];
//        cell.deleteButton.hidden = NO;
//    }
//    cell.deleteButton.tag = indexPath.row;
//    cell.titleLabel.text =  c.name;
//    if (c.name.length == 0){
//        cell.titleLabel.text = c.email;
//    }
//    cell.detailLabel.text = c.email;
    return nil;
}

- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
//    if ([tableView isEqual:self.userListTable])
//    {
//        return self.us.count;
//    }
//    return self.attendeeList.count;
    return nil;
}


@end
