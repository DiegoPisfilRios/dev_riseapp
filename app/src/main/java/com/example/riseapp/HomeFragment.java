package com.example.riseapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
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
import utilities.StorageLocal;
import utilities.DateConvert;

/**
 * A simple {@link Fragment} subclass.
 */

public class HomeFragment extends Fragment {
    // Se declaran variables y crean objetos
    PublicAdapter publicAdapter;
    StorageLocal storageLocal;
    NestedScrollView nestedScrollView;
    ProgressBar progressBar;
    RecyclerView recyclerViewPublic;
    ConstraintLayout constraintLayout;

    int page = 1, limit = 10, total_page = 0;

    ArrayList<PublicModel> PublicList;
    ImageButton search;
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /** Cambia el color del statusBar ----------------------------------------------------------*/
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

        // Inicializa variables
        storageLocal = new StorageLocal(getContext());
        Sprite doubleBounce = new CubeGrid();
        Sprite doubleBounce1 = new CubeGrid();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        PublicList = new ArrayList<>();
        nestedScrollView = view.findViewById(R.id.scroll_view);
        recyclerViewPublic = view.findViewById(R.id.recyclerView);
        search = view.findViewById(R.id.btnSearch);
        constraintLayout = view.findViewById(R.id.con);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.GONE);
        ProgressBar progressBar1 = (ProgressBar) view.findViewById(R.id.spin_kit1);
        progressBar1.setIndeterminateDrawable(doubleBounce1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        publicAdapter = new PublicAdapter(getContext(), PublicList);
        recyclerViewPublic.setLayoutManager(linearLayoutManager);
        recyclerViewPublic.setHasFixedSize(true);
        recyclerViewPublic.setAdapter(publicAdapter);
        page = 1;

        // Carga publicaciónes
        loadItems(page, limit);

        // Al no haber mas publicaciones pide 10 más
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

        // LLeva al activity de busqueda
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Solicita publicaciones, segun el numero de pagina y el limite
    public void loadItems(int page, int limit) {
        modelPublic = new PublicModel(getContext());
        modelPublic.findAllPublics(page, limit, new ServerHelper.ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.GONE);
                try {
                    total_page = Integer.parseInt(response.getString("totalPages"));
                    cargarLista(response.getJSONArray("docs"));// Carga la lista con las publicaciones
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(JSONObject error) {
                String err = "Algo salio mal. Comunicate con el desarrollador.";
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

    public void cargarLista(JSONArray object) {
        for (int i = 0; i < object.length(); i++) {
            try {
                final JSONObject jsonObject = object.getJSONObject(i);
                final JSONArray likes = jsonObject.getJSONArray("likes");
                final JSONArray comments = jsonObject.getJSONArray("comments");

                PublicList.add(new PublicModel(// Agrega las publicaciones una por una
                        jsonObject.getString("_id"),// _id publicación
                        jsonObject.getString("user"),// _id usuario
                        new DateConvert(jsonObject.getString("createdAt")).getAgo(),
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
        loadUsers();// Cambia en las lista el id del usuario por su información
    }

    public void loadUsers() {
        modelUser = new UserModel(getContext());
        try {

            JSONObject data = new JSONObject();
            data.put("users", filtroUsuarios());// solo usuarios de los cuales no se tienen información

            arrayTEM = new JSONArray();

            modelUser.findUsers(data, new ServerHelper.ServerCallback() {// Solicitud de info usuarios
                @Override
                public void onSuccess(JSONObject response) {
                    String rs = storageLocal.readLocalData("users");
                    // Almacena la información de los usuarios
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
                            storageLocal.writeLocalData("users", newUsers.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        storageLocal.writeLocalData("users", response.toString());
                    }
                    // Cambia el id por la información del usuario
                    try {
                        JSONObject dataUsers = new JSONObject(storageLocal.readLocalData("users"));
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
                        // Carga el adaptador con la lista
                        publicAdapter = new PublicAdapter(getContext(), PublicList);
                        // Carga el RecyclerView con el adaptador
                        recyclerViewPublic.setAdapter(publicAdapter);
                        st_scroll = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(JSONObject error) {
                    System.out.println(error.toString());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {// Reinicia lista de publicaciones al regresar, solo si hubo cambios
        super.onResume();
        String res = storageLocal.readLocalData("resume_home");
        if (res.equals("ok")) {
            storageLocal.writeLocalData("resume_home", "");
            arrayETP = new JSONArray();
            arrayTEM = new JSONArray();
            arrayTEMa = new JSONArray();
            arrayMR = new JSONArray();
            arrayMRa = new JSONArray();

            st_scroll = true;
            constraintLayout.setVisibility(View.VISIBLE);
            storageLocal.writeLocalData("users", "");
            System.out.println("RESUME HOME");

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
        }

    }

    private JSONArray filtroUsuarios() {
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

                if (rep) {

                    arrayTEM.put((arrayETP.get(i)));

                }

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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arrayTEM;
    }

    @Override
    public void onStop() {// Al detener el activity se borra la información de los usuarios en la SharedPreferences
        super.onStop();
        st_scroll = true;
        storageLocal.writeLocalData("users", "");
    }
}