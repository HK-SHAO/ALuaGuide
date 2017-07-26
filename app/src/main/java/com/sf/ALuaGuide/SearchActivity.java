package com.sf.ALuaGuide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class SearchActivity extends MySwipeBackActivity {

    private SearchAdapter adapter;
    private RecyclerView recyclerView;
    private List<Data> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.DarkAppTheme_Translucent);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_search);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToFinishActivity();
            }
        });
        data = DataManager.getAll(this);
        initRecyclerView();
        initEditText();
    }

    private void initEditText() {
        EditText search = (EditText) findViewById(R.id.edit_search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    adapter.removeAll();
                    adapter.notifyDataSetChanged();
                } else {
                    List<Data> dataList = new ArrayList<>();
                    Pattern p = Pattern.compile(s.toString());
                    for (Data d : data) {
                        String title = d.title;
                        String content = d.content;
                        if (p.matcher(title).find() || p.matcher(content).find()) {
                            dataList.add(d);
                        }
                    }
                    adapter.setData(dataList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.search_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SearchAdapter();
        recyclerView.setAdapter(adapter);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }
}
