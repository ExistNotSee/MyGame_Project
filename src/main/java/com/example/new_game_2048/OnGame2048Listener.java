package com.example.new_game_2048;

/**
 * Created by silent on 17-6-8.
 */

public interface OnGame2048Listener {
    void onScoreChange(int score);//得分
    void onGameOver();//游戏结束
}
