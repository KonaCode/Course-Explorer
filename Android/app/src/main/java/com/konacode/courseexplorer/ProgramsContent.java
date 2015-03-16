package com.konacode.courseexplorer;

/**
 * Created by Ryan on 3/8/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing content for the about activity
 */
public class ProgramsContent
{
   /*
   The items of the about that we want to store
    */
   private List<Program> mContent;

   /*
   This map provides a means to look up items in the list
    */
   private Map<Integer, Program> mProgramIDMap;
   private Map<String, Program> mProgramNameMap;

   private static ProgramsContent mInstance = null;

   public static ProgramsContent getInstance()
   {
      if(mInstance == null)
      {
         mInstance = new ProgramsContent();
      }

      return mInstance;
   }

   public static boolean isEmpty()
   {
      boolean result = (mInstance == null) ? true : mInstance.mContent.isEmpty();

      return result;
   }

   public static boolean clear()
   {
      boolean result = true;

      if(mInstance != null)
      {
         mInstance.mContent.clear();
         mInstance.mProgramIDMap.clear();
      }

      return result;
   }

   public static List<Program> getContent()
   {
      List<Program> result = (mInstance == null) ? new ArrayList<Program>() : mInstance.mContent;

      return result;
   }

   public static Program getProgram(int pID)
   {
      Program result = null;

      if(mInstance != null)
      {
         result = mInstance.mProgramIDMap.get(pID);
      }

      return result;
   }

   public static Program getProgram(String pName)
   {
      Program result = null;

      if(mInstance != null)
      {
         result = mInstance.mProgramNameMap.get(pName);
      }

      return result;
   }

   private ProgramsContent()
   {
      mContent = new ArrayList<Program>();
      mProgramIDMap = new HashMap<Integer, Program>();
      mProgramNameMap = new HashMap<String, Program>();
   }

   public void addItem(Program pItem)
   {
      /*
      When adding an item, make sure the items list and mapping are consistent
       */
      mContent.add(pItem);
      mProgramIDMap.put(pItem.mID, pItem);
      mProgramNameMap.put(pItem.mTitle, pItem);
   }
}
