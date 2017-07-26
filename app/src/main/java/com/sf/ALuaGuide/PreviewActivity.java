package com.sf.ALuaGuide;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.sf.LuaEditor.CodeEditText;
import com.sf.LuaEditor.ShaderEditor;


public class PreviewActivity extends MySwipeBackActivity {

    private Context context;
    private String text;
    private String title;
    private ShaderEditor content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.DarkAppTheme);
        }
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_preview);
        setupActionBar();
        content = (ShaderEditor) findViewById(R.id.content_preview);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        setTitle(title);
        text = intent.getStringExtra("content");

        if (sp.getBoolean("autoFormat", true)) {
            content.setText(content.format(text));
        } else {
            content.setText(text);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("复制").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(title + "\n" + text);
                Snackbar.make(content, "已复制全部内容", Snackbar.LENGTH_SHORT).setAction("确定", null).show();
                return true;
            }
        });
        menu.add("分享").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + text);
                context.startActivity(Intent.createChooser(intent, "分享"));
                return true;
            }
        });
        return true;
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static void actionStart(Context context, String title, String content) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }
}
