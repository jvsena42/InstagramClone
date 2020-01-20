package com.app.instagram.model;

import com.app.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Postagem {

    /*
    * postagens
    *   idUsuario
    *       idPostagem
    *           descricao
    *           caminhofoto
    *           idUsuario
    * */

    private String id;
    private String idUsuario;
    private String Descricao;
    private String caminhoFoto;

    public Postagem() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagem = postagemRef.push().getKey();
        setId(idPostagem);

    }

    public boolean salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference postagensRef = firebaseRef.child("postagens").child(getIdUsuario()).child(getId());
        postagensRef.setValue(this);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }
}