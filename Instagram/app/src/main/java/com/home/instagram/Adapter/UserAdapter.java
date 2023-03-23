//package com.home.instagram.Adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import androidx.fragment.app.FragmentActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.home.instagram.Fragments.ProfileFragment;
//import com.home.instagram.MainActivity;
//import com.home.instagram.Model.User;
//import com.home.instagram.R;
//import com.squareup.picasso.Picasso;
//
//import java.util.HashMap;
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
//    private Context mContext;
//    private List<User> mUsers;
//    private boolean isFargment;
//    private FirebaseUser firebaseUser;
//
//    public UserAdapter(Context mContext, List<User> mUsers) {
//        this.mContext = mContext;
//        this.mUsers = mUsers;
//
//    }
//
//
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
//        return new UserAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        final User user = mUsers.get(position);
//        holder.btn_follow.setVisibility(View.VISIBLE);
//
//        holder.username.setText(user.getUsername());
//        holder.name.setText(user.getName());
//        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
//
//        isFollowed(user.getId() , holder.btn_follow);
//
//        if (user.getId().equals(firebaseUser.getUid())){
//            holder.btn_follow.setVisibility(View.GONE);
//        }
//
//        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.btn_follow.getText().toString().equals(("follow"))){
//                    FirebaseDatabase.getInstance().getReference().child("Follow").
//                            child((firebaseUser.getUid())).child("following").child(user.getId()).setValue(true);
//
//                    FirebaseDatabase.getInstance().getReference().child("Follow").
//                            child(user.getId()).child("followers").child(firebaseUser.getUid()).setValue(true);
//                    addNotification(user.getId());
//                } else {
//                    FirebaseDatabase.getInstance().getReference().child("Follow").
//                            child((firebaseUser.getUid())).child("following").child(user.getId()).removeValue();
//
//                    FirebaseDatabase.getInstance().getReference().child("Follow").
//                            child(user.getId()).child("followers").child(firebaseUser.getUid()).removeValue();
//                }
//            }
//        });
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isFargment) {
//                    mContext.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().putString("profileId", user.getId()).apply();
//
//                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new ProfileFragment()).commit();
//                } else {
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    intent.putExtra("publisherId", user.getId());
//                    mContext.startActivity(intent);
//                }
//            }
//        });
//
//    }
//
//    private void isFollowed(final String id, final Button btn_follow) {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
//                .child("following");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(id).exists())
//                    btn_follow.setText("following");
//                else
//                    btn_follow.setText("follow");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mUsers.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder{
//        public CircleImageView imageProfile;
//        public TextView username;
//        public TextView name;
//        public Button btn_follow;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageProfile = itemView.findViewById(R.id.image_profile);
//            username = itemView.findViewById(R.id.username);
//            name = itemView.findViewById(R.id.name);
//            btn_follow = itemView.findViewById(R.id.btn_follow);
//        }
//    }
//
//    private void addNotification(String userId) {
//        HashMap<String, Object> map = new HashMap<>();
//
//        map.put("userid", userId);
//        map.put("text", "started following you.");
//        map.put("postid", "");
//        map.put("isPost", false);
//
//        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid()).push().setValue(map);
//    }*/
//}
