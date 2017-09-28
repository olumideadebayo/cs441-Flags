package org.olumide.adebayo.flags;

import android.content.Context;

import static org.olumide.adebayo.flags.FlagActivity.correct;
import static org.olumide.adebayo.flags.FlagActivity.levelAnswers;
import static org.olumide.adebayo.flags.FlagActivity.levelFlags;
import static org.olumide.adebayo.flags.FlagActivity.levels;
import static org.olumide.adebayo.flags.FlagActivity.level;
import static org.olumide.adebayo.flags.FlagActivity.round;

import static org.olumide.adebayo.flags.FlagActivity.drawables;
import static org.olumide.adebayo.flags.FlagActivity.levelTV;
import static org.olumide.adebayo.flags.FlagActivity.roundTV;

/**
 * Created by oadebayo on 9/27/17.
 */

public class FlagUtils {

    //just reset some vars
    static void reset(){
        FlagActivity.wrong = 0;
        FlagActivity.correct = 0;
        level = 1;
        FlagActivity.round = 1;
        FlagActivity.top = 0;
        FlagActivity.left = 0;
        FlagActivity.currentContinent = null;
        FlagActivity.continents = null;
        drawables = null;
    }

    //check if we've hit max round for the current level
    static boolean isMaxRound(int correct){
        int index = 0;
        for(int i=0;i<levels.length;i++){
            if( levels[i] == level){
                index = i;
                break;
            }
        }
        int rounds = levelAnswers[index];
        return rounds <= correct;
    }

    //check if we're on the maximum level
    static boolean isMaxLevel(){
        int maxLevel = levels[ levels.length-1];
        return level == maxLevel;
    }

    //get number of flags needed for this level
    static int getLevelFlagCount(){
        int index = 0;
        for(int i=0;i<levels.length;i++){
            if( levels[i] == level){
                index = i;
                break;
            }
        }
        return levelFlags[index];
    }

    /*
    update the 2 textviews for Level and Round
    */
    static void setLevelRound(Context ctx){
        levelTV.setText(ctx.getString(R.string.level)+" "+level);
        roundTV.setText(ctx.getString(R.string.round)+" "+round);

    }


}
