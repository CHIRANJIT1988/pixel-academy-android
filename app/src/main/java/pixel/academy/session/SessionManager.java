package pixel.academy.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import pixel.academy.RegisterActivity;
import pixel.academy.model.User;

import static pixel.academy.app.Global.MOBILE_NUMBER;
import static pixel.academy.app.Global.USER_ID;
import static pixel.academy.app.Global.USER_NAME;


public class SessionManager 
{
	
	private static SharedPreferences pref; // Shared Preferences
	private static Editor editor; // Editor for Shared preferences
	private Context _context; // Context
	private int PRIVATE_MODE = 0; // Shared pref mode

	private static final String PREF_NAME = "PixelAcademyPref"; // Shared Pref file name
	private static final String IS_LOGIN = "IsLoggedIn"; // All Shared Preferences Keys

	@SuppressLint("CommitPrefEdits") 
	public SessionManager(Context context) // Constructor
	{
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void createLoginSession(User user)
	{
		
		editor.putBoolean(IS_LOGIN, true); // Storing login value as TRUE

		editor.putString(USER_ID, user.user_id);
		editor.putString(USER_NAME, user.name);
		editor.putString(MOBILE_NUMBER, user.phone_number);
		
		editor.commit(); // commit changes
	}


	public boolean checkLogin()
	{
		
		if(!this.isLoggedIn()) // Check login status
		{
			
			Intent i = new Intent(_context, RegisterActivity.class); // user is not logged in redirect him to Login Activity
			
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Closing all the Activities
			
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add new Flag to start new Activity
			
			_context.startActivity(i); // Staring Login Activity
			
			return false;
			
		}
		
		return true;
	}
	

	/**
	 * Get stored session data
	 * */
	
	public HashMap<String, String> getUserDetails()
	{
		
		HashMap<String, String> store = new HashMap<>();

		store.put(USER_ID, pref.getString(USER_ID, null)); // store id
		store.put(USER_NAME, pref.getString(USER_NAME, null)); // store name
		store.put(MOBILE_NUMBER, pref.getString(MOBILE_NUMBER, null)); // mobile no

		return store; // return user
	}


	public String getUserId() // Get Login State
	{
		return pref.getString(USER_ID, null);
	}


	/**
	 * Clear session details
	 * */
	
	public void logoutUser()
	{
		
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		
		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, RegisterActivity.class);

		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	
	}

	
	/**
	 * Quick check for login
	 * ***/
	
	public boolean isLoggedIn() // Get Login State
	{
		return pref.getBoolean(IS_LOGIN, false);
	}
}