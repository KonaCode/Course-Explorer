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

public class ConversationActivity extends Activity
{
   String mAddress;
   int mPort;
   EditText mMessageEdit;
   TextView mTextView;

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

      ReceiveTask task = new ReceiveTask(mAddress, mPort);
      task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

      Button send = (Button) findViewById(R.id.chat_send_button_id);

      send.setOnClickListener(new View.OnClickListener()
      {
         @Override
         public void onClick(View pView)
         {
            String message = mMessageEdit.getText().toString();
            SendTask task = new SendTask(mAddress, mPort, message);

            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

   public class ReceiveTask extends AsyncTask<Void, Void, Void>
   {
      String mAddress;
      int mPort;
      String mResponse;

      ReceiveTask(String pAddress, int pPort)
      {
         mAddress = pAddress;
         mPort = pPort;
      }

      @Override
      protected Void doInBackground(Void... pArguments)
      {
         InetAddress server = null;
         Socket socket = null;

         try
         {
            server = InetAddress.getByName(mAddress);
            socket = new Socket(server, mPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            while(socket.isConnected())
            {
               mResponse = null;
               InputStream inputStream = socket.getInputStream();

               // inputStream.read() will block if no data return
               for(bytesRead = 0; (bytesRead = inputStream.read(buffer)) != -1; )
               {
                  byteArrayOutputStream.write(buffer, 0, bytesRead);

                  mResponse = byteArrayOutputStream.toString("UTF-8");
               }

               publishProgress();
            }
         }
         catch(UnknownHostException pException)
         {
            pException.printStackTrace();

            Log.i("conversation", "UnknownHostException: " + pException.toString());
         }
         catch(IOException pException)
         {
            pException.printStackTrace();

            Log.i("conversation", "IOException: " + pException.toString());
         }
         finally
         {
            if(socket != null)
            {
               try
               {
                  socket.close();
               }
               catch(IOException pException)
               {
                  pException.printStackTrace();
               }
            }
         }

         return null;
      }

      @Override
      protected void onProgressUpdate(Void... pValues)
      {
         super.onProgressUpdate(pValues);

         if(mResponse != null)
         {
            mTextView.append(String.format("Response: %s", mResponse));
         }
      }
   }

   public class SendTask extends AsyncTask<Void, Void, Void>
   {
      String mAddress;
      int mPort;
      String mMessage;

      SendTask(String pAddress, int pPort, String pMessage)
      {
         mAddress = pAddress;
         mPort = pPort;
         mMessage = pMessage;
      }

      @Override
      protected Void doInBackground(Void... pArguments)
      {
         InetAddress server = null;
         Socket socket = null;
         PrintWriter out = null;

         try
         {
            server = InetAddress.getByName(mAddress);
            socket = new Socket(server, mPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            if(socket.isConnected())
            {
               out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

               out.write(mMessage);
            }
         }
         catch(UnknownHostException pException)
         {
            pException.printStackTrace();

            Log.i("conversation", "UnknownHostException: " + pException.toString());
         }
         catch(IOException pException)
         {
            pException.printStackTrace();

            Log.i("conversation", "IOException: " + pException.toString());
         }
         finally
         {
            if(socket != null)
            {
               try
               {
                  socket.close();
               }
               catch(IOException pException)
               {
                  pException.printStackTrace();
               }
            }

            if(out != null)
            {
               try
               {
                  out.close();
               }
               catch(Exception pException)
               {
                  pException.printStackTrace();
               }
            }
         }

         return null;
      }

      @Override
      protected void onPostExecute(Void pResult)
      {
         super.onPostExecute(pResult);

         mTextView.append(String.format("Sent: %s", mMessage));
      }
   }
}
