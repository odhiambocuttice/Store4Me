package com.android.store4me;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class Store_locationActivity extends AppCompatActivity {

    private EditText mEdtFarrmDesc, mEdtVenueSpace, mEdtVenuePrice;
    private TextView mTxtSearchLoc, mTxtEmail;
    private Button mBtnSaveLoc;
    private LatLng farmLocation;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int REQUEST_READ_EXTERNAL = 1234;
    PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.LAT_LNG,
            Place.Field.NAME,
            Place.Field.ADDRESS);

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String userID, placeName;
    private ProgressBar loading;
    //    private FirebaseUser user;
    private ImageView imgAddVenue;
    private Uri resultUri;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the SDK
        Places.initialize(Store_locationActivity.this, getString(R.string.google_maps_key));

        // Create a new Places client instance
        placesClient = Places.createClient(Store_locationActivity.this);

        setContentView(R.layout.activity_store_location);


//        user = FirebaseAuth.getInstance().getCurrentUser();


        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
//        user = FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Stores").child(userID);
        key = mUserDatabase.getKey();

        mBtnSaveLoc = findViewById(R.id.btnSaveLoc);
        mEdtFarrmDesc = findViewById(R.id.edtFarmDesc);
        mEdtVenueSpace = findViewById(R.id.edtPhoneNumber);
        mEdtVenuePrice = findViewById(R.id.edtPrice);
        mTxtSearchLoc = findViewById(R.id.txtSearchLoc);
//        mTxtEmail = findViewById(R.id.txtEmailID);
        imgAddVenue = findViewById(R.id.imgAddVenue);
        loading = findViewById(R.id.loadingAddLoc);


        final TextView mTxtEmail = findViewById(R.id.txtEmailID);

//        String user_id = mAuth.getCurrentUser().getUid();

        if (userID != null) {


            Intent intent = new Intent(Store_locationActivity.this, StoreProfileActivity.class);
            intent.putExtra("user_id", userID);
            startActivity(intent);
            finish();

            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(userID);
            myRef.keepSynced(true);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = (String) dataSnapshot.child("name").getValue();
                    mTxtEmail.setText(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Exception FB", databaseError.toException());
//                    Toast.makeText(Store_locationActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();

                }
            });

            mTxtSearchLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .setCountry("KE")
                            .build(Store_locationActivity.this);
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                }
            });

            imgAddVenue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPermissions();
                }
            });

            mBtnSaveLoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    save_venue();
                }
            });
        }
    }


    private void save_venue() {

        loading.setVisibility(View.VISIBLE);

        final String desc = mEdtFarrmDesc.getText().toString().trim();
        final String phone_number = mEdtVenueSpace.getText().toString().trim();
        final String price = mEdtVenuePrice.getText().toString().trim();


        if (farmLocation == null) {
            loading.setVisibility(View.GONE);
            Toast.makeText(this, "Enter Location", Toast.LENGTH_SHORT).show();
        } else if (desc.isEmpty()) {
            loading.setVisibility(View.GONE);
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
        }  else if (desc.length() < 30 ) {
            mEdtFarrmDesc.setError("The Store's Description must be 30 characters");
            return;
        }   else if (price.isEmpty()) {
            loading.setVisibility(View.GONE);
            Toast.makeText(this, "Enter Venue Price", Toast.LENGTH_SHORT).show();
        }else if (phone_number.isEmpty()) {
            loading.setVisibility(View.GONE);
            Toast.makeText(this, "Enter Your Contact Number", Toast.LENGTH_SHORT).show();
        }else if (phone_number.length() < 10) {
            mEdtVenueSpace.setError("Phone Number Must be = 10 digits");
            return;
        }else if (resultUri == null) {
            loading.setVisibility(View.GONE);
            Toast.makeText(this, "Add Venue Image", Toast.LENGTH_SHORT).show();
        } else if (placeName.isEmpty()) {
            placeName = desc;
        } else {


            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("venue_images").child(key);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            final UploadTask uploadTask = filePath.putBytes(data);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Uri url = taskSnapshot.getDownloadUrl();

                    final Map newImage = new HashMap();

                    filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            // ------------ SAVE IMAGE TO STORAGE & DB -----------------

                            newImage.put("venueImageUrl", task.getResult().toString());
                            mUserDatabase.updateChildren(newImage);
                            mUserDatabase.child("UserID").setValue(userID);
                            mUserDatabase.child("Longitude").setValue(farmLocation.longitude);
                            mUserDatabase.child("Latitude").setValue(farmLocation.latitude);
                            mUserDatabase.child("PlaceName").setValue(placeName);
                            mUserDatabase.child("Price").setValue(price);
                            mUserDatabase.child("PhoneNumber").setValue(phone_number);
                            mUserDatabase.child("Description").setValue(desc);

                            Toast.makeText(Store_locationActivity.this, "Setup Complete", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);

                            Intent intent = new Intent(Store_locationActivity.this, StoreProfileActivity.class);
                            intent.putExtra("user_id", userID);
                            startActivity(intent);


                            // Toast.makeText(AddVenueActivity.this,
                            //         "Venue Saved", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            });
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Store_locationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if ((ContextCompat.checkSelfPermission(Store_locationActivity.this, READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)) {

                // Permission is not granted


                ActivityCompat.requestPermissions(Store_locationActivity.this, new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL);


            } else {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);

            }


        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // --------------- PLACES RESULT ---------------
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                farmLocation = place.getLatLng();
                placeName = place.getName();
                mTxtSearchLoc.setText(place.getName());

                //Toast.makeText(this, "Place: " + place.getName(), Toast.LENGTH_SHORT).show();
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == RESULT_CANCELED) {
                //mEdtFarrmDesc.setText("");
                // The user canceled the operation.
            }
        }
        // --------------- VENUE IMAGE RESULT ------------
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            imgAddVenue.setImageURI(resultUri);
        }
    }
}