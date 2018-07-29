package pixel.academy.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pixel.academy.R;
import pixel.academy.app.MyApplication;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.helper.Security;
import pixel.academy.model.User;

import static pixel.academy.app.Global.ERROR_TAG;
import static pixel.academy.app.Global.JSON_TAG;
import static pixel.academy.app.Global.MAX_RETRIES;
import static pixel.academy.app.Global.RESPONSE_TAG;
import static pixel.academy.app.Global.TIMEOUT;
import static pixel.academy.app.MyApplication.getInstance;
import static pixel.academy.configuration.Configuration.SECRET_KEY;


public class OTPVerification
{

	private OnTaskCompleted listener;
	private Context context;
	
	public OTPVerification(Context _context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = _context;
	}

	public void execute(final User user)
	{
		/**
		 * Server target URL
		 */
		final String URL = getInstance().getResources().getString(R.string.pixelServerBaseUrl)
				+ getInstance().getResources().getString(R.string.pixelServerVerificationUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);

					JSONObject jsonObj = new JSONObject(response);

					int error_code = jsonObj.getInt("error_code");
					String message = jsonObj.getString("message");

					if (error_code == 100)
					{
						/**
                         * Successful
						 */
						listener.onTaskCompleted(true, error_code, message);
						return;
					}

					/**
					 *  Unsuccessful
					 */
					listener.onTaskCompleted(false, 500, message);
				}

				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error)
			{
				/**
				 * if (error instanceof TimeoutError)
				 */
				Log.v(ERROR_TAG, "" + error.getMessage());
				listener.onTaskCompleted(false, 500, "Internet connection fail. Try Again");
			}
		})

		{

			@Override
			protected Map<String, String> getParams()
			{

				Map<String, String> params = new HashMap<>();

				params.put("mobile_no", Security.encrypt(user.phone_number, SECRET_KEY));
				params.put("code", Security.encrypt(user.confirmation_code, SECRET_KEY));

				Log.v(JSON_TAG, "" + params);
				return params;
			}
		};

		/**
		 * Retry if Server time out
		 */
		RetryPolicy policy = new DefaultRetryPolicy(TIMEOUT, MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		postRequest.setRetryPolicy(policy);

		/**
		 * Add request to queue
		 */
		MyApplication.getInstance().addToRequestQueue(postRequest);
	}
}