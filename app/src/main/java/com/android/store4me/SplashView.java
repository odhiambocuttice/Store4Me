package com.android.store4me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashView extends AppCompatActivity {

    private static int SPLASHCREEN = 4000;

    public FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthlistener;


    Animation topAnim, BottomAmin, SideAnim;
    ImageView shelfimage, shelfimage2;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_view);

        mAuth = FirebaseAuth.getInstance();

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        BottomAmin = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        SideAnim = AnimationUtils.loadAnimation(this,R.anim.side_animation);

        shelfimage = findViewById(R.id.imageView);
        shelfimage2 = findViewById(R.id.shelf2);
        logo = findViewById(R.id.textView2);
        slogan = findViewById(R.id.textView3);

        shelfimage.setAnimation(topAnim);
        shelfimage2.setAnimation(SideAnim);
        logo.setAnimation(BottomAmin);
        slogan.setAnimation(BottomAmin);

        mAuthlistener =  new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mfirebaseUser = mAuth.getCurrentUser();
                if (mfirebaseUser != null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent (SplashView.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, SPLASHCREEN);
                }else if (mfirebaseUser == null){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent signup = new Intent (SplashView.this, RegistrationActivity.class);
                            startActivity(signup);
                            finish();
                        }
                    }, SPLASHCREEN);

                }
            }
        };



    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthlistener);
    }
}
