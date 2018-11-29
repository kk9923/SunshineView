package com.kx.sunshineview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by admin on 2018/11/28.
 *  参考 link{https://www.jianshu.com/p/2c9dc35f3aad}
 *  超萌动感天气小太阳
 */
public class SunshineView extends View {
    private  Context mContext ;
    private  int mWidth ;
    private  int mHeight ;
    private  Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //  第一步 外圆环的颜色
    private int mOutRingColor = Color.argb(255,251, 203, 64);
    //  第一步 外圆环的半径
    private float mOutRingWidth ;
    //  第一步 内圆环的颜色
    private int mInnerRingColor = Color.argb(255,255, 255, 255);
    //  第一步 内圆环的半径
    private float mInnerRingWidth ;
    //  第一步  圆环放大缩小动画时间
    private int ZOOM_RING_DURATION_TIME = 300;

    //   第二步 外圆弧的宽度
    private float mOutArcStrokeWidth ;
    //   第二步 内圆弧的宽度
    private float mInnerArcStrokeWidth ;
    //   第二步 外圆弧的所在RectF
    private RectF mOutArcRectF;
    //   第二步 内圆弧的所在RectF
    private RectF mInnerArcRectF;
    //   第二步 外圆环的起始角度
    private int mOutArcStartAngle = 270;
    //   第二步 外圆环的旋转角度
    private int mOutArcSweepAngle = -90;
    //   第二步 内圆环的起始角度
    private int mInnerArcStartAngle = -92;
    //   第二步 外圆环的旋转角度
    private int mInnerArcSweepAngle = 2;
    private AnimatorSet mAnimatorSet;

    public SunshineView(Context context) {
        this(context,null);
    }

    public SunshineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SunshineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mInnerRingWidth = dp2px(mContext,2);
        mOutRingWidth = dp2px(mContext,4);
        mOutArcStrokeWidth = dp2px(mContext,8);
        mInnerArcStrokeWidth = dp2px(mContext,8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = 0;
        if (width> height){
            size = height;
        }else {
            size = width;
        }
        setMeasuredDimension(size,size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mOutArcRectF = new RectF(-mWidth/4,-mWidth/4,mWidth/4,mWidth/4);
        mInnerArcRectF = new RectF(-mWidth/8,-mWidth/8,mWidth/8,mWidth/8);
        ValueAnimator outRingAnimator = ValueAnimator.ofFloat(mOutRingWidth,w / 4 );
        outRingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 改变 第一步 外圆的半径
                mOutRingWidth= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator innerRingAnimator = ValueAnimator.ofFloat(mInnerRingWidth, w / 4  - dp2px(mContext,1));
        innerRingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 改变 第一步 内圆的半径
                mInnerRingWidth= (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        innerRingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                //  第一步的动画结束后开始绘制 圆环动画
                isSecond  = true;
            }
        });
        mAnimatorSet = new AnimatorSet().setDuration(ZOOM_RING_DURATION_TIME);
        ValueAnimator outRingAnimator1 = ValueAnimator.ofInt(0,180);
        outRingAnimator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mInnerArcSweepAngle = value;

                mOutArcSweepAngle = -90 - value / 2 ;

                mInnerArcStartAngle = -90 + value;
                mOutArcStartAngle = 270 - value  ;

                invalidate();
            }
        });
        outRingAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mOutArcStartAngle = -90 ;
                mInnerArcStartAngle  = -90;
            }
        });
        final ValueAnimator outRingAnimator2 = ValueAnimator.ofInt(0,180);
        outRingAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();

                mInnerArcSweepAngle = value;

                mOutArcSweepAngle = 180-value ;

                mInnerArcSweepAngle = value - 180;

                invalidate();
            }
        });
        mAnimatorSet.playSequentially(outRingAnimator,innerRingAnimator,outRingAnimator1,outRingAnimator2);
       // mAnimatorSet.play(innerRingAnimator).after(outRingAnimator).before(outRingAnimator1).after(outRingAnimator2);
        mAnimatorSet.start();
    }
private  boolean isSecond = false;
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getWidth()/2,getHeight()/2);
        if (!isSecond) {
            drawZoomRing(canvas);
        }else {
            drawWhirligigArc(canvas);
        }
    }

    /**
     * 1.第一步: 圆环放大缩小消失效果
     * @param canvas 画笔
     */
    private void drawZoomRing(Canvas canvas) {
        mPaint.setShader(null);
        mPaint.setStrokeWidth(0);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mOutRingColor);
        //外圆大圆
        canvas.drawCircle(0,0,mOutRingWidth,mPaint);
        mPaint.setColor(mInnerRingColor);
        //内圆小圆
        canvas.drawCircle(0,0,mInnerRingWidth,mPaint);
    }

    /**
     * 2. 第二步：圆弧转动缩小动画
     * @param canvas 画笔
     */
    private void drawWhirligigArc(Canvas canvas) {
        mPaint.setColor(mOutRingColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mOutArcStrokeWidth);
        canvas.drawArc(mOutArcRectF,mOutArcStartAngle,mOutArcSweepAngle,false,mPaint);
        mPaint.setStrokeWidth(mInnerArcStrokeWidth);
        canvas.drawArc(mInnerArcRectF,mInnerArcStartAngle,mInnerArcSweepAngle,false,mPaint);
    }

    public void reLoad(){
        mInnerRingWidth = dp2px(mContext,2);
        mOutRingWidth = dp2px(mContext,4);
        mOutArcStartAngle = 270;
        mOutArcSweepAngle = -90;
        mInnerArcStartAngle = -92;
        mInnerArcSweepAngle = 2;
        isSecond = false;
        mAnimatorSet.start();
    }

    private int dp2px(Context context, int dpValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,context.getResources().getDisplayMetrics());
    }
    private int sp2px(Context context,int spValue){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,spValue,context.getResources().getDisplayMetrics());
    }
}
