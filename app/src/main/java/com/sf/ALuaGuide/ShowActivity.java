package com.sf.ALuaGuide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class ShowActivity extends MySwipeBackActivity {

    private ShowAdapter adapter;
    private RecyclerView recyclerView;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.DarkAppTheme_SwipeBack);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_show);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        title = intent.getStringExtra("name");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.show_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        String[] array = {title + ".txt"};
        adapter = new ShowAdapter(DataManager.getData(this, array));
        recyclerView.setAdapter(adapter);
    }

    public static void actionStart(Context context, String name) {
        Intent intent = new Intent(context, ShowActivity.class);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }
}
