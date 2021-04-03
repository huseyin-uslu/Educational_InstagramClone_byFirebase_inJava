package com.firstprojects.instaclonefirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.PostHold>{
    private ArrayList<String> eMailArrayList;
    private ArrayList<String> postArrayList;
    private ArrayList<String> imageArrayList;

    public FeedRecyclerViewAdapter(ArrayList<String> eMailArrayList, ArrayList<String> postArrayList, ArrayList<String> imageArrayList) {
        this.eMailArrayList = eMailArrayList;
        this.postArrayList = postArrayList;
        this.imageArrayList = imageArrayList;
    }




    @NonNull
    @Override
    public PostHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycle_view_row,parent,false);
        return new PostHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHold holder, int position) {
    holder.emailTextView.setText(eMailArrayList.get(position));
    holder.postTextView.setText(postArrayList.get(position));
        Picasso.get().load(imageArrayList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return eMailArrayList.size();
    }

    class PostHold extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView postTextView , emailTextView;


        public PostHold(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recyleview_picture);
            postTextView = itemView.findViewById(R.id.recyleview_post);
            emailTextView = itemView.findViewById(R.id.recyleview_email);
        }
    }
}
