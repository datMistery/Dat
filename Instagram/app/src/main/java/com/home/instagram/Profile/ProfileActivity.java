package com.home.instagram.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.home.instagram.Model.Photo;
import com.home.instagram.Model.User;
import com.home.instagram.R;
import com.home.instagram.Utils.ViewCommentsFragment;
import com.home.instagram.Utils.ViewPostFragment;
import com.home.instagram.Utils.ViewProfileFragment;

public class ProfileActivity extends AppCompatActivity implements
                ProfileFragment.OnGridImageSelectedListener,
                ViewPostFragment.OnCommentThreadSelectedListener
                , ViewProfileFragment.OnGridImageSelectedListener {
    private static final String TAG = "ProfileActivity";

    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        Log.d(TAG, "onCommentThreadSelectedListener:  selected a comment thread");

        ViewCommentsFragment fragment = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();
    }

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image gridview: " + photo.toString());

        ViewPostFragment fragment = new ViewPostFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putInt(getString(R.string.activity_number), activityNumber);
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }

    private static final int NUM_GRID_COLUMNS = 3;
    private Context mContext = ProfileActivity.this;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private ProgressBar mProgressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        findId();
//        getBottomNavigationView(bottomNavigationView);
//        setupToolbar();
//        setupActivityWidgets();
//        setProfileImage();
//
//        tempGridSetup();
        init();
    }

    private void init() {
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment2));
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            Log.d(TAG, "init: searching for user object attached as intent extra");
            if (intent.hasExtra(getString(R.string.intent_user))) {
                User user = intent.getParcelableExtra(getString(R.string.intent_user));
                if(!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Log.d(TAG, "init: inflating view profile");
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user),
                            intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.addToBackStack(getString(R.string.view_profile_fragment));
                    transaction.commit();
                }

            }else{
                Log.d(TAG, "init: inflating Profile");
                ProfileFragment fragment = new ProfileFragment();
                FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(getString(R.string.profile_fragment));
                transaction.commit();
            }
        }else{
            Log.d(TAG, "init: inflating Profile");
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(getString(R.string.profile_fragment));
            transaction.commit();
        }
    }
}




//    private void tempGridSetup(){
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://pbs.twimg.com/profile_images/616076655547682816/6gMRtQyY.jpg");
//        imgURLs.add("https://i.redd.it/9bf67ygj710z.jpg");
//        imgURLs.add("https://c1.staticflickr.com/5/4276/34102458063_7be616b993_o.jpg");
//        imgURLs.add("http://i.imgur.com/EwZRpvQ.jpg");
//        imgURLs.add("http://i.imgur.com/JTb2pXP.jpg");
//        imgURLs.add("https://i.redd.it/59kjlxxf720z.jpg");
//        imgURLs.add("https://i.redd.it/pwduhknig00z.jpg");
//        imgURLs.add("https://i.redd.it/clusqsm4oxzy.jpg");
//        imgURLs.add("https://i.redd.it/svqvn7xs420z.jpg");
//        imgURLs.add("http://i.imgur.com/j4AfH6P.jpg");
//        imgURLs.add("https://i.redd.it/89cjkojkl10z.jpg");
//        imgURLs.add("https://i.redd.it/aw7pv8jq4zzy.jpg");
//
//        setupImageGrid(imgURLs);
//    }
//    private void setupImageGrid(ArrayList<String> imgURLs){
//        GridView gridView = findViewById(R.id.gridView);
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//        gridView.setColumnWidth(imageWidth);
//
//        GridImageAdapter adapter = new GridImageAdapter(ProfileActivity.this, R.layout.layout_grid_imageview, "", imgURLs);
//        gridView.setAdapter(adapter);
//    }
//
//
//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: setting profile photo.");
//        String imgURL = "imgs.search.brave.com/Q6VdQK8upd1nxC6fgj62eRHGbDizKfv3CUAUzqnu4Fo/rs:fit:934:933:1/g:ce/aHR0cHM6Ly9jcmFj/a2JlcnJ5LmNvbS9z/aXRlcy9jcmFja2Jl/cnJ5LmNvbS9maWxl/cy90b3BpY19pbWFn/ZXMvMjAxMy9BTkRS/T0lELnBuZw";
//        UniversalImageLoader.setImage(imgURL, profilePhoto, mProgressBar, "https://");
//    }
//
//    private void setupActivityWidgets(){
//        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
//    }
//
//
//    private void setupToolbar(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolbar);
//        setSupportActionBar(toolbar);
//
//        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
//        profileMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to account settings.");
//                Intent intent  = new Intent(ProfileActivity.this, AccountSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void getBottomNavigationView(BottomNavigationView bottomNavigationView) {
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.nav_home:
//                        selectorFragment = new HomeFragment();
//                        break;
//                    case R.id.nav_search:
//                        selectorFragment = new SearchFragment();
//                        break;
//                    case R.id.nav_add:
//                        selectorFragment = null;
//                        startActivity(new Intent(ProfileActivity.this, PostActivity.class));
//                        break;
//                    case R.id.nav_heart:
//                        selectorFragment = new NotificationFragment();
//                        break;
//                    case R.id.nav_profile:
//                        selectorFragment = null;
//                        break;
//                }
//                if(selectorFragment != null){
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
//                }
//                return true;
//            }
//        });
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//
//    }
//
//    private void findId() {
//        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
//    }


//    private void getBottomNavigationView(BottomNavigationView bottomNavigationView) {
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.nav_home:
//                        selectorFragment = new HomeFragment();
//                        break;
//                    case R.id.nav_search:
//                        selectorFragment = new SearchFragment();
//                        break;
//                    case R.id.nav_add:
//                        selectorFragment = null;
//                        startActivity(new Intent(ProfileActivity.this, PostActivity.class));
//                        break;
//                    case R.id.nav_heart:
//                        selectorFragment = new NotificationFragment();
//                        break;
//                    case R.id.nav_profile:
//                        selectorFragment = null;
//                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
//                        break;
//                }
//                if(selectorFragment != null){
//                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
//                }
//                return true;
//            }
//        });
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.profile_menu, menu);
//        return true;
//    }
