package com.jokerwan.refreshrecyclerview.wrapper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jokerwan.refreshrecyclerview.widget.CustomFooterView;
import com.jokerwan.refreshrecyclerview.widget.CustomHeaderView;

/**
 * Created by ${JokerWan} on 2017/11/8.
 * WeChat: wjc398556712
 * Function:
 */

public class WjcRecyclerViewAdapterWrapper extends RecyclerView.Adapter{
    private static final int TYPE_HEADER = 436874;
    private static final int TYPE_ITEM = 256478;
    private static final int TYPE_FOOTER = 9621147;

    private RecyclerView.Adapter adapter;
    private RecyclerView.ViewHolder vhItem;
    private boolean loadMore;
    private boolean hideFooterView;

    public WjcRecyclerViewAdapterWrapper(RecyclerView.Adapter adapter){
        this.adapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            View headerView = new CustomHeaderView(parent.getContext());
            return new VHHeader(headerView);
        } else if(viewType==TYPE_FOOTER){
            CustomFooterView footerView = new CustomFooterView(parent.getContext());
            return new VHFooter(footerView);
        } else {
            //inflate your layout and pass it to view holder
            return adapter.onCreateViewHolder(parent,viewType);
        }
    }

    public void setLoadMoreEnable(boolean enable){
        this.loadMore = enable;
    }

    public boolean getLoadMoreEnable(){return loadMore;}

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {//相当于getView

        if (holder instanceof WjcRecyclerViewAdapterWrapper.VHHeader) {
            //cast holder to VHHeader and set data for header.

        }else if(holder instanceof WjcRecyclerViewAdapterWrapper.VHFooter){

            if(!loadMore || hideFooterView) {
                ((VHFooter) holder).footerView.setVisibility(View.GONE);//第一次初始化显示的时候要不要显示footerView
            } else {
                ((VHFooter) holder).footerView.setVisibility(View.VISIBLE);
            }
        } else {

            adapter.onBindViewHolder(holder,position);
        }
    }

    /**
     * 提供给外部用来隐藏FooterView（比如ListData只有几条数据不足以充满一屏幕时一般隐藏FooterView）
     */
    public void setFooterViewVisiable(boolean isVisiable){
        hideFooterView = isVisiable;
    }


    @Override
    public int getItemCount() {
        int listCount = adapter.getItemCount();
        return listCount == 0 ? 1 : listCount + 2;//如果有header,若list不存在或大小为0就没有footView，反之则有
    }//这里要考虑到头尾部，多以要加2

    /**
     * 根据位置判断这里该用哪个ViewHolder
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        else if(isPositonFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return adapter.getItemViewType(position);
        }
    }

    private boolean isPositonFooter(int position){//这里的position从0算起
        return position == getItemCount() - 1;//如果有item(也许为0)
    }

    private class VHHeader extends RecyclerView.ViewHolder {
        private VHHeader(View headerView) {
            super(headerView);
        }
    }

    private class VHFooter extends RecyclerView.ViewHolder {
        private CustomFooterView footerView;

        private VHFooter(View itemView) {
            super(itemView);
            footerView = (CustomFooterView)itemView;
        }
    }

}
