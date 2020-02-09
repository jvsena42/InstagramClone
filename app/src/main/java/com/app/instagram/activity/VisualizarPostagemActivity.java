package com.app.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.instagram.R;
import com.app.instagram.model.Postagem;
import com.app.instagram.model.Usuario;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private TextView textPerfilpostagem, textQtdCurtidasPostagens, textDescricaoPostagem;
    private ImageView imagePostagemSelecionada;
    private CircleImageView imagePerfilPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        //inicializar os componentes
        inicializarComponentes();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar Postagem");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Recuperar dados da activity
        Bundle bundle = getIntent().getExtras();
        if ( bundle!= null){

            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            //Exibir os dados do usuario
            Uri uriUsuario = Uri.parse(usuario.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this).load(uriUsuario).into(imagePerfilPostagem);
            textPerfilpostagem.setText(usuario.getNome());

            Uri uriPostagem = Uri.parse(postagem.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this).load(uriPostagem).into(imagePostagemSelecionada);
            textDescricaoPostagem.setText(postagem.getDescricao());
        }
    }

    private void inicializarComponentes(){
        textPerfilpostagem = findViewById(R.id.textPerfilPostagem);
        textQtdCurtidasPostagens = findViewById(R.id.textQtdCurtidas);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);
        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
        imagePerfilPostagem = findViewById(R.id.imagePerfilPostagem);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
