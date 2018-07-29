package pixel.academy.api;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import pixel.academy.R;
import pixel.academy.app.MyApplication;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.session.SessionManager;


import static pixel.academy.app.Global.ADDRESS;
import static pixel.academy.app.Global.ERROR_TAG;
import static pixel.academy.app.Global.JSON_TAG;
import static pixel.academy.app.Global.LATITUDE;
import static pixel.academy.app.Global.LONGITUDE;
import static pixel.academy.app.Global.MAX_RETRIES;
import static pixel.academy.app.Global.MOBILE_NUMBER;
import static pixel.academy.app.Global.RECIPIENT_ID;
import static pixel.academy.app.Global.RESPONSE_TAG;
import static pixel.academy.app.Global.SENDER_ID;
import static pixel.academy.app.Global.TIMEOUT;
import static pixel.academy.app.Global.TUTOR_ID;
import static pixel.academy.app.Global.USER_ID;
import static pixel.academy.app.Global.USER_NAME;
import static pixel.academy.app.MyApplication.getInstance;
import static pixel.academy.configuration.Configuration.API_URL;


public class PingTutor
{

	private OnTaskCompleted listener;
	private ProgressDialog pDialog;
	private Context context;

	public PingTutor(Context context , OnTaskCompleted listener)
	{
		this.listener = listener;
		this.context = context;
		this.pDialog = new ProgressDialog(context);
	}

	private void initProgressDialog(String message)
	{

		pDialog.setMessage(message);
		pDialog.setCancelable(false);
		pDialog.setIndeterminate(false);
		pDialog.show();
	}

	public void execute(final String address_json_data, final int tutor_id, final double latitude, final double longitude)
	{

		//initProgressDialog("Pinging ...");
		/**
		 * Server target URL
		 */
		final String URL = getInstance().getResources().getString(R.string.pixelServerBaseUrl)
				+ getInstance().getResources().getString(R.string.pixelServerPingUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{

				try
				{
					Log.v(RESPONSE_TAG , "" + response);

					/*if(pDialog.isShowing())
					{
						pDialog.dismiss();
					}*/

					JSONObject jsonObj = new JSONObject(response);

					int status_code = jsonObj.getInt("status_code");
					String message = jsonObj.getString("message");

					if (status_code == 200) // checking for error node in json
					{
						/**
						 * Registration Successful
						 */
						listener.onTaskCompleted(true, status_code, message); // Successful
					}

					else
					{
						/**
						 * Ping Unsuccessful
						 */
						listener.onTaskCompleted(false, 500, message);
					}
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
				/*if(pDialog.isShowing())
				{
					pDialog.dismiss();
				}*/

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

				try
				{
					JSONObject jsonObject = new JSONObject();

					//params.put("responseJSON", Security.encrypt(json_data, preferences.getString("key", null)));

					HashMap<String, String> user_details = new SessionManager(context).getUserDetails();

					jsonObject.put(SENDER_ID, user_details.get(USER_ID));
					jsonObject.put(USER_NAME, user_details.get(USER_NAME));
					jsonObject.put(MOBILE_NUMBER, user_details.get(MOBILE_NUMBER));
					jsonObject.put(ADDRESS, address_json_data);
					jsonObject.put(LATITUDE, latitude);
					jsonObject.put(LONGITUDE, longitude);
					jsonObject.put(RECIPIENT_ID, String.valueOf(tutor_id));

					params.put(JSON_TAG, jsonObject.toString());
				}

				catch (Exception e)
				{
					e.printStackTrace();
				}

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