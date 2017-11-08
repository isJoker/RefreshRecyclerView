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

public class WjcRecyclerViewAdapter extends RecyclerView.Adapter<WjcRecyclerViewAdapter.WjcRecyclerViewHolder> {

    private List<String> dataList;
    private Context mContext;

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
    public WjcRecyclerViewAdapter.WjcRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WjcRecyclerViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview,parent,false));
    }

    @Override
    public void onBindViewHolder(WjcRecyclerViewAdapter.WjcRecyclerViewHolder holder, int position) {
        holder.tv.setText(dataList.get(position-1));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class WjcRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public WjcRecyclerViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_test);
        }
    }
}
