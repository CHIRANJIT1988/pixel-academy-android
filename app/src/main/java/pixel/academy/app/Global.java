package pixel.academy.app;

/**
 * Created by CHIRANJIT on 12/6/2016.
 */

public class Global
{
    /**
     * Application Tag
     */
    public static final String JSON_TAG = "responseJSON";
    public static final String ERROR_TAG = "json_error";
    public static final String RESPONSE_TAG = "json_response";
    public static final String CONNECTIVITY_ERROR = "Internet Connection Failure";

    /**
     * Retry Policy
     */
    public static final int MAX_RETRIES = 5;
    public static final int TIMEOUT = 3000;

    /**
     * Preference and JSON Tag Name
     */
    public static final String PREF = "pixel.tutor.pref";
    public static final String TOKEN = "push_token";
    public static final String FIRST_RUN = "first_run";
    public static final String KEY = "key";

    public static final String ADDRESS_DETAILS = "address_details";

    public static final String USER_ID = "user_id";
    public static final String USER = "user";
    public static final String SENDER_ID = "sender_id";
    public static final String RECIPIENT_ID = "recipient_id";
    public static final String TUTOR_ID = "tutor_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_TYPE = "user_type";
    public static final String MOBILE_NUMBER = "mobile_no";
    public static final String ADDRESS = "address";
    public static final String LOCALITY = "locality";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String PINCODE = "pincode";
    public static final String LATITUDE = "longitude";
    public static final String LONGITUDE = "latitude";
    public static final String DEVICE_ID = "device_id";
    public static final String STATUS_CODE = "status_code";
    public static final String MESSAGE = "message";
}
