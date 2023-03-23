package com.home.instagram.Search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.home.instagram.Fragments.HomeFragment;
import com.home.instagram.Home.HomeActivity;
import com.home.instagram.Likes.LikesActivity;
import com.home.instagram.Model.User;
import com.home.instagram.Profile.ProfileActivity;
import com.home.instagram.R;
import com.home.instagram.Share.PostActivity;
import com.home.instagram.Utils.UserListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private Context mContext = SearchActivity.this;
    private Fragment selectorFragment;
    private BottomNavigationView bottomNavigationView;

    //widgets
    private EditText mSearchParam;
    private ListView mListView;

    //vars
    private List<User> mUserList;
    private UserListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search); // tam tho su dung thang activity_home
        findId();
        hideSoftKeyboard();
        getBottomNavigationView(bottomNavigationView,this);
        initTextListener();

    }

    private void initTextListener(){
        Log.d(TAG, "initTextListener: initializing");

        mUserList = new ArrayList<>();

        mSearchParam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String text = mSearchParam.getText().toString().toLowerCase(Locale.getDefault());
                searchForMatch(text);
            }
        });
    }

    private void searchForMatch(String keyword){
        Log.d(TAG, "searchForMatch: searching for a match: " + keyword);
        mUserList.clear();
        //update the users list view
        if(keyword.length() ==0){

        }else{
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference.child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username)).equalTo(keyword);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: found user:" + singleSnapshot.getValue(User.class).toString());

                        mUserList.add(singleSnapshot.getValue(User.class));
                        //update the users list view
                        updateUsersList();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void updateUsersList(){
        Log.d(TAG, "updateUsersList: updating users list");

        mAdapter = new UserListAdapter(SearchActivity.this, R.layout.layout_user_listitem, mUserList);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected user: " + mUserList.get(position).toString());

                //navigate to profile activity
                Intent intent =  new Intent(SearchActivity.this, ProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.search_activity));
                intent.putExtra(getString(R.string.intent_user), mUserList.get(position));
                startActivity(intent);
            }
        });
    }

    private void hideSoftKeyboard(){
        if(getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private void findId() {
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        mSearchParam = (EditText) findViewById(R.id.search);
        mListView = (ListView)  findViewById(R.id.listView);
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