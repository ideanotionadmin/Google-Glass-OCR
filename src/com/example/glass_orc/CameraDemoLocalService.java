package com.example.glass_orc;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.RemoteViews;

import com.example.glass_orc.CamActivity;
import com.example.glass_orc.R;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;


// ...
public class CameraDemoLocalService extends Service
{
    // For live card
    private LiveCard liveCard = null;

    private static final String cardId = "camerademo_card";

    // "Heart beat".
    private Timer heartBeat = null;

    // Stored/caches the file path of the "last" photo taken...
    private String currentPhotoFilePath = null;
    // For "previous" photo
    private String previousPhotoFilePath = null;
    // Last updated time
    private long currentPhotoTime = 0L;


    // No need for IPC...
    public class LocalBinder extends Binder {
        public CameraDemoLocalService getService() {
            return CameraDemoLocalService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();


    // temporary
    StrictMode.ThreadPolicy defaultThreadPolicy = null;
    // temporary


    @Override
    public void onCreate()
    {
        super.onCreate();

        // temporary
        defaultThreadPolicy = StrictMode.getThreadPolicy();
        // temporary

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.xxx");   // TBD:..

//        if(heartBeat == null) {
//            heartBeat = new Timer();
//        }
//        startHeartBeat();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i("Received start id " + startId + ": " + intent);
        onServiceStart();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // ????
        onServiceStart();
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        // ???
        if(heartBeat != null) {
            heartBeat.cancel();
            heartBeat = null;
        }
        onServiceStop();
        super.onDestroy();
    }



    // Service state handlers.
    // ....

    private boolean onServiceStart()
    {
        Log.d("onServiceStart() called.");

        // temporary
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(defaultThreadPolicy).permitAll().build());
        // temporary

        // Publish live card...
        publishCard(this);
        if(heartBeat == null) {
            heartBeat = new Timer();
        }
        startHeartBeat();

        return true;
    }

    private boolean onServicePause()
    {
        Log.d("onServicePause() called.");

        // temporary
        if(defaultThreadPolicy != null) {
            StrictMode.setThreadPolicy(defaultThreadPolicy);
        }
        // temporary

        return true;
    }
    private boolean onServiceResume()
    {
        Log.d("onServiceResume() called.");

        // temporary
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(defaultThreadPolicy).permitAll().build());
        // temporary

        return true;
    }

    private boolean onServiceStop()
    {
        Log.d("onServiceStop() called.");

        // temporary
        if(defaultThreadPolicy != null) {
            StrictMode.setThreadPolicy(defaultThreadPolicy);
        }
        // temporary

        // TBD:
        // Unpublish livecard here
        // .....
        unpublishCard(this);
        // ...

        // Stop the heart beat.
        // ???
        // onServiceStop() is called when the service is destroyed.... ??? Need to check
        if(heartBeat != null) {
            heartBeat.cancel();
        }
        // ...

        return true;
    }


    // To be called by iBinder clients...
    public void setPhotoFilePath(String currentPhotoFilePath)
    {
        if(currentPhotoFilePath != null) {
            currentPhotoTime = System.currentTimeMillis();
            if(Log.D) Log.d("currentPhotoFilePath set to " + currentPhotoFilePath + " at " + currentPhotoTime);
            this.previousPhotoFilePath = this.currentPhotoFilePath;
            this.currentPhotoFilePath = currentPhotoFilePath;
        } else {
            if(Log.D) Log.d("Input currentPhotoFilePath = null.");
        }
    }


    // For live cards...

