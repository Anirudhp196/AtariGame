package com.example.atarigame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    //Code from this program has been used from Beginning Android Games
    //Review SurfaceView, Canvas, continue

    GameSurface gameSurface;
    MediaPlayer player;
    MediaPlayer eating;
    static int clickCounter;

    public static int changeVelocity(){
        if(clickCounter % 2 == 1)
            return 30;
        else
            return 5;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameSurface = new GameSurface(this);
        setContentView(gameSurface);

        player = MediaPlayer.create(this, R.raw.spongebob);
        eating = MediaPlayer.create(this, R.raw.crunchy);

    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSurface.resume();
    }

    //----------------------------GameSurface Below This Line--------------------------
    public class GameSurface extends SurfaceView implements Runnable,SensorEventListener{

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap spongebobOriginal;
        Bitmap spongebob;
        Bitmap krabbyOriginal;
        Bitmap krabbyPatty;
        Bitmap cryingOriginal;
        Bitmap crying;
        AtomicInteger scoreCounter = new AtomicInteger();
        ArrayList<Bitmap> krabbyPatties = new ArrayList<Bitmap>();
        ArrayList<Integer> krabbyPattiesWidth = new ArrayList<Integer>();
        ArrayList<KrabbyPatty> krabbyValues = new ArrayList<KrabbyPatty>();
        TextView textViewX;
        int ballX=0;
        String score = "Score: ";
        int timeRemaining = 30;
        Paint paintProperty;
        Paint paintProperty2;

        int screenWidth;
        int screenHeight;
        boolean isCrying = false;


        public GameSurface(Context context) {

            super(context);
            holder=getHolder();
            krabbyOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.krabbypatty);

            for (int i =0; i < 3; i++) {
                KrabbyPatty krabbyPatty = new KrabbyPatty();
                krabbyValues.add(krabbyPatty);
            }

            krabbyPatties.add(Bitmap.createScaledBitmap(krabbyOriginal, 200, 200, true));
            krabbyPatties.add(Bitmap.createScaledBitmap(krabbyOriginal, 200, 200, true));
            krabbyPatties.add(Bitmap.createScaledBitmap(krabbyOriginal, 200, 200, true));



            krabbyPatty = Bitmap.createScaledBitmap(krabbyOriginal, 100, 100, true);
            spongebobOriginal= BitmapFactory.decodeResource(getResources(),R.drawable.spongebob);
            spongebob = Bitmap.createScaledBitmap(spongebobOriginal, 200, 200, true);
            cryingOriginal= BitmapFactory.decodeResource(getResources(),R.drawable.cryingspongebob);
            crying = Bitmap.createScaledBitmap(cryingOriginal, 200, 200, true);


            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth=sizeOfScreen.x;
            screenHeight=sizeOfScreen.y;
            // Log.d("Screen Width", "" + screenWidth);  2264
            // Log.d("Screen Height", "" + screenHeight); 1017

            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,accelerometerSensor,sensorManager.SENSOR_DELAY_GAME);

            paintProperty= new Paint();
            paintProperty.setTextSize(36);

            paintProperty2 = new Paint();
            paintProperty2.setTextSize(96);

            Timer timer = new Timer();
            final Handler handler = new Handler();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {
                        timeRemaining--;

                        if(timeRemaining == 0){
                            running = false;
                        }
                    });
                }
            }, 0, 1000);

        }

        @Override
        public void run() {
            gameSurface.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCounter++;
                    if(clickCounter % 2 == 1){
                        KrabbyPatty.krabbyVelocity = 30;
                    }
                    else
                        KrabbyPatty.krabbyVelocity = 5;
                }
            });

            player.start(); // Starts music

            while (running == true){
                if (holder.getSurface().isValid() == false)
                    continue;
                Canvas canvas= holder.lockCanvas();

                canvas.drawRGB(38,131,202);

                canvas.drawText(score + scoreCounter, 1800, 100, paintProperty);
                canvas.drawText("Time Remaining: " + timeRemaining, 1800, 200, paintProperty);
                if(isCrying){
                    canvas.drawBitmap(crying, ((screenWidth/2) - spongebob.getWidth()/2 +ballX), 600, null);
                }else{
                    canvas.drawBitmap(spongebob,(screenWidth/2) - spongebob.getWidth()/2 +ballX ,600 ,null);
                }
                //canvas.drawBitmap(spongebob,(screenWidth/2) - spongebob.getWidth()/2 +ballX ,680 ,null);

                for(int i = 0; i < krabbyPatties.size(); i++){
                    canvas.drawBitmap(krabbyPatties.get(i), krabbyValues.get(i).krabbyX, krabbyValues.get(i).krabbyY, null);
                    krabbyValues.get(i).krabbyY+= krabbyValues.get(i).krabbyVelocity;

                    if((Math.abs(krabbyValues.get(i).krabbyX - ((screenWidth/2) - spongebob.getWidth()/2 +ballX)) < 100) && (krabbyValues.get(i).krabbyY > 500)){
                        player.pause();
                        eating.start();
                        Log.d("Hi", "KrabbyX: " + krabbyValues.get(i).krabbyX + "Spongebob: " + ((screenWidth/2) - spongebob.getWidth()/2 +ballX));
                        Log.d("Hi", "KrabbyY: " + krabbyValues.get(i).krabbyY + "Spongebob: " + spongebob.getHeight());
                        isCrying = true;
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                isCrying = false;
                            }
                        },1000);
                        canvas.drawBitmap(crying, ((screenWidth/2) - spongebob.getWidth()/2 +ballX), 700, null);
                        scoreCounter.getAndAdd(-5);
                        krabbyValues.get(i).newPosition();
                    }
                    player.start();

                    if(krabbyValues.get(i).krabbyY > 700){
                        krabbyValues.get(i).newPosition();
                        scoreCounter.getAndAdd(1);
                    }
                }

                holder.unlockCanvasAndPost(canvas);
            }

            Canvas canvas = holder.lockCanvas();
            canvas.drawRGB(56,55,245);
            canvas.drawText("Game Over", 890, 350, paintProperty2);
            canvas.drawText("Your Score Was: " + scoreCounter, 725, 450, paintProperty2);

            holder.unlockCanvasAndPost(canvas);
            player.stop();
            eating.stop();


        }

        public void resume(){
            running=true;
            gameThread=new Thread(this);
            gameThread.start();
        }

        public void pause() {
            running = false;
            while (true) {
                try {
                    gameThread.join();
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            //tilt phone and change position
            if(event.values[1] < 0.15 && event.values[1] > -2 && ballX > -1000) {
                ballX -= 7;
            }

            if(event.values[1] < -2 && ballX > -1000){
                ballX -= 14;
            }


            if(event.values[1] > 0.15 && event.values[1] < 2 && ballX < 1000){
                ballX += 7;
            }

            if(event.values[1] > 2 && ballX < 1000){
                ballX+= 14;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }//GameSurface
}//Activity