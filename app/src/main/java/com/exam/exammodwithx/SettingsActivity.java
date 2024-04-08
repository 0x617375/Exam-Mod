package com.exam.exammodwithx;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.exam.exammodwithx.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    
    private ActivitySettingsBinding binding;
    private SharedPreferences sharedPreferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.stpvFortune.startAnimation(0, 1);
        
        new ColorfulTextViewAnimator(binding.manual).startAnimationTwo();
        new ColorfulTextViewAnimator(binding.fitur).startAnimation();
        
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
        
        sharedPreferences = getSharedPreferences("setting", 0);
        if(sharedPreferences.getBoolean("darkmode",false)){
            binding.switchDarkMode.setChecked(true);
        } else {
            binding.switchDarkMode.setChecked(false);
        }
        
        binding.switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("darkmode", true).commit();
                } else {
                    sharedPreferences.edit().putBoolean("darkmode", false).commit();
                }
            }
        });
        
        binding.switchSupportZoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("support_zoom", true).commit();
                } else {
                    sharedPreferences.edit().putBoolean("support_zoom", false).commit();
                }
            }
        });
        
        if(sharedPreferences.getBoolean("support_zoom",true)){
            binding.switchSupportZoom.setChecked(true);
        } else {
            binding.switchSupportZoom.setChecked(false);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    
}
