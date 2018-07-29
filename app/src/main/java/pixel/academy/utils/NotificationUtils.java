package pixel.academy.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pixel.academy.service.ActionButtonReceiver;
import pixel.academy.service.NotificationActionReceiver;
import pixel.academy.service.NotificationDeleteReceiver;

import static pixel.academy.app.MyApplication.getInstance;

/**
 * This class is designed to initialize notification object
 * Create notification style based on downstream message
 * Notify User
 * Send notification user behaviour to the server
 */

public class NotificationUtils
{

    private static String TAG = "NotificationUtils";

    /**
     * Declare NotificationUtils class variables
     */
    public int notification_id;
    private static int count;
    public String received_time, action_time, action;
    private String title, body, color, icon, deep_link;
    private String big_title, big_text, summary_text, large_icon, remote_picture, data;
    private String action_name;
    private boolean vibrate;

    /**
     * Array list to store notification action buttons
     */
    private List<NotificationUtils> action_list = new ArrayList<>();

    /**
     * Call constructor
     * @param notification_id - Notification unique id
     * @param received_time - Notification receive time
     */
    private NotificationUtils(int notification_id, String received_time)
    {
        this.notification_id = notification_id;
        this.received_time = received_time;
    }

    /**
     * Call constructor
     * @param notification_id - Notification unique id
     * @param action_time - Notification action time
     * @param action - Notification action
     */
    public NotificationUtils(int notification_id, String action_time, String action)
    {
        this.notification_id = notification_id;
        this.action_time = action_time;
        this.action = action;
    }

    /**
     * Call constructor
     * @param title - Notification title
     * @param body - Notification body
     * @param color - Notification icon background color
     * @param icon - Notification small icon
     * @param vibrate - Notification vibration
     * @param deep_link - Notification action link to activity
     * @param data - Pass data to activity on click
     * @param large_icon - Notification large icon URL/Resource
     * @param action_list - Notification action button array list
     */
    private NotificationUtils(String title, String body, String color, String icon, boolean vibrate,
                              String deep_link, String data, String large_icon, List<NotificationUtils> action_list)
    {
        this.title          = title;
        this.body           = body;
        this.color          = color;
        this.icon           = icon;
        this.vibrate        = vibrate;
        this.deep_link      = deep_link;
        this.data           = data;
        this.large_icon     = large_icon;
        this.action_list    = action_list;
    }

    /**
     * Call constructor
     * @param title - Notification title
     * @param body - Notification body
     * @param color - Notification icon background color
     * @param icon - Notification small icon
     * @param vibrate - Notification vibration
     * @param deep_link - Notification action link to activity
     * @param data - Pass data to activity on click
     * @param large_icon - Notification large icon URL/Resource
     * @param big_title - Notification big title
     * @param big_text - Big notification text
     * @param summary_text - Notification summery text
     * @param remote_picture - Picture notification URL/Resource
     * @param action_list - Notification action button array list
     */
    private NotificationUtils(String title, String body, String color, String icon, boolean vibrate,
                              String deep_link, String data, String large_icon, String big_title, String big_text,
                              String summary_text, String remote_picture, List<NotificationUtils> action_list)
    {
        this.title          = title;
        this.body           = body;
        this.color          = color;
        this.icon           = icon;
        this.vibrate        = vibrate;
        this.deep_link      = deep_link;
        this.data           = data;
        this.large_icon     = large_icon;
        this.big_title      = big_title;
        this.big_text       = big_text;
        this.summary_text   = summary_text;
        this.remote_picture = remote_picture;
        this.action_list    = action_list;
    }

    /**
     * Call constructor
     * @param action_name - Notification action button name
     * @param icon - Notification action button icon
     * @param deep_link - Notification action click target activity
     * @param data - Pass data to activity on action button click
     */
    private NotificationUtils(String action_name, String icon, String deep_link, String data)
    {
        this.action_name    = action_name;
        this.icon           = icon;
        this.deep_link      = deep_link;
        this.data           = data;
    }

