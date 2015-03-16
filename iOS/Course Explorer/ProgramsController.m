//
//  ProgramsController.m
//  Course Explorer
//
//  Created by Ryan Wing on 3/8/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "Program.h"
#import "ProgramsController.h"
#import "XMLParser.h"

@interface ProgramsController ()

@end

@implementation ProgramsController

NSMutableData* responseData;
NSMutableArray* displayPrograms;
UIAlertView* alert;

- (void)viewDidLoad
{
   [super viewDidLoad];
   
   // Uncomment the following line to preserve selection between presentations.
   // self.clearsSelectionOnViewWillAppear = NO;
   
   // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
   // self.navigationItem.rightBarButtonItem = self.editButtonItem;
   
   if(responseData == nil)
   {
      responseData = [[NSMutableData alloc] init];
   }
   
   if(displayPrograms == nil)
   {
      displayPrograms = [[NSMutableArray alloc] init];
   }
   
   [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"ProgramCell"];
   alert = [[UIAlertView alloc] initWithTitle:@"Alert" message:@"Refreshing Display." delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
}

- (void)didReceiveMemoryWarning
{
   [super didReceiveMemoryWarning];
   // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear:(BOOL)animated
{
   if(displayPrograms.count == 0)
   {
      [self refresh];
   }
}

- (void)viewWillDisappear:(BOOL)animated
{
}

- (void)refresh
{
   Boolean result = true;
   NSURLConnection* connection = nil;
   
   [alert show];
   
   if(result)
   {
      NSURL* url = [NSURL URLWithString:@"http://regisscis.net/Regis2/webresources/regis2.program"];
      NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:url];
      
      [request setHTTPMethod:@"GET"];
      
      connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
      result = (connection != nil);
   }
   
   if(!result)
   {
      NSLog(@"Mutable URL Request Failed");
   }
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView*)tableView
{
   // Return the number of sections.
   return 1;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
   // Return the number of rows in the section.
   return displayPrograms.count;
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath*)indexPath
{
   UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"ProgramCell" forIndexPath:indexPath];
   
   // Configure the cell...
   Program* program = [displayPrograms objectAtIndex:indexPath.row];
   cell.textLabel.text = [program description];
   cell.textLabel.font = [UIFont systemFontOfSize:14];
   
   return cell;
}

// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView*)tableView canEditRowAtIndexPath:(NSIndexPath*)indexPath
{
   // Return NO if you do not want the specified item to be editable.
   return YES;
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
   return YES;
}

#pragma mark - Connection delegate responses go here

- (void)connection:(NSURLConnection*)connection didReceiveResponse:(NSURLResponse*)response
{
   responseData = [[NSMutableData alloc] init];
}

- (void)connection:(NSURLConnection*)connection didReceiveData:(NSData*)data
{
   NSString* bytes = [[NSString alloc] initWithBytes:data.bytes length:data.length encoding:nil];
   
   if(bytes != nil)
   {
      [responseData appendData:data];
   }
}

- (NSCachedURLResponse*)connection:(NSURLConnection*)connection willCacheResponse:(NSCachedURLResponse*)cachedResponse
{
   // return nil to indicate a cached response is not necessary
   return nil;
}

- (void)connectionDidFinishLoading:(NSURLConnection*)connection
{
   // Parse the response data (JSON, XML) in responseData
   NSUInteger length = [responseData length];
   
   if(length > 0)
   {
      NSDictionary* dictionary = [XMLParser dictionaryForXMLData:responseData];
      
      if(dictionary != nil)
      {
         NSDictionary* dictPrograms = dictionary[@"programs"];
         
         if(dictPrograms != nil)
         {
            NSArray* programs = dictPrograms.allValues[0];
            
            for(int i = 0; i < programs.count; ++i)
            {
               NSDictionary* program = programs[i];
               
               if(program != nil)
               {
                  NSString* programID = program[@"id"][@"text"];
                  NSString* programName = program[@"name"][@"text"];
                  
                  if(programID && programName)
                  {
                     NSNumberFormatter* formatter = [[NSNumberFormatter alloc] init];
                     formatter.numberStyle = NSNumberFormatterDecimalStyle;
                     
                     Program* newProgram = [[Program alloc] init];
                     
                     newProgram.id = [formatter numberFromString:programID];
                     newProgram.name = programName;
                     
                     [displayPrograms addObject:newProgram];
                  }
               }
            }
         }
      }
      
      self.navigationItem.title = [[NSString alloc] initWithFormat:@"%ld SCIS Programs", (long)displayPrograms.count];
      [self.tableView reloadData];
      
      if(displayPrograms.count > 0)
      {
         [self.tableView selectRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0] animated:NO scrollPosition:0];
      }
   }
   
   [alert dismissWithClickedButtonIndex:0 animated:YES];
}

- (void)connection:(NSURLConnection*)connection didFailWithError:(NSError*)error
{
   if(error != nil)
   {
      NSLog(@"Error: %@", [error description]);
   }
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue*)segue sender:(id)sender
{
   // Get the new view controller using [segue destinationViewController].
   // Pass the selected object to the new view controller.
}

@end
