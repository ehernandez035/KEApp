package es.ehu.ehernandez035.kea.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.RequestCallback;
import es.ehu.ehernandez035.kea.ServerRequest;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        // TODO Get user info


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        final TextView username = header.findViewById(R.id.username_nav);
        final TextView points = header.findViewById(R.id.totalPoints_nav);
        final TextView level = header.findViewById(R.id.levelTV_nav);
        LinearLayout rankingLayout = findViewById(R.id.home_rankingLayout);
        LinearLayout konbertsoreaLayout = findViewById(R.id.home_konbertsoreaLayout);
        LinearLayout galdetegiakLayout = findViewById(R.id.home_galdetegiakLayout);
        final LinearLayout progLayout = findViewById(R.id.home_programLayout);


        new ServerRequest(this, "http://elenah.duckdns.org/userInfo.php", new RequestCallback() {
            @Override
            public void onSuccess(Response response) throws IOException {
                String result = response.body().string();
                if (result != null) {
                    try {
                        JSONObject jObject = new JSONObject(result);
                        int status = jObject.getInt("status");
                        if (status == 0) {
                            Snackbar.make(username, R.string.connection_error, Snackbar.LENGTH_SHORT).show();
                        } else {
                            level.setText(Integer.toString(jObject.getInt("level")));
                            points.setText(Integer.toString(jObject.getInt("points")));
                            username.setText(jObject.getString("username"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).execute();


        navigationView.setNavigationItemSelectedListener(this);


        rankingLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        konbertsoreaLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, KodDekodActivity.class);
                startActivity(intent);
            }
        });

        galdetegiakLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GaldetegiZerrendaActivity.class);
                startActivity(intent);
            }
        });


        progLayout.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                // TODO Popup
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle(R.string.programaMota);
                mBuilder.setPositiveButton(R.string.while_radio_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, ProgActivity.class);
                        // TODO Send parameters in the intent
                        startActivity(intent);
                    }
                });
                mBuilder.setNeutralButton(R.string.makro_radio_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MainActivity.this, ProgActivity.class);
                        // TODO Send parameters in the intent
                        startActivity(intent);
                    }
                });
                final AlertDialog alertDialog = mBuilder.create();
                alertDialog.show();

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            startActivity(myIntent);
        } else if (id == R.id.logout) {
            Intent myIntent = new Intent(this, RegisterActivity.class);
            startActivity(myIntent);
        }

        this.drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
