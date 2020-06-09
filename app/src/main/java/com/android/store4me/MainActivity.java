package com.android.store4me;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Button mReg;

    TextView name, email, mobile;
    String BackpackID;
    FirebaseAuth mAuth;
    ListView lstPlaces;


    private GoogleMap mMap; //Map object
    private FusedLocationProviderClient mFusedLocationProviderClient; //Gets current location of user
    private List<AutocompletePrediction> predictionList; //Saves Prediction lists

    private Location mLastKnownLocation; //Current location of user
    private final float DEFAULT_ZOOM = 14.0f;
    private Marker mUserLocMarker;
    private LatLng pickupLocation;

    private static final String TAG = "MainActivity";

    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.NAME,
            Place.Field.ADDRESS);
    int AUTOCOMPLETE_REQUEST_CODE = 1;

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private PlacesClient mPlacesClient;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    Marker mCurrLocationMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationPermission();
//        }

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.fullname);
        email = headerView.findViewById(R.id.useremail);

        mAuth = FirebaseAuth.getInstance();
        BackpackID = mAuth.getCurrentUser().getUid();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Set up the views
        lstPlaces = (ListView) findViewById(R.id.listPlaces);


        // Initialize the Places client
        String apiKey = getString(R.string.google_maps_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mReg = findViewById(R.id.buttonReg);

        mReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                mAuth.removeAuthStateListener(firebaseAuthListener);
                startActivity(new Intent(getApplicationContext(), Registration3Activity.class));
                finish();
            }
        });


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

        String user_id = mAuth.getCurrentUser().getUid();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Backpacks").child(user_id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String namevalue = (String) dataSnapshot.child("Name").getValue();
                name.setText(namevalue);
                String emailvalue = (String) dataSnapshot.child("Email").getValue();
                email.setText(emailvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_geolocate:

                // COMMENTED OUT UNTIL WE DEFINE THE METHOD
                // Present the current place picker
                pickCurrentPlace();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(1.2921, 36.8219);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6));


        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

//        getDeviceLocation();

        //
        // PASTE THE LINES BELOW THIS COMMENT
        //

        // Enable the zoom controls for the map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Prompt the user for permission.
        getLocationPermission();


        String user_id2 = mAuth.getCurrentUser().getUid();

        //Displaying markers from DB

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Stores");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());


                } else {
                    Toast.makeText(MainActivity.this, "Add new location to view on map", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());
            }
        });

    }

    private void getCurrentPlaceLikelihoods() {
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

        // Get the likely places - that is, the businesses and other points of interest that
        // are the best match for the device's current location.
        @SuppressWarnings("MissingPermission") final FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        Task<FindCurrentPlaceResponse> placeResponse = mPlacesClient.findCurrentPlace(request);
        placeResponse.addOnCompleteListener(this,
                new OnCompleteListener<FindCurrentPlaceResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                        if (task.isSuccessful()) {
                            FindCurrentPlaceResponse response = task.getResult();
                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (response.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                                count = response.getPlaceLikelihoods().size();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                Place currPlace = placeLikelihood.getPlace();
                                mLikelyPlaceNames[i] = currPlace.getName();
                                mLikelyPlaceAddresses[i] = currPlace.getAddress();
                                mLikelyPlaceAttributions[i] = (currPlace.getAttributions() == null) ?
                                        null : TextUtils.join(" ", currPlace.getAttributions());
                                mLikelyPlaceLatLngs[i] = currPlace.getLatLng();

                                String currLatLng = (mLikelyPlaceLatLngs[i] == null) ?
                                        "" : mLikelyPlaceLatLngs[i].toString();

                                Log.i(TAG, String.format("Place " + currPlace.getName()
                                        + " has likelihood: " + placeLikelihood.getLikelihood()
                                        + " at " + currLatLng));

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }


                            // COMMENTED OUT UNTIL WE DEFINE THE METHOD
                            // Populate the ListView
                            fillPlacesList();
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof ApiException) {
                                ApiException apiException = (ApiException) exception;
                                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                            }
                        }
                    }
                });
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {

                            if (mCurrLocationMarker != null) {
                                mCurrLocationMarker.remove();
                            }
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = location;
                            Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                            Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()))      // Sets the center of the map to location user
                                    .zoom(17)                   // Sets the zoom
                                    .bearing(90)                // Sets the orientation of the camera to east
                                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            mCurrLocationMarker =    mMap.addMarker(new MarkerOptions()
                                    .title("YOU")
                                    .position(  new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                            //Getting Location Query
                            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("geofire");
                            GeoFire geoFire = new GeoFire(ref);

                            GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), 3);
                            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                                @Override
                                public void onKeyEntered(String key, GeoLocation location) {
                                    //Any location key which is within 3km from the user's location will show up here as the key parameter in this method
                                    //You can fetch the actual data for this location by creating another firebase query here

                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Stores").child(key);
                                    Query locationDataQuery = ref2;
                                    locationDataQuery.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.exists()) {
                                                collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());


                                            }
                                            //The dataSnapshot should hold the actual data about the location
