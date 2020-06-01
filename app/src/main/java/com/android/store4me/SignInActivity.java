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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText emailId;
    public EditText password;
    Button btnLoginIn;
    TextView signIntext;
    public FirebaseAuth mAuth;
    ProgressDialog progressbar;
    private FirebaseAuth.AuthStateListener mAuthlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email_field);
        password = findViewById(R.id.passwordfield);
        signIntext = findViewById(R.id.Signup);
        btnLoginIn = findViewById(R.id.signinbutton);

        progressbar = new ProgressDialog(this);



        mAuthlistener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mfirebaseUser = mAuth.getCurrentUser();
                if ( mfirebaseUser != null){
                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(i);
                    Toast.makeText(SignInActivity.this, "Logged in!!", Toast.LENGTH_SHORT).show();
                }
//                else {
//                    Toast.makeText(SignInActivity.this, "Please Log in!!", Toast.LENGTH_SHORT).show();
//                }

            }
        };
        btnLoginIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter your email");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();

                }
                else if (pwd.isEmpty() && email.isEmpty()){
                    Toast.makeText(SignInActivity.this,"Empty fields", Toast.LENGTH_SHORT).show();

                }
                else if (!(pwd.isEmpty() && email.isEmpty())){

                    mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignInActivity.this,"Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressbar.setMessage("Login User");
                                progressbar.show();

                                Intent intToHome = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intToHome);

                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Backpacks").child(user_id);
//
                                String token = FirebaseInstanceId.getInstance().getToken();
                                current_user_db.child("notificationTokens").child(token).setValue(true);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(SignInActivity.this,"Error Occured", Toast.LENGTH_SHORT).show();

                }
            }
        });
        signIntext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intSignup = new Intent(SignInActivity.this, RegistrationActivity.class);
                startActivity(intSignup);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthlistener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthlistener);
    }
}
