//
//  iWinProjectViewController.m
//  MeetingBuilder
//
//  Created by CSSE Department on 10/2/13.
//  Copyright (c) 2013 CSSE371. All rights reserved.
//

#import "iWinProjectViewController.h"
#import "RestKit/RestKit.h"

@interface iWinProjectViewController ()
@property (strong, nonatomic) NSMutableArray *projectList;
@end

@implementation iWinProjectViewController

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
    self.projectList = [[NSMutableArray alloc] init];

}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)onAddNewProject:(id)sender
{
    UIAlertView *projectAlertView = [[UIAlertView alloc] initWithTitle:@"New Project" message:@"Enter Project Name" delegate:self cancelButtonTitle:@"Cancel" otherButtonTitles:@"Ok", nil];
    [projectAlertView setAlertViewStyle:UIAlertViewStylePlainTextInput];
    [projectAlertView show];
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == 1)
    {
        NSString *projectName = [alertView textFieldAtIndex:0].text;
        projectName = [projectName stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]];
        if (projectName.length > 0)
        {
            [self.projectList addObject:projectName];
        }
        [self.projectTable reloadData];
    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"Cell"];
    if (cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"Cell"];
    }
    cell.textLabel.text = self.projectList[indexPath.row];
    return cell;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.projectList.count;
}

@end
