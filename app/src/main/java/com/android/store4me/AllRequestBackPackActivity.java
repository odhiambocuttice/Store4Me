package com.android.store4me;

import android.os.Bundle;
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

public class AllRequestBackPackActivity extends AppCompatActivity {


    private Toolbar mtoolbar;
    private ListView mRequestList;

    private DatabaseReference mRequestReference;
    private FirebaseAuth mAuth;

    String user_id, BackPackID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_request_back_pack);


        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        getSupportActionBar().setTitle("All Requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_id = getIntent().getStringExtra("user_id");


        mAuth = FirebaseAuth.getInstance();
        BackPackID = mAuth.getCurrentUser().getUid();



        mRequestList = (ListView) findViewById(R.id.all_requests);
        final ArrayList<String> list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.store_request_list, list);
        mRequestList.setAdapter(adapter);

//        AdapterView.OnItemClickListener listClickedHandler = new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(AllRequestsActivity.this, RequestReceivedActivity.class);
//                intent.putExtra("user_id", user_id);
//                startActivity(intent);
//
//            }
//        };

//
//        mRequestList.setOnItemClickListener(listClickedHandler);
//        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Stores");
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                list.clear();
//                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                    StoreRequest_FromBackpacks2 storeRequest_fromBackpacks2 =
//                            dataSnapshot1.getValue(StoreRequest_FromBackpacks2.class);
//                    String shopname = storeRequest_fromBackpacks2.getShopname();
//                    String phoneNumber = storeRequest_fromBackpacks2.getPhoneNumber();
//                    String price = storeRequest_fromBackpacks2.getPrice();
//                    list.add("To: " + shopname + "\n" +"Mobile : " + phoneNumber
//                            + "Contents :" + price);
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("Exception FB", databaseError.toException());
//
//
//            }
//        });

        mRequestReference = FirebaseDatabase.getInstance().getReference().child("BackpackRequest")
        .child("iLtTMaekweMDGPo9SrWdWhR3eDd2");
        mRequestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    StoreRequests_FromBackpacks storeRequests_fromBackpacks =
                            snapshot.getValue(StoreRequests_FromBackpacks.class);
                    String backpackOwner = storeRequests_fromBackpacks.getBackpackOwner();
                    String contents = storeRequests_fromBackpacks.getBackPackContents();
                    String  name = storeRequests_fromBackpacks.getShopname();
                    list.add("Shopname : " + name + "\n" + "Contents :" + contents);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
