//
//  Program.m
//  Course Explorer
//
//  Created by Ryan Wing on 3/12/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "Program.h"

static NSString* const NAME = @"name";
static NSString* const ITEMS = @"items";

@implementation Program

- (id)init
{
   self = [super init];
   
   if(self)
   {
      self.name = [[NSString alloc] initWithFormat:@"Uknonwn"];
   }
   
   return self;
}

- (NSString*)description
{
   return [NSString stringWithFormat:@"%@", _name];
}

- (void)encodeWithCoder:(NSCoder*)coder
{
   // encode the property values
   [coder encodeObject:self.name forKey:NAME];
}

- (id)initWithCoder:(NSCoder*)decoder
{
   self = [super init];
   
   // decode the property values
   if(self)
   {
      _name = [decoder decodeObjectForKey:NAME];
   }
   
   return self;
}

@end
