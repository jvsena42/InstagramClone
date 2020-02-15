package com.app.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.instagram.R;
import com.app.instagram.helper.UsuarioFirebase;
import com.app.instagram.model.Comentario;
import com.app.instagram.model.Usuario;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;
    private String idPostagem;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Inicializar componentes
        editComentario = findViewById(R.id.editComentario);

        //Configuracoes iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Recuperar id da postagem
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bundle.getString("idPostagem");
        }

    }

    public void salvarComentario(View view) {

        String textoComentario = editComentario.getText().toString();
        if (!textoComentario.isEmpty()) {

            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getId());
            comentario.setNomeUsuario(usuario.getNome());
            comentario.setCaminhoFoto(usuario.getCaminhoFoto());
            comentario.setComentario(textoComentario);
            if (comentario.salvar()){
                Toast.makeText(getApplicationContext(),"Comentario salvo com sucesso!",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Erro ao salvar comentário!",Toast.LENGTH_SHORT).show();
            }

            Toast.makeText(getApplicationContext(), "Comentário salvo com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Digite um comentário antes de salvar!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}
