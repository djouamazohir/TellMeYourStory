package com.zohirdev.mystory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.zohirdev.mystory.R;

import java.util.Objects;

public class BottomNavActivity extends AppCompatActivity {

    private final int ID_HOME = 1;
    private final int ID_SEARCH = 2;
    private final int ID_ADD = 3;
    private final int ID_NOTIFICATION = 4;
    private final int ID_ACCOUNT = 5;

    MeowBottomNavigation meowBottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        Objects.requireNonNull(getSupportActionBar()).hide();

        meowBottomNavigation = findViewById(R.id.bottomNavigation);

        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_baseline_home_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_SEARCH, R.drawable.ic_baseline_search_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_ADD, R.drawable.ic_baseline_add_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_NOTIFICATION, R.drawable.ic_baseline_notifications_24));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_ACCOUNT, R.drawable.ic_baseline_account_circle_24));

        meowBottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
            }
        });

        meowBottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                String name;
                switch (item.getId()) {
                    case ID_HOME:
                        name = "Home";
                        break;

                    case ID_SEARCH:
                        name = "Search";
                        break;

                    case ID_ADD:
                        name = "Add";
                        break;

                    case ID_NOTIFICATION:
                        name = "Notification";
                        break;

                    case ID_ACCOUNT:
                        name = "Account";
                        break;
                    default:
                        name = "";
                }

            }
        });

        meowBottomNavigation.show(ID_HOME, true);
    }
}