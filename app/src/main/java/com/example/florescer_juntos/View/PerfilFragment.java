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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.florescer_juntos.Controler.PostDAO;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.ImageAdapter;
import com.example.florescer_juntos.Model.Post;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment implements ImageAdapter.OnItemClickListener {
    TextView tvNome, tvEmail, tvTelefone, tvDescricao, tvImagem;
    ImageView imageView;
    Button btnLogout, btnEditarPerfil, btnDelete;
    SharedPreferences sp;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private DatabaseReference databaseReference;
    private List<Post> mPosts;
    private ProgressBar mProgressCircle;

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

        mProgressCircle = rootView.findViewById(R.id.progress_circle_perfil);
        mRecyclerView = rootView.findViewById(R.id.postsViewPerfil);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mPosts = new ArrayList<>();
        mAdapter = new ImageAdapter(requireContext(), mPosts, "");

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

                        //Seto o adapter com o user logado
                        mAdapter = new ImageAdapter(requireContext(), mPosts, usuario.getId());
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(PerfilFragment.this);
                        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
                        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DataSnapshot snapshot = task.getResult();
                                    mPosts.clear(); // Limpar a lista atual de posts
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        // Pego todos os posts
                                        Post post = new Post();
                                        post.setId(dataSnapshot.getKey());
                                        post.setDescricao(dataSnapshot.child("desc").getValue(String.class));
                                        post.setDataHora(dataSnapshot.child("dateTime").getValue(String.class));
                                        post.setImageUrl(dataSnapshot.child("image").getValue(String.class));
                                        post.setTipoPlanta(dataSnapshot.child("type").getValue(String.class));
                                        post.setTipoUsuario(dataSnapshot.child("typeUser").getValue(String.class));
                                        post.setIdUsuario(dataSnapshot.child("userId").getValue(String.class));
                                        post.setEmailUsuario(dataSnapshot.child("mailUser").getValue(String.class));

                                        // Salvo para mostar apenas os posts do usuario
                                        if (post.getIdUsuario().equals(usuario.getId())){
                                            mPosts.add(post);
                                        }
                                    }

                                    // Atualizar a RecyclerView
                                    Collections.reverse(mPosts);
                                    mAdapter.notifyDataSetChanged();
                                    mProgressCircle.setVisibility(View.INVISIBLE);

                                } else {
                                    // Tratar erro
                                    mProgressCircle.setVisibility(View.VISIBLE);
                                    mPosts.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });

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

        startActivity(new Intent(getActivity(), SplashActivity.class));
        getActivity().finish();
    }

    // Função que altera o fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClick(int position) {

    }

    @SuppressLint("CheckResult")
    @Override
    public void onSaveClick(int position) {
        // Salva a imagem localmente
        Post selectedItem = mPosts.get(position);
        PostDAO postDAO = new PostDAO(selectedItem, requireContext());
        if(postDAO.isPostSaved(selectedItem.getId())){
            if(postDAO.updatePost()){
                Toast.makeText(requireContext(), "Post atualizado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Glide.with(requireContext())
                    .load(selectedItem.getImageUrl())
                    .downloadOnly(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            // A imagem foi baixada com sucesso, agora você pode salvar o caminho do arquivo local
                            String imagePath = resource.getAbsolutePath();
                            // Salve o caminho da imagem local no banco de dados ou onde preferir
                            selectedItem.setImageUrl(imagePath);

                            if(postDAO.isPostSaved(selectedItem.getId())){
                                if(postDAO.updatePost()){
                                    Toast.makeText(requireContext(), "Post atualizado", Toast.LENGTH_SHORT).show();
                                }
                            } else{
                                postDAO.setPost(selectedItem);
                                if(postDAO.saveOffline()){
                                    Toast.makeText(requireContext(), "Post salvo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onVerPerfilClick(int position) {

    }

    @Override
    public void onEditarClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {
        Post selectedItem = mPosts.get(position);
        final String selectedKey = selectedItem.getId();

        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("FirebaseStorage", "Arquivo excluído com sucesso");
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(requireContext(), "Post deletado", Toast.LENGTH_SHORT).show();
                replaceFragment(new PerfilFragment());
            }
        });
    }


    @Override
    public void onDeleteOffClick(int position) {

    }
}