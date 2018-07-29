package pixel.academy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pixel.academy.api.FindNearestTutors;
import pixel.academy.gps.GData;
import pixel.academy.gps.GPSTracker;
import pixel.academy.helper.Helper;
import pixel.academy.helper.OnLocationFound;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.model.Tutor;
import pixel.academy.network.InternetConnectionDetector;
import pixel.academy.session.SessionManager;

import static pixel.academy.configuration.Configuration.PLACES_API_BASE;
import static pixel.academy.configuration.Configuration.TYPE_AUTOCOMPLETE;
import static pixel.academy.configuration.Configuration.OUT_JSON;
import static pixel.academy.configuration.Configuration.API_KEY;


public class ServiceLocationActivity extends AppCompatActivity implements
        View.OnClickListener, OnTaskCompleted, OnLocationFound, AdapterView.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback
{

    private int back_pressed = 0;

    private GoogleMap mMap;

    private TextView markerText;

    private LatLng center;
    private LinearLayout markerLayout;
    private List<Address> addresses;

    private TextView address;
    private ProgressBar progressBar;
    private Button btnDone;
    private LinearLayout footer_main;
    private ImageButton ibTutor;
    private ProgressBar pbLoading;
    private LinearLayout fab_main;

    private static double latitude, longitude;

    private static final int GPS_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_location);

        if(!new SessionManager(this).checkLogin())
        {
            finish();
        }

        markerText = (TextView) findViewById(R.id.locationMarkertext);
        address = (TextView) findViewById(R.id.address);
        markerLayout = (LinearLayout) findViewById(R.id.locationMarker);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading);
        btnDone = (Button) findViewById(R.id.btnDone);
        ibTutor = (ImageButton) findViewById(R.id.ibTutor);
        footer_main = (LinearLayout) findViewById(R.id.footer_main);
        fab_main = (LinearLayout) findViewById(R.id.fab_main);
        pbLoading = (ProgressBar) findViewById(R.id.pBar);

        ibTutor.setOnClickListener(this);

        /*if (!isGooglePlayServicesAvailable())
        {
            finish();
        }*/

        AutoCompleteTextView edit_search = (AutoCompleteTextView) findViewById(R.id.edit_search);

        edit_search.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item_auto_complete));
        edit_search.setOnItemClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        permissionCheckerGPS();
    }


    @Override
    public void onMapReady(GoogleMap map)
    {
        mMap = map;

        if(new InternetConnectionDetector(this).isConnected())
        {

            GPSTracker gps = new GPSTracker(this, ServiceLocationActivity.this);

            if(!gps.canGetLocation())
            {
                showSettingsAlert();
            }
        }
    }


    public void showSettingsAlert()
    {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("GPS Settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is required to find nearest stores");
        // Setting Cancelable
        alertDialog.setCancelable(false);

        // On pressing Settings button
        alertDialog.setPositiveButton("Turn On GPS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                finish();
            }
        });


        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    @Override
    public void onBackPressed()
    {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {

            if(back_pressed == 0)
            {
                back_pressed++;
                Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
            }

            else
            {
                finish();
            }
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        startActivity(new Intent(ServiceLocationActivity.this, EditProfileActivity.class));
        return true;
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    private void setUpMap(double latitude, double longitude)
    {

        LatLng position = new LatLng(latitude, longitude);
        MarkerOptions options = new MarkerOptions();
        options.position(position);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position).zoom(16f).tilt(50).build();

        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.clear();


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition arg0)
            {

                center = mMap.getCameraPosition().target;

                markerText.setText(String.valueOf(" My Location "));
                mMap.clear();
                markerLayout.setVisibility(View.VISIBLE);

                try
                {

                    new GetLocationAsync(center.latitude, center.longitude).execute();

                    if(new InternetConnectionDetector(ServiceLocationActivity.this).isConnected())
                    {
                        fab_main.setVisibility(View.GONE);
                        pbLoading.setVisibility(View.VISIBLE);
                        new FindNearestTutors(ServiceLocationActivity.this, ServiceLocationActivity.this).execute(center.latitude, center.longitude);
                    }
                }

                catch (Exception e)
                {

                }
            }
        });

        mMap.setTrafficEnabled(false);
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.ibTutor:

                Intent intent = new Intent(ServiceLocationActivity.this, MainActivity.class);
                intent.putExtra("LATITUDE", latitude);
                intent.putExtra("LONGITUDE", longitude);
                startActivity(intent);

                break;

            /*case R.id.btnDone:

                Intent intent = new Intent();
                intent.putExtra("LATITUDE", latitude);
                intent.putExtra("LONGITUDE", longitude);
                setResult(1, intent);

                finish();*/
        }
    }


    private boolean isGooglePlayServicesAvailable()
    {

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == status)
        {
            return true;
        }

        else
        {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

            Log.v("location ", "lat: " + latitude + ", long: " + longitude);

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private class SearchLocationAsync extends AsyncTask<String, Void, String>
    {

        private String location;
        private LatLng p1;

        public SearchLocationAsync(String location)
        {
            this.location = location;
        }


        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
            address.setText(String.valueOf("Searching Location ..."));
        }

        @Override
        protected String doInBackground(String... params)
        {

            // Getting reference to the SupportMapFragment
            // Create a new global location parameters object
            LocationRequest mLocationRequest = LocationRequest.create();

            //Set the update interval
            mLocationRequest.setInterval(GData.UPDATE_INTERVAL_IN_MILLISECONDS);

            // Use high accuracy
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            // Set the interval ceiling to one minute
            mLocationRequest.setFastestInterval(GData.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

            // Note that location updates are off until the user turns them on
            // boolean mUpdatesRequested = false;

            p1 = getLocationFromAddress(getApplicationContext(), location);
            return null;
        }


        @Override
        protected void onPostExecute(String result)
        {

            try
            {
                setUpMap(p1.latitude, p1.longitude);
                btnDone.setVisibility(View.VISIBLE);
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    private class GetLocationAsync extends AsyncTask<String, Void, String>
    {

        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude)
        {
            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
            address.setText(String.valueOf("Fetching Address ..."));
        }

        @Override
        protected String doInBackground(String... params)
        {

            try
            {

                Geocoder geocoder = new Geocoder(ServiceLocationActivity.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();

                if (Geocoder.isPresent())
                {

                    Address returnAddress = addresses.get(0);

                    String localityString = returnAddress.getLocality();
                    String city = returnAddress.getCountryName();
                    String region_code = returnAddress.getCountryCode();
                    String zipcode = returnAddress.getPostalCode();

                    str.append(localityString + " ");
                    str.append(city + " " + region_code + " ");
                    str.append(zipcode + " ");

                }
            }

            catch (IOException e)
            {
                Log.e("tag", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {

            try
            {
                address.setText(String.valueOf(Helper.toCamelCase(addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getAddressLine(1) + " ")));
                progressBar.setVisibility(View.GONE);
            }

            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


        @Override
        protected void onProgressUpdate(Void... values)
        {

        }
    }

    public void FooterAnimationShow()
    {
        footer_main.setVisibility(View.VISIBLE);
        Animation show = AnimationUtils.loadAnimation(this, R.anim.enter_anim);
        footer_main.startAnimation(show);
    }


    /*public void FooterAnimationHide()
    {
        footer_main.setVisibility(View.GONE);
        Animation hide = AnimationUtils.loadAnimation(this, R.anim.exit_anim);
        footer_main.startAnimation(hide);
    }*/


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            fab_main.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);

            FooterAnimationShow();

            for(Tutor tutor: Tutor.tutorList)
            {
                LatLng location = new LatLng(tutor.addressObj.latitude, tutor.addressObj.longitude);
                mMap.addMarker(new MarkerOptions().position(location).title(tutor.first_name));
            }
        }

        catch (Exception e)
        {

        }
    }


    @Override
    public void onLocationFound(Location location)
    {
        setUpMap(location.getLatitude(), location.getLongitude());
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {

        String location = (String) adapterView.getItemAtPosition(position);

        if(new InternetConnectionDetector(this).isConnected())
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(footer_main.getWindowToken(), 0);
            new SearchLocationAsync(location).execute();
        }

        else
        {
            Toast.makeText(getApplicationContext(), "Internet Connection Failure", Toast.LENGTH_LONG).show();
        }
    }


    public static ArrayList<String> autocomplete(String input)
    {

        ArrayList<String> resultList = new ArrayList<>();

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try
        {

            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());

            System.out.println("URL: "+url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1)
            {
                jsonResults.append(buff, 0, read);
            }
        }

        catch (MalformedURLException e)
        {
            Log.e("Google Place Error: ", "Error processing Places API URL", e);
            return resultList;
        }

        catch (IOException e)
        {
            Log.e("Google Place Error: ", "Error connecting to Places API", e);
            return resultList;
        }

        finally
        {

            if (conn != null)
            {
                conn.disconnect();
            }
        }

        try
        {

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>(predsJsonArray.length());

            for (int i = 0; i < predsJsonArray.length(); i++)
            {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        }

        catch (JSONException e)
        {
            Log.e("Google Place Error: ", "Cannot process JSON results", e);
        }

        return resultList;
    }


    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable
    {

        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId)
        {
            super(context, textViewResourceId);
        }


        @Override
        public int getCount() {
            return resultList.size();
        }


        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }


        @Override
        public Filter getFilter()
        {

            Filter filter = new Filter()
            {

                @Override
                protected FilterResults performFiltering(CharSequence constraint)
                {

                    FilterResults filterResults = new FilterResults();

                    if (constraint != null)
                    {

                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }

                    return filterResults;
                }


                @Override
                protected void publishResults(CharSequence constraint, FilterResults results)
                {

                    if (results != null && results.count > 0)
                    {
                        notifyDataSetChanged();
                    }

                    else
                    {
                        notifyDataSetInvalidated();
                    }
                }
            };

            return filter;
        }
    }


    private boolean checkPermissionGPS()
    {

        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    private boolean permissionCheckerGPS()
    {

        if (!checkPermissionGPS())
        {
            requestPermissionGPS();
            return false;
        }

        return true;
    }


    private void requestPermissionGPS(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
        {
            makeToast("GPS permission allows us to access location data. Please allow in App Settings for location.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case GPS_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    makeToast("Permission Granted");
                }

                else
                {
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