//                                            dataSnapshot.getChild("name").getValue(String.class); //should return the name of the location and dataSnapshot.getChild("description").getValue(String.class); //should return the description of the locations
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                                @Override
                                public void onKeyExited(String key) {
                                }

                                @Override
                                public void onKeyMoved(String key, GeoLocation location) {
                                }

                                @Override
                                public void onGeoQueryReady() {
                                    //This method will be called when all the locations which are within 3km from the user's location has been loaded Now you can do what you wish with this data
                                }

                                @Override
                                public void onGeoQueryError(DatabaseError error) {

                                }
                            });

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));

                        }

                        getCurrentPlaceLikelihoods();
                    }
                });
            }
        } catch (Exception e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void pickCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            getDeviceLocation();
//            getDriversAround();
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .snippet(getString(R.string.default_info_snippet)));


            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    private AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            // position will give us the index of which place was selected in the array
            LatLng markerLatLng = mLikelyPlaceLatLngs[position];
            String markerSnippet = mLikelyPlaceAddresses[position];
            if (mLikelyPlaceAttributions[position] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[position];
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            mMap.addMarker(new MarkerOptions()
                    .title(mLikelyPlaceNames[position])
                    .position(markerLatLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng));
        }
    };

    private void fillPlacesList() {
        // Set up an ArrayAdapter to convert likely places into TextViews to populate the ListView
        ArrayAdapter<String> placesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mLikelyPlaceNames);
        lstPlaces.setAdapter(placesAdapter);
        lstPlaces.setOnItemClickListener(listClickedHandler);
    }


    private String user_id;

    private void collectPhoneNumbers(Map<String, Object> users) {

//        int size = (int) dataSnapshot.getChildrenCount(); //
//        Marker[] allMarkers = new Marker[size];
//        Marker mm;

        ArrayList<Double> phoneNumbers = new ArrayList<>();
        ArrayList<Double> latitude = new ArrayList<>();
        double lat, lng;
        String desc;

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            phoneNumbers.add((Double) singleUser.get("Longitude"));
            latitude.add((Double) singleUser.get("Latitude"));
            lat = (double) singleUser.get("Latitude");
            lng = (double) singleUser.get("Longitude");
            desc = (String) singleUser.get("PlaceName");
            user_id = (String) singleUser.get("UserID");
            String value = (String) singleUser.get("Shopname");
            LatLng latLng = new LatLng(lat, lng);

            //  mMap.animateCamera(CameraUpdateFactory.zoomTo(13f));
            //Toast.makeText(getContext(), ""+ latLng  , Toast.LENGTH_SHORT).show();
            if (mMap != null) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.shelf))
                        .position(new LatLng(lat, lng))
                        .snippet(value)
                        .title(user_id));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,6));
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        double lat = marker.getPosition().latitude;
                        double lng = marker.getPosition().longitude;

                        Intent intent = new Intent(MainActivity.this, StoreProfileBackpackActivity.class);
                        intent.putExtra("user_id", marker.getTitle());
                        startActivity(intent);
                    }
                });
            }
//            mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(latLng).title("Farm"));

        }

        //System.out.println(phoneNumbers.toString());
        //Toast.makeText(getContext(), phoneNumbers.toString(), Toast.LENGTH_SHORT).show();

    }


    //INTENET PERMISSION
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            onBackPressed();
        }
        super.onBackPressed();
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        mAuth.removeAuthStateListener(firebaseAuthListener);
        startActivity(new Intent(getApplicationContext(), Registration3Activity.class));
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.mlogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                break;
            case R.id.call_us:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+254704195820"));
                startActivity(intent);
                break;
        }
        return true;
    }


    @Override
    protected void onStop() {
        if (firebaseAuthListener != null) {
            mAuth.removeAuthStateListener(firebaseAuthListener);
        }
        super.onStop();
    }
}
