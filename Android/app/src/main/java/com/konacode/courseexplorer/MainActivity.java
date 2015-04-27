package com.konacode.courseexplorer;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements ActionBar.OnNavigationListener
{

   /**
    * The serialization (saved instance state) Bundle key representing the
    * current dropdown position.
    */
   private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      // Set up the action bar to show a dropdown list.
      final ActionBar actionBar = getActionBar();
      actionBar.setDisplayShowTitleEnabled(false);
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

      // Specify a SpinnerAdapter to populate the dropdown list.
      ArrayAdapter<String> contentAdapter = new ArrayAdapter<String>(actionBar.getThemedContext(),
         android.R.layout.simple_list_item_1,
         android.R.id.text1, SectionContent.getInstance(this).getMainContent());

      // Set up the dropdown list navigation in the action bar.
      actionBar.setListNavigationCallbacks(contentAdapter, this);
   }

   @Override
   public void onRestoreInstanceState(Bundle savedInstanceState)
   {
      // Restore the previously serialized current dropdown position.
      if(savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM))
      {
         getActionBar().setSelectedNavigationItem(
               savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState)
   {
      // Serialize the current dropdown position.
      outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
            getActionBar().getSelectedNavigationIndex());
   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      MenuInflater inflater = getMenuInflater();
      boolean result = true;

      if(result)
      {
         // Inflate the menu; this adds items to the action bar if it is present.
         inflater.inflate(R.menu.main_activity_actions, menu);
      }

      if(result)
      {
         result = super.onCreateOptionsMenu(menu);
      }

      return result;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if(id == R.id.action_settings)
      {
         return true;
      }

      return super.onOptionsItemSelected(item);
   }

   @Override
   public boolean onNavigationItemSelected(int position, long id)
   {
      Context context = this;
      Intent intent = null;
      Bundle bundle = null;
      FragmentManager fragmentManager = getFragmentManager();

      fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();

      switch(position)
      {
         case 0:
            // Home
            break;
         case 1:
            // SCIS Programs
            intent = new Intent(context, ProgramsActivity.class);
            Toast.makeText(context, "Showing Programs", Toast.LENGTH_SHORT).show();
            break;
         case 2:
            // Messaging
            Toast.makeText(context, "Not Available Yet!", Toast.LENGTH_SHORT).show();
            break;
         case 3:
            // Social Media
            intent = new Intent(context, SocialMediaActivity.class);
            Toast.makeText(context, "Showing Social Media", Toast.LENGTH_SHORT).show();
            break;
         case 4:
            // Chat
            intent = new Intent(context, ChatActivity.class);
            Toast.makeText(context, "Showing Chat", Toast.LENGTH_SHORT).show();
            break;
         case 5:
            // About
            intent = new Intent(context, AboutActivity.class);
            Toast.makeText(context, "Showing About", Toast.LENGTH_SHORT).show();
            break;
      }

      if(intent != null)
      {
         startActivity(intent);
      }

      return true;
   }

   /**
    * A placeholder fragment containing a simple view.
    */
   public static class PlaceholderFragment extends Fragment
   {
      /**
       * The fragment argument representing the section number for this
       * fragment.
       */
      private static final String ARG_SECTION_NUMBER = "section_number";

      /**
       * Returns a new instance of this fragment for the given section
       * number.
       */
      public static PlaceholderFragment newInstance(int sectionNumber)
      {
         PlaceholderFragment fragment = new PlaceholderFragment();
         Bundle args = new Bundle();
         args.putInt(ARG_SECTION_NUMBER, sectionNumber);
         fragment.setArguments(args);
         return fragment;
      }

      public PlaceholderFragment()
      {
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState)
      {
         View view = inflater.inflate(R.layout.fragment_main, container, false);
         Context context = view.getContext();

         JustifiedTextView description = new JustifiedTextView(context, context.getString(R.string.app_description));
         description.textColor = Color.WHITE;
         description.textSize = 20;
         description.setFont(Typeface.DEFAULT);

         LinearLayout place = (LinearLayout)view.findViewById(R.id.app_description_layout_id);
         place.addView(description);

         return view;
      }
   }

}
