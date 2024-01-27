package com.example.florescer_juntos.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Cadastro2 extends AppCompatActivity{
    private Button btnCadastro, btnImagem;
    private EditText edtTelefone, edtDescricao;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Uri imageUri;
    private int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private SharedPreferences sp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro2);

        // Instancio o que preciso
        btnCadastro = findViewById(R.id.btnCadastro);
        btnImagem = findViewById(R.id.btnImagem);
        edtTelefone = findViewById(R.id.edtTelefone);
        edtDescricao = findViewById(R.id.edtDesc);
        imageView = findViewById(R.id.imagemCadastro);
        progressBar = findViewById(R.id.progressBar);

        storageReference = FirebaseStorage.getInstance().getReference("usuarios");
        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        // Pego os dados da activity anterior
        sp = getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        String email = sp.getString("email", "");
        String nome = sp.getString("nome", "");
        String senha = sp.getString("senha", "");
        Usuario user = new Usuario(nome, email, senha, "", "", "");

        btnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {escolherImagem();}
        });
        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setTelefone(String.valueOf(edtTelefone.getText()));
                user.setDescricao(String.valueOf(edtDescricao.getText()));
                salvarUsuario(user);
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
        btnCadastro.setEnabled(false); // Para não clicarem mais de uma vez no botão
        if(imageUri != null){ // Se houver imagem eu salvo ela no cloud
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
                                    UsuarioDAO userDao = new UsuarioDAO(user);

                                    // Salvo o usuário
                                    userDao.save();

                                    // Limpo o shared e salvo o email do usuário logado
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.clear();
                                    editor.putString("userLog", user.getEmail());
                                    editor.commit();

                                    startActivity(new Intent(Cadastro2.this, MainActivity.class));
                                    btnCadastro.setEnabled(true);
                                    finish();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Cadastro2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            btnCadastro.setEnabled(true);
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
            Toast.makeText(this, "Selecione uma imagem de perfil!", Toast.LENGTH_SHORT).show();
        }
    }
}