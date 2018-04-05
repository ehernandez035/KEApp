package es.ehu.ehernandez035.kea.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import es.ehu.ehernandez035.kea.R;

public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        LinearLayout rankingLayout = findViewById(R.id.home_rankingLayout);
        rankingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, RankingActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout konbertsoreaLayout = findViewById(R.id.home_konbertsoreaLayout);
        konbertsoreaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CalculatorActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout galdetegiakLayout = findViewById(R.id.home_galdetegiakLayout);
        galdetegiakLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, GaldetegiZerrendaActivity.class);
                startActivity(intent);
            }
        });

        final LinearLayout progLayout = findViewById(R.id.home_programLayout);

        progLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Popup
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.program_type_popup, null);
                mBuilder.setView(dialogView);
                final AlertDialog alertDialog = mBuilder.create();
                final RadioGroup radioGroup = dialogView.findViewById(R.id.popup_radioGroup);
                Button hasButton = dialogView.findViewById(R.id.popup_hasiButton);

                hasButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HomeActivity.this, ProgActivity.class);
                        switch (radioGroup.getCheckedRadioButtonId()) {
                            case R.id.WPradioButton:
                                // TODO Send parameters in the intent
                                break;
                            case R.id.MPradioButton:
                                // TODO Send parameters in the intent
                                break;
                        }
                        startActivity(intent);
                    }
                });

                ImageView closeButton = dialogView.findViewById(R.id.close_popup);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
