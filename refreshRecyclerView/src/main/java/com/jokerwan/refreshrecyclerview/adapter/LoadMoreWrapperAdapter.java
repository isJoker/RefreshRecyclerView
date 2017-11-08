package com.jokerwan.refreshrecyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jokerwan.refreshrecyclerview.R;

import java.util.List;

/**
 * 上拉加载更多
 * 装饰着模式
 */

public class LoadMoreWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> dataList;

    private final int TYPE_HEAD = 1;
    private final int TYPE_NOMAL = 2;
    private int currentType;

    public LoadMoreWrapperAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEAD) {
            return new HeadViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_head_view, parent, false));
        } else {
            return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_recyclerview, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
            recyclerViewHolder.tvItem.setText(dataList.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            currentType = TYPE_HEAD;
        } else {
            currentType = TYPE_NOMAL;
        }
        return currentType;
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {

        HeadViewHolder(View itemView) {
            super(itemView);
        }
    }
}
