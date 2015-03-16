//
//  XMLParser.h
//  Course Explorer
//
//  Created by Ryan Wing on 3/15/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface XMLParser : NSObject<NSXMLParserDelegate>
{
   NSMutableArray* dictionaryStack;
   NSMutableString* textInProgress;
}

+ (NSDictionary*)dictionaryForXMLData:(NSData*)data;
+ (NSDictionary*)dictionaryForXMLString:(NSString*)string;

@end
