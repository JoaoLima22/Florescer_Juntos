package com.example.florescer_juntos.View;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Cadastro extends AppCompatActivity {
    private TextView redirectLogin;
    private Button btnCadastro, btnImagem;
    private EditText txtEmail, txtNome, txtSenha, txtSenhaCon, txtTelefone, txtDescricao;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private SharedPreferences sp;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private final int PICK_IMAGE_REQUEST = 1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Instancio o que preciso
        //Inputs
        txtNome = findViewById(R.id.edtNomeCadastro);
        txtEmail = findViewById(R.id.edtEmailCadastro);
        txtSenha = findViewById(R.id.edtSenhaCadastro);
        txtSenhaCon = findViewById(R.id.edtSenhaConfirmeCadastro);
        txtTelefone = findViewById(R.id.edtTelefoneCadastro);
        txtDescricao = findViewById(R.id.edtDescricaoCadastro);
        //Botões
        redirectLogin = findViewById(R.id.btnRedirectLogin);
        btnCadastro = findViewById(R.id.btnCadastro);
        btnImagem = findViewById(R.id.btnImagem);
        //Outros
        imageView = findViewById(R.id.imagemCadastro);
        progressBar = findViewById(R.id.progressBar);
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        sp = getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);

        // Busco a imagem padrão
        imageUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.perfil_image);
        imageView.setImageURI(imageUri);

        sp = getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        Usuario user = new Usuario();
        btnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escolherImagem();
            }
        });
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Usuario user = new Usuario();
                user.setNome(String.valueOf(txtNome.getText()));
                user.setDescricao(String.valueOf(txtEmail.getText()));
                user.setTelefone(String.valueOf(txtSenha.getText()));
                user.setDescricao(String.valueOf(txtSenhaCon.getText()));
                user.setTelefone(String.valueOf(txtTelefone.getText()));
                user.setDescricao(String.valueOf(txtDescricao.getText()));
                //img
                salvarUsuario(user);
            }
        });
        // Método que verifica alterações no campo
        txtSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String senha = s.toString().trim();
                if (senha.length()<8) {
                    txtSenha.setError("Digite pelo menos 8 digitos!");
                }
            }
        });
        txtSenhaCon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String senhaCon = s.toString().trim();
                if (senhaCon.length()<8) {
                    txtSenhaCon.setError("Digite pelo menos 8 digitos!");
                }
                if (!txtSenhaCon.getText().toString().equals(txtSenha.getText().toString())) {
                    txtSenhaCon.setError("Senhas diferentes!");
                }
            }
        });

        txtNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String nome = s.toString().trim();
                if (nome.equals("")) {
                    txtNome.setError("Preencha este campo!");
                }
            }
        });
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString().trim();
                if (isEmailValid(email)) {
                    UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
                    usuarioDAO.isSaved(email, FirebaseDatabase.getInstance().getReference("usuarios"), new UsuarioDAO.ExistenceCheckCallback() {
                        @Override
                        public void onResult(boolean exists) {
                            if (exists) { // Caso já exista uma conta com essa senha...
                                txtEmail.setError("Email já em uso!");
                            }
                        }
                    });
                } else {
                    // Se o email for inválido...
                    txtEmail.setError("Email inválido!");
                }
            }
        });

        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Cadastro.this, Login.class);
                startActivity(it);
                finish();
            }
        });
    }


    // Escolhe a imagem e atualiza a imagem da tela
    private void escolherImagem(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    // Função que retorna a extensão do arquivo, pra salvar a url
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void salvarUsuario(Usuario user) {
        String name, mail, pass, passCon;
        name = txtNome.getText().toString();
        mail = txtEmail.getText().toString();
        pass = txtSenha.getText().toString();
        passCon = txtSenhaCon.getText().toString();

        // Testo campos vazios
        if (name.equals("")) {
            txtNome.setError("Preencha este campo!");
        }
        if (mail.equals("")) {
            txtEmail.setError("Preencha este campo!");
        }
        if (pass.equals("")) {
            txtSenha.setError("Preencha este campo!");
        }
        if (passCon.equals("")) {
            txtSenhaCon.setError("Preencha este campo!");
        }

        if (name.equals("") || mail.equals("") || pass.equals("") || passCon.equals("")) {
            Toast.makeText(Cadastro.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        } else {
            // Verifico se já existe alguma conta com aquele email
            UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
            usuarioDAO.isSaved(mail, FirebaseDatabase.getInstance().getReference("usuarios"), new UsuarioDAO.ExistenceCheckCallback() {
                @Override
                public void onResult(boolean exists) {
                    // Se houver conta
                    if (exists) {
                        txtEmail.setError("Email já em uso!");
                        txtEmail.setText("");
                        Toast.makeText(Cadastro.this, "Email já em uso", Toast.LENGTH_SHORT).show();
                    } else if (pass.length() < 8) {
                        txtSenha.setError("Digite pelo menos 8 dígitos!");
                        txtSenha.setText("");
                        txtSenhaCon.setText("");
                        Toast.makeText(Cadastro.this, "Senha inválida!", Toast.LENGTH_LONG).show();
                    } else if (!isEmailValid(mail)) {
                        txtEmail.setError("Email inválido!");
                        txtEmail.setText("");
                        Toast.makeText(Cadastro.this, "Email inválido!", Toast.LENGTH_LONG).show();
                    } else {
                        // Se não houver
                        if (!pass.equals(passCon)) {
                            // Testo senhas diferentes
                            txtSenha.setError("Senhas diferentes!");
                            txtSenhaCon.setError("Senhas diferentes!");
                            txtSenha.setText("");
                            txtSenhaCon.setText("");
                            Toast.makeText(Cadastro.this, "Senhas diferentes!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (imageUri != null) { // Se houver imagem, eu salvo ela no cloud
                                btnCadastro.setEnabled(false);
                                btnImagem.setEnabled(false); // Para não clicarem mais de uma vez no botão
                                Toast.makeText(Cadastro.this, "Cadastrando...", Toast.LENGTH_SHORT).show();
                                StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "."+ getFileExtension(imageUri));
                                fileReference.putFile(imageUri)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // Se salvar a imagem, eu pego a Url
                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri downloadUrl) {
                                                        // Salvo a url no usuário
                                                        user.setImageUrl(downloadUrl.toString());
                                                        // Salvo o usuário
                                                        UsuarioDAO userDao = new UsuarioDAO(user);
                                                        userDao.save();

                                                        // Limpo o shared e salvo o email do usuário logado
                                                        SharedPreferences.Editor editor = sp.edit();
                                                        editor.clear();
                                                        editor.putString("userLog", user.getEmail());
                                                        editor.commit();

                                                        startActivity(new Intent(Cadastro.this, MainActivity.class));
                                                        btnCadastro.setEnabled(true);
                                                        btnImagem.setEnabled(true);
                                                        finish();
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Toast.makeText(Cadastro2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                btnCadastro.setEnabled(true);
                                                btnImagem.setEnabled(true);
                                            }
                                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                                // Isso é para a barra de progresso funcionar
                                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                                progressBar.setProgress((int) progress);
                                            }
                                        });
                            } else {
                                Toast.makeText(Cadastro.this, "Selecione uma imagem de perfil!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }
    }

    // Funcção que verifica o email
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}