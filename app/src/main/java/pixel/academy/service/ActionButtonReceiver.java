package pixel.academy.service;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class is designed to receive broadcast
 * on notification action button click
 */
public class ActionButtonReceiver extends BroadcastReceiver
{

    private static String TAG = "NotificationService";

    /**
     * Call this method when broadcast received
     * @param context Pass application context
     * @param intent Pass Intent
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int notificationId = intent.getExtras().getInt("push.notificationId");
        Log.d(TAG, "Clicked - " + notificationId);
        Log.v(TAG, "Data - " + intent.getExtras().getString("data"));

        String action_time = String.valueOf(System.currentTimeMillis());
        Log.v(TAG, "Action Time - " + action_time);

        /**
         * If action not null
         * Start activity on action button click
         * gotoIntent.setClassName(context, action);
         */
        if(intent.getAction() != null)
        {
            Intent gotoIntent = new Intent(intent.getAction());
            gotoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            gotoIntent.putExtra("data", intent.getExtras().getString("data"));
            context.startActivity(gotoIntent);
        }

        /**
         * if you want cancel notification
         */
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(notificationId);
    }
}