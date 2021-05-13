package it.benche.upofood.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import it.benche.upofood.message.MessageListActivity

class MyFirebaseMessaging: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var sented: String? = remoteMessage.data.get("sented")

        var firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null && sented == firebaseUser.uid) {
            sendNotification(remoteMessage)
        }
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        var user = remoteMessage.data.get("user")
        var icon = remoteMessage.data.get("icon")
        var title = remoteMessage.data.get("title")
        var body = remoteMessage.data.get("body")

        var notification: RemoteMessage.Notification? = remoteMessage.notification

        var j: Int = Integer.parseInt(user?.replace("[\\D]", ""))
        var intent = Intent(this, MessageListActivity::class.java)
        var bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        var pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)
        var defaultSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(Integer.parseInt(icon))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if(j > 0) {
            i = j
        }

        notificationManager.notify(i, builder.build())
    }
}