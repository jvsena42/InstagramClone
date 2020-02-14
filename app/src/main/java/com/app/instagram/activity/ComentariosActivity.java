package com.app.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.app.instagram.R;

public class ComentariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }
}
