# RefreshRecyclerView
自定义带下拉刷新，上拉加载更多的，支持分类型的的RecyclerView;


效果图如下：


![image](https://github.com/isJoker/RefreshRecyclerView/blob/master/gif/RefreshRecyclerView1.gif)


使用

* 布局文件：
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.jokerwan.refreshrecyclerview.widget.WjcRefreshRecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scrollbarAlwaysDrawVerticalTrack="true"
        android:scrollbars="vertical"/>
        
</RelativeLayout>

```

* 代码调用：
```
        final YourAdapter adapter = new YourAdapter(nameList,this);
        final WjcRecyclerViewAdapterWrapper adapterWrapper = new WjcRecyclerViewAdapterWrapper(adapter);

        //开始让加载更多不可见
        adapterWrapper.setFooterViewInVisiable(true);
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
                adapterWrapper.setFooterViewInVisiable(false);
            }
        });

        recyclerView.setLoadMoreListener(new WjcRefreshRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                // TODO: 2017/12/26  
            }
        });
        recyclerView.setOnRefreshListener(new WjcRefreshRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: 2017/12/26
            }
        });

          //其他方法
//        recyclerView.forceRefresh();//强制刷新
//        recyclerView.noMoreData();//显示没有更多数据
//        recyclerView.stopLoadMore();//停用加载更多
//        adapterWrapper.notifyDataSetChanged();//刷新数据
//        recyclerView.stopRefresh();//停止下拉刷新
```


![image](https://github.com/isJoker/RefreshRecyclerView/blob/master/gif/RefreshRecyclerView2.gif)

使用

* 布局文件：
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <android.support.v4.widget.SwipeRefreshLayout
        android:id="@+id/swipe_refresh_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </android.support.v4.widget.SwipeRefreshLayout>

</LinearLayout>

```

* 代码调用：

```
LoadMoreWrapperAdapter loadMoreWrapperAdapter = new LoadMoreWrapperAdapter(dataList);
        loadMoreWrapper = new LoadMoreWrapper(loadMoreWrapperAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(loadMoreWrapper);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新数据
                // TODO: 2017/12/26  
                
                loadMoreWrapper.notifyDataSetChanged();
                
            }
        });

        // 设置加载更多监听
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);//正在加载中
                // TODO: 2017/12/26  
            }
        });
        
        //其他方法
        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);// 加载完成
        loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);// 显示加载到底
```

