package com.konacode.courseexplorer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ProgramsActivity extends Activity
{
   private AbsListView mListView;
   private BroadcastReceiver mBroadcastReceiver;
   private TaskProcessorService mService;
   private boolean mBound = false;
   private ProgressDialog mProgress = null;
   private int mNumTasks = 0;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_programs);
      getActionBar().setDisplayHomeAsUpEnabled(true);

      mListView = (AbsListView)findViewById(R.id.programs_view_id);
      mProgress = new ProgressDialog(this);
      mBroadcastReceiver = new LocalBroadcastReceiver(this);
   }

   @Override
   protected void onStart()
   {
      super.onStart();

      // Register the broadcast receiver as an observer when a task completes
      IntentFilter mFilter = new IntentFilter(getApplicationContext().getPackageName() + ".SCISResult");
      registerReceiver(mBroadcastReceiver, mFilter);

      // Bind to the service to receive notifications
      Intent intent = new Intent(this, TaskProcessorService.class);
      bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
   }

   @Override
   protected void onStop()
   {
      unregisterReceiver(mBroadcastReceiver);

      // Unbind from the service
      if(mBound)
      {
         unbindService(mConnection);
         mBound = false;
      }

      super.onStop();
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      boolean result = true;

      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_programs, menu);

      return result;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      Context context = this;
      Intent intent = null;
      boolean result = true;

      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      switch(id)
      {
         case android.R.id.home:
            /*
            When we go back (home) from the action bar, we start a new intent here
            for the main activity.
             */
            intent = new Intent(context, MainActivity.class);
            break;
         case R.id.action_settings:
            Toast.makeText(context, "There are no settings defined yet!", Toast.LENGTH_LONG).show();
            break;
         default:
            result = super.onOptionsItemSelected(item);
            break;
      }

      if(intent != null)
      {
         startActivity(intent);
      }

      return result;
   }

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment
   {
      public PlaceholderFragment()
      {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState)
      {
         View rootView = inflater.inflate(R.layout.fragment_programs, container, false);

         return rootView;
      }
   }

   private ServiceConnection mConnection = new ServiceConnection()
   {
      @Override
      public void
      onServiceConnected(ComponentName pComponentName, IBinder pService)
      {
         mBound = true;
      }

      @Override
      public void
      onServiceDisconnected(ComponentName pComponentName)
      {
         mBound = false;
      }
   };

   private class LocalBroadcastReceiver extends BroadcastReceiver
   {
      private Activity mActivity;

      public LocalBroadcastReceiver(Activity pActivity)
      {
         mActivity = pActivity;
      }

      @Override
      public void onReceive(Context pContext, Intent pIntent)
      {
         Bundle extras = pIntent.getExtras();
         boolean hasResult = (extras.get(TaskProcessorService.Extras.RESULT_EXTRA) != null);
         String description = extras.getString(TaskProcessorService.Extras.PROVIDER_DESCRIPTION_EXTRA);

         if(hasResult)
         {
            mNumTasks--;

            if((mNumTasks == 0) && (mProgress != null))
            {
               mProgress.dismiss();
            }

            // Which method is the result for
            ArrayAdapter adapter;
            int taskID = extras.getInt(TaskProcessorService.Extras.METHOD_EXTRA);
            boolean success = extras.getBoolean(TaskProcessorService.Extras.RESULT_EXTRA);
            String text = description + " " + (success ? "Complete" : "Failure");

            switch(taskID)
            {
               case SCISServiceProvider.TaskIDs.TASK_RETRIEVE_COURSES:
                  adapter = new ArrayAdapter<Program>(mActivity,
                        android.R.layout.simple_list_item_1, android.R.id.text1, ProgramsContent.getContent());
                  mListView.setAdapter(adapter);
                  break;
            }
         }
         else
         {
            mNumTasks++;

            if(mProgress != null)
            {
               if(mNumTasks == 1)
               {
                  mProgress.setTitle("SCIS Information");
                  mProgress.setMessage(description);
                  mProgress.setIndeterminate(true);
                  mProgress.show();
               }
               else
               {
                  mProgress.setMessage(description);
               }
            }
         }
      }
   };
}
