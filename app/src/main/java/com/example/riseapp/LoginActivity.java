package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import API.ServerHelper;
import models.UserModel;
import utilities.LoadingDialog;
import utilities.StorageLocal;

public class LoginActivity extends AppCompatActivity {

    private EditText etuser;
    private EditText etpass;
    private Button btnin;
    private Button btnup;
    private LoadingDialog dialog;
    private StorageLocal storageLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /** Cambia color del statusBar ------------------------------------------------------------*/
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        /**----------------------------------------------------------------------------------------*/

        dialog = new LoadingDialog(this);
        storageLocal = new StorageLocal(this);

        etuser = findViewById(R.id.etUser);
        etpass = findViewById(R.id.etPass);
        btnin = findViewById(R.id.btnIn);
        btnup = findViewById(R.id.btnUp);

        btnin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.showDialog(R.layout.dialog_login);// Muestra mensaje

                JSONObject data = new JSONObject();// Crea objeto JSON y lo carga de datos
                try {
                    data.put("email", etuser.getText().toString());
                    data.put("password", etpass.getText().toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }

                new UserModel(getApplicationContext()).signIn(data, new ServerHelper.ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        dialog.dismissDialog();// Oculta mensaje
                        storageLocal.writeLocalData("app_token", response.getString("token"));// guarda token

                        Intent intent = new Intent( getApplicationContext() , RootActivity.class);// ir a principal
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onError(JSONObject error) throws JSONException {
                        dialog.dismissDialog();// Oculta mensaje
                        String msg = error.getString("msg");
                        Snackbar.make( v, msg, Snackbar.LENGTH_LONG).show();// Muestra el error
                    }
                });
            }
        });

        btnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( LoginActivity.this , RegisterActivity.class);// ir a registrar
                startActivity(intent);
            }
        });
    }
}
