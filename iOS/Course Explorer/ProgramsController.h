//
//  ProgramsController.h
//  Course Explorer
//
//  Created by Ryan Wing on 3/8/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <CoreData/CoreData.h>
#import <UIKit/UIKit.h>

@interface ProgramsController : UITableViewController<NSFetchedResultsControllerDelegate>

@property (nonatomic, retain) UIAlertView* alert;
@property (nonatomic, retain) NSArray* objects;
@property (nonatomic, retain) NSArray* programsWithCourses;

@end
