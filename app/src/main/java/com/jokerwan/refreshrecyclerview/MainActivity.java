package com.jokerwan.refreshrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.jokerwan.refreshrecyclerview.adapter.WjcRecyclerViewAdapter;
import com.jokerwan.refreshrecyclerview.widget.WjcRefreshRecyclerView;
import com.jokerwan.refreshrecyclerview.wrapper.WjcRecyclerViewAdapterWrapper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WjcRefreshRecyclerView recyclerView = (WjcRefreshRecyclerView) findViewById(R.id.recyclerView);
//        String[] names = {"张三","李四","王五","赵六","小白","豆豆","小黑","花花","帅哥","美女","大妈","小明","阿诚","小磊磊","浩总","庆庆","佳爷","璁璁","小可爱"};
        String[] names = {"张三","李四","王五","赵六"};
        final List<String> nameList = new ArrayList<>();
        for(String s:names) nameList.add(s);

        final WjcRecyclerViewAdapter adapter = new WjcRecyclerViewAdapter(nameList,this);
        final WjcRecyclerViewAdapterWrapper adapterWrapper = new WjcRecyclerViewAdapterWrapper(adapter);

        //开始让加载更多不可见
        adapterWrapper.setFooterViewVisiable(true);

        recyclerView.setAdapter(adapterWrapper);//需要先setAdapter,再setLoadMoreEnable
        recyclerView.setLoadMoreEnable(true);
        recyclerView.setPullRefreshEnable(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //让加载更多可见
                adapterWrapper.setFooterViewVisiable(false);
            }
        });

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
                        for(String s:moreNames) nameList2.add(s);
                        adapter.setDataList(nameList2);//重设数据
                        adapterWrapper.notifyDataSetChanged();
                        recyclerView.stopRefresh();
                    }
                },2000);
            }
        });

//        recyclerView.forceRefresh();//强制刷新
    }
}
