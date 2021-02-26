package com.example.riseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import API.ServerHelper;
import models.UserModel;
import utilities.DateConvert;
import utilities.DatePickerFragment;
import utilities.ImageConvert;
import utilities.LoadingDialog;
import utilities.StorageLocal;

public class EditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextInputEditText email, password, name, surname;
    Bitmap photo = null;
    Button btnBirth, btnRe;
    ImageButton pic, btnR;
    RoundedImageView perfil;
    LoadingDialog dialog;
    private StorageLocal storageLocal;
    private BottomSheetDialog bottomSheetDialog;

    private final String CARPETA_RAIZ = "RiseApp/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ + "Pictures";

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int GALLERY_PERMISSION_CODE = 1001;

    private static final int CAMERA_CODE = 0;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private Uri photoURI;
    private String mCurrentPhotoPath;
    String birth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        UserModel model = new UserModel(this);
        dialog = new LoadingDialog(this);
        storageLocal = new StorageLocal(this);
        pic = findViewById(R.id.btnPic);

        email = (TextInputEditText) findViewById(R.id.correo);
        password = (TextInputEditText) findViewById(R.id.contra);
        name = (TextInputEditText) findViewById(R.id.nombres);
        surname = (TextInputEditText) findViewById(R.id.apellidos);
        perfil = findViewById(R.id.ed_avatar);
        btnRe = (Button) findViewById(R.id.btnRege);
        btnBirth = (Button) findViewById(R.id.btnBirthe);
        btnR = (ImageButton) findViewById(R.id.pf_returne);

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.super.onBackPressed();
            }
        });

        btnBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(EditActivity.this, R.style.BottomSheetTheme);

                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.edit_sheet,
                        (ViewGroup) findViewById(R.id.edit_sheet));

                sheetView.findViewById(R.id.edCamara).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                                String[] permissions = {Manifest.permission.CAMERA};
                                requestPermissions(permissions, CAMERA_PERMISSION_CODE);
                            } else {
                                pickImageFromCamera();
                            }
                        } else {
                            pickImageFromCamera();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });

                sheetView.findViewById(R.id.edGaleria).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                                requestPermissions(permissions, GALLERY_PERMISSION_CODE);
                            } else {
                                pickImageFromGallery();
                            }
                        } else {
                            pickImageFromGallery();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });

        btnRe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.showDialog(R.layout.dialog_update);
                //Update
                try {
                    JSONObject map = new JSONObject();
                    map.put("email", email.getText().toString());
                    map.put("name", name.getText().toString());
                    map.put("surname", surname.getText().toString());
                    map.put("birth", birth);
                    if(photo != null){
                        map.put("avatar","data:image/jpeg;base64," + new ImageConvert().convert(photo));
                    }

                    if(!password.getText().toString().isEmpty()){
                        map.put("password", password.getText().toString());
                    }

                    model.putUser(map, new ServerHelper.ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            System.out.println(response);
                            model.MyData(new ServerHelper.ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    dialog.dismissDialog();
                                    System.out.println(response);
                                    storageLocal.writeLocalData("my_data", response.toString());
                                    Intent intent = new Intent(EditActivity.this, RootActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void onError(JSONObject error) {
                                    dialog.dismissDialog();
                                    String err = "error";
                                    try {
                                        err = error.getString("msg");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Snackbar.make(findViewById(android.R.id.content), err, Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onError(JSONObject error) {
                            dialog.dismissDialog();
                        }
                    });

                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });

        drawProfile();
    }

    public void drawProfile() {
        try {
            JSONObject object = new JSONObject(storageLocal.readLocalData("my_data"));
            Picasso.get().load(object.getString("avatar")).into(perfil);
            email.setText(object.getString("email"));
            name.setText(object.getString("name"));
            surname.setText(object.getString("surname"));

            LocalDateTime time = new DateConvert().getLocalDateTime(object.getString("birth"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String dia = "";
                String mes = "";
                if ((time.getDayOfMonth() + 1) < 10) {
                    dia = "0";
                }
                if (time.getMonthValue() < 10) {
                    mes = "0";
                }
                birth = time.getYear()+"-"+mes + time.getMonthValue()+"-"+dia + (time.getDayOfMonth() + 1);
                btnBirth.setText(dia + (time.getDayOfMonth() + 1) + "/" + mes + time.getMonthValue() + "/" + time.getYear());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void pickImageFromCamera() {
        dispatchTakePictureIntent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case GALLERY_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(EditActivity.this, "Permiso denegado...", Toast.LENGTH_LONG).show();
                }
            }
            case CAMERA_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromCamera();
                } else {
                    Toast.makeText(EditActivity.this, "Permiso denegado...", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "MyPicture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
                photoURI = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                uri = Uri.parse(data.getDataString());
                perfil.setImageURI(uri);
                photo = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 800);
            } else if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_CODE) {
                uri = Uri.parse(photoURI.toString());
                perfil.setImageURI(uri);
                photo = RotateBitmap(getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 800), 90);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,
                width,
                height,
                true);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int mes = month + 1;
        String m = "" + mes;
        if (month < 10) {
            m = "0" + mes;
        }


        String d = "" + dayOfMonth;
        if (dayOfMonth < 10) {
            d = "0" + dayOfMonth;
        }

        birth = year+"-"+m+"-"+d;
        btnBirth.setText("" + d + "/" + m + "/" + year);
    }
}