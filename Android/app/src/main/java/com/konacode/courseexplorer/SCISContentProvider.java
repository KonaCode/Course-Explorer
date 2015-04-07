package com.konacode.courseexplorer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.List;

/**
 * Created by Ryan on 3/25/2015.
 */
public class SCISContentProvider extends ContentProvider
{
   public static final Uri CONTENT_URI = Uri.parse("content://com.konacode.courseexplorer/programs");

   private Context mContext;
   private ProgramServiceSQLiteIO mProgramsStore;
   private List<Program> mPrograms;

   @Override
   public boolean onCreate()
   {
      mContext = getContext();
      mProgramsStore = new ProgramServiceSQLiteIO(mContext);
      mPrograms = mProgramsStore.retrieveAll();

      return true;
   }

   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
   {
      Cursor result = null;

      SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
      queryBuilder.setTables(mProgramsStore.mProgramsTableName);

      if(isMemberURI(uri))
      {
         queryBuilder.appendWhere("id=" + uri.getPathSegments().get(1));
      }

      String orderBy = ((sortOrder == null) || sortOrder.isEmpty()) ? "id" : sortOrder;

      result = queryBuilder.query(mProgramsStore.getReadableDatabase(), projection, selection, selectionArgs, null, null, orderBy);
      result.setNotificationUri(getContext().getContentResolver(), uri);

      return result;
   }

   @Override
   public String getType(Uri uri)
   {
      String result = "com.android.cursor." + (isMemberURI(uri) ? "item" : "dir") + "/com.konacode.courseexplorer.program";

      return result;
   }

   @Override
   public Uri insert(Uri uri, ContentValues values)
   {
      Uri result = null;
      Long id = mProgramsStore.insert(values);

      if(id > 0)
      {
         result = ContentUris.withAppendedId(CONTENT_URI, id);
      }

      mContext.getContentResolver().notifyChange(uri, null);

      return result;
   }

   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs)
   {
      String key = getMemberID(uri);
      int result = mProgramsStore.delete(key, selection, selectionArgs);

      mContext.getContentResolver().notifyChange(uri, null);

      return result;
   }

   @Override
   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
   {
      String key = getMemberID(uri);
      int result = mProgramsStore.update(key, values, selection, selectionArgs);

      mContext.getContentResolver().notifyChange(uri, null);

      return result;
   }

   private Boolean isMemberURI(Uri uri)
   {
      Boolean result = (uri.getLastPathSegment() == null);

      return result;
   }

   private String getMemberID(Uri uri)
   {
      String result = isMemberURI(uri) ? uri.getPathSegments().get(1) : "";

      return result;
   }
}
