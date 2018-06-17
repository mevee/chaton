package com.example.vikesh.chaton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusUpDateActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ProgressDialog pDialog;
    private TextInputLayout textInputLayoutStatus;
    private Button btnSave;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_up_date);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Saving Changes");
        pDialog.setMessage("wait while we save the changes");
        mToolbar = (Toolbar) findViewById(R.id.toolbar_profileupdate);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("update status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        btnSave = (Button) findViewById(R.id.btn_save_profileupdate);

        btnSave.setVisibility(View.INVISIBLE);
        btnSave.setEnabled(false);
        textInputLayoutStatus = (TextInputLayout) findViewById(R.id.til_status_profileupdate);
        textInputLayoutStatus.getEditText().setText(intent.getExtras().getString("status"));
        textInputLayoutStatus.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnSave.setVisibility(View.VISIBLE);
                btnSave.setEnabled(true);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveStatus();
            }
        });
    }

    private void saveStatus() {
            pDialog.show();
        String status = textInputLayoutStatus.getEditText().getText().toString();

        mDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(StatusUpDateActivity.this, "status updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    pDialog.hide();
                    Toast.makeText(StatusUpDateActivity.this,"error :"+ task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private boolean validations(String status) {
        if (status == null) {
            return false;
        }
        return true;
    }
}

