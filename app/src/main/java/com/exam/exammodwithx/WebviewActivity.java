package com.exam.exammodwithx;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.exam.exammodwithx.databinding.ActivityWebViewBinding;

public class WebviewActivity extends AppCompatActivity {
    private ActivityWebViewBinding binding;
    private Handler handler;
    private boolean doubleBackPressedOnce = false;
    private boolean tripleBackPressedOnce = false;
    private SharedPreferences sharedPreferences;
    private WebSettings wbst;
    private WebView myWebView;
    private boolean pinned = false;

    @Override
    //@SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        String url = String.valueOf(getIntent().getStringExtra("url"));
        
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide(); // Menyembunyikan title tampilan paling atas
        }
        
        myWebView = binding.myWebView;
        
        getWindow().setFlags( // Agar layar tetap menyala 
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags( // Biar Fullscreen cuy
            WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Kode dibawah yang menyebabkan tidak bisa ScreenShoot
        /*getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        ); */
        
        sharedPreferences = getSharedPreferences("setting", 0);
        
         class MyWebViewClient extends WebViewClient {
                @Override
                public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                    return false;
                }

                @Override
                @Deprecated
                public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                    return false;
                }
            }
        
        wbst = myWebView.getSettings();
        wbst.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new JSBridge(), "JSBridge"); // Membuat koneksi antara java dan javascript
        WebView.setWebContentsDebuggingEnabled(true);
        wbst.setAllowContentAccess(true);
        wbst.setAllowFileAccess(true);
        wbst.setDomStorageEnabled(true);
        
        // Useless (Tidak perlu)
        //binding.myWebView.getSettings().setDatabaseEnabled(true);
        //binding.myWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //binding.myWebView.getSettings().setLoadWithOverviewMode(true);
        //binding.myWebView.getSettings().setUseWideViewPort(true);
        
        if(sharedPreferences.getBoolean("support_zoom", true)){       
            wbst.setSupportZoom(true); // Agar web dapat dizoom
            wbst.setBuiltInZoomControls(true); // Agar web dapat dizoom
            wbst.setDisplayZoomControls(false); // Menyembunyikan tombol kontrol zoom
        }
        
        if(sharedPreferences.getBoolean("darkmode", false)){
            //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){ //Error: Call requires API level 29 (current min is 21): android.webkit.WebSettings#setForceDark [NewApi]
                wbst.setForceDark(WebSettings.FORCE_DARK_ON); // Membuat web menjadi dark (opsional tergantung setting)
            //}
        }
        if(sharedPreferences.getString("user_agent", null) != null && 
           !sharedPreferences.getString("user_agent", null).isEmpty()){
            wbst.setUserAgentString(sharedPreferences.getString("user_agent", null));
        }
        
        myWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
                binding.loadingTextView.setVisibility(View.GONE); // Menghilangkan Loading saat web sudah dimuat
                myWebView.setVisibility(View.VISIBLE);

                injectOverrideScript();
            }
            
            @Override
            public void onReceivedError(WebView webView, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(webView, request, error);
                Toast.makeText(WebviewActivity.this, "Jaringan kurang stabil", Toast.LENGTH_SHORT).show();
            }
        });
        myWebView.loadUrl(url);
    }
    
    // Override onBackPressed to handle WebView navigation history
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Mencegah WebView memuat ulang halaman saat konfigurasi berubah (rotasi dan split layar)
        if (myWebView != null) {
            //binding.myWebView.reload(); //untuk reload page saat config berubah
            
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
    
    // Kode dibawah untuk handle tombol 2x back dan 3x
    @Override
    @SuppressWarnings("deprecation")
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        handler = new Handler();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tripleBackPressedOnce) {
                // If triple-tap back button is pressed, exit the app
                finish();
            } else if (doubleBackPressedOnce) {
                // If double-tap back button is pressed, show a toast message
                //Toast.makeText(this, "Press back once more to exit", Toast.LENGTH_SHORT).show();
                tripleBackPressedOnce = true;
                myWebView.loadUrl("javascript:selesaiTest()"); // Ini fungsi yang dijalankan untuk mengirim langsung jawaban ke server

                // Reset the triple tap flag after a delay of 2 seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tripleBackPressedOnce = false;
                    }
                }, 2000); // Delay 2 detik untuk mereset kondisi tertekan tombol back
            } else {
                // Show a toast indicating to press back again to exit
                //Toast.makeText(this, "el Press back once more to exit", Toast.LENGTH_SHORT).show();
                doubleBackPressedOnce = true;
                Toast.makeText(this, "Tekan 2x untuk logout", Toast.LENGTH_SHORT).show();

                // Reset the double tap flag after a delay of 2 seconds
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPressedOnce = false;
                    }
                }, 2000); // Sama dengan atasnya yaitu delay
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // contoh metode di Activity / Fragment
    private void injectOverrideScript() {
    String script =
      "(function(){\n" +
      "  if(window.__android_blur_override_installed) return;\n" +
      "  window.__android_blur_override_installed = true;\n" +
      "  var storedHandler = null;\n" +
      "  function noop(){}\n" +
      "  try{\n" +
      "    Object.defineProperty(window, 'onblur', {\n" +
      "      configurable: false,\n" +
      "      enumerable: true,\n" +
      "      get: function(){ return storedHandler; },\n" +
      "      set: function(fn){\n" +
      "        // simpan referensi jika suatu saat mau panggil\n" +
      "        storedHandler = fn;\n" +
      "        try{ console && console.log('onblur assignment intercepted'); }catch(e){}\n" +
      "      }\n" +
      "    });\n" +
      "  }catch(e){\n" +
      "    // fallback jika defineProperty gagal (CSP / environment)\n" +
      "    try{ console && console.warn('defineProperty failed, fallback', e); }catch(x){}\n" +
      "    window.onblur = noop;\n" +
      "    setInterval(function(){ if(window.onblur !== noop){ window.onblur = noop; } }, 500);\n" +
      "  }\n" +
      "  // event listeners tambahan untuk robust detection\n" +
      "  window.addEventListener('blur', function(){ noop(); }, true);\n" +
      "  document.addEventListener('visibilitychange', function(){ if(document.hidden) noop(); }, false);\n" +
      "})();";
    
        // eksekusi di WebView (panggil dari UI thread)
        myWebView.post(() -> myWebView.evaluateJavascript(script, null));
    }
    

    final class JSBridge {
        @JavascriptInterface
        public final void checkBimasoftExamClient(String str) {
            // Ini alasan mengapa tidak diijinkan menggunakan 
            // web browser karena aplikasi mempunyai fungsi ini.
            // Tidak melakukan apa apa hanya jika tidak ada fungsi
            // ini dalam class maka tidak diijinkan mengakses website
        }

        @JavascriptInterface
        public final boolean isAppPinned() {
            return pinned;
        }

        @JavascriptInterface
        public final void startPinningApp() {
            pinned = true;
        }

        @JavascriptInterface
        public final void stopPinningApp() {
            pinned = false;
        }
        
        // not sure
        @JavascriptInterface
        public final boolean isDevice() {
            return false;
        }
    
        @JavascriptInterface
        public final boolean isForbidden() {
            return false;
        }
        
    }
}
