package com.mars.component.view.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mars.component.R;
import com.mars.component.annotation.LoadingStyle;
import com.mars.component.utils.StringTools;

import java.lang.ref.WeakReference;

/**
 * @author Mars
 * 加载控件
 * 1、多种加载样式
 * 2、加载成功或失败提示（文字、文字+图形）
 * 3、加载提示文字描述
 */
public class MutiLoadingView extends View {
    private final static String TAG = MutiLoadingView.class.getSimpleName();

    /**
     * 默认值设置
     */
    private final int DEFAULT_LOADING_STYLE = LoadingStyle.StyleType.NORMAL.getStyleValue();
    private final int DEFAULT_LOADING_BG = 0x90000000;
    private final int DEFAULT_LOADING_TEXT_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_LOADING_STATUS_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_LOADING_LOADING_COLOR = 0xFFFFFFFF;
    private final int DEFAULT_WIDTH = (int) dip2px(100);
    private final int DEFAULT_HEIGHT = (int) dip2px(100);
    private final float DEFAULT_LOADING_LOADINGWIDTH = DEFAULT_WIDTH / 2;
    private final float DEFAULT_LOADING_LOADINGHEIGHT = DEFAULT_HEIGHT / 2;
    private final String DEFAULT_LOADING_LOADINGTEXT = "加载中";
    private final float DEFAULT_LOADING_LOADINGTEXT_SIZE = 20f;

    private final int[] CIRCLELOADING_BG = new int[]{0x00FFFFFF, 0x33FFFFFF, 0x66FFFFFF, 0x99FFFFFF, 0xBBFFFFFF, 0xFFFFFFFF};
    private SweepGradient loadingSweepGradient;

    /**
     * 样式属性
     */
    private int style = DEFAULT_LOADING_STYLE;
    private int show_status = 0;
    private int bg = DEFAULT_LOADING_BG;
    private int text_color = DEFAULT_LOADING_TEXT_COLOR;
    private int status_color = DEFAULT_LOADING_STATUS_COLOR;
    private int loading_color = DEFAULT_LOADING_LOADING_COLOR;
    private float loadingwidth = DEFAULT_LOADING_LOADINGWIDTH;
    private float loadingheight = DEFAULT_LOADING_LOADINGHEIGHT;
    private String text = DEFAULT_LOADING_LOADINGTEXT;
    private float textSize = DEFAULT_LOADING_LOADINGTEXT_SIZE;
    private float ptWidth = dip2px(2);
    private float width = DEFAULT_WIDTH;
    private float height = DEFAULT_HEIGHT;
    private int rotate = 1;

    private Paint mPaint;
    private Paint txtPaint;
    private Rect rect;
    private MyHandler myHandler;

    private float cx = width / 2f;
    private float cy = height / 2f;

    public MutiLoadingView(Context context) {
        this(context, null);
    }

