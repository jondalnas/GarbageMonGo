package com.garbagemon.jonas.garbagemongo;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;

public class Exp {
    private ClipDrawable expBar;
    private TextView levelText;
    private ClipDrawable modifierImage;
    private TextView currentModifier;
    private TextView nextModifier;
    private double xp, targetXP;
    private int level = 1;
    private int toNextLevel = 350;
    private int lastToNextLevel = 0;
    private double modifier = 1, targetModifier = 1;
    private Context context;

    public Exp(Context context, ClipDrawable expBar, TextView levelText, ClipDrawable modifierImage, TextView currentModifier, TextView nextModifier) {
        this.expBar = expBar;
        this.levelText = levelText;
        this.modifierImage = modifierImage;
        this.currentModifier = currentModifier;
        this.nextModifier = nextModifier;
        this.context = context;

        loadEXP();
    }

    public void addXP(int xp) {
        targetXP += xp*targetModifier;
        targetModifier++;
    }

    public void updateExp() {
        if (xp < targetXP) {
            xp += (targetXP-xp)/60.0;

            if (targetXP-xp < 0.1) xp = targetXP;

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

            ByteBuffer bb = ByteBuffer.allocate(4);

            bb.putInt((int) xp);

            Save.saveFile(context, "EXP", bb.array());
        }

        targetModifier -= 1.0/(60.0*90.0);

        if (targetModifier < 1) targetModifier = 1;
        if (targetModifier > 5) targetModifier = 5;

        if (targetModifier < modifier) modifier = targetModifier;
        else if (targetModifier > modifier) modifier += 1.0/60.0;

        if (modifier > 5) modifier = 4.99;

        modifierImage.setLevel((int) ((modifier-Math.floor(modifier))*10000.0));
        currentModifier.setText((int) (Math.floor(modifier))+"");
        nextModifier.setText(((int) (Math.floor(modifier))+1)+"");
    }

    public void loadEXP() {
        String xpString = Save.loadFile(context, "EXP");

        if (xpString == null) return;

        try {
            targetXP = xp = ((int) xpString.toCharArray()[3]) +
                    (((int) xpString.toCharArray()[2]) << 8) +
                    (((int) xpString.toCharArray()[1]) << 16) +
                    (((int) xpString.toCharArray()[0]) << 24);


            while (xp >= toNextLevel) {
                level++;

                levelText.setText(level+"");

                int toNextLevelOld = toNextLevel;
                toNextLevel += (lastToNextLevel==0?toNextLevel:lastToNextLevel);
                lastToNextLevel = toNextLevelOld;
            }

            expBar.setLevel((int) (((double) (xp-lastToNextLevel)/(toNextLevel-lastToNextLevel))*10000.0));
        } catch (Exception e) {e.printStackTrace();}
    }
}
