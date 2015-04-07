package com.konacode.courseexplorer;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ryan on 3/24/2015.
 */
public class ProgramServiceSQLiteIO extends SQLiteOpenHelper implements IProgramsService
{
   public String mProgramsTableName = "";
   private static int mDatabaseVersion = 1;
   private String mCreateProgramTable = "";

   private final Context mContext;

   public ProgramServiceSQLiteIO(Context pContext)
   {
      super(pContext, "programs.db", null, mDatabaseVersion);

      mProgramsTableName = "programs";
      mContext = pContext;

      mCreateProgramTable = "CREATE TABLE " + mProgramsTableName + " (id integer primary key autoincrement, program_id integer, program_title text not null, course_id integer, course_title text)";
   }

   @Override
   public void onCreate(SQLiteDatabase pDatabase)
   {
      pDatabase.execSQL(mCreateProgramTable);
   }

   @Override
   public void onUpgrade(SQLiteDatabase pDatabase, int pArg1, int pArg2)
   {
      pDatabase.execSQL("DROP TABLE IF EXISTS " + mProgramsTableName);
      onCreate(pDatabase);
   }

   public int count()
   {
      int result = 0;
      SQLiteDatabase database = this.getWritableDatabase();
      Cursor cursor = database.query(mProgramsTableName, new String[]{ "id", "program_id", "program_title", "course_id", "course_title" },
            null, null, null, null, null);

      result = cursor.getCount();

      cursor.close();

      return result;
   }

   public boolean clear()
   {
      SQLiteDatabase database = this.getWritableDatabase();

      database.execSQL("DROP TABLE IF EXISTS " + mProgramsTableName);
      onCreate(database);

      return true;
   }

   public Long insert(ContentValues pValues)
   {
      Long id = null;
      Boolean result = pValues.containsKey("program_id") && pValues.containsKey("program_title");

      if(result)
      {
         SQLiteDatabase database = this.getWritableDatabase();

         pValues.remove("id");
         id = database.insert(mProgramsTableName, "program", pValues);
      }

      return id;
   }

   public Program create(Program pProgram)
   {
      SQLiteDatabase database = this.getWritableDatabase();
      Program result = null;

      if(!has(mProgramsTableName, "program_id", pProgram.mID))
      {
         ContentValues values = new ContentValues();
         values.put("program_id", pProgram.mID);
         values.put("program_title", pProgram.mTitle);

         Long id = database.insert(mProgramsTableName, null, values);
         pProgram.mID = id.intValue();

         for(Course course: pProgram.mCourses)
         {
            values.put("course_id", course.mID);
            values.put("course_title", course.mTitle);

            id = database.insert(mProgramsTableName, null, values);
         }

         database.close();

         result = pProgram;
      }

      return result;
   }

   public List<Program> retrieveAll()
   {
      List<Program> programs = new ArrayList<Program>();
      Boolean result = true;

      if(result)
      {
         HashMap<Integer, Program> programsHash = new HashMap<>();
         SQLiteDatabase database = this.getWritableDatabase();
         Cursor cursor = database.query(mProgramsTableName, new String[]{ "id", "program_id", "program_title", "course_id", "course_title" },
               null, null, null, null, null);

         while(cursor.moveToNext())
         {
            Program program = programsHash.get(cursor.getInt(1));
            Course course = new Course();

            if(program == null)
            {
               program = new Program();
            }

            program.mID = cursor.getInt(1);
            program.mTitle = cursor.getString(2);
            course.mID = cursor.getInt(3);
            course.mTitle = cursor.getString(4);

            program.mCourses.add(course);
            programsHash.put(program.mID, program);
         }

         cursor.close();

         for(Program program: programsHash.values())
         {
            programs.add(program);
         }
      }

      return programs;
   }

   public int update(String pKey, ContentValues pValues, String pWhere, String[] pWhereArgs)
   {
      int result = 0;

      if(has(mProgramsTableName, "id", pKey))
      {
         SQLiteDatabase database = this.getWritableDatabase();
         String where = pKey.isEmpty() ? pWhere : ("id=" + pKey);

         if(!pKey.isEmpty())
         {
            where += pWhere.isEmpty() ? "" : (" AND (" + pWhere + ")");
         }

         result = database.update(mProgramsTableName, pValues, where, pWhereArgs);
      }

      return result;
   }

   public Program update(Program pProgram)
   {
      Program result = null;

      if(pProgram != null)
      {
         delete(pProgram);

         result = create(pProgram);
      }

      return result;
   }

   public int delete(String pKey, String pWhere, String[] pWhereArgs)
   {
      SQLiteDatabase database = this.getWritableDatabase();
      String where = pKey.isEmpty() ? pWhere : ("id=" + pKey);
      int result = 0;

      if(!pKey.isEmpty())
      {
         where += pWhere.isEmpty() ? "" : (" AND (" + pWhere + ")");
      }

      result = database.delete(mProgramsTableName, where, pWhereArgs);

      return result;
   }

   public Program delete(Program pProgram)
   {
      Program result = null;

      if(has(mProgramsTableName, "id", String.valueOf(pProgram.mID)))
      {
         SQLiteDatabase database = this.getWritableDatabase();

         database.delete(mProgramsTableName, "id=" + String.valueOf(pProgram.mID), null);
         database.close();

         result = pProgram;
      }

      return result;
   }

   private Boolean has(String pTableName, String pKey, String pValue)
   {
      Boolean result = false;

      if(pTableName != null)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         Cursor cursor = database.rawQuery("SELECT " + pKey + " FROM " + pTableName + " WHERE " + pKey + "=?", new String[]{pValue});

         result = (cursor.getCount() > 0);
      }

      return result;
   }

   private Boolean has(String pTableName, String pKey, Integer pValue)
   {
      Boolean result = false;

      if(pTableName != null)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         Cursor cursor = database.rawQuery("SELECT " + pKey + " FROM " + pTableName + " WHERE " + pKey + "=?", new String[]{String.valueOf(pValue)});

         result = (cursor.getCount() > 0);
      }

      return result;
   }

   private Program getProgram(Cursor pCursor)
   {
      Program program = new Program();

      program.mID = pCursor.getInt(0);
      program.mTitle = pCursor.getString(1);

      return program;
   }

   private Course getItem(long pProgramID, Cursor pCursor)
   {
      long courseID = pCursor.getInt(1);
      Course result = null;

      Course course = new Course();
      course.mID = pCursor.getInt(0);
      course.mTitle = pCursor.getString(2);

      if(courseID == pProgramID)
      {
         result = course;
      }

      return result;
   }
}
