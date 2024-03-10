package com.example.florescer_juntos.View;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.florescer_juntos.Controler.PostDAO;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Post;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostarFragment extends Fragment {
    ImageView imageView;
    Button btnPhoto, btnConfirmar, btnImagem, btnRotateImg;
    TextView edtDesc;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Uri imageUri;
    private static final int pic_id = 123;
    private boolean isImageFromCamera = false;
    private int rotationAngle = 0;


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
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    public PostarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostarFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static PostarFragment newInstance(String param1, String param2) {
        PostarFragment fragment = new PostarFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_postar, container, false);

        radioGroup = rootView.findViewById(R.id.radioGroup);
        edtDesc = rootView.findViewById(R.id.edtDescPost);
        imageView = rootView.findViewById(R.id.imagemPostar);
        btnImagem = rootView.findViewById(R.id.btnImagemPost);
        btnConfirmar = rootView.findViewById(R.id.btnConfirmarPost2);
        btnPhoto = rootView.findViewById(R.id.btnPhoto);
        btnRotateImg = rootView.findViewById(R.id.btnRotateImg);

// Declare uma variável para acompanhar o número de rotações


        btnRotateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    Bitmap bitmap = getBitmapFromUri(imageUri);
                    if (bitmap != null) {
                        // Incrementar o ângulo de rotação em 90 graus
                        rotationAngle += 90;
                        if (rotationAngle >= 360) {
                            rotationAngle = 0; // Resetar para 0 quando alcançar 360 graus
                        }
                        // Girar a imagem de acordo com o ângulo atual
                        Bitmap rotatedBitmap = rotateBitmap(bitmap, rotationAngle);
                        imageView.setImageBitmap(rotatedBitmap); // Definir a imagem girada no ImageView
                    }
                }
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });

        btnImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {escolherImagem();}
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    // Nenhum botão está selecionado
                    Toast.makeText(requireContext(), "Selecione um tipo!", Toast.LENGTH_SHORT).show();
                } else if (imageUri == null) {
                    Toast.makeText(requireContext(), "Selecione uma imagem!", Toast.LENGTH_SHORT).show();
                } else {
                    radioButton = rootView.findViewById(selectedId);
                    // Mover a lógica de salvar o post para dentro deste bloco
                    salvarPost();
                }
            }
        });
        return rootView;
    }

    private void salvarPost() {
        Post post = new Post();
        post.setDescricao(edtDesc.getText().toString());
        post.setTipoPlanta(radioButton.getText().toString());

        // Obter a data e hora atual
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        post.setDataHora(dateFormat.format(calendar.getTime()));

        btnConfirmar.setEnabled(false);
        btnPhoto.setEnabled(false);
        btnImagem.setEnabled(false);
        Toast.makeText(requireContext(), "Salvando...", Toast.LENGTH_SHORT).show();

        SharedPreferences sp = requireActivity().getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
        UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
        String emailUsuario = "";
        String reference = "";

        // Verifico qual tipo de usuario
        FirebaseUser user_Google = FirebaseAuth.getInstance().getCurrentUser();
        if (user_Google != null) {
            emailUsuario = user_Google.getEmail();
            reference = "users";
            post.setTipoUsuario("Google");
        } else {
            emailUsuario = sp.getString("userLog", "");
            reference = "usuarios";
            post.setTipoUsuario("Common");
        }

        // Busco o usuario
        usuarioDAO.getUsuarioAsync(emailUsuario, FirebaseDatabase.getInstance().getReference(reference), getActivity(), new UsuarioDAO.UsuarioCallback() {
            @Override
            public void onUsuarioCarregado(Usuario usuario) {
                if (usuario != null) {
                    // Salvar o id do usuário e tentar salvar a imagem
                    post.setIdUsuario(usuario.getId());
                    post.setEmailUsuario(usuario.getEmail());
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("posts");
                    StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                    Bitmap bitmap = getBitmapFromUri(imageUri);
                    if (bitmap != null) {
                        // Aplicar rotação ao bitmap antes de salvá-lo
                        Bitmap rotatedBitmap = rotateBitmap(bitmap, rotationAngle);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = fileReference.putBytes(data);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Se salvar a imagem, obter a URL
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUrl) {
                                        // Salvar a URL no post e salvá-lo
                                        post.setImageUrl(downloadUrl.toString());
                                        PostDAO postDAO = new PostDAO(post, requireContext());
                                        if (postDAO.save()) {
                                            Toast.makeText(requireContext(), "Salvo com sucesso", Toast.LENGTH_SHORT).show();
                                            replaceFragment(new PerfilFragment());
                                            mainActivity.changeSelectedItem(R.id.btnPerfil);
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btnConfirmar.setEnabled(true);
                                btnImagem.setEnabled(true);
                                btnPhoto.setEnabled(true);
                                // Tratar falha no upload da imagem
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                // Isso é para a barra de progresso funcionar
                                double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            }
                        });
                    }
                } else {
                    btnConfirmar.setEnabled(true);
                    btnImagem.setEnabled(true);
                    btnPhoto.setEnabled(true);
                    Log.d("Usuario", "Usuário não encontrado");
                }
            }
        });
    }

    // Função que altera o fragment
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void escolherImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 112);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 112 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Para imagem selecionada
            isImageFromCamera = false;
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        } else if (requestCode == pic_id) { // Para a imagem da camera
            isImageFromCamera = true;

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageUri = getImageUri(getActivity(), rotateBitmap(photo, 90));
            imageView.setImageURI(imageUri);
        }
    }

    // Função que retorna a extensão do arquivo, pra salvar a url
    private String getFileExtension(Uri uri){
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // Função para converter o Bitmap em Uri
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        if (isImageFromCamera) {
            // Se a imagem foi tirada pela câmera, salva na galeria
            return Uri.parse(MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, null, null));
        } else {
            // Se a imagem foi selecionada da galeria, apenas retorna a URI do bitmap
            return Uri.parse("");
        }
    }



    // Função para girar o Bitmap
    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // Obter um Bitmap a partir de uma Uri
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}