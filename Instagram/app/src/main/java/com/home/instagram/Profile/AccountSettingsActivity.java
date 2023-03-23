package com.home.instagram.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.home.instagram.Fragments.HomeFragment;
import com.home.instagram.Fragments.LikesFragment;
import com.home.instagram.Fragments.SearchFragment;
import com.home.instagram.Share.PostActivity;
import com.home.instagram.R;
import com.home.instagram.Utils.FirebaseMethods;
import com.home.instagram.Utils.SectionsStatePagerAdapter;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {
    private static final String TAG = "AccountSettingsActivity";

    private Context mContext;

    public SectionsStatePagerAdapter pagerAdapter;

    private ViewPager mViewPager;
    private RelativeLayout mRelativeLayout;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);
        mContext = AccountSettingsActivity.this;
        Log.d(TAG, "onCreate: started.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayout1);

        findId();
        setupSettingsList();
//        getBottomNavigationView(bottomNavigationView);
        setupFragments();
        getIncomingIntent();
        //setup the backarrow  for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to 'ProfileActivity'");
                finish();
            }
        });
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();

        if(intent.hasExtra(getString(R.string.selected_image))
                || intent.hasExtra(getString(R.string.selected_bitmap))){

            //if there is an imageUrl attached as an extra, then it was chosen from the gallery/photo fragment
            Log.d(TAG, "getIncomingIntent: New incoming imgUrl");
            if(intent.getStringExtra(getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment))){

                if(intent.hasExtra(getString(R.string.selected_image))){
                //set the new profile picture
                FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                        intent.getStringExtra(getString(R.string.selected_image)), null);
            }  else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    //set the new profile picture
                    FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                    firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null, 0,
                            null,(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap)));
                }

            }
        }
        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: received incoming intent from " + getString(R.string.profile_activity));
            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    private void findId() {
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
    }

    private void setupFragments(){
        pagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); //fragment 0
        pagerAdapter.addFragment(new SignOutFragment(), getString(R.string.sign_out_fragment)); //fragment 1


    }

    public void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigating to fragment #: " + fragmentNumber);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(fragmentNumber);

    }

    private void setupSettingsList(){
        Log.d(TAG, "setupSettingsList: initializing 'Account Settings' list.");
        ListView listView = (ListView) findViewById(R.id.lvAccountSettings);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment)); //fragment 0
        options.add(getString(R.string.sign_out_fragment)) ; //fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, options);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment #: " + position);
                setViewPager(position);
            }
        });
    }

    private void getBottomNavigationView(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;
                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;
                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(AccountSettingsActivity.this, PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        selectorFragment = new LikesFragment();
                        break;
                    case R.id.nav_profile:
                        selectorFragment = null;
                        startActivity(new Intent(AccountSettingsActivity.this, ProfileActivity.class));
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
