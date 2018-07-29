package pixel.academy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import pixel.academy.configuration.Configuration;
import pixel.academy.helper.Helper;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.session.SessionManager;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener,
        OnTaskCompleted/*, AddSchoolFragment.AddSchoolFragmentListener, NewPasswordFragment.AddSchoolFragmentListener*/
{

    private Button /*button_new_password, button_edit_school,*/ button_edit_profile;
    private TextView tv_user_name, tv_mobile_number, tv_address, tv_locality, tv_city, tv_state, tv_country, tv_pincode, tv_progress;
    private ProgressBar profile_progress;
    //private School schoolObject;

    private SharedPreferences preferences;
    private ProgressDialog pDialog;
    private SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("My Profile");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById();
        setListener();

        this.preferences = getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
        this.pDialog = new ProgressDialog(this);
        this.session = new SessionManager(this);
    }


    private void initProgressDialog(String message)
    {

        pDialog.setMessage(message);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();
    }


    private void setListener()
    {
        //button_edit_school.setOnClickListener(this);
        button_edit_profile.setOnClickListener(this);
        //button_new_password.setOnClickListener(this);
    }


    private void findViewById()
    {

        //button_edit_school = (Button) findViewById(R.id.button_edit_school);
        button_edit_profile = (Button) findViewById(R.id.button_edit_profile);
        //button_new_password = (Button) findViewById(R.id.button_new_password);

        tv_user_name = (TextView) findViewById(R.id.user_name);
        tv_mobile_number = (TextView) findViewById(R.id.mobile_number);
        tv_address = (TextView) findViewById(R.id.address);
        tv_locality = (TextView) findViewById(R.id.locality);
        tv_city = (TextView) findViewById(R.id.city);
        tv_state = (TextView) findViewById(R.id.state);
        tv_country = (TextView) findViewById(R.id.country);
        tv_pincode = (TextView) findViewById(R.id.pincode);

        tv_progress = (TextView) findViewById(R.id.tv_progress);
        profile_progress = (ProgressBar) findViewById(R.id.profile_progress);
    }


    @Override
    public void onResume()
    {

        super.onResume();
        setProfileData(preferences.getString("address_details", ""));
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            /*case R.id.button_new_password:

                FragmentManager fm = getSupportFragmentManager();

                NewPasswordFragment dialogFragment = new NewPasswordFragment();
                dialogFragment.setListener(ProfileActivity.this);
                dialogFragment.setRetainInstance(true);
                dialogFragment.show(fm, "NewPasswordFragment");

                break;

            case R.id.button_edit_school:

                FragmentManager fm1 = getSupportFragmentManager();

                AddSchoolFragment dialogFragment1 = new AddSchoolFragment(getApplicationContext());
                dialogFragment1.setListener(ProfileActivity.this);
                dialogFragment1.setRetainInstance(true);
                dialogFragment1.show(fm1, "AddSchoolFragment");

                break;*/

            case R.id.button_edit_profile:

                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;

        }
    }


    /*@Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, School school)
    {

        initProgressDialog("Updating School ...");

        JSONObject jsonObject = new JSONObject();

        try
        {

            jsonObject.put("school_name", school.school_name);
            jsonObject.put("state", school.state);

            schoolObject = school;

            new UpdateSchoolDetails(getApplicationContext(), this).update(jsonObject.toString(), session.getUserId());
        }

        catch (JSONException e)
        {

        }
    }


    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, String new_password)
    {

        initProgressDialog("Changing Password ...");
        new ChangePassword(getApplicationContext(), this).change_password(session.getUserId(), new_password);
        //Toast.makeText(getApplicationContext(), new_password, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog)
    {
        // Do nothing
    }*/


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            /*case R.id.action_logout:

                session.logoutUser();
                finish();
                break;*/

            case android.R.id.home:
            {
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void setProfileData(String address_json_data)
    {

        try
        {

            JSONObject jsonObj = new JSONObject(address_json_data);

            String address = jsonObj.getString("address");
            String locality = jsonObj.getString("locality");
            String city = jsonObj.getString("city");
            String state = jsonObj.getString("state");
            String country = jsonObj.getString("country");
            String pincode = jsonObj.getString("pincode");

            if(!address.isEmpty())
            {
                tv_address.setText(Helper.toCamelCase(address));
            }

            if(!locality.isEmpty())
            {
                tv_locality.setText(locality);
            }

            if(!city.isEmpty())
            {
                tv_city.setText(Helper.toCamelCase(city));
            }

            if(!state.isEmpty())
            {
                tv_state.setText(Helper.toCamelCase(state));
            }

            if(!country.isEmpty())
            {
                tv_country.setText(Helper.toCamelCase(country));
            }

            if(!pincode.isEmpty())
            {
                tv_pincode.setText(String.valueOf(pincode));
            }
        }

        catch (JSONException e)
        {

        }
    }


    /*private void profile_progress(User userObj, School schoolObj)
    {

        int progress = 0;

        if(!userObj.getEmail().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!userObj.getLocation().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!userObj.getDateOfBirth().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!userObj.getGender().trim().isEmpty())
        {
            progress += (100/5);
        }

        if(!schoolObj.school_name.trim().isEmpty())
        {
            progress += (100/5);
        }

        profile_progress.setProgress(progress);
        tv_progress.setText(String.valueOf(progress + "%"));
    }*/


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {
            /*if(code == 200)
            {

                JSONObject jsonObject = new JSONObject();

                try
                {

                    jsonObject.put("school_name", schoolObject.school_name);
                    jsonObject.put("state", schoolObject.state);

                    this.preferences.edit().putString("school_details", jsonObject.toString()).apply();
                    setProfileData(preferences.getString("profile_details", ""), preferences.getString("school_details", ""));
                }

                catch (JSONException e)
                {

                }
            }*/
        }

        catch (Exception e)
        {

        }

        finally
        {

            if(pDialog.isShowing())
            {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}