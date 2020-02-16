package com.app.instagram.model;

import com.app.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {

    private String nome, email, senha, caminhoFoto, id;
    private int seguidores =0;
    private int seguindo =0;
    private int postagens =0;

    public Usuario() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());
        usuariosRef.setValue(this);
    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        Map objeto = new HashMap();
        objeto.put("/usuarios/" + getId() + "/nome",getNome());
        objeto.put("/usuarios/" + getId() + "/caminhoFoto",getCaminhoFoto());

        firebaseRef.updateChildren(objeto);

        /*DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());
        Map<String,Object> valoresUsuario = converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);*/
    }

    public void atualizarQtdPostagens(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());

        HashMap<String,Object> dados = new HashMap<>();
        dados.put("postagens",getPostagens());

        usuariosRef.updateChildren(dados);
    }

    public Map<String,Object> converterParaMap(){

        HashMap<String,Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email",getEmail());
        usuarioMap.put("nome",getNome());
        usuarioMap.put("id",getId());
        usuarioMap.put("caminhoFoto", getCaminhoFoto());
        usuarioMap.put("seguidores",getSeguidores());
        usuarioMap.put("seguindo",getSeguindo());
        usuarioMap.put("postagens",getPostagens());

        return usuarioMap;
    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toLowerCase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
