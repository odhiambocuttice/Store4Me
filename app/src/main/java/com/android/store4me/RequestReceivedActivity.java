package com.android.store4me;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RequestReceivedActivity extends AppCompatActivity {

    private TextView mRequest, mBackpackID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_received);

        String Messmessage = getIntent().getStringExtra("message");
        String MessFrom = getIntent().getStringExtra("from_id");

        mRequest = (TextView) findViewById(R.id.BackpackDetails);
        mBackpackID = (TextView) findViewById(R.id.txtBackpackNameID);

        mRequest.setText("FROM :" + Messmessage);
        mBackpackID.setText(MessFrom);

        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("BackpackRequest");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.child("BackPackContents").getValue();
                mRequest.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Exception FB", databaseError.toException());


            }
        });

    }
}
