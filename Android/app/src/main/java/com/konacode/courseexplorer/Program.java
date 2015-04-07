package com.konacode.courseexplorer;

import java.util.ArrayList;

/**
 * Created by Ryan on 3/9/2015.
 */
public class Program
{
   /*
   The Program class encapsulates one entry within the programs content.
   For now, it is essentially just managing an ID and a single string
    */
   public Integer mID;
   public String mTitle;
   public ArrayList<Course> mCourses;

   public Program()
   {
      mID = 0;
      mTitle = "<unknown>";
      mCourses = new ArrayList<>();
   }

   public boolean equals(Object pOther)
   {
      Program other = (Program)pOther;
      boolean result = (mID == other.mID);

      return result;
   }

   public String toString()
   {
      int id = mID.intValue();
      String result = String.format("Program ID:%d %s (%d course%s)", id, mTitle, mCourses.size(), ((mCourses.size() == 1) ? "" : "s"));

      return result;
   }
}
