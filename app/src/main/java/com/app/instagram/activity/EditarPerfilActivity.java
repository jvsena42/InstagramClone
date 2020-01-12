package com.app.instagram.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.instagram.R;
import com.app.instagram.helper.ConfiguracaoFirebase;
import com.app.instagram.helper.Permissao;
import com.app.instagram.helper.UsuarioFirebase;
import com.app.instagram.model.Usuario;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private EditText editNomePerfil, editEmailPerfil;
    private Button buttonSalvarAlteracoes;
    private Usuario usuarioLogado;
    private StorageReference storageRef;
    private static final int SELECAO_GALERIA = 200;
    private String identificadorUsuario;

    private String[] permissoesNecessarias = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdUsuario();

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Instagram");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Validar permissões
        Permissao.validarPermissoes(permissoesNecessarias,this,1);

        //Inicializar componentes
        inicializarCompotentes();

        //Recuperar dados do usuário
        final FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editEmailPerfil.setText(usuarioPerfil.getEmail());
        editNomePerfil.setText(usuarioPerfil.getDisplayName().toLowerCase());

        Uri url = usuarioPerfil.getPhotoUrl();
        if (url != null){
            Glide.with(EditarPerfilActivity.this).load(url).into(imageEditarPerfil);
        }else {
            imageEditarPerfil.setImageResource(R.drawable.avatar);
        }

        //Salvar alterações no nome
        buttonSalvarAlteracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeAtualizado = editNomePerfil.getText().toString();

                //Atualizar nome do perfil no firebaseUser
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                //Atualizar nome no banco de dados
                usuarioLogado.setNome(nomeAtualizado);
                usuarioLogado.atualizar();

                Toast.makeText(EditarPerfilActivity.this,"Dados alterados com sucesso",Toast.LENGTH_SHORT).show();
            }
        });

        //Alterar foto do usuario
        textAlterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                //Selecionar apenas da galeria
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),localImagemSelecionada);
                        break;
                }

                if (imagem != null){
                    //Configura imagem na tela do usuario
                    imageEditarPerfil.setImageBitmap(imagem);

                    //Recuperar imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem para o firebase Storage
                    StorageReference imageRef = storageRef.child("imagens").child("perfil").child(identificadorUsuario + ".jpeg");
                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditarPerfilActivity.this,"Erro ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Recuperar local da foto
                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uri.isComplete());
                            Uri url = uri.getResult();

                            atualizarFotoUsuario(url);

                            Toast.makeText(EditarPerfilActivity.this,"Sucesso ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void atualizarFotoUsuario(Uri url){

        //Atualizar foto no Firebase User
        UsuarioFirebase.atualizarFotoUsuario(url);

        //Atualizar foto no firebase
        usuarioLogado.setCaminhofoto(url.toString());
        usuarioLogado.atualizar();

        Toast.makeText(EditarPerfilActivity.this,"Sua foto foi alterada",Toast.LENGTH_SHORT).show();
    }

    public void inicializarCompotentes(){
        imageEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editNomePerfil = findViewById(R.id.editNomeEditarPerfil);
        editEmailPerfil = findViewById(R.id.editEmailEditarPerfil);
        buttonSalvarAlteracoes = findViewById(R.id.buttonAcaoPerfil);
        editEmailPerfil.setFocusable(false);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}
