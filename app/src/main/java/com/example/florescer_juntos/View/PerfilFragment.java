package com.example.florescer_juntos.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {
    TextView tview;
    ImageView imageView;
    Button btnLogout;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

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
        tview = rootView.findViewById(R.id.tvPostar);
        imageView = rootView.findViewById(R.id.imagemPerfil);
        btnLogout = rootView.findViewById(R.id.btnLogout);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("usuarios");
        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);

        // Busco os dados do usuário pelo email logado
        String email = sp.getString("userLog", "");
        Query usuarios = databaseReference.orderByChild("mail").equalTo(email);
        usuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Usuario user = new Usuario();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Pego os valores dos campos
                        user.setNome(snapshot.child("name").getValue(String.class));
                        user.setEmail(snapshot.child("mail").getValue(String.class));
                        user.setTelefone(snapshot.child("phone").getValue(String.class));
                        user.setSenha(snapshot.child("password").getValue(String.class));
                        user.setImageUrl(snapshot.child("photo").getValue(String.class));
                    }
                    // Uso os dados do usuario
                    if (isAdded()) {
                        Context context = requireContext();
                        Glide.with(context)
                                .load(user.getImageUrl())
                                .into(imageView);
                        tview.setText(user.getNome());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ocorreu um erro durante a leitura dos dados
                Log.e("Firebase", "Erro ao ler dados", databaseError.toException());
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpo o SP e volto para login
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });

        return rootView;
    }
}