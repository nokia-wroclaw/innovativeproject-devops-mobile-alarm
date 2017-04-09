package pwr.android_app.network.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import pwr.android_app.R;
import pwr.android_app.view.activities.MainActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)                                                                // Make this notification automatically dismissed when the user touches it.
                .setShowWhen(true)                                                                  // Show the timestamp
                .setVibrate(new long[] { 1000, 1000 })                                              // Set vibrations
                .setLights(Color.YELLOW, 500, 3000)                                                 // Turn on LED
                .setContentTitle(remoteMessage.getNotification().getTitle())                        // Set the first line of text (TITLE) in the platform notification template.
                .setContentText(remoteMessage.getNotification().getBody())                          // Set the second line of text (MESSAGE TEXT) in the platform notification template.
                .setSmallIcon(R.drawable.icon)                                                      // Set the small icon resource, which will be used to represent the notification in the status bar.
                .setColor(getColor(R.color.dark_grey))                                              // Set the background, where the small icon is
                .setContentIntent(pendingIntent);                                                   // Supply a PendingIntent to be sent when the notification is clicked.
                                                                                                    // [source: https://developer.android.com/reference/android/app/Notification.Builder.html]
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}
