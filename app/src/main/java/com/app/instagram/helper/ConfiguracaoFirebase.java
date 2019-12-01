package com.app.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static DatabaseReference firebaseDatabase;
    private static FirebaseAuth firebaseAuth;

    //Retornar a instancia do firebase Auth
    public static FirebaseAuth getReferenciaAutenticacao(){
        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    //Retornar a instancia do firebase Database
    public static DatabaseReference getFirebaseDatabase(){
        if (firebaseDatabase == null){
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        }
        return firebaseDatabase;
    }


}
