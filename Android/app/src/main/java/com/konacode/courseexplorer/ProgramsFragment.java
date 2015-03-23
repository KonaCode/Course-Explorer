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
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Ryan on 3/8/2015.
 */
public class ProgramsFragment extends Fragment implements AbsListView.OnItemClickListener
{
   /**
    * The Adapter which will be used to populate the ListView/GridView with
    * Views.
    */
   private SCISServiceHelper mServiceHelper;
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
   public void onCreate(Bundle pSavedInstanceState)
   {
      super.onCreate(pSavedInstanceState);
   }

   @Override
   public View onCreateView(LayoutInflater pInflater, ViewGroup pContainer,
                            Bundle pSavedInstanceState)
   {
      Context context = getActivity();
      View view = pInflater.inflate(R.layout.fragment_programs, pContainer, false);
      ArrayAdapter adapter = new ArrayAdapter<Program>(context,
            android.R.layout.simple_list_item_1, android.R.id.text1, ProgramsContent.getContent());

      mServiceHelper = new SCISServiceHelper(context, context.getApplicationContext().getPackageName() + ".SCISResult");

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
            mServiceHelper.RetrieveAll();
         }
      });

      if(ProgramsContent.isEmpty())
      {
         mServiceHelper.RetrieveAll();
      }

      return view;
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
   public void onItemClick(AdapterView<?> pParent, View pView, int pPosition, long pID)
   {
   }

   /**
    * The default content for this Fragment has a TextView that is shown when
    * the list is empty. If you would like to change the text, call this method
    * to supply the text it should use.
    */
   public void setEmptyText(CharSequence pEmptyText)
   {
      View emptyView = mListView.getEmptyView();

      if(emptyView instanceof TextView)
      {
         ((TextView) emptyView).setText(pEmptyText);
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
}
