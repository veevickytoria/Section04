//
//  iWinViewAndAddViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/24/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinViewAndAddViewController.h"
#import "iWinAddUsersViewController.h"
#import <QuartzCore/QuartzCore.h>
#import "iWinBackEndUtility.h"

@interface iWinViewAndAddViewController ()
@property (nonatomic) NSMutableArray *itemList;
@property (nonatomic) BOOL isEditing;
@property (nonatomic) iWinAgendaItemViewController *agendaItemViewController;
@property (nonatomic) iWinAddUsersViewController *userViewController;
@property (nonatomic) NSInteger agendaID;
@property (strong, nonatomic) iWinBackEndUtility *backendUtility;

@end


@implementation iWinViewAndAddViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
//        self.isEditing = isEditing;
        self.agendaID = 0;
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    if (!self.isAgendaCreated) {
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
    } else {
        NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Agenda/%d", self.agendaID];
        NSMutableURLRequest *urlRequest = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalAndRemoteCacheData timeoutInterval:30];
        [urlRequest setHTTPMethod:@"GET"];
        NSURLResponse * response = nil;
        NSError * error = nil;
        NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                              returningResponse:&response
                                                          error:&error];
        NSArray *jsonArray;
        if (error)
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Meetings not found" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
            [alert show];
        }
        else
        {
            NSError *jsonParsingError = nil;
            NSDictionary *deserializedDictionary = (NSDictionary *)[NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers|NSJSONReadingAllowFragments error:&jsonParsingError];
            jsonArray = [deserializedDictionary objectForKey:@"title"];
        }

        
        
        
        
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
    
    NSString *agendaItemName = [[self.itemList objectAtIndex:indexPath.row] objectForKey:@"title"];
    cell.textLabel.text = agendaItemName;
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.itemList.count;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    //This is where edit on a row happen: indexPath.row
    
    NSDictionary *agendaItem = [self.itemList objectAtIndex:indexPath.row];
    NSString *agendaItemName = [agendaItem objectForKey:@"title"];
    NSString *agendaItemDuration = [agendaItem objectForKey:@"duration"];
    NSString *agendaItemDescription = [agendaItem objectForKey:@"description"];

    
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    self.agendaItemViewController = [[iWinAgendaItemViewController alloc]
                                     initWithNibName:@"iWinAgendaItemViewController" bundle:nil];
    self.agendaItemViewController.itemTitle = agendaItemName;
    self.agendaItemViewController.itemDuration = agendaItemDuration;
    self.agendaItemViewController.itemDescription = agendaItemDescription;

    self.agendaItemViewController.itemIndex = indexPath.row;
    
    
    self.agendaItemViewController.itemDelegate = self;
    [self.agendaItemViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.agendaItemViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.agendaItemViewController animated:YES completion:nil];
    self.agendaItemViewController.view.superview.bounds = CGRectMake(0,0,556,283);
    
}

- (IBAction)onClickSave
{
    if (self.agendaID == 0)
    {
        [self saveNewMeeting];
    }
    else
    {
        [self updateMeetingInfo];
    }

}

-(void) updateMeetingInfo
{
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Agenda/"];
    
    NSArray *keys = [NSArray arrayWithObjects:@"agendaID", @"field", @"value", nil];
    NSArray *objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.agendaID] stringValue], @"title", self.titleTextField.text, nil];
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.agendaID] stringValue], @"meeting", [[NSNumber numberWithInt:self.meetingID] stringValue], nil];
    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
    
    objects = [NSArray arrayWithObjects:[[NSNumber numberWithInt:self.agendaID] stringValue], @"content", self.itemList, nil];    jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    [self.backendUtility putRequestForUrl:url withDictionary:jsonDictionary];
}


-(void) saveNewMeeting
{
    NSArray *keys = [NSArray arrayWithObjects:@"title", @"meeting", @"content",nil];
    NSArray *objects = [NSArray arrayWithObjects: self.titleTextField.text, [[NSNumber numberWithInt:self.meetingID] stringValue], self.itemList, nil];
    
    NSDictionary *jsonDictionary = [NSDictionary dictionaryWithObjects:objects forKeys:keys];
    NSString *url = [NSString stringWithFormat:@"http://csse371-04.csse.rose-hulman.edu/Agenda/"];
    NSDictionary *deserializedDictionary = [self.backendUtility postRequestForUrl:url withDictionary:jsonDictionary];
    self.agendaID = [[deserializedDictionary objectForKey:@"agendaID"] integerValue];
}



- (IBAction)onClickCancel
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)onClickAddItem
{
    self.agendaItemViewController = [[iWinAgendaItemViewController alloc] initWithNibName:@"iWinAgendaItemViewController" bundle:nil];
    self.agendaItemViewController.itemDelegate = self;
    [self.agendaItemViewController setModalPresentationStyle:UIModalPresentationFormSheet];
    [self.agendaItemViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
    
    [self presentViewController:self.agendaItemViewController animated:YES completion:nil];
    self.agendaItemViewController.view.superview.bounds = CGRectMake(0,0,556,283);
    
}

- (IBAction)onClickAddAttendees
{
    self.userViewController = [[iWinAddUsersViewController alloc] initWithNibName:@"iWinAddUsersViewController" bundle:nil withPageName:@"Agenda" inEditMode:self.isEditing];
    [self.userViewController setModalPresentationStyle:UIModalPresentationPageSheet];
    [self.userViewController setModalTransitionStyle:UIModalTransitionStyleCoverVertical];
  //  self.userViewController.userDelegate = self;
    [self presentViewController:self.userViewController animated:YES completion:nil];
    self.userViewController.view.superview.bounds = CGRectMake(0,0,768,1003);
}

-(void)saveItem:(NSString *)name duration: (NSString*) duration description:(NSString*)
description itemIndex: (NSInteger *) itemIndex
{
    //TODO: PUT DESCRIPTION BACK!!!
    if((NSInteger)self.agendaItemViewController.itemIndex > -1){
        NSDictionary *agendaItem = @{@"title" : name, @"duration": duration, @"description": description};
        [self.itemList replaceObjectAtIndex:self.agendaItemViewController.itemIndex withObject:agendaItem];
    }
    
    else{
        NSDictionary *agendaItem = @{@"title" : name, @"duration": duration, @"description": description};
    [self.itemList addObject:agendaItem];
    }
    
    [self.itemTableView reloadData];
    [self dismissViewControllerAnimated:YES completion:Nil];
}

-(void) cancel
{
    [self dismissViewControllerAnimated:YES completion:Nil];
}


@end
