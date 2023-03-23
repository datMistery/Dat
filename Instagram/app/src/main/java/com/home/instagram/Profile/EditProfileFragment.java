package com.home.instagram.Profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.home.instagram.Model.User;
import com.home.instagram.Model.UserAccountSettings;
import com.home.instagram.Model.UserSettings;
import com.home.instagram.R;
import com.home.instagram.Share.PostActivity;
import com.home.instagram.Utils.FirebaseMethods;
import com.home.instagram.Utils.UniversalImageLoader;
import com.home.instagram.dialogs.ConfirmPasswordDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements
        ConfirmPasswordDialog.OnConfirmPasswordListener{
    @Override
    public void onConfirmPassword(String password) {

        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.


        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        ///////////////////// Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");

                            ///////////////////////check to see if the email is not already present in the database
                            mAuth.fetchSignInMethodsForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try{
                                            if(task.getResult().getSignInMethods().size() == 1){
                                                Log.d(TAG, "onComplete: that email is already in use.");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Log.d(TAG, "onComplete: That email is available.");

                                                //////////////////////the email is available so update it
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: "  +e.getMessage() );
                                        }
                                    }
                                }
                            });
                        }else{
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }

                    }
                });
    }

    private static final String TAG = "EditProfileFragment";
    Uri imageUri;
    StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    private String userID;
    private Context mContext;
    public ImageLoader imageLoader;
    //vars
    private UserSettings mUserSettings;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mContext = getActivity();
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mDisplayName = (EditText) view.findViewById(R.id.display_name);
        mUsername = (EditText) view.findViewById(R.id.username);
        mWebsite = (EditText) view.findViewById(R.id.website);
        mDescription = (EditText) view.findViewById(R.id.description);
        mEmail = (EditText) view.findViewById(R.id.email);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.change_profile_photo);
        mFirebaseMethods = new FirebaseMethods(getActivity());
//        setProfileImage();
//        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
//        imageLoader.init(ImageLoaderConfiguration.createDefault(universalImageLoader.getConfig()));
        setupFireBaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes. ");
                saveProfileSettings();
            }
        });

        return view;
    }
//    private void initImageLoader(){
//        UniversalImageLoader universalImageLoader = new UniversalImageLoader(getActivity());
//        ImageLoader.getInstance().init(universalImageLoader.getConfig());
//    }
    /*
    * Retrieves the data contained in the widgets and submits it to the database
    * Before doing so it checks to make sure the username chosen is unique
    * */

    private void saveProfileSettings(){
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());

/*                User user = new User();
                for(DataSnapshot ds: snapshot.child(getString(R.string.dbname_users)).getChildren()){
                    if(ds.getKey().equals(userID)){
                        user.setUsername(ds.getValue(User.class).getUsername());
                    }
                }
                Log.d(TAG,"onDatachange: Current Username" + user.getUsername());*/
                //case 1: if the user made a change to their username
                if(!mUserSettings.getUser().getUsername().equals(username)){
                    checkIfUsernameExists(username);
                }
                //case 2: if the user made a change to their email
                if(!mUserSettings.getUser().getEmail().equals(email)){
                    //step 1: Re-authenticate
                    //            -Confirm the password and email
//                    initImageLoader();
                    ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
                    dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
                    dialog.setTargetFragment(EditProfileFragment.this, 1);
                    //step 2: check if the email already is registered
                    //            -'fetchProvidersForEmail(String email)'
                    //step 3: change the email
                    //            -submit the new email to the database and authentication
                }
                /**
                 * change the rest of the settings that do not require uniqueness
                 */
        if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)){
            //update displayname
            mFirebaseMethods.updateUserAccountSettings(displayName, null, null, 0);
        }
        if(!mUserSettings.getSettings().getWebsite().equals(website)){
            //update website
            mFirebaseMethods.updateUserAccountSettings(null, website, null, 0);
        }
        if(!mUserSettings.getSettings().getDescription().equals(description)){
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, description, 0);
        }
        if(!mUserSettings.getSettings().getProfile_photo().equals(phoneNumber)){
            //update phoneNumber
            mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);
        }


    }
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
                if(!snapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username.", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: snapshot.getChildren()){
                    if(singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getSettings().getUsername());

        mUserSettings = userSettings;
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        try{
            UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        }catch(Exception e){
            Toast.makeText(mContext, "Save failed", Toast.LENGTH_SHORT).show();
        }
//        Glide.with(mContext)
//                .load(settings.getProfile_photo())
//                .into(mProfilePhoto);
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

//    private void setProfileImage(){
//        Log.d(TAG, "set ProfileImage: setting profile image");
//        String imgURL = "imgs.search.brave.com/Q6VdQK8upd1nxC6fgj62eRHGbDizKfv3CUAUzqnu4Fo/rs:fit:934:933:1/g:ce/aHR0cHM6Ly9jcmFj/a2JlcnJ5LmNvbS9z/aXRlcy9jcmFja2Jl/cnJ5LmNvbS9maWxl/cy90b3BpY19pbWFn/ZXMvMjAxMy9BTkRS/T0lELnBuZw";
//        UniversalImageLoader.setImage(imgURL, mProfilePhoto, null, "https://");
//
//    }
    /*-------------------------Firebase------------------------------------------------------*/

    /*
     * Setup the firebase auth object
     * */
    private void setupFireBaseAuth(){
        Log.d(TAG, "setupFireBaseAuth:setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
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
                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(snapshot));
                //retrieve images for the user in question
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
