package com.example.vikesh.chaton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.vikesh.chaton.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference  mDatabaseRefrence;
    private TextInputLayout textInputLayoutEmail,textInputLayoutPassword,textInputLayoutcPassword,textInputLayoutUserName;
    private Button submitbtn;
    private ProgressDialog signupDialog;
    private Toolbar mToolbar;
    private String TAG ="SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mToolbar = (Toolbar)findViewById(R.id.apbar_signin);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Signup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        signupDialog = new ProgressDialog(this);
        signupDialog.setTitle("Signing up");


        textInputLayoutEmail = (TextInputLayout)findViewById(R.id.til_email);
        textInputLayoutPassword = (TextInputLayout)findViewById(R.id.til_password);
        textInputLayoutcPassword = (TextInputLayout)findViewById(R.id.til_cpassword);
        textInputLayoutUserName = (TextInputLayout)findViewById(R.id.til_username);
        submitbtn = (Button)findViewById(R.id.submit_btn_signupactivity);

        Log.d(TAG,"onCreate");
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = textInputLayoutEmail.getEditText().getText().toString();
                String password = textInputLayoutPassword.getEditText().getText().toString();
                String cPassword = textInputLayoutcPassword.getEditText().getText().toString();
                String userName = textInputLayoutUserName.getEditText().getText().toString();
                if(validations(userName,password,cPassword,email))
                {
                    signUp(email,password,userName);
                }
            }
        });
    }

    private void signUp(String email, String password, final String username)
    {
        mAuth = FirebaseAuth.getInstance();
        signupDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    mUser =  mAuth.getCurrentUser();
                    String u_Id =mUser.getUid();

                     mDatabaseRefrence =  FirebaseDatabase.getInstance().getReference().child("Users").child(u_Id);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String,String> myData =new HashMap<>();
                    myData.put("username",username);
                    myData.put("status","Hi there i am using Chat On ");
                    myData.put("imageurl","default");
                    myData.put("thumb_image","default");
                    myData.put("device_token",deviceToken);
                    mDatabaseRefrence.setValue(myData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           signupDialog.dismiss();
                           Intent intent = new Intent(SignUpActivity.this,StartingActivity.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(intent);
                           finish();
                           Toast.makeText(SignUpActivity.this, "creation successful", Toast.LENGTH_SHORT).show();

                       }
                       }
                    });

                }else
                    {
                        signupDialog.hide();
                    Toast.makeText(SignUpActivity.this, "some error occured", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private boolean validations(String user,String password,String password2,String email)
    {
        if(email.isEmpty())
        {
            textInputLayoutEmail.setError("insert email");
            textInputLayoutEmail.requestFocus();
            return false;
        }
        if(user.isEmpty())
        {
            textInputLayoutUserName.setError("insert user name");
            textInputLayoutEmail.requestFocus();
            return false;
        }
        if(password.isEmpty())
        {
            textInputLayoutPassword.setError("insert password");
            textInputLayoutPassword.requestFocus();
            return false;
        }
        if(password2.isEmpty())
        {
            textInputLayoutcPassword.setError("insert password");
            textInputLayoutcPassword.requestFocus();
            return false;
        }
        if (password.equals(password2)!=true)
        {
            Toast.makeText(SignUpActivity.this,"Password did not matched", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
