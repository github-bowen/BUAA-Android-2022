package com.example.success.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CurveView extends View {
    private Paint mBrokenLinePaint;
    private Paint mInCirclePaint;
    private Paint mFillCirclePaint;
    private Paint mOutCirclePaint;
    private Paint mBottomLinePaint;
    private Paint mXTextPaint;
    private Paint mYLinePaint;
    private Paint mYTextPaint;
    private boolean isCurve;
    /**
     * X轴的数量,按需调整
     */
    private int mLength = 7;
    /**
     * 获取屏幕的宽度
     */
    private final int mScreenWidth = getScreenWidth(getContext());
    /**
     * 获取实际屏幕的宽度
     */
    private int cScreenWidth = getScreenWidth(getContext());
    /**
     * 整个折线图的高度=屏幕高度-顶部的状态栏高度
     */
    private final int mHeight = getScreenHeight(getContext()) - dp2px(getContext(), 190);
    /**
     * 节点外圆的半径
     */
    private final int outRadius = dp2px(getContext(), 6);
    /**
     * 节点内圆的半径
     */
    private final int inRadius = dp2px(getContext(), 3);
    /**
     * 左右两边距边缘的距离
     */
    private final float mSideLength = dp2px(getContext(), 20);
    /**
     * X轴底部文字的高度
     */
    private final int mXTextHeight = dp2px(getContext(), 30);

    /**
     * 距离上边距的高度
     */
    private final int mPaddingTop = dp2px(getContext(), 200);

    /**
     * 获取间隔距离
     */
    private int mSpaceLength;
    /**
     * 用户设置的数据
     */
    private final List<Pair<String, Float>> dataValue = new ArrayList<>();
    /**
     * 节点数据
     */
    private final List<Pair<Float, Float>> nodeValue = new ArrayList<>();
    /**
     * 节点上标注的文字
     */
    private final List<String> yValue = new ArrayList<>();
    private Paint mShadowPaint;

    public CurveView(Context context) {
        this(context, null);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        return Math.round(px);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size.x;
    }

    /*设置实际内容宽度*/
    public void setContentWidth(int size) {
        mLength = size + 1;
        cScreenWidth = (mScreenWidth / 7) * size;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        return size.y;
    }

    /**
     * 设置数据
     */
    public void setData(List<String> xAxis, List<String> yAxis) {
        dataValue.clear();
        yValue.clear();
        float yHeight = mHeight - mXTextHeight;
        for (int i = 0; i < xAxis.size(); i++) {
            yValue.add(yAxis.get(i));
            float value = Float.parseFloat(yAxis.get(i));
            dataValue.add(new Pair<>(xAxis.get(i), (yHeight - value / 200f * yHeight)));
        }
        invalidate();
        requestLayout();
    }

    private void init() {
        getSpaceLength();//获取间隔距离
        initYLine();//初始化竖直方向的线条
        initBottomLine();//初始化底部横线paint
        initInCircle();//初始化节点内圆
        initBrokenLine();//初始化曲线、折线
        initXtext();//初始化X轴标签
        initYtext();//初始化Y轴上数值
        initShadowPaint();//初始化阴影
    }

    /**
     * 获取间隔距离
     * （屏幕宽度-两边的间距）/(x轴数量-1)
     */
    private void getSpaceLength() {
        mSpaceLength = (int) (mScreenWidth - mSideLength * 2) / (mLength - 1);
    }

    /**
     * 初始化竖直方向的线条
     */
    private void initYLine() {
        mYLinePaint = new Paint();
        mYLinePaint.setColor(Color.GRAY);
        mYLinePaint.setStrokeWidth(1);
        mYLinePaint.setStyle(Paint.Style.STROKE);
        mYLinePaint.setAntiAlias(true);
    }

    /**
     * 初始化底部横线paint
     */
    private void initBottomLine() {
        mBottomLinePaint = new Paint();
        mBottomLinePaint.setColor(Color.GRAY);
        mBottomLinePaint.setStrokeWidth(dp2px(getContext(), 0.5f));
        mBottomLinePaint.setStyle(Paint.Style.STROKE);
        mBottomLinePaint.setAntiAlias(true);
    }

    /**
     * 初始化X轴标签
     */
    private void initXtext() {
        mXTextPaint = new Paint();
        mXTextPaint.setColor(Color.GRAY);
        mXTextPaint.setTextSize(dp2px(getContext(), 12));
        mXTextPaint.setStyle(Paint.Style.FILL);
        mXTextPaint.setAntiAlias(true);
    }

    /**
     * 初始化Y轴上数值
     */
    private void initYtext() {
        mYTextPaint = new Paint();
        mYTextPaint.setColor(Color.GRAY);
        mYTextPaint.setTextSize(dp2px(getContext(), 12));
        mYTextPaint.setStyle(Paint.Style.FILL);
        mYTextPaint.setAntiAlias(true);
    }

    /**
     * 初始化节点内圆
     */
    private void initInCircle() {
        //初始化外圆
        mOutCirclePaint = new Paint();
        mOutCirclePaint.setColor(Color.GREEN);
        mOutCirclePaint.setStyle(Paint.Style.FILL);
        mOutCirclePaint.setAntiAlias(true);
        //内框
        mInCirclePaint = new Paint();
        mInCirclePaint.setColor(Color.GRAY);
        mInCirclePaint.setStyle(Paint.Style.STROKE);
        mInCirclePaint.setStrokeWidth(dp2px(getContext(), 2));
        mInCirclePaint.setAntiAlias(true);
        //内圆
        mFillCirclePaint = new Paint();
        mFillCirclePaint.setColor(Color.GRAY);
        mFillCirclePaint.setStyle(Paint.Style.FILL);
        mFillCirclePaint.setAntiAlias(true);
    }

    /**
     * 初始化曲线、折线
     */
    private void initBrokenLine() {
        mBrokenLinePaint = new Paint();//折线
        mBrokenLinePaint.setColor(Color.GRAY);
        mBrokenLinePaint.setStrokeWidth(dp2px(getContext(), 2f));
        mBrokenLinePaint.setStyle(Paint.Style.STROKE);
        mBrokenLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mBrokenLinePaint.setAntiAlias(true);
    }

    /**
     * 初始化阴影
     */
    private void initShadowPaint() {
        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setAntiAlias(true);
        Shader shader = new LinearGradient(getWidth() / 2f, getHeight(), getWidth() / 2f, 0,
                Color.parseColor("#3300FF00"), Color.parseColor("#3300FF00"), Shader.TileMode.MIRROR);
        mShadowPaint.setShader(shader);
    }

    /**
     * 绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawYLine(canvas);
        // 绘制底部的X轴线
        drawBottomLine(canvas);
        // 绘制节点和折线图
        drawCircleLine(canvas);
        // 绘制Y轴上的数据和背景
        drawYtext(canvas);
        // 绘制X轴标签文字
        drawBottomText(canvas);
        //阴影
        drawShadow(canvas);
    }

    /**
     * 绘制竖直方向上的线
     */
    private void drawYLine(Canvas canvas) {
        for (int i = 0; i < dataValue.size(); i++) {
            canvas.drawLine(mSideLength + mSpaceLength * i, mPaddingTop, mSideLength + mSpaceLength * i, mHeight - mXTextHeight, mYLinePaint);
        }
    }

    /**
     * 绘制节点和曲线或者折线图
     */
    private void drawCircleLine(Canvas canvas) {
        nodeValue.clear();
        for (int i = 0; i < dataValue.size(); i++) {
            Pair<String, Float> pair = dataValue.get(i);
            // 绘制节点外圆
            canvas.drawCircle(mSideLength + mSpaceLength * i, pair.second, outRadius, mOutCirclePaint);
            // 绘制节点内框
            canvas.drawCircle(mSideLength + mSpaceLength * i, pair.second, inRadius, mFillCirclePaint);
            // 绘制节点内圆
            canvas.drawCircle(mSideLength + mSpaceLength * i, pair.second, inRadius, mInCirclePaint);
            // 保存圆心坐标
            Pair<Float, Float> pairs = new Pair<>(mSideLength + mSpaceLength * i, pair.second);
            nodeValue.add(pairs);
        }
        // drawScrollLine(canvas);//曲线
        drawLine(canvas);//折线
    }

    /**
     * 绘制曲线图
     */
    private void drawScrollLine(Canvas canvas) {
        isCurve = true;
        PointF pStart, pEnd;
        List<PointF> points = getPoints();
        Path path = new Path();
        for (int i = 0; i < points.size() - 1; i++) {
            pStart = points.get(i);
            pEnd = points.get(i + 1);
            PointF point3 = new PointF();
            PointF point4 = new PointF();
            float wd = (pStart.x + pEnd.x) / 2;
            point3.x = wd;
            point3.y = pStart.y;
            point4.x = wd;
            point4.y = pEnd.y;
            path.moveTo(pStart.x, pStart.y);
            path.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y);
            canvas.drawPath(path, mBrokenLinePaint);
        }
    }

    /**
     * 绘制折线
     */
    private void drawLine(Canvas canvas) {
        isCurve = false;
        for (int i = 0; i < nodeValue.size(); i++) {
            if (i != nodeValue.size() - 1) {
                canvas.drawLine((float) nodeValue.get(i).first,
                        (float) nodeValue.get(i).second,
                        (float) nodeValue.get(i + 1).first,
                        (float) nodeValue.get(i + 1).second, mBrokenLinePaint);
            }
        }
    }

    /**
     * 绘制阴影
     */
    private void drawShadow(Canvas canvas) {
        List<PointF> points = getPoints();
        if (isCurve) {//曲线
            PointF pStart, pEnd;
            Path path = new Path();
            for (int i = 0; i < points.size() - 1; i++) {
                pStart = points.get(i);
                pEnd = points.get(i + 1);
                PointF point3 = new PointF();
                PointF point4 = new PointF();
                float wd = (pStart.x + pEnd.x) / 2;
                point3.x = wd;
                point3.y = pStart.y;
                point4.x = wd;
                point4.y = pEnd.y;
                path.moveTo(pStart.x, pStart.y);
                path.cubicTo(point3.x, point3.y, point4.x, point4.y, pEnd.x, pEnd.y);
                //减去文字和指示标的高度
                path.lineTo(pEnd.x, getHeight() - mXTextHeight);
                path.lineTo(pStart.x, getHeight() - mXTextHeight);
            }
            path.close();
            canvas.drawPath(path, mShadowPaint);
        } else {
            Path path = new Path();
            path.moveTo(points.get(0).x, points.get(0).y);
            for (int i = 1; i < points.size(); i++) {
                path.lineTo(points.get(i).x, points.get(i).y);
            }
            //链接最后两个点
            int index = points.size() - 1;
            path.lineTo(points.get(index).x, points.get(0).y);
            path.lineTo(points.get(0).x, points.get(0).y);
            path.close();
            canvas.drawPath(path, mShadowPaint);
        }
    }

    /**
     * 获取坐标点
     */
    private List<PointF> getPoints() {
        ArrayList<PointF> points = new ArrayList<>();
        for (Pair<Float, Float> pair : nodeValue) {
            points.add(new PointF((float) pair.first, (float) pair.second));
        }
        return points;
    }

    /**
     * 绘制底部的X轴线
     */
    private void drawBottomLine(Canvas canvas) {
        canvas.drawLine(0, mHeight - mXTextHeight, cScreenWidth, mHeight - mXTextHeight, mBottomLinePaint);
    }

    /**
     * 绘制X轴标签文字
     */
    private void drawBottomText(Canvas canvas) {
        for (int i = 0; i < dataValue.size(); i++) {
            String xValue = dataValue.get(i).first;
            // 获取Text内容宽度
            Rect bounds = new Rect();
            mXTextPaint.getTextBounds(xValue, 0, xValue.length(), bounds);
            int width = bounds.right - bounds.left;
            canvas.drawText(xValue, mSideLength - width / 2f + mSpaceLength * i, mHeight - mXTextHeight / 2f, mXTextPaint);
        }
    }

    /**
     * 绘制Y轴上的数据和背景
     */
    private void drawYtext(Canvas canvas) {
        for (int i = 0; i < dataValue.size(); i++) {
            Pair<String, Float> pair = dataValue.get(i);
            // 用Rect计算Text内容宽度
            Rect bounds = new Rect();
            mYTextPaint.getTextBounds(pair.first, 0, pair.first.length(), bounds);
            int textWidth = bounds.right - bounds.left;
            // 绘制节点上的文字
            canvas.drawText(yValue.get(i), mSideLength + mSpaceLength * i - textWidth / 2f, pair.second - 25, mYTextPaint);
        }
    }
}
