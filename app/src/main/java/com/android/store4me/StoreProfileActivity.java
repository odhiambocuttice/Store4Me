package com.android.store4me;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
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
    private String key, user_id;
    Button call_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store_profile);

        user_id = getIntent().getStringExtra("user_id");

        mAuth = FirebaseAuth.getInstance();
        BackpackID = mAuth.getCurrentUser().getUid();

        final TextInputEditText fetchedText = findViewById(R.id.placeName);
        final TextInputEditText fetchedTextPhone = findViewById(R.id.tvValue);
        final TextInputEditText fetchedTextPriceHourValue = findViewById(R.id.PriceHourValue);
        final EditText fetchedTextDescriptionValue = findViewById(R.id.DescriptionValue);
        final ImageView fetchedProfilePicValue = (ImageView) findViewById(R.id.profile_picture);
        final Button fetchedPhoneValue = findViewById(R.id.call_button);

//        fetchedPhoneValue.setVisibility(View.GONE);
//        if(user_id != null)
//            fetchedPhoneValue.setVisibility(View.VISIBLE);
//
////        resetButton.setVisibility(View.VISIBLE);


        //String user_id = mAuth.getCurrentUser().getUid();


        final Button fetchedBackpackValue = findViewById(R.id.btnEdit);

        fetchedBackpackValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StoreProfileActivity.this, Store_locationActivity.class);
//                intent.putExtra("user_id", user_id);
                startActivity(intent);
                finish();
            }
        });



        if (user_id != null) {


            final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
            myRef.keepSynced(true);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {
                    String value = (String) dataSnapshot.child("Shopname").getValue();
                    fetchedText.setText(value);
                    final String PhoneNumber = (String) dataSnapshot.child("PhoneNumber").getValue();
                    fetchedTextPhone.setText(PhoneNumber);
                    String HourlyCharge = (String) dataSnapshot.child("Price").getValue();
                    fetchedTextPriceHourValue.setText(HourlyCharge);
                    String StoreDesc = (String) dataSnapshot.child("Description").getValue();
                    fetchedTextDescriptionValue.setText(StoreDesc);
                    final String PhoneNumber2 = (String) dataSnapshot.child("PhoneNumber").getValue();
//                    fetchedPhoneValue.setText(PhoneNumber2);

//
//                    fetchedPhoneValue.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(Intent.ACTION_DIAL);
//                            intent.setData(Uri.parse("tel:" + PhoneNumber2));
//                            startActivity(intent);
//
//                        }
//                    });

//                    fetchedTextPhone.setText(PhoneNumberBtn);

                    Glide.with(StoreProfileActivity.this).load(dataSnapshot.child("venueImageUrl").getValue()).
                            into(fetchedProfilePicValue);

//                String latitu = (String) dataSnapshot.child("Latitude").getValue();
//                String longitu = (String) dataSnapshot.child("Longitude").getValue();

                    ;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("Exception FB", databaseError.toException());

                }
            });

        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Stores").child("venueImageUrl");
        key = mUserDatabase.getKey();

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        final StorageReference imgRef = mStorageRef.child("venue_images").child(key);
        final long ONE_MEGABYTE = 1024 * 1024;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.threedots_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mlogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), StoresLoginActivity.class));
                break;
            case R.id.mcall:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+254704195820"));
                startActivity(intent);
                break;
            case R.id.mLocation:
                startActivity(new Intent(getApplicationContext(), StoreMapActivity.class));
                break;
            case R.id.mRequest:
                startActivity(new Intent(getApplicationContext(), AllRequestsActivity.class));
                break;
        }
        return true;
    }
}
