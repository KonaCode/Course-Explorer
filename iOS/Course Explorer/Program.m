//
//  Program.m
//  Course Explorer
//
//  Created by Ryan Wing on 4/2/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "Program.h"

@implementation Program

@dynamic program_id;
@dynamic title;
@synthesize courses = _courses;

- (NSMutableArray*)courses
{
   if(!_courses)
   {
      _courses = [[NSMutableArray alloc] init];
   }
   
   return _courses;
}

- (NSString*)description
{
   return [NSString stringWithFormat:@"Program %@: %@ (%ld courses)", self.program_id, self.title, (unsigned long)self.courses.count];
}

@end
