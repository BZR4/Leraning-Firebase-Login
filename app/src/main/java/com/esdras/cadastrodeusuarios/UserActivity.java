package com.esdras.cadastrodeusuarios;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserActivity extends AppCompatActivity {

    private static String TAG = "UserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        setTitle("User Page");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.common_google_signin_btn_icon_dark);
        toolbar.setSubtitle("Subtitulo do usuario");

        //Referencia de FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        //Criar referencia de armazenamento - Ponteiros para nuvem gs://cadastro-de-usuarios-8f288.appspot.com
        StorageReference sorageReference = storage.getReferenceFromUrl("gs://cadastro-de-usuarios-8f288.appspot.com");

        //Criar referencia para um nó filho
        StorageReference imageReference = sorageReference.child("images");

        Log.d(TAG, "Bucket: "+imageReference.getBucket().toString());
        Log.d(TAG, "Image name: "+imageReference.getName().toString());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Titulo descritivo na Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Exibir mensagem", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                message("SnackBar is cool!");
                            }
                        }).show();
            }
        });

        if(getIntent().getExtras() != null){
            openNotification();
        }
    }

    public void message(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Alerta");
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok",null);
        dialog.create();
        dialog.show();
    }

    private void openNotification(){
        String titulo = getIntent().getExtras().getString("title").toString().trim();
        String mensagem = getIntent().getExtras().getString("body").toString().trim();
        AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.NovoAlerta);
        alert.setTitle(titulo);
        alert.setMessage(mensagem);
        alert.setPositiveButton("Ok",null);
        alert.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Toast.makeText(getApplicationContext(),"Menu Item",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
