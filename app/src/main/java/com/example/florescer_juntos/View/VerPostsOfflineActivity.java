package com.example.florescer_juntos.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.florescer_juntos.Controler.PostDAO;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.ImageAdapter;
import com.example.florescer_juntos.Model.Post;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerPostsOfflineActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private List<Post> mPosts;
    private ProgressBar mProgressCircle;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_posts_offline);

        mProgressCircle = findViewById(R.id.progress_circleOff);
        mRecyclerView = findViewById(R.id.postsViewOff);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mPosts = new ArrayList<>();

        PostDAO postDAO = new PostDAO(null, getApplicationContext());
        mPosts = postDAO.getPosts();
        Collections.reverse(mPosts);
        mAdapter = new ImageAdapter(getApplicationContext(), mPosts, "000");

        mRecyclerView.setAdapter(mAdapter);
        if(mPosts.size()>0){
            mProgressCircle.setVisibility(View.INVISIBLE);
        }
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
    public void onComentarClick(int position) {

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