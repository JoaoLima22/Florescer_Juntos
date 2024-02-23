package com.example.florescer_juntos.Controler;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class UsuarioDAO {
    private Usuario user;

    public interface UsuarioCallback {
        void onUsuarioCarregado(Usuario usuario);
    }
    private UsuarioCallback callback;

    public UsuarioDAO(Usuario user) {
        this.user = user;
    }

    // Método que salva um usuário
    public Boolean save(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
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

    // Função que retorna o usuário de forma assincrona
    public void getUsuarioAsync(String email, DatabaseReference databaseReference, Activity activity, UsuarioCallback callback) {
        this.callback = callback;
        Query usuarios = databaseReference.orderByChild("mail").equalTo(email);
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usuario user = new Usuario();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        user.setId(snapshot.getKey());

                        String nome = snapshot.child("name").getValue(String.class);
                        user.setNome(nome != null ? nome : ""); //Caso algum campo venha vazio

                        String email = snapshot.child("mail").getValue(String.class);
                        user.setEmail(email != null ? email : "");

                        String telefone = snapshot.child("phone").getValue(String.class);
                        user.setTelefone(telefone != null ? telefone : "");

                        String descricao = snapshot.child("desc").getValue(String.class);
                        user.setDescricao(descricao != null ? descricao : "");

                        String senha = snapshot.child("password").getValue(String.class);
                        user.setSenha(senha != null ? senha : "");

                        String imageUrl = snapshot.child("photo").getValue(String.class);
                        user.setImageUrl(imageUrl != null ? imageUrl : Uri.parse("android.resource://" + activity.getPackageName() + "/" + R.drawable.perfil_image).toString());

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

    public interface ExistenceCheckCallback {
        void onResult(boolean exists);
    }
    // Função que verifica se há um usuário salvo com determinado email
    public void isSaved(String email, DatabaseReference databaseReference, ExistenceCheckCallback callback) {
        Query usuarios = databaseReference.orderByChild("mail").equalTo(email);
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                callback.onResult(exists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Erro ao ler dados", databaseError.toException());
                callback.onResult(false);
            }
        });
    }

    // Função que deleta usuários por email
    public void deleteUsuarioByEmail(String email, DatabaseReference databaseReference) {
        Query query = databaseReference.orderByChild("mail").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Erro
            }
        });
    }
    public void updateUsuario(String userId, Map<String, Object> updates, DatabaseReference databaseReference) {
        databaseReference.child(userId).setValue(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "Usuário atualizado com sucesso");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Erro ao atualizar usuário", e);
                    }
                });
    }


    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
}