    /**
     * Call to to this method to notify user
     * @param mContext - Pass application context
     * @param remoteMessage - Pass notification downstream data sent from server
     */
    public static void notify(final Context mContext, final RemoteMessage remoteMessage)
    {
        count ++;
        int notificationId = (int) (Math.random() * 100);
        String received_time = String.valueOf(System.currentTimeMillis());

        Map<String, String> map = remoteMessage.getData();

        String title    = /*remoteMessage.getNotification().getTitle();*/ map.get("title");
        String body     = /*remoteMessage.getNotification().getBody();*/ map.get("body");
        String color    = /*remoteMessage.getNotification().getColor();*/ map.get("color");
        String icon     = /*remoteMessage.getNotification().getIcon();*/ map.get("icon");

        String large_icon       = map.get("large_icon");
        String action_list      = map.get("b_action");
        String big_title        = map.get("big_title");
        String big_text         = map.get("big_text");
        String summary_text     = map.get("summary_text");
        String remote_picture   = map.get("pic_url");
        String deep_link        = map.get("deep_link");
        String data             = map.get("data");
        boolean vibrate         = Boolean.valueOf(map.get("vibrate"));

        /**
         * Check if notification contains picture
         */
        if(map.containsKey("pic_url"))
        {
            NotificationUtils utils = new NotificationUtils(title, body, color, icon, vibrate, deep_link, data, large_icon, big_title,
                    big_text, summary_text, remote_picture, getActionButton(action_list));
            /**
             *  Call picture notification method
             */
            big_picture_notification(mContext, notificationId, utils);
        }

        /**
         * Check if notification contains big text
         */
        else if(map.containsKey("big_text"))
        {
            NotificationUtils utils = new NotificationUtils(title, body, color, icon, vibrate, deep_link, data, large_icon, big_title,
                    big_text, summary_text, remote_picture, getActionButton(action_list));
            /**
             * Call big text notification method
             */
            big_text_notification(mContext, notificationId, utils);
        }

        /**
         * If notification contains picture or big text
         */
        else
        {
            /**
             * Call default notification method
             */
            NotificationUtils utils = new NotificationUtils(title, body, color, icon, vibrate, deep_link, data, large_icon, getActionButton(action_list));
            small_notification(mContext, notificationId, utils);
        }
    }

