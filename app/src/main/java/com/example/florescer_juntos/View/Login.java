package com.example.florescer_juntos.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    Button btnLogin;
    TextView tvSign;
    EditText edtEmail, edtSenha;
    com.google.android.gms.common.SignInButton googleAuth;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;
    DatabaseReference databaseReference;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instancio o que preciso
        tvSign = findViewById(R.id.tvCadastro);
        googleAuth = findViewById(R.id.sign_in_button);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        sp = getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        String email = sp.getString("userLog", "");

        // Para o login do Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();


        googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });
        tvSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Login.this, Cadastro.class);
                startActivity(it);
                finish();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail, pass;
                mail = edtEmail.getText().toString();
                pass = edtSenha.getText().toString();

                // Se os campos estiverem vazios
                if (mail.equals("")) {
                    edtEmail.setError("Preencha este campo!");
                }
                if (pass.equals("")) {
                    edtSenha.setError("Preencha este campo!");
                }
                if (mail.equals("") || pass.equals("")) {
                    // Testo campos vazios
                    Toast.makeText(Login.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    // Verifico se existe uma conta com esse email
                    UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
                    usuarioDAO.isSaved(mail, FirebaseDatabase.getInstance().getReference("usuarios"), new UsuarioDAO.ExistenceCheckCallback() { // Verifico se existe uma conta com o email
                        @Override
                        public void onResult(boolean exists) {
                            if (!exists) {
                                // Se não existir
                                edtEmail.setText("");
                                edtEmail.setError("Email inválido!");
                                edtSenha.setText("");
                            } else {
                                // Se existir busco o usuário
                                usuarioDAO.getUsuarioAsync(mail, FirebaseDatabase.getInstance().getReference("usuarios"), Login.this, new UsuarioDAO.UsuarioCallback() {
                                    @Override
                                    public void onUsuarioCarregado(Usuario usuario) {
                                        if (usuario != null) {
                                            if (!usuario.getSenha().equals(pass)) {
                                                // Se não for
                                                edtSenha.setError("Senha inválida!");
                                                edtSenha.setText("");
                                            } else {
                                                // Se for
                                                Toast.makeText(Login.this, "Logando...", Toast.LENGTH_SHORT).show();
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.clear();
                                                editor.putString("userLog", mail);
                                                editor.commit();

                                                startActivity(new Intent(Login.this, MainActivity.class));
                                                finish();
                                            }
                                        } else {
                                            Log.d("Usuario", "Usuário não encontrado");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
// ------------------ Autenticação do Google ------------------
    private void googleSignIn() {
        Intent it = googleSignInClient.getSignInIntent();
        startActivityForResult(it, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(Login.this, "Logando...", Toast.LENGTH_SHORT).show();
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Salvo os dados/atualizo essas informações automaticamente
                            FirebaseUser user = auth.getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", user.getUid());
                            map.put("name", user.getDisplayName());
                            // Em dúvida se atualizo a foto sempre ou não
                            //map.put("photo", user.getPhotoUrl().toString());
                            map.put("mail", auth.getCurrentUser().getEmail());
                            database.getReference().child("users").child(user.getUid()).updateChildren(map);

                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Algo errado durante a autenticação!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}