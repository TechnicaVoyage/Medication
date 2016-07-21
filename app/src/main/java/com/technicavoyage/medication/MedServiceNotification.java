package com.technicavoyage.medication;

//import android.app.Notification;
//import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.widget.Toast;

/**
 * Created by TechnicaVoyage on 21-Jul-16.
 */
public class MedServiceNotification extends Service {
    //  private NotificationManager mNM;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }


        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String name = sp.getString("medName", "Medication");
            int hour = sp.getInt("hour", 22);
            int minutes = sp.getInt("minutes", 0);
            int durationV = sp.getInt("durationV", 1);
            int dur = sp.getInt("dur", 0);
            int cdrV = sp.getInt("cdrV", 1);
            int cd = sp.getInt("cd", 0);
            if (dur != durationV && cd != cdrV) {       //for 21st day: Dur==durationV, for cd: cd==cdrV,
                cd = 0;                                                     //for 1st day: durationV!=1
                Intent alarmFinal = new Intent(AlarmClock.ACTION_SET_ALARM);
                alarmFinal.putExtra(AlarmClock.EXTRA_HOUR, hour);
                alarmFinal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                alarmFinal.putExtra(AlarmClock.EXTRA_MESSAGE, "Take " + name);
                alarmFinal.putExtra(AlarmClock.EXTRA_MINUTES, minutes);
                alarmFinal.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                startActivity(alarmFinal);
                dur++;
                Toast.makeText(getApplicationContext(), "AlarmSet! "+dur, Toast.LENGTH_SHORT).show();
                //showNotification(dur);
            } else {
                cd++;
                if (cd == cdrV) {
                    dur = 0;
                    cd = 0;
                }
            }
            SharedPreferences.Editor e = sp.edit();
            e.putInt("cd", cd);
            e.putInt("dur", dur);
            e.apply();
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
        }
    }


    public class LocalBinder extends Binder {
        MedServiceNotification getService() {
            return MedServiceNotification.this;
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_DEFAULT);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        //    mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    //private void showNotification(int dur) {
    // The PendingIntent to launch our activity if the user selects this notification
    //PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
    // Set the info for the views that show in the notification panel.
        /*Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText("Alarm Set! " + dur)// the status text
                .setContentTitle("Medication")  // the label of the entry.setContentText("text")  // the contents of the entry
                .setContentIntent(contentIntent).build();  // The intent to send when the entry is clicked
        // Send the notification.
        mNM.notify(7, notification);*/
    //}
}

