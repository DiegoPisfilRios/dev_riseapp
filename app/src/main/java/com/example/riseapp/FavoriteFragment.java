package com.example.riseapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;
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

public class FavoriteFragment extends Fragment {

    PublicAdapter publicAdapter;
    StorageLocal storageLocal;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    RecyclerView recyclerViewPublic;

    ConstraintLayout constraintLayout;

    int page = 1, limit = 10, total_page = 0;

    ArrayList<PublicModel> PublicList;
    private PublicModel modelPublic;
    private UserModel modelUser;

    JSONArray arrayETP = new JSONArray();
    JSONArray arrayTEM = new JSONArray();
    JSONArray arrayTEMa = new JSONArray();
    JSONArray arrayMR = new JSONArray();
    JSONArray arrayMRa = new JSONArray();

    boolean st_mirror = false;
    boolean st_scroll = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        Window window = getActivity().getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.white));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrima));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.white));
        }
        /**-----------------------------------------------------------------------------------------*/
        storageLocal = new StorageLocal(getContext());

        constraintLayout = view.findViewById(R.id.conf);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kitf);
        Sprite doubleBounce = new CubeGrid();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);

        ProgressBar progressBar1 = (ProgressBar) view.findViewById(R.id.spin_kit1f);
        Sprite doubleBounce1 = new CubeGrid();
        progressBar1.setIndeterminateDrawable(doubleBounce1);

        // Assign variable
        //progressBar = view.findViewById(R.id.pgb_home);
        nestedScrollView = view.findViewById(R.id.scroll_viewf);
        recyclerViewPublic = view.findViewById(R.id.rvFavorite);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        PublicList = new ArrayList<>();

        // Initialize adapter
        publicAdapter = new PublicAdapter(getContext(), PublicList);

        // Set layout manager
        recyclerViewPublic.setLayoutManager(linearLayoutManager);
        recyclerViewPublic.setHasFixedSize(true);

        // Set adapter
        recyclerViewPublic.setAdapter(publicAdapter);



        page = 1;
        loadItems(page, limit);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (page < total_page && st_scroll == true) {
                        page++;
                        progressBar.setVisibility(View.VISIBLE);
                        st_scroll = false;
                        loadItems(page, limit);
                    }
                }
            }
        });
        return view;
    }

    public void loadItems(int page, int limit) {
        modelPublic = new PublicModel(getContext());
        modelPublic.findFavorite(page, limit, new ServerHelper.ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.GONE);
                try {
                    total_page = Integer.parseInt(response.getString("totalPages"));
                    JSONArray jsonArray = response.getJSONArray("docs");
                    if(Integer.parseInt(response.getString("totalDocs")) > 0){
                        parseResult(jsonArray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject error) {
                String err = "Algo salio mal. Comunicate con los desarrolladores.";
                try {
                    err = error.getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Snackbar.make(getActivity().findViewById(android.R.id.content), err, Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    public void parseResult(JSONArray object) {
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
        modelUser = new UserModel(getContext());
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
            arrayTEM = new JSONArray();
            modelUser.findUsers(data, new ServerHelper.ServerCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    String rs = storageLocal.readLocalData("usersf");
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
                            storageLocal.writeLocalData("usersf", newUsers.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        storageLocal.writeLocalData("usersf", response.toString());
                    }
                    // charge adapter
                    try {
                        JSONObject dataUsers = new JSONObject(storageLocal.readLocalData("usersf"));
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
                        publicAdapter = new PublicAdapter(getContext(), PublicList);
                        // Set adapter
                        recyclerViewPublic.setAdapter(publicAdapter);
                        publicAdapter.setOnClickItemListener(new PublicAdapter.OnItemClickListener() {
                            @Override
                            public void onItemDelete(int position) {
                                PublicList.remove(position);
                                publicAdapter.notifyItemRemoved(position);
                                goHome();
                            }
                        });
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

    public void goHome(){
        if(publicAdapter.getItemCount() == 0){

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        st_scroll = true;
        storageLocal.writeLocalData("usersf", "");
    }
}