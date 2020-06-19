package com.android.store4me;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StoreBackpackOption extends AppCompatActivity {

    CheckBox checkBoxstore, checkBoxbackpack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_store_backpack_option);

        checkBoxstore = (CheckBox) findViewById(R.id.store);
        checkBoxbackpack = (CheckBox) findViewById(R.id.backpack);

        checkBoxstore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxbackpack.setEnabled(false);
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
                    Toast.makeText(StoreBackpackOption.this, "Backpack User Selected", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(StoreBackpackOption.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
}
