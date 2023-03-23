package com.home.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.home.instagram.Home.HomeActivity;

public class StartActivity extends AppCompatActivity {

    private ImageView iconImage;
    private LinearLayout linearLayout;
    private Button register;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findId();
        translateAnimation();
        LoadingDialog loadingDialog = new LoadingDialog(StartActivity.this);

        register.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dimissDialog();
                }
            }, 5000);
            startActivity(new Intent(StartActivity.this, RegisterActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                });
            // Dấu | là toán tử bitwise -> dùng để kết hợp hai options lại thành một giá trị
            // v -> là lamda -> dùng để viết trực tiếp và thay thế cho một hàm(để nhanh)

        login.setOnClickListener(v -> {
            loadingDialog.startLoadingDialog();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dimissDialog();
                }
            }, 5000);
                startActivity(new Intent(StartActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }

    private void findId(){
        iconImage = findViewById(R.id.icon_image);
        linearLayout = findViewById(R.id.linear_layout);
        register = findViewById(R.id.btn_register);
        login = findViewById(R.id.btn_login);
    }

    private void translateAnimation(){
        linearLayout.animate().alpha(0f).setDuration(0); // 0f là mất
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());
        iconImage.setAnimation(animation);
    }

    private class MyAnimationListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            iconImage.clearAnimation();
            iconImage.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000); // 1f là hiện
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Khi chạy nó sẽ dừng ở user đăng nhập lần gần nhất và tại trang Main.
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(StartActivity.this, HomeActivity.class));
            finish();
        }
    }
}