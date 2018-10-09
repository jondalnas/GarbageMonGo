package com.garbagemon.jonas.garbagemongo;

import android.graphics.drawable.ClipDrawable;
import android.util.Log;

public class Exp {
    public ClipDrawable expBar;
    private int xp, targetXP;
    private int level;
    private int toNextLevel = 350;
    private int lastToNextLevel = 0;

    public void addXP(int xp) {
        targetXP += xp;
    }

    public void updateExp() {
        if (xp < targetXP) {
            xp++;

            if (xp >= toNextLevel) {
                level++;

                int toNextLevelOld = toNextLevel;
                toNextLevel += (lastToNextLevel==0?toNextLevel:lastToNextLevel);
                lastToNextLevel = toNextLevelOld;
            }

            if (toNextLevel != lastToNextLevel) {
                expBar.setLevel((int) (((double) (xp-lastToNextLevel)/(toNextLevel-lastToNextLevel))*10000.0));
            }
        }
    }
}
