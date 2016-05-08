package xiaojianming.testsurfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by xjm on 16/5/8.
 */
public class ColorPicker extends SurfaceView implements SurfaceHolder.Callback {
    private Paint imagePaint;        //定义画笔
    private SurfaceHolder sfh;
    private float touchX, touchY;
    private Bitmap openBitmap;
    private Bitmap colorBitmap;
    private Bitmap pointBitmap;
    private float[] colorHSV = new float[]{0f, 1f, 1f};
    private int dotPointeRadius = 15;   //触摸点小圆半径
    private float colorWheelRadious;   //整个半径
    private float centX;
    private float openBitMapWidth;
    private int color;   //当前的颜色值

    public ColorPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ColorPicker(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        dotPointeRadius = (int) (dotPointeRadius * getContext().getResources()
                .getDisplayMetrics().density);

        sfh = this.getHolder();  // 获取holder
        setZOrderOnTop(true);  // 设置SurfaceView背景透明
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        sfh.addCallback(this);   //给SurfaceView当前的持有者一个回调对象。
        //初始化画笔
        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        imagePaint.setDither(true);
        imagePaint.setFilterBitmap(true);
        imagePaint.setStyle(Paint.Style.FILL);
        //初始化当前颜色值
        color = Color.RED;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centX = w / 2.0f;
        colorWheelRadious = w * 0.385f;   //触摸的边缘 *0.8*0.5
        touchX = centX - colorWheelRadious;
        touchY = h * 0.5f;   //初始化在中间
        openBitMapWidth = w * 0.22f;   //这个比例是进行估算的
        //修正图片大小
        Bitmap openBitmapRes = BitmapFactory.decodeResource(getResources(), R.drawable.light_button);
        openBitmap = Bitmap.createScaledBitmap(openBitmapRes,
                (int) (openBitMapWidth * 2),
                (int) (openBitMapWidth * 2), true);
        openBitmapRes.recycle();   //回收BitMap资源

        Bitmap colorBitmapRes = BitmapFactory.decodeResource(getResources(), R.drawable.color_round);
        colorBitmap = Bitmap.createScaledBitmap(colorBitmapRes, w, w, true);
        colorBitmapRes.recycle();

        Bitmap pointBitmapRes = BitmapFactory.decodeResource(getResources(), R.drawable.color_picker_knob);
        pointBitmap = Bitmap.createScaledBitmap(pointBitmapRes, dotPointeRadius * 2, dotPointeRadius * 2, true);
        pointBitmapRes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        float cx = x - centX;
        float cy = y - centX;
        double degrees = Math.atan2(cy, cx);
        //根据HSV空间理论算出颜色
        colorHSV[0] = (float) Math.toDegrees(degrees) + 180;
        color = Color.HSVToColor(colorHSV);
//        Log.v("test", "计算出颜色:" + Color.red(color)+"    "+Color.green(color)+"     "+Color.blue(color));
        //根据反三角函数计算出映射在固定圆上
        touchX = centX + (float) Math.cos(degrees) * colorWheelRadious;
        touchY = centX + (float) Math.sin(degrees) * colorWheelRadious;
        //开始进行重绘
        DrawView();
        //进行事件回调通知外部当前颜色改变
        if (mWheelColorChangerListener != null) {
            mWheelColorChangerListener.onWheelColorChangle(color);
        }
        return true;
    }

    private void DrawView() {
        Canvas canvas = sfh.lockCanvas();   //锁定画布
        //这里进行绘制图形
        //1.进行清屏
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //2.画颜色图片
        canvas.drawBitmap(colorBitmap, 0, 0, imagePaint);
        //3.画触摸点
        canvas.drawBitmap(pointBitmap, touchX - dotPointeRadius,
                touchY - dotPointeRadius, imagePaint);
        //画中间按钮图片
        canvas.drawBitmap(openBitmap, centX - openBitMapWidth, centX - openBitMapWidth, imagePaint);
        sfh.unlockCanvasAndPost(canvas);    //解除锁定画布
    }

    //外部获取当前颜色
    public int getCurrentColor() {
        return color;
    }

    //外部设置当前颜色
    public void setCurrentColor(int color) {
        //根据三角函数算出当前颜色角度值
        float[] colorHSV = new float[3];
        Color.colorToHSV(color, colorHSV);
        //只需要获取当前颜色角度
        this.colorHSV[0] = colorHSV[0];
        //把弧度制角度转为角度制
        float angle = (colorHSV[0] - 180) / 57.29577951f;
        //根据颜色角度计算当前的X,Y值
        touchX = centX + (float) Math.cos(angle) * colorWheelRadious;
        touchY = centX + (float) Math.sin(angle) * colorWheelRadious;
        DrawView();   //进行重绘视图
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //创建的时候初始化一下
        DrawView();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public interface WheelColorChangerListener {
        void onWheelColorChangle(int color);
    }

    private WheelColorChangerListener mWheelColorChangerListener;

    public void setOnWheelColorChangerListener(
            WheelColorChangerListener mWheelColorChangerListener) {
        this.mWheelColorChangerListener = mWheelColorChangerListener;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //当View不再显示在Windows上需要释放BitMap对象   避免内存泄露
        colorBitmap.recycle();
        pointBitmap.recycle();
        openBitmap.recycle();
    }
}
