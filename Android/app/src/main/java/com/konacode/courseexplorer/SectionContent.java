package com.konacode.courseexplorer;

import android.content.Context;

/**
 * Created by Ryan on 3/6/2015.
 */
public class SectionContent
{
   private Context mContext;
   private static SectionContent mInstance = null;

   public static SectionContent getInstance(Context pContext)
   {
      if(mInstance == null)
      {
         mInstance = new SectionContent(pContext);
      }

      return mInstance;
   }

   private SectionContent(Context pContext)
   {
      mContext = pContext;
   }

   public String[] getMainContent()
   {
      String[] content = { mContext.getString(R.string.app_name), "SCIS Programs", "Messaging", "Social Media", "Chat", "About" };

      return content;
   }
}
