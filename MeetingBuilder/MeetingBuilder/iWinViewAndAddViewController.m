//
//  iWinViewAndAddViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndAddViewController.h"

@interface iWinViewAndAddViewController ()
@property (nonatomic) NSMutableArray *itemList;
@property (nonatomic) BOOL isEditing;
@end

@implementation iWinViewAndAddViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil inEditMode:(BOOL)isEditing
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
        self.isEditing = isEditing;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.itemList = [[NSMutableArray alloc] init];
    
    self.headerLabel.text = @"Create Agenda";
    
    if (self.isEditing)
    {
        self.titleTextField.text = @"Agenda 101";
        [self.itemList addObject:@"Item 1"];
        [self.itemList addObject:@"Item 2"];
        [self.itemList addObject:@"Item 3"];
        self.headerLabel.text = @"View Agenda";
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"ItemCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"ItemCell"];
    }
    
    cell.textLabel.text = (NSString *)[self.itemList objectAtIndex:indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.itemList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
   // [self.meetingListDelegate meetingSelected];
}

- (IBAction)onClickSave
{
    //save agenda
    [self.agendaDelegate goToViewMeeting];
}

- (IBAction)onClickCancel
{
    [self.agendaDelegate goToViewMeeting];
}

- (IBAction)onClickAddItem
{
    
}

- (IBAction)onClickAddAttendees
{
    
}
@end
