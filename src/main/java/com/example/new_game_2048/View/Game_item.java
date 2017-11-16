package com.example.new_game_2048.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by silent on 17-6-7.
 */

public class Game_item extends View {
    private int item_number;
    private String item_number_str;

    private Paint mPain;
    private Rect item_rect;

    public Game_item(Context context) {
     this(context,null);
    }

    public Game_item(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public Game_item(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPain=new Paint();//画笔
    }
    //获取item上的数字
    public int getItem_number() {
        return item_number;
    }

    //设置item上的数字
    public void setItemNumber(int number){
        item_number=number;
        item_number_str=item_number+"";
        mPain.setTextSize(100);
        item_rect=new Rect();
        //获得文字矩形边框
        mPain.getTextBounds(item_number_str,0,item_number_str.length(),item_rect);
        invalidate();//刷新view
    }
    //绘制item
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String BgColor="#FFFFFF";
        switch (item_number){
            case 0:
                BgColor="#FFDDAA";
                break;
            case 2:
                BgColor="#BBFFEE";
                break;
            case 4:
                BgColor="#33FF33";
                break;
            case 8:
                BgColor="#227700";
                break;
            case 16:
                BgColor="#33CCFF";
                break;
            case 32:
                BgColor="#5555FF";
                break;
            case 64:
                BgColor="#0066FF";
                break;
            case 128:
                BgColor="#FFFF77";
                break;
            case 256:
                BgColor="#FF7744";
                break;
            case 512:
                BgColor="#FF3333";
                break;
            case 1024:
                BgColor="#FF0000";
                break;
            case 2048:
                BgColor="#FFFFFF";
                break;
            default:
                BgColor="#000000";
                break;
        }
        mPain.setColor(Color.parseColor(BgColor));
        mPain.setStyle(Paint.Style.FILL);//设置背景样式为：填充
        //绘制矩形背景
        canvas.drawRect(0,0,getWidth(),getHeight(),mPain);
        //绘制文字（先绘制文字会被背景覆盖）
        if (item_number!=0){
            mPain.setColor(Color.BLACK);
            float x=(getWidth()-item_rect.width())/2;
            float y=getHeight()/2+item_rect.height()/2;
            canvas.drawText(item_number_str,x,y,mPain);
        }
    }
}
