package pixel.academy.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by dell on 18-06-2015.
 */

public class GenerateUniqueId
{


    public static String generateRandomString()
    {

        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();

        StringBuilder sb = new StringBuilder( 50 );

        for( int i = 0; i < 50; i++ )
        {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }

        return (getDate() + getTime() + "_") + sb.toString();
    }


    public static String generateMessageId(String phone)
    {
        return String.valueOf(System.currentTimeMillis() + "-" + phone);
    }


    public static String getDate()
    {

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");

        return dateFormat.format(new Date());
    }


    private static String getTime()
    {

        Calendar calendar = Calendar.getInstance();

        String hour = dayMonthFormat(calendar.get(Calendar.HOUR_OF_DAY));
        String min = dayMonthFormat(calendar.get(Calendar.MINUTE));
        String sec = dayMonthFormat(calendar.get(Calendar.SECOND));

        return new StringBuilder().append(hour).append(min).append(sec).toString();
    }


    public static String dayMonthFormat(int value)
    {

        if(value < 10)
        {
            return String.valueOf("0" + value);
        }

        else
        {
            return String.valueOf(value);
        }
    }


    public static int getRandomNo(int maximum, int minimum)
    {

        Random rn = new Random();

        int range = maximum - minimum + 1;

        return rn.nextInt(range) + minimum;
    }


    public static String generateOrderNumber(int user_id)
    {

        StringBuilder order_number = new StringBuilder().append(getDate()).append(getTime()).append("-");

        if (user_id >= 0 && user_id < 10)
        {
            order_number.append("00000" + user_id);
        }

        else if (user_id >= 10 && user_id < 100)
        {
            order_number.append("0000" + user_id);
        }

        else if (user_id >= 100 && user_id < 1000)
        {
            order_number.append("000" + user_id);
        }

        else if (user_id >= 1000 && user_id < 10000)
        {
            order_number.append("00" + user_id);
        }

        else if (user_id >= 10000 && user_id < 100000)
        {
            order_number.append("0" + user_id);
        }

        else if (user_id >= 100000 && user_id < 1000000)
        {
            order_number.append("" + user_id);
        }

        return order_number.toString();
    }
}
