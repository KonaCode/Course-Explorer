package com.konacode.courseexplorer;

/**
 * Created by Ryan on 3/9/2015.
 */
public class Course
{
   /*
   The Program class encapsulates one entry within the programs content.
   For now, it is essentially just managing an ID and a single string
    */
   public Integer mID;
   public String mTitle;

   public Course()
   {
      mID = 0;
      mTitle = "<unknown>";
   }

   public String toString()
   {
      int id = mID.intValue();
      String result = String.format("(ID:%d) %s", id, mTitle);

      return result;
   }
}
