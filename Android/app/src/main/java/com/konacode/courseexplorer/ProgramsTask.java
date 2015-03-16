package com.konacode.courseexplorer;

import android.os.AsyncTask;

import javax.xml.transform.Result;

/**
 * Created by Ryan on 3/9/2015.
 */
public class ProgramsTask extends AsyncTask
{
   @Override
   protected Object doInBackground(Object[] pParameters)
   {
      Object taskResult = null;
      Boolean result = (pParameters != null);

      if(result)
      {

      }

      return taskResult;
   }

   protected void onPostExecute(Result pResult)
   {
      super.onPostExecute(pResult);
   }
}
