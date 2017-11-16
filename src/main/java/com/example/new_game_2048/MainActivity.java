package com.example.new_game_2048;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.new_game_2048.View.Game_UI;

public class MainActivity extends AppCompatActivity implements OnGame2048Listener{
    private TextView
            max_score,//最高分
            score_tv;//当前得分
    private int is_score=0;
    private Game_UI game_ui;
    private SharedPreferences sharedPreferences;//储存游戏数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iniView();
    }
    private void iniView(){
        max_score=(TextView)findViewById(R.id.max_score);
        score_tv=(TextView)findViewById(R.id.score);
        game_ui=(Game_UI)findViewById(R.id.Game_UI);
        game_ui.setOnGame2048Listener(this);
        sharedPreferences=this.getSharedPreferences("score", Context.MODE_PRIVATE);
        max_score.setText(sharedPreferences.getString("max_Score","0"));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
//            case R.id.action_settings:
//                //设置
//                Intent intent_settings=new Intent(getApplicationContext(), Activity_Settings.class);
//                startActivity(intent_settings);
//                break;
            case R.id.action_restart:
                //重新开始
                game_ui.reStart();
                break;
            case R.id.action_break:
                //退出
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //更新当前得分
    @Override
    public void onScoreChange(int score) {
        score_tv.setText(score+"");
        is_score=score;
    }
    //游戏结束
    @Override
    public void onGameOver() {
        SharedPreferences.Editor editor=sharedPreferences.edit();//存放数据
        //判断文件内是否有数据
        int score_a=Integer.parseInt(sharedPreferences.getString("max_Score","0"));
        //判断现在得分是否最高分
        score_a=score_a>is_score?score_a:is_score;
        //存入最高分
        editor.putString("max_Score",score_a+"");
        editor.commit();
        max_score.setText(score_a+"");
        new AlertDialog.Builder(this).setTitle("游戏结束")
                .setMessage("你的得分:"+score_tv.getText())
                .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        score_tv.setText("0");
                        game_ui.reStart();
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

}
