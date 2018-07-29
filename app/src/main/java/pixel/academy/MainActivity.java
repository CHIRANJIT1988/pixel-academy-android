package pixel.academy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import pixel.academy.adapter.TutorsRecyclerAdapter;
import pixel.academy.alert.CustomAlertDialog;
import pixel.academy.api.PingTutor;
import pixel.academy.configuration.Configuration;
import pixel.academy.helper.OnAlertButtonClick;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.model.Tutor;


public class MainActivity extends AppCompatActivity implements OnTaskCompleted, OnAlertButtonClick
{

    private static final int CALL_PERMISSION_REQUEST_CODE = 1;

    private RecyclerView recyclerView;
    private TutorsRecyclerAdapter adapter;
    private static Tutor selected_tutor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Tutors");

        recyclerView = (RecyclerView) findViewById(R.id.list);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        /*Occupation occupation = new Occupation("Software Engineer", "Zaloni", "13/10/2015", "20/09/2016");
        TutorPreference preference = new TutorPreference("1, 3, 5, 8, 9", "Math, Science", "100-1000", "1", "3", "Bengali, English", "SEBA, CBSC");
        Address address = new Address("Dhing Town", "Schhol Reserve", "782123", "Nagaon", "Assam", "India", 26.3313, 91.313134, 3);
        Education education1 = new Education("HS", "Star", "Last Qualification", "NIL", "Dhing Collage");
        Education education2 = new Education("BSc(CS)", "64.2", "Persuing Qualification", "NIL", "NIIT");

        Tutor t = new Tutor();
        t.educationList.add(education1);
        t.educationList.add(education2);

        Tutor.tutorList.add(new Tutor("Manash", "Sonowal", "M", "", "manash@gmail.com", "9832932828", address, t.educationList, preference, occupation));
        Tutor.tutorList.add(new Tutor("Chiranjit", "Bardhan", "M", "", "manash@gmail.com", "9832932828", address, t.educationList, preference, occupation));

*/
        adapter = new TutorsRecyclerAdapter(getApplicationContext(), this, Tutor.tutorList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.SetOnItemClickListener(new TutorsRecyclerAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position)
            {

                Intent intent = new Intent(MainActivity.this, TutorProfileActivity.class);
                intent.putExtra("TUTOR_OBJ", Tutor.tutorList.get(position));
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onAlertButtonClick(boolean flag, int code, String action)
    {

        if(flag && code == 200 && action.equals("ping"))
        {
            SharedPreferences preferences = getSharedPreferences(Configuration.SHARED_PREF, Context.MODE_PRIVATE);
            String address = preferences.getString("address_details", "");
            new PingTutor(this, this).execute(address, selected_tutor.tutor_id, getIntent().getDoubleExtra("LATITUDE", 0), getIntent().getDoubleExtra("LONGITUDE", 0));
        }

        else if(flag && code == 200 && action.equals("call"))
        {

            try
            {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                callIntent.setData(Uri.parse("tel:" + selected_tutor.phone_no));
                startActivity(callIntent);
            }

            catch(Exception e)
            {
                Toast.makeText(getApplicationContext(), "Unable to Call", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void onTaskCompleted(boolean flag, int code, String message)
    {

        selected_tutor = Tutor.tutorList.get(code);

        if(flag && message.equals("ping"))
        {
            new CustomAlertDialog(this, this).showConfirmationDialog("Ping", "Ping Now ?", "ping");
        }

        else if(flag && message.equals("call"))
        {

            if(permissionCheckerCall())
            {
                new CustomAlertDialog(this, this).showConfirmationDialog("Call", "Call Now ?", "call");
            }
        }
    }


    private boolean permissionCheckerCall() {

        if (!checkPermissionCall()) {
            requestPermissionCall();
            return false;
        }

        return true;
    }


    private boolean checkPermissionCall()
    {

        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    private void requestPermissionCall(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))
        {
            makeToast("CALL permission allows us to call from app. Please allow in App Settings for call.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION_REQUEST_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case CALL_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeToast("Permission Granted");
                } else {
                    makeToast("Permission Denied");
                }

                break;
        }
    }

    private void makeToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}