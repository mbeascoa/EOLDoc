package com.beastek.eol.Extras.broadcast_receiver;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.beastek.eol.R;
import com.beastek.eol.ui.patient.AlarmActivity;

import java.util.ArrayList;
import java.util.Random;


public class RingtonePlayingService extends Service {

    private MediaPlayer media_song;
    private int startId;
    private boolean isRunning;
    public final static String CHANNEL_ID = "EOL notifications";
    private NotificationManager notify_manager;
    public final static String NOTIFICATION_CHANNEL_ID = "4655";


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);


        String state = intent.getExtras().getString("extra");
        Integer alarm_sound_choice = intent.getExtras().getInt("sound_choice");

        Log.e("Ringtone extra is ", state);
        Log.e("Alarm choice is ", alarm_sound_choice.toString());


        /*
        // Create the NotificationChannel, but only on API 26+ because

        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Entities Online Channel" ;
            String description = "This is the Entities Online Channel for communication purposes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notify_manager = getSystemService(NotificationManager.class);

            notify_manager.createNotificationChannel(channel);
        } else {
            notify_manager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
        }
        */


        Intent intent_alarm_activity = new Intent(getApplicationContext(), AlarmActivity.class);
        intent_alarm_activity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pending_intent_alarm_activity = PendingIntent.getActivity(getApplicationContext(), 0,
                intent_alarm_activity, PendingIntent.FLAG_ONE_SHOT);


        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_action_call)
                .setContentTitle("Urgente !!!")
                .setContentText("Recuerda la cita")
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setSound(null)
                .setContentIntent(pending_intent_alarm_activity)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setTicker("Nueva notificación")
                .setContentInfo("nuevo");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "nuevo", NotificationManager.IMPORTANCE_HIGH);
            nc.setDescription("Notificación EOL");
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }


        Random random = new Random();
        int idNotify = random.nextInt(8000);

        assert nm != null;
        nm.notify(idNotify, builder.build());







/*
        //when notification is triggered then we go to AlarmActivity.class in order to Switch OFF the alarm
        Notification notification_popup = new Notification.Builder(this)
                .setContentTitle("Reminder Alert!")
                .setContentText("You have a reminder set")
                .setSmallIcon(R.drawable.ic_action_call)
                .setContentIntent(pending_intent_alarm_activity)
                .setAutoCancel(true)
                .build();

 */

        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                Log.e("Start ID is ", state);
                break;
            default:
                startId = 0;
                break;
        }


        if (!this.isRunning && startId == 1) {
            Log.i("there is no music, ", "and you want start");

            this.isRunning = true;
            this.startId = 0;

            //notify_manager.(0, notification_popup);
            //this notification pushes to the top of the phone, with a sound notification, apart from the alarm sound
            nm.notify(idNotify, builder.build());



            if (alarm_sound_choice == 0) {

                int minimum_number = 1;
                int maximum_number = 13;

                Random random_number = new Random();
                int alarm_number = random_number.nextInt(maximum_number + minimum_number);
                Log.i("random number is ", String.valueOf(alarm_number));


                if (alarm_number == 1) {
                    media_song = MediaPlayer.create(this, R.raw.alarm1);
                    media_song.start();
                } else if (alarm_number == 2) {

                    media_song = MediaPlayer.create(this, R.raw.alarm2);
                    media_song.start();
                } else {
                    media_song = MediaPlayer.create(this, R.raw.alarm3);
                    media_song.start();
                }


            } else if (alarm_sound_choice == 1) {
                media_song = MediaPlayer.create(this, R.raw.alarm1);

                media_song.start();
            } else if (alarm_sound_choice == 2) {
                media_song = MediaPlayer.create(this, R.raw.alarm2);
                media_song.start();
            } else {
                media_song = MediaPlayer.create(this, R.raw.alarm3);
                media_song.start();
            }
        } else if (this.isRunning && startId == 0) {
            Log.e("there is music, ", "and you want end");

            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;
        } else if (!this.isRunning && startId == 0) {
            Log.e("there is no music, ", "and you want end");

            this.isRunning = false;
            this.startId = 0;

        } else if (this.isRunning && startId == 1) {
            Log.e("there is music, ", "and you want start");

            this.isRunning = true;
            this.startId = 1;

        } else {
            Log.e("else ", "somehow you reached this");

        }

        return START_NOT_STICKY;
    }




    @Override
    public void onDestroy() {
        Log.e("on Destroy called", "ta da");

        super.onDestroy();
        this.isRunning = false;
    }


}

