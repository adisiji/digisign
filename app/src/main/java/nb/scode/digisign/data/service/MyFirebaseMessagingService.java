package nb.scode.digisign.data.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.Map;
import nb.scode.digisign.R;
import nb.scode.digisign.view.impl.ReceivedDocActivity;
import timber.log.Timber;

/**
 * Created by neobyte on 5/23/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  // [START receive_message]
  @Override public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    // [START_EXCLUDE]
    // There are two types of messages data messages and notification messages. Data messages are handled
    // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
    // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
    // is in the foreground. When the app is in the background an automatically generated notification is displayed.
    // When the user taps on the notification they are returned to the app. Messages containing both notification
    // and data payloads are treated as notification messages. The Firebase console always sends notification
    // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
    // [END_EXCLUDE]

    // Check if message contains a notification payload.
    if (remoteMessage.getNotification() != null && remoteMessage.getData() != null) {
      Map<String, String> data = remoteMessage.getData();
      Timber.d("onMessageReceived(): size data => " + data.size());

      RemoteMessage.Notification notification = remoteMessage.getNotification();
      String body = remoteMessage.getNotification().getBody();

      Timber.d("onMessageReceived(): Message Notification Body: " + body);
      sendNotification(notification, data);
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
  }
  // [END receive_message]

  /**
   * Create and show a simple notification containing the received FCM message.
   *
   * @param notification contain message notification payload
   * @param data FCM data contain message data payload
   */
  private void sendNotification(@NonNull RemoteMessage.Notification notification,
      @NonNull Map<String, String> data) {
    Intent intent = new Intent(this, ReceivedDocActivity.class);
    String desc = data.get("desc");
    String link = data.get("linkDownload");
    String origin = data.get("origin");
    String senderKey = data.get("senderKey");
    String times = data.get("timestamp");
    String type = data.get("type");

    intent.putExtra("desc", desc);
    intent.putExtra("linkdown", link);
    intent.putExtra("origin", origin);
    intent.putExtra("times", times);
    intent.putExtra("type", type);
    intent.putExtra("senderkey", senderKey);

    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent =
        PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notification.getTitle())
            .setContentText(notification.getBody())
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(new long[] { 500, 1000, 500, 1000 })
            .setContentIntent(pendingIntent);

    NotificationManager notificationManager =
        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
  }
}
