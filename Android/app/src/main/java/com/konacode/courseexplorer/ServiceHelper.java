package com.konacode.courseexplorer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

/**
 * Created by Ryan on 3/18/2015.
 *
 * Source Credit: http://www.codeproject.com/Articles/429997/Sample-Implementation-of-Virgil-Dobjanschis-Rest-p
 * Modified by Ryan Wing
 */
public abstract class ServiceHelper
{
   protected String mName;
   protected Context mContext;
   protected int mProviderID;
   protected String mResultAction;

   protected
   ServiceHelper(String pName, Context pContext, int pProviderID, String pResultAction)
   {
      mName = pName;
      mContext = pContext;
      mProviderID = pProviderID;
      mResultAction = pResultAction;
   }

   /**
    * Starts the specified methodId with no parameters
    * @param pTaskID The method to start
    */
   protected void
   StartTask(int pTaskID)
   {
      StartTask(pTaskID, null);
   }

   /**
    * Starts the specified methodId with the parameters given in Bundle
    * @param pTaskID The method to start
    * @param pBundle   The parameters to pass to the method
    */
   protected void
   StartTask(int pTaskID, Bundle pBundle)
   {
      Intent service = new Intent(mContext, TaskProcessorService.class);
      String description = Description(pTaskID);

      service.putExtra(TaskProcessorService.Extras.PROVIDER_EXTRA, mProviderID);
      service.putExtra(TaskProcessorService.Extras.PROVIDER_DESCRIPTION_EXTRA, description);
      service.putExtra(TaskProcessorService.Extras.METHOD_EXTRA, pTaskID);
      service.putExtra(TaskProcessorService.Extras.RESULT_ACTION_EXTRA, mResultAction);

      if(pBundle != null)
      {
         service.putExtras(pBundle);
      }

      mContext.startService(service);
   }

   protected abstract String
   Description(int pTaskID);
}
