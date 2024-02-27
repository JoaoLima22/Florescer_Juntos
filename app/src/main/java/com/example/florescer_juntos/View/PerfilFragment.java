package com.example.florescer_juntos.View;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {
    TextView tvNome, tvEmail, tvTelefone, tvDescricao, tvImagem;
    ImageView imageView;
    Button btnLogout, btnEditarPerfil, btnDelete;
    SharedPreferences sp;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Instancio o que preciso
        tvNome = rootView.findViewById(R.id.tvNome);
        tvEmail = rootView.findViewById(R.id.tvEmail);
        tvTelefone = rootView.findViewById(R.id.tvTelefone);
        tvDescricao = rootView.findViewById(R.id.tvDescricao);
        tvImagem = rootView.findViewById(R.id.tvImagem);
        imageView = rootView.findViewById(R.id.imagemPerfil);
        btnLogout = rootView.findViewById(R.id.btnLogout);
        btnEditarPerfil = rootView.findViewById(R.id.btnEditarPerfil);
        btnDelete = rootView.findViewById(R.id.btnDelete);
        sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);

        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        String emailUsuario = "";
        String reference = "";

        // Verifico qual tipo de usuario
        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
        if (user_Google != null) {
            emailUsuario = user_Google.getEmail();
            reference = "users";
        } else {
            sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
            emailUsuario = sp.getString("userLog", "");
            reference = "usuarios";
        }

        // Busco o usuario e mostro seus dados
        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {
                    if (isAdded()) {
                        Context context = requireContext();
                        Glide.with(context)
                                .load(usuario.getImageUrl())
                                .into(imageView);
                        tvNome.setText(usuario.getNome());
                        tvEmail.setText(usuario.getEmail());
                        tvTelefone.setText(usuario.getTelefone());
                        tvDescricao.setText(usuario.getDescricao());
                    }
                } else {
                    Log.d("Usuario", "Usuário não encontrado");
                }
            }
        });
        tvImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {alterarImagem();}
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {replaceFragment(new EditarPerfilFragment());}
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gero um alerta
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirmação");
                builder.setMessage("Tem certeza que deseja excluir este usuário?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Se confirmar excluo o usuario
                        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
                        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
                        String emailUsuario = "";
                        String reference = "";

                        if (user_Google != null) {
                            emailUsuario = user_Google.getEmail();
                            reference = "users";
                        } else {
                            // Busco os dados do usuário pelo email logado
                            sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
                            emailUsuario = sp.getString("userLog", "");
                            reference = "usuarios";
                        }

                        // Busco o usuario para apagar sua imagem
                        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
                            @Override
                            public void onUsuarioCarregado(Usuario usuario) {
                                if (usuario != null) {
                                    if (!usuario.getImageUrl().equals(("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.perfil_image).toString())) {
                                        String url = usuario.getImageUrl();
                                        // Obtenho a referência do arquivo no Firebase Storage a partir do URL
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                        // Excluo o arquivo
                                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Arquivo excluído com sucesso
                                                Log.d("FirebaseStorage", "Arquivo excluído com sucesso");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Falha ao excluir o arquivo
                                                Log.e("FirebaseStorage", "Erro ao excluir arquivo", e);
                                            }
                                        });
                                    }
                                } else {
                                    Log.d("Usuario", "Usuário não encontrado");
                                }
                            }
                        });

                        // Apago o usuario e dou logout
                        if (user_Google != null) {
                            usuarioDAO.deleteUsuarioByEmail(user_Google.getEmail(), FirebaseDatabase.getInstance().getReference("users"));
                        } else {
                            usuarioDAO.deleteUsuarioByEmail(sp.getString("userLog", ""), FirebaseDatabase.getInstance().getReference("usuarios"));}
                        logout();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // O usuário cancelou, não faz nada
                    }
                });
                builder.show();
            }
        });
        return rootView;
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void alterarImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 111);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null){
            // Pego a imagem selecionada e ativo um progressdialog
            Uri imageUri = data.getData();
            ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.CustomProgressDialog);
            progressDialog.setMessage("Alterando imagem...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    imageView.setImageURI(imageUri);
                }
            }, 4000);

            // Salvo a imagem no banco
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("usuarios");
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "."+ getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Se salvar a imagem, eu pego a Url
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
                                    String emailUsuario = "";
                                    String reference = "";

                                    // Verifico qual tipo de usuario e salvo a imagem nele
                                    FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user_Google != null) {
                                        emailUsuario = user_Google.getEmail();
                                        reference = "users";
                                    } else {
                                        // Busco os dados do usuário pelo email logado
                                        sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
                                        emailUsuario = sp.getString("userLog", "");
                                        reference = "usuarios";
                                    }
                                    usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
                                        @Override
                                        public void onUsuarioCarregado(Usuario usuario) {
                                            if (usuario != null) {
                                                Map<String, Object> update = new HashMap<>();
                                                update.put("photo", downloadUrl.toString());
                                                DatabaseReference databaseReference;
                                                if (user_Google != null) {
                                                    databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                                    databaseReference.child(user_Google.getUid()).updateChildren(update);
                                                } else {
                                                    databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
                                                    databaseReference.child(usuario.getId()).updateChildren(update);
                                                }

                                                // Deleto a imagem
                                                if (!usuario.getImageUrl().equals(("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.perfil_image).toString())) {
                                                    String url = usuario.getImageUrl();
                                                    // Obtenho a referência do arquivo no Firebase Storage a partir do URL
                                                    StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                                    // Excluo o arquivo
                                                    storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Arquivo excluído com sucesso
                                                            Log.d("FirebaseStorage", "Arquivo excluído com sucesso");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Falha ao excluir o arquivo
                                                            Log.e("FirebaseStorage", "Erro ao excluir arquivo", e);
                                                        }
                                                    });
                                                }
                                            } else {
                                                Log.d("Usuario", "Usuário não encontrado");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
        }
    }

    // Função que da logout no usuario
    public void logout(){
        // Limpo o SP e volto para login
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();

        // Desconecta do Firebase Auth
        FirebaseAuth.getInstance().signOut();
        Context context = getContext();
        if (context != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
            mGoogleSignInClient.signOut();
        }

        startActivity(new Intent(getActivity(), Splash.class));
        getActivity().finish();
    }

    // Função que altera o fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}