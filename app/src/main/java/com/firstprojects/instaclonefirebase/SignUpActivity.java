package com.firstprojects.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    StorageReference storageRef;
    EditText eMailText , passWordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        eMailText = findViewById(R.id.eMailText);
        passWordText = findViewById(R.id.passWordText);
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();



        if(firebaseUser != null) {
            Intent intent = new Intent(SignUpActivity.this,FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public void signIN(View view) {
        String eMail = eMailText.getText().toString();
        String passWord = passWordText.getText().toString();
       if(eMail.length() > 0 && passWord.length() > 0) {
           mAuth.signInWithEmailAndPassword(eMail, passWord).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

               @Override
               public void onSuccess(AuthResult authResult) {
                   Toast.makeText(SignUpActivity.this, "SIGN IN is Successfull.", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(SignUpActivity.this,FeedActivity.class);
                   startActivity(intent);
                   finish();

               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
               }
           });
       }else {
           Toast.makeText(this, "Are You Crazy? You must write the thing expected", Toast.LENGTH_LONG).show();
       }
    }




    public void signUP(View view) {
        String eMail = eMailText.getText().toString();
        String passWord = passWordText.getText().toString();
        if( eMail.length() > 0 && passWord.length() > 0) {
            mAuth.createUserWithEmailAndPassword(eMail,passWord).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(SignUpActivity.this, "SIGN UP is Successfull.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "Are You Crazy? You must write the thing expected", Toast.LENGTH_LONG).show();
        }

    }

}