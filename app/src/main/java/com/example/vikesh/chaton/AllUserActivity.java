package com.example.vikesh.chaton;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vikesh.chaton.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUserActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mUserList;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluser);
        mToolbar = (Toolbar)findViewById(R.id.toolbar_allusers);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUserList =(RecyclerView)findViewById(R.id.recylerview_allusers);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User,UserHolder> recyclerAdapter =new FirebaseRecyclerAdapter<User, UserHolder>(
                User.class,
                R.layout.single_user_layout,
                UserHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(UserHolder viewHolder, User model, int position) {
            viewHolder.setName(model.getUsername());
            viewHolder.setStatus(model.getStatus());
            viewHolder.setThumb_Image(model.getThumb_image());
            final String u_id = getRef(position).getKey();

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent userprofileIntent = new Intent(AllUserActivity.this,ProfileActivity.class);
                    userprofileIntent.putExtra("user_id",u_id);
                    startActivity(userprofileIntent);

                }
            });
            }
        };
        mUserList.setAdapter(recyclerAdapter);
    }
    public static class UserHolder extends RecyclerView.ViewHolder{

     View mView;
        public UserHolder(View itemView) {
            super(itemView);
     mView = itemView;
     }
     public void setName(String name)
     {
         TextView tvname = (TextView)mView.findViewById(R.id.username_single_user);
         tvname.setText(name);
     }
        public void setStatus(String name)
        {
            TextView tvstatus = (TextView)mView.findViewById(R.id.status_single_user);
            tvstatus.setText(name);
        }
        public void setThumb_Image(String urlimage)
        {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.imageview_singleuser);
//            if (!urlimage.equals("default") && !urlimage.equals("dafault"))
            Picasso.get().load(urlimage).into(image);
        }

    }
}
