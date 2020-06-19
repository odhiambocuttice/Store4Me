package com.android.store4me;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RequestReceivedActivity extends AppCompatActivity {

    private TextView mRequest, mBackpackID;
    private Button mCornfirmbtn, mDeclinebtn;

    private FirebaseAuth mAuth;

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_received);

        String Messmessage = getIntent().getStringExtra("message");
        String MessFrom = getIntent().getStringExtra("from_id");

        mRequest = (TextView) findViewById(R.id.BackpackDetails);
        mBackpackID = (TextView) findViewById(R.id.txtBackpackNameID);
        mCornfirmbtn = (Button) findViewById(R.id.Confirm_button);
        mDeclinebtn = (Button) findViewById(R.id.Decline_button);

        mRequest.setText("FROM :" + MessFrom);
        mBackpackID.setText(MessFrom);

//        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("BackpackRequest");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = (String) dataSnapshot.child("BackPackContents").getValue();
//                mRequest.setText(value);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("Exception FB", databaseError.toException());
//
//
//            }
//        });

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

//        mCornfirmbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDeclinebtn.setEnabled(false);
//                final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("BackpackRequest");
//                mDb.child("Status").setValue("Confirmed");
//
//            }
//        });
//
//        mDeclinebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mCornfirmbtn.setEnabled(false);
//                final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("BackpackRequest2");
//                mDb.child("Status").setValue("Declines");
//
//            }
//        });
    }

}
