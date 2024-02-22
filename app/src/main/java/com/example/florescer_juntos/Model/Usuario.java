package com.example.florescer_juntos.Model;

public class Usuario extends Pessoa{
    private String telefone;
    private String descricao;
    private String imageUrl;
    private String id;

    public Usuario() {//
    }

    public Usuario(String nome, String email, String senha, String telefone, String descricao, String imageUrl) {
        super(nome, email, senha);

        this.telefone = telefone;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
