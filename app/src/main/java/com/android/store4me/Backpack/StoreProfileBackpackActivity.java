package com.android.store4me.Backpack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.store4me.R;
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

public class StoreProfileBackpackActivity extends AppCompatActivity {

    String BackpackID;
    FirebaseAuth mAuth;

    private DatabaseReference mUserDatabase;
    private String key, user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile_backpack);

        user_id = getIntent().getStringExtra("user_id");

        mAuth = FirebaseAuth.getInstance();
        BackpackID = mAuth.getCurrentUser().getUid();

        final TextInputEditText fetchedText = findViewById(R.id.placeName);
        final TextInputEditText fetchedTextPhone = findViewById(R.id.tvValue);
        final TextInputEditText fetchedTextPriceHourValue = findViewById(R.id.PriceHourValue);
        final EditText fetchedTextDescriptionValue = findViewById(R.id.DescriptionValue);
        final ImageView fetchedProfilePicValue = (ImageView) findViewById(R.id.profile_picture);
        final Button fetchedPhoneValue = findViewById(R.id.call_button);


        final Button fetchedBackpackValue = findViewById(R.id.request_button);

        fetchedBackpackValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(StoreProfileBackpackActivity.this, RequestActivity.class);
                intent.putExtra("user_id", user_id);
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


                    fetchedPhoneValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + PhoneNumber2));
                            startActivity(intent);

                        }
                    });

                    Glide.with(StoreProfileBackpackActivity.this).load(dataSnapshot.child("venueImageUrl").getValue()).
                            into(fetchedProfilePicValue);

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
    }


}
