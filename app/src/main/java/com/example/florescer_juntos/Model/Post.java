package com.example.florescer_juntos.Model;

public class Post {
    private String id;
    private String descricao;
    private String imageUrl;
    private String idUsuario;
    private String tipoPlanta;
    private String tipoUsuario;
    private String dataHora;

    public Post(){
     // Construtor vazio
    }
    public Post(String id, String descricao, String imageUrl, String idUsuario, String dataHora) {
        this.id = id;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
        this.idUsuario = idUsuario;
        this.dataHora = dataHora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String getTipoPlanta() {
        return tipoPlanta;
    }

    public void setTipoPlanta(String tipoPlanta) {
        this.tipoPlanta = tipoPlanta;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
