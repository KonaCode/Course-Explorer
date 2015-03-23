package com.konacode.courseexplorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Ryan on 3/18/2015.
 */
public class SCISServiceProvider implements IServiceProvider
{
   private Context mContext;
   private Bundle mExtras;

   public static class TaskIDs
   {
      public static final int TASK_RETRIEVE_PROGRAMS = 1;
      public static final int TASK_RETRIEVE_COURSES = 2;
   }

   public
   SCISServiceProvider(Context pContext)
   {
      mContext = pContext;
   }

   @Override
   public boolean
   RunTask(int pTaskID, Bundle pExtras)
   {
      boolean result = true;

      mExtras = pExtras;

      switch(pTaskID)
      {
      case TaskIDs.TASK_RETRIEVE_PROGRAMS:
      case TaskIDs.TASK_RETRIEVE_COURSES:
         result = Retrieve(pTaskID);
         break;
      default:
         result = false;
         break;
      }

      return result;
   }

   private boolean
   Retrieve(int pTaskID)
   {
      boolean result = true;

      ProgramsContent content = ProgramsContent.getInstance();
      HttpClient client = new DefaultHttpClient();

      String url = "http://regisscis.net/Regis2/webresources/";
      url += (pTaskID == TaskIDs.TASK_RETRIEVE_COURSES) ? "regis2.course" : "regis2.program";

      HttpGet httpGet = new HttpGet(url);
      httpGet.setHeader("Accept", "application/json");

      try
      {
         HttpResponse response = client.execute(httpGet);

         InputStream inputStream = response.getEntity().getContent();
         JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

         for(reader.beginArray(); reader.hasNext(); )
         {
            Course course = new Course();
            Program program = new Program();

            for(reader.beginObject(); reader.hasNext(); )
            {
               String name = reader.nextName();
               Boolean useValue = name.equals("id") || name.equals("name") || name.equals("pid");

               if(!useValue)
               {
                  reader.skipValue();
                  continue;
               }

               if(pTaskID == TaskIDs.TASK_RETRIEVE_COURSES)
               {
                  if(name.equals("pid"))
                  {
                     for(reader.beginObject(); reader.hasNext(); )
                     {
                        name = reader.nextName();
                        program.mID = name.equals("id") ? reader.nextInt() : program.mID;
                        program.mTitle = name.equals("name") ? reader.nextString() : program.mTitle;
                     }

                     reader.endObject();
                  }
                  else
                  {
                     course.mID = name.equals("id") ? reader.nextInt() : course.mID;
                     course.mTitle = name.equals("name") ? reader.nextString() : course.mTitle;
                  }
               }
               else
               {
                  program.mID = name.equals("id") ? reader.nextInt() : program.mID;
                  program.mTitle = name.equals("name") ? reader.nextString() : program.mTitle;
               }
            }

            reader.endObject();

            if((program.mID != 0) && !program.mTitle.isEmpty())
            {
               if(pTaskID == TaskIDs.TASK_RETRIEVE_COURSES)
               {
                  program = content.getProgram(program.mID);

                  if(program != null)
                  {
                     program.mCourses.add(course);
                  }
               }
               else
               {
                  ProgramsContent.getInstance().addItem(program);
               }
            }
         }

         reader.endArray();
      }
      catch(Exception pException)
      {
         Log.i("SCISServiceProvider", String.format("Exception: %s", pException.toString()));

         ProgramsContent.clear();
      }

      return result;
   }
}
