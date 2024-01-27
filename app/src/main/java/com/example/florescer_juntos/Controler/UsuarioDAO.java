package com.example.florescer_juntos.Controler;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.florescer_juntos.Model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UsuarioDAO {
    private Usuario user;
    private DatabaseReference databaseReference;

    public interface UsuarioCallback {
        void onUsuarioCarregado(Usuario usuario);
    }
    private UsuarioCallback callback;

    public UsuarioDAO(Usuario user) {
        this.user = user;

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
    }

    public Boolean save(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", user.getNome());
        map.put("phone", user.getTelefone());
        map.put("mail", user.getEmail());
        map.put("password", user.getSenha());
        map.put("desc", user.getDescricao());
        map.put("photo", user.getImageUrl());
        // Salvo no banco de dados
        databaseReference.child(databaseReference.push().getKey()).setValue(map);
        return true;
    }


    public void getUsuarioAsync(String email, UsuarioCallback callback) {
        this.callback = callback;
        Query usuarios = databaseReference.orderByChild("mail").equalTo(email);
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usuario user = new Usuario();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        user.setNome(snapshot.child("name").getValue(String.class));
                        user.setEmail(snapshot.child("mail").getValue(String.class));
                        user.setTelefone(snapshot.child("phone").getValue(String.class));
                        user.setSenha(snapshot.child("password").getValue(String.class));
                        user.setImageUrl(snapshot.child("photo").getValue(String.class));
                    }
                    // Notificar a interface quando os dados estiverem prontos
                    callback.onUsuarioCarregado(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ocorreu um erro durante a leitura dos dados
                Log.e("Firebase", "Erro ao ler dados", databaseError.toException());
            }
        });
    }


    public Boolean isSaved(String email){
        Boolean is = false;
        final int[] count = {0};
        Query usuarios = databaseReference.orderByChild("mail").equalTo(email);
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        count[0] = 1;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ocorreu um erro durante a leitura dos dados
                Log.e("Firebase", "Erro ao ler dados", databaseError.toException());
            }
        });

        if(count[0] != 0){is = true;}

        return is;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
}
