package com.konacode.courseexplorer;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ryan on 3/18/2015.
 *
 * Source Credit: http://www.codeproject.com/Articles/429997/Sample-Implementation-of-Virgil-Dobjanschis-Rest-p
 * Modified by Ryan Wing
 */
public class TaskProcessorService extends Service
{
   private Integer mLastStartID;
   private Context mContext = this;
   private ArrayList<Messenger> mClients = new ArrayList<Messenger>();
   private Messenger mMessenger = new Messenger(new IncomingHandler());
   private NotificationManager mNotificationManager;

   /**
    * The keys to be used for the required actions to start this service.
    */
   public static class Extras
   {
      /**
       * Determine if the tasks are to be run in serial or parallel
       */
      public static final String PARALLEL_EXTRA = "PARALLEL_EXTRA";

      /**
       * The provider which the called method is on.
       */
      public static final String PROVIDER_EXTRA = "PROVIDER_EXTRA";

      /**
       * The provider description of the called method.
       */
      public static final String PROVIDER_DESCRIPTION_EXTRA = "PROVIDER_DESCRIPTION_EXTRA";

      /**
       * The method to call.
       */
      public static final String METHOD_EXTRA = "METHOD_EXTRA";

      /**
       * The action to used for the result intent.
       */
      public static final String RESULT_ACTION_EXTRA = "RESULT_ACTION_EXTRA";

      /**
       * The extra used in the result intent to return the result.
       */
      public static final String RESULT_EXTRA = "RESULT_EXTRA";
   }

   public static class Messages
   {
      static final int MSG_REGISTER_CLIENT = 1;
   }

   private final HashMap<String, AsyncServiceTask> mTasks = new HashMap<String, AsyncServiceTask>();

   /**
    * Identifier for each supported provider.
    * Cannot use 0 as Bundle.getInt(key) returns 0 when the key does not exist.
    */
   public static class Providers
   {
      public static final int SCIS_PROVIDER = 1;
   }

   private IServiceProvider GetProvider(int pProviderID)
   {
      IServiceProvider provider = null;

      switch(pProviderID)
      {
      case Providers.SCIS_PROVIDER:
         provider = new SCISServiceProvider(this);
         break;
      }

      return provider;
   }

   /**
    * Builds a string identifier for this method call.
    * The identifier will contain data about:
    *   What processor was the method called on
    *   What method was called
    *   What parameters were passed
    * This should be enough data to identify a task to detect if a similar task is already running.
    */
   private String getTaskIdentifier(Bundle pExtras)
   {
      String[] keys = pExtras.keySet().toArray(new String[0]);
      StringBuilder identifier = new StringBuilder();
      String result = null;

      java.util.Arrays.sort(keys);

      for(String key: keys)
      {
         // The result action may be different for each call.
         if(key.equals(Extras.RESULT_ACTION_EXTRA))
         {
            continue;
         }

         identifier.append("{");
         identifier.append(key.replace("_EXTRA", ""));
         identifier.append(":");
         identifier.append(pExtras.get(key).toString());
         identifier.append("}");
      }

      result = identifier.toString();

      return result;
   }

   @Override
   public void onCreate()
   {
      mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
   }

   @Override
   public int onStartCommand(Intent pIntent, int pFlags, int pStartID)
   {
      int result = START_STICKY;

      // This must be synchronised so that service is not stopped while a new task is being added.
      synchronized(mTasks)
      {
         // stopSelf will be called later and if a new task is being added we do not want to stop the service.
         mLastStartID = pStartID;

         Bundle extras = pIntent.getExtras();
         String taskIdentifier = getTaskIdentifier(extras);

         Log.i("TaskProcessorService", "Starting Task:" + taskIdentifier);

         // If a similar task is already running then lets use that task.
         AsyncServiceTask task = mTasks.get(taskIdentifier);

         if(task == null)
         {
            mTasks.put(taskIdentifier, task = new AsyncServiceTask(taskIdentifier, extras));
         }

         // Add this Result Action to the task so that the calling activity can be notified when the task is complete.
         String resultAction = extras.getString(Extras.RESULT_ACTION_EXTRA);

         if(resultAction != "")
         {
            task.addResultAction(resultAction);
         }

         boolean inParallel = extras.getBoolean(Extras.PARALLEL_EXTRA);
         Void[] params = (Void[])null;

         if(inParallel)
         {
            // AsyncTasks are by default only run in serial (depending on the android version)
            // see android documentation for AsyncTask.execute()
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
         }
         else
         {
            task.execute(params);
         }
      }

      return result;
   }