    public MutiLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MutiLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.e(TAG, "MutiLoadingView");
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MutiLoadingView);
        style = ta.getInt(R.styleable.MutiLoadingView_mutiloadingview_loading_style, DEFAULT_LOADING_STYLE);
        Log.e(TAG, "style:" + style);

        bg = ta.getColor(R.styleable.MutiLoadingView_mutiloadingview_bg, DEFAULT_LOADING_BG);
        text_color = ta.getColor(R.styleable.MutiLoadingView_mutiloadingview_text_color, DEFAULT_LOADING_TEXT_COLOR);
        status_color = ta.getColor(R.styleable.MutiLoadingView_mutiloadingview_status_color, DEFAULT_LOADING_STATUS_COLOR);
        loading_color = ta.getColor(R.styleable.MutiLoadingView_mutiloadingview_loading_color, DEFAULT_LOADING_LOADING_COLOR);

        show_status = ta.getInt(R.styleable.MutiLoadingView_mutiloadingview_show_status, 0);

        loadingwidth = ta.getDimension(R.styleable.MutiLoadingView_mutiloadingview_loadingwidth, DEFAULT_LOADING_LOADINGWIDTH);
        loadingheight = ta.getDimension(R.styleable.MutiLoadingView_mutiloadingview_loadingheight, DEFAULT_LOADING_LOADINGHEIGHT);
        textSize = ta.getDimensionPixelSize(R.styleable.MutiLoadingView_mutiloadingview_loadingtext_size, (int) DEFAULT_LOADING_LOADINGTEXT_SIZE);
        text = ta.getString(R.styleable.MutiLoadingView_mutiloadingview_loadingtext);
        ta.recycle();
        initPaint();
        myHandler = new MyHandler(this);
    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(bg);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(ptWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        txtPaint = new Paint();
        txtPaint.setTextSize(textSize);
        txtPaint.setAntiAlias(true);
        txtPaint.setColor(text_color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e(TAG, "onMeasure");

        width = getMySize(DEFAULT_WIDTH, widthMeasureSpec);
        height = getMySize(DEFAULT_HEIGHT, heightMeasureSpec);

        loadingwidth = Math.min(loadingwidth, loadingheight);
        loadingheight = Math.min(loadingwidth, loadingheight);
        Log.e(TAG, "width:" + width);
        Log.e(TAG, "height:" + height);
        Log.e(TAG, "loadingwidth:" + loadingwidth);
        Log.e(TAG, "loadingheight:" + loadingheight);
        cx = width / 2f;
        cy = height / 2f;
        Log.e(TAG, "cx:" + cx);
        Log.e(TAG, "cy:" + cy);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw");
        /**
         * 画背景
         */
        initPaint();
        mPaint.setColor(bg);
        rect = new Rect(0, 0, (int) width, (int) height);
        canvas.drawRect(rect, mPaint);
        /**
         * 画加载框
         */
        /**
         *绘制文本
         */
        if (StringTools.strIsNotNull(text)) {
            Log.e(TAG, "文字起始位置:" + (cx - StringTools.mesureText(txtPaint, text)[0] / 2f));
            canvas.drawText(text, cx - StringTools.mesureText(txtPaint, text)[0] / 2f, cy + StringTools.mesureText(txtPaint, text)[1] / 2f, txtPaint);
        }
        canvas.drawLine(0, cy, width, cy, txtPaint);
        canvas.drawLine(cx, 0, cx, height, txtPaint);

        if (show_status == 0) {
            switch (style) {
                case 1:
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(loading_color);
                    for (int i = 0; i < 20; i++) {
                        canvas.rotate(18, cx, cy);
                        canvas.drawCircle(cx, cy - (Math.min(width, height) / 4 - ptWidth), ptWidth / (rotate / 18 == i ? 1 : 2), mPaint);
                    }
                    break;
                case 2:
                    break;
                case 0:
                default:
                    canvas.rotate(rotate, cx, cy);
                    mPaint.setStyle(Paint.Style.STROKE);
                    loadingSweepGradient = new SweepGradient(cx, cy, CIRCLELOADING_BG, null);
                    mPaint.setShader(loadingSweepGradient);
                    canvas.drawCircle(cx, cy, Math.min(width, height) / 4 - ptWidth, mPaint);
                    break;
            }
        } else {

        }
        canvas.save();

    }

    /**
     * 设置加载状态
     *
     * @param statusTxt
     * @param status
     */
    public void setStatus(String statusTxt, int status) {
        if (StringTools.strIsNotNull(statusTxt)) {
            text = statusTxt;
            show_status = status;
            postInvalidate();
        }
    }

    /**
     * 动画
     */
    private static class MyHandler extends Handler {
        WeakReference<MutiLoadingView> weakReference;

        public MyHandler(MutiLoadingView weakReferenceFr) {
            weakReference = new WeakReference<>(weakReferenceFr);
        }

        @Override
        public void handleMessage(Message msg) {
            MutiLoadingView view = weakReference.get();
            switch (msg.what) {
                case 0x01:
                    view.rotate += 18;
                    if (view.rotate > 360) {
                        view.rotate = 0;
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


    private float dip2px(float dipValue) {
        final float scale = this.getContext().getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
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

}
