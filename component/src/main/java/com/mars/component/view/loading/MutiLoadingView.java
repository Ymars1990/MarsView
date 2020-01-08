package com.mars.component.view.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.mars.component.R;
import com.mars.component.annotation.LoadingStatus;
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
    private final float DEFAULT_LOADING_LOADINGTEXT_SIZE = 24f;

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
    private int rotate = 0;

    private Paint mPaint;
    private Paint txtPaint;
    private Rect rect;
    private MyHandler myHandler;
    private Path path;

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
        if (myHandler == null) {
            myHandler = new MyHandler(this);
        }
        myHandler.sendEmptyMessage(0x02);
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

/*    @Override
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
    }*/

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
        float radius = Math.min(width, height) / 4 - ptWidth;
        float offsetY = (StringTools.strIsNotNull(text) ? dip2px(10f) : 0);
        if (StringTools.strIsNotNull(text)) {
            Log.e(TAG, "文字宽度:" + StringTools.mesureText(txtPaint, text)[0]);
            if (textSize > DEFAULT_LOADING_LOADINGTEXT_SIZE * 2) {
                textSize = DEFAULT_LOADING_LOADINGTEXT_SIZE;
            }
            txtPaint.setTextSize(textSize);
            txtPaint.setColor(text_color);
            Log.e(TAG, String.format("文字起始位置--> X:%s  Y:%s", (cx - StringTools.mesureText(txtPaint, text)[0] / 2f), cy + StringTools.mesureText(txtPaint, text)[1] / 2f));
            canvas.drawText(text, cx - StringTools.mesureText(txtPaint, text)[0] / 2f, cy + Math.min(width, height) / 4 - ptWidth + dip2px(10f), txtPaint);
        }
        //用于对齐测试
