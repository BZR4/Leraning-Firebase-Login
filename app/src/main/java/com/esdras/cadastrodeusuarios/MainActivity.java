package com.esdras.cadastrodeusuarios;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private EditText editTextEmail, editTextPassword;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"Open again");

        //Obter uma instancia de FirebaseAut
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Criar um Listener para FirebaseAuth para responder as mudancas de estado do login
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Log.d(TAG, "Signed user: " + user.getUid().toString());
                }else{
                    Log.d(TAG,"User has logout");
                }
            }
        };

        Button buttonSignIn =(Button)findViewById(R.id.buttonSiginSigup);

        //<editor-fold desc="Sign In - Login no Firebase">
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEmail = (EditText)findViewById(R.id.editTextEmail);
                editTextPassword = (EditText)findViewById(R.id.editTextPassword);
                String email = editTextEmail.getText().toString();
                email.trim();
                String password = editTextPassword.getText().toString();
                password.trim();

                //Validar formulario
                if(!validateForm(editTextEmail, editTextPassword)){
                    return;
                }

                showProgressDialog();

                //Criacao de usuario atraves da referencia de FirebaseAuth

                mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgressDialog();
                        Log.d("isSuccessful: ",String.valueOf(task.isSuccessful()));
                        if(!task.isSuccessful()){
                            hideProgressDialog();
                            Toast.makeText(getApplicationContext(),"Not Logged: "+task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }else{
                            hideProgressDialog();
                            Intent intent = new Intent(MainActivity.this, UserActivity.class);
                            intent.putExtra("title","MainActivity");
                            intent.putExtra("body","MainActivity - Body");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        //</editor-fold>


        //Criacao de novo usuario
        TextView textViewSignUp = (TextView)findViewById(R.id.textViewSignUp);
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEmail = (EditText)findViewById(R.id.editTextEmail);
                editTextPassword = (EditText)findViewById(R.id.editTextPassword);
                String email = editTextEmail.getText().toString();
                email.trim();
                String password = editTextPassword.getText().toString();
                password.trim();

                //Validar formulario
                if(!validateForm(editTextEmail, editTextPassword)){
                    return;
                }

                showProgressDialog();

                //Criacao de usuario atraves da referencia de FirebaseAuth
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onComplete Login: "+task.isSuccessful());

                                //Obter dados do usuario registrado em caso de sucesso
                                if (task.isSuccessful()){
                                    hideProgressDialog();
                                    editTextEmail.setText("");
                                    editTextPassword.setText("");
                                    Log.d(TAG, task.getResult().getUser().toString());
                                }else{
                                    Toast.makeText(getApplicationContext(), "Sign In Failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        if(getIntent().getExtras() != null){
            openNotification();
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Carregando...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public boolean validateForm(EditText editTextEmail, EditText editTextPassword){

        boolean result = true;

        if (TextUtils.isEmpty(editTextEmail.getText().toString())){
            editTextEmail.setError("Email requerido");
            result = false;
//            message("O campo de email nao foi preenchido");

        }else{
            editTextEmail.setError(null);
        }

        if (TextUtils.isEmpty(editTextEmail.getText().toString())){
            editTextEmail.setError("Senha Requerida");
//            message("A senha nao foi preenchida");
            result = false;

        }else{
            editTextEmail.setError(null);
        }

        return result;
    }

    public void message(String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Alerta");
        dialog.setMessage(message);
        dialog.setPositiveButton("Ok",null);
        dialog.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListner);
        if(getIntent().getExtras() != null){
            openNotification();
        }
    }

    @Override
    protected void onResume() {
        super.onPostResume();
        if(getIntent().getExtras() != null){
            openNotification();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListner != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListner);
        }
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
}
