package com.sf.ALuaGuide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View page1;
    private View page2;
    private List<View> pageList;
    private MainPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private SwipeRefreshLayout swipeRefresh;
    private SharedPreferences sp;
    public static AppCompatActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("nightMode", false)) {
            setTheme(R.style.DarkAppTheme_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.actionStart(view.getContext());
            }
        });
        initSwipeRefresh();
        initPages();
        initTabs();
        initRecyclerView();
    }

    private void initSwipeRefresh() {
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        if (sp.getBoolean("nightMode", false)) {
            swipeRefresh.setColorSchemeResources(R.color.darkColorAccent);
        } else {
            swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void initTabs() {
        TabLayout tabs = (TabLayout) findViewById(R.id.main_tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setText("教程");
        tabs.getTabAt(1).setText("代码");
    }

    private void initRecyclerView() {
        RecyclerView recyclerView1 = (RecyclerView) page1.findViewById(R.id.page1_rv);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager1);
        String[] fileArray1 = {"Lua教程.txt"};
        RecyclerAdapter adapter1 = new RecyclerAdapter(R.layout.page1_item, DataManager.getData(this, fileArray1));
        recyclerView1.setAdapter(adapter1);

        final RecyclerView recyclerView2 = (RecyclerView) page2.findViewById(R.id.page2_rv);
        StaggeredGridLayoutManager layoutManager2 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView2.setLayoutManager(layoutManager2);
        final RecyclerAdapter adapter2 = new RecyclerAdapter(R.layout.page2_item, getPageData());
        recyclerView2.setAdapter(adapter2);

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //得到当拖拽的viewHolder的Position
                int fromPosition = viewHolder.getAdapterPosition();
                //拿到当前拖拽到的item的viewHolder
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(adapter2.getList(), i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(adapter2.getList(), i, i - 1);
                    }
                }
                adapter2.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView2);
    }

    private List<Data> getPageData() {
        List<Data> list = new ArrayList<>();
        list.add(new Data("Lua参考手册", "———由菜鸟教程提供"));
        list.add(new Data("AndroLua帮助", "———由nirenr提供"));
        list.add(new Data("实用代码", "———由寒歌提供"));
        list.add(new Data("网络操作", "———由寒歌提供"));
        list.add(new Data("文件操作", "———由寒歌提供"));
        list.add(new Data("用户界面", "———由寒歌提供"));
        list.add(new Data("基础代码", "———由寒歌提供"));
        list.add(new Data("Intent类", "———由寒歌提供"));
        list.add(new Data("笔记", "———由烧风提供"));
        return list;
    }

    private void initPages() {
        LayoutInflater inflater = getLayoutInflater();
        page1 = inflater.inflate(R.layout.page1, null);
        page2 = inflater.inflate(R.layout.page2, null);
        pageList = new ArrayList<>();
        pageList.add(page1);
        pageList.add(page2);

        viewPager = (ViewPager) findViewById(R.id.mViewPager);
        pagerAdapter = new MainPagerAdapter(pageList);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                SettingsActivity.actionStart(this);
                break;
            case R.id.action_finish:
                finish();
                break;
        }
        return true;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
