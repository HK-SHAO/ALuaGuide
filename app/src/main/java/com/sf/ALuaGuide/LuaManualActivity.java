package com.sf.ALuaGuide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LuaManualActivity extends MySwipeBackActivity {


    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.DarkAppTheme);
        }
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_lua_manual);
        initWebView();
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webView);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        webView.loadUrl("file:///android_asset/LuaManual/manual.html");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else {
                scrollToFinishActivity();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LuaManualActivity.class);
        context.startActivity(intent);
    }

}
