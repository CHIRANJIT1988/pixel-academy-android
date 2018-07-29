package pixel.academy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DecimalFormat;

import pixel.academy.helper.Blur;
import pixel.academy.helper.Helper;
import pixel.academy.model.Tutor;

import static pixel.academy.configuration.Configuration.IMAGE_URL;


public class TutorProfileActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private RatingBar rating;
    private TextView tvOccupation, tvEmployer, tvDate, tvName, tvAddress, tvDistance, tvClasses, tvMedium, tvBoard, tvSubjects, tvFeesRange;
    private ImageView image;

    private Tutor tutor;
    private DecimalFormat df = new DecimalFormat("0.00");

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("");


        this.tutor = (Tutor) getIntent().getSerializableExtra("TUTOR_OBJ");


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                *//*Intent intent = new Intent(StoreProfileActivity.this, ChatWindowActivity.class);
                intent.putExtra("USER", new ChatMessage(String.valueOf(store.id), store.name));
                startActivity(intent);*//*
            }
        });*/


        findViewById();
        display();
    }


    private void findViewById()
    {

        image = (ImageView) findViewById(R.id.header);
        tvOccupation = (TextView) findViewById(R.id.occupation);
        tvEmployer = (TextView) findViewById(R.id.employer);
        tvDate = (TextView) findViewById(R.id.date);

        tvName = (TextView) findViewById(R.id.tutor_name);
        tvDistance = (TextView) findViewById(R.id.distance);
        tvAddress = (TextView) findViewById(R.id.address);

        tvClasses = (TextView) findViewById(R.id.classes);
        tvSubjects = (TextView) findViewById(R.id.subjects);
        tvMedium = (TextView) findViewById(R.id.medium);
        tvBoard = (TextView) findViewById(R.id.board);
        tvFeesRange= (TextView) findViewById(R.id.fees_range);
    }


    private void display()
    {

        StringBuilder address = new StringBuilder().append(tutor.addressObj.home_address).append(", ").append(tutor.addressObj.locality).append(", ")
                .append(tutor.addressObj.city).append(", ").append(tutor.addressObj.state).append(", ").append(tutor.addressObj.country).append(" - ").append(tutor.addressObj.pincode);

        tvName.setText(String.valueOf(tutor.first_name + " " + tutor.last_name).toUpperCase());

        tvAddress.setText(address.toString().toUpperCase());
        tvDistance.setText(String.valueOf(tutor.addressObj.distance + " km from current or selected location"));

        tvOccupation.setText(Helper.toCamelCase(tutor.occupationObj.occupation));
        tvEmployer.setText(Helper.toCamelCase(tutor.occupationObj.employer));
        tvDate.setText(String.valueOf(tutor.occupationObj.start_date + " - " + tutor.occupationObj.end_date));

        tvClasses.setText(String.valueOf("Classes ~ " + tutor.preferenceObj.classes));
        tvSubjects.setText(Helper.toCamelCase("Subjects ~ " + tutor.preferenceObj.subjects));
        tvMedium.setText(Helper.toCamelCase("Medium ~ " + tutor.preferenceObj.medium));
        tvBoard.setText(String.valueOf("Board ~ " + (tutor.preferenceObj.board).toUpperCase()));
        tvFeesRange.setText(tutor.preferenceObj.fees_range);


        setUpMap();

        Transformation blurTransformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source)
            {
                Bitmap blurred = Blur.fastblur(TutorProfileActivity.this, source, 10);
                source.recycle();
                return blurred;
            }

            @Override
            public String key()
            {
                return "blur()";
            }
        };


        Picasso.with(TutorProfileActivity.this)
            .load(IMAGE_URL + "TUTOR_" + tutor.profile_pic + ".jpg") // thumbnail url goes here
            .transform(blurTransformation)
            .into(image, new Callback() {

                @Override
                public void onSuccess()
                {

                    Picasso.with(TutorProfileActivity.this)
                            .load(IMAGE_URL + "TUTOR_" + tutor.profile_pic + ".jpg") // image url goes here
                            .into(image);
                }

                @Override
                public void onError()
                {
                    //Toast.makeText(getApplicationContext(), "Failed to load Profile Picture", Toast.LENGTH_LONG).show();
                }
            });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void  onMapReady(GoogleMap map)
    {
        mMap = map;
    }


    private void setUpMap()
    {


        try
        {

            //show error dialog if GoolglePlayServices not available
            if (!isGooglePlayServicesAvailable())
            {
                finish();
            }


            LatLng position = new LatLng(tutor.addressObj.latitude, tutor.addressObj.longitude);

            // Instantiating MarkerOptions class
            MarkerOptions options = new MarkerOptions();

            // Setting position for the MarkerOptions
            options.position(position);


            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null)
            {

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(position).zoom(15f).tilt(50).build();


                // Try to obtain the map from the SupportMapFragment.
                /*SupportMapFragment supportMapFragment = */((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(this);
                //(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);

                /*mMap = supportMapFragment.getMapAsync(this);
                mMap.setMyLocationEnabled(false);*/

                // LatLng position = new LatLng(store.latitude, store.longitude);

                // Instantiating MarkerOptions class
                // MarkerOptions options = new MarkerOptions();

                // Setting position for the MarkerOptions
                // options.position(position);


                // Check if we were successful in obtaining the map.
                if (mMap != null)
                {

                    mMap.addMarker(options);
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {

                            marker.showInfoWindow(); // show marker info window on marker click
                            return true;
                        }
                    });


                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                        @Override
                        public void onMapClick(LatLng point)
                        {

                        }
                    });
                }

                else
                {
                    Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
                }
            }
        }

        catch (Exception e)
        {

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
}