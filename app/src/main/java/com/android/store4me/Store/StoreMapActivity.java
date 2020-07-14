package com.android.store4me.Store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.store4me.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class StoreMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    String BackpackID;
    FirebaseAuth mAuth;


    private GoogleMap mMap; //Map object
    private FusedLocationProviderClient mFusedLocationProviderClient; //Gets current location of user
    private List<AutocompletePrediction> predictionList; //Saves Prediction lists

    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.NAME,
            Place.Field.ADDRESS);


    private PlacesClient mPlacesClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private boolean mStoreChecked;


    private final float DEFAULT_ZOOM = 14.0f;
    private Switch mWorkingSwitch;
    String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_map);

        mAuth = FirebaseAuth.getInstance();
        BackpackID = mAuth.getCurrentUser().getUid();

//        user_id = getIntent().getStringExtra("user_id");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize the Places client
        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //GIVE INTERNET PERMISSION
        if (!isConnected()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Internet Connection Alert")
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }

        mWorkingSwitch = (Switch) findViewById(R.id.switch_store);

        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWorkingSwitch.setChecked(true);
                    ConnectStore();
                } else {

                    disconnectStore();
                    mWorkingSwitch.setChecked(false);
                }

            }
        });
    }

    private void getLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            DisplayStoreMarkers();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);


        // Prompt the user for permission.
        getLocationPermission();

        //Displaying markers from DB


    }


    public void DisplayStoreMarkers() {


        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("Stores")
                .child(BackpackID);
        mDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("UserID").getValue(String.class);
                String Shopname = dataSnapshot.child("Shopname").getValue(String.class);
                Status = dataSnapshot.child("Status").getValue(String.class);
                double latt = dataSnapshot.child("Latitude").getValue(Double.class);
                double lngg = dataSnapshot.child("Longitude").getValue(Double.class);
                LatLng storeLatLng = new LatLng(latt, lngg);

                if (Status.equals("Online")) {
                    mWorkingSwitch.setChecked(true);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            storeLatLng, DEFAULT_ZOOM));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latt, lngg)).title(Shopname)
                            .snippet(name)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.shelf)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void ConnectStore() {
        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("Stores")
                .child(BackpackID);
        mDb.child("Status").setValue("Online");
    }

    private void disconnectStore() {
        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("Stores")
                .child(BackpackID);
        mDb.child("Status").setValue("Offline");


//        dataSnapshot.getRef().removeValue();
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
//
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(BackpackID);
//
//        GeoFire geoFire = new GeoFire(myRef);
//        geoFire.removeLocation(BackpackID);

    }


    //INTERNET PERMISSION
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
//
//    @Override
//    public void onBackPressed() {
//
//        super.onBackPressed();
//        this.finish();
//        getIntent().removeExtra("user_id");
//        getIntent().setAction("");
//    }
}
