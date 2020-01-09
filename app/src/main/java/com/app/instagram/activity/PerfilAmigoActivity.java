package com.app.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instagram.R;
import com.app.instagram.helper.ConfiguracaoFirebase;
import com.app.instagram.helper.UsuarioFirebase;
import com.app.instagram.model.Usuario;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioAmigoRef;

    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private String idUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        //Inicializar componentes
        inicializarComponentes();

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Recuperar usuario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Configurar nome do usuário na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            //Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhofoto();

            if (caminhoFoto != null){
                //Alterar foto
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this).load(url).into(imagePerfil);
            }
        }

    }

    private void recuperarDadosUsuarioLogado(){

        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     usuarioLogado = dataSnapshot.getValue(Usuario.class);

                     verificaSegueUsuarioAmigo();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }


    private void verificaSegueUsuarioAmigo(){

        DatabaseReference seguidorRef = seguidoresRef.child(idUsuarioLogado).child(usuarioSelecionado.getId());

        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            //Já está seguindo
                            habilitarBotaoSeguir(true);
                        }else {
                            //Ainda não está seguindo
                            habilitarBotaoSeguir(false);

                            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    //Salvar seguidor
                                    salvarSeguidor(usuarioLogado,usuarioSelecionado);

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    public void salvarSeguidor(Usuario userLogado, Usuario userAmigo){
        /*
        * seguidores
        *   id usuario
        *       id amigo
        *           dados amigo
        * */

        HashMap<String, Object> dadosAmigo = new HashMap<>();
        dadosAmigo.put("nome",userAmigo.getNome());
        dadosAmigo.put("caminhoFoto",userAmigo.getCaminhofoto());

        DatabaseReference seguidorRef = seguidoresRef.child(userLogado.getId())
                                                    .child(userAmigo.getId());
        seguidorRef.setValue(dadosAmigo);

        //Alterar nome para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

    }

    private void habilitarBotaoSeguir(Boolean segueUsuario){
        if (segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
        }else {
            buttonAcaoPerfil.setText("Seguir");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Recupera dados do amigo selecionado
        recuperarDadosPerfilAmigo();

        //Recupera dados do usuário logado
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo(){
        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);

                String postagens = String.valueOf(usuario.getPostagens());
                String seguidores = String.valueOf(usuario.getSeguidores());
                String seguindo = String.valueOf(usuario.getSeguindo());

                //Configurar valores recuperados
                textPublicacoes.setText(postagens);
                textSeguidores.setText(seguidores);
                textSeguindo.setText(seguindo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes(){
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Seguir");
        imagePerfil = findViewById(R.id.imagePerfil);
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
