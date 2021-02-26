package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import API.ServerHelper;
import adapter.PublicAdapter;
import models.PublicModel;
import models.UserModel;
import utilities.DateConvert;
import utilities.StorageLocal;

public class SearchActivity extends AppCompatActivity {

    ImageButton volver, btnclean;
    EditText search;
    private PublicModel modelPublic;
    private UserModel modelUser;
    ProgressBar progressBar;
    ConstraintLayout constraintLayout;
    ConstraintLayout conte;

    PublicAdapter publicAdapter;
    StorageLocal storageLocal;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerViewPublic;
    ArrayList<PublicModel> PublicList;

    JSONArray arrayETP = new JSONArray();
    JSONArray arrayTEM = new JSONArray();
    JSONArray arrayTEMa = new JSONArray();
    JSONArray arrayMR = new JSONArray();
    JSONArray arrayMRa = new JSONArray();

    boolean st_mirror = false;
    boolean st_scroll = true;

    int page = 1, limit = 10, total_page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        modelPublic = new PublicModel(this);
        storageLocal = new StorageLocal(this);
        constraintLayout = findViewById(R.id.cont);
        conte = findViewById(R.id.conte);
        volver = findViewById(R.id.btnVolver);
        btnclean = findViewById(R.id.btnclean);
        btnclean.setVisibility(View.GONE);
        btnclean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                btnclean.setVisibility(View.GONE);
            }
        });

        search = (EditText) findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(search.getText().toString().length() > 0){
                    btnclean.setVisibility(View.VISIBLE);
                }
            }
        });

        search.requestFocus();
        progressBar = (ProgressBar) findViewById(R.id.spin);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        // Assign variable
        //progressBar = view.findViewById(R.id.pgb_home);
        nestedScrollView = findViewById(R.id.scroll_v);
        recyclerViewPublic = findViewById(R.id.recyclers);



        page = 1;

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;

                        constraintLayout.setVisibility(View.VISIBLE);
                        if (actionId == EditorInfo.IME_ACTION_SEARCH && !search.getText().toString().equals("")) {
                            conte.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            PublicList = new ArrayList<>();

                            // Initialize adapter
                            publicAdapter = new PublicAdapter(getApplicationContext(), PublicList);

                            // Set layout manager
                            recyclerViewPublic.setLayoutManager(linearLayoutManager);
                            recyclerViewPublic.setHasFixedSize(true);

                            // Set adapter
                            recyclerViewPublic.setAdapter(publicAdapter);
                            modelPublic.findSearchPublics(search.getText().toString(), page, limit, new ServerHelper.ServerCallback() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    System.out.println(response);
                                    progressBar.setVisibility(View.GONE);
                                    constraintLayout.setVisibility(View.GONE);
                                    try {
                                        total_page = Integer.parseInt(response.getString("totalPages"));
                                        JSONArray jsonArray = response.getJSONArray("docs");
                                        parseResult(jsonArray);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(JSONObject error) {
                                    progressBar.setVisibility(View.GONE);
                                    constraintLayout.setVisibility(View.GONE);
                                }
                            });

                            handled = true;
                        }
                        return handled;
                    }
                });

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.onBackPressed();
            }
        });
    }

    public void parseResult(JSONArray object) {
        PublicList.clear();

        if(object.length() == 0){
            conte.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < object.length(); i++) {
            try {
                final JSONObject jsonObject = object.getJSONObject(i);
                final JSONArray likes = jsonObject.getJSONArray("likes");
                final JSONArray comments = jsonObject.getJSONArray("comments");

                DateConvert dateConvert = new DateConvert(jsonObject.getString("createdAt"));

                PublicList.add(new PublicModel(
                        jsonObject.getString("_id"),
                        jsonObject.getString("user"),
                        dateConvert.getAgo(),
                        jsonObject.getString("text"),
                        jsonObject.getString("file"),
                        likes.length(),
                        likes,
                        comments.length(),
                        comments));
                arrayETP.put(jsonObject.getString("user"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        loadUsers();
    }

    public void loadUsers() {
        modelUser = new UserModel(getApplicationContext());
        try {
            int i = 0;
            do {
                boolean rep = true;
                if (arrayTEM.length() == 0)
                    arrayTEM.put((arrayETP.get(i)));

                for (int q = 0; q < arrayTEM.length(); q++) {
                    if (arrayETP.get(i).equals(arrayTEM.get(q)))
                        rep = false;
                }

                if (rep)
                    arrayTEM.put((arrayETP.get(i)));

                i++;
            } while (i < arrayETP.length());
            arrayETP = new JSONArray();
            if (arrayMR.length() > 0)
                st_mirror = true;

            Vector index = new Vector();
            for (int t = 0; t < arrayTEM.length(); t++) {
                if (st_mirror) {
                    for (int d = 0; d < arrayMR.length(); d++) {
                        if (arrayTEM.getString(t).equals(arrayMR.getString(d))) {
                            index.add(t);
                        }
                    }
                } else {
                    arrayMR.put(arrayTEM.getString(t));
                }
            }
            // remove
            for (int b = 0; b < index.size(); b++) {
                int n = (int) index.elementAt(b);
                if (b > 0)
                    n--;
                arrayTEM.remove(n);
            }
            // put
            if (st_mirror) {
                for (int b = 0; b < arrayTEM.length(); b++) {
                    arrayMR.put(arrayTEM.getString(b));
                }
            }
            JSONObject data = new JSONObject();
            data.put("users", arrayTEM);
            System.out.println(arrayTEM);
            arrayTEM = new JSONArray();
            modelUser.findUsers(data, new ServerHelper.ServerCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    String rs = storageLocal.readLocalData("usersh");
                    if (!rs.equals("")) {
                        try {
                            JSONObject objectLocal = new JSONObject(rs);
                            JSONArray arrayLocal = objectLocal.getJSONArray("docs");
                            JSONArray arrayResponse = response.getJSONArray("docs");
                            for (int i = 0; i < arrayResponse.length(); i++) {
                                JSONObject respon = arrayResponse.getJSONObject(i);
                                arrayLocal.put(respon);
                            }
                            JSONObject newUsers = new JSONObject();
                            newUsers.put("docs", arrayLocal);
                            storageLocal.writeLocalData("usersh", newUsers.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        storageLocal.writeLocalData("usersh", response.toString());
                    }
                    // charge adapter
                    try {
                        JSONObject dataUsers = new JSONObject(storageLocal.readLocalData("usersh"));
                        JSONArray itemsUsers = dataUsers.getJSONArray("docs");

                        for (int r = 0; r < PublicList.size(); r++) {
                            String userID = PublicList.get(r).getUser();
                            for (int b = 0; b < itemsUsers.length(); b++) {
                                JSONObject user = itemsUsers.getJSONObject(b);
                                if (userID.equals(user.getString("_id"))) {
                                    PublicList.get(r).setUser(user.toString());
                                }
                            }
                        }
                        // Initialize adapter
                        publicAdapter = new PublicAdapter(getApplicationContext(), PublicList);
                        // Set adapter
                        recyclerViewPublic.setAdapter(publicAdapter);
                        st_scroll = true;
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
}