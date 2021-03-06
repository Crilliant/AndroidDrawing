//Created by Cao Yx on 2022.3.8
//Column chart for the detection of the blood pressure
package com.example.driverdector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ColumnChart extends View {
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
    //数据柱的画笔
    private Paint ColumnPaint;
    //文字画笔：data，坐标
    private Paint TextPaint, CommentPaint;

    /**边框文本,正常血压在148-70,所以设置这里数据范围170-50*/
    private int[] valueText =new int[]{170,150,130,110,90,70,50};
    /**数据值*/
    private int[] maxValue=new int[]{100,110,120,110,103,104,140};
    private int[] minValue=new int[]{70,90,80,69,83,85,72};
    private String[] days=new String[]{"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};


    public ColumnChart(Context c){
        super(c);
    }
    public ColumnChart(Context context, @Nullable AttributeSet attrs) {
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
        drawColumn(canvas);
    }



    //初始化chart宽高
    private void initChartWidthHeight(){
        ChartWidth=ViewWidth-PaddingLeft-PaddingRight;
        ChartHeight=ViewHeight-PaddingTop-PaddingBottom;
    }

    //初始化画笔
    private void initPaint(){
        // 文字画笔
        if(TextPaint==null)
            TextPaint=new Paint();
        initPaint(TextPaint);
        if(CommentPaint==null)
            CommentPaint=new Paint();
        initPaint(CommentPaint);

        // x/y轴画笔
        if(BorderLinePaint==null)
            BorderLinePaint=new Paint();
        initPaint(BorderLinePaint);

        // column画笔
        if(ColumnPaint==null)
            ColumnPaint=new Paint();
        initPaint(ColumnPaint);
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

    //绘制column and date
    private void drawColumn(Canvas canvas){
        //设置橘红色圆角线格式
        ColumnPaint.setColor(getResources().getColor(R.color.OrangeRed));
        ColumnPaint.setStrokeCap(Paint.Cap.ROUND);//stroke，笔画；这里设置圆角线帽
        ColumnPaint.setStrokeWidth(30);

        //文字
        TextPaint.setTextAlign(Paint.Align.CENTER);
        TextPaint.setColor(getResources().getColor(R.color.OrangeRed));
        TextPaint.setTextSize(20f);

        //日期
        CommentPaint.setTextAlign(Paint.Align.CENTER);

        //获取column点坐标
        Point[] maxPoints=getPoints(maxValue);
        Point[] minPoints=getPoints(minValue);
        for(int i=0;i<maxValue.length;i++){
            canvas.drawText(maxValue[i]+"",//max value
                    maxPoints[i].x,maxPoints[i].y-30,TextPaint);
            canvas.drawText(minValue[i]+"",//min value text
                    minPoints[i].x,minPoints[i].y+45,TextPaint);
            canvas.drawText(days[i],
                    maxPoints[i].x,ViewHeight-PaddingBottom+30,CommentPaint);

            canvas.drawLine(maxPoints[i].x,maxPoints[i].y,
                    minPoints[i].x,minPoints[i].y,ColumnPaint);


        }
    }

    //获得一组数据值在表格的中的位置
    public Point[] getPoints(int[]values){
        int n= maxValue.length;
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

}
