package com.home.instagram;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.home.instagram.Model.User;
import com.home.instagram.Model.UserAccountSettings;
import com.home.instagram.Utils.FirebaseMethods;
import com.home.instagram.Utils.StringManipulation;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText mUsername;
    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private String email, username, password, name;
    private Button register;
    private TextView loginUser;
    private final int LENGTHOFPASSWORD = 6;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseMethods firebaseMethods;
    private String append = "";
    private String userID;
    private Context mContext;
        LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseMethods = new FirebaseMethods(mContext);
        findID();
        setupFireBaseAuth();
        init();
        loginUser.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();
            Handler handler = new Handler();
            handler.postDelayed(() -> loadingDialog.dimissDialog(), 5000);
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }
//        register.setOnClickListener(v -> {
//            String txtUserName = mUsername.getText().toString();
//            String txtName = mName.getText().toString();
//            String txtEmail = mEmail.getText().toString();
//            String txtPassword = mPassword.getText().toString();
//
//            if(TextUtils.isEmpty(txtUserName) || TextUtils.isEmpty(txtName)
//            || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
//                Toast.makeText(RegisterActivity.this, "Empty Credential!", Toast.LENGTH_SHORT).show();
//            }else if(txtPassword.length() < LENGTHOFPASSWORD){
//                Toast.makeText(RegisterActivity.this, "Password is too short!", Toast.LENGTH_SHORT).show();
//            }else{
////                registerUser(txtUserName, txtName, txtEmail, txtPassword);
//                firebaseMethods.registerNewEmail(email, password, username);
//            }
//        });
    private void init(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
                name = mName.getText().toString();
                if(checkInputs(email, username, password, name)){
                    firebaseMethods.registerNewEmail(email, password, username, name);
                }
            }
        });
    }
    private boolean checkInputs(String email, String username, String password, String name){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(name)
          || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.length() < LENGTHOFPASSWORD) {
            Toast.makeText(RegisterActivity.this, "Password is too short!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if(user  == null){
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }
//    private void registerUser(String username, String name, String email, String password) {
//
//        loadingDialog.startLoadingDialog();
//
//        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                loadingDialog.dimissDialog();
//                Toast.makeText(RegisterActivity.this, "Update the profile for better experience", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            } else if (!task.isSuccessful()) {
//                loadingDialog.dimissDialog();
//                Toast.makeText(RegisterActivity.this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

//        addOnSuccessListener(authResult -> {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("name", name);
//            map.put("email", email);
//            map.put("username", username);
//            map.put("id", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
//            map.put("bio", "");
//            map.put("imageurl", "default");
//            map.put("phone_number", "");
//
//            mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map)
//        }).addOnFailureListener(e -> {
//            loadingDialog.dimissDialog();
//            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        });


    private void findID(){
        mUsername = findViewById(R.id.username);
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        register = findViewById(R.id.btn_register);
        loginUser = findViewById(R.id.login_user);
    }
    /*-------------------------Firebase------------------------------------------------------*/
    /*
     * Setup the firebase auth object
     * */

    /*
     * Check is @param username already exists in the database
     * @param username
     *
     * */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernamExists: Checking if" + username + "already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot singleSnapshot: snapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        append = mRootRef.push().getKey().substring(3, 10);
                        Log.d(TAG, "onDataChange: username already exists. Appending random string to name:" + append);
                    }
                }
                String mUsername = "";
                mUsername += append;
                // add new user to the database
                firebaseMethods.addNewUser(email, mUsername, "", "", "");
                Toast.makeText(mContext, "Signup successful. Sending verification email.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setupFireBaseAuth(){
        Log.d(TAG, "setupFireBaseAuth:setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = firebaseDatabase.getReference();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if user is logged in

                if(user != null){
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());

                    mRootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            checkIfUsernameExists(username);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });
                    finish();
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

    }

    @Override
    public void onStop(){
        super.onStop();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}