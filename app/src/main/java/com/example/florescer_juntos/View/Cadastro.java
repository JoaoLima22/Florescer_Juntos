package com.example.florescer_juntos.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.florescer_juntos.R;

public class Cadastro extends AppCompatActivity {
    TextView tvlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        tvlogin = findViewById(R.id.tvLogin);

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Cadastro.this, Login.class);
                startActivity(it);
                finish();
            }
        });
    }
}