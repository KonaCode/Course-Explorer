package com.konacode.courseexplorer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
      Uri uri = SCISContentProvider.CONTENT_URI;
      ContentResolver resolver = mContext.getContentResolver();
      Cursor cursor = resolver.query(uri, null, null, null, null);
      Boolean populateContent = true;
      Boolean result = true;

      if(result && populateContent)
      {
         HttpClient client = new DefaultHttpClient();
         String url = "http://regisscis.net/Regis2/webresources/" + ((pTaskID == TaskIDs.TASK_RETRIEVE_COURSES) ? "regis2.course" : "regis2.program");
         HttpGet httpGet = new HttpGet(url);

         httpGet.setHeader("Accept", "application/json");

         if(pTaskID == TaskIDs.TASK_RETRIEVE_PROGRAMS)
         {
            resolver.delete(uri, "", null);
         }

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
                  ContentValues values = new ContentValues();
                  values.put("program_id", program.mID);
                  values.put("program_title", program.mTitle);

                  if(pTaskID == TaskIDs.TASK_RETRIEVE_COURSES)
                  {
                     values.put("course_id", course.mID);
                     values.put("course_title", course.mTitle);
                  }

                  resolver.insert(uri, values);
               }
            }

            reader.endArray();
         }
         catch(Exception pException)
         {
            Log.i("SCISServiceProvider", String.format("Exception: %s", pException.toString()));

            ProgramsContent.clear();
         }
      }

      ProgramsContent.clear();
      cursor = resolver.query(SCISContentProvider.CONTENT_URI, null, null, null, null);

      if(cursor != null)
      {
         for(cursor.moveToFirst(); cursor.moveToNext(); )
         {
            Program program = ProgramsContent.getInstance().getProgram(cursor.getInt(1));

            if(program == null)
            {
               program = new Program();
            }

            program.mID = cursor.getInt(1);
            program.mTitle = cursor.getString(2);

            if(cursor.getInt(3) != 0)
            {
               Course course = new Course();
               course.mID = cursor.getInt(3);
               course.mTitle = cursor.getString(4);

               program.mCourses.add(course);
            }

            ProgramsContent.getInstance().update(program);
         }
      }

      return result;
   }
}
