package com.konacode.courseexplorer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Ryan on 4/11/2015.
 */
public class CloudIntentService extends Service
{
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
