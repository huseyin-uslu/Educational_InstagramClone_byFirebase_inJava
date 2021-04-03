package com.firstprojects.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


public class UploadActivity<bitmap> extends AppCompatActivity { ImageView imageView;
EditText addPostText;
Bitmap selectedImage;
Uri imagesData;
private FirebaseAuth auth;
private FirebaseStorage storage;
private StorageReference storageRef;
private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = findViewById(R.id.imageView);
        addPostText = findViewById(R.id.addPostText);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

    }
    public void addImage(View view){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK && data != null) {
           imagesData = data.getData();
           if(Build.VERSION.SDK_INT >= 28) {
               ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imagesData);
               try {
                   selectedImage = ImageDecoder.decodeBitmap(source);
                   Bitmap bitmapArranged = makeSmallerBitmap(selectedImage,1335);
                   imageView.setImageBitmap(bitmapArranged);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }else {
               try {
                   selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imagesData);
                   Bitmap bitmapArranged = makeSmallerBitmap(selectedImage,1335);
                   imageView.setImageBitmap(bitmapArranged);
               } catch (IOException e) {
                   e.printStackTrace();
               }

           }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && ContextCompat.checkSelfPermission(UploadActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }
    public Bitmap makeSmallerBitmap(Bitmap bitmap , int maxSize){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float rate = (float)width / (float)height;
        if(rate >= 1) {
           width = maxSize - 285;
           height = (int)(width / rate);
        }else {
            height = maxSize;
            width = (int)(height * rate);
        }

        return Bitmap.createScaledBitmap(bitmap,width,height,true);

    }

    public void saveButton(View view){

//Universal Uniqe ID
        UUID uuid = UUID.randomUUID();
        Random random = new Random();
        int RNumb = random.nextInt();
        String imageName = "image/" + uuid + RNumb;
       
        if(imagesData != null) {
            storageRef.child(imageName).putFile(imagesData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   StorageReference  newReference = FirebaseStorage.getInstance().getReference(imageName);
                   newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                      @Override
                      public void onSuccess(Uri uri) {
                          FirebaseUser firebaseUser = auth.getCurrentUser();
                          //HashMap User's datas
                          String urio = uri.toString();
                          String postText = addPostText.getText().toString();
                          String eMailOfUser = firebaseUser.getEmail();

                          //Upload User's Datas as Hashmap form
                          HashMap<String, Object> uploadHashM = new HashMap<>();
                          uploadHashM.put("picturesUri",urio);
                          uploadHashM.put("usersPost",postText);
                          uploadHashM.put("usersEmail",eMailOfUser);
                          uploadHashM.put("postsDate", FieldValue.serverTimestamp());

                          firestore.collection("Posts").add(uploadHashM).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                              @Override
                              public void onSuccess(DocumentReference documentReference) {
                                  Intent intentToFeedActivity = new Intent(UploadActivity.this,FeedActivity.class);
                                  intentToFeedActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                  startActivity(intentToFeedActivity);

                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                  Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                              }
                          });

                      }
                  });

                    Toast.makeText(UploadActivity.this,"Upload is succesful!",Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }else {
            Toast.makeText(this, "At first , You must choose a picture!", Toast.LENGTH_LONG).show();
        }

    }
}