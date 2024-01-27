package com.example.florescer_juntos.Model;

public class Pessoa {
    private String nome;
    private String email;
    private String senha;

    public Pessoa(){

    }
    public Pessoa(String nome,String email, String senha){
        this.nome = nome;
        this.senha = senha;
        this.email = email;
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
