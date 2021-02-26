package com.example.riseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import API.ServerHelper;
import adapter.CommentAdapter;
import adapter.PublicAdapter;
import models.CommentModel;
import models.PublicModel;
import models.UserModel;
import utilities.StorageLocal;

public class DetailsActivity extends AppCompatActivity {

    private PublicModel publicModel;
    private UserModel modelUser;
    private String _id;

    CommentAdapter commentAdapter;
    RecyclerView recyclerViewComment;
    ArrayList<CommentModel> CommentList;
    JSONArray Json_likes;
    ScrollView scrollDetails;

    private CommentModel commentModel;
    ImageButton retornar, send;
    TextView name, description, ago, comment, nCom;
    CheckBox like;
    LinearLayout noComment;
    ImageButton likes;
    RoundedImageView avatar, picture;

    StorageLocal storageLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.black));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }
        storageLocal = new StorageLocal(this);
        /**-------------------------------------------------------------*/
        likes = findViewById(R.id.dt_likes);
        retornar = findViewById(R.id.btnVolver);
        description = findViewById(R.id.dt_description);
        name = findViewById(R.id.dt_name);
        noComment = findViewById(R.id.noComments);
        avatar = findViewById(R.id.dt_avatar);
        picture = findViewById(R.id.dt_picture);
        ago = findViewById(R.id.ago);
        like = findViewById(R.id.iconlike);
        send = findViewById(R.id.btnSend);
        comment = findViewById(R.id.etComment);
        nCom = findViewById(R.id.nCom);
        scrollDetails = findViewById(R.id.scroll_details);

        publicModel = new PublicModel(this);
        modelUser = new UserModel(this);

        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewComment.setLayoutManager(linearLayoutManager);
        recyclerViewComment.setHasFixedSize(true);

        CommentList = new ArrayList<>();
        drawData();
        //scrollDetails.fullScroll(ScrollView.FOCUS_DOWN);

        retornar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.onBackPressed();
            }
        });

        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( v.getContext(), LikesActivity.class);
                intent.putExtra("likes", _id);//Json_likes.toString()
                startActivity(intent);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageLocal.writeLocalData("resume_home","ok");
                publicModel.putLikePublic(_id, new ServerHelper.ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            System.out.println(response.getString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(JSONObject error) {
                        System.out.println("NO LIKE: " + error);
                    }
                });
                setColorChek((CheckBox) v);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storageLocal.writeLocalData("resume_home","ok");
                JSONObject js_comment = new JSONObject();
                try {
                    js_comment.put("comment", comment.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                publicModel.putCommentPublic(_id, js_comment, new ServerHelper.ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            System.out.println(response.getString("msg"));
                            comment.setText("");
                            drawComments();
                            if(commentAdapter != null){
                                scrollDetails.fullScroll(ScrollView.FOCUS_DOWN);
                                recyclerViewComment.scrollToPosition(commentAdapter.getItemCount() - 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(JSONObject error) {
                        System.out.println("Comment: " + error);
                    }
                });
            }
        });
    }

    public void drawData() {
        final JSONObject[] objectuser = {null};
        try {
            JSONObject object = new JSONObject(getIntent().getStringExtra("data"));
            _id = object.getString("_id");

            try{
                 objectuser[0] = new JSONObject(object.getString("user"));
                description.setText(object.getString("description"));
                name.setText(objectuser[0].getString("name") + " " + objectuser[0].getString("surname"));
                Picasso.get().load(objectuser[0].getString("avatar")).into(avatar);
                Picasso.get().load(object.getString("file")).into(picture);
                ago.setText(object.getString("ago"));
                nCom.setText(""+object.getInt("ncomments"));
                like.setChecked(object.getBoolean("st_like"));
                Json_likes = new JSONArray(object.getString("likes"));
                setColorChek(like);
                drawComments();
            }catch (JSONException e) {
                modelUser.findUser(object.getString("user"), new ServerHelper.ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            objectuser[0] = response;
                            description.setText(object.getString("description"));
                            name.setText(objectuser[0].getString("name") + " " + objectuser[0].getString("surname"));
                            Picasso.get().load(objectuser[0].getString("avatar")).into(avatar);
                            Picasso.get().load(object.getString("file")).into(picture);
                            ago.setText(object.getString("ago"));
                            nCom.setText(""+object.getInt("ncomments"));
                            like.setChecked(object.getBoolean("st_like"));
                            Json_likes = new JSONArray(object.getString("likes"));
                            setColorChek(like);
                            drawComments();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(JSONObject error) {

                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void drawComments() {
        publicModel.findComments(_id, new ServerHelper.ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                System.out.println(response);
                CommentList.clear();
                try {
                    final JSONArray comments = response.getJSONArray("comments");
                    for (int i = 0; i < comments.length(); i++) {
                        final JSONObject jsonObject = comments.getJSONObject(i);
                        final JSONObject user = new JSONObject(jsonObject.getString("user"));
                        try {
                            CommentList.add(new CommentModel(
                                    user.getString("name"),
                                    user.getString("surname"),
                                    user.getString("avatar"), jsonObject.getString("comment")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (i + 1 == comments.length()) {
                            commentAdapter = new CommentAdapter(DetailsActivity.this, CommentList);
                            recyclerViewComment.setAdapter(commentAdapter);
                            nCom.setText(""+commentAdapter.getItemCount());
                        }
                    }
                    if (comments.length() == 0) {
                        noComment.setVisibility(View.VISIBLE);
                        recyclerViewComment.setVisibility(View.GONE);
                    } else if (commentAdapter.getItemCount() > 0) {
                        noComment.setVisibility(View.GONE);
                        recyclerViewComment.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject error) {

            }
        });
    }

    public void setColorChek(CheckBox chckLike) {
        if (chckLike.isChecked()) {
            chckLike.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#F23B5F")));
        } else {
            chckLike.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#B7B7B7")));
        }
    }
}