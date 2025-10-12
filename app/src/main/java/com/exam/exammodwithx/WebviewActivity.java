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
      "\n" +
      "  function noop(){}\n" +
      "  function safeLog(){ try{ console && console.log.apply(console, arguments); }catch(e){} }\n" +
      "\n" +
      "  // notify to Android bridge\n" +
      "  function notifyAndroid(){\n" +
      "    try{\n" +
      "      if(window.AndroidBridge && typeof window.AndroidBridge.onBlurDetected === 'function'){\n" +
      "        window.AndroidBridge.onBlurDetected();\n" +
      "      }\n" +
      "    }catch(e){ safeLog('notifyAndroid failed', e); }\n" +
      "  }\n" +
      "\n" +
      "  // ---------- Protect window.onblur ----------\n" +
      "  var storedOnBlur = null;\n" +
      "  try{\n" +
      "    Object.defineProperty(window, 'onblur', {\n" +
      "      configurable: false,\n" +
      "      enumerable: true,\n" +
      "      get: function(){ return storedOnBlur; },\n" +
      "      set: function(fn){ storedOnBlur = fn; safeLog('onblur assignment intercepted'); }\n" +
      "    });\n" +
      "  }catch(e){\n" +
      "    safeLog('defineProperty(window.onblur) failed, fallback', e);\n" +
      "    window.onblur = noop;\n" +
      "    setInterval(function(){ if(window.onblur !== noop) window.onblur = noop; }, 500);\n" +
      "  }\n" +
      "  // ensure blur events still notify Android\n" +
      "  window.addEventListener('blur', function(){ notifyAndroid(); }, true);\n" +
      "\n" +
      "  // ---------- Protect document.onvisibilitychange and addEventListener for 'visibilitychange' ----------\n" +
      "  (function(){\n" +
      "    var originalAdd = Document.prototype.addEventListener;\n" +
      "    var originalRemove = Document.prototype.removeEventListener;\n" +
      "    var storedHandlers = [];\n" +
      "\n" +
      "    // override addEventListener to intercept 'visibilitychange'\n" +
      "    Document.prototype.addEventListener = function(type, listener, options){\n" +
      "      try{\n" +
      "        if(String(type).toLowerCase() === 'visibilitychange'){\n" +
      "          // store reference but do NOT attach it so it won't run\n" +
      "          storedHandlers.push({target: this, listener: listener, options: options});\n      " +
      "          safeLog('Intercepted addEventListener(visibilitychange)');\n" +
      "          return; // swallow the registration\n" +
      "        }\n" +
      "      }catch(e){ safeLog('addEventListener hook error', e); }\n" +
      "      // otherwise call original\n" +
      "      return originalAdd.call(this, type, listener, options);\n" +
      "    };\n" +
      "\n" +
      "    // override removeEventListener to keep behavior consistent for other events\n" +
      "    Document.prototype.removeEventListener = function(type, listener, options){\n" +
      "      try{\n" +
      "        if(String(type).toLowerCase() === 'visibilitychange'){\n" +
      "          // try remove from storedHandlers if present\n" +
      "          for(var i = storedHandlers.length - 1; i >= 0; i--){\n" +
      "            var h = storedHandlers[i];\n" +
      "            if(h.target === this && h.listener === listener){ storedHandlers.splice(i,1); safeLog('Removed stored visibilitychange handler'); }\n" +
      "          }\n" +
      "          return;\n" +
      "        }\n" +
      "      }catch(e){ safeLog('removeEventListener hook error', e); }\n" +
      "      return originalRemove.call(this, type, listener, options);\n" +
      "    };\n" +
      "\n" +
      "    // intercept document.onvisibilitychange assignment\n" +
      "    var storedOnVis = null;\n" +
      "    try{\n" +
      "      Object.defineProperty(document, 'onvisibilitychange', {\n" +
      "        configurable: false,\n" +
      "        enumerable: true,\n" +
      "        get: function(){ return storedOnVis; },\n" +
      "        set: function(fn){ storedOnVis = fn; safeLog('document.onvisibilitychange assignment intercepted'); }\n" +
      "      });\n" +
      "    }catch(e){\n" +
      "      safeLog('defineProperty(document.onvisibilitychange) failed, fallback');\n" +
      "      document.onvisibilitychange = null; // try to neutralize\n      " +
      "      setInterval(function(){ if(document.onvisibilitychange !== null) document.onvisibilitychange = null; }, 500);\n" +
      "    }\n" +
      "\n" +
      "    // Also intercept addEventListener on window (some libs attach there)\n" +
      "    var origWinAdd = window.addEventListener;\n" +
      "    var origWinRemove = window.removeEventListener;\n" +
      "    window.addEventListener = function(type, listener, options){\n" +
      "      try{\n" +
      "        if(String(type).toLowerCase() === 'visibilitychange'){\n" +
      "          safeLog('Intercepted window.addEventListener(visibilitychange)');\n" +
      "          return; // swallow\n" +
      "        }\n" +
      "      }catch(e){ safeLog('window.addEventListener hook error', e); }\n" +
      "      return origWinAdd.call(this, type, listener, options);\n" +
      "    };\n" +
      "    window.removeEventListener = function(type, listener, options){\n" +
      "      try{\n" +
      "        if(String(type).toLowerCase() === 'visibilitychange'){\n" +
      "          // nothing to remove because we swallowed registrations\n" +
      "          return;\n" +
      "        }\n" +
      "      }catch(e){ safeLog('window.removeEventListener hook error', e); }\n" +
      "      return origWinRemove.call(this, type, listener, options);\n" +
      "    };\n" +
      "\n" +
      "    // If something tries to dispatch visibilitychange manually, intercept at dispatchEvent\n" +
      "    var origDispatch = EventTarget.prototype.dispatchEvent;\n" +
      "    EventTarget.prototype.dispatchEvent = function(evt){\n" +
      "      try{\n" +
      "        var t = (evt && evt.type) ? String(evt.type).toLowerCase() : '';\n" +
      "        if(t === 'visibilitychange'){\n" +
      "          safeLog('Blocked dispatch of visibilitychange');\n" +
      "          // still notify android if document became hidden\n" +
      "          if(document.hidden) notifyAndroid();\n" +
      "          return true; // pretend dispatched\n" +
      "        }\n" +
      "      }catch(e){ safeLog('dispatchEvent hook error', e); }\n" +
      "      return origDispatch.call(this, evt);\n" +
      "    };\n" +
      "\n" +
      "    // Finally, ensure we call notifyAndroid when page actually becomes hidden (robust notify)\n" +
      "    document.addEventListener('visibilitychange', function(){\n" +
      "      try{ if(document.hidden) notifyAndroid(); }catch(e){}\n" +
      "    }, true);\n" +
      "\n" +
      "  })();\n" +
      "\n" +
      "  // small heartbeat to re-assert protections in case libs try to re-overwrite our hooks\n" +
      "  setInterval(function(){\n" +
      "    try{\n" +
      "      // rebind noop to onblur if someone overwrote\n" +
      "      if(window.onblur !== null && window.onblur !== undefined && window.onblur !== storedOnBlur){ window.onblur = storedOnBlur; }\n" +
      "    }catch(e){}\n" +
      "  }, 1000);\n" +
      "\n" +
      "  safeLog('Android blur/visibility protections installed');\n" +
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
