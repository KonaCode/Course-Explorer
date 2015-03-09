package com.konacode.courseexplorer;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

/**
 * Created on 2/23/2015 by Ryan Wing
 */
public class AboutActivity extends Activity
{
   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_about);
      getActionBar().setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_about, menu);

      return true;
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
}
