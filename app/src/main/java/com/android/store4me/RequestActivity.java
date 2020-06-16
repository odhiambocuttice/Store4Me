package com.android.store4me;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressBar loading;
    String user_id, BackpackID;
    String value3, value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        loading = findViewById(R.id.loadingAddLoc);


        mAuth = FirebaseAuth.getInstance();
//        user_id = mAuth.getCurrentUser().getUid();
        BackpackID = mAuth.getCurrentUser().getUid();

        user_id = getIntent().getStringExtra("user_id");

        final TextView fetchedTextBackpackValue = findViewById(R.id.txtBackpackNameID);


        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Backpacks").child(BackpackID);
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value3 = (String) dataSnapshot.child("Name").getValue();
                fetchedTextBackpackValue.setText(value3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());

            }
        });


        final TextView fetchedTextDescriptionValue = findViewById(R.id.txtShopNameID);


        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                value = (String) dataSnapshot.child("Shopname").getValue();
                fetchedTextDescriptionValue.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());


            }
        });
        final Button findDirection = findViewById(R.id.direction_button);

        final Button fetchedPhoneValue = findViewById(R.id.call_button);
        fetchedPhoneValue.setVisibility(View.INVISIBLE);

        final Button Fin = findViewById(R.id.finish_request_button);
        final TextInputEditText mMessage = findViewById(R.id.BackpackDetails);
        Fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading.setVisibility(View.VISIBLE);

                String details = mMessage.getText().toString().trim();
                if (details.isEmpty()) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(RequestActivity.this, "Enter Your Backpack Contents", Toast.LENGTH_SHORT).show();
                } else {

                    DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference()
                            .child("BackpackRequest").child(user_id).push();



                    mUserDatabase.child("BackpackOwner").setValue(BackpackID);
                    mUserDatabase.child("BackPackContents").setValue(details);
                    mUserDatabase.child("Name").setValue(value3);
                    mUserDatabase.child("Shopname").setValue(value);

//                    DatabaseReference mUserDatabase2 = FirebaseDatabase.getInstance().getReference()
//                            .child("BackpackRequest2").child(user_id).push();
//
//
//
//                    mUserDatabase2.child("BackpackOwner").setValue(BackpackID);
//                    mUserDatabase2.child("BackPackContents").setValue(details);
//                    mUserDatabase2.child("Name").setValue(value);

                    DatabaseReference NotificationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");

                    NotificationDatabase.child(user_id).child(BackpackID).setValue(true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(RequestActivity.this, "Request Sent", Toast.LENGTH_SHORT).show();
                                    fetchedPhoneValue.setVisibility(View.VISIBLE);

                                    mMessage.setVisibility(View.GONE);
                                    loading.setVisibility(View.GONE);
                                    Fin.setText("Request Sent");
                                    Fin.setEnabled(false);

                                    final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
                                    myRef.keepSynced(true);
                                    myRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            final String PhoneNumber2 = (String) dataSnapshot.child("PhoneNumber").getValue();


                                            fetchedPhoneValue.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                                    intent.setData(Uri.parse("tel:" + PhoneNumber2));
                                                    startActivity(intent);

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w("Exception FB", databaseError.toException());


                                        }
                                    });

                                    final DatabaseReference SelectedStore = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
                                    SelectedStore.keepSynced(true);
                                    SelectedStore.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            final String PhoneNumber2 = (String) dataSnapshot.child("PhoneNumber").getValue();


                                            findDirection.setVisibility(View.VISIBLE);

                                            findDirection.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(RequestActivity.this, DriverMapActivity.class);
                                                    intent.putExtra("user_id", user_id);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w("Exception FB", databaseError.toException());


                                        }
                                    });
                                }
                            });



//                    Intent intent = new Intent(RequestActivity.this, StoreProfileBackpackActivity.class);
//                    intent.putExtra("user_id", user_id);
//                    startActivity(intent);
//                    finish();
                }
            }
        });


    }
}