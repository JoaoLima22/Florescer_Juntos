package com.example.florescer_juntos.Model;

public class Comentario {
    private String id;
    private String idPost;
    private String idUsuario;
    private String emailUsuario;
    private String tipoUsuario;
    private String texto;

    public Comentario() {
    }

    public Comentario(String id, String idPost, String idUsuario, String emailUsuario, String texto, String tipo) {
        this.id = id;
        this.idPost = idPost;
        this.idUsuario = idUsuario;
        this.emailUsuario = emailUsuario;
        this.texto = texto;
        this.tipoUsuario = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }


    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
