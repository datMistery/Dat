package com.home.instagram.Likes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.home.instagram.Fragments.HomeFragment;
import com.home.instagram.Home.HomeActivity;
import com.home.instagram.Profile.ProfileActivity;
import com.home.instagram.R;
import com.home.instagram.Search.SearchActivity;
import com.home.instagram.Share.PostActivity;

public class LikesActivity extends AppCompatActivity {
    private static final String TAG = "LikesActivity";
    private Context mContext = LikesActivity.this;
    private Fragment selectorFragment;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findId();
        getBottomNavigationView(bottomNavigationView, this);
    }

    private void findId() {
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
    }

    private void getBottomNavigationView(BottomNavigationView bottomNavigationView, final Activity callingActivity) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, HomeActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_search:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, SearchActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, PostActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_heart:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, LikesActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_profile:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, ProfileActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }
                if(selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

    }
}