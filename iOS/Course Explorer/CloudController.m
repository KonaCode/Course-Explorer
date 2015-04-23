//
//  CloudController.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/10/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "CloudController.h"

@interface CloudController ()

@end

@implementation CloudController

- (id)init
{
   if([super init])
   {
   }
   
   return self;
}

- (void)viewDidLoad
{
   [super viewDidLoad];

   // Do any additional setup after loading the view.
   NSUbiquitousKeyValueStore* store = [NSUbiquitousKeyValueStore defaultStore];

   [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(storeDidChange:) name:NSUbiquitousKeyValueStoreDidChangeExternallyNotification object:store];
   [[NSUbiquitousKeyValueStore defaultStore] synchronize];
   [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didAddNewNote:) name:@"New Note" object:nil];

   UIBarButtonItem* addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(insertNewObject:)];
   self.navigationItem.rightBarButtonItem = addButton;
   self.navigationItem.title = [[NSString alloc]initWithFormat:@"iCloud Notes"];
}

- (void)didReceiveMemoryWarning
{
   [super didReceiveMemoryWarning];
   // Dispose of any resources that can be recreated.
}

- (UIAlertView*)alert
{
   if(!_alert)
   {
      self.alert = [[UIAlertView alloc] initWithTitle:@"iCloud Notes" message:@"Error" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
   }
   
   return _alert;
}

- (NSMutableArray*)notes
{
   if(!_notes)
   {
      _notes = [[NSMutableArray alloc] init];
   }
   
   return _notes;
}

- (void)insertNewObject:(id)sender
{
   [self.alert show];
//   if (!self.objects)
//   {
//      self.objects = [[NSMutableArray alloc] init];
//   }
   
   // Create an empty contact and add it to the service
   // This will populate the members with default values
//   SmartList* smartList = [[SmartList alloc] init];
//   smartList.name = [NSString stringWithFormat:@"New List #%@", [[NSNumber alloc] initWithInt:(int)[listService count]+1]];
//   
//   [listService create:smartList];
   
   // Notify the rtable view to reload, causing it to re-fetch the contacts from the service
//   [self.tableView reloadData];
}

/*
- (void)scrollViewDidScroll:(UIScrollView*)scrollView
{
   CGRect frame = goToTop.frame;
   frame.origin.y = scrollView.contentOffset.y + self.tableView.frame.size.height - goToTop.frame.size.height;
   goToTop.frame = frame;
   
   [self.view bringSubviewToFront:goToTop];
}
*/

#pragma mark - iCloud Observers

- (void)didAddNewNote:(NSNotification*)notification
{
   NSDictionary* userInfo = [notification userInfo];
   NSString* noteStr = [userInfo valueForKey:@"Note"];
   [self.notes addObject:noteStr];
   
   // Update data on the iCloud
   [[NSUbiquitousKeyValueStore defaultStore] setArray:self.notes forKey:@"AVAILABLE_NOTES"];
   
   // Reload the table view to show changes
   [self.tableView reloadData];
}

- (void)storeDidChange:(NSNotification*)notification
{
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue*)segue sender:(id)sender
{
   // Get the new view controller using [segue destinationViewController].
   // Pass the selected object to the new view controller.
}

@end