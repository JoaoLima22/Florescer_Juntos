package com.example.florescer_juntos.View;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarPerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarPerfilFragment extends Fragment {
    EditText edtNome, edtTelefone, edtDesc, edtEmail, edtSenha, edtSenhaCon;
    Button btnConfirmar, btnCancelar;
    TextView tvEmail, tvSenha, tvSenhaCon;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditarPerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarPerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarPerfilFragment newInstance(String param1, String param2) {
        EditarPerfilFragment fragment = new EditarPerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_editar_perfil, container, false);

        // Instancio o que preciso
        edtNome = rootView.findViewById(R.id.edtNomeEdtPerfil);
        edtTelefone = rootView.findViewById(R.id.edtTelefoneEdtPerfil);
        edtDesc = rootView.findViewById(R.id.edtDescEdtPerfil);
        edtEmail = rootView.findViewById(R.id.edtEmailEdtPerfil);
        edtSenha = rootView.findViewById(R.id.edtSenhaEdtPerfil);
        edtSenhaCon = rootView.findViewById(R.id.edtSenhaConEdtPerfil);
        tvEmail = rootView.findViewById(R.id.tvEmail);
        tvSenha = rootView.findViewById(R.id.tvSenha);
        tvSenhaCon = rootView.findViewById(R.id.tvSenhaCon);
        btnConfirmar = rootView.findViewById(R.id.btnEditarPerfil);
        btnCancelar = rootView.findViewById(R.id.btnCancelarPerfil);
        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);

        // Valido esses campos
        edtSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String senha = s.toString().trim();
                if (senha.length()<8) { // Vejo se possui 8 digitos
                    edtSenha.setError("Digite pelo menos 8 digitos!");
                }
            }
        });
        edtSenhaCon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String senhaCon = s.toString().trim();
                if (senhaCon.length()<8) { // Vejo se possui 8 digitos
                    edtSenhaCon.setError("Digite pelo menos 8 digitos!");
                }
                if (!edtSenhaCon.getText().toString().equals(edtSenha.getText().toString())) { // Vejo se são iguais
                    edtSenhaCon.setError("Senhas diferentes!");
                }
            }
        });
        edtNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Verifico o email após o usuário modificar-lo
                String nome = s.toString().trim();
                if (nome.equals("")) { // Vejo se é válido
                    edtNome.setError("Preencha este campo!");
                }
            }
        });
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Verifico o email após o usuário modificar-lo
                String email = s.toString().trim();
                if (isEmailValid(email)) { // Vejo se é válido
                    UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
                    usuarioDAO.isSaved(email, FirebaseDatabase.getInstance().getReference("usuarios"), new UsuarioDAO.ExistenceCheckCallback() {
                        @Override
                        public void onResult(boolean exists) {
                            // Se houver conta
                            if (exists && !(email.equals(sp.getString("userLog", "")))) { // Caso já exista uma conta com essa senha...
                                edtEmail.setError("Email já em uso!");
                            }
                        }
                    });
                } else {
                    // Se o email for inválido...
                    edtEmail.setError("Email inválido!");
                }
            }
        });

        // Verifico qual o tipo de usuario logado
        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
        if (user_Google != null) {
            // Se for do google, removo os campos e mostro seus dados
            removerCampos(rootView);
            usuarioDAO.getUsuarioAsync(user_Google.getEmail(), FirebaseDatabase.getInstance().getReference("users"), getActivity(), new UsuarioDAO.UsuarioCallback() {
                @Override
                public void onUsuarioCarregado(Usuario usuario) {
                    if (usuario != null) {
                        edtNome.setText(usuario.getNome());
                        edtTelefone.setText(usuario.getTelefone());
                        edtDesc.setText(usuario.getDescricao());
                    } else {
                        Log.d("Usuario", "Usuário não encontrado");
                    }
                }
            });
        } else {
            // Busco os dados do usuário pelo email logado
            usuarioDAO.getUsuarioAsync(sp.getString("userLog", ""), FirebaseDatabase.getInstance().getReference("usuarios"), getActivity(), new UsuarioDAO.UsuarioCallback() {
                @Override
                public void onUsuarioCarregado(Usuario usuario) {
                    if (usuario != null) {
                        edtNome.setText(usuario.getNome());
                        edtTelefone.setText(usuario.getTelefone());
                        edtDesc.setText(usuario.getDescricao());
                        edtEmail.setText(usuario.getEmail());
                        edtSenha.setText(usuario.getSenha());
                        edtSenhaCon.setText(usuario.getSenha());
                    } else {
                        Log.d("Usuario", "Usuário não encontrado");
                    }
                }
            });
        }
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {replaceFragment(new PerfilFragment());}
        });
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome, mail, pass, passCon, desc, telefone;
                nome = edtNome.getText().toString();
                desc = edtDesc.getText().toString();
                telefone = edtTelefone.getText().toString();

                if (user_Google != null) { // email do google
                    if(nome.equals("")){edtNome.setError("Preencha este campo!");
                    } else {updateUser();}
                } else {
                    mail = edtEmail.getText().toString();
                    pass = edtSenha.getText().toString();
                    passCon = edtSenhaCon.getText().toString();

                    // Verifico se já existe alguma conta com aquele email
                    UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
                    usuarioDAO.isSaved(mail, FirebaseDatabase.getInstance().getReference("usuarios"), new UsuarioDAO.ExistenceCheckCallback() {
                        @Override
                        public void onResult(boolean exists) {
                            // Se houver conta
                            if (exists && !(mail.equals(sp.getString("userLog", "")))) {
                                edtEmail.setError("Email já em uso!");
                                edtEmail.setText("");
                                Toast.makeText(requireContext(), "Email já em uso", Toast.LENGTH_LONG).show();
                            } else {
                                // Se não houver
                                if(nome.equals("")){
                                    edtNome.setError("Preencha este campo!");
                                    Toast.makeText(requireContext(), "Digite seu nome!", Toast.LENGTH_LONG).show();
                                } else if (!isEmailValid(mail)) {
                                    edtEmail.setError("Email inválido!");
                                    edtEmail.setText("");
                                    Toast.makeText(requireContext(), "Email inválido!", Toast.LENGTH_LONG).show();
                                } else if (pass.length()<8) {
                                    edtSenha.setError("Digite pelo menos 8 digitos!");
                                    edtSenha.setText("");
                                    edtSenhaCon.setText("");
                                    Toast.makeText(requireContext(), "Senha inválida!", Toast.LENGTH_LONG).show();
                                } else if(!pass.equals(passCon)){
                                    // Testo senhas diferentes
                                    edtSenha.setError("Senhas diferentes!");
                                    edtSenhaCon.setError("Senhas diferentes!");
                                    edtSenha.setText("");
                                    edtSenhaCon.setText("");
                                    Toast.makeText(requireContext(), "Senhas diferentes!", Toast.LENGTH_LONG).show();
                                } else {
                                    updateUser();
                                }
                            }
                        }
                    });
                }
            }
        });
        return rootView;
    }

    // Funcção que verifica o email
    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Método que esconde os campos para usuários do google
    private void removerCampos(View rootView){
        // Ajusto os elementos que ficariam bugados
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) edtTelefone.getLayoutParams();
        layoutParams.topToBottom = R.id.edtNomeEdtPerfil;
        edtTelefone.setLayoutParams(layoutParams);

        // Removo os elementos
        ConstraintLayout constraintLayout = rootView.findViewById(R.id.constraintLayout6);
        constraintLayout.removeView(edtEmail);
        constraintLayout.removeView(tvEmail);
        constraintLayout.removeView(edtSenha);
        constraintLayout.removeView(tvSenha);
        constraintLayout.removeView(edtSenhaCon);
        constraintLayout.removeView(tvSenhaCon);
    }

    // Função que altera o fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void updateUser(){
        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        String emailUsuario = "";
        String reference = "";

        // Verifico qual tipo de usuario
        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
        if (user_Google != null) {
            emailUsuario = user_Google.getEmail();
            reference = "users";
        } else {
            // Busco os dados do usuário pelo email logado
            SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
            emailUsuario = sp.getString("userLog", "");
            reference = "usuarios";
        }

        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {
                    if (isAdded()) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("name", edtNome.getText().toString());
                        updates.put("phone", edtTelefone.getText().toString());
                        updates.put("desc", edtDesc.getText().toString());
                        updates.put("photo", usuario.getImageUrl());

                        UsuarioDAO userDao = new UsuarioDAO(usuario);
                        if (user_Google != null) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                            databaseReference.child(user_Google.getUid()).updateChildren(updates)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("Firebase", "Usuário atualizado com sucesso");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("Firebase", "Erro ao atualizar usuário", e);
                                        }
                                    });
                            replaceFragment(new PerfilFragment());
                        } else {
                            updates.put("mail", edtEmail.getText().toString());
                            updates.put("password", edtSenha.getText().toString());
                            userDao.updateUsuario(usuario.getId(), updates, FirebaseDatabase.getInstance().getReference("usuarios"));
                            SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.clear();
                            editor.putString("userLog", edtEmail.getText().toString());
                            editor.commit();
                            replaceFragment(new PerfilFragment());
                        }
                    }
                } else {
                    Log.d("Usuario", "Usuário não encontrado");
                }
            }
        });
    }
}