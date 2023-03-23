package com.home.instagram.Share;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.home.instagram.Fragments.HomeFragment;
import com.home.instagram.Fragments.LikesFragment;
import com.home.instagram.Fragments.SearchFragment;
import com.home.instagram.Home.HomeActivity;
import com.home.instagram.Profile.ProfileActivity;
import com.home.instagram.R;
import com.home.instagram.Utils.Permissions;
import com.home.instagram.Utils.SectionsPagerAdapter;


public class PostActivity extends AppCompatActivity {
    //constants
    private static final String TAG = "PostActivity";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ViewPager mViewPager;
    private Fragment selectorFragment;
    private BottomNavigationView bottomNavigationView;
    private Context mContext = PostActivity.this;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started.");
        setContentView(R.layout.activity_post);
        findId();
        if(checkPermissionsArray(Permissions.PERMISSIONS)){
            setupViewPager();
        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }
        super.onCreate(savedInstanceState);
        // getBottomNavigationView(bottomNavigationView);


    }

    /**
     *  return the current tab number
     *  0 = GalleryFragment
     *  1 = PhotoFragment
     * @return
     */
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }

    /**
     * setup viewpager for manager the tabs
     */
    private void setupViewPager(){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment());
       adapter.addFragment(new PhotoFragment());
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
    }
    public int getTask(){
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();
    }

    /**
     * Verified all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");
        ActivityCompat.requestPermissions(
                PostActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");
        for(int i = 0; i < permissions.length; i++){
            String  check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }
    // Để ghi nhanh được comment kèm với @param thi ghi như sau: /** + Enter
    /**
     * Check a single permission  is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission." + permission);
        int permissionRequest = ActivityCompat.checkSelfPermission(PostActivity.this, permission);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    private void findId() {
        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
    }
    private void getBottomNavigationView(BottomNavigationView bottomNavigationView) {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, HomeActivity.class));
                        break;
                    case R.id.nav_search:
                        selectorFragment = new SearchFragment(); // Phải chuyển sang Activity
                        break;
                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, PostActivity.class));
                        break;
                    case R.id.nav_heart:
                        selectorFragment = new LikesFragment();
                        break;
                    case R.id.nav_profile:
                        selectorFragment = null;
                        startActivity(new Intent(mContext, ProfileActivity.class));
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
/*   private ImageView close;
    private ImageView imageAdded;
    private TextView post;


    private Uri imageUri;
    private String imageUrl;

    final int PIC_CROP = 1;
    EditText desciption;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        findID();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                finish();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

//        CropImage.activity().start(PostActivity.this);
    }

    private void upload() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadtask = filePath.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();

                    HashMap<String , Object> map = new HashMap<>();
                    map.put("postid" , postId);
                    map.put("imageurl" , imageUrl);
                    map.put("description" , desciption.getText().toString());
                    map.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

//                    DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
//                    List<String> hashTags = desciption.getHashtags();
//                    if (!hashTags.isEmpty()){
//                        for (String tag : hashTags){
//                            map.clear();
//
//                            map.put("tag" , tag.toLowerCase());
//                            map.put("postid" , postId);
//
//                            mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
//                        }
//                    }

                    pd.dismiss();
                    startActivity(new Intent(PostActivity.this , MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    private void findID() {
        close = findViewById(R.id.close);
        imageAdded = findViewById(R.id.image_add);
        post = findViewById(R.id.post);
        desciption = findViewById(R.id.description);
    }


 *//*   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            imageAdded.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Try again!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this , MainActivity.class));
            finish();
        }
    }*//*
 @Override
 protected void onStart() {
     super.onStart();

//     final ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());
//
//     FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
//         @Override
//         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//             for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                 hashtagAdapter.add(new Hashtag(snapshot.getKey() , (int) snapshot.getChildrenCount()));
//             }
//         }

//         @Override
//         public void onCancelled(@NonNull DatabaseError databaseError) {
//
//         }
//     });
//
//     description.setHashtagAdapter(hashtagAdapter);
 }




*/
}