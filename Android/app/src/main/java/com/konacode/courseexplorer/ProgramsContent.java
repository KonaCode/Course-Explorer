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
   public static List<ProgramsItem> ITEMS = new ArrayList<ProgramsItem>();

   /*
   This map provides a means to look up items in the list
    */
   public static Map<String, ProgramsItem> ITEM_MAP = new HashMap<String, ProgramsItem>();

   static
   {
      // Add the about items. For now, we have the author name and class project name
      addItem(new ProgramsItem("Sample Program #1"));
      addItem(new ProgramsItem("Sample Program #2"));
      addItem(new ProgramsItem("Sample Program #3"));
   }

   private static void addItem(ProgramsItem item)
   {
      /*
      When adding an item, make sure the items list and mapping are consistent
       */
      ITEMS.add(item);
      ITEM_MAP.put(item.id, item);
   }

   /*
   The ProgramsItem class encapsulates one entry within the about content.
   For now, it is essentially just managing a single string
    */
   public static class ProgramsItem
   {
      public String id;
      public String content;
      private static int mCurrentID = 1;

      public ProgramsItem(String content)
      {
         this.id = String.valueOf(mCurrentID++);
         this.content = content;
      }

      @Override
      public String toString()
      {
         return content;
      }
   }
}
