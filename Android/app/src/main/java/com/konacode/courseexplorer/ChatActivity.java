package com.konacode.courseexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ChatActivity extends Activity
{
   Context mContext;
   EditText mAddressEdit;
   EditText mPortEdit;
   EditText mNameEdit;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_chat);
      getActionBar().setDisplayHomeAsUpEnabled(true);

      mContext = this;
      mAddressEdit = (EditText)findViewById(R.id.chat_address_edit_id);
      mPortEdit = (EditText)findViewById(R.id.chat_port_edit_id);
      mNameEdit = (EditText)findViewById(R.id.chat_name_edit_id);

      Button connect = (Button)findViewById(R.id.chat_connect_button_id);

      mAddressEdit.setText("www.regisscis.net");
      mPortEdit.setText("8080");
      connect.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View pView)
         {
            String message = "";
            String address = mAddressEdit.getText().toString();
            String port = mPortEdit.getText().toString();
            Intent intent = null;

            if(address.isEmpty())
            {
               message += "The address field is empty! ";
            }

            if(port.isEmpty())
            {
               message += "The port field is empty! ";
            }

            if(mNameEdit.getText().toString().isEmpty())
            {
               message += "You must specify a name! ";
            }

            if(message.isEmpty())
            {
               message += "Connecting";
               intent = new Intent(mContext, ConversationActivity.class);
               intent.putExtra("address", address);
               intent.putExtra("port", Integer.parseInt(port));
            }

            if(!message.isEmpty())
            {
               Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }

            if(intent != null)
            {
               startActivity(intent);
            }
         }
      });
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_chat, menu);
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
