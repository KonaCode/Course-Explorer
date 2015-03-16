//
//  Program.h
//  Course Explorer
//
//  Created by Ryan Wing on 3/12/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Program : NSObject<NSCoding>

@property (nonatomic, strong)NSNumber* id;
@property (nonatomic, strong)NSString* name;

@end
