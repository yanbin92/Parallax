package com.definewidgest.parallax.view;


import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 步骤： 1 重写 overScrollBy 实现下拉
 *       2 获取ImageView的原始高度 添加header,
 *       注意 直接在setHeaderView方法中获取不到ImageView高度  使用viewtree树监听 或者onMeasure onSizeChanged
 *
 *       3 根据下拉距离动态改变ImageView的高度   实现视差特效
 *       不能让ImageView高度无限制增加 设置ImageView最大高度为图片的原始高度
 *       4 手指抬起时 使用属性动画 实现回弹效果 （重写OnTouchEvent）
 *       注意差值器(Interpolator) 估值器(TypeEvaluator) =
 * Created by yanb on 2015/12/26.
 */
public class MyParallaxListView extends ListView {
    private ImageView headerView;
    int oriingnalHeight;
    int maxHeight;
    public MyParallaxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyParallaxListView(Context context) {
        super(context);
    }

    public MyParallaxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        //
        Log.i("TAG","deltaY :" +deltaY +"scrollY :"+scrollY + "scrollRangeY :"+ scrollRangeY + "maxOverScrollY :"+maxOverScrollY
        +"isTouchEvent :"+isTouchEvent);
        //deltaY  垂直方向的顺时变化量 在顶部下拉 是负值 底部上拉是正值
        //scrollY scrollY Current Y scroll value in pixels before applying deltaY
        //scrollRangeY 垂直方向超出滑动的范围
       // maxOverScrollY 垂直方向超出滑动的最大范围
        //isTouchEvent 手指触摸滑动时是true 惯性时是false

        //如果是下拉 并且是手指触摸滑动
        if(deltaY<0&isTouchEvent){
            //将deltaY的绝对值加给HeaderView
            int newHeight=headerView.getHeight()+Math.abs(deltaY/2);
            //不能超出图片最大高度
            if(newHeight<maxHeight) {
                headerView.getLayoutParams().height = newHeight;
                requestLayout();
            }
        }


        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
                //处理回弹 动画
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(1);
                final int startValue=headerView.getHeight();
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        //0-->1
                        float fraction=animation.getAnimatedFraction();

                        int endValue=oriingnalHeight;

                        //TypeEvaluator

                        int newHeight= (int)evaluate(fraction,startValue,endValue);

                        headerView.getLayoutParams().height=newHeight;
                        requestLayout();

                    }
                });
                // Amount of overshoot. When tension equals 0.0f, there is
                //no overshoot and the interpolator becomes a simple deceleration interpolator.
                //Interpolator 差值器
                valueAnimator.setInterpolator(new OvershootInterpolator(3));
                //一定要设置时间
                valueAnimator.setDuration(500);
                valueAnimator.start();


                break;
        }

        //super.onTouchEvent(ev) 不要更改 ListView 在其中处理了滑动 等操作
        return super.onTouchEvent(ev);
    }

    public void setHeaderView(ImageView headerView) {
        this.headerView = headerView;
        oriingnalHeight = headerView.getHeight();
        //Return the intrinsic height of the underlying drawable object
        //获取图片最大高度

        maxHeight = headerView.getDrawable().getIntrinsicHeight();
        //   Log.i("TAG","oriingnalHeight"+oriingnalHeight);
    }

    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
