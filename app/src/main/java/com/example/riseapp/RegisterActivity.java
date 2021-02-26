package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import API.ServerHelper;
import utilities.DatePickerFragment;
import models.UserModel;
import utilities.LoadingDialog;
import utilities.StorageLocal;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText email, password, name, surname;
    private Button btnBirth, btnReg;
    private ImageButton btnR;
    private String birth = "";
    private LoadingDialog dialog;
    private StorageLocal storageLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /** Cambia color del statusBar ------------------------------------------------------------*/
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        /**----------------------------------------------------------------------------------------*/

        storageLocal = new StorageLocal(this);
        dialog = new LoadingDialog(this);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPass);
        name = findViewById(R.id.etName);
        surname = findViewById(R.id.etSurame);
        btnReg = findViewById(R.id.btnReg);
        btnBirth = findViewById(R.id.btnBirth);
        btnR = findViewById(R.id.btnR);

        btnBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(
                        email.getText().toString().equals("") ||
                        password.getText().toString().equals("") ||
                        name.getText().toString().equals("") ||
                        surname.getText().toString().equals("") ||
                        birth.equals("")
                ){
                    Snackbar.make(v, "Completar información 🙄", Snackbar.LENGTH_LONG).show();
                }else{
                    dialog.showDialog(R.layout.dialog_register);// Muestra mensaje
                    JSONObject data = new JSONObject();// Crea objeto JSON y lo carga de datos
                    try {
                        data.put("email", email.getText().toString());
                        data.put("password", password.getText().toString());
                        data.put("name", name.getText().toString());
                        data.put("surname", surname.getText().toString());
                        data.put("birth", birth);
                    } catch( JSONException e){
                        e.printStackTrace();
                    }

                    new UserModel(RegisterActivity.this).signUp(data, new ServerHelper.ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) throws JSONException {
                            dialog.dismissDialog();
                            storageLocal.writeLocalData("app_token", response.getString("token"));// guarda token

                            Intent intent = new Intent(getApplicationContext(), RootActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onError(JSONObject error) throws JSONException {
                            dialog.dismissDialog();
                            Snackbar.make(v, error.getString("msg"), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.super.onBackPressed();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int mes = month + 1;
        String m = ""+mes;

        if(month < 10){
            m = "0"+mes;
        }

        String d = ""+dayOfMonth;
        if(dayOfMonth < 10){
            d = "0"+dayOfMonth;
        }

        birth = ""+year+"-"+m+"-"+d;
        btnBirth.setText(""+d+"/"+m+"/"+year);// cambia texto del botón
    }
}
