package com.example.florescer_juntos.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarPerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarPerfilFragment extends Fragment {
    EditText edtNome, edtTelefone, edtDesc;
    Button btnConfirmar, btnCancelar, btnImagem;
    ImageView imageView;

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
        btnConfirmar = rootView.findViewById(R.id.btnEditarPerfil);
        btnCancelar = rootView.findViewById(R.id.btnCancelarPerfil);
        btnImagem = rootView.findViewById(R.id.btnImagemEditar);
        imageView = rootView.findViewById(R.id.imagemEditar);

        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        String emailUsuario = "";
        String reference = "";

        // Verifico qual o tipo de usuario logado
        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
        if (user_Google != null) {
            emailUsuario = user_Google.getEmail();
            reference = "users";
        } else {
            // Busco os dados do usuário pelo email logado
            emailUsuario = sp.getString("userLog", "");
            reference = "usuarios";
        }
        // Busco ele e apresento seus dados
        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {
                    if (isAdded()) {
                        Context context = requireContext();
                        Glide.with(context)
                                .load(usuario.getImageUrl())
                                .into(imageView);
                        edtNome.setText(usuario.getNome());
                        edtTelefone.setText(usuario.getTelefone());
                        edtDesc.setText(usuario.getDescricao());
                    }
                } else {
                    Log.d("Usuario", "Usuário não encontrado");
                }
            }
        });
        return rootView;
    }
}