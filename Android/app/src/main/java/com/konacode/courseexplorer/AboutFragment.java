package com.konacode.courseexplorer;

/**
 * Created by Ryan on 3/8/2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class AboutFragment extends Fragment implements AbsListView.OnItemClickListener
{
   /**
    * The Adapter which will be used to populate the ListView/GridView with
    * Views.
    */
   private OnFragmentInteractionListener mListener;
   private AbsListView mListView;
   private ListAdapter mAdapter;

   // TODO: Rename and change types of parameters
   public static AboutFragment newInstance()
   {
      AboutFragment fragment = new AboutFragment();
      Bundle args = new Bundle();

      fragment.setArguments(args);

      return fragment;
   }

   /**
    * Mandatory empty constructor for the fragment manager to instantiate the
    * fragment (e.g. upon screen orientation changes).
    */
   public AboutFragment()
   {
   }

   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      /*
      The adapter for the list view will retrieve items from the about content
       for display by the UI.
       */
      mAdapter = new ArrayAdapter<AboutContent.AboutItem>(getActivity(),
            android.R.layout.simple_list_item_1, android.R.id.text1, AboutContent.ITEMS);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {
      Context context = getActivity();
      View view = inflater.inflate(R.layout.fragment_about, container, false);

      /*
      After we inflate the about fragment, we assign the list adapter to the list view.
      We don't need to refresh the list view or adapter because the content is immutable
       */
      mListView = (AbsListView) view.findViewById(R.id.about_view_id);
      mListView.setAdapter(mAdapter);

      return view;
   }

   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
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
}

