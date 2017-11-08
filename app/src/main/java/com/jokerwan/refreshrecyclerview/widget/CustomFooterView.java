package com.jokerwan.refreshrecyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jokerwan.refreshrecyclerview.R;

/**
 * Created by ${JokerWan} on 2017/11/7.
 * WeChat: wjc398556712
 * Function:
 */

public class CustomFooterView extends LinearLayout{

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_ERROR = 3;
    public final static int STATE_NO_MORE = 4;

    private Context mContext;

    private View mContentView;
    private LinearLayout layoutLoadmore;
    private TextView mHintView;

    public CustomFooterView(Context context) {
        super(context);
        initView(context);
    }

    public CustomFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    public void setState(int state) {
        layoutLoadmore.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText("松手加载更多");
        } else if (state == STATE_LOADING) {
            layoutLoadmore.setVisibility(View.VISIBLE);
            mHintView.setVisibility(INVISIBLE);
        } else if(state == STATE_ERROR){
            layoutLoadmore.setVisibility(GONE);
        } else if(state == STATE_NO_MORE) {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText("没有更多数据了");

        } else {
            mHintView.setVisibility(View.VISIBLE);
            mHintView.setText("上拉加载更多");
        }
    }

    public void setBottomMargin(int height) {
        if (height < 0) return ;
        LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
        lp.bottomMargin = height;
        mContentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
        return lp.bottomMargin;
    }


    /**
     * normal status
     */
    public void normal() {
        mHintView.setVisibility(View.VISIBLE);
        layoutLoadmore.setVisibility(View.GONE);
    }


    /**
     * loading status
     */
    public void loading() {
        mHintView.setVisibility(View.GONE);
        layoutLoadmore.setVisibility(View.VISIBLE);
    }

    private void initView(Context context) {
        mContext = context;
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_custom_footer, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mContentView = moreView.findViewById(R.id.rlContentView);
        layoutLoadmore = moreView.findViewById(R.id.loadContentView);
        mHintView = moreView.findViewById(R.id.ctvContentView);
        mHintView.setText("load more");
//            layoutLoadmore.setVisibility(VISIBLE);//一直会显示转圈，自动加载更多时使用
    }
}

