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

        // Para o login do Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();

        // Verifico se há usuário logado no google
        if(auth.getCurrentUser() != null){
            Intent it = new Intent(Login.this, MainActivity.class);
            startActivity(it);
            finish();
        }

        // Verifico se há usuário logado pelo banco
        sp = getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        String email = sp.getString("userLog", "");

        isSaved(email, new ExistenceCheckCallback() {
            @Override
            public void onResult(boolean exists) {
                // Se houver
                if (exists) {
                    startActivity(new Intent(Login.this, MainActivity.class));
                    finish();
                } else {
                    // Se não houver
                }
            }
        });

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
                    isSaved(mail, new ExistenceCheckCallback() { // Verifico se existe uma conta com o email
                        @Override
                        public void onResult(boolean exists) {
                            if (!exists) {
                                // Se não existir
                                edtEmail.setText("");
                                edtEmail.setError("Email inválido!");
                                edtSenha.setText("");
                            } else {
                                // Se existir busco o usuário
                                Query usuarios = databaseReference.orderByChild("mail").equalTo(mail);
                                usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            Usuario user = new Usuario();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                user.setSenha(snapshot.child("password").getValue(String.class));
                                            }
                                            // Verifico se a senha é válida
                                            if (!user.getSenha().equals(pass)) {
                                                // Se não for
                                                edtSenha.setError("Senha inválida!");
                                                edtSenha.setText("");
                                            } else {
                                                // Se for
                                                SharedPreferences.Editor editor = sp.edit();
                                                editor.clear();
                                                editor.putString("userLog", mail);
                                                editor.commit();

                                                startActivity(new Intent(Login.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Ocorreu um erro durante a leitura dos dados
                                        Log.e("Firebase", "Erro ao ler dados", databaseError.toException());
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    // Função que verifica se há um usuário logado pelo email
    public void isSaved(String email, ExistenceCheckCallback callback) {
        Query usuarios = databaseReference.orderByChild("mail").equalTo(email);
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                callback.onResult(exists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ocorreu um erro durante a leitura dos dados
                Log.e("Firebase", "Erro ao ler dados", databaseError.toException());
                callback.onResult(false); // Assumindo que um erro significa que não existe
            }
        });
    }

    public interface ExistenceCheckCallback {
        void onResult(boolean exists);
    }

// ------------------ Autenticação do Google ------------------
    private void googleSignIn() {
        Intent it = googleSignInClient.getSignInIntent();
        startActivityForResult(it, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
                            map.put("photo", user.getPhotoUrl().toString());
                            map.put("mail", auth.getCurrentUser().getEmail());

                            database.getReference().child("users").child(user.getUid()).setValue(map);

                            startActivity(new Intent(Login.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Algo errado durante a autenticação!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}