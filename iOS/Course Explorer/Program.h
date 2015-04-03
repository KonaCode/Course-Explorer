//
//  Program.h
//  Course Explorer
//
//  Created by Ryan Wing on 4/2/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Program : NSManagedObject

@property (nonatomic, retain) NSNumber * program_id;
@property (nonatomic, retain) NSString * title;

@end
