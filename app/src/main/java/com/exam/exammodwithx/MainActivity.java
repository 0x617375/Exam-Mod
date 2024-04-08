package com.exam.exammodwithx;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.exam.exammodwithx.databinding.ActivityMainBinding;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        //binding.urlEditText.setVisibility(View.GONE); //untuk hide urlEditText dari view (tidak perlu)
        //binding.masukkanURLTextView.setVisibility(View.GONE); //untuk hide masukkanURLTextView dari view (tidak perlu)

        sharedPreferences = getSharedPreferences("setting", 0);
        if(sharedPreferences.getBoolean("is_first_time", true)){ // Hanya ditampilkan sekali saat aplikasi dimulai
            Toasty.Config.getInstance()
            .setToastTypeface(Typeface.createFromAsset(getAssets(), "PCap Terminal.otf"))
            .allowQueue(false)
            .apply();
            
            Toast tscr = Toasty.custom(MainActivity.this, 
            R.string.credit, 
            ContextCompat.getDrawable(this, R.drawable.laptop512),
            android.R.color.black, android.R.color.holo_green_light, 
            Toasty.LENGTH_LONG, true, true);
            
            tscr.show(); // Menampilkan credit saat mulai aplikasi
            tscr.show();
            
            Toast.makeText(this,"Tekan logo Tut Wuri untuk melihat fitur dan custom setting", 1).show();
            sharedPreferences.edit().putBoolean("is_first_time", false).apply();
            sharedPreferences.edit().putBoolean("support_zoom", true).apply();
        }
        
        String url = sharedPreferences.getString("url", null);
        
        if(url != null){
            binding.urlEditText.setText(url);
        };
        
        binding.masukBtn.setOnClickListener((View v) -> {
                String urltv = binding.urlEditText.getText().toString();
                if(urltv.isEmpty()){ // Tidak dimuat saat url kosong
                    Toast.makeText(this, "Masukkan url dengan benar", 0).show();
                    return;
                }
                if(!urltv.startsWith("https")){ // Memaksa ke https agar bisa diload
                    urltv = "https://" + urltv;
                }
                Intent webAct = new Intent(MainActivity.this, WebviewActivity.class);
                webAct.putExtra("url", urltv);
                sharedPreferences.edit().putString("url", urltv).apply();
                startActivity(webAct);
        });
        
        binding.logoView.setOnClickListener((View v) -> {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    @Override
    public void onBackPressed() {
        finish();
    }
}
