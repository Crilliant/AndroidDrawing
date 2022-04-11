//Created by Cao Yx on 2022.3.31
//broken line chart for the detection of the heart rate
package com.example.driverdector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BrokenLineChart extends View {
    /*定义绘图基础参数*/
    //View布局的长宽
    private int ViewHeight,ViewWidth;
    //表格的长宽
    private int ChartHeight,ChartWidth;
    //内边距
    private int PaddingLeft=80,PaddingRight=50;
    private int PaddingTop=40,PaddingBottom=40;

    //xy轴、背景横线的画笔
    private Paint BorderLinePaint;
    //圆点
    private Paint CyclePaint;
    private int radius=8;
    //折线的画笔
    private Paint LinePaint;
    //半透明背景
    private  Paint BackgroundPaint;
    //文字画笔：data，坐标
    private Paint TextPaint, CommentPaint;

    /**边框文本,正常血压在148-70,所以设置这里数据范围170-50*/
    private int[] valueText =new int[]{170,150,130,110,90,70,50};
    /**数据值*/
    private int[] data=new int[]{100,110,120,110,163,104,140};
    private String[] days=new String[]{"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};

    public BrokenLineChart(Context c){
        super(c);
    }
    public BrokenLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        ViewHeight=getMeasuredHeight();
        ViewWidth=getMeasuredWidth();
        initChartWidthHeight();
        initPaint();
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        drawBorderLineText(canvas);
        drawCycle(canvas);
        drawLineDate(canvas);
    }
    //初始化chart宽高
    private void initChartWidthHeight(){
        ChartWidth=ViewWidth-PaddingLeft-PaddingRight;
        ChartHeight=ViewHeight-PaddingTop-PaddingBottom;
    }

    //初始化画笔
    private void initPaint(){
        //文字画笔
        if(TextPaint==null)
            TextPaint=new Paint();
        initPaint(TextPaint);

        if(CommentPaint==null)
            CommentPaint=new Paint();
        initPaint(CommentPaint);

        //x/y轴画笔
        if(BorderLinePaint==null)
            BorderLinePaint=new Paint();
        initPaint(BorderLinePaint);

        //折线画笔
        if(LinePaint==null)
            LinePaint=new Paint();
        initPaint(LinePaint);
        if(BackgroundPaint==null)
            BackgroundPaint=new Paint();
        initPaint(BackgroundPaint);

        //圆圈画笔
        if(CyclePaint==null)
            CyclePaint=new Paint();
        initPaint(CyclePaint);
    }

    //画笔默认黑色实心
    private void initPaint(Paint paint){
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
    }

    //绘制x.y轴、背景横线，以及y轴文本
    private void drawBorderLineText(Canvas canvas){
        BorderLinePaint.setColor(getResources().getColor(R.color.darkGrey));

        //y axis
        canvas.drawLine(PaddingLeft,PaddingTop,
                PaddingLeft,ViewHeight-PaddingBottom,BorderLinePaint);
        //x axis
        canvas.drawLine(PaddingLeft,ViewHeight-PaddingBottom,
                ViewWidth-PaddingRight,ViewHeight-PaddingBottom,BorderLinePaint);


        //边框分段横线
        float averHeight=ChartHeight/(valueText.length-1);
        BorderLinePaint.setColor(getResources().getColor(R.color.lightGrey));
        //文字样式
        CommentPaint.setTextAlign(Paint.Align.RIGHT);//向原点左侧画
        CommentPaint.setTextSize(25f);
        CommentPaint.setColor(getResources().getColor(R.color.darkGrey));

        for(int i=0;i< valueText.length;i++){
            float nowHeight= ViewHeight - PaddingBottom - averHeight*i;
            canvas.drawLine(PaddingLeft, nowHeight,
                    ViewWidth-PaddingRight, nowHeight, BorderLinePaint);
            canvas.drawText(valueText[valueText.length - 1-i]+"",
                    PaddingLeft-10,nowHeight+5,CommentPaint);

        }
    }

    //获得一组数据值在表格的中的位置
    public Point[] getPoints(int[]values){
        int n= data.length;
        Point []points=new Point[n];
        float averWidth=ChartWidth/n;
        //TODO:这里写死chart数据范围是170-50
        float weight=ChartHeight/120;//权重

        for(int i=0;i<n;i++){
            int x=(int)(PaddingLeft+(i+1)*averWidth);
            int y=(int)(PaddingTop+ChartHeight-(values[i]-50)*weight);
            points[i]=new Point(x,y);
        }
        return points;
    }

    public void drawCycle(Canvas canvas){
        //设置橘红色实心圆点
        CyclePaint.setColor(getResources().getColor(R.color.OrangeRed));

        Point[] points=getPoints(data);

        for(int i=0;i<data.length;i++){
            canvas.drawCircle(points[i].x,points[i].y,radius,CyclePaint);
        }
    }
    //画曲线、日期、半透明的背景
    public void drawLineDate(Canvas canvas){
        //折线画笔
        LinePaint.setColor(getResources().getColor(R.color.OrangeRed));
        LinePaint.setStrokeWidth(4);
        LinePaint.setStyle(Paint.Style.STROKE);
        //背景画笔
        BackgroundPaint.setColor(getResources().getColor(R.color.OrangeRed));
        BackgroundPaint.setAlpha(50);//不透明度
        //日期画笔
        CommentPaint.setTextAlign(Paint.Align.CENTER);

        Point[] points=getPoints(data);
        Path p=new Path();
        for(int i=0;i<data.length;i++){
            if(i == 0){
                p.moveTo(points[i].x,points[i].y);
            }
            else{
                p.lineTo(points[i].x,points[i].y);
            }
            canvas.drawText(days[i],
                    points[i].x,ViewHeight-PaddingBottom+30,CommentPaint);
        }
        canvas.drawPath(p,LinePaint);

        p.lineTo(ChartWidth+PaddingLeft,ViewHeight-PaddingBottom);
        p.lineTo(points[0].x,ViewHeight-PaddingBottom);
        canvas.drawPath(p,BackgroundPaint);
    }
}
