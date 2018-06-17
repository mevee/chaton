package com.example.vikesh.chaton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mUserDatabase;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword;
    private Button submitbtn,forgetPassword;
    private ProgressDialog loginDialog;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Login");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginDialog = new ProgressDialog(this);

        loginDialog.setTitle("Loging in");

        mAuth = FirebaseAuth.getInstance();
        mUserDatabase =FirebaseDatabase.getInstance().getReference().child("Users");
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.email_textinputlayout_login);

        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.password_inputtextlayout_login);

       forgetPassword = (Button)findViewById(R.id.forgetPassword_btn);
        submitbtn = (Button) findViewById(R.id.submit_btn_login);

        submitbtn.setOnClickListener(new View.OnClickListener() {

                                         @Override
                                         public void onClick(View view) {
                                             String passsword = textInputLayoutPassword.getEditText().getText().toString();

                                             String email = textInputLayoutEmail.getEditText().getText().toString();

                                             if (validations(passsword, email)) {
                                                 signIn(email, passsword);
                                             }
                                         }
                                     }
        );
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgetPassword = new Intent(SignInActivity.this,ForgetPasswordActivtiy.class);
                startActivity(forgetPassword);
            }
        });
    }

    private void signIn(String email, String password) {
        loginDialog.show();

        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            loginDialog.dismiss();
                            String uid = mAuth.getCurrentUser().getUid();

                                    Intent homeIntent = new Intent(SignInActivity.this, HomeActivity.class);
                                    homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(homeIntent);
                                    finish();

                                }
                           else {
                                    loginDialog.hide();
                            Toast.makeText(SignInActivity.this,
                                    "error :"+task.getException(), Toast.LENGTH_SHORT).show();
                                }


                        }
                    });
    }

    private boolean validations(String password, String email) {

        if (email.isEmpty()) {
            textInputLayoutEmail.setError("insert email");

            textInputLayoutEmail.requestFocus();
            return false;
        }
        if (password.isEmpty())

        {
            textInputLayoutPassword.setError("insert password");
            textInputLayoutPassword.requestFocus();

            return false;
        }
        return true;


    }
}
