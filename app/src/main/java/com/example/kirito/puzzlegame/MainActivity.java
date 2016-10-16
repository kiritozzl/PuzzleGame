package com.example.kirito.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageView ivs[][] = new ImageView[5][3];
    private GridLayout gl;

    private ImageView null_iv;
    private GestureDetector gd;
    private boolean isStart = false;

    private static final String TAG = "MainActivity";

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gd = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                String direction = detectDicr(e1.getX(),e1.getY(),e2.getX(),e2.getY()) + "";
                moveBoxByGesture(detectDicr(e1.getX(),e1.getY(),e2.getX(),e2.getY()));
                //Toast.makeText(getApplicationContext(),direction,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        setContentView(R.layout.activity_main);
        init();
    }

    //随机打乱方块的顺序
    private void setRandom(){
        //通过循环来多次调用依据手势移动方块的方法来随机打乱方块的顺序
        for (int i = 0; i < 100; i++) {
            int flag = (int) ((Math.random() * 4) + 1);
            //Log.e(TAG, "setRandom: flag---"+flag );
            //需要移动时没有动画效果
            moveBoxByGesture(flag,false);
        }
        isStart = true;
    }

    private void moveBoxByGesture(int ges,boolean isAnime){
        Box null_box = (Box) null_iv.getTag();
        int new_x = null_box.x;
        int new_y = null_box.y;
        if (ges == 1){
            new_y++;
        }else if (ges == 2){
            new_y--;
        }else if (ges == 3){
            new_x++;
        }else if (ges == 4){
            new_x--;
        }
        if (new_x >= 0 && new_x < ivs[0].length && new_y >= 0 && new_y < ivs.length){
            if (isAnime){
                setAnimation(ivs[new_y][new_x]);
            }else if (!isAnime){
                changeData(ivs[new_y][new_x]);
            }
        }
    }

    //通过手势来移动方块：1,2,3,4对应上下左右
    private void moveBoxByGesture(int ges){
        moveBoxByGesture(ges,true);
    }

    /**
     * 判断手势的方向
     * @param start_x 起始的x位置
     * @param start_y  起始的y位置
     * @param end_x
     * @param end_y
     * @return 1,2,3,4对应上下左右
     */
    private int detectDicr(float start_x,float start_y,float end_x,float end_y){
        boolean isLeftOrRight = Math.abs(start_x - end_x) > Math.abs(start_y - end_y) ? true : false;
        if (isLeftOrRight){
            if (start_x - end_x > 0){
                return 3;
            }else if (start_x - end_x < 0){
                return 4;
            }
        }else {
            if (start_y - end_y > 0){
                return 1;
            }else if (start_y - end_y < 0){
                return 2;
            }
        }
        return 0;
    }

    private void init(){
        gl = (GridLayout) findViewById(R.id.gl);
        //从drawable下获取图片资源并将其转为bitmap
        Bitmap bm = ((BitmapDrawable)getResources().getDrawable(R.drawable.a)).getBitmap();
        int bm_height = bm.getHeight() / 5;
        int bm_width = bm.getWidth() / 3;
        for (int i = 0; i < ivs.length; i++) {
            for (int j = 0; j < ivs[0].length; j++) {
                ivs[i][j] = new ImageView(this);
                //通过bitmap的createBitmap方法来切割图片，把完整图片分割成五行三列
                /**
                 * createBitmap方法解析
                 * Bitmap.createBitmap(source, 60, 0, 480, 260); // 320 - 60 = 260
                 Basically, you are drawing from x = 60, y = 0 to x = 480 + 60, y = 260 on a Bitmap
                 */
                //完整分割图片,小方块不是正方形
                Bitmap bp = Bitmap.createBitmap(bm,j * bm_width,i * bm_height,bm_width,bm_height);
                ivs[i][j].setImageBitmap(bp);
                //这种均等切割为正方形的方式，最终只会显示原图的一部分，即把原图左上角部分的正方形切割下来，其他的就舍弃了
                /*Bitmap bp = Bitmap.createBitmap(bm,j * bm_height,i * bm_height,bm_height,bm_height);
                ivs[i][j].setImageBitmap(bp);*/
                ivs[i][j].setPadding(2,2,2,2);

                //给每个拼图的view绑定数据
                //要注意x=j,y=i!!!
                Box box = new Box(j,i,bp);
                ivs[i][j].setTag(box);

                ivs[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = isAroundNull((ImageView) v);
                        //Toast.makeText(getApplicationContext(),"是否在周围--"+flag,Toast.LENGTH_SHORT).show();
                        if(flag){
                            setAnimation((ImageView) v);
                        }
                    }
                });
            }
        }

        for (int i = 0; i < ivs.length; i++) {
            for (int j = 0; j < ivs[0].length; j++) {
                //通过给gridview添加切割好的小图片
                gl.addView(ivs[i][j]);
            }
        }
        setNullImageview(ivs[4][2]);
        setRandom();
    }

    private void setAnimation(final ImageView iv){
        TranslateAnimation ta = null;
        if (iv.getX() > null_iv.getX()){
            ta = new TranslateAnimation(0.1f,-iv.getWidth(),0.1f,0.1f);
        }else if (iv.getX() < null_iv.getX()){
            ta = new TranslateAnimation(0.1f,iv.getWidth(),0.1f,0.1f);
        }else if (iv.getY() > null_iv.getY()){
            ta = new TranslateAnimation(0.1f,0.1f,0.1f,-iv.getWidth());
        }else if (iv.getY() < null_iv.getY()){
            ta = new TranslateAnimation(0.1f,0.1f,0.1f,iv.getWidth());
        }
        ta.setDuration(70);
        //设置动画是否停留
        ta.setFillAfter(true);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            //交换点击的方块与空方块之间的数据
            @Override
            public void onAnimationEnd(Animation animation) {
                //iv清除动画
                iv.clearAnimation();
                changeData(iv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(ta);
    }

    //判读游戏是否结束，通过循环遍历所有方块当前位置与初始位置是否相等来判读
    private void isGameOver(){
        boolean isOver = false;
        boolean isBreakFromj = false;
        for (int i = 0; i < ivs.length; i++) {
            if (isBreakFromj){
                break;
            }
            for (int j = 0; j < ivs[0].length; j++) {
                //若是空方块，则跳过
                if (ivs[i][j] == null_iv){
                    continue;
                }
                Box box = (Box) ivs[i][j].getTag();
                if (box.isTrue()){
                    isOver = true;
                }else if (!box.isTrue()){
                    isOver = false;
                    //break只能跳出一层循环
                    isBreakFromj = true;
                    break;
                }
            }
        }
        if (isOver){
            Toast.makeText(getApplicationContext(),"congratulations game over!",Toast.LENGTH_SHORT).show();
        }
    }

    private void changeData(ImageView iv){
        //把当前点击的方块的数据传给当前空方块
        Box b1 = (Box) iv.getTag();
        null_iv.setImageBitmap(b1.bp);
        Box nul_b = (Box) null_iv.getTag();
        nul_b.bp = b1.bp;
        nul_b.change_x = b1.change_x;
        nul_b.change_y = b1.change_y;
        //设置当前点击方块为空方块
        setNullImageview(iv);
        //每次交换完成后都进行判断游戏是否结束
        if (isStart){
            isGameOver();
        }
    }

    private class Box{
        public int x;
        public int y;
        public Bitmap bp;
        public int change_x;
        public int change_y;

        public Box(int x, int y, Bitmap bp) {
            this.x = x;
            this.y = y;
            this.bp = bp;
            this.change_x = x;
            this.change_y = y;
        }

        //通过比较某一方块初始位置与当前位置的关系，判断该方块是否在其初始位置
        public boolean isTrue() {
            if (x == change_x && y == change_y){
                return true;
            }else {
                return false;
            }
        }
    }

    /**
     * 判断当前点击的iv与空iv之间的位置关系
     * @param iv
     * @return
     */
    private boolean isAroundNull(ImageView iv){
        Box null_b = (Box) null_iv.getTag();
        Box b = (Box) iv.getTag();
        //在空view上面
        if (null_b.x == b.x && null_b.y - 1 == b.y){
            return true;
        }else if (null_b.x - 1 == b.x && null_b.y == b.y){
            //左边
            return true;
        }else if (null_b.x + 1 == b.x && null_b.y == b.y){
            //右边
            return true;
        }else if (null_b.x == b.x && null_b.y + 1 == b.y){
            //下面
            return true;
        }
        return false;
    }

    /**
     * 设置空方块
     * @param iv
     */
    private void setNullImageview(ImageView iv){
        iv.setImageBitmap(null);
        Box b_null = (Box) iv.getTag();
        //需要把设置为空方块的bitmap设为空
        b_null.bp = null;
        null_iv = iv;
    }
}
