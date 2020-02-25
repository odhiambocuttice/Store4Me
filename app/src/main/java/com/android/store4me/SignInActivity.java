package com.android.store4me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    TextInputEditText emailId;
    public EditText password;
    Button btnLoginIn;
    TextView signIntext;
    public FirebaseAuth mAuth;
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

        mAuthlistener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mfirebaseUser = mAuth.getCurrentUser();
                if ( mfirebaseUser != null){
                    Toast.makeText(SignInActivity.this, "Logged in!!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(SignInActivity.this, "Please Log in!!", Toast.LENGTH_SHORT).show();
                }

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
                                Intent intToHome = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intToHome);
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
}
