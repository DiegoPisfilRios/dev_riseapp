package com.example.riseapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import API.ServerHelper;
import models.UserModel;
import utilities.StorageLocal;

public class RootActivity extends AppCompatActivity {

    private BottomAppBar bottomAppBar;
    private FloatingActionButton floatingActionButton;
    private boolean isFabTapped = false;
    private BottomSheetDialog bottomSheetDialog;
    FrameLayout frameLayout;
    private UserModel model;
    private StorageLocal storageLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        isFabTapped = false;
        model = new UserModel(this);
        storageLocal = new StorageLocal(this);

        /** DATA USER ----------------------------------------------------------------*/
        model.MyData(new ServerHelper.ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                System.out.println(response);
                storageLocal.writeLocalData("my_data", response.toString());
            }

            @Override
            public void onError(JSONObject error) {
                String err = "error";
                try {
                    err = error.getString("msg");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Snackbar.make(findViewById(android.R.id.content), err, Snackbar.LENGTH_LONG).show();
            }
        });
        /**-------------------------------------------------------------------------*/

        bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);

        floatingActionButton = findViewById(R.id.fab);
        if (savedInstanceState == null) {
            handleFrame(new HomeFragment());
        }

        handleFab();
        handleOnClickListeners();

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog = new BottomSheetDialog(RootActivity.this, R.style.BottomSheetTheme);

                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.buttom_sheet,
                        (ViewGroup) findViewById(R.id.bottom_sheet));
                try {
                    JSONObject object = new JSONObject(storageLocal.readLocalData("my_data"));

                    sheetView.findViewById(R.id.btnOut).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            model.LogOutUser();
                            bottomSheetDialog.dismiss();
                            Intent intent = new Intent(RootActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });

                    sheetView.findViewById(R.id.btnProfile).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                            Intent intent = new Intent( v.getContext(), ProfileActivity.class);
                            startActivity(intent);
                        }
                    });

                    sheetView.findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bottomSheetDialog.dismiss();
                            Intent intent = new Intent( v.getContext(), AboutActivity.class);
                            startActivity(intent);
                        }
                    });

                    Picasso.get().load(object.getString("avatar")).resize(40, 40)
                            .into((ImageView) sheetView.findViewById(R.id.bs_avatar));

                    TextView name =(TextView) sheetView.findViewById(R.id.bs_name);
                    name.setText(object.getString("name")+" "+object.getString("surname"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
            }
        });
    }

    private void handleOnClickListeners() {
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuOne:
                        handleFrame(new HomeFragment());
                        break;

                    case R.id.menuTwo:
                        handleFrame(new FavoriteFragment());
                        break;

                    default:
                        return false;
                }

                return false;
            }
        });
    }

    private void handleFab() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFabTapped = !isFabTapped;
                if (isFabTapped) {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_x));
                    bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                    handleFrame(new AddFragment());
                } else {
                    floatingActionButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_plus));
                    bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                    handleFrame(new HomeFragment());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //handleFab();
    }

    private void handleFrame(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frame1, fragment, null);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
