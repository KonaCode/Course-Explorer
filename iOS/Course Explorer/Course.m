//
//  Course.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/2/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "Course.h"


@implementation Course

@dynamic course_id;
@dynamic title;
@dynamic program_id;
@dynamic program_title;

- (NSString*)description
{
   return [NSString stringWithFormat:@"Course ID %@: %@ For Program ID %@: %@", self.course_id, self.title, self.program_id, self.program_title];
}

@end
