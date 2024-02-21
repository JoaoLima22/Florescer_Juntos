package com.example.florescer_juntos.View;

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
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditarLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarLoginFragment extends Fragment {
    EditText edtEmail, edtSenha, edtSenhaCon;
    Button btnConfirmar, btnCancelar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditarLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarLoginFragment newInstance(String param1, String param2) {
        EditarLoginFragment fragment = new EditarLoginFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_editar_login, container, false);

        // Instancio o que preciso
        edtEmail = rootView.findViewById(R.id.edtEmailEdtLogin);
        edtSenha = rootView.findViewById(R.id.edtSenhaEdtLogin);
        edtSenhaCon = rootView.findViewById(R.id.edtConSenhaEdtLogin);
        btnCancelar = rootView.findViewById(R.id.btnCancelarLogin);
        btnConfirmar = rootView.findViewById(R.id.btnEditarLogin);

        // Busco o usuário e mostro seus dados
        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        usuarioDAO.getUsuarioAsync(sp.getString("userLog", ""), FirebaseDatabase.getInstance().getReference("usuarios"), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {
                    edtEmail.setText(usuario.getEmail());
                    edtSenha.setText(usuario.getSenha());
                    edtSenhaCon.setText(usuario.getSenha());
                } else {
                    Log.d("Usuario", "Usuário não encontrado");
                }
            }
        });

        return rootView;
    }
}