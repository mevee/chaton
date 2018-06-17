package com.example.vikesh.chaton.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.vikesh.chaton.ProfileActivity;
import com.example.vikesh.chaton.R;
import com.example.vikesh.chaton.models.FriendRequest;
import com.example.vikesh.chaton.models.RequestHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestFragment extends Fragment {
    private RecyclerView mReqList;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");
    private DatabaseReference mUsers;
    private DatabaseReference mFriendrequestDatbase;
    private FirebaseAuth mAuth;
    private DatabaseReference mNotificationDatabase;
    private String mCurrent_user_id;
    private View mMainView;
    private RequestHolder requestHolder = new RequestHolder();

    public RequestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_request, container, false);
        mNotificationDatabase =FirebaseDatabase.getInstance().getReference().child("notifications");
        mUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendrequestDatbase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mReqList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mUsersDatabase.keepSynced(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mReqList.setLayoutManager(linearLayoutManager);



        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
    FirebaseRecyclerAdapter<FriendRequest,FriendReqViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FriendRequest, FriendReqViewHolder>(
            FriendRequest.class,
            R.layout.friend_reqest,
            FriendReqViewHolder.class,
            mUsersDatabase

    ) {
        @Override
        protected void populateViewHolder(final FriendReqViewHolder viewHolder, FriendRequest model, int position) {
            final String uid =getRef(position).getKey();
            String reqType = model.getRequest_type();
            viewHolder.declineReqst.setVisibility(View.INVISIBLE);
            viewHolder.declineReqst.setVisibility(View.INVISIBLE);
            Log.d("RequestFragment", uid + reqType);
            if(reqType.equals("received"))
            {
                mUsers.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setName(dataSnapshot.child("username").getValue().toString());
                        viewHolder.setUserImage(dataSnapshot.child("thumb_image").getValue().toString());
                        viewHolder.acceptReqst.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                mFriendsDatabase.child(mCurrent_user_id).child(uid).child("friend_ship_date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mFriendsDatabase.child(uid).child(mCurrent_user_id).child("friend_ship_date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                mFriendrequestDatbase.child(mCurrent_user_id).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mFriendrequestDatbase.child(uid).child(mCurrent_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                mNotificationDatabase.child(mCurrent_user_id).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        mNotificationDatabase.child(uid).child(mCurrent_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {


                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }

                        });
                       viewHolder.declineReqst.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               mFriendrequestDatbase.child(mCurrent_user_id).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       mFriendrequestDatbase.child(uid).child(mCurrent_user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                           }
                                       });
                                   }
                               });
                           }
                       });
                       viewHolder.userImageView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                               profileIntent.putExtra("user_id",uid);
                               startActivity(profileIntent);
                           }
                       });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    };
    mReqList.setAdapter(firebaseRecyclerAdapter);
    }

        public static class FriendReqViewHolder extends RecyclerView.ViewHolder {
            private Button acceptReqst;
            private Button declineReqst;
            private View mView;
            CircleImageView userImageView;

            public FriendReqViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                acceptReqst =(Button)mView.findViewById(R.id.friend_reqest_acceptrequestbtn);
                declineReqst =(Button)mView.findViewById(R.id.friend_reqest_declinerequestbtn);
            }



            public void setName(String name) {
                TextView userNameView = (TextView) mView.findViewById(R.id.friend_reqest_usernametv);
                userNameView.setText(name);

            }

            public void setUserImage(String thumb_image) {

                userImageView = (CircleImageView) mView.findViewById(R.id.friend_reqest_image);
                Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

            }
    }
}
