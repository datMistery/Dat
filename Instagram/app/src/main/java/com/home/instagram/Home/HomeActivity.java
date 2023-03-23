package com.home.instagram.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.home.instagram.Likes.LikesActivity;
import com.home.instagram.LoginActivity;
import com.home.instagram.Model.Photo;
import com.home.instagram.Model.UserAccountSettings;
import com.home.instagram.Search.SearchActivity;
import com.home.instagram.Share.PostActivity;
import com.home.instagram.R;
import com.home.instagram.Utils.MainfeedListAdapter;
import com.home.instagram.Utils.SectionsPagerAdapter;
import com.home.instagram.Fragments.CameraFragment;
import com.home.instagram.Fragments.HomeFragment;
import com.home.instagram.Fragments.MessagesFragment;
import com.home.instagram.Fragments.LikesFragment;
import com.home.instagram.Fragments.SearchFragment;
import com.home.instagram.Profile.ProfileActivity;
import com.home.instagram.Utils.UniversalImageLoader;
import com.home.instagram.Utils.ViewCommentsFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements MainfeedListAdapter.OnLoadMoreItemsListener {
    @Override
    public void OnLoadMoreItems() {
        Log.d(TAG, "OnLoadMoreItems: display more photoes");
        HomeFragment fragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }
    private static final String TAG = "MainActivity";
    private static final int HOME_FRAGMENT = 1 ;
    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findId();
        setupFireBaseAuth();


            initImageLoader();

        getBottomNavigationView(bottomNavigationView, this);
        setupViewPager();
    }


    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));

        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }


    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }


    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(HomeActivity.this);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }


    private void getBottomNavigationView(BottomNavigationView bottomNavigationView, final Activity callingActivity) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_search:
                        selectorFragment = null;
                        startActivity(new Intent(HomeActivity.this, SearchActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(HomeActivity.this, PostActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_heart:
                        startActivity(new Intent(HomeActivity.this, LikesActivity.class));
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    case R.id.nav_profile:
                        selectorFragment = null;
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
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

    /*
    * Responsible for adding 3 tabs: Camera, Home and Messages
    * */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); //index 0
        adapter.addFragment(new HomeFragment());//index 1
        adapter.addFragment(new MessagesFragment());//index 2

        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_camera);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.instagram_icon);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_messenger);

    }

    private void findId() {
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);
    }

    /*-------------------------Firebase------------------------------------------------------*/
    /*
    * checks to see if the @param 'user' is logged in
    * @param user
    * */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user  == null){
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /*
     * Setup the firebase auth object
     * */
    private void setupFireBaseAuth(){
        Log.d(TAG, "setupFireBaseAuth:setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if user is logged in
                checkCurrentUser(user);
                if(user != null){
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());

                }else{
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    @Override
    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
        mViewPager.setCurrentItem(HOME_FRAGMENT);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


}