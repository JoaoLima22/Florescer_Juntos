package com.example.florescer_juntos.View;

import androidx.annotation.NonNull;
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
import com.example.florescer_juntos.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Cadastro extends AppCompatActivity {
    TextView tvlogin;
    Button btnContinuar;
    EditText txtEmail, txtNome, txtSenha, txtSenhaCon;
    DatabaseReference databaseReference;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        // Instancio o que preciso
        tvlogin = findViewById(R.id.tvLogin);
        btnContinuar = findViewById(R.id.btnContinuar);
        txtNome = findViewById(R.id.edtNome);
        txtEmail = findViewById(R.id.edtEmail);
        txtSenha = findViewById(R.id.edtSenha);
        txtSenhaCon = findViewById(R.id.edtSenhaConfirmar);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        sp = getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Cadastro.this, Login.class);
                startActivity(it);
                finish();
            }
        });
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user, mail, pass, passCon;
                user = txtNome.getText().toString();
                mail = txtEmail.getText().toString();
                pass = txtSenha.getText().toString();
                passCon = txtSenhaCon.getText().toString();

                //Testo campos vazios
                if(user.equals("")){txtNome.setError("Preencha este campo!");}
                if(mail.equals("")){txtEmail.setError("Preencha este campo!");}
                if(pass.equals("")){txtSenha.setError("Preencha este campo!");}
                if(passCon.equals("")){txtSenhaCon.setError("Preencha este campo!");}

                if (user.equals("") || mail.equals("") || pass.equals("") || passCon.equals("")){
                    Toast.makeText(Cadastro.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    // Verifico se já existe alguma conta com aquele email
                    isSaved(mail, new Login.ExistenceCheckCallback() {
                        @Override
                        public void onResult(boolean exists) {
                            // Se houver conta
                            if (exists) {
                                txtEmail.setError("Email já em uso!");
                                txtEmail.setText("");
                                Toast.makeText(Cadastro.this, "Email já em uso", Toast.LENGTH_SHORT).show();
                            } else {
                                // Se não houver
                                if(!pass.equals(passCon)){
                                    // Testo senhas diferentes
                                    txtSenha.setError("Senhas diferentes!");
                                    txtSenhaCon.setError("Senhas diferentes!");
                                    txtSenha.setText("");
                                    txtSenhaCon.setText("");
                                    Toast.makeText(Cadastro.this, "Senhas diferentes!", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Salvo os dados e mando pra próxima tela
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("nome", user);
                                    editor.putString("email", mail);
                                    editor.putString("senha", pass);
                                    editor.commit();

                                    Intent intent = new Intent(Cadastro.this, Cadastro2.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    // Função que verifica se há um usuário logado pelo email
    public void isSaved(String email, Login.ExistenceCheckCallback callback) {
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
}