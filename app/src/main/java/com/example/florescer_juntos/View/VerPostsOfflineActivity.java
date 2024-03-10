package com.example.florescer_juntos.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.florescer_juntos.Controler.PostDAO;
import com.example.florescer_juntos.ImageAdapter;
import com.example.florescer_juntos.Model.Post;
import com.example.florescer_juntos.R;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerPostsOfflineActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<Post> mPosts;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_posts_offline);

        mRecyclerView = findViewById(R.id.postsViewOff);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mPosts = new ArrayList<>();

        PostDAO postDAO = new PostDAO(null, getApplicationContext());
        mPosts = postDAO.getPosts();
        Collections.reverse(mPosts);
        mAdapter = new ImageAdapter(getApplicationContext(), mPosts, "000");
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(VerPostsOfflineActivity.this);
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onSaveClick(int position) {

    }

    @Override
    public void onVerPerfilClick(int position) {

    }

    @Override
    public void onEditarClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {

    }

    @Override
    public void onDeleteOffClick(int position) {
        Post selectedItem = mPosts.get(position);
        PostDAO postDAO = new PostDAO(selectedItem, getApplicationContext());
        if (postDAO.deletePost()){
            //Deletar o arquivo aqui também
            File oldImageFile = new File(selectedItem.getImageUrl());
            if (oldImageFile.exists()) {
                if (oldImageFile.delete()) {
                    Toast.makeText(this, "Post deletado", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, VerPostsOfflineActivity.class));
                    finish();
                }
            }
        }
    }
}