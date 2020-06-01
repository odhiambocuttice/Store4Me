package com.android.store4me;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class StoresLoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin;
    TextView mRegistration;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    ProgressDialog progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_login);
        mAuth = FirebaseAuth.getInstance();


        progressbar = new ProgressDialog(this);
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(StoresLoginActivity.this, Store_locationActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);

        mLogin = (Button) findViewById(R.id.login);
        mRegistration = (TextView) findViewById(R.id.registration);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Registration3Activity.class));
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String email = emailId.getText().toString();
//                String pwd = password.getText().toString();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if (email.isEmpty()) {
                    mEmail.setError("Please enter your email");
                    mEmail.requestFocus();
                } else if (password.isEmpty()) {
                    mPassword.setError("Please enter your password");
                    mPassword.requestFocus();

                } else if (password.isEmpty() && email.isEmpty()) {
                    Toast.makeText(StoresLoginActivity.this, "Empty fields", Toast.LENGTH_SHORT).show();


                }

                else if (!(password.isEmpty() && email.isEmpty())) {

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(StoresLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(StoresLoginActivity.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                            } else {
                                progressbar.setMessage("Login User");
                                progressbar.show();

                                Intent intToHome = new Intent(StoresLoginActivity.this, StoreProfileActivity.class);
                                startActivity(intToHome);

                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
//
                                String token = FirebaseInstanceId.getInstance().getToken();
                                current_user_db.child("notificationTokens").child(token).setValue(true);
                            }
                        }
                    });
                } else {
                    Toast.makeText(StoresLoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();

                }
            }
        });

//        mLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String email = mEmail.getText().toString();
//                final String password = mPassword.getText().toString();
//                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(StoresLoginActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(!task.isSuccessful()){
//                            Toast.makeText(StoresLoginActivity.this, "sign in error", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            String user_id = mAuth.getCurrentUser().getUid();
//                            DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Stores").child(user_id);
//
//                            String token = FirebaseInstanceId.getInstance().getToken();
//                            current_user_db.child("notificationTokens").child(token).setValue(true);
//                        }
//                    }
//                });
//
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

}
