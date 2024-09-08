package com.example.atarigame;

import static com.example.atarigame.MainActivity.changeVelocity;
import static com.example.atarigame.MainActivity.clickCounter;

public class KrabbyPatty {
    int krabbyX;
    int krabbyY;
    public static int krabbyVelocity;

    public KrabbyPatty(){
        //krabbyX = 4;
        krabbyY = this.krabbyY;
        krabbyVelocity = 5;
        newPosition();
    }

    public void newPosition(){
        krabbyX = (int)(Math.random() * 2000);
        krabbyY = -10;
        krabbyVelocity = changeVelocity();
    }

    public int XPos(){
        return krabbyX;
    }

    public static void increaseVelocity(){
        krabbyVelocity = 10;
    }




}
