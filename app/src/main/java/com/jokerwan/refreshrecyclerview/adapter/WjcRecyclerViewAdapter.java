package com.jokerwan.refreshrecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jokerwan.refreshrecyclerview.R;

import java.util.List;

/**
 * Created by ${JokerWan} on 2017/11/8.
 * WeChat: wjc398556712
 * Function:
 */

public class WjcRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<String> dataList;
    private Context mContext;

    private final int TYPE_HEAD = 1;
    private final int TYPE_NOMAL = 2;
    private int currentType;

    public WjcRecyclerViewAdapter(List<String> dataList,Context context) {
        this.mContext = context;
        this.dataList = dataList;
    }

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> data) {
        dataList.clear();
        if(data != null) {
            dataList.addAll(data);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEAD) {
            return  new HeadViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_head_view,parent,false));
        } else {
            return new WjcRecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof WjcRecyclerViewHolder) {
            ((WjcRecyclerViewHolder)holder).tv.setText(dataList.get(position-2));//自己的头🏠下拉刷新的头
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 1) {//position为0时下拉刷新头布局
            currentType = TYPE_HEAD;
        } else {
            currentType = TYPE_NOMAL;
        }
        return currentType;
    }

    public class WjcRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public WjcRecyclerViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_test);
        }
    }

    private class HeadViewHolder extends RecyclerView.ViewHolder {

        HeadViewHolder(View itemView) {
            super(itemView);
        }
    }
}
