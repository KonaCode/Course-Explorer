/**
 * Created on 3/5/2015 by Ryan Wing
 */
package com.konacode.courseexplorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing content for the about activity
 */
public class AboutContent
{
   /*
   The items of the about that we want to store
    */
   public static List<AboutItem> ITEMS = new ArrayList<AboutItem>();

   /*
   This map provides a means to look up items in the list
    */
   public static Map<String, AboutItem> ITEM_MAP = new HashMap<String, AboutItem>();

   static
   {
      // Add the about items. For now, we have the author name and class project name
      addItem(new AboutItem("Course Explorer"));
      addItem(new AboutItem("Copyright 2015 Ryan Wing"));

      addItem(new AboutItem("MSSE 657"));
      addItem(new AboutItem("Enterprise Android Development"));
   }

   private static void addItem(AboutItem item)
   {
      /*
      When adding an item, make sure the items list and mapping are consistent
       */
      ITEMS.add(item);
      ITEM_MAP.put(item.id, item);
   }

   /*
   The AboutItem class encapsulates one entry within the about content.
   For now, it is essentially just managing a single string
    */
   public static class AboutItem
   {
      public String id;
      public String content;
      private static int mCurrentID = 1;

      public AboutItem(String content)
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
