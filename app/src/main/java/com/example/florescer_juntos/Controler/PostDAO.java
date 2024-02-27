package com.example.florescer_juntos.Controler;

import com.example.florescer_juntos.Model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PostDAO {
    private Post post;

    public PostDAO() {
        //Construtor vazio
    }

    public PostDAO(Post post) {
        this.post = post;
    }

    public Boolean save(){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
            HashMap<String, Object> map = new HashMap<>();
            map.put("desc", post.getDescricao());
            map.put("userId", post.getIdUsuario());
            map.put("image", post.getImageUrl());
            map.put("type", post.getTipoPlanta());
            map.put("typeUser", post.getTipoUsuario());
            map.put("datetime", post.getDataHora());
            // Salvo no banco de dados
            databaseReference.child(databaseReference.push().getKey()).setValue(map);
            return true;
    }





    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
