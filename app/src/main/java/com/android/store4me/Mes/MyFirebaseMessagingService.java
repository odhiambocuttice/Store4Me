package com.android.store4me.Mes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.store4me.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String MessTitle =remoteMessage.getNotification().getTitle();
        String MessBody =remoteMessage.getNotification().getBody();
        String click_action =remoteMessage.getNotification().getClickAction();
        String Messmessage =remoteMessage.getData().get("message");
        String MessFrom =remoteMessage.getData().get("from_id");
        String RequestID =remoteMessage.getData().get("request");

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(MessTitle)
                .setContentText(MessBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(click_action);
        intent.putExtra("message", Messmessage);
        intent.putExtra("from_id", MessFrom);
        intent.putExtra("request", RequestID);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);

       int notificationId = (int) System.currentTimeMillis();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());



    }


}
