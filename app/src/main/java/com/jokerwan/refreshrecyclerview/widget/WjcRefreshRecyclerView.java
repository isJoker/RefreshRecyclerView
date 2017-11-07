package com.jokerwan.refreshrecyclerview.widget;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ${JokerWan} on 2017/11/7.
 * WeChat: wjc398556712
 * Function: 自定义带上拉刷新下拉加载RecyclerView(目前的局限性，不支持分类型)
 */

public class WjcRefreshRecyclerView extends RecyclerView {

    private int footerHeight = -1;
    LinearLayoutManager layoutManager;
    // -- footer view
    private CustomFooterView mFooterView;
    private boolean mEnablePullLoad;
    private boolean mIsLoading;
    private boolean isBottom;
    private boolean mIsFooterReady = false;
    private LoadMoreListener loadMoreListener;

    // -- header view
    private CustomHeaderView mHeaderView;
    private boolean mEnablePullRefresh = true;
    private boolean mIsRefreshing;
    private boolean isHeader;
    private boolean mIsHeaderReady = false;
    private Timer timer;
    private float oldY;
    Handler handler = new Handler();
    private OnRefreshListener refreshListener;
    private WjcDragRecyclerViewAdapter adapter;
    private int maxPullHeight = 50;//最多下拉高度的px值

    private static final int HEADER_HEIGHT = 68;//头部高度68dp
    private static final int MAX_PULL_LENGTH = 150;//最多下拉150dp
    private OnClickListener footerClickListener;
    private static WjcRefreshRecyclerView instance;


    public WjcRefreshRecyclerView(Context context) {
        this(context,null);
    }

