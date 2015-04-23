//
//  ProgramsController.m
//  Course Explorer
//
//  Created by Ryan Wing on 3/8/15.
//  Copyright (c) 2015 Ryan Wing. All rights reserved.
//

#import <CoreData/CoreData.h>
#import <RKInMemoryManagedObjectCache.h>
#import <RKManagedObjectStore.h>
#import <RKMIMETypes.h>
#import <RKObjectManager.h>
#import <RKResponseDescriptor.h>

#import "Course.h"
#import "Program.h"
#import "ProgramsController.h"

@interface ProgramsController ()

@end

@implementation ProgramsController

BOOL requestBusy;

@synthesize alert = _alert;
@synthesize objects = _objects;
@synthesize programsWithCourses = _programsWithCourses;

- (void)viewDidLoad
{
   [super viewDidLoad];

   // Uncomment the following line to preserve selection between presentations.
   // self.clearsSelectionOnViewWillAppear = NO;
    
   // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
   // self.navigationItem.rightBarButtonItem = self.editButtonItem;

   [self initializeRestKit];
   [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"ProgramCell"];
   
   requestBusy = false;
}

- (void)didReceiveMemoryWarning
{
   // Dispose of any resources that can be recreated.
   [super didReceiveMemoryWarning];
}

- (UIAlertView*)alert
{
   if(!_alert)
   {
      self.alert = [[UIAlertView alloc] initWithTitle:@"Alert" message:@"Working" delegate:nil cancelButtonTitle:@"Ok" otherButtonTitles:nil, nil];
   }
   
   return _alert;
}

- (NSArray*)objects
{
   if(!_objects)
   {
      _objects = [[NSArray alloc] init];
   }

   if(([_objects count] == 0) && !requestBusy)
   {
      NSDictionary* requestParams = nil;

      requestBusy = true;

      self.alert.title = @"Retrieve Programs Status";
      self.alert.message = @"Contacting Server...";
      [self.alert show];

      [[RKObjectManager sharedManager] getObjectsAtPath:@"/Regis2/webresources/regis2.program" parameters:requestParams
      success:^(RKObjectRequestOperation* operation, RKMappingResult* mappingResult)
      {
         NSLog(@"Objects: %@", self.objects);
         
         requestBusy = false;
      }
      failure:^(RKObjectRequestOperation* operation, NSError* error)
      {
         self.alert.title = @"Error";
         self.alert.message = [NSString stringWithFormat:@"%@", error];

         NSLog(@"Error: %@", error);

         requestBusy = false;
      }];

      [[RKObjectManager sharedManager] getObjectsAtPath:@"/Regis2/webresources/regis2.course" parameters:requestParams
      success:^(RKObjectRequestOperation* operation, RKMappingResult* mappingResult)
       {
          _objects = mappingResult.array;
          
          [self.tableView reloadData];
          [self.alert dismissWithClickedButtonIndex:0 animated:YES];
          
          NSLog(@"Objects: %@", self.objects);
          
          requestBusy = false;
       }
       failure:^(RKObjectRequestOperation* operation, NSError* error)
       {
          self.alert.title = @"Error";
          self.alert.message = [NSString stringWithFormat:@"%@", error];
          
          NSLog(@"Error: %@", error);
          
          requestBusy = false;
       }];
   }

   return _objects;
}

- (NSArray*)programsWithCourses
{
   if(!_programsWithCourses)
   {
      _programsWithCourses = [[NSArray alloc] init];
   }

   if(([_programsWithCourses count] == 0) && !requestBusy)
   {
      NSDictionary* requestParams = nil;
      
      requestBusy = true;
      
      self.alert.title = @"SCIS Information";
      self.alert.message = @"Retrieving Programs...";
      [self.alert show];
      
      [[RKObjectManager sharedManager] getObjectsAtPath:@"/Regis2/webresources/regis2.program" parameters:requestParams
      success:^(RKObjectRequestOperation* operation, RKMappingResult* mappingResult)
      {
         self.alert.message = @"Retrieving Courses...";

         _programsWithCourses = mappingResult.array;

         NSLog(@"Programs: %@", self.programsWithCourses);

         [[RKObjectManager sharedManager] getObjectsAtPath:@"/Regis2/webresources/regis2.course" parameters:requestParams
         success:^(RKObjectRequestOperation* operation, RKMappingResult* mappingResult)
         {
            for(Course* course in mappingResult.array)
            {
               for(Program* program in _programsWithCourses)
               {
                  if([program.program_id intValue] == [course.program_id intValue])
                  {
                     [program.courses addObject:course];

                     break;
                  }
               }
            }
            
            [self.tableView reloadData];
            [self.alert dismissWithClickedButtonIndex:0 animated:YES];

            NSLog(@"Programs With Courses: %@", self.objects);

            requestBusy = false;
         }
         failure:^(RKObjectRequestOperation* operation, NSError* error)
         {
            [self.tableView reloadData];
            self.alert.title = @"Error";
            self.alert.message = [NSString stringWithFormat:@"%@", error];

            NSLog(@"Error: %@", error);

            requestBusy = false;
         }];
      }
      failure:^(RKObjectRequestOperation* operation, NSError* error)
      {
         self.alert.title = @"Error";
         self.alert.message = [NSString stringWithFormat:@"%@", error];

         NSLog(@"Error: %@", error);

         requestBusy = false;
      }];
   }
   
   return _programsWithCourses;
}

- (void)initializeRestKit
{
   RKObjectManager* objectManager = [RKObjectManager managerWithBaseURL:[NSURL URLWithString:@"http://regisscis.net"]];
   NSManagedObjectModel* objectModel = [NSManagedObjectModel mergedModelFromBundles:nil];
   RKManagedObjectStore* objectStore = [[RKManagedObjectStore alloc] initWithManagedObjectModel:objectModel];
   
   objectManager.managedObjectStore = objectStore;
   
   // Create a map for the program class
   RKEntityMapping* programMapping = [RKEntityMapping mappingForEntityForName:NSStringFromClass([Program class]) inManagedObjectStore:objectStore];
   
   programMapping.identificationAttributes = @[ @"program_id", @"title" ];
   [programMapping addAttributeMappingsFromDictionary:@{ @"id": @"program_id", @"name": @"title" }];
   
   // Create a map for the course class
   RKEntityMapping* courseMapping = [RKEntityMapping mappingForEntityForName:NSStringFromClass([Course class]) inManagedObjectStore:objectStore];
   
   courseMapping.identificationAttributes = @[ @"course_id", @"title", @"program_id", @"program_title" ];
   [courseMapping addAttributeMappingsFromDictionary:@{ @"id": @"course_id", @"name": @"title", @"pid.id": @"program_id", @"pid.name": @"program_title" }];
   
   // Create the persistence store
   [objectStore createPersistentStoreCoordinator];
   
   NSURL* applicationDocumentsDirectory = [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
   NSURL* storeURL = [applicationDocumentsDirectory URLByAppendingPathComponent:@"CourseExplorer-SCIS.sqlite"];
   NSString* storePath = [storeURL path];
   NSError* error = nil;
   
   NSPersistentStore* persistentStore = [objectStore addSQLitePersistentStoreAtPath:storePath fromSeedDatabaseAtPath:nil withConfiguration:nil options:@{ NSMigratePersistentStoresAutomaticallyOption: @YES, NSInferMappingModelAutomaticallyOption: @YES } error:&error];
   
   NSAssert(persistentStore, @"Failed to initialize the persistent store: %@", error);
   
   [objectStore createManagedObjectContexts];
   objectStore.managedObjectCache = [[RKInMemoryManagedObjectCache alloc] initWithManagedObjectContext:objectStore.persistentStoreManagedObjectContext];

   NSString* keyPath = nil;
   RKResponseDescriptor* programsResponseDescriptor = [RKResponseDescriptor responseDescriptorWithMapping:programMapping method:RKRequestMethodGET pathPattern:@"/Regis2/webresources/regis2.program" keyPath:keyPath statusCodes:[NSIndexSet indexSetWithIndex:200]];
   RKResponseDescriptor* coursesResponseDescriptor = [RKResponseDescriptor responseDescriptorWithMapping:courseMapping method:RKRequestMethodGET pathPattern:@"/Regis2/webresources/regis2.course" keyPath:keyPath statusCodes:[NSIndexSet indexSetWithIndex:200]];
   
   [objectManager setAcceptHeaderWithMIMEType:RKMIMETypeJSON];
   [objectManager setRequestSerializationMIMEType:RKMIMETypeJSON];
   [objectManager addResponseDescriptor:programsResponseDescriptor];
   [objectManager addResponseDescriptor:coursesResponseDescriptor];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView*)tableView
{
   NSInteger result = [self.programsWithCourses count];
   
   // Return the number of sections.
   return result;
}

- (NSInteger)tableView:(UITableView*)tableView numberOfRowsInSection:(NSInteger)section
{
   Program* program = self.programsWithCourses[section];
   NSInteger result = [program.courses count];

   // Return the number of rows in the section.
   return result;
}

- (NSString*)tableView:(UITableView*)tableView titleForHeaderInSection:(NSInteger)section
{
   Program* program = self.programsWithCourses[section];
   NSString* result = [program description];
   
   return result;
}

- (UITableViewCell*)tableView:(UITableView*)tableView cellForRowAtIndexPath:(NSIndexPath*)indexPath
{
   // Configure the cell...
   UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:@"ProgramCell" forIndexPath:indexPath];
   Program* program = self.programsWithCourses[indexPath.section];
   Course* course = program.courses[indexPath.row];

   cell.textLabel.text = [course description];
   cell.textLabel.font = [UIFont systemFontOfSize:14];
   cell.textLabel.numberOfLines = 0;
   cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;

   return cell;
}

// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView*)tableView canEditRowAtIndexPath:(NSIndexPath*)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return NO;
}

// Override to support editing the table view.
- (void)tableView:(UITableView*)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath*)indexPath
{
   if (editingStyle == UITableViewCellEditingStyleDelete)
   {
      // Delete the row from the data source
      [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
   }
   else if (editingStyle == UITableViewCellEditingStyleInsert)
   {
      // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
   }
}

// Override to support rearranging the table view.
- (void)tableView:(UITableView*)tableView moveRowAtIndexPath:(NSIndexPath*)fromIndexPath toIndexPath:(NSIndexPath*)toIndexPath
{
}

// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView*)tableView canMoveRowAtIndexPath:(NSIndexPath*)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return NO;
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue*)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}

@end
