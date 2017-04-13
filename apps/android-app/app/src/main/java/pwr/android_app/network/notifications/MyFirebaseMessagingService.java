package pwr.android_app.network.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import pwr.android_app.R;
import pwr.android_app.view.activities.LoginActivity;
import pwr.android_app.view.activities.MainActivity;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // creating String with notification Title
        int notificationTitleId = getResources().getIdentifier(remoteMessage.getNotification().getTitleLocalizationKey(),"string", getPackageName());
        String[] notificationTitleArgs = remoteMessage.getNotification().getTitleLocalizationArgs();
        String notificationTitle = getResources().getString(notificationTitleId, notificationTitleArgs);

        // creating String with notification Body
        int notificationBodyId = getResources().getIdentifier(remoteMessage.getNotification().getBodyLocalizationKey(), "string", getPackageName());
        String[] notificationBodyArgs = remoteMessage.getNotification().getBodyLocalizationArgs();
        String notificationBody = getResources().getString(notificationBodyId, notificationBodyArgs );

        // preparing icon id
        int notificationIconId = getResources().getIdentifier(remoteMessage.getNotification().getIcon(), "drawable", getPackageName());


        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
					.setAutoCancel(true)
					.setShowWhen(true)
					.setVibrate(new long[] { 1000, 1000, 100 })
					.setLights(Color.YELLOW, 500, 3000)
					.setContentTitle(notificationTitle)
					.setContentText(notificationBody)
					.setSmallIcon(notificationIconId)
					.setColor(getColor(R.color.dark_grey))
					.setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0, builder.build());
    }
}
