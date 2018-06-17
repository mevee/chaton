package com.example.vikesh.chaton;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity  {

    private TextView tvId;
    private CircleImageView profileImage;
    private TextView tvStatus,
            tvProfilename, tvProfileState, tvProfileFrienCount;
    private Button sendFriendRequestbtn, deleteFriendrequestbtn;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurentuser ;
    private ProgressDialog progressDialog;
    private final int NOT_FRIEND = 0,REQEST_SENT = 1,REQUEST_RECEIVED=2,FRIEND=3;
    private int currentState;
    private String mCurrent_state;
    private DatabaseReference mFriendrequestDatbase;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mNotificationDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String uid = getIntent().getStringExtra("user_id");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mCurrent_state = "not_friends";


        mFriendrequestDatbase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends");

        mCurentuser = FirebaseAuth.getInstance().getCurrentUser();
        mNotificationDatabase =FirebaseDatabase.getInstance().getReference().child("notifications");

        sendFriendRequestbtn = (Button) findViewById(R.id.friend_request_btn);
        deleteFriendrequestbtn = (Button) findViewById(R.id.delete_friend_request_btn);

        deleteFriendrequestbtn.setVisibility(View.INVISIBLE);
        deleteFriendrequestbtn.setEnabled(false);
        profileImage = (CircleImageView) findViewById(R.id.profile_image_imageview);
        tvProfilename = (TextView) findViewById(R.id.profile_name_tv);
        tvStatus = (TextView) findViewById(R.id.status_profile_tv);

        progressDialog = new ProgressDialog( this);
        progressDialog.setTitle("loading");
        progressDialog.setMessage("wait while loading user data");
        progressDialog.setCanceledOnTouchOutside(false);
        currentState = NOT_FRIEND;

        if(mCurentuser.getUid().equals(uid)){

            deleteFriendrequestbtn.setEnabled(false);
            deleteFriendrequestbtn.setVisibility(View.INVISIBLE);

            sendFriendRequestbtn.setEnabled(false);
            sendFriendRequestbtn.setVisibility(View.INVISIBLE);

        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String status = dataSnapshot.child("status").getValue().toString();
                String name = dataSnapshot.child("username").getValue().toString();
               final String image = dataSnapshot.child("imageurl").getValue().toString();
                tvStatus.setText(status);
                tvProfilename.setText(name);
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(profileImage);

                mFriendrequestDatbase.child(mCurentuser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                   if(dataSnapshot.hasChild(uid)){

                       String request_type = dataSnapshot.child(uid).child("request_type").getValue().toString();

                       if(request_type.equals("received"))
                       {
                           currentState = REQUEST_RECEIVED;
                           sendFriendRequestbtn.setText("Accept friend request");
                           deleteFriendrequestbtn.setVisibility(View.INVISIBLE);
                           deleteFriendrequestbtn.setEnabled(true);
                       }
                       else if(request_type.equals("sent")){
                           currentState = REQEST_SENT;
                           sendFriendRequestbtn.setText("Cancel Friend Request");
                           deleteFriendrequestbtn.setVisibility(View.INVISIBLE);
                           deleteFriendrequestbtn.setEnabled(false);
                       }
                       else {
                           mFriendsDatabase.child(mCurentuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(DataSnapshot dataSnapshot) {
                                   if(dataSnapshot.hasChild(uid))
                                   {    currentState = FRIEND;
                                       sendFriendRequestbtn.setText("Unfriend");
                                       deleteFriendrequestbtn.setVisibility(View.INVISIBLE);
                                       deleteFriendrequestbtn.setEnabled(false);
                                   }
                               }
                               @Override
                               public void onCancelled(DatabaseError databaseError) {

                               }
                           });
                       }
                   }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendFriendRequestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFriendRequestbtn.setEnabled(false);

                if (currentState == NOT_FRIEND) {//-------------------------------------------code to send a friendTxt request
                    progressDialog.show();
                    mFriendrequestDatbase.child(mCurentuser.getUid()).child(uid).child("request_type").setValue("sent").
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mFriendrequestDatbase.child(uid).child(mCurentuser.getUid()).child("request_type").setValue("received").
                                                addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressDialog.dismiss();
                                                        sendFriendRequestbtn.setEnabled(true);
                                                        HashMap<String,String> notificationmap =new HashMap<>();
                                                        notificationmap.put("from",mCurentuser.getUid());
                                                        notificationmap.put("type","request");
                                                        mNotificationDatabase.child(uid).push().setValue(notificationmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                currentState = REQEST_SENT;
                                                                sendFriendRequestbtn.setText("Cancel Friend Request");
                                                                deleteFriendrequestbtn.setVisibility(View.INVISIBLE);
                                                                deleteFriendrequestbtn.setEnabled(false);
                                                                Toast.makeText(ProfileActivity.this, "request sent successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });
                                    } else
                                        Toast.makeText(ProfileActivity.this, "failed to sent friendTxt request", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
//                -------------------------request Received state or acepting a friendTxt requesst----------------

                if (currentState == REQUEST_RECEIVED)
                {      progressDialog.show();
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    mFriendsDatabase.child(mCurentuser.getUid()).child(uid).child("friend_ship_date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                       mFriendsDatabase.child(uid).child(mCurentuser.getUid()).child("friend_ship_date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {

                               mFriendrequestDatbase.child(mCurentuser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                   @Override
                                   public void onSuccess(Void aVoid) {
                                       mFriendrequestDatbase.child(uid).child(mCurentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {

                                               mNotificationDatabase.child(mCurentuser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                   @Override
                                                   public void onSuccess(Void aVoid) {
                                                       mNotificationDatabase.child(uid).child(mCurentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                           @Override
                                                           public void onSuccess(Void aVoid) {
                                                               progressDialog.dismiss();
                                                               sendFriendRequestbtn.setEnabled(true);
                                                               sendFriendRequestbtn.setText("Unfriend");
                                                               currentState = FRIEND;

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
                //--------------------------if friends and want ti\o unfriend----------------
                if(currentState==FRIEND)
                {      progressDialog.show();
                    mFriendsDatabase.child(mCurentuser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendsDatabase.child(uid).child(mCurentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    sendFriendRequestbtn.setEnabled(true);
                                    sendFriendRequestbtn.setText("send friendTxt request");
                                    currentState = NOT_FRIEND;
                                    deleteFriendrequestbtn.setVisibility(View.INVISIBLE);
                                    deleteFriendrequestbtn.setEnabled(false);
                                }
                            });
                        }
                    });

                }
                //-----------------------------when request is been sent and user want to delete request ----------------
                if (currentState==REQEST_SENT)
                {        progressDialog.show();
                    mFriendrequestDatbase.child(mCurentuser.getUid()).child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendrequestDatbase.child(uid).child(mCurentuser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    sendFriendRequestbtn.setEnabled(true);
                                    sendFriendRequestbtn.setText("send friendTxt request");
                                    currentState = NOT_FRIEND;
                                    deleteFriendrequestbtn.setVisibility(View.INVISIBLE);
                                    deleteFriendrequestbtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }
        }
    });


        }

}