   @Override
   public IBinder onBind(Intent pIntent)
   {
      IBinder result = null;

      if(mMessenger != null)
      {
         result = mMessenger.getBinder();
      }

      return result;
   }

   public class IncomingHandler extends Handler
   {
      @Override
      public void
      handleMessage(Message pMessage)
      {
         boolean result = true;

         switch(pMessage.what)
         {
         case Messages.MSG_REGISTER_CLIENT:
            mClients.add(pMessage.replyTo);
            break;
         default:
            super.handleMessage(pMessage);
            break;
         }
      }
   }

   public class AsyncServiceTask extends AsyncTask<Void, Void, Boolean>
   {
      private final Bundle mExtras;
      private final ArrayList<String> mResultActions = new ArrayList<String>();
      private final String mTaskIdentifier;

      /**
       * Constructor for AsyncServiceTask
       *
       * @param pTaskIdentifier A string which describes the method being called.
       * @param pExtras         The Extras from the Intent which was used to start this method call.
       */
      public AsyncServiceTask(String pTaskIdentifier, Bundle pExtras)
      {
         mTaskIdentifier = pTaskIdentifier;
         mExtras = pExtras;
      }

      public void addResultAction(String pResultAction)
      {
         if(!mResultActions.contains(pResultAction))
         {
            mResultActions.add(pResultAction);
         }
      }

      @Override
      protected void onPreExecute()
      {
         // This must be synchronised so that service is not stopped while a new task is being added.
         synchronized(mTasks)
         {
            Log.i("TaskProcessorService", "Starting Task:" + mTaskIdentifier);

            // Notify the caller(s) that the method has finished executing
            for(int i = 0; i < mResultActions.size(); i++)
            {
               Intent resultIntent = new Intent(mResultActions.get(i));

               resultIntent.putExtras(mExtras);
               resultIntent.setPackage(mContext.getPackageName());

               mContext.sendBroadcast(resultIntent);
            }
         }
      }

      @Override
      protected Boolean doInBackground(Void... pParameters)
      {
         final int providerID = mExtras.getInt(Extras.PROVIDER_EXTRA);
         final int methodID = mExtras.getInt(Extras.METHOD_EXTRA);
         Boolean result = false;

         Log.i("TaskProcessorService", "Working Task:" + mTaskIdentifier);

         if((providerID != 0) && (methodID != 0))
         {
            final IServiceProvider provider = GetProvider(providerID);

            if(provider != null)
            {
               try
               {
                  result = provider.RunTask(methodID, mExtras);
               }
               catch(Exception pException)
               {
                  Log.e("TaskProcessorService", String.format("Exception: %s", pException.getMessage()));

                  result = false;
               }
            }

         }

         return result;
      }

      @Override
      protected void onPostExecute(Boolean pResult)
      {
         // This must be synchronised so that service is not stopped while a new task is being added.
         synchronized(mTasks)
         {
            Log.i("TaskProcessorService", "Finishing Task:" + mTaskIdentifier);

            // Notify the caller(s) that the method has finished executing
            for(int i = 0; i < mResultActions.size(); i++)
            {
               Intent resultIntent = new Intent(mResultActions.get(i));

               resultIntent.putExtra(Extras.RESULT_EXTRA, pResult.booleanValue());
               resultIntent.putExtras(mExtras);
               resultIntent.setPackage(mContext.getPackageName());

               mContext.sendBroadcast(resultIntent);
            }

            // The task is complete so remove it from the running tasks list
            mTasks.remove(mTaskIdentifier);

            // If there are no other executing methods then stop the service
            if(mTasks.size() < 1)
            {
               stopSelf(mLastStartID);
            }
         }
      }
   }
}