    public WjcRefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WjcRefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        instance = this;
        initView(context);
    }

    public void setAdapter(WjcDragRecyclerViewAdapter adapter){
        super.setAdapter(adapter);
        this.adapter = adapter;
    }

    public boolean isLoading() {
        return mIsLoading;
    }

    public boolean isreshing() {
        return mIsRefreshing;
    }

    private void updateFooterHeight(float delta) {
        if(mFooterView==null)return;
        int bottomMargin = mFooterView.getBottomMargin();
//        Log.i("Alex3","初始delta是"+delta);
        if(delta>50)delta = delta/6;
        if(delta>0) {//越往下滑越难滑
            if(bottomMargin>maxPullHeight)delta = delta*0.65f;
            else if(bottomMargin>maxPullHeight * 0.83333f)delta = delta*0.7f;
            else if(bottomMargin>maxPullHeight * 0.66667f)delta = delta*0.75f;
            else if(bottomMargin>maxPullHeight >> 1)delta = delta*0.8f;
            else if(bottomMargin>maxPullHeight * 0.33333f)delta = delta*0.85f;
            else if(bottomMargin>maxPullHeight * 0.16667F && delta > 20)delta = delta*0.2f;//如果是因为惯性向下迅速的俯冲
            else if(bottomMargin>maxPullHeight * 0.16667F)delta = delta*0.9f;
//            Log.i("Alex3","bottomMargin是"+mFooterView.getBottomMargin()+" delta是"+delta);
        }

        int height = mFooterView.getBottomMargin() + (int) (delta+0.5);

        if (mEnablePullLoad && !mIsLoading) {
            if (height > 150){//必须拉超过一定距离才加载更多
//            if (height > 1){//立即刷新
                mFooterView.setState(CustomFooterView.STATE_READY);
                mIsFooterReady = true;
//                Log.i("Alex2", "ready");
            } else {
                mFooterView.setState(CustomFooterView.STATE_NORMAL);
                mIsFooterReady = false;
//                Log.i("Alex2", "nomal");
            }
        }
        mFooterView.setBottomMargin(height);


    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 20) {
            Log.i("Alex2", "准备重置高度,margin是" + bottomMargin + "自高是" + footerHeight);
            this.smoothScrollBy(0,-bottomMargin);
            //一松手就立即开始加载
            if(mIsFooterReady){
                startLoadMore();
            }
        }
    }


    public void setLoadMoreListener(LoadMoreListener listener){
        this.loadMoreListener = listener;
    }

    public void initView(Context context){
        layoutManager = new LinearLayoutManager(context);//自带layoutManager，请勿设置
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        layoutManager.offsetChildrenVertical(height*2);//预加载2/3的卡片
        this.setLayoutManager(layoutManager);
        maxPullHeight = dp2px(getContext().getResources().getDisplayMetrics().density,MAX_PULL_LENGTH);//最多下拉150dp
        this.footerClickListener = new footerViewClickListener();
        this.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Log.i("Alex2", "停下了||放手了");
                        if(isBottom) resetFooterHeight();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        Log.i("Alex2", "开始拖了,现在margin是" + (mFooterView == null ? "" : mFooterView.getBottomMargin()));
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        Log.i("Alex2", "开始惯性移动");
                        break;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItemPosition = layoutManager.findLastVisibleItemPosition();
                if(lastItemPosition == layoutManager.getItemCount()-1 && mEnablePullLoad) {//如果到了最后一个
                    isBottom = true;
                    mFooterView = (CustomFooterView)layoutManager.findViewByPosition(layoutManager.findLastVisibleItemPosition());//一开始还不能hide，因为hide得到最后一个可见的就不是footerview了
                    if(mFooterView!=null) mFooterView.setOnClickListener(footerClickListener);
                    if(footerHeight==-1 && mFooterView!=null){
                        mFooterView.show();
                        mFooterView.setState(CustomFooterView.STATE_NORMAL);
                        footerHeight = mFooterView.getMeasuredHeight();//这里的测量一般不会出问题
                    }
                    updateFooterHeight(dy);
                } else if(lastItemPosition == layoutManager.getItemCount()-1 && mEnablePullLoad){//如果到了倒数第二个
                    startLoadMore();//开始加载更多
                } else {
                    isBottom = false;
                }
            }
        });
    }

    /**
     * 设置是否开启上拉加载更多的功能
     *
     * @param enable
     */
    public void setLoadMoreEnable(boolean enable) {
        mIsLoading = false;
        mEnablePullLoad = enable;
        if(adapter!=null) adapter.setLoadMoreEnable(enable);//adapter和recyclerView要同时设置
        if(mFooterView == null) return;
        if (!mEnablePullLoad) {
//            this.smoothScrollBy(0,-footerHeight);
            mFooterView.hide();
            mFooterView.setOnClickListener(null);
            mFooterView.setBottomMargin(0);
            //make sure "pull up" don't show a line in bottom when listview with one page
        } else {
            mFooterView.show();
            mFooterView.setState(CustomFooterView.STATE_NORMAL);
            mFooterView.setVisibility(VISIBLE);
            //make sure "pull up" don't show a line in bottom when listview with one page
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    /**
     * 停止loadmore
     */
    public void stopLoadMore() {
        if (mIsLoading == true) {
            mIsLoading = false;
            if(mFooterView==null) return;
            mFooterView.show();
            mFooterView.setState(CustomFooterView.STATE_ERROR);
        }
    }

    private void startLoadMore() {
        if(mIsLoading) return;
        mIsLoading = true;
        if(mFooterView!=null) mFooterView.setState(CustomFooterView.STATE_LOADING);
        mIsFooterReady = false;
        if (loadMoreListener != null) {
            loadMoreListener.onLoadMore();
        }
    }

    /**
     * 在刷新时要执行的方法
     */
    public interface LoadMoreListener{
        void onLoadMore();
    }

    /**
     * 点击loadMore后要执行的事件
     */
    class footerViewClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            startLoadMore();
        }
    }


    private void updateHeaderHeight(float delta) {
        mHeaderView = (CustomHeaderView) layoutManager.findViewByPosition(0);
        if(delta>0){//如果是往下拉
            int topMargin = mHeaderView.getTopMargin();
            if(topMargin>maxPullHeight * 0.33333f)delta = delta*0.5f;
            else if(topMargin>maxPullHeight * 0.16667F)delta = delta*0.55f;
            else if(topMargin>0)delta = delta*0.6f;
            else if(topMargin<0)delta = delta*0.6f;//如果没有被完全拖出来
            mHeaderView.setTopMargin(mHeaderView.getTopMargin() + (int)delta);
        } else{//如果是推回去
            if(!mIsRefreshing || mHeaderView.getTopMargin()>0) {//在刷新的时候不把margin设为负值以在惯性滑动的时候能滑回去
                this.scrollBy(0, (int) delta);//禁止既滚动，又同时减少触摸
                mHeaderView.setTopMargin(mHeaderView.getTopMargin() + (int) delta);
            }
        }
        if(mHeaderView.getTopMargin()>0 && !mIsRefreshing){
            mIsHeaderReady = true;
            mHeaderView.setState(CustomHeaderView.STATE_READY);
        }//设置为ready状态
        else if(!mIsRefreshing){
            mIsHeaderReady = false;
            mHeaderView.setState(CustomHeaderView.STATE_NORMAL);
        }//设置为普通状态并且缩回去
    }

    @Override
    public void smoothScrollToPosition(final int position) {
        super.smoothScrollToPosition(position);
        final Timer scrollTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int bottomCardPosition = layoutManager.findLastVisibleItemPosition();
                if(bottomCardPosition<position+1){//如果要向下滚
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            smoothScrollBy(0,50);
                        }
                    });
                }else if(bottomCardPosition>position){//如果要向上滚
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            smoothScrollBy(0,-50);
                        }
                    });
                }else {
                    if(scrollTimer!=null)scrollTimer.cancel();
                }
            }
        };
        scrollTimer.schedule(timerTask,0,20);

    }

    /**
     * 在用户非手动强制刷新的时候，通过一个动画把头部一点点冒出来
     */
    private void smoothShowHeader(){
        if(mHeaderView==null)return;
        if(timer!=null)timer.cancel();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(mHeaderView==null){
                    if(timer!=null)timer.cancel();
                    return;
                }
                Log.i("Alex2","topMargin是"+mHeaderView.getTopMargin()+" height是"+mHeaderView.getHeight());
                if(mHeaderView.getTopMargin()<0){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsRefreshing) {//如果目前是ready状态或者正在刷新状态
                                mHeaderView.setTopMargin(mHeaderView.getTopMargin() +2);
                            }
                        }
                    });
                } else if(timer!=null){//如果已经完全缩回去了，但是动画还没有结束，就结束掉动画
                    timer.cancel();
                }
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask,0,16);
    }

    /**
     * 在用户松手的时候让头部自动收缩回去
     */
    private void resetHeaderHeight() {
        if(mHeaderView==null)mHeaderView = (CustomHeaderView) layoutManager.findViewByPosition(0);
        if(layoutManager.findFirstVisibleItemPosition()!=0){//如果刷新完毕的时候用户没有注视header
            mHeaderView.setTopMargin(-mHeaderView.getRealHeight());
            return;
        }
        if(timer!=null)timer.cancel();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(mHeaderView==null)return;
                if(mHeaderView.getTopMargin()>-mHeaderView.getRealHeight()){//如果header没有完全缩回去
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsHeaderReady || mIsRefreshing) {//如果目前是ready状态或者正在刷新状态
                                int delta = mHeaderView.getTopMargin() / 9;
                                if (delta < 5) delta = 5;
                                if (mHeaderView.getTopMargin() > 0)
                                    mHeaderView.setTopMargin(mHeaderView.getTopMargin() - delta);
                            } else {//如果是普通状态
                                mHeaderView.setTopMargin(mHeaderView.getTopMargin() - 5);
                            }
                        }
                    });
                } else if(timer!=null){//如果已经完全缩回去了，但是动画还没有结束，就结束掉动画
                    timer.cancel();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mHeaderView.setState(mHeaderView.STATE_FINISH);
                        }
                    });
                }
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask,0,10);
    }


    /**
     * 头部是通过onTouchEvent控制的
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(!mEnablePullRefresh)break;
                int delta = (int)(event.getY()-oldY);
                oldY = event.getY();
                if (layoutManager.findViewByPosition(0) instanceof CustomHeaderView) {
                    isHeader = true;
                    updateHeaderHeight(delta);//更新margin高度
                }else{
                    isHeader = false;
                    if(mHeaderView!=null && !mIsRefreshing)mHeaderView.setTopMargin(-mHeaderView.getRealHeight());
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mIsHeaderReady && !mIsRefreshing)startRefresh();
                if(isHeader)resetHeaderHeight();//抬手之后恢复高度
                break;
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return super.onTouchEvent(event);
    }

    /**
     * 因为设置了子元素的onclickListener之后，ontouch方法的down失效，所以要在分发前获取手指的位置
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("Alex", "touch down分发前");
                oldY = ev.getY();
                if (timer != null) timer.cancel();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOnRefreshListener(OnRefreshListener listener){
        this.refreshListener = listener;
    }

    /**
     * 设置是否支持下啦刷新的功能
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mIsRefreshing = false;
        mEnablePullRefresh = enable;
        if(mHeaderView==null)return;
        if (!mEnablePullRefresh) {
            mHeaderView.setOnClickListener(null);
        } else {
            mHeaderView.setState(CustomHeaderView.STATE_NORMAL);
            mHeaderView.setVisibility(VISIBLE);
        }
    }

    /**
     * 停止下拉刷新，并且通过动画让头部自己缩回去
     */
    public void stopRefresh() {
        if (mIsRefreshing == true) {
            mIsRefreshing = false;
            mIsHeaderReady = false;
            if(mHeaderView==null)return;
            mHeaderView.setState(CustomFooterView.STATE_NORMAL);
            resetHeaderHeight();
        }
    }

    /**
     * 在用户没有用手控制的情况下，通过动画把头部露出来并且执行刷新
     */
    public void forceRefresh(){
        if(mHeaderView==null)mHeaderView = (CustomHeaderView) layoutManager.findViewByPosition(0);
        if(mHeaderView!=null)mHeaderView.setState(CustomHeaderView.STATE_REFRESHING);
        mIsRefreshing = true;
        Log.i("Alex2", "现在开始强制刷新");
        mIsHeaderReady = false;
        smoothShowHeader();
        if (refreshListener != null)refreshListener.onRefresh();
    }


    private void startRefresh() {
        mIsRefreshing = true;
        mHeaderView.setState(CustomHeaderView.STATE_REFRESHING);
        Log.i("Alex2", "现在开始加载");
        mIsHeaderReady = false;
        if (refreshListener != null) refreshListener.onRefresh();

    }

    public interface OnRefreshListener{
        void onRefresh();
    }


    public static int dp2px(float density, int dp) {
        if (dp == 0) {
            return 0;
        }
        return (int) (dp * density + 0.5f);
    }


    public static abstract class WjcDragRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int TYPE_HEADER = 436874;
        private static final int TYPE_ITEM = 256478;
        private static final int TYPE_FOOTER = 9621147;

        private int ITEM;

        private ViewHolder vhItem;
        private boolean loadMore;

        private List<T> dataList;

        public List<T> getDataList() {
            return dataList;
        }

        public void setDataList(List<T> dataList) {
            this.dataList = dataList;
        }

        public WjcDragRecyclerViewAdapter(List<T> dataList, int itemLayout, boolean pullEnable){
            this.dataList = dataList;
            this.ITEM = itemLayout;
            this.loadMore = pullEnable;
        }

        public abstract ViewHolder setItemViewHolder(View itemView);

        private T getObject(int position){
            if(dataList!=null && dataList.size()>=position)return dataList.get(position-1);//如果有header
            return null;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_ITEM) {
                //inflate your layout and pass it to view holder
                View itemView = LayoutInflater.from(parent.getContext()).inflate(ITEM,null);
                this.vhItem = setItemViewHolder(itemView);
                return vhItem;
            } else if (viewType == TYPE_HEADER) {
                //inflate your layout and pass it to view holder
                View headerView = new CustomHeaderView(parent.getContext());
                return new VHHeader(headerView);
            } else if(viewType==TYPE_FOOTER){
                CustomFooterView footerView = new CustomFooterView(parent.getContext());
                return new VHFooter(footerView);
            }

            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        public void setLoadMoreEnable(boolean enable){
            this.loadMore = enable;
        }

        public boolean getLoadMoreEnable(){return loadMore;}

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {//相当于getView
            if (vhItem!=null && holder.getClass() == vhItem.getClass()) {
                //cast holder to VHItem and set data
                initItemView(holder,position-1,getObject(position));//减去头布局
            }else if (holder instanceof WjcDragRecyclerViewAdapter.VHHeader) {
                //cast holder to VHHeader and set data for header.

            }else if(holder instanceof WjcDragRecyclerViewAdapter.VHFooter){
                if(loadMore){
                    //当item数据条数较少不足以充满整个屏幕时
//                    ((VHFooter) holder).footerView.setState(CustomFooterView.STATE_NO_MORE);
                } else {
                    ((VHFooter) holder).footerView.hide();//第一次初始化显示的时候要不要显示footerView
                }
            }
        }

        @Override
        public int getItemCount() {
            return (dataList==null ||dataList.size()==0)?1:dataList.size() + 2;//如果有header,若list不存在或大小为0就没有footView，反之则有
        }//这里要考虑到头尾部，多以要加2

        /**
         * 根据位置判断这里该用哪个ViewHolder
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            if (position == 0) return TYPE_HEADER;
            else if(isPositonFooter(position)) return TYPE_FOOTER;
            return TYPE_ITEM;
        }

        private boolean isPositonFooter(int position){//这里的position从0算起
            if (dataList == null && position == 1) return true;//如果没有item
            return position == dataList.size() + 1;//如果有item(也许为0)
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

        public abstract void initItemView(ViewHolder itemHolder,int posion,T entity);

    }

}
