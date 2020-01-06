package com.mars.component.view.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.mars.component.R;

import java.lang.ref.WeakReference;

/**
 * 圆形加载框
 *
 * @author Mars
 */
public class CircleLoadingView extends View {
    private final static String TAG = CircleLoadingView.class.getSimpleName();
    /**
     * 默认属性
     */
    private int[] CIRCLELOADING_BG = new int[]{0x00FFFFFF, 0x33FFFFFF, 0x66FFFFFF, 0x99FFFFFF, 0xBBFFFFFF, 0xFFFFFFFF};
    private SweepGradient loadingSweepGradient;
    private int DEFAULT_WIDTH = (int) dip2px(50);
    private int DEFAULT_HEIGHT = (int) dip2px(50);
    private int width = DEFAULT_WIDTH;
    private int height = DEFAULT_HEIGHT;
    private float ptWidth = dip2px(2);
    private Paint mPaint;

    private MyHandler myHandler;

    private int rotate = 1;

    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleLoadingView);
        ptWidth = ta.getFloat(R.styleable.CircleLoadingView_ptWidth, ptWidth);
        ta.recycle();
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(0xFFFFFFFF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ptWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        myHandler = new MyHandler(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMySize(DEFAULT_WIDTH, widthMeasureSpec);
        height = getMySize(DEFAULT_HEIGHT, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.rotate(rotate, width / 2, height / 2);
        loadingSweepGradient = new SweepGradient(width / 2, height / 2, CIRCLELOADING_BG, null);
        mPaint.setShader(loadingSweepGradient);
        canvas.drawCircle(width / 2, height / 2, width / 2 - ptWidth, mPaint);
    }


    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        Log.e(TAG, "获取焦点或失去焦点" + hasWindowFocus);
        if (hasWindowFocus) {
            if (myHandler == null) {
                myHandler = new MyHandler(this);
            }
            myHandler.sendEmptyMessage(0x02);
        } else {
            if (myHandler == null) {
                myHandler = new MyHandler(this);
            }
            myHandler.removeMessages(0x02);
            myHandler.removeMessages(0x01);
        }
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            //如果没有指定大小，就设置为默认大小
            case MeasureSpec.UNSPECIFIED: {
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size - getPaddingLeft() - getPaddingRight();
                break;
            }
            case MeasureSpec.EXACTLY: {
                //如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
            default:
                break;
        }
        return mySize;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * DisplayMetrics类中属性density
     */
    private float dip2px(float dipValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    /**
     * 动画
     */
    private static class MyHandler extends Handler {
        WeakReference<CircleLoadingView> weakReference;

        public MyHandler(CircleLoadingView weakReferenceFr) {
            weakReference = new WeakReference<>(weakReferenceFr);
        }

        @Override
        public void handleMessage(Message msg) {
            CircleLoadingView view = weakReference.get();
            switch (msg.what) {
                case 0x01:
                    view.rotate += 18;
                    if (view.rotate > 360) {
                        view.rotate = 1;
                    }
                    view.invalidate();
                    Log.e(TAG, "通知重绘");
                    this.sendEmptyMessageDelayed(0x01, 80);
                    break;
                case 0x02:
                    this.sendEmptyMessageDelayed(0x01, 100);
                    break;
                default:
                    break;
            }
        }
    }

}
