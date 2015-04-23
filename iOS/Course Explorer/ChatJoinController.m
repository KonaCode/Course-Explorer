//
//  ChatJoinController.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/22/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "ChatJoinController.h"

@interface ChatJoinController ()

@end

@implementation ChatJoinController

- (void)viewDidLoad
{
   [super viewDidLoad];
   // Do any additional setup after loading the view.
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
      self.alert = [[UIAlertView alloc] initWithTitle:@"Alert" message:@"Working" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
   }
   
   return _alert;
}

- (IBAction)exitHere:(UIStoryboardSegue*)sender
{
}

#pragma mark - Navigation

- (BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier sender:(id)sender
{
   BOOL result = YES;
   
   if([identifier isEqualToString:@"joinChat"])
   {
      NSString* name = [self.nameEdit text];
      
      result = ([name length] > 0);
      
      if(!result)
      {
         self.alert.message = @"You must enter a valid name!";
         [self.alert show];
      }
   }

   return result;
}

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue*)segue sender:(id)sender
{
   // Get the new view controller using [segue destinationViewController].
   // Pass the selected object to the new view controller.
}

@end
