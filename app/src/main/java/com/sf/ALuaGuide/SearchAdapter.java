package com.sf.ALuaGuide;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2017/7/22.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<Data> list = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private CardView cardView;

        public ViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.search_cardview);
            title = (TextView) v.findViewById(R.id.search_title);
            content = (TextView) v.findViewById(R.id.search_content);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(v.getContext());
            Boolean nightMode = sp.getBoolean("nightMode", false);
            if (nightMode) {
                View line = v.findViewById(R.id.lineView);
                line.setBackgroundColor(Color.parseColor("#616161"));
                title.setTextColor(Color.parseColor("#f5f5f5"));
                content.setTextColor(Color.parseColor("#e0e0e0"));
            }
        }
    }

    public void removeAll() {
        list.clear();
        notifyDataSetChanged();
    }

    public void setData(List<Data> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(final SearchAdapter.ViewHolder holder, int position) {
        final Data data = list.get(position);
        holder.title.setText(data.title);
        holder.content.setText(data.content);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeActivity.actionStart(v.getContext(), data.title, data.content);
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showListDialog(v, data);
                return false;
            }
        });
    }

    private void showListDialog(final View view, final Data data) {
        final String[] items = {"复制全部内容", "分享全部内容"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(view.getContext());
        listDialog.setTitle("选择操作");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        ClipboardManager cmb = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText(data.title + "\n" + data.content);
                        Snackbar.make(view, "已复制全部内容", Snackbar.LENGTH_SHORT).setAction("确定", null).show();
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, data.title + "\n" + data.content);
                        view.getContext().startActivity(Intent.createChooser(intent, "分享"));
                        break;
                }
            }
        });
        listDialog.show();
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder(view);
        return holder;
    }
}