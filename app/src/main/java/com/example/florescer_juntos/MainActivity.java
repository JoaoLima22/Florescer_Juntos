package com.example.florescer_juntos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        // Corrija aqui, inicializando a textView usando findViewById
        textView = findViewById(R.id.tview);

        // Verifique se o usuário não é nulo antes de acessar seus dados
        if (user != null) {
            // Certifique-se de que getDisplayName() não é nulo antes de chamar toString()
            String displayName = user.getDisplayName();
            if (displayName != null) {
                textView.setText(displayName);
            } else {
                // Lidar com o caso em que getDisplayName() é nulo
                textView.setText("Nome de usuário indisponível");
            }
        } else {
            // Lidar com o caso em que o usuário é nulo
            textView.setText("Usuário não autenticado");
        }
    }
}