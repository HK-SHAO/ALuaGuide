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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by user on 2017/7/19.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Data> list;
    private int layoutId;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private CardView cardView;

        public ViewHolder(View v, int layoutId) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.cardview);
            title = (TextView) v.findViewById(R.id.title);
            content = (TextView) v.findViewById(R.id.content);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(v.getContext());
            Boolean nightMode = sp.getBoolean("nightMode", false);
            if (nightMode) {
                View line = v.findViewById(R.id.lineView);
                line.setBackgroundColor(Color.parseColor("#616161"));
                title.setTextColor(Color.parseColor("#f5f5f5"));
                content.setTextColor(Color.parseColor("#e0e0e0"));
            }

            if (layoutId == R.layout.page2_item) {
                ImageView image = (ImageView) v.findViewById(R.id.image);
                if (nightMode) {
                    image.setColorFilter(Color.parseColor("#e0e0e0"));
                } else {
                    image.setColorFilter(Color.parseColor("#424242"));
                }
            }
        }
    }

    public List<Data> getList() {
        return list;
    }

    public RecyclerAdapter(int layoutId, List<Data> list) {
        this.list = list;
        this.layoutId = layoutId;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Data data = list.get(position);
        holder.title.setText(data.title);
        holder.content.setText(data.content);

        if (layoutId == R.layout.page1_item) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PreviewActivity.actionStart(v.getContext(), data.title, data.content);
                }
            });
            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showListDialog(v, data);
                    return false;
                }
            });
        } else {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.title.equals("Lua参考手册")) {
                        LuaManualActivity.actionStart(v.getContext());
                        return;
                    }
                    ShowActivity.actionStart(v.getContext(), data.title);
                }
            });
        }
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        ViewHolder holder = new ViewHolder(view, layoutId);
        return holder;
    }
}
