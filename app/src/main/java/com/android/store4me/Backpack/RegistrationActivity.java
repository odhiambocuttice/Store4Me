package com.android.store4me.Backpack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.store4me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegistrationActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText emailId, name, password;
    Button btnsignUp;
    TextView signIntext;
    String BackpackID;
    ProgressDialog progressbar;
    public FirebaseAuth mAuth;
    public FirebaseFirestore mfirestore;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        mfirestore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.Namefield);
        emailId = findViewById(R.id.emailfield);
        password = findViewById(R.id.passwordfield);
        signIntext = findViewById(R.id.AccountExists);
        btnsignUp = findViewById(R.id.signupbutton);

        progressbar = new ProgressDialog(this);

//        if(mAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null && user.isEmailVerified()){
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        btnsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                final String fullname = name.getText().toString();

                if(TextUtils.isEmpty(fullname)){
                    name.setError("Username is Required.");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    emailId.setError("Email is Required.");
                    return;
                }

                if(TextUtils.isEmpty(pwd)){
                    password.setError("Password is Required.");
                    return;
                }

                if(pwd.length() < 6){
                    password.setError("Password Must be >= 6 Characters");
                    return;
               }

                progressbar.setMessage("Registering user");
                progressbar.show();
                mAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Verification email sent to " + user.getEmail(),
                                                        Toast.LENGTH_SHORT).show();
                                                progressbar.hide();
                                                emailId.setEnabled(false);
                                                password.setEnabled(false);
                                                name.setEnabled(false);
                                                btnsignUp.setEnabled(false);

//                                                    Toast.makeText(RegistrationActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                                                String user_id = mAuth.getCurrentUser().getUid();
                                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Backpacks").child(user_id);
                                                current_user_db.child("Email").setValue(email);
                                                current_user_db.child("Name").setValue( fullname);


                                                String token = FirebaseInstanceId.getInstance().getToken();
                                                current_user_db.child("notificationTokens").child(token).setValue(true);
                                            } else {
//                                                Log.e(TAG, "sendEmailVerification", task.getException());
                                                Toast.makeText(getApplicationContext(),
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }else {
                            Toast.makeText(RegistrationActivity.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signIntext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
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
