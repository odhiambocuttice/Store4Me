package com.android.store4me;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String user_id, BackpackID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mAuth = FirebaseAuth.getInstance();
//        user_id = mAuth.getCurrentUser().getUid();
        BackpackID = mAuth.getCurrentUser().getUid();

        user_id = getIntent().getStringExtra("user_id");

        final TextView fetchedTextBackpackValue = findViewById(R.id.txtBackpackNameID);

        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Backpacks").child(BackpackID);
        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value3 = (String) dataSnapshot.child("Name").getValue();
                fetchedTextBackpackValue.setText(value3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());

            }
        });


        final TextView fetchedTextDescriptionValue = findViewById(R.id.txtShopNameID);


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.child("Shopname").getValue();
                fetchedTextDescriptionValue.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());


            }
        });

        final Button Fin = findViewById(R.id.finish_request_button);
        Fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RequestActivity.this, StoreProfileBackpackActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                finish();
            }
        });


    }
}
