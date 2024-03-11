package com.example.florescer_juntos.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerPerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerPerfilFragment extends Fragment implements ImageAdapter.OnItemClickListener {
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
    private MainActivity mainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Busco a activity para alterar o botão do menu depois
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    public VerPerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerPerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerPerfilFragment newInstance(String param1, String param2) {
        VerPerfilFragment fragment = new VerPerfilFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ver_perfil, container, false);

        // Instancio o que preciso
        tvNome = rootView.findViewById(R.id.tvNomeVer);
        tvEmail = rootView.findViewById(R.id.tvEmailVer);
        tvTelefone = rootView.findViewById(R.id.tvTelefoneVer);
        tvDescricao = rootView.findViewById(R.id.tvDescricaoVer);
        tvImagem = rootView.findViewById(R.id.tvImagemVer);
        imageView = rootView.findViewById(R.id.imagemVerPerfil);
        btnLogout = rootView.findViewById(R.id.btnLogoutVer);
        btnEditarPerfil = rootView.findViewById(R.id.btnEditarPerfilVer);
        btnDelete = rootView.findViewById(R.id.btnDeleteVer);
        sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);

        mProgressCircle = rootView.findViewById(R.id.progress_circle_perfil_ver);
        mRecyclerView = rootView.findViewById(R.id.postsViewPerfilVer);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mPosts = new ArrayList<>();


        //Escondo os botões
        btnDelete.setVisibility(View.INVISIBLE);
        btnEditarPerfil.setVisibility(View.INVISIBLE);

        // Removo os elementos
        ConstraintLayout constraintLayout = rootView.findViewById(R.id.constraintLayout);
        constraintLayout.removeView(tvImagem);
        constraintLayout = rootView.findViewById(R.id.constraintLayoutBtns);
        constraintLayout.removeView(btnLogout);


        //pegar o usuario loggado

        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        String emailUsuario = "";
        String referenceLog = "";

        // Verifico qual tipo de usuario
        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
        if (user_Google != null) {
            emailUsuario = user_Google.getEmail();
            referenceLog = "users";
        } else {
            sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
            emailUsuario = sp.getString("userLog", "");
            referenceLog = "usuarios";
        }

        // Busco o usuario e mostro seus dados
        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(referenceLog), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {
                    mAdapter = new ImageAdapter(requireContext(), mPosts, usuario.getId());

                    if(usuario.getId().equals("-NscKymtcrxWxhqItHyj")){
                        btnDelete.setVisibility(View.VISIBLE);
                    }

                    String idUser, tipoUser, reference = "";
                    idUser = sp.getString("id_user_ver", "");
                    tipoUser = sp.getString("tipo_user_ver", "");

                    //Vejo qual tipo de usuario antes de buscar
                    if (tipoUser.equals("Google")) {
                        reference = "users";
                    } else if(tipoUser.equals("Common")){
                        reference = "usuarios";
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference(reference).child(idUser);
                    databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot snapshot = task.getResult();
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    //Busco o usuario e seus dados
                                    Usuario user = new Usuario();
                                    user.setId(dataSnapshot.getKey());
                                    String nome = snapshot.child("name").getValue(String.class);
                                    user.setNome(nome != null ? nome : ""); //Caso algum campo venha vazio
                                    String email = snapshot.child("mail").getValue(String.class);
                                    user.setEmail(email != null ? email : "");
                                    String telefone = snapshot.child("phone").getValue(String.class);
                                    user.setTelefone(telefone != null ? telefone : "");
                                    String descricao = snapshot.child("desc").getValue(String.class);
                                    user.setDescricao(descricao != null ? descricao : "");
                                    String imageUrl = snapshot.child("photo").getValue(String.class);
                                    user.setImageUrl(imageUrl != null ? imageUrl : Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.drawable.perfil_image).toString());

                                    //Mostro esses dados
                                    if (isAdded()) {
                                        Context context = requireContext();
                                        Glide.with(context)
                                                .load(user.getImageUrl())
                                                .into(imageView);
                                        tvNome.setText(user.getNome());
                                        tvEmail.setText(user.getEmail());
                                        tvTelefone.setText(user.getTelefone());
                                        tvDescricao.setText(user.getDescricao());

                                        //Carrego os posts do usuario
                                        mAdapter = new ImageAdapter(requireContext(), mPosts, usuario.getId());
                                        mRecyclerView.setAdapter(mAdapter);
                                        mAdapter.setOnItemClickListener(VerPerfilFragment.this);
                                        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
                                        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @SuppressLint("NotifyDataSetChanged")
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DataSnapshot snapshot = task.getResult();
                                                    mPosts.clear(); // Limpar a lista atual de posts
                                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                        Post post = new Post();
                                                        post.setId(dataSnapshot.getKey());
                                                        post.setDescricao(dataSnapshot.child("desc").getValue(String.class));
                                                        post.setDataHora(dataSnapshot.child("dateTime").getValue(String.class));
                                                        post.setImageUrl(dataSnapshot.child("image").getValue(String.class));
                                                        post.setTipoPlanta(dataSnapshot.child("type").getValue(String.class));
                                                        post.setTipoUsuario(dataSnapshot.child("typeUser").getValue(String.class));
                                                        post.setIdUsuario(dataSnapshot.child("userId").getValue(String.class));
                                                        post.setEmailUsuario(dataSnapshot.child("mailUser").getValue(String.class));

                                                        if (post.getIdUsuario().equals(idUser)){
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
                                }
                            } else {
                                Toast.makeText(requireContext(), "Erro ao buscar dados", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
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

                        String idUser, tipoUser, emailUser, reference = "";
                        idUser = sp.getString("id_user_ver", "");
                        tipoUser = sp.getString("tipo_user_ver", "");
                        emailUser = sp.getString("email_user_ver", "");

                        if (tipoUser.equals("Google")) {
                            reference = "users";
                        } else if(tipoUser.equals("Common")){
                            reference = "usuarios";
                        }

                        // Busco o usuario para apagar sua imagem
                        usuarioDAO.getUsuarioAsync(emailUser, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
                            @Override
                            public void onUsuarioCarregado(Usuario usuario) {
                                if (usuario != null) {
                                    if (isAdded()) {
                                        Context context = requireContext();
                                        if (!usuario.getImageUrl().equals(("android.resource://" + context.getPackageName() + "/" + R.drawable.perfil_image).toString())) {
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
                                    }

                                } else {
                                    Log.d("Usuario", "Usuário não encontrado");
                                }
                            }
                        });
                        if (tipoUser.equals("Google")) {
                            usuarioDAO.deleteUsuarioByEmail(emailUser, FirebaseDatabase.getInstance().getReference("users"));
                            replaceFragment(new HomeFragment());
                            mainActivity.changeSelectedItem(R.id.btnHome);
                        } else if(tipoUser.equals("Common")){
                            usuarioDAO.deleteUsuarioByEmail(emailUser, FirebaseDatabase.getInstance().getReference("usuarios"));}
                            replaceFragment(new HomeFragment());
                            mainActivity.changeSelectedItem(R.id.btnHome);
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
    public void onComentarClick(int position) {
        if (isAdded()) {
            Post selectedItem = mPosts.get(position);
            SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("id_post", selectedItem.getId());


            UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
            String emailUsuario = "";
            String reference = "";

            // Verifico qual tipo de usuario
            FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
            if (user_Google != null) {
                emailUsuario = user_Google.getEmail();
                reference = "users";
                editor.putString("tipo_user", "Google");
            } else {
                emailUsuario = sp.getString("userLog", "");
                reference = "usuarios";
                editor.putString("tipo_user", "Common");
            }

            // Busco o usuario e mostro seus dados
            usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
                @Override
                public void onUsuarioCarregado(Usuario usuario) {
                    if (usuario != null) {
                        editor.putString("id_user", usuario.getId());
                        editor.putString("email_user", usuario.getEmail());
                        editor.commit();
                        replaceFragment(new ComentariosFragment());
                    } else {
                        Log.d("Usuario", "Usuário não encontrado");
                    }
                }
            });
        }
    }

    @Override
    public void onDeleteClick(int position) {
        Post selectedItem = mPosts.get(position);
        final String selectedKey = selectedItem.getId();
        // Deleto a imagem no storage
        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("FirebaseStorage", "Arquivo excluído com sucesso");
                // Deleto os dados do post do database
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(requireContext(), "Post deletado", Toast.LENGTH_SHORT).show();
                replaceFragment(new VerPerfilFragment());
            }
        });
    }

    @Override
    public void onDeleteOffClick(int position) {

    }

    // Função que altera o fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}