//        canvas.drawLine(0, cy, width, cy, txtPaint);
//        canvas.drawLine(cx, 0, cx, height, txtPaint);

        if (show_status == LoadingStatus.StatusType.LOADING.getStyleValue()) {
            switch (style) {
                case 1:
                    mPaint.setStrokeWidth(ptWidth);
                    mPaint.setColor(loading_color);
                    canvas.rotate(rotate, cx, cy - offsetY);
                    for (int i = 0; i < 16; i++) {
                        canvas.save();
                        mPaint.setAlpha(15 * i);
                        canvas.rotate(360f / 16f * (i), cx, cy - offsetY);
                        canvas.drawCircle(cx, cy - offsetY - (radius), ptWidth * (i + 1) / 16, mPaint);
                        canvas.restore();
                    }
                    break;
                case 2:
                    mPaint.setColor(loading_color);
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setStrokeWidth(ptWidth);
                    canvas.rotate(rotate, cx, cy - offsetY);
                    path = new Path();
                    for (int i = 0; i < 16; i++) {
                        canvas.save();
                        mPaint.setStrokeWidth(ptWidth);
                        mPaint.setAlpha(15 * i);
                        canvas.rotate(360f / 16f * (i), cx, cy - offsetY);
                        path.moveTo(cx, cy - offsetY - Math.min(width, height) / 4);
                        path.quadTo(cx + dip2px(6) / 2, cy - offsetY - Math.min(width, height) / 4 + dip2px(10) / 2f, cx, cy - offsetY - Math.min(width, height) / 4 + dip2px(10));
                        canvas.drawPath(path, mPaint);
                        path.moveTo(cx, cy - offsetY - Math.min(width, height) / 4);
                        path.quadTo(cx - dip2px(6) / 2, cy - offsetY - Math.min(width, height) / 4 + dip2px(10) / 2f, cx, cy - offsetY - Math.min(width, height) / 4 + dip2px(10));
                        canvas.drawPath(path, mPaint);
                        canvas.restore();
                    }
                    break;
                case 0:
                default:
                    canvas.rotate(rotate, cx, cy - offsetY);
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setStrokeWidth(ptWidth);
                    loadingSweepGradient = new SweepGradient(cx, cy, CIRCLELOADING_BG, null);
                    mPaint.setShader(loadingSweepGradient);
                    canvas.drawCircle(cx, cy - offsetY, radius, mPaint);
                    break;
            }
        } else if (show_status == LoadingStatus.StatusType.DISMISS.getStyleValue()) {
        } else {
            Log.e(TAG, "rotate:" + rotate);
            if (myHandler != null && rotate == 360) {
                myHandler.removeMessages(0x02);
                myHandler.removeMessages(0x01);
            }
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(ptWidth);
            mPaint.setColor(status_color);
            canvas.drawCircle(cx, cy - offsetY, radius, mPaint);
            if (show_status == LoadingStatus.StatusType.SUCCESS.getStyleValue()) {
                //成功 画两条线
                //第一条线
                if (rotate / 36 <= 5) {
                    canvas.drawLine(cx - radius / 3f - radius / 6f, cy - offsetY, cx - radius / 3f - radius / 6f + radius / 3f * rotate / 36f / 5f, cy - offsetY + radius / 3f * rotate / 36f / 5f, mPaint);
                } else {
                    //第二条线
                    canvas.drawLine(cx - radius / 3f - radius / 6f, cy - offsetY, cx - radius / 6f, cy - offsetY + radius / 3f, mPaint);
                    canvas.drawLine(cx - radius / 6f, cy - offsetY + radius / 3f, cx - radius / 6f + radius * 2 / 3f * (rotate - 180) / 36f / 5f, cy - offsetY + radius / 3f - radius * 2 / 3f * (rotate - 180) / 36 / 5, mPaint);
                }
            } else if (show_status == LoadingStatus.StatusType.FAILED.getStyleValue()) {
                //失败 画两条线
                //第一条线
                if (rotate / 36 <= 5) {
                    canvas.drawLine(cx + radius / 3f, cy - offsetY - radius / 3f, cx + radius / 3f - radius * 2 / 3f * (rotate / 36f / 5f), cy - offsetY - radius / 3f + radius * 2 / 3f * (rotate / 36f / 5f), mPaint);
                } else {
                    //第二条线
                    canvas.drawLine(cx + radius / 3f, cy - offsetY - radius / 3f, cx - radius / 3f, cy - offsetY + radius / 3f, mPaint);
                    canvas.drawLine(cx - radius / 3f, cy - offsetY - radius / 3f, cx - radius / 3f + radius * 2 / 3f * ((rotate - 180) / 36f / 5f),
                            cy - offsetY - radius / 3f + radius * 2 / 3f * ((rotate - 180) / 36f / 5f), mPaint);
                }
            }
        }
    }

    /**
     * 设置加载文字
     */
    public void setStyle(LoadingStyle style) {
        this.style = style.getStyleValue();
        rotate = 0;
        myHandler.removeMessages(0x01);
        myHandler.removeMessages(0x02);
        myHandler.sendEmptyMessage(0x02);
    }

    /**
     * 设置加载文字
     */
    public void setText(String txt) {
        if (StringTools.strIsNotNull(txt)) {
            text = txt;
            rotate = 0;
            myHandler.removeMessages(0x01);
            myHandler.removeMessages(0x02);
            myHandler.sendEmptyMessage(0x02);
        }
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float size) {
        textSize = size;
        rotate = 0;
        myHandler.removeMessages(0x01);
        myHandler.removeMessages(0x02);
        myHandler.sendEmptyMessage(0x02);
    }

    /**
     * 设置文字大小
     */
    public void setText_color(int text_color) {
        this.text_color = text_color;
        rotate = 0;
        myHandler.removeMessages(0x01);
        myHandler.removeMessages(0x02);
        myHandler.sendEmptyMessage(0x02);
    }

    /**
     * 设置文字大小
     */
    public void setStatus_color(int status_color) {
        this.status_color = status_color;
        rotate = 0;
        myHandler.removeMessages(0x01);
        myHandler.removeMessages(0x02);
        myHandler.sendEmptyMessage(0x02);
    }

    /**
     * 设置加载状态和文字
     *
     * @param statusTxt
     * @param status
     */
    public void setStatus(String statusTxt, LoadingStatus status) {
        if (StringTools.strIsNotNull(statusTxt)) {
            text = statusTxt;
        }
        setStatus(status);
    }

    /**
     * 设置加载状态
     */
    public void setStatus(LoadingStatus status) {
        if (myHandler == null) {
            myHandler = new MyHandler(this);
        }
        myHandler.removeMessages(0x01);
        myHandler.removeMessages(0x02);
        Log.e(TAG, status.toString());

        show_status = status.getStyleValue();
        if (show_status == LoadingStatus.StatusType.LOADING.getStyleValue()) {
            myHandler.sendEmptyMessage(0x02);
        } else if (show_status == LoadingStatus.StatusType.DISMISS.getStyleValue()) {
            Log.e(TAG, "无状态");
            postInvalidate();
        } else {
            rotate = 0;
            myHandler.sendEmptyMessage(0x02);
        }
    }

    /**
     * 清空画布
     *
     * @param canvas
     */
    private void clear(Canvas canvas) {
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        invalidate();
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
                    view.rotate += (view.show_status == 2 || view.show_status == 1) ? 18 * 2 : 18;
                    if (view.rotate > 360) {
                        view.rotate = 0;
                    }
                    view.invalidate();
                    Log.e(TAG, "通知重绘");
                    this.sendEmptyMessageDelayed(0x01, 50);
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
