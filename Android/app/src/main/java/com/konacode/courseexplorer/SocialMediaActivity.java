package com.konacode.courseexplorer;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;


public class SocialMediaActivity extends Activity
{
   private Context mContext;
   private SocialAuthAdapter mAdapter;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_social_media);
      getActionBar().setDisplayHomeAsUpEnabled(true);

      mContext = this;
      mAdapter = new SocialAuthAdapter(new ResponseListener(mContext));

      Button postButton = (Button)findViewById(R.id.id_post_message_btn);
      postButton.setTextColor(Color.WHITE);
      postButton.setBackgroundResource(R.drawable.com_facebook_button_background);

      mAdapter.addProvider(SocialAuthAdapter.Provider.FACEBOOK, R.drawable.facebook);
      mAdapter.addProvider(SocialAuthAdapter.Provider.LINKEDIN, R.drawable.linkedin);
      mAdapter.enable(postButton);
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_social_media, menu);
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

   private final class ResponseListener implements DialogListener
   {
      private Context mContext;

      ResponseListener(Context pContext)
      {
         super();

         mContext = pContext;
      }

      @Override
      public void onError(SocialAuthError socialAuthError)
      {
         String message = String.format("Error: %s", socialAuthError.getMessage());

         Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onCancel()
      {
         Toast.makeText(mContext, "Cancelled", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onBack()
      {
         Toast.makeText(mContext, "Back", Toast.LENGTH_LONG).show();
      }

      public void onComplete(Bundle pValues)
      {
         String message = ((EditText)findViewById(R.id.id_social_message_edit)).getText().toString();

         mAdapter.updateStatus(message, new MessageListener(mContext), false);
      }
   }

   private final class MessageListener implements SocialAuthListener
   {
      private Context mContext;

      MessageListener(Context pContext)
      {
         super();

         mContext = pContext;
      }

      @Override
      public void onExecute(String s, Object o)
      {
         String message = new String("Error: The message was not posted.");
         Integer status = (Integer)o;

         switch(status.intValue())
         {
         case 200:
         case 201:
         case 204:
            message = "Message Posted";
            break;
         }

         Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onError(SocialAuthError socialAuthError)
      {
         String message = String.format("Error: %s", socialAuthError.getMessage());

         Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
      }
   }
}
