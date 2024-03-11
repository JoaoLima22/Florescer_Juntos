package com.example.florescer_juntos.Controler;

import com.example.florescer_juntos.Model.Comentario;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ComentarioDAO {
    private Comentario comentario;

    public ComentarioDAO() {
    }
    public ComentarioDAO(Comentario comentario) {
        this.comentario = comentario;
    }

    public Boolean save(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("coments");
        HashMap<String, Object> map = new HashMap<>();
        map.put("idPost", comentario.getIdPost());
        map.put("idUser", comentario.getIdUsuario());
        map.put("mailUser", comentario.getEmailUsuario());
        map.put("text", comentario.getTexto());
        map.put("typeUser", comentario.getTipoUsuario());

        // Salvo no banco de dados
        databaseReference.child(databaseReference.push().getKey()).setValue(map);
        return true;
    }


    public Comentario getComentario() {
        return comentario;
    }

    public void setComentario(Comentario comentario) {
        this.comentario = comentario;
    }
}
