package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import API.ServerHelper;
import models.PublicModel;
import utilities.ImageConvert;
import utilities.LoadingDialog;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    RoundedImageView previewImage;
    EditText text;
    Button btnCancel, btnPost;

    private Bitmap bmap;
    LoadingDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        text = findViewById(R.id.text);
        btnCancel = findViewById(R.id.btnCancel);
        btnPost = findViewById(R.id.btnPost);
        btnPost.setOnClickListener(this::onClick);
        btnCancel.setOnClickListener(this::onClick);
        previewImage = findViewById(R.id.previewImage);

        dialog = new LoadingDialog(this);

        Uri uri = null;

        try {
            if (getIntent().getStringExtra("uri") != null) {
                uri = Uri.parse(getIntent().getStringExtra("uri"));
                previewImage.setImageURI(uri);
                bmap = getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 800);
            } else if (getIntent().getStringExtra("image") != null) {
                uri = Uri.parse(getIntent().getStringExtra("image"));
                previewImage.setImageURI(uri);
                bmap = RotateBitmap(getResizedBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri), 800), 90);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel) {
            PreviewActivity.super.onBackPressed();
        } else if (v.getId() == R.id.btnPost) {
            dialog.showDialog(R.layout.dialog_post);
            Map<String, String> map = new HashMap<String, String>();
            map.put("text", text.getText().toString());
            map.put("file", "data:image/jpeg;base64," + new ImageConvert().convert(bmap));

            new PublicModel(this).postPublic(map, new ServerHelper.ServerCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    dialog.dismissDialog();
                    Intent intent = new Intent(getApplicationContext(), RootActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onError(JSONObject error) {
                    dialog.dismissDialog();
                    System.out.println("OOPS... " + error);
                }
            });
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
}