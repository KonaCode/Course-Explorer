package com.konacode.courseexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import de.tavendo.autobahn.*;

public class ConversationActivity extends Activity
{
   final String mTag = "ConversationActivity";

   String mAddress;
   int mPort;
   EditText mMessageEdit;
   TextView mTextView;

   WebSocketConnection mConnection;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_conversation);
      getActionBar().setDisplayHomeAsUpEnabled(true);

      Bundle extras = getIntent().getExtras();

      if(extras != null)
      {
         mAddress = extras.getString("address");
         mPort = extras.getInt("port");
      }

      mMessageEdit = (EditText)findViewById(R.id.chat_edit_id);
      mTextView = (TextView)findViewById(R.id.chat_response_view_id);
      mConnection = new WebSocketConnection();

      connect();

      Button send = (Button) findViewById(R.id.chat_send_button_id);

      send.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View pView)
         {
            String message = mMessageEdit.getText().toString();

            mConnection.sendTextMessage(String.format("Sending: %s", message));
         }
      });
   }

   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_conversation, menu);
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
            intent = new Intent(context, ChatActivity.class);
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

   private void connect()
   {
      final String connectionURI = "ws://www.regisscis.net:80/WebSocketServer/chat";

      try
      {
         mConnection.connect(connectionURI, new WebSocketHandler()
         {
            @Override
            public void onOpen()
            {
               Log.i(mTag, "Status: Opened Connection " + connectionURI);

               super.onOpen();
               mConnection.sendTextMessage("Hello!");
            }

            @Override
            public void onClose(int code, String reason)
            {
               Log.i(mTag, "Status: Closed Connection " + connectionURI);

               super.onClose(code, reason);
            }

            @Override
            public void onTextMessage(String payload)
            {
               Log.i(mTag, "Received Message: " + payload);

               super.onTextMessage(payload);
               mTextView.append(String.format("Response: %s", payload));
            }
         });
      }
      catch(WebSocketException pException)
      {
         Log.i(mTag, "Web Socket Exception: " + pException.toString());
      }
   }
}
