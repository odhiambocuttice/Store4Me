package com.android.store4me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.android.store4me.R.layout.activity_all_requests;

public class AllRequestsActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ListView mRequestList;

    private DatabaseReference mRequestReference;
    private FirebaseAuth mAuth;

    String user_id;
    String Messmessage, MessFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_all_requests);

        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        getSupportActionBar().setTitle("All Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();


        Messmessage = getIntent().getStringExtra("message");
        MessFrom = getIntent().getStringExtra("from_id");

        mRequestList = (ListView) findViewById(R.id.all_requests);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.store_request_list, list);
        mRequestList.setAdapter(adapter);

        AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllRequestsActivity.this, RequestReceivedActivity.class);
//                intent.putExtra("user_id", user_id);
                intent.putExtra("message", Messmessage);
                intent.putExtra("from_id", MessFrom);
                startActivity(intent);

            }
        };


        mRequestList.setOnItemClickListener(listClickedHandler);


        mRequestReference = FirebaseDatabase.getInstance().getReference().child("BackpackRequest").child(user_id);
        mRequestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StoreRequests_FromBackpacks storeRequests_fromBackpacks =
                            snapshot.getValue(StoreRequests_FromBackpacks.class);
                    String backpackOwner = storeRequests_fromBackpacks.getBackpackOwner();
                    String contents = storeRequests_fromBackpacks.getBackPackContents();
                    String name = storeRequests_fromBackpacks.getName();
                    list.add("From : " + name + "\n" + "Contents :" + contents);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
