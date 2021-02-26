package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import API.ServerHelper;
import adapter.MyPublicAdapter;
import models.PostModel;
import models.PublicModel;
import models.UserModel;
import utilities.DateConvert;
import utilities.StorageLocal;

public class ProfileActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;
    RoundedImageView roundedImageView;
    TextView rname, name, email, nPost;
    StorageLocal storageLocal;
    ImageButton imageButton, btnEdit;

    RecyclerView recyclerView;
    ArrayList<PostModel> postModelList;
    PublicModel publicModel;
    UserModel userModel;
    String _id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        roundedImageView = findViewById(R.id.pf_avatar);
        rname = findViewById(R.id.pf_rname);
        name = findViewById(R.id.pf_name);
        email = findViewById(R.id.pf_email);
        btnEdit = findViewById(R.id.btnEdit);
        storageLocal = new StorageLocal(this);
        imageButton = findViewById(R.id.pf_return);
        postModelList = new ArrayList<PostModel>();
        publicModel = new PublicModel(this);
        userModel = new UserModel(this);
        nPost = findViewById(R.id.nPost);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.post);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );

        _id = getIntent().getStringExtra("_id");
        btnEdit.setVisibility(View.GONE);
        searchData(_id);

    }

    public void searchData(final String _id) {
        try {
            JSONObject object = new JSONObject(storageLocal.readLocalData("my_data"));
            if (_id == null) {
                btnEdit.setVisibility(View.VISIBLE);
                setDataProfile(object);
                findPublic(null);
            } else {
                if(_id.equals(object.getString("_id"))){
                    btnEdit.setVisibility(View.VISIBLE);
                }
                userModel.findUser(_id, new ServerHelper.ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        setDataProfile(response);
                        findPublic(_id);
                    }

                    @Override
                    public void onError(JSONObject error) {
                        String err = "error";
                        try {
                            err = error.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Snackbar.make(android.R.id.content , err, Snackbar.LENGTH_LONG).show();
                    }
                });
                /**-------------------------------------------------------------------------*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDataProfile(JSONObject object) {
        try {
            Picasso.get().load(object.getString("avatar")).into(roundedImageView);
            rname.setText(object.getString("name") + " " + object.getString("surname"));
            name.setText(object.getString("name") + " " + object.getString("surname"));
            email.setText(object.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void findPublic(String _id) {

        if (_id == null) {
            publicModel.findMyPublics(new ServerHelper.ServerCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    addAdapterOnRecyclerView(response);
                }

                @Override
                public void onError(JSONObject error) {
                    System.out.println(error);
                }
            });
        } else {
            publicModel.findUserPublics(_id, new ServerHelper.ServerCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    addAdapterOnRecyclerView(response);
                }

                @Override
                public void onError(JSONObject error) {

                }
            });
        }
    }

    public void addAdapterOnRecyclerView(JSONObject object) {
        try {
            final JSONArray jsonArray = object.getJSONArray("data");
            nPost.setText("" + jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject jsonObject = jsonArray.getJSONObject(i);
                final JSONArray likes = jsonObject.getJSONArray("likes");
                final JSONArray comments = jsonObject.getJSONArray("comments");

                /*postModelList.add(new PostModel(
                        jsonObject.getString("_id"),
                        jsonObject.getString("file"),
                        jsonObject.getString("text"),
                        likes.length(),
                        comments.length()));*/

                DateConvert dateConvert = new DateConvert(jsonObject.getString("createdAt"));

                postModelList.add(new PostModel(
                        jsonObject.getString("_id"),
                        jsonObject.getString("user"),
                        dateConvert.getAgo(),
                        jsonObject.getString("text"),
                        jsonObject.getString("file"),
                        likes.length(),
                        likes,
                        comments.length(),
                        comments));

                recyclerView.removeAllViews();
                MyPublicAdapter myPublicAdapter = new MyPublicAdapter(this, postModelList);
                recyclerView.setAdapter(myPublicAdapter);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //String id = getIntent().getStringExtra("_id");
        /*if (_id == null || !_id.equals("")) {
            searchData(_id);
        } else {
            searchData(null);
        }*/
    }
}