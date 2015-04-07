package com.konacode.courseexplorer;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Ryan on 3/18/2015.
 */
public class SCISServiceHelper extends ServiceHelper
{
   private SCISContentProvider mContentProvider;

   public SCISServiceHelper(Context pContext, String pResultAction)
   {
      super("SCIS Information", pContext, TaskProcessorService.Providers.SCIS_PROVIDER, pResultAction);

      mContentProvider = new SCISContentProvider();
   }

   public void
   RetrieveAll()
   {
      RetrievePrograms();
      RetrieveCourses();
   }

   public void
   RetrievePrograms()
   {
      ProgramsContent.clear();

      StartTask(SCISServiceProvider.TaskIDs.TASK_RETRIEVE_PROGRAMS);
   }

   public void
   RetrieveCourses()
   {
      StartTask(SCISServiceProvider.TaskIDs.TASK_RETRIEVE_COURSES);
   }

   @Override
   protected String
   Description(int pTaskID)
   {
      String result = mName;

      switch(pTaskID)
      {
         case SCISServiceProvider.TaskIDs.TASK_RETRIEVE_PROGRAMS:
            result += ": Retrieving Programs";
            break;
         case SCISServiceProvider.TaskIDs.TASK_RETRIEVE_COURSES:
            result += ": Retrieving Courses";
            break;
      }

      return result;
   }
}
