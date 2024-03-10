package com.example.florescer_juntos.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements ImageAdapter.OnItemClickListener {
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private DatabaseReference databaseReference;
    private List<Post> mPosts;
    private ProgressBar mProgressCircle;
    Button btnFiltroTipo;

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

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    @SuppressLint({"MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        // Instancio oq preciso
        btnFiltroTipo = rootView.findViewById(R.id.btnFiltroTipo);
        mProgressCircle = rootView.findViewById(R.id.progress_circle);
        mRecyclerView = rootView.findViewById(R.id.postsView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mPosts = new ArrayList<>();
        mAdapter = new ImageAdapter(requireContext(), mPosts, "");

        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        String emailUsuario = "";
        String reference = "";

        // Verifico qual tipo de usuario e pego o usuario
        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
        if (user_Google != null) {
            emailUsuario = user_Google.getEmail();
            reference = "users";
        } else {
            sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
            emailUsuario = sp.getString("userLog", "");
            reference = "usuarios";
        }
        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {

                    // Seto o adapter com o usuario logado
                    mAdapter = new ImageAdapter(requireContext(), mPosts, usuario.getId());
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(HomeFragment.this);
                    databaseReference = FirebaseDatabase.getInstance().getReference("posts");
                    databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot snapshot = task.getResult();
                                mPosts.clear(); // Limpar a lista atual de posts
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    // Busco os posts
                                    Post post = new Post();
                                    post.setId(dataSnapshot.getKey());
                                    post.setDescricao(dataSnapshot.child("desc").getValue(String.class));
                                    post.setDataHora(dataSnapshot.child("dateTime").getValue(String.class));
                                    post.setImageUrl(dataSnapshot.child("image").getValue(String.class));
                                    post.setTipoPlanta(dataSnapshot.child("type").getValue(String.class));
                                    post.setTipoUsuario(dataSnapshot.child("typeUser").getValue(String.class));
                                    post.setIdUsuario(dataSnapshot.child("userId").getValue(String.class));
                                    post.setEmailUsuario(dataSnapshot.child("mailUser").getValue(String.class));
                                    mPosts.add(post);
                                }

                                // Atualizor a RecyclerView
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
                } else {
                    Log.d("Usuario", "Usuário não encontrado");
                }
            }
        });
        btnFiltroTipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gero o popup do filto
                PopupMenu popupMenu = new PopupMenu(requireContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.filtro_tipos, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.filtroCancelar){
                            replaceFragment(new HomeFragment());
                            return true;
                        } else if (item.getItemId()==R.id.filtroSuculenta) {
                            filterPosts("Suculenta");
                            return true;
                        } else if (item.getItemId()==R.id.filtroFlor) {
                            filterPosts("Flor");
                            return true;
                        } else if (item.getItemId()==R.id.filtroHortaliça) {
                            filterPosts("Hortaliça");
                            return true;
                        } else if (item.getItemId()==R.id.filtroOutro) {
                            filterPosts("Outro");
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return rootView;
    }
    private void filterPosts(String tipo){SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
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
        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {
                    mAdapter = new ImageAdapter(requireContext(), mPosts, usuario.getId());
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(HomeFragment.this);
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
                                    if(post.getTipoPlanta().equals(tipo)){

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
                } else {
                    Log.d("Usuario", "Usuário não encontrado");
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(requireContext(), "Clicou no item de posição: " + position, Toast.LENGTH_SHORT).show();
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
        // Salvo os dados do usuario dono do post
        Post selectedItem = mPosts.get(position);
        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("id_user_ver", selectedItem.getIdUsuario());
        editor.putString("tipo_user_ver", selectedItem.getTipoUsuario());
        editor.commit();

        // Mando pro fragment desejado e altero
        // o botão do menu por conveniencia
        mainActivity.changeSelectedItem(R.id.btnPerfil);
        replaceFragment(new VerPerfilFragment());
    }

    @Override
    public void onEditarClick(int position) {

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
                replaceFragment(new HomeFragment());
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