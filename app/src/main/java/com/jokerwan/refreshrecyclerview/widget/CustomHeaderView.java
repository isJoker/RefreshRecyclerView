package com.jokerwan.refreshrecyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jokerwan.refreshrecyclerview.R;

import static com.jokerwan.refreshrecyclerview.widget.WjcRefreshRecyclerView.dp2px;

/**
 * Created by ${JokerWan} on 2017/11/7.
 * WeChat: wjc398556712
 * Function:
 */
public class CustomHeaderView extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_FINISH = 3;

    public float screenDensity;
    private final int ROTATE_ANIM_DURATION = 180;
    private Context mContext;

    private View mContentView;
    private View mProgressBar;
    private ImageView mArrowImageView;
    private TextView mHintTextView;
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    public CustomHeaderView(Context context) {
        super(context);
        initView(context);
    }

    public CustomHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private int mState;
    public void setState(int state) {
        if (state == mState)
            return;

        if (state == STATE_REFRESHING) { // 显示进度
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else { // 显示箭头图片
            mArrowImageView.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                    mHintTextView.setText("下拉刷新");
                }
                else if (mState == STATE_REFRESHING) {//如果是从刷新状态过来
//                        mArrowImageView.clearAnimation();
                    mArrowImageView.setVisibility(INVISIBLE);
                    mHintTextView.setText("刷新完成");
                }
                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                }
                mHintTextView.setText("刷新数据");
                break;
            case STATE_REFRESHING:
                mHintTextView.setText("正在刷新");
                break;
            case STATE_FINISH:
                mArrowImageView.setVisibility(View.VISIBLE);
                mHintTextView.setText("下拉刷新 ");
                break;
            default:
        }

        mState = state;
    }

    public void setTopMargin(int height) {
        if (mContentView==null) return ;
        LinearLayout.LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
        lp.topMargin = height;
        mContentView.setLayoutParams(lp);
    }
    //
    public int getTopMargin() {
        LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
        return lp.topMargin;
    }

    public void setHeight(int height){
        if (mContentView==null) return ;
        LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
        lp.height = height;
        mContentView.setLayoutParams(lp);
    }

    private int realHeight;

    /**
     * 得到这个headerView真实的高度，而且这个高度是自己定的
     * @return
     */
    public int getRealHeight(){
        return realHeight;
    }

    private void initView(Context context) {
        mContext = context;
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));//recyclerView里不加这句话的话宽度就会比较窄
        LinearLayout moreView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_custom_header, null);
        addView(moreView);
        moreView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mContentView = moreView.findViewById(R.id.ll_header_content);
        LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
        screenDensity = getContext().getResources().getDisplayMetrics().density;//设置屏幕密度，用来px向dp转化
        lp.height = dp2px(screenDensity,68);//头部高度75dp
        realHeight = lp.height;
        lp.topMargin = -lp.height;
        mContentView.setLayoutParams(lp);
        mArrowImageView = findViewById(R.id.img_header_arrow);
        mHintTextView = findViewById(R.id.header_hint_textview);
        mProgressBar = findViewById(R.id.header_progressbar);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }
}

