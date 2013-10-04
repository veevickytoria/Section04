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
@property (strong, nonatomic) NSString* email;
@end

@implementation iWinProjectViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil withEmail:(NSString *)email
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
    // Do any additional setup after loading the view from its nib.
    //self.projectList = [[NSMutableArray alloc] init];
    
    //load projects
    NSString *url = [NSString stringWithFormat:@"http://localhost:8888/db_api.php?action=read&table=Project&email=%@", self.email];
    url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    
    NSData *dataURL = [NSData dataWithContentsOfURL:[NSURL URLWithString:url]];
    NSString *strResult = [[NSString alloc] initWithData:dataURL encoding:NSUTF8StringEncoding];
    
    self.projectList = [[NSMutableArray alloc] initWithArray:[strResult componentsSeparatedByString:@"___"]];
    
    /*NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
    NSURLResponse * response = nil;
    NSError * error = nil;
    NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                          returningResponse:&response
                                                      error:&error];
    //self.projectList = [NSKeyedUnarchiver unarchiveObjectWithData:self.responseData];
    NSPropertyListFormat format;
    NSArray *array = [NSPropertyListSerialization propertyListFromData:data
                                                      mutabilityOption:NSPropertyListMutableContainers
                                                                format:&format
                                                      errorDescription:NULL];
    self.projectList =  [[NSMutableArray alloc] initWithArray:array];*/
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
            //add project to db.
            NSString *url = [NSString stringWithFormat:@"http://localhost:8888/db_api.php?action=write&table=Project&email=%@&name=%@", self.email, projectName];
            url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
            NSURLRequest * urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
            NSURLResponse * response = nil;
            NSError * error = nil;
            NSData * data = [NSURLConnection sendSynchronousRequest:urlRequest
                                                  returningResponse:&response
                                                              error:&error];
        }
        NSString *url = [NSString stringWithFormat:@"http://localhost:8888/db_api.php?action=read&table=Project&email=%@", self.email];
        url = [url stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSData *dataURL = [NSData dataWithContentsOfURL:[NSURL URLWithString:url]];
        NSString *strResult = [[NSString alloc] initWithData:dataURL encoding:NSUTF8StringEncoding];
        
        self.projectList = [[NSMutableArray alloc] initWithArray:[strResult componentsSeparatedByString:@"___"]];
        //self.projectList = [NSKeyedUnarchiver unarchiveObjectWithData:self.responseData];
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
