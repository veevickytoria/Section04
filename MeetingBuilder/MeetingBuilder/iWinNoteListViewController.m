//
//  iWinNoteListViewController.m
//  MeetingBuilder
//
//  Created by Richard Shomer on 10/29/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinNoteListViewController.h"
#import "RestKit/RestKit.h"
#import <QuartzCore/QuartzCore.h>

@interface iWinNoteListViewController ()

@property (strong, nonatomic) NSMutableArray *noteList;
@property (strong, nonatomic) NSMutableArray *noteDetail;
@property (strong, nonatomic) NSString* email;
@end


@implementation iWinNoteListViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withEmail:(NSString *) email
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.email = email;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.noteList = [[NSMutableArray alloc] init];
    self.noteDetail = [[NSMutableArray alloc] init];
    
    [self.noteList addObject:@"Notes for Meeting 1"];
    [self.noteDetail addObject:@"10/24/13 9:00 pm"];
    
    [self.noteList addObject:@"Notes for Meeting 2"];
    [self.noteDetail addObject:@"10/25/13 9:10 pm"];
    
    [self.noteList addObject:@"Notes for Meeting 3"];
    [self.noteDetail addObject:@"10/26/13 7:00 pm"];
    
    self.createNoteButton.layer.cornerRadius = 0;
    self.createNoteButton.layer.borderColor = [[UIColor darkGrayColor] CGColor];
    self.createNoteButton.layer.borderWidth = 1.0f;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(IBAction) onCreateNewNote
{
    //[self.noteListDelegate createNoteClicked:NO];
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"NoteCell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle reuseIdentifier:@"NoteCell"];
    }
    
    cell.textLabel.text = (NSString *)[self.noteList objectAtIndex:indexPath.row];
    cell.detailTextLabel.text = (NSString *)[self.noteDetail objectAtIndex:indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.noteList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //[self.noteListDelegate createNoteClicked:YES];
}

@end
