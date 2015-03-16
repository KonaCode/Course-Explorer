package com.konacode.courseexplorer;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Ryan on 3/8/2015.
 */
public class ProgramsFragment extends Fragment implements AbsListView.OnItemClickListener
{
   public enum Attribute { PROGRAMS, COURSES };

   /**
    * The Adapter which will be used to populate the ListView/GridView with
    * Views.
    */
   private OnFragmentInteractionListener mListener;
   private AbsListView mListView;

   // TODO: Rename and change types of parameters
   public static ProgramsFragment newInstance()
   {
      ProgramsFragment fragment = new ProgramsFragment();
      Bundle args = new Bundle();

      fragment.setArguments(args);

      return fragment;
   }

   /**
    * Mandatory empty constructor for the fragment manager to instantiate the
    * fragment (e.g. upon screen orientation changes).
    */
   public ProgramsFragment()
   {
   }

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      Context context = getActivity();
      View view = inflater.inflate(R.layout.fragment_programs, container, false);
      ArrayAdapter adapter = new ArrayAdapter<Program>(context,
            android.R.layout.simple_list_item_1, android.R.id.text1, ProgramsContent.getContent());

      /*
      After we inflate the about fragment, we assign the list adapter to the list view.
      We don't need to refresh the list view or adapter because the content is immutable
       */
      mListView = (AbsListView) view.findViewById(R.id.programs_view_id);
      mListView.setAdapter(adapter);

      Button refresh = (Button)view.findViewById(R.id.refresh_programs_button_id);

      refresh.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View pView)
         {
            retrieve();
         }
      });

      if(ProgramsContent.isEmpty())
      {
         retrieve();
      }

      return view;
   }

   public boolean retrieve()
   {
      boolean result = true;

      if(result)
      {
         new HttpAsyncTask(Attribute.PROGRAMS).execute("http://regisscis.net/Regis2/webresources/regis2.program");
         new HttpAsyncTask(Attribute.COURSES).execute("http://regisscis.net/Regis2/webresources/regis2.course");
      }

      return result;
   }

   @Override
   public void onAttach(Activity pActivity)
   {
      super.onAttach(pActivity);
   }

   @Override
   public void onDetach()
   {
      super.onDetach();

      mListener = null;
   }

   @Override
   public void onItemClick(AdapterView<?> parent, View view, int position, long id)
   {
   }

   /**
    * The default content for this Fragment has a TextView that is shown when
    * the list is empty. If you would like to change the text, call this method
    * to supply the text it should use.
    */
   public void setEmptyText(CharSequence emptyText)
   {
      View emptyView = mListView.getEmptyView();

      if(emptyView instanceof TextView)
      {
         ((TextView) emptyView).setText(emptyText);
      }
   }

   /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p/>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
   public interface OnFragmentInteractionListener
   {
      // TODO: Update argument type and name
      public void onFragmentInteraction(String id);
   }

   private class HttpAsyncTask extends AsyncTask<String, Void, String>
   {
      private Attribute mAttribute;
      private ProgressDialog mProgress;

      public
      HttpAsyncTask(Attribute pAttribute)
      {
         mAttribute = pAttribute;
      }

      @Override
      protected void onPreExecute()
      {
         mProgress = new ProgressDialog(getActivity());

         if(mProgress != null)
         {
            mProgress.setTitle("Refreshing");

            if(mAttribute == Attribute.COURSES)
            {
               mProgress.setMessage("Collecting Course Information");
            }
            else if(mAttribute == Attribute.PROGRAMS)
            {
               mProgress.setMessage("Collecting Program Information");
            }

            mProgress.setIndeterminate(true);
            mProgress.show();
         }

         ProgramsContent.clear();
      }

      @Override
      protected String doInBackground(String... pParameters)
      {
         Log.i("ProgramsHttpAsyncTask", "Do In Background");
         String result  = "";

         ProgramsContent content = ProgramsContent.getInstance();
         HttpClient client = new DefaultHttpClient();
         String url = "testing";
         url = pParameters[0];

         HttpGet httpGet = new HttpGet(url);

         httpGet.setHeader("Accept", "application/json");

         try
         {
            HttpResponse response = client.execute(httpGet);

            InputStream inputStream = response.getEntity().getContent();
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));

            for(reader.beginArray(); reader.hasNext(); )
            {
               Course course = new Course();
               Program program = new Program();

               for(reader.beginObject(); reader.hasNext(); )
               {
                  String name = reader.nextName();
                  Boolean useValue = name.equals("id") || name.equals("name") || name.equals("pid");

                  if(!useValue)
                  {
                     reader.skipValue();
                     continue;
                  }

                  if(mAttribute == Attribute.COURSES)
                  {
                     if(name.equals("pid"))
                     {
                        for(reader.beginObject(); reader.hasNext(); )
                        {
                           name = reader.nextName();
                           program.mID = name.equals("id") ? reader.nextInt() : program.mID;
                           program.mTitle = name.equals("name") ? reader.nextString() : program.mTitle;
                        }

                        reader.endObject();
                     }
                     else
                     {
                        course.mID = name.equals("id") ? reader.nextInt() : course.mID;
                        course.mTitle = name.equals("name") ? reader.nextString() : course.mTitle;
                     }
                  }
                  else if(mAttribute == Attribute.PROGRAMS)
                  {
                     program.mID = name.equals("id") ? reader.nextInt() : program.mID;
                     program.mTitle = name.equals("name") ? reader.nextString() : program.mTitle;
                  }
               }

               reader.endObject();

               if((program.mID != 0) && !program.mTitle.isEmpty())
               {
                  if(mAttribute == Attribute.COURSES)
                  {
                     program = content.getProgram(program.mID);

                     if(program != null)
                     {
                        program.mCourses.add(course);
                     }
                  }
                  else if(mAttribute == Attribute.PROGRAMS)
                  {
                     ProgramsContent.getInstance().addItem(program);
                  }
               }
            }

            reader.endArray();
         }
         catch(Exception pException)
         {
            Log.i("ProgramsHttpAsyncTask", String.format("Exception: %s", pException.toString()));

            result = "<empty>";

            ProgramsContent.clear();
         }

         Log.i("ProgramsHttpAsyncTask", String.format("Result: %s", result));

         return result;
      }

      @Override
      protected void onPostExecute(String pResult)
      {
         Context context = getActivity();

         Log.i("ProgramsHttpAsyncTask", "Post Execute");

         if(mProgress != null)
         {
            mProgress.dismiss();
            mProgress = null;
         }

         ArrayAdapter adapter = new ArrayAdapter<Program>(context,
            android.R.layout.simple_list_item_1, android.R.id.text1, ProgramsContent.getContent());

         mListView.setAdapter(adapter);
      }
   }
}
