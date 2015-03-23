//
//  ProgramsResponseSerializer.h
//  Course Explorer
//
//  Created by Ryan Wing on 3/20/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import "AFURLResponseSerialization.h"

typedef NS_ENUM(NSUInteger, ResponseType)
{
   JSON = 0,
   XML = 1
};

@interface ProgramsResponseSerializer : AFHTTPResponseSerializer

@property (readwrite)ResponseType responseType;

- (id)ParseAsJSON:(NSDictionary*)dictionary;
- (id)ParseAsXML:(NSDictionary*)dictionary;

@end
