package com.example.florescer_juntos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import com.example.florescer_juntos.View.HomeFragment;
import com.example.florescer_juntos.View.PerfilFragment;
import com.example.florescer_juntos.View.PostarFragment;
import com.example.florescer_juntos.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());

        binding.barraInferior.setOnItemSelectedListener(item -> {

            if (item.getItemId()==R.id.btnAdd){
                replaceFragment(new PostarFragment());
            } else if (item.getItemId()==R.id.btnHome){
                replaceFragment(new HomeFragment());
            } else if (item.getItemId()==R.id.btnPerfil){
                replaceFragment(new PerfilFragment());
            }

            return true;
        });
    }
    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}