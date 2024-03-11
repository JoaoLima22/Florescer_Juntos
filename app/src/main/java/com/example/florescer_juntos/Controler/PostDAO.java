package com.example.florescer_juntos.Controler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.florescer_juntos.Helper.PostDbHelper;
import com.example.florescer_juntos.Model.Post;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostDAO {
    private Post post;
    private PostDbHelper db;


    public PostDAO() {
        //Construtor vazio
    }

    public PostDAO(Post post, Context context) {
        this.post = post;
        this.db = new PostDbHelper(context);
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
            map.put("mailUser", post.getEmailUsuario());
            // Salvo no banco de dados
            databaseReference.child(databaseReference.push().getKey()).setValue(map);
            return true;
    }

    public boolean saveOffline(){
        SQLiteDatabase dbLite = this.db.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put("id", post.getId());
        content.put("descricao", post.getDescricao());
        content.put("userId", post.getIdUsuario());
        content.put("image", post.getImageUrl());
        content.put("type", post.getTipoPlanta());
        content.put("typeUser", post.getTipoUsuario());
        content.put("datetime", post.getDataHora());
        content.put("mailUser", post.getEmailUsuario());

        //Defino a "tabela", oq substituirá se houver valor nulo, e os valores
        long id = dbLite.insert("post", null, content);

        if (id == -1) {return false;} else {return true;}
    }

    public boolean updatePost() {
        SQLiteDatabase dbLite = this.db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("descricao", post.getDescricao());
        values.put("userId", post.getIdUsuario());
        values.put("image", post.getImageUrl());
        values.put("type", post.getTipoPlanta());
        values.put("typeUser", post.getTipoUsuario());
        values.put("datetime", post.getDataHora());
        values.put("mailUser", post.getEmailUsuario());

        String selection = "id = ?";
        String[] selectionArgs = {post.getId()};

        int count = dbLite.update("post", values, selection, selectionArgs);

        return count > 0;
    }

    public List<Post> getPosts() {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase dbLite = this.db.getReadableDatabase();

        // Especifica as colunas que deseja retornar...
        String[] projection = {"id", "descricao", "userId", "image", "type", "typeUser", "datetime", "mailUser"};

        // Executa a query para obter todos os usuários
        Cursor c = dbLite.query(
                "post",                 // A tabela
                projection,             // As colunas que deseja retornar
                null,                   // A cláusula WHERE
                null,                   // Os valores para a cláusula WHERE
                null,                   // Não agrupar as linhas
                null,                   // Não filtrar por grupos de linhas
                null                    // A ordem de classificação
        );

        // Move o cursor para o primeiro resultado
        if (c != null) {
            c.moveToFirst();
            while (c.moveToNext()) {
                Post post = new Post();
                post.setId(c.getString(0));
                post.setDescricao(c.getString(1));
                post.setIdUsuario(c.getString(2));
                post.setImageUrl(c.getString(3));
                post.setTipoPlanta(c.getString(4));
                post.setTipoUsuario(c.getString(5));
                post.setDataHora(c.getString(6));
                post.setEmailUsuario(c.getString(7));
                postList.add(post);
            }
            c.close();
        }

        return postList;
    }
    public boolean isPostSaved(String postId) {
        SQLiteDatabase dbLite = this.db.getReadableDatabase();

        String[] projection = {"id", "descricao", "userId", "image", "type", "typeUser", "datetime", "mailUser"};
        String selection = "id = ?";
        String[] selectionArgs = {postId};

        Cursor c = dbLite.query(
                "post",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean postSaved = c != null && c.moveToFirst();
        if (c != null) {
            c.close();
        }

        return postSaved;
    }
    public List<Post> getPostByIdUser(String userId) {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase dbLite = this.db.getReadableDatabase();

        String[] projection = {"id", "descricao", "userId", "image", "type", "typeUser", "datetime", "mailUser"};
        String selection = "userId = ?";
        String[] selectionArgs = {userId};

        Cursor c = dbLite.query(
                "post",
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // Percorre todas as linhas de resultados
        if (c != null) {
            while (c.moveToNext()) {
                Post post = new Post();
                post.setId(c.getString(0));
                post.setDescricao(c.getString(1));
                post.setIdUsuario(c.getString(2));
                post.setImageUrl(c.getString(3));
                post.setTipoPlanta(c.getString(4));
                post.setTipoUsuario(c.getString(5));
                post.setDataHora(c.getString(6));
                post.setEmailUsuario(c.getString(7));
                postList.add(post);
            }
            c.close();
        }

        return postList;
    }


    public boolean deletePost() {
        SQLiteDatabase dbLite = this.db.getWritableDatabase();

        String selection = "id = ?";
        String[] selectionArgs = {post.getId()};

        int count = dbLite.delete("post", selection, selectionArgs);

        return count > 0;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
