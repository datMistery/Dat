package com.home.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.home.instagram.Home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText email;
    private EditText password;
    private Button login;
    private TextView registerUser;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findId();
        mAuth = FirebaseAuth.getInstance();
        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);


        registerUser.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dimissDialog();
                }
            }, 5000);
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        login.setOnClickListener(v -> {
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();

            if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(LoginActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
            }else{
                loginUser(txt_email, txt_password);
            }
        });
    }

    private void loginUser(String email, String password) {
        LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        loadingDialog.startLoadingDialog();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if(task.isSuccessful()){
                loadingDialog.dimissDialog();
                try{
                    assert user != null;
                    if(user.isEmailVerified()){
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }catch (NullPointerException e){
                    Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                }
//                Toast.makeText(LoginActivity.this, "Update the profile for better experience", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(e -> {
            loadingDialog.dimissDialog();
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }


    private void findId() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.btn_login);
        registerUser = findViewById(R.id.register_user);


    }
}