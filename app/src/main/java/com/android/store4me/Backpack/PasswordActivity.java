package com.android.store4me.Backpack;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.store4me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {
    TextInputEditText emailId;
    Button btnResetPassword;
    ProgressBar progressBar;
    Toolbar toolbar;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        toolbar = findViewById(R.id.toolbar);
        emailId = findViewById(R.id.email_field);
        btnResetPassword = findViewById(R.id.resetbutton);
        progressBar = findViewById(R.id.progressBar);

//        toolbar.setTitle("Reset Password");

        firebaseAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(emailId.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if(task.isSuccessful()){
                                    Toast.makeText(PasswordActivity.this,
                                            "Reset Link Sent to Your Email",Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(PasswordActivity.this,
                                            task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            }
        });
    }
}
