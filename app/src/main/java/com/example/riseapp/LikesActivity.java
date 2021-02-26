package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import API.ServerHelper;
import adapter.CommentAdapter;
import adapter.LikeAdapter;
import adapter.PublicAdapter;
import models.CommentModel;
import models.LikeModel;
import models.PublicModel;
import models.UserModel;

public class LikesActivity extends AppCompatActivity {

    JSONArray Json_likes;
    RecyclerView recyclerViewLikes;
    ArrayList<LikeModel> LikeList;
    LikeAdapter adapter;
    TextView nlikes;
    UserModel userModel;
    PublicModel publicModel;
    ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        back = findViewById(R.id.lk_return);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikesActivity.super.onBackPressed();
            }
        });
        userModel = new UserModel(this);
        publicModel = new PublicModel(this);
        recyclerViewLikes = findViewById(R.id.recyclerLikes);
        nlikes = findViewById(R.id.nlikes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewLikes.setLayoutManager(linearLayoutManager);
        LikeList = new ArrayList<>();

        // Initialize adapter
        adapter = new LikeAdapter(this, LikeList);

        // Set layout manager
        recyclerViewLikes.setLayoutManager(linearLayoutManager);
        recyclerViewLikes.setHasFixedSize(true);

        // Set adapter
        recyclerViewLikes.setAdapter(adapter);
        drawLikes();
    }

    public void drawLikes() {


        publicModel.findPublic(getIntent().getStringExtra("likes"), new ServerHelper.ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray likes = response.getJSONArray("likes");
                    nlikes.setText("" + likes.length());

                    JSONObject data = new JSONObject();
                    JSONArray likes_n = new JSONArray();
                    for (int y = 0; y < likes.length(); y++) {
                        JSONObject jsonObject = likes.getJSONObject(y);
                        likes_n.put(jsonObject.getString("user"));
                    }

                    data.put("users", likes_n);
                    userModel.findUsers(data, new ServerHelper.ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            try {
                                System.out.print(response);
                                JSONArray itemsUsers = response.getJSONArray("docs");

                                for (int i = 0; i < itemsUsers.length(); i++) {
                                    final JSONObject jsonObject = itemsUsers.getJSONObject(i);
                                    LikeList.add(new LikeModel(
                                            jsonObject.getString("name") + " " + jsonObject.getString("surname"),
                                            jsonObject.getString("avatar"),
                                            false
                                    ));

                                }
                                // Initialize adapter
                                adapter = new LikeAdapter(getApplicationContext(), LikeList);
                                // Set adapter
                                recyclerViewLikes.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(JSONObject error) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject error) {

            }
        });


    }
}