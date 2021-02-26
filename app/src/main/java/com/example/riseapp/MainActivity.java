package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import utilities.StorageLocal;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.pink));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.aqua));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.black));
        }


        // Splash Screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StorageLocal storageLocal = new StorageLocal(getApplicationContext());
                String req = storageLocal.readLocalData("app_token");

                Intent intent;

                if ( !req.equals("") ){
                    intent = new Intent( MainActivity.this, RootActivity.class);
                }else {
                    intent = new Intent( MainActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}
