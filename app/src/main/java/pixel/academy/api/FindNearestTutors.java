package pixel.academy.api;

import pixel.academy.R;
import pixel.academy.app.MyApplication;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.model.Address;
import pixel.academy.model.Education;
import pixel.academy.model.Occupation;
import pixel.academy.model.Tutor;
import pixel.academy.model.TutorPreference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Comparator;

import static pixel.academy.app.Global.ERROR_TAG;
import static pixel.academy.app.Global.JSON_TAG;
import static pixel.academy.app.Global.MAX_RETRIES;
import static pixel.academy.app.Global.RESPONSE_TAG;
import static pixel.academy.app.Global.TIMEOUT;
import static pixel.academy.app.MyApplication.getInstance;


public class FindNearestTutors
{

	private OnTaskCompleted listener;
	private Context context;

	public FindNearestTutors(Context context , OnTaskCompleted listener)
	{

		this.listener = listener;
		this.context = context;
	}

	public void execute(final double latitude, final double longitude)
	{
		/**
		 * Server target URL
		 */
		final String URL = getInstance().getResources().getString(R.string.pixelServerBaseUrl)
				+ getInstance().getResources().getString(R.string.pixelServerFindTutorUrl);

		final StringRequest postRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

			@Override
			public void onResponse(String response)
			{
				try
				{
					Log.v(RESPONSE_TAG, response);

					JSONArray arr = new JSONArray(response);
					JSONObject jsonObj;

					Tutor.tutorList.clear();

					if(arr.length() > 0)
					{
						for (int i = 0; i < arr.length(); i++)
						{
							jsonObj = arr.getJSONObject(i);

							int tutor_id = jsonObj.getInt("tutor_id");
							String first_name = jsonObj.getString("first_name");
							String last_name = jsonObj.getString("last_name");
							String gender = jsonObj.getString("gender");
							String email = jsonObj.getString("email");
							String phone_no = jsonObj.getString("phone_no");
							String pic_path = jsonObj.getString("pic_path");

							String home_address = jsonObj.getString("home_address");
							String city = jsonObj.getString("city");
							String state = jsonObj.getString("state");
							String country = jsonObj.getString("country");
							String pincode = jsonObj.getString("pincode");
							String locality = jsonObj.getString("locality");
							double latitude = Double.parseDouble(jsonObj.getString("latitude"));
							double longitude = Double.parseDouble(jsonObj.getString("longitude"));
							double distance = jsonObj.getDouble("distance");

							Address addressObj = new Address(locality, home_address, pincode, city, state, country, latitude, longitude, distance);

							String occupation = jsonObj.getString("occupation");
							String employer = jsonObj.getString("employer");
							String start_date = jsonObj.getString("start_date");
							String end_date = jsonObj.getString("end_date");

							Occupation occupationObj = new Occupation(occupation, employer, start_date, end_date);

							String classes = jsonObj.getString("classes");
							String subjects = jsonObj.getString("subjects");
							String fees_range = jsonObj.getString("fees_range");
							String duration_per_day = jsonObj.getString("duration_per_day");
							String days_per_week = jsonObj.getString("days_per_week");
							String medium = jsonObj.getString("medium");
							String board = jsonObj.getString("board");

							TutorPreference preferenceObj = new TutorPreference(classes, subjects, fees_range, duration_per_day, days_per_week, medium, board);

							JSONArray education_array = new JSONArray(jsonObj.getString("education_details"));

							List<Education> educationList = new ArrayList<>();

							for (int j = 0; j < education_array.length(); j++)
							{

								jsonObj = (JSONObject) education_array.get(j);

								String qualification = jsonObj.getString("qualification");
								String division = jsonObj.getString("division");
								String qualification_status = jsonObj.getString("qualification_status");
								String academic_achievement = jsonObj.getString("academic_achievement");
								String college = jsonObj.getString("college");

								educationList.add(new Education(qualification, division, qualification_status, academic_achievement, college));

								Log.v("education_details", qualification);
							}

							Tutor tutorObj = new Tutor(tutor_id, first_name, last_name, gender, pic_path, email, phone_no, addressObj, educationList, preferenceObj, occupationObj);
							Tutor.tutorList.add(tutorObj);
						}

						Collections.sort(Tutor.tutorList, new DistanceCompare());
						listener.onTaskCompleted(true, 200, "available");
					}

					else
					{
						listener.onTaskCompleted(true, 199, "Sorry!! No Nearest Store Found");
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

				params.put("latitude", String.valueOf(latitude));
				params.put("longitude", String.valueOf(longitude));

				Log.v(JSON_TAG, String.valueOf(params));

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


	class DistanceCompare implements Comparator<Tutor>
	{

		@Override
		public int compare(Tutor s1, Tutor s2)
		{

			if(s1.addressObj.distance > s2.addressObj.distance)
			{
				return 1;
			}

			return -1;
		}
	}
}