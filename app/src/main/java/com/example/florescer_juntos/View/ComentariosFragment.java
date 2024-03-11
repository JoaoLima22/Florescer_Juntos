package com.example.florescer_juntos.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.florescer_juntos.ComentarioAdapter;
import com.example.florescer_juntos.Controler.ComentarioDAO;
import com.example.florescer_juntos.ImageAdapter;
import com.example.florescer_juntos.Model.Comentario;
import com.example.florescer_juntos.Model.Post;
import com.example.florescer_juntos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComentariosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComentariosFragment extends Fragment implements ComentarioAdapter.OnItemClickListener{
    TextView tvEmail, tvDesc, tvTipo;
    ImageView imagemView;
    EditText edtComentar;
    Button btnComentar;
    private RecyclerView mRecyclerView;
    private ComentarioAdapter mAdapter;
    private List<Comentario> mComentarios;
    private ProgressBar mProgressCircle;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComentariosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComentariosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComentariosFragment newInstance(String param1, String param2) {
        ComentariosFragment fragment = new ComentariosFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_comentarios, container, false);

        tvEmail = rootView.findViewById(R.id.NomeComentatio);
        tvDesc = rootView.findViewById(R.id.DescComentario);
        tvTipo = rootView.findViewById(R.id.TipoComentario);
        imagemView = rootView.findViewById(R.id.ImageComentario);
        edtComentar = rootView.findViewById(R.id.editTextComentar);
        btnComentar = rootView.findViewById(R.id.btnComentar);

        mProgressCircle = rootView.findViewById(R.id.progress_circle_comentario);
        mRecyclerView = rootView.findViewById(R.id.postsViewComentario);

        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mComentarios = new ArrayList<>();
        mAdapter = new ComentarioAdapter(requireContext(), mComentarios, sp.getString("id_user", ""));
        mAdapter.setOnItemClickListener(ComentariosFragment.this);
        mRecyclerView.setAdapter(mAdapter);

        String id_post = sp.getString("id_post", "");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(id_post);
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Post post = new Post();
                        post.setId(snapshot.getKey());
                        post.setDescricao(snapshot.child("desc").getValue(String.class));
                        post.setDataHora(snapshot.child("dateTime").getValue(String.class));
                        post.setImageUrl(snapshot.child("image").getValue(String.class));
                        post.setTipoPlanta(snapshot.child("type").getValue(String.class));
                        post.setTipoUsuario(snapshot.child("typeUser").getValue(String.class));
                        post.setIdUsuario(snapshot.child("userId").getValue(String.class));
                        post.setEmailUsuario(snapshot.child("mailUser").getValue(String.class));

                        if (isAdded()) {
                            Context context = requireContext();
                            Glide.with(context)
                                    .load(post.getImageUrl())
                                    .into(imagemView);
                            tvEmail.setText(post.getEmailUsuario());
                            tvDesc.setText(post.getDescricao());
                            String tipo = "Tipo: " + post.getTipoPlanta();
                            tvTipo.setText(tipo);
                        }

                        DatabaseReference comentariosRef = FirebaseDatabase.getInstance().getReference("coments");
                        comentariosRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DataSnapshot snapshot = task.getResult();
                                    mComentarios.clear(); // Limpar a lista atual de comentários
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        // Buscar os comentários
                                        Comentario coment = new Comentario();
                                        coment.setId(dataSnapshot.getKey());
                                        coment.setIdUsuario(dataSnapshot.child("idUser").getValue(String.class));
                                        coment.setIdPost(dataSnapshot.child("idPost").getValue(String.class));
                                        coment.setEmailUsuario(dataSnapshot.child("mailUser").getValue(String.class));
                                        coment.setTipoUsuario(dataSnapshot.child("typeUser").getValue(String.class));
                                        coment.setTexto(dataSnapshot.child("text").getValue(String.class));

                                        if (coment.getIdPost().equals(sp.getString("id_post", ""))){
                                            mComentarios.add(coment);
                                        }
                                    }

                                    // Atualizar a RecyclerView
                                    // Inverte os comentarios, (mais novo primeiro)
                                    Collections.reverse(mComentarios);
                                    mAdapter.notifyDataSetChanged();
                                    if (mComentarios.size()>0){
                                        mProgressCircle.setVisibility(View.INVISIBLE);
                                    }

                                } else {
                                    // Tratar erro
                                    mProgressCircle.setVisibility(View.VISIBLE);
                                    mComentarios.clear();
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    } else {
                        // O post não foi encontrado
                    }
                } else {
                    // Ocorreu um erro ao buscar o post
                }
            }
        });

        btnComentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtComentar.getText().toString().equals("")){
                    edtComentar.setError("Digite algo!");
                } else {
                    String text = edtComentar.getText().toString();

                    Comentario coment = new Comentario();
                    coment.setTexto(text);
                    coment.setIdPost(sp.getString("id_post", ""));
                    coment.setIdUsuario(sp.getString("id_user", ""));
                    coment.setEmailUsuario(sp.getString("email_user", ""));
                    coment.setTipoUsuario(sp.getString("tipo_user", ""));

                    ComentarioDAO comentarioDAO = new ComentarioDAO(coment);

                    if(comentarioDAO.save()){
                        Toast.makeText(requireContext(), "Comentário salvo", Toast.LENGTH_SHORT).show();
                        replaceFragment(new ComentariosFragment());
                    }
                }
            }
        });

        return rootView;
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

    @Override
    public void onDeleteComentarioClick(int position) {
        //Deletar comentario aqui
        Comentario selectedItem = mComentarios.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("coments").child(selectedItem.getId());
        databaseReference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Comentário deletado com sucesso
                        Toast.makeText(requireContext(), "Deletado", Toast.LENGTH_SHORT).show();
                        replaceFragment(new ComentariosFragment());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Falha ao deletar o comentário
                        Toast.makeText(requireContext(), "Erro ao tentar deletar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}