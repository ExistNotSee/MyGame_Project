package com.example.new_game_2048.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.example.new_game_2048.OnGame2048Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by silent on 17-6-7.
 */

public class Game_UI extends RelativeLayout {
    //设置最短滑动距离
    private final int FLING_MIN_DISTANCE = 50;
    private int
            NumToNum = 4,//初始数字个数
            item_sum = 4,//item总数
            item_Padding,//内边距
            item_Margin = 10,//外边距
            record_score;//记录分数

    /*
    * 运动方向定义
    * 上右下左-->1234
    * */
    private final int
            TOP = 1,
            RIGHT = 2,
            BOTTOM = 3,
            LEFT = 4;

    private boolean isMoveHappen = true;//移动
    private boolean isMergeHappen = true;//合并
    private boolean isOnce = true;//初始化执行
    private Game_item[] game_item_array;

    private OnGame2048Listener onGame2048Listener;
    private GestureDetector gestureDetector;

    public Game_UI(Context context) {
        this(context, null);
    }

    public Game_UI(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Game_UI(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //将非标准尺度单位转换为Android标准尺度单位（px）
        item_Margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, item_Margin, getResources().getDisplayMetrics());
        //取最小值作为padding
        item_Padding = min_num(getPaddingLeft(), getPaddingBottom(), getPaddingRight(), getPaddingTop());
        gestureDetector = new GestureDetector(context, new MyGestureDetector());
    }

    public void setOnGame2048Listener(OnGame2048Listener onGame2048Listener) {
        this.onGame2048Listener = onGame2048Listener;
    }

    /*
    * 测量layout的宽高，以及设置item的宽高，
    * 忽略warp_content,以宽、高中最小值来绘制正方形
    * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获得Game_UI边长
        int length = Math.min(getMeasuredHeight(), getMeasuredWidth());
        //获得item宽度
        int childWidth = (length - item_Padding * 2 - item_Margin * (item_sum - 1)) / item_sum;

        if (isOnce) {
            if (game_item_array == null)
                game_item_array = new Game_item[item_sum * item_sum];

            //放置item
            for (int i = 0; i < game_item_array.length; i++) {
                Game_item game_item = new Game_item(getContext());
                game_item_array[i] = game_item;
                game_item.setId(i + 1);
                RelativeLayout.LayoutParams rl = new LayoutParams(childWidth, childWidth);
                //设置横向边距，忽略最后一列
                if ((i + 1) % item_sum != 0) {
                    rl.rightMargin = item_Margin;
                }
                //在不是第一行和最后一行的前提下设置纵向边距
                if ((i + 1) > item_sum) {
                    rl.topMargin = item_Margin;
                    //相对于i-item_sum个item的下面
                    rl.addRule(RelativeLayout.BELOW, game_item_array[i - item_sum].getId());
                }
                //设置横向布局，item摆放位置
                if (i % item_sum != 0) {
                    rl.addRule(RelativeLayout.RIGHT_OF, game_item_array[i - 1].getId());
                }
                addView(game_item, rl);
            }
            //开始游戏生成最少数字个数
            for (int i = 0; i < NumToNum; i++) {
                isMergeHappen = isMoveHappen = true;
                RandomNumber();
            }
        }
        isOnce = false;
        setMeasuredDimension(length, length);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }

    //随机产生一个数字
    private void RandomNumber() {
        /*
        * 判断是否全部位置都有数字
        * 否 随机产生一个数字
        * 是 跳出方法
        * */
        if (!isFull()) {
            //随机item内产生一个数字
            if (isMergeHappen || isMoveHappen) {
                Random random = new Random();
                int next = random.nextInt(item_sum * item_sum);
                Game_item item = game_item_array[next];
                //寻找为0item（随机）
                while (item.getItem_number() != 0) {
                    next = random.nextInt(16);
                    item = game_item_array[next];
                }
                //产生一个数字
                item.setItemNumber(Math.random() > 0.75 ? 4 : 2);
                isMoveHappen = isMergeHappen = false;
            }
        }else if(checkOver()) {
            if (onGame2048Listener != null) {
                onGame2048Listener.onGameOver();
            }
        }
    }

    //多个int值中找到最小值
    private int min_num(int... params) {
        int minNum = params[0];
        for (int a : params) {
            if (minNum > a)
                minNum = a;
        }
        return minNum;
    }

    //判断是否全部位置都有数字
    private boolean isFull() {
        for (int i = 0; i < game_item_array.length; i++) {
            //如果当前位置没有数字
            if (game_item_array[i].getItem_number() == 0)
                return false;
        }
        return true;
    }

    //检查是否全部位置是否都有数字，且相邻位置没有相同的数字--->game over
    private boolean checkOver() {
        /*
        * 判断相邻间有没相同数字
        * 没有 true
        * 有   false
        * */
        for (int i = 0; i < item_sum; i++)
            for (int j = 0; j < item_sum; j++) {
                //计算当前位置
                int index = i * item_sum + j;
                Log.e("index:", index + ",i:" + i + ",j:" + j);
                Game_item item=game_item_array[index];
                //上边
                if (index - item_sum >= 0) {
                    Game_item item_top = game_item_array[index - item_sum];
                    Log.e("item_top:",item_top.getItem_number()+",item:"+item.getItem_number()+"-----------");
                    if (item_top.getItem_number() == item.getItem_number())
                        return false;
                }
                //右边
                if ((index + 1) % item_sum != 0) {
                    Game_item item_right = game_item_array[index + 1];
                    Log.e("item_top:",item_right .getItem_number()+",item:"+item.getItem_number()+"-----------");
                    if (item.getItem_number() == item_right.getItem_number()) {
                        return false;
                    }
                }
                //下边
                if ((index + item_sum) < item_sum * item_sum) {
                    Game_item item_bottom = game_item_array[index + item_sum];
                    Log.e("item_top:",item_bottom .getItem_number()+",item:"+item.getItem_number()+"-----------");
                    if (item.getItem_number() == item_bottom.getItem_number()) {
                        return false;
                    }
                }
                //左边
                if (index % item_sum != 0) {
                    Game_item item_left = game_item_array[index - 1];
                    Log.e("item_top:",item_left .getItem_number()+",item:"+item.getItem_number()+"-----------");
                    if (item.getItem_number() == item_left.getItem_number()) {
                        return false;
                    }
                }
            }
        return true;
    }

    //根据移动，整体进行移动合并值
    public void action(int direction) {
        //行|列
        for (int i = 0; i < item_sum; i++) {
            List<Game_item> row = new ArrayList<Game_item>();
            //行|列
            //记录不为零的数字
            for (int j = 0; j < item_sum; j++) {
                //得到下标
                int index = getIndexByDirection(direction, i, j);
                Game_item item = game_item_array[index];
                if (item.getItem_number() != 0)
                    row.add(item);
            }

            //判断是否发生移动
            for (int j = 0; j < item_sum && j < row.size(); j++) {
                int index = getIndexByDirection(direction, i, j);
                Game_item item = game_item_array[index];
                if (item.getItem_number() != row.get(j).getItem_number()) {
                    isMoveHappen = true;
                }
            }
            //移动过程中合并相同的数字
            mergeItem(row);

            //设置合并后的值
            for (int j = 0; j < item_sum; j++) {
                int index = getIndexByDirection(direction, i, j);
                if (row.size() > j) {
                    game_item_array[index].setItemNumber(row.get(j).getItem_number());
                } else {
                    game_item_array[index].setItemNumber(0);
                }
            }
        }
        RandomNumber();
    }

    /*
    * 根据direction和i、j的到下标
    * */
    private int getIndexByDirection(int direction, int i, int j) {
        //初始化下标
        int index = -1;
        switch (direction) {
            case TOP:
                index = i + j * item_sum;
                break;
            case RIGHT:
                index = i * item_sum + item_sum - j - 1;
                break;
            case BOTTOM:
                index = i + (item_sum - 1 - j) * item_sum;
                break;
            case LEFT:
                index = i * item_sum + j;
                break;
        }
        return index;
    }

    //移动过程中合并相同的数字
    private void mergeItem(List<Game_item> row) {
        if (row.size() < 2) {
            return;
        }
        for (int i = 0; i < row.size() - 1; i++) {
            Game_item item1 = row.get(i);
            Game_item item2 = row.get(i + 1);

            if (item1.getItem_number() == item2.getItem_number()) {
                isMergeHappen = true;
                int value = item1.getItem_number() + item2.getItem_number();
                item1.setItemNumber(value);

                //更新当前得分
                record_score += value;
                if (onGame2048Listener != null) {
                    onGame2048Listener.onScoreChange(record_score);
                }
                //向前移动
                for (int j = i + 1; j < row.size() - 1; j++) {
                    row.get(j).setItemNumber(row.get(j + 1).getItem_number());
                }
                row.get(row.size() - 1).setItemNumber(0);
                return;
            }
        }
    }

    //重新开始游戏
    public void reStart() {
        isOnce=true;
        //所有数值初始化
        for (Game_item item : game_item_array) {
            item.setItemNumber(0);
        }
        record_score = 0;
        if (onGame2048Listener != null) {
            onGame2048Listener.onScoreChange(record_score);
        }
        isMoveHappen = isMergeHappen = true;
        for (int i = 0; i < 3; i++) {
            isMergeHappen = isMoveHappen = true;
            RandomNumber();
        }
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //计算点e1到点e2的距离（滑动距离）
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            if (x > FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
                //右
                action(RIGHT);
            } else if (x < -FLING_MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {
                //左
                action(LEFT);
            } else if (y > FLING_MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
                //下
                action(BOTTOM);
            } else if (y < -FLING_MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {
                //上
                action(TOP);
            }
            return true;
        }
    }
}