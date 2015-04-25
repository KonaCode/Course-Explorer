//
//  ChatController.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/22/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "ChatController.h"

@interface ChatController ()

@end

@implementation ChatController

- (UIAlertView*)alert
{
   if(!_alert)
   {
      self.alert = [[UIAlertView alloc] initWithTitle:@"Alert" message:@"Working" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
   }
   
   return _alert;
}

- (SocketService*)service
{
   if(!_service)
   {
      _service = [[SocketService alloc] initWithDelegate:self];
      [_service connect];
   }
   
   return _service;
}

- (NSString*)name
{
   if(!_name)
   {
      _name = [[NSString alloc] initWithFormat:@"<Unknown User>"];
   }
   
   return _name;
}

- (void)viewDidLoad
{
   [super viewDidLoad];

   // Do any additional setup after loading the view.
   [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"ChatMessageCell"];
   [self.tableView setDataSource:self];
}

- (void)didReceiveMemoryWarning
{
   [super didReceiveMemoryWarning];

   // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
   [super viewWillAppear:animated];
   
   [self.service connect];
   [self.service send:[[NSString alloc] initWithFormat:@"%@ has joined", self.name]];
   [self.tableView reloadData];
}

- (void)viewWillDisappear:(BOOL)animated
{
   [super viewWillDisappear:animated];
   
   [self.service disconnect];
}

- (IBAction)sendMessage:(id)sender
{
   NSString* message = [self.messageEdit text];
   
   if([message length] == 0)
   {
      self.alert.message = @"The message is empty!";
      [self.alert show];
   }
   else
   {
      [self.service send:[[NSString alloc] initWithFormat:@"%@: %@", self.name, message]];
      [self.tableView reloadData];
      [self.messageEdit setText:@""];
   }
}

- (void)socketService:(SocketService*)service didReceive:(NSString*)message
{
   [self.tableView reloadData];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView*)tableView
{
   NSInteger result = 1;
   
   // Return the number of sections.
   return result;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
   NSInteger result = [self.service.messages count];
   
   // Return the number of rows in the section.
   return result;
}

- (NSString*)tableView:(UITableView*)tableView titleForHeaderInSection:(NSInteger)section
{
   NSString* result = [[NSString alloc] initWithFormat:@"Chat Messages"];
   
   return result;
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath*)indexPath
{
   // Configure the cell...
   UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"ChatMessageCell" forIndexPath:indexPath];
   NSString* message = self.service.messages[indexPath.row];
   
   cell.textLabel.text = message;
   cell.textLabel.font = [UIFont systemFontOfSize:14];
   cell.textLabel.numberOfLines = 0;
   cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
   
   return cell;
}

// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView*)tableView canEditRowAtIndexPath:(NSIndexPath*)indexPath
{
   // Return NO if you do not want the specified item to be editable.
   return NO;
}

// Override to support editing the table view.
- (void)tableView:(UITableView*)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath*)indexPath
{
   if (editingStyle == UITableViewCellEditingStyleDelete)
   {
      // Delete the row from the data source
      [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
   }
   else if (editingStyle == UITableViewCellEditingStyleInsert)
   {
      // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
   }
}

// Override to support rearranging the table view.
- (void)tableView:(UITableView*)tableView moveRowAtIndexPath:(NSIndexPath*)fromIndexPath toIndexPath:(NSIndexPath*)toIndexPath
{
}

// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView*)tableView canMoveRowAtIndexPath:(NSIndexPath*)indexPath
{
   // Return NO if you do not want the item to be re-orderable.
   return NO;
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
   // Get the new view controller using [segue destinationViewController].
   // Pass the selected object to the new view controller.
}

@end
