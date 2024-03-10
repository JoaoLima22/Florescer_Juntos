package com.example.florescer_juntos.View;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.florescer_juntos.Controler.UsuarioDAO;
import com.example.florescer_juntos.Model.Usuario;
import com.example.florescer_juntos.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {
    ImageView imgGif;
    //private static final int splashTime = 10000; // 10000 milliseconds = 10 segundos
    private static final int splashTime = 1000; // Só durante o desenvolvimento
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Instancio o que preciso
        imgGif = findViewById(R.id.imgGif);
        progressBar = findViewById(R.id.progressBarSplash);
        progressBar.setMax(splashTime);

        // Defino o gif
        RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(this)
                .asGif()
                .load(R.drawable.caqueiro_movimento)
                .apply(requestOptions)
                .into(imgGif);

        // Verifico se há usuario logado
        final int[] aux = {0};
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            aux[0] = 1;
        } else {
            SharedPreferences sp = getSharedPreferences("Florescer_Juntos", Context.MODE_PRIVATE);
            String email = sp.getString("userLog", "");

            UsuarioDAO usuarioDAO = new UsuarioDAO(new Usuario());
            usuarioDAO.isSaved(email, FirebaseDatabase.getInstance().getReference("usuarios"), new UsuarioDAO.ExistenceCheckCallback() {
                @Override
                public void onResult(boolean exists) {
                    // Se houver
                    if (exists) {
                        aux[0] = 2;
                    }
                }
            });
        }

        new CountDownTimer(splashTime, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                progressBar.setProgress((int) (splashTime - millisUntilFinished));
            }
            @Override
            public void onFinish() {
                // Quando tiver acabado o tempo direciono pro objetivo
                progressBar.setProgress(splashTime);
                if (aux[0]==1 || aux[0]==2 ){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }.start();
    }
}