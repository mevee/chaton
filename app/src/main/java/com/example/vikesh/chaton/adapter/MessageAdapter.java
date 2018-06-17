package com.example.vikesh.chaton.adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.vikesh.chaton.R;
import com.example.vikesh.chaton.models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private String username;
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout ,parent, false);

        return new MessageViewHolder(v);

    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public ImageView messageImage;
        public RelativeLayout relativeLayout;

        public MessageViewHolder(View view) {
            super(view);
            relativeLayout = (RelativeLayout)view.findViewById(R.id.message_single_layout);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String currentU_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessageList.get(i);
        String from_user = c.getFrom();
          mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
          mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  username = dataSnapshot.child("username").getValue().toString();
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });

        if(from_user.equals(currentU_id))
        {
             viewHolder.messageText.setText(c.getMessage());
             viewHolder.relativeLayout.setBackgroundResource(R.drawable.message_layout_bg);
             viewHolder.displayName.setVisibility(View.INVISIBLE);
             viewHolder.profileImage.setVisibility(View.INVISIBLE);
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.displayName.setText(username);
            viewHolder.relativeLayout.setBackgroundResource(R.drawable.received_message_layout_bg);
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


}
