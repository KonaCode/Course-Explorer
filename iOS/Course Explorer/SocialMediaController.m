//
//  SocialMediaControllerViewController.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/6/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "SocialMediaController.h"

@interface SocialMediaController ()

@end

@implementation SocialMediaController

Boolean hasValidLogin;

- (id)init
{
   if([super init])
   {
      hasValidLogin = true;
   }
   
   return self;
}

- (void)viewDidLoad
{
   [super viewDidLoad];

   // Do any additional setup after loading the view.
   self.hasFacebookLogin = true;
   self.hasTwitterLogin = true;
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

- (IBAction) exitHere:(UIStoryboardSegue*)sender
{
}

- (void)targetedShare:(NSString*)serviceType
{
   Boolean result = hasValidLogin;
   
   if(!result)
   {
      [self performSegueWithIdentifier:@"loginSocialSegue" sender:self];
      return;
   }

   if(result)
   {
      result = [SLComposeViewController isAvailableForServiceType:serviceType];
   }
   
   if(result)
   {
      SLComposeViewController* shareView = [SLComposeViewController composeViewControllerForServiceType:serviceType];
      
      [shareView setInitialText:@"New Message"];
      [shareView addImage:[UIImage imageNamed:@"ExampleImage"]];
      [self presentViewController:shareView animated:YES completion:nil];
   }
   
   if(!result)
   {
      self.alert.message = @"You do not have this service";
      [self.alert show];
   }
}

- (IBAction)FacebookShare:(id)sender
{
   hasValidLogin = self.hasFacebookLogin;
   
   [self targetedShare:SLServiceTypeFacebook];
}

- (IBAction)TwitterShare:(id)sender
{
   hasValidLogin = self.hasTwitterLogin;

   [self targetedShare:SLServiceTypeTwitter];
}

- (IBAction)loginButton:(id)sender
{
}

- (IBAction)shareButton:(id)sender
{
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
   // Get the new view controller using [segue destinationViewController].
   // Pass the selected object to the new view controller.
}

@end
