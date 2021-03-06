package com.app.instagram.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.app.instagram.R;
import com.app.instagram.adapter.AdapterGrid;
import com.app.instagram.helper.ConfiguracaoFirebase;
import com.app.instagram.helper.UsuarioFirebase;
import com.app.instagram.model.Postagem;
import com.app.instagram.model.Usuario;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;

    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;
    private AdapterGrid adapterGrid;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference postagensUsuarioRef;
    private GridView gridViewPerfil;

    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private String idUsuarioLogado;

    private List<Postagem> postagens;


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

            //Configurar referencia poostagens usuario
            postagensUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("postagens").child(usuarioSelecionado.getId());

            //Configurar nome do usuário na toolbar
            getSupportActionBar().setTitle(usuarioSelecionado.getNome());

            //Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();

            if (caminhoFoto != null){
                //Alterar foto
                Uri url = Uri.parse(caminhoFoto);
                Glide.with(PerfilAmigoActivity.this).load(url).into(imagePerfil);
            }
        }

        //Inicializar o image loader
        inicializarImageLoader();

        //Carregar as fotos das postagens de um usuario
        carregarFotosPostagem();

        //Abrir foto clicada
        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get(position);
                Intent i = new Intent(getApplicationContext(),VisualizarPostagemActivity.class);

                i.putExtra("postagem",postagem);
                i.putExtra("usuario",usuarioSelecionado);

                startActivity(i);
            }
        });

    }

    public void inicializarImageLoader(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void carregarFotosPostagem(){

        //Recuperar as fotos postadas pelo usuário
        postagens = new ArrayList<>();
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid/3;
                gridViewPerfil.setColumnWidth(tamanhoImagem);

                List<String> urlFotos = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    Postagem postagem = dataSnapshot.getValue(Postagem.class);
                    postagens.add(postagem);
                    urlFotos.add(postagem.getCaminhoFoto());
                }

                //int qtdPostagem = urlFotos.size();
                //textPublicacoes.setText(String.valueOf(qtdPostagem));

                //Configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(),R.layout.grid_postagem,urlFotos);
                gridViewPerfil.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        DatabaseReference seguidorRef = seguidoresRef.child(usuarioSelecionado.getId()).child(idUsuarioLogado);

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
        *   id amigo
        *       id usuario
        *           dados amigo
        * */

        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome",userLogado.getNome());
        dadosUsuarioLogado.put("caminhoFoto",userLogado.getCaminhoFoto());

        DatabaseReference seguidorRef = seguidoresRef.child(userAmigo.getId())
                                                    .child(userLogado.getId());
        seguidorRef.setValue(dadosUsuarioLogado);

        //Alterar nome para seguindo
        buttonAcaoPerfil.setText("Seguindo");
        buttonAcaoPerfil.setOnClickListener(null);

        //Incrementear seguindo do usuario logado
        int seguindo = userLogado.getSeguindo() + 1;

        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo",seguindo);

        DatabaseReference usuarioSeguindo = usuariosRef.child(userLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);


        //Incrementar seguidores do amigo
        int seguidores = userAmigo.getSeguidores() + 1;

        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores",seguidores);

        DatabaseReference usuarioSeguidores = usuariosRef.child(userAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);
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
                //textPublicacoes.setText(postagens);
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
        gridViewPerfil = findViewById(R.id.gridViewPerfil);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
