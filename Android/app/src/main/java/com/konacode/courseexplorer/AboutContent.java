/**
 * Created on 3/5/2015 by Ryan Wing
 */
package com.konacode.courseexplorer;

import android.os.Build;

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
      addItem(new AboutItem(String.format("Course Explorer v%s", BuildConfig.VERSION_CODE)));
      addItem(new AboutItem("Author: Ryan Wing"));

      addItem(new AboutItem(String.format("Product Version: %s", BuildConfig.VERSION_NAME)));
      addItem(new AboutItem(String.format("Android Version: %s", Build.VERSION.RELEASE)));
      addItem(new AboutItem("Product Type: Course Project"));

      addItem(new AboutItem("Course Name: MSSE 657"));
      addItem(new AboutItem("Course Description: Enterprise Android Development"));

      addItem(new AboutItem("Copyright 2015 Ryan Wing"));
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