    private void publishCard(Context context)
    {
        Log.d("publishCard() called.");
        // if (liveCard == null || !liveCard.isPublished()) {
        if (liveCard == null) {
            TimelineManager tm = TimelineManager.from(context);
            liveCard = tm.createLiveCard(cardId);
//             liveCard.setNonSilent(false);       // the livecard runs in the "background" only.
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.livecard_camera);
            liveCard.setViews(remoteViews);
            Intent intent = new Intent(context, CamActivity.class);
            liveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
            liveCard.publish(LiveCard.PublishMode.SILENT);
        } else {
            // Card is already published.
            return;
        }
    }
    // This will be called by the "HeartBeat".
    private void updateCard(Context context)
    {
        Log.d("updateCard() called.");
        // if (liveCard == null || !liveCard.isPublished()) {
        if (liveCard == null) {
            // Use the default content.
            publishCard(context);
        } else {
            // Card is already published.

//            // temporary
//            // ????
//            // Without this (if use "republish" below),
//            // we will end up with multiple live cards....
//            liveCard.unpublish();
//            // ...


            // getLiveCard() seems to always publish a new card
            //       contrary to my expectation based on the method name (sort of a creator/factory method).
            // That means, we should not call getLiveCard() again once the card has been published.
//            TimelineManager tm = TimelineManager.from(context);
//            liveCard = tm.createLiveCard(cardId);
//            liveCard.setNonSilent(false);
            // TBD: The reference to remoteViews can be kept in this service as well....
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.livecard_camera);
            String content = "";

            // testing
            long now = System.currentTimeMillis();
            content = "Updated: " + now;
            content += "\nPhoto taken at " + this.currentPhotoTime;
            if(this.currentPhotoFilePath != null) {
                content += "\nPath = " + this.currentPhotoFilePath;
            }
            // ...

            remoteViews.setCharSequence(R.id.livecard_content, "setText", content);


            // temporary
            File extStorageDir = Environment.getExternalStorageDirectory();
            if(extStorageDir != null) {
                if(Log.I) Log.i("extStorageDir = " + extStorageDir);
            } else {
                // ??? Can this happen???
                Log.w("extStorageDir is null.");
            }
            // temporary


            // TBD:
            // Set the image with the "last" photo ...
            // TBD:
            // The photo in the given file path may not have been saved.
            // We will need to use FileObserver or something similar/asynchronous...
            // ...

            Log.w(">>>>>>>>>>>> Before processing image. ");

            if(this.currentPhotoFilePath != null) {
                try {
                    // ????
                    // What the ????
                    String filePath = this.currentPhotoFilePath;
//                    if(this.currentPhotoFilePath.startsWith("/mnt")) {
//                        filePath = this.currentPhotoFilePath.substring("/mnt".length());
//                    }

//                    Uri photoUri = Uri.parse(filePath);   // ???
//                    remoteViews.setImageViewUri(R.id.livecard_image, photoUri);


                    // temporary
                    File photoFile = new File(filePath);
                    if(! photoFile.exists()) {
                        Log.e("File does not exist: filePath = " + filePath);
                    }
                    if(! photoFile.isFile()) {
                        Log.e("Photo image is not a file: filePath = " + filePath);
                    }


                    Log.w(">>>>>>>>>>>> Before reading the bitmap. ");


                    // TBD:
                    // [1] either this...
/* */
                    Bitmap bitmap1 = BitmapFactory.decodeFile(filePath);
                    if(bitmap1 != null) {
                        Bitmap bitmap1r = Bitmap.createScaledBitmap(bitmap1, 320, 180, false);
                        if(Log.D) Log.d("Setting the bitmap in the remote imageView: filePath = " + filePath);
                        remoteViews.setImageViewBitmap(R.id.livecard_image, bitmap1r);
                    } else {
                        if(Log.I) Log.i("Failed to create bitmap from the photo image: filePath = " + filePath);
                    }
/* */

                    // TBD:
                    // [2] or this...
/*
                    FileInputStream fis = new FileInputStream(photoFile);
                    Bitmap bitmap2 = BitmapFactory.decodeStream(fis);
                    if(bitmap2 != null) {
                        Bitmap bitmap2r = Bitmap.createScaledBitmap(bitmap2, 320, 180, false);
                        if(Log.D) Log.d("Setting the bitmap in the remote imageView: filePath = " + filePath);
                        remoteViews.setImageViewBitmap(R.id.livecard_image, bitmap2r);
                    } else {
                        if(Log.I) Log.i("Failed to create bitmap from the file input stream: filePath = " + filePath);
                    }
*/

                    Log.w(">>>>>>>>>>>> After reading the bitmap. ");


                } catch(Exception e) {
                    Log.e("Failed to set the remote imageView.", e);
                }
            } else {
                Log.i("currentPhotoFilePath is not set.");
            }

            Log.w(">>>>>>>>>>>> After processing image. ");

            liveCard.setViews(remoteViews);

            // Do we need to re-publish ???
            // Unfortunately, the view does not refresh without this....
            Intent intent = new Intent(context, CamActivity.class);
            liveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));

//            // temporary
//            liveCard.publish();

            // Is this if() necessary???? or Is it allowed/ok not to call publish() when updating????
            if(! liveCard.isPublished()) {
                liveCard.publish(LiveCard.PublishMode.SILENT);
            } else {
                // ????
                // According to the doc,
                // it appears we should call publish() every time the content changes...
                // But, it seems to work without re-publishing...
                if(Log.D) Log.d("liveCard not published at " + now);
            }
        }
    }

    private void unpublishCard(Context context)
    {
        Log.d("unpublishCard() called.");
        if (liveCard != null) {
            liveCard.unpublish();
            liveCard = null;
        }
    }


    private void startHeartBeat()
    {
        final Handler handler = new Handler();
        TimerTask liveCardUpdateTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            updateCard(CameraDemoLocalService.this);
                        } catch (Exception e) {
                            Log.e("Failed to run the task.", e);
                        }
                    }
                });
            }
        };
        heartBeat.schedule(liveCardUpdateTask, 0L, 15000L); // Every 15 seconds...
    }







}
