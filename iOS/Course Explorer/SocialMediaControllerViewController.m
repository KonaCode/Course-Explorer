//
//  SocialMediaControllerViewController.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/6/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "SocialMediaControllerViewController.h"

@interface SocialMediaControllerViewController ()

@end

@implementation SocialMediaControllerViewController

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

- (void)targetedShare:(NSString*)serviceType
{
   if([SLComposeViewController isAvailableForServiceType:serviceType])
   {
      SLComposeViewController* shareView = [SLComposeViewController composeViewControllerForServiceType:serviceType];
      
      [shareView setInitialText:@"New Message"];
      [shareView addImage:[UIImage imageNamed:@"ExampleImage"]];
      [self presentViewController:shareView animated:YES completion:nil];
   }
   else
   {
      self.alert.message = @"You do not have this service";
      [self.alert show];
   }
}

- (IBAction)FacebookShare:(id)sender
{
   [self targetedShare:SLServiceTypeFacebook];
}

- (IBAction)TwitterShare:(id)sender
{
   [self targetedShare:SLServiceTypeTwitter];
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
   // Get the new view controller using [segue destinationViewController].
   // Pass the selected object to the new view controller.
}

@end
