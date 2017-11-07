package com.jokerwan.refreshrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jokerwan.refreshrecyclerview.widget.WjcRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WjcRefreshRecyclerView recyclerView = (WjcRefreshRecyclerView) findViewById(R.id.alx_recyclerView);
        String[] names = {"张三","李四","王五","赵六","李明","小白","豆豆","小黑","花花","帅哥","美女","大妈","小明","阿诚","小磊磊","浩总","庆庆","佳爷","璁璁","小可爱"};
        final List<String> nameList = new ArrayList<>();
        for(String s:names) nameList.add(s);

        final WjcRecyclerViewAdapter adapter = new WjcRecyclerViewAdapter(nameList,R.layout.recyclerview_item,true);
        recyclerView.setAdapter(adapter);//需要先setAdapter,再setLoadMoreEnable
        recyclerView.setLoadMoreEnable(true);
        recyclerView.setLoadMoreListener(new WjcRefreshRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                //模拟加载数据
                new Handler().postDelayed(new Runnable() {//模拟两秒网络延迟
                    @Override
                    public void run() {
                        String[] moreNames = {"新加的名字1","新加的名字2","新加的名字3","新加的名字4","新加的名字5","新加的名字6","新加的名字7","新加的名字8"};
                        List<String> dataList = adapter.getDataList();
                        for(String s:moreNames)dataList.add(s);
                        adapter.notifyItemInserted(nameList.size() - moreNames.length + 1);
                        recyclerView.stopLoadMore();
                    }
                },2000);

            }
        });
        recyclerView.setOnRefreshListener(new WjcRefreshRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {//模拟两秒网络延迟
                    @Override
                    public void run() {
                        String[] moreNames = {"刷新的名字1","刷新的名字2","刷新的名字3","刷新的名字4","刷新的名字5","刷新的名字6","刷新的名字7","刷新的名字8","刷新的名字9","刷新的名字10","刷新的名字11","刷新的名字12"};
                        final List<String> nameList2 = new ArrayList<String>();
                        for(String s:moreNames)nameList2.add(s);
                        adapter.setDataList(nameList2);//重设数据
                        adapter.notifyDataSetChanged();
                        recyclerView.stopRefresh();
                    }
                },2000);
            }
        });


//        recyclerView.forceRefresh();//强制刷新
    }

    class WjcRecyclerViewAdapter extends WjcRefreshRecyclerView.WjcDragRecyclerViewAdapter<String> {

        public WjcRecyclerViewAdapter(List<String> dataList, int itemLayout, boolean pullEnable) {
            super(dataList, itemLayout, pullEnable);
        }

        @Override
        public RecyclerView.ViewHolder setItemViewHolder(View itemView) {
            return new AlxRecyclerViewHolder(itemView);
        }

        @Override
        public void initItemView(RecyclerView.ViewHolder itemHolder, int posion, String entity) {
            AlxRecyclerViewHolder holder = (AlxRecyclerViewHolder)itemHolder;
            holder.tv1.setText(getDataList().get(posion));
        }
    }

    class AlxRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView tv1;
        public AlxRecyclerViewHolder(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv1);
        }
    }
}
