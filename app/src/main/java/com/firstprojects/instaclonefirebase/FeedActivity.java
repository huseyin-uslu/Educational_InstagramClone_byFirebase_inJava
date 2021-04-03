package com.firstprojects.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    ArrayList<String> usersEmailArray;
    ArrayList<String> usersPicUrlArray;
    ArrayList<String> usersPostArray;
    FeedRecyclerViewAdapter feedRecyclerViewAdapter;
            RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        //
        usersEmailArray = new ArrayList<>();
        usersPicUrlArray = new ArrayList<>();
        usersPostArray = new ArrayList<>();
        //
        TheMethodOfProcess();
        //RecyclerView Set Up Adapter
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerViewAdapter = new FeedRecyclerViewAdapter(usersEmailArray,usersPostArray,usersPicUrlArray);
        recyclerView.setAdapter(feedRecyclerViewAdapter);
    }
    public void TheMethodOfProcess() { //to make searching "a , e , i , b " as scanning a lecture Reach to the result. I'll arrive that I want to the result.
        CollectionReference reference = firestore.collection("Posts");
        reference.orderBy("postsDate", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if(value != null){
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                       Map<String , Object> usersData = snapshot.getData();
                       if(usersData != null && usersData.size() > 0) {
                           //PuttingArrayProcess
                           usersPostArray.add(String.valueOf(usersData.get("usersPost")));
                           usersEmailArray.add(String.valueOf(usersData.get("usersEmail")));
                           usersPicUrlArray.add(String.valueOf(usersData.get("picturesUri")));
                           feedRecyclerViewAdapter.notifyDataSetChanged();
                       }

                    }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.upload_options_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.addPost) {
            Intent intentToUpload = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpload);

        }else if(item.getItemId() == R.id.signOut) {
            auth.signOut();//user's account is being signed out
            Intent intentToSignOut = new Intent(FeedActivity.this,SignUpActivity.class);
            startActivity(intentToSignOut);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}