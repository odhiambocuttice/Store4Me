package com.android.store4me.SplashView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.store4me.R;
import com.android.store4me.Backpack.SignInActivity;
import com.android.store4me.Store.StoresLoginActivity;

public class StoreBackpackOption extends AppCompatActivity {

    CheckBox checkBoxstore, checkBoxbackpack;
    TextView manualText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_store_backpack_option);

        checkBoxstore = (CheckBox) findViewById(R.id.store);
        checkBoxbackpack = (CheckBox) findViewById(R.id.backpack);
        manualText = (TextView) findViewById(R.id.instructionsText);

        checkBoxstore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxbackpack.setEnabled(false);
                    manualText.setEnabled(false);
                    Toast.makeText(StoreBackpackOption.this, "Store User Selected", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(StoreBackpackOption.this, StoresLoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        checkBoxbackpack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxstore.setEnabled(false);
                    manualText.setEnabled(false);
                    Toast.makeText(StoreBackpackOption.this, "Backpack User Selected", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(StoreBackpackOption.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        manualText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreBackpackOption.this, OnboadInstructionsActivity.class);
                startActivity(intent);

            }
        });

    }
}
