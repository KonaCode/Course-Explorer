package com.konacode.courseexplorer;

import android.os.Bundle;

/**
 * Created by Ryan on 3/18/2015.
 *
 * Implementations of this interface will use the RunTask() method and its arguments to perform
 * the service-specific task.
 */
public interface IServiceProvider
{
   boolean
   RunTask(int pTaskID, Bundle pExtras);
}
