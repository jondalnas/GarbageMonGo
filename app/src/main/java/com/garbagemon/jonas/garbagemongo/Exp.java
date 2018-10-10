package com.garbagemon.jonas.garbagemongo;

import android.graphics.drawable.ClipDrawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Exp {
    public ClipDrawable expBar;
    public TextView levelText;
    public ClipDrawable modifierImage;
    private int xp, targetXP;
    private int level = 1;
    private int toNextLevel = 350;
    private int lastToNextLevel = 0;
    private double modifier;

    public void addXP(int xp) {
        targetXP += xp;
        modifier++;
    }

    public void updateExp() {
        if (xp < targetXP) {
            xp++;

            if (xp >= toNextLevel) {
                level++;

                levelText.setText(level+"");

                int toNextLevelOld = toNextLevel;
                toNextLevel += (lastToNextLevel==0?toNextLevel:lastToNextLevel);
                lastToNextLevel = toNextLevelOld;
            }

            if (toNextLevel != lastToNextLevel) {
                expBar.setLevel((int) (((double) (xp-lastToNextLevel)/(toNextLevel-lastToNextLevel))*10000.0));
            }
        }

        modifier -= 1.0/(60.0*90.0);

        if (modifier < 1) modifier = 1;

        modifierImage.setLevel((int) ((modifier-Math.floor(modifier))*10000.0));
    }
}
