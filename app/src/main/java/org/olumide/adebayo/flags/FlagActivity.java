package org.olumide.adebayo.flags;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static android.R.attr.maxLevel;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.icu.util.EthiopicCalendar.TER;
import static android.widget.Toast.makeText;
import static org.olumide.adebayo.flags.FlagUtils.getLevelFlagCount;

public class FlagActivity extends Activity {

    

    private Random random = new Random();
    static  Drawable[] drawables=null;

    static ArrayList<String> continents = null;
    static String currentContinent=null;

    static Button btn1,btn2,btn3,btn4;
    static ImageView flag1,flag2,flag3,flag4;
    static TextView staticMeTV,roundTV,levelTV;

    static int top = 0;
    static int left = 0;

    //various counters
    static int level = 1;
    static int round = 1;
    static int correct = 0;
    static int wrong = 0;
    int maxWrong = 3;//max allowed wrong choices

    //game levels
    static int[] levels = new int[] {1,2,3,4};
    //number of flags to show during each level
    static int[] levelFlags = new int[] {4,3,2,1};
    //number of rounds/answers for each level
    static int[] levelAnswers = new int[] {4,3,2,1};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);

        FlagUtils.reset();
        //initialize vars declared above
        findViews();

        Bundle extras = getIntent().getExtras();
        if( extras != null){
            continents =extras.getStringArrayList("continents");
            if( continents != null){
                Log.d("Olu","# of continents "+continents.size());
                showRandomFlags();

            }else{
                Log.d("Olu","no continent list passed in ");
            }

        }else{
            Log.d("Olu","extra was null");
        }

        //get the REAL size of your device
        DisplayMetrics drm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(drm);
        Log.d("r-size",drm.widthPixels+","+drm.heightPixels);
        //get the size - the bar space
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.d("size",dm.widthPixels+","+dm.heightPixels);

        setFrameSize(drm);

    }

    /*
    click listener for continent buttons
     */
    View.OnClickListener continentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            unSetContinentButtons();

            boolean match =false;

            Button btn = (Button) view;
            String btnText = btn.getText().toString();

            String _currentContinent = currentContinent.replaceAll("_","");
            _currentContinent = _currentContinent.replaceAll(" ","");
            btnText = btnText.replaceAll(" ","");

            Log.d("Olu","current continent "+_currentContinent);
            Log.d("Olu",btnText);
            if( btnText.equalsIgnoreCase(_currentContinent)){

                correct++;
                match = true;

            }else{
                wrong++;
                match= false;
                wrongAlert();

            }
            if(wrong == maxWrong) {//lost!
                //do stuff for losers :(
                endGame(0);

                return;

            }

            Log.d("Olu"," correct now"+correct);

            if(match) {

                correctAlert();

                Log.d("Olu","there was a match");

                if (FlagUtils.isMaxRound(correct)) {
                    Log.d("Olu",round+"maxRound reached at level "+level);

                    //if possible go to next level
                    if( !FlagUtils.isMaxLevel()){

                        Log.d("Olu",level+" is not max level");

                        level++;
                        correct = 0;
                        round = 1;

                        //fake a delay so animation completes
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showRandomFlags();
                            }
                        },2002);

                        return;
                    }
                    //otherwise, show WINNER
                    endGame(1);
                    return;

                } else {
                    round++;
                   // showRandomFlags();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showRandomFlags();
                        }
                    },2002);
                }
            }else{
                //allow buttons to be clickable back
                setContinentButtons();
                return;
            }

        }
    };

    /*
    this is really like a mini-controller
     */
    private void showRandomFlags(){

        int index = random.nextInt(FlagUtils.getLevelFlagCount());

        //get random continent
        currentContinent = continents.get(index);

        Log.d("Olu","current continent "+currentContinent);

        //get random 4 flags
        drawables = getRandomFlags(getLevelFlagCount());

        //display 4 flags
        setImageViewFlags();

        //display continent buttons
        setContinentButtons();

        //display level+round
        FlagUtils.setLevelRound(FlagActivity.this);

    }

    /*
    this will return an array of count flags
     */
    public Drawable[] getRandomFlags(int count){
        if( count < 1 || currentContinent==null){
            Log.d("Olu","missing values");
            return null;
        }
        Drawable[] drawables= new Drawable[count];

        //get the manager ( like a file explorer)
        AssetManager manager = getAssets();
        try {
            //list of all the items in the folder
            String path = "flags/"+currentContinent;

            String [] flags = manager.list(path);

            Random _random = new Random();

            path += "/";
            while(count > 0 && flags.length>0) {
                int index = _random.nextInt(flags.length);

                String _flag = flags[index];

                /*
                what i'm doing here is just a way to ensure a flag is not
                repeated.  there are many ways to approach this but this is
                what I opted
                 */
                if(_flag.equals("---")) {
                    continue;
                }
                flags[index] = "---";
                _flag = path + _flag;

                InputStream stream = manager.open(_flag);

                Drawable _d = Drawable.createFromStream(stream, null);
                drawables[count-1] = _d;
                count--;
            }
        } catch (IOException e) {
            Log.e("Olu","error in manager:"+e.toString());
        }
        Log.d("Olu","total flags "+drawables.length);

        return drawables;
    }

    /*
    this will simply make all continent
    buttons unclickable
     */
    private void unSetContinentButtons(){

        btn1.setClickable(false);
        btn2.setClickable(false);
        btn3.setClickable(false);
        btn4.setClickable(false);

    }

    /*
    assign texts to continent buttons
     */
    private void setContinentButtons(){
        btn1.setText(continents.get(0).replace("_"," "));
        btn2.setText(continents.get(1).replace("_"," "));
        btn3.setText(continents.get(2).replace("_"," "));
        btn4.setText(continents.get(3).replace("_"," "));

        btn1.setOnClickListener(continentClickListener);
        btn2.setOnClickListener(continentClickListener);
        btn3.setOnClickListener(continentClickListener);
        btn4.setOnClickListener(continentClickListener);

        btn1.setClickable(true);
        btn2.setClickable(true);
        btn3.setClickable(true);
        btn4.setClickable(true);
    }

    /*
    assign images to imageview
     */
    private void setImageViewFlags() {
        flag1.setAlpha(0f);
        flag2.setAlpha(0f);
        flag3.setAlpha(0f);
        flag4.setAlpha(0f);
        if( drawables.length>=1) {
            flag1.setImageDrawable(drawables[0]);
            flag1.setAlpha(1.0f);
        }
        if( drawables.length>=2) {
            flag2.setImageDrawable(drawables[1]);
            flag2.setAlpha(1.0f);
        }
        if( drawables.length>=3) {
            flag3.setImageDrawable(drawables[2]);
            flag3.setAlpha(1.0f);
        }
        if( drawables.length>=4) {
            flag4.setImageDrawable(drawables[3]);
            flag4.setAlpha(1.0f);
        }

    }



    /*
    this is where the various views are laid out
    on the FrameLayout
     */
    private void setFrameSize(DisplayMetrics drm){

        int width = drm.widthPixels;
        int height = drm.heightPixels;
        int center = width / 2;
        int btnWidth = (int) (width*.4);
        int btnHeight = (int) (height*.1);

        int flagWidth = (int) (width*.4);
        int flagHeight = (int) (height*.1);

        int textWidth = (int) (width*.4);
        int textHeight = (int) (height *.1);
        int rowMargin = (int) (height *.03);

        //set top appropriately
        top = (int) (height*.01);
        left = (int) (width*.01);

        FrameLayout.LayoutParams levelParams = new FrameLayout.LayoutParams(textWidth,textHeight);
        levelParams.setMargins(left,top,0,0);
        levelTV.setLayoutParams(levelParams);

        FrameLayout.LayoutParams roundParams = new FrameLayout.LayoutParams(textWidth,textHeight);
        roundParams.setMargins((textWidth+left),top,0,0);
        roundTV.setLayoutParams(roundParams);


        //update top
        top += textHeight;


        //flag imageview
        FrameLayout.LayoutParams flagParams1 = new  FrameLayout.LayoutParams(flagWidth,flagHeight,Gravity.LEFT);
        flagParams1.setMargins(left,top,0,0);
        flag1.setLayoutParams(flagParams1);
        flag1.setPadding(5,5,5,5);
        flag1.setBackgroundColor(Color.BLACK);


        FrameLayout.LayoutParams flagParams2 = new  FrameLayout.LayoutParams(flagWidth,flagHeight,Gravity.LEFT);
        flagParams2.setMargins( (center+left),top,0,0);
        flag2.setLayoutParams(flagParams2);
        flag2.setPadding(5,5,5,5);
        flag2.setBackgroundColor(Color.BLACK);


        //update top for next row of flags
        top += flagHeight;
        top += rowMargin;


        FrameLayout.LayoutParams flagParams3 = new  FrameLayout.LayoutParams(flagWidth,flagHeight,Gravity.LEFT);
        flagParams3.setMargins(left,top,0,0);
        flag3.setLayoutParams(flagParams3);
        flag3.setPadding(5,5,5,5);
        flag3.setBackgroundColor(Color.BLACK);

        FrameLayout.LayoutParams flagParams4 = new  FrameLayout.LayoutParams(flagWidth,flagHeight,Gravity.LEFT);
        flagParams4.setMargins( (center+left),top,0,0);
        flag4.setLayoutParams(flagParams4);
        flag4.setPadding(5,5,5,5);
        flag4.setBackgroundColor(Color.BLACK);

        //update top position
        top += flagHeight;
        top += rowMargin;
        top += rowMargin;


        FrameLayout.LayoutParams _params = new FrameLayout.LayoutParams(textWidth,textHeight);
        int _left = width/3;
        _params.setMargins((_left),top,0,0);
        staticMeTV.setLayoutParams(_params);

        //update top
        top += (textHeight);
        top += rowMargin;


        //continent buttons
        FrameLayout.LayoutParams btnParams1 = new  FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.LEFT);
        btnParams1.setMargins(left,top,0,0);
        btn1.setLayoutParams(btnParams1);

        FrameLayout.LayoutParams btnParams2 = new FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.RIGHT);
        btnParams2.setMargins(left+center,top,0,0);
        btn2.setLayoutParams(btnParams2);

        //update top for next row of buttons
        top += btnHeight;
        top += rowMargin;

        FrameLayout.LayoutParams btnParams3 = new FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.LEFT);
        btnParams3.setMargins(left,top,0,0);
        btn3.setLayoutParams(btnParams3);

        FrameLayout.LayoutParams btnParams4 = new FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.RIGHT);
        btnParams4.setMargins(left+center,top,0,0);
        btn4.setLayoutParams(btnParams4);
    }


    private void wrongAlert(){
        Animation anim = AnimationUtils.loadAnimation(FlagActivity.this,R.anim.wrong);
        flag1.startAnimation(anim);
        flag2.startAnimation(anim);
        flag3.startAnimation(anim);
        flag4.startAnimation(anim);
    }
    private void correctAlert(){
        Animation anim = AnimationUtils.loadAnimation(FlagActivity.this,R.anim.right);
        flag1.startAnimation(anim);
        flag2.startAnimation(anim);
        flag3.startAnimation(anim);
        flag4.startAnimation(anim);
    }

    /*
    end the game
     */
    private void endGame(int status){
        AlertDialog.Builder builder = new AlertDialog.Builder(FlagActivity.this);
        builder.setTitle(R.string.game_over);
        if( status == 1) {
            builder.setMessage(R.string.you_won);
        }else{
            builder.setMessage(R.string.you_lost);
        }
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(FlagActivity.this,Splash.class);
                intent.putExtra("isReStart",true);
                startActivityForResult(intent, 211);
            }
        });
        builder.show();


    }

    /*
    this collects all the views we'll
    interact with
     */
    private void findViews(){
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        flag1= findViewById(R.id.flag1);
        flag2 = findViewById(R.id.flag2);
        flag3 = findViewById(R.id.flag3);
        flag4 = findViewById(R.id.flag4);
        staticMeTV = findViewById(R.id.staticMe);
        roundTV = findViewById(R.id.round);
        levelTV = findViewById(R.id.level);
    }

}
