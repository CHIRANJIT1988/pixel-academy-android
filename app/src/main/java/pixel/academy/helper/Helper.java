package pixel.academy.helper;

public class Helper {

    public static String toCamelCase(String inputString) {

        String result = "";

        if (inputString.length() == 0) {
            return result;
        }

        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);

        result = result + firstCharToUpperCase;

        for (int i = 1; i < inputString.length(); i++) {

            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);

            if (previousChar == ' ') {

                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }

        return result;
    }


    public static String formatNumber(int no)
    {

        if(no<10)
        {
            return "0" + no;
        }

        else
        {
            return String.valueOf(no);
        }
    }


    public static String dateTimeFormat(String str)
    {

        String datetime = "";
        String day;

        String[] parts = str.split(" ")[0].split("-");

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int date = Integer.parseInt(parts[2]);

        if(date == 1 || date == 21 || date == 31)
        {
            day = "st";
        }

        else if(date == 2 || date == 22)
        {
            day = "nd";
        }

        else if(date == 3 || date == 23)
        {
            day = "rd";
        }

        else
        {
            day = "th";
        }

        switch (month)
        {

            case 1:

                datetime = date + day + " Jan " + year;
                break;

            case 2:

                datetime = date + day + " Feb " + year;
                break;

            case 3:

                datetime = date + day + " Mar " + year;
                break;

            case 4:

                datetime = date + day + " Apr " + year;
                break;

            case 5:

                datetime = date + day + " May " + year;
                break;

            case 6:

                datetime = date + day + " Jun " + year;
                break;

            case 7:

                datetime = date + day + " Jul " + year;
                break;

            case 8:

                datetime = date + day + " Aug " + year;
                break;

            case 9:

                datetime = date + day + " Sep " + year;
                break;

            case 10:

                datetime = date + day + " Oct " + year;
                break;

            case 11:

                datetime = date + day + " Nov " + year;
                break;

            case 12:

                datetime = date + day + " Dec " + year;
                break;
        }

        return datetime;
    }


    public static long calculateHourDifference(String complain_date)
    {

        //DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date date = format.parse(complain_date);

        try
        {

            return  (System.currentTimeMillis() - Long.valueOf(complain_date)) / (60 * 60 * 1000);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }



    public static String timeFormat(String str)
    {

        String datetime = "";

        try
        {

            String[] time = str.split(" ")[1].split(":");
            return setDateTime(Integer.parseInt(time[0]), Integer.parseInt(time[1]));

        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return datetime;
    }


    public static String setDateTime(int hour, int minute)
    {

        String timeSet;

        if (hour > 12)
        {
            hour -= 12;
            timeSet = "PM";
        }

        else if (hour == 0)
        {
            hour += 12;
            timeSet = "AM";
        }

        else if (hour == 12)
        {
            timeSet = "PM";
        }

        else
        {
            timeSet = "AM";
        }


        String minutes;

        if (minute < 10)
        {
            minutes = "0" + minute;
        }

        else
        {
            minutes = String.valueOf(minute);
        }

        return String.valueOf(hour + ":" + minutes + " " + timeSet);
    }
}