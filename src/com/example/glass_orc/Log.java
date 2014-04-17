package com.example.glass_orc;
//package com.bitcoin_glass.service;

import android.os.SystemClock;

final class Log 
{
    private static final String TAG = "Live CardDemo";

    static final boolean V = android.util.Log.VERBOSE <= android.util.Log.VERBOSE;
    static final boolean D = android.util.Log.VERBOSE <= android.util.Log.DEBUG;
    static final boolean I = android.util.Log.VERBOSE <= android.util.Log.INFO;
    static final boolean W = android.util.Log.VERBOSE <= android.util.Log.WARN;
    static final boolean E = android.util.Log.VERBOSE <= android.util.Log.ERROR;

    static void v(String logMe)
    {
        android.util.Log.v(TAG, SystemClock.uptimeMillis() + " " + logMe);
    }
    static void v(String logMe, Throwable ex)
    {
        android.util.Log.v(TAG, SystemClock.uptimeMillis() + " " + logMe, ex);
    }

    static void d(String logMe) 
    {
        android.util.Log.d(TAG, SystemClock.uptimeMillis() + " " + logMe);
    }
    static void d(String logMe, Throwable ex)
    {
        android.util.Log.d(TAG, SystemClock.uptimeMillis() + " " + logMe, ex);
    }

    static void i(String logMe)
    {
        android.util.Log.i(TAG, logMe);
    }
    static void i(String logMe, Throwable ex)
    {
        android.util.Log.i(TAG, logMe, ex);
    }

    static void w(String logMe)
    {
        android.util.Log.w(TAG, logMe);
    }
    static void w(String logMe, Throwable ex)
    {
        android.util.Log.w(TAG, logMe, ex);
    }

    static void e(String logMe)
    {
        android.util.Log.e(TAG, logMe);
    }
    static void e(String logMe, Exception ex)
    {
        android.util.Log.e(TAG, logMe, ex);
    }
}