    /**
     *
     * @param mContext - Pass application context
     * @param notificationId - Pass notification id
     * @param utils - Pass notification object
     */
    private static void small_notification(final Context mContext, final int notificationId, NotificationUtils utils)
    {

        /** Start activity when user taps on notification.
        * Intent gotoIntent = new Intent(this, NotificationDeleteReceiver.class);
        * gotoIntent.setClassName(mContext, "convertifier.sample.MainActivity");
        * PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        */

        /**
         * Send to Broadcast for Notification Click
         */
        Intent gotoIntent = new Intent(mContext, NotificationActionReceiver.class);
        gotoIntent.setAction(utils.deep_link);
        gotoIntent.putExtra("data", utils.data);
        gotoIntent.putExtra("push.notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        final long when = System.currentTimeMillis();

        notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        /**
         * Set notification property
         */
        Notification notification;
        notificationBuilder
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(utils.title)
                .setContentText(utils.body)
                .setContentIntent(contentIntent)
                .setNumber(count);

        /**
         * .setContentInfo("xyz")
         * .setSmallIcon(icon)
         * .setTicker("ticker")
         */

        try
        {
            /**
             * If notification object contains color
             */
            if(utils.color != null)
            {
                notificationBuilder.setColor(Color.parseColor(utils.color));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            /**
             * Check if notification contains small icon set from resource
             */
            if(utils.icon != null)
            {
                final int small_icon = getInstance().getResources().getIdentifier(utils.icon, "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }

            /**
             * Else set default notification small icon from resource
             */
            else
            {
                final int small_icon = getInstance().getResources().getIdentifier("ic_bell", "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*try
        {
            //final int sound = getInstance().getResources().getIdentifier("sound", "raw", getInstance().getPackageName());
            final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //Uri uri = Uri.parse("android.resource://" + getInstance().getPackageName() + "/" + sound);
            notificationBuilder.setSound(defaultSoundUri);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        /**
         * If notification contains large icon
         */
        if(utils.large_icon != null)
        {

            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.large_icon);
                Bitmap large_icon = BitmapFactory.decodeStream((InputStream) url.getContent());
                notificationBuilder.setLargeIcon(large_icon);
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int resID = getInstance().getResources().getIdentifier(utils.large_icon, "drawable", getInstance().getPackageName());
                Bitmap largeIcon = BitmapFactory.decodeResource(getInstance().getResources(), resID);
                notificationBuilder.setLargeIcon(largeIcon);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Yes intent
         * Intent yesReceive = new Intent(this, NotificationActionReceiver.class);
         * yesReceive.setAction(YES_ACTION);
         * PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Maybe intent
         * Intent maybeReceive = new Intent(this, NotificationActionReceiver.class);
         * maybeReceive.setAction(MAYBE_ACTION);
         * PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12345, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * No intent
         * Intent noReceive = new Intent(this, NotificationActionReceiver.class);
         * noReceive.setAction("convertifier.sample.SecondActivity");
         * PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * .setNumber(++count);
         * .setTicker("Reload.in")
         * .setStyle(new NotificationCompat.BigTextStyle().bigText("big text"))
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Yes", pendingIntentYes)
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Partly", pendingIntentMaybe) * .addAction(R.drawable.ic_alarm_check_black_18dp, "No", pendingIntentNo)
         * .build();
         */

        /**
         * Dynamic Action Button
         */
        for(NotificationUtils nUtils: utils.action_list)
        {
            /**
             * Send to Broadcast for Action Button Click
             */
            Intent aIntent = new Intent(mContext, ActionButtonReceiver.class);
            aIntent.setAction(nUtils.deep_link);
            aIntent.putExtra("data", nUtils.data);
            aIntent.putExtra("push.notificationId", notificationId);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, notificationId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            /**
             * Set action button icon from resource and add to notification
             */
            int resID = getInstance().getResources().getIdentifier(nUtils.icon, "drawable", getInstance().getPackageName());
            notificationBuilder.addAction(resID, nUtils.action_name, pIntent);
        }

        /**
         * Send to Broadcast for Delete
         * Add notificationBuilder.setDeleteIntent(contentIntent);
         */
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(mContext, notificationId));
        notification = notificationBuilder.build();

        /**
         * This will cancel notification on swipe.
         */
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        /**
         * Vibration is set to true vibrate default on notification
         */
        if(utils.vibrate)
        {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        /**
         * Finally notify user
         */
        notificationManager.notify(notificationId, notification);
    }


    private static void big_text_notification(final Context mContext, final int notificationId, NotificationUtils utils)
    {

        /** Start activity when user taps on notification.
         * Intent gotoIntent = new Intent(this, NotificationDeleteReceiver.class);
         * gotoIntent.setClassName(mContext, "convertifier.sample.MainActivity");
         * PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Send to Broadcast for Notification Click
         */
        Intent gotoIntent = new Intent(mContext, NotificationActionReceiver.class);
        gotoIntent.setAction(utils.deep_link);
        gotoIntent.putExtra("data", utils.data);
        gotoIntent.putExtra("push.notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        final long when = System.currentTimeMillis();

        notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        /**
         * Set notification property
         */
        Notification notification;
        notificationBuilder
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(utils.title)
                .setContentText(utils.body)
                .setNumber(count)
                .setContentIntent(contentIntent);

        /**
         * Set notification big text style
         */
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(utils.big_text)
                .setBigContentTitle(utils.big_title)
                .setSummaryText(utils.summary_text));

        /**
         * .setContentInfo("xyz")
         * .setSmallIcon(icon)
         * .setTicker("ticker")
         */

        try
        {
            /**
             * If notification object contains color
             */
            if(utils.color != null)
            {
                notificationBuilder.setColor(Color.parseColor(utils.color));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            /**
             * Check if notification contains small icon set from resource
             */
            if(utils.icon != null)
            {
                final int small_icon = getInstance().getResources().getIdentifier(utils.icon, "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }

            /**
             * Else set default notification small icon from resource
             */
            else
            {
                final int small_icon = getInstance().getResources().getIdentifier("ic_bell", "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*try
        {
            final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Uri uri = Uri.parse("android.resource://" + getInstance().getPackageName() + "/" + R.raw.icon);
            notificationBuilder.setSound(uri);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        /**
         * If notification contains large icon
         */
        if(utils.large_icon != null)
        {

            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.large_icon);
                Bitmap large_icon = BitmapFactory.decodeStream((InputStream) url.getContent());
                notificationBuilder.setLargeIcon(large_icon);
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int resID = getInstance().getResources().getIdentifier(utils.large_icon, "drawable", getInstance().getPackageName());
                Bitmap largeIcon = BitmapFactory.decodeResource(getInstance().getResources(), resID);
                notificationBuilder.setLargeIcon(largeIcon);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Yes intent
         * Intent yesReceive = new Intent(this, NotificationActionReceiver.class);
         * yesReceive.setAction(YES_ACTION);
         * PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Maybe intent
         * Intent maybeReceive = new Intent(this, NotificationActionReceiver.class);
         * maybeReceive.setAction(MAYBE_ACTION);
         * PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12345, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * No intent
         * Intent noReceive = new Intent(this, NotificationActionReceiver.class);
         * noReceive.setAction("convertifier.sample.SecondActivity");
         * PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * .setNumber(++count);
         * .setTicker("Reload.in")
         * .setStyle(new NotificationCompat.BigTextStyle().bigText("big text"))
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Yes", pendingIntentYes)
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Partly", pendingIntentMaybe) * .addAction(R.drawable.ic_alarm_check_black_18dp, "No", pendingIntentNo)
         * .build();
         */

        /**
         * Dynamic Action Button
         */
        for(NotificationUtils nUtils: utils.action_list)
        {
            /**
             * Send to broadcast for action button click
             */
            Intent aIntent = new Intent(mContext, ActionButtonReceiver.class);
            aIntent.setAction(nUtils.deep_link);
            aIntent.putExtra("data", nUtils.data);
            aIntent.putExtra("push.notificationId", notificationId);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, notificationId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            /**
             * Set action button icon from resource and add to notification
             */
            int resID = getInstance().getResources().getIdentifier(nUtils.icon, "drawable", getInstance().getPackageName());
            notificationBuilder.addAction(resID, nUtils.action_name, pIntent);
        }

        /**
         * Send to Broadcast for Delete
         * Add notificationBuilder.setDeleteIntent(contentIntent);
         */
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(mContext, notificationId));
        notification = notificationBuilder.build();

        /**
         * This will cancel notification on swipe.
         */
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        /**
         * Vibration is set to true vibrate default on notification
         */
        if(utils.vibrate)
        {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        /**
         * Finally notify user
         */
        notificationManager.notify(notificationId, notification);
    }


    private static void big_picture_notification(final Context mContext, final int notificationId, NotificationUtils utils)
    {

        /** Start activity when user taps on notification.
         * Intent gotoIntent = new Intent(this, NotificationDeleteReceiver.class);
         * gotoIntent.setClassName(mContext, "convertifier.sample.MainActivity");
         * PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Send to Broadcast for Notification Click
         */
        Intent gotoIntent = new Intent(mContext, NotificationActionReceiver.class);
        gotoIntent.setAction(utils.deep_link);
        gotoIntent.putExtra("data", utils.data);
        gotoIntent.putExtra("push.notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        final long when = System.currentTimeMillis();

        notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        /**
         * Set notification property
         */
        Notification notification;
        notificationBuilder
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(utils.title)
                .setContentText(utils.body)
                .setNumber(count)
                .setContentIntent(contentIntent);

        /**
         * .setContentInfo("xyz")
         * .setSmallIcon(icon)
         * .setTicker("ticker")
         */

        try
        {
            /**
             * If notification object contains color
             */
            if(utils.color != null)
            {
                notificationBuilder.setColor(Color.parseColor(utils.color));
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            /**
             * Check if notification contains small icon set from resource
             */
            if(utils.icon != null)
            {
                final int small_icon = getInstance().getResources().getIdentifier(utils.icon, "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }

            /**
             * Else set default notification small icon from resource
             */
            else
            {
                final int small_icon = getInstance().getResources().getIdentifier("ic_bell", "drawable", getInstance().getPackageName());
                notificationBuilder.setSmallIcon(small_icon);
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*try
        {
            final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Uri uri = Uri.parse("android.resource://" + getInstance().getPackageName() + "/" + R.raw.icon);
            notificationBuilder.setSound(uri);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }*/

        /**
         * If notification contains large icon
         */
        if(utils.large_icon != null)
        {

            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.large_icon);
                Bitmap large_icon = BitmapFactory.decodeStream((InputStream) url.getContent());
                notificationBuilder.setLargeIcon(large_icon);
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int resID = getInstance().getResources().getIdentifier(utils.large_icon, "drawable", getInstance().getPackageName());
                Bitmap largeIcon = BitmapFactory.decodeResource(getInstance().getResources(), resID);
                notificationBuilder.setLargeIcon(largeIcon);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * If notification contains picture
         */
        if(utils.remote_picture != null)
        {

            try
            {
                /**
                 * Check if large icon is URL - Set URL icon
                 */
                URL url = new URL(utils.remote_picture);
                Bitmap remote_picture = BitmapFactory.decodeStream((InputStream) url.getContent());

                /**
                 * Set picture notification style
                 */
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(remote_picture)
                        .setBigContentTitle(utils.big_title)
                        .setSummaryText(utils.summary_text));
            }

            catch (MalformedURLException e)
            {
                /**
                 * If invalid URL set large icon from resource
                 */
                int res_id = getInstance().getResources().getIdentifier(utils.remote_picture, "drawable", getInstance().getPackageName());
                Bitmap remote_picture = BitmapFactory.decodeResource(getInstance().getResources(), res_id);

                /**
                 * Set picture notification style
                 */
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(remote_picture)
                        .setBigContentTitle(utils.big_title)
                        .setSummaryText(utils.summary_text));
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Yes intent
         * Intent yesReceive = new Intent(this, NotificationActionReceiver.class);
         * yesReceive.setAction(YES_ACTION);
         * PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * Maybe intent
         * Intent maybeReceive = new Intent(this, NotificationActionReceiver.class);
         * maybeReceive.setAction(MAYBE_ACTION);
         * PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12345, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * No intent
         * Intent noReceive = new Intent(this, NotificationActionReceiver.class);
         * noReceive.setAction("convertifier.sample.SecondActivity");
         * PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
         */

        /**
         * .setNumber(++count);
         * .setTicker("Reload.in")
         * .setStyle(new NotificationCompat.BigTextStyle().bigText("big text"))
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Yes", pendingIntentYes)
         * .addAction(R.drawable.ic_alarm_check_black_18dp, "Partly", pendingIntentMaybe) * .addAction(R.drawable.ic_alarm_check_black_18dp, "No", pendingIntentNo)
         * .build();
         */

        /**
         * Dynamic Action Button
         */
        for(NotificationUtils nUtils: utils.action_list)
        {
            /**
             * Send to Broadcast for Action Button Click
             */
            Intent aIntent = new Intent(mContext, ActionButtonReceiver.class);
            aIntent.setAction(nUtils.deep_link);
            aIntent.putExtra("data", nUtils.data);
            aIntent.putExtra("push.notificationId", notificationId);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, notificationId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            /**
             * Set action button icon from resource and add to notification
             */
            int resID = getInstance().getResources().getIdentifier(nUtils.icon, "drawable", getInstance().getPackageName());
            notificationBuilder.addAction(resID, nUtils.action_name, pIntent);
        }

        /**
         * Send to Broadcast for Delete
         * Add notificationBuilder.setDeleteIntent(contentIntent);
         */
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(mContext, notificationId));
        notification = notificationBuilder.build();

        /**
         * This will cancel notification on swipe.
         */
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        /**
         * Vibration is set to true vibrate default on notification
         */
        if(utils.vibrate)
        {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        /**
         * Finally notify user
         */
        notificationManager.notify(notificationId, notification);
    }


    /*public static void inbox_style_notification(final Context mContext, Intent intent, String notification_body)
    {

        String received_time = String.valueOf(System.currentTimeMillis());
        Log.v(TAG, "Received Time - " + received_time);
        int notificationId = (int) (Math.random() * 100);


        //Start activity when user taps on notification.
        //Intent gotoIntent = new Intent(this, NotificationDeleteReceiver.class);
        //gotoIntent.setClassName(mContext, "convertifier.sample.MainActivity");
        //PendingIntent contentIntent = PendingIntent.getActivity(mContext, notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //Send to Broadcast for Notification Click
        Intent gotoIntent = new Intent(mContext, NotificationActionReceiver.class);
        gotoIntent.setAction("convertifier.sample.MainActivity");
        gotoIntent.putExtra("push.notificationId", notificationId);
        PendingIntent contentIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), notificationId, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;

        final long when = System.currentTimeMillis();
        final int icon = R.drawable.icon;
        final long[] pattern = { 500, 500, 500 };
        final Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        //Log.v("TAG_IMAGE", "" + intent.getExtras().getString("message"));
        //Log.v("TAG_IMAGE", "" + intent.getExtras().getString("imageurl"));

        notificationBuilder = new NotificationCompat.Builder(mContext);
        notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification;
        notificationBuilder
                .setSmallIcon(icon)
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle("New Message !!")
                .setContentText(notification_body)
                .setContentIntent(contentIntent);

        notificationBuilder.setStyle(new NotificationCompat.InboxStyle()
                .setBigContentTitle("Big Text Notification")
                .addLine("Message 1.")
                .addLine("Message 2.")
                .addLine("Message 3.")
                .addLine("Message 4.")
                .addLine("Message 5.")
                .setSummaryText("+2 more"));

        if(false)
        {
            notificationBuilder.setVibrate(pattern);
        }

        if(true)
        {
            notificationBuilder.setSound(defaultSoundUri);
        }


        if(true)
        {

            Bitmap remote_picture;

            try
            {
                remote_picture = BitmapFactory.decodeStream((InputStream) new URL("http://image.slidesharecdn.com/advertising-ppt-1234276542503994-1/95/advertising-ppt-1-728.jpg?cb=1234255226").getContent());
                notificationBuilder.setLargeIcon(remote_picture);
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        //Yes intent
        //Intent yesReceive = new Intent(this, NotificationActionReceiver.class);
        //yesReceive.setAction(YES_ACTION);
        //PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        //Maybe intent
        //Intent maybeReceive = new Intent(this, NotificationActionReceiver.class);
        //maybeReceive.setAction(MAYBE_ACTION);
        //PendingIntent pendingIntentMaybe = PendingIntent.getBroadcast(this, 12345, maybeReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        //No intent
        //Intent noReceive = new Intent(this, NotificationActionReceiver.class);
        //noReceive.setAction("convertifier.sample.SecondActivity");
        //PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);

        //.setNumber(++count);
        //.setTicker("Reload.in")
        //.setStyle(new NotificationCompat.BigTextStyle().bigText("dcdddcecececece"*//*intent.getExtras().getString("message")*//*))
        //.addAction(R.drawable.ic_alarm_check_black_18dp, "Yes", pendingIntentYes)
        //.addAction(R.drawable.ic_alarm_check_black_18dp, "Partly", pendingIntentMaybe)
        //.addAction(R.drawable.ic_alarm_check_black_18dp, "No", pendingIntentNo)
        //.build();

        //Dynamic Action Button
        List<PendingIntent> pendingIntentList = new ArrayList<>();
        List<String> goIntent = new ArrayList<>();
        List<String> actionButton = new ArrayList<>();

        goIntent.add("convertifier.sample.SecondActivity");
        goIntent.add("convertifier.sample.MainActivity");
        actionButton.add("YES");
        actionButton.add("NO");

        //Send to Broadcast for for Action Click
        for(int i=0;i<2;i++)
        {
            Intent aIntent = new Intent(mContext, NotificationActionReceiver.class);
            aIntent.setAction(goIntent.get(i));
            aIntent.putExtra("push.notificationId", notificationId);
            PendingIntent pIntent = PendingIntent.getBroadcast(mContext, notificationId, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            pendingIntentList.add(pIntent);
        }

        //Dynamically Add Action Button
        for(int i=0; i<2; i++)
        {
            String mDrawableName = "ic_share_variant_grey600_24dp";
            int resID = getInstance().getResources().getIdentifier(mDrawableName , "drawable", getInstance().getPackageName());

            notificationBuilder.addAction(resID, actionButton.get(i), pendingIntentList.get(i));
        }


        //Send to Broadcast for Delete
        //Add notificationBuilder.setDeleteIntent(contentIntent);
        notificationBuilder.setDeleteIntent(createOnDismissedIntent(mContext, notificationId));
        notification = notificationBuilder.build();

        //This will generate separate notification each time server sends.
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(notificationId, notification);

        new SQLiteHelper(mContext).insert(new NotificationUtils(notificationId, received_time));
    }*/


    /**
     * Create pending intent for delete notification
     * @param context - Pass application context
     * @param notificationId - Pass unique notification id
     * @return - Return dissmiss pending intent
     */
    private static PendingIntent createOnDismissedIntent(Context context, int notificationId)
    {
        Intent intent = new Intent(context, NotificationDeleteReceiver.class);
        intent.putExtra("push.notificationId", notificationId);

        return PendingIntent.getBroadcast(context.getApplicationContext(),
                notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     *
     * @param json - Pass JSON downstream string received from server
     * @return Array of notification action button
     */
    private static List<NotificationUtils> getActionButton(String json)
    {

        try
        {

            List<NotificationUtils> action_list = new ArrayList<>();

            /**
             * If invalid JSON return empty array list
             */
            if (json == null)
            {
                return action_list;
            }

            /**
             * Create JSONArray Object
             */
            JSONArray jsonArray = new JSONArray(json);

            /**
             * Loop through JSON Array
             */
            for (int i=0; i< jsonArray.length(); i++)
            {

                /**
                 * Create JSON Object from JSON Array element
                 */
                JSONObject obj = (JSONObject) jsonArray.get(i);

                String name         = obj.getString("action_name");
                String icon         = obj.getString("icon");
                String deep_link    = null;
                String data         = null;

                if(obj.has("deep_link"))
                {
                    deep_link = obj.getString("deep_link");
                }

                if(obj.has("data"))
                {
                    data = obj.getString("data");
                }

                action_list.add(new NotificationUtils(name, icon, deep_link, data));
            }

            return action_list;
        }

        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Call this method receive for notification topic subscription
     * @param topic - Name of the subscription topic
     */
    public static void subscribe(String topic)
    {
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    /**
     * Call this method receive for notification topic un subscription
     * @param topic - Name of the subscription topic
     */
    public static void unsubscribe(String topic)
    {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }
}