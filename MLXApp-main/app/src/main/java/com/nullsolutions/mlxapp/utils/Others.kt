package com.nullsolutions.mlxapp.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.nullsolutions.mlxapp.ui.SplashActivity

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun sendNotification(context: Context, title: String, body: String) {
    val appActivityIntent = Intent(context, SplashActivity::class.java)
    val contentAppActivityIntent = PendingIntent.getActivity(
        context,
        100,
        appActivityIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification: Notification = NotificationCompat.Builder(context, "NEWS")
//        .setSmallIcon(R.drawable.ic_comment_white)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
        .setContentIntent(contentAppActivityIntent)
        .setAutoCancel(true)
        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
        .setWhen(0)
        .setDefaults(Notification.DEFAULT_ALL)
        .build()
    notificationManager.notify(0, notification)
}