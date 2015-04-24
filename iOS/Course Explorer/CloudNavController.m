//
//  CloudNavController.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/10/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "CloudNavController.h"

@interface CloudNavController ()

@end

@implementation CloudNavController

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
}

- (void)didReceiveMemoryWarning
{
   [super didReceiveMemoryWarning];
   // Dispose of any resources that can be recreated.
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
   // Get the new view controller using [segue destinationViewController].
   // Pass the selected object to the new view controller.
}

@end
