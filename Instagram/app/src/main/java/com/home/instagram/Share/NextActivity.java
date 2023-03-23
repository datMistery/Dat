package com.home.instagram.Share;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home.instagram.R;
import com.home.instagram.Utils.FirebaseMethods;
import com.home.instagram.Utils.UniversalImageLoader;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String mAppend = "file:///";
    private int imageCount = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;
    //widgets
    private EditText mCaption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mCaption = (EditText) findViewById(R.id.caption) ;
        setupFireBaseAuth();

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity.");
                finish();
            }
        });


        TextView post = (TextView) findViewById(R.id.tvPost);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();
                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgUrl, null);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount,null, bitmap);
                }

            }
        });
        setImage();
    }

    private void someMethod(){
        /*
        *  step 1: Create a data model for Photos
        *  step 2: Add properties to the Photo Objects (caption, data, imageUrl, photo_id, tags, user_id)
        *  step 3: Count the number of photos that the user already has.
        *  step 4:
        *  a. Upload the photo to Firebase Storage and insert tow new nodes in the Firebase Database
        *  b. insert into 'photos' node
        *  c. insert into 'user_photos' node
        * */

    }

    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
         intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);
        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new bitmap");
            image.setImageBitmap(bitmap);
        }
    }
    /*-------------------------Firebase------------------------------------------------------*/

    /*
     * Setup the firebase auth object
     * */
    private void setupFireBaseAuth(){
        Log.d(TAG, "setupFireBaseAuth:setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: imageCount: " + imageCount);
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in" + user.getUid());

                }else{
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageCount = mFirebaseMethods.getImageCount(snapshot);
                Log.d(TAG, "onDataChange: imageCount: " + imageCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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