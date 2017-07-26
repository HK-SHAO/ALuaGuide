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


public class CodeActivity extends MySwipeBackActivity {

    private Context context;
    private CodeEditText editor;
    private String title;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.DarkAppTheme);
        }
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_code);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);

        editor = (CodeEditText) findViewById(R.id.edit_code);
        if (sp.getBoolean("autoFormat", true)) {
            editor.setText(editor.format(content));
        } else {
            editor.setText(content);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("复制").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(title + "\n" + content);
                Snackbar.make(editor, "已复制全部内容", Snackbar.LENGTH_SHORT).setAction("确定", null).show();
                return true;
            }
        });
        menu.add("分享").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + content);
                context.startActivity(Intent.createChooser(intent, "分享"));
                return true;
            }
        });
        return true;
    }

    public static void actionStart(Context context, String title, String content) {
        Intent intent = new Intent(context, CodeActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        context.startActivity(intent);
    }
}
