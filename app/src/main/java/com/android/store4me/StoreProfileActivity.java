package com.android.store4me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StoreProfileActivity extends AppCompatActivity {

    EditText editText;

    String BackpackID;
    FirebaseAuth mAuth;
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
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_store_profile);


        mAuth = FirebaseAuth.getInstance();
        BackpackID = mAuth.getCurrentUser().getUid();

        final TextView fetchedText = findViewById(R.id.placeName);
        final TextView fetchedTextPhone = findViewById(R.id.tvValue);
        final TextView fetchedTextPriceHourValue = findViewById(R.id.PriceHourValue);
        final TextView fetchedTextDescriptionValue = findViewById(R.id.DescriptionValue);
        final ImageView fetchedProfilePicValue = (ImageView) findViewById(R.id.profile_picture);


        String user_id = mAuth.getCurrentUser().getUid();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.child("PlaceName").getValue();
                fetchedText.setText(value);
                String PhoneNumber = (String) dataSnapshot.child("PhoneNumber").getValue();
                fetchedTextPhone.setText(PhoneNumber);
                String HourlyCharge = (String) dataSnapshot.child("Price").getValue();
                fetchedTextPriceHourValue.setText(HourlyCharge);
                String StoreDesc = (String) dataSnapshot.child("Description").getValue();
                fetchedTextDescriptionValue.setText(StoreDesc);


//                String latitu = (String) dataSnapshot.child("Latitude").getValue();
//                String longitu = (String) dataSnapshot.child("Longitude").getValue();

                ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());
                Toast.makeText(StoreProfileActivity.this, "Error fetching data", Toast.LENGTH_LONG).show();

            }
        });

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Stores").child("venueImageUrl");
        key = mUserDatabase.getKey();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imgRef = mStorageRef.child("venue_images").child(key);
        final long ONE_MEGABYTE = 1024*1024;

        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
//                Glide.with(StoreProfileActivity.this).load(imgRef).into(fetchedProfilePicValue);
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                fetchedProfilePicValue.setMinimumHeight(dm.heightPixels);
                fetchedProfilePicValue.setMinimumWidth(dm.widthPixels);
                fetchedProfilePicValue.setImageBitmap(bm);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(StoreProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

//        mImageRef.getBytes(ONE_MEGABYTE)
//                .addOnSuccessListener(new onSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        DisplayMetrics dm = new DisplayMetrics();
//                        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//                        fetchedProfilePicValue.setMinimumHeight(dm.heightPixels);
//                        fetchedProfilePicValue.setMinimumWidth(dm.widthPixels);
//                        fetchedProfilePicValue.setImageBitmap(bm);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });

    }

}
