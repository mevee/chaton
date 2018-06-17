package com.example.vikesh.chaton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vikesh.chaton.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSeetingsActivity extends AppCompatActivity {
    private TextView tvstatus, tvUserName;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRefrence;
    private FirebaseUser mCurrentUser;
    private CircleImageView imageView;
    private Button changeImage, changeStatus;
    private Toolbar mToolbar;
    private ProgressDialog pDialog;
    private StorageReference mStorageRef;
    private static final int RESQUEST_GALLARY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_seetings);
        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("uploading image");
        pDialog.setMessage("plese wait while uploading the image");
        pDialog.setCanceledOnTouchOutside(false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_accountsettins);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeImage = (Button) findViewById(R.id.btn_changeimage_setting_activity);
        changeStatus = (Button) findViewById(R.id.btn_change_status_settingactivity);
        imageView = (CircleImageView) findViewById(R.id.imageview_profile_AccountSetting);
        tvstatus = (TextView) findViewById(R.id.tv_status_accountsettingactivity);
        tvUserName = (TextView) findViewById(R.id.tv_username_settingactivty);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String u_Id = mCurrentUser.getUid();

        mDatabaseRefrence = FirebaseDatabase.getInstance().getReference().child("Users").child(u_Id);

        mDatabaseRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String username = dataSnapshot.child("username").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                String imagepath = dataSnapshot.child("imageurl").getValue(String.class);
                String thumbpath = dataSnapshot.child("thumb_image").getValue(String.class);

                tvUserName.setText(username);
                tvstatus.setText(status);
                final String imageUrl = imagepath;

                if (!imageUrl.equals("default") ) {

                    Picasso.get().load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.get().load(imageUrl).placeholder(R.drawable.default_avatar).into(imageView);

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(imageUrl).placeholder(R.drawable.default_avatar).into(imageView);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pDialog.hide();
            }
        });
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent updatestatusIntent = new Intent(AccountSeetingsActivity.this, StatusUpDateActivity.class);
                updatestatusIntent.putExtra("status", tvstatus.getText().toString());
                updatestatusIntent.putExtra("username", tvUserName.getText().toString());
                startActivity(updatestatusIntent);
            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent galleryIntent = new Intent();
//                galleryIntent.setType("image/*");
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), RESQUEST_GALLARY);
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AccountSeetingsActivity.this);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESQUEST_GALLARY && resultCode == RESULT_OK) {
            Uri uriImage = data.getData();
            //this code will dave the cropped image
            CropImage.activity(uriImage)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                pDialog.show();
                Uri resultUri = result.getUri();

                File thumb_filepath = new File(resultUri.getPath());
                String currentUid = mAuth.getCurrentUser().getUid();

                Bitmap thumb_bitmap = null;


                try {
                    thumb_bitmap = new Compressor(AccountSeetingsActivity.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_filepath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference refPath = mStorageRef.child("profile_images").child(currentUid + "userimage.jpg");
                final StorageReference thum_path = mStorageRef.child("profile_images").child("thumbs").child(currentUid + "userimage.jpg");

                refPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String imageurl = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thum_path.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_url = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()) {
                                        Map  myMap = new HashMap<>();
                                        myMap.put("imageurl",imageurl);
                                        myMap.put("thumb_image",thumb_url);
                                        mDatabaseRefrence.updateChildren(myMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pDialog.dismiss();
                                                    Toast.makeText(AccountSeetingsActivity.this, "sucessfully uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else
                                        Toast.makeText(AccountSeetingsActivity.this, "error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            pDialog.hide();
                            Toast.makeText(AccountSeetingsActivity.this, "error while uploading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Picasso.get().load(resultUri).into(imageView);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
