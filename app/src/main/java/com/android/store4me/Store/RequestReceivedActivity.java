package com.android.store4me.Store;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.store4me.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestReceivedActivity extends AppCompatActivity {

    private TextView mRequest, mBackpackID;
    private Button mCornfirmbtn, mDeclinebtn;

    private FirebaseAuth mAuth;

    String user_id, request_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_received);

        String Messmessage = getIntent().getStringExtra("message");
        String MessFrom = getIntent().getStringExtra("from_id");
        String RequestID = getIntent().getStringExtra("request");

        mRequest = (TextView) findViewById(R.id.BackpackDetails);
        mBackpackID = (TextView) findViewById(R.id.txtBackpackNameID);
        mCornfirmbtn = (Button) findViewById(R.id.Confirm_button);
        mDeclinebtn = (Button) findViewById(R.id.Decline_button);

        mRequest.setText(Messmessage);
        mBackpackID.setText(MessFrom);

        user_id = getIntent().getStringExtra("user_id");
        request_id = getIntent().getStringExtra("request_id");


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

        mCornfirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeclinebtn.setEnabled(false);
                final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("BackpackRequest")
                        .child(user_id).child(request_id);
                mDb.child("Status").setValue("Confirmed");

            }
        });

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
