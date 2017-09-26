package org.olumide.adebayo.flags;

import android.accessibilityservice.AccessibilityService;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class FlagActivity extends Activity {

    private int continentId = 0;
    private ArrayList<String> continents = null;
    private String currentContinent=null;

    private Random random = new Random(4);
    private  Drawable[] drawables=null;

    Button btn1,btn2,btn3,btn4;
    ImageView flag1,flag2,flag3,flag4;
    TextView staticMe,round,level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);

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

    private void showRandomFlags(){
            int index = random.nextInt(4-1);

            //get random continent
            currentContinent = continents.get(index);

        Log.d("Olu","current continent "+currentContinent);

        //get list of glags
        drawables = getRandomFlags(4);
            //get random 4 flags
            //display 4 flags
            //display continent buttons
            setContinentButtons();
    }

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

            Log.d("Olu","path is "+path);

            String [] flags = manager.list(path);

            Log.d("Olu","count of flags "+flags.length);

            Random random = new Random(flags.length);

            path += "/";
            while(count > 0 && flags.length>0) {
                int index = random.nextInt(flags.length);

                String _flag = path + flags[index];

                Log.d("Olu","random flag index "+index);

                Log.d("Olu",_flag);

                InputStream stream = manager.open(_flag);

                Log.d("Olu","got this far ");

                Drawable _d = Drawable.createFromStream(stream, null);
                drawables[count-1] = _d;
                count--;
            }
        } catch (IOException e) {
            Log.e("Olu","error in manager:"+e.toString());
        }
        return drawables;
    }

    private void setContinentButtons(){
        btn1.setText(continents.get(0));
        btn2.setText(continents.get(1));
        btn3.setText(continents.get(2));
        btn4.setText(continents.get(3));
    }
    private void setImageViewFlags(){
        flag1.setImageDrawable(drawables[0]);
        flag2.setImageDrawable(drawables[1]);
        flag3.setImageDrawable(drawables[2]);
        flag4.setImageDrawable(drawables[3]);

        flag1.setAlpha(1.0f);
        flag2.setAlpha(1.0f);
        flag3.setAlpha(1.0f);
        flag4.setAlpha(1.0f);



    }
    private void setFrameSize(DisplayMetrics drm){

        int width = drm.widthPixels;
        int height = drm.heightPixels;
        int center = width / 2;
        int btnWidth = (int) (width*.4);
        int btnHeight = (int) (height*.1);

        int _wMarginR = (int) (center*.05);
        int _wMarginL = (int) (width *.05);

        int _t = (int) (height*.5);

        FrameLayout.LayoutParams btnParams1 = new  FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.LEFT);
        btnParams1.setMargins(_wMarginL,_t,0,0);
        btn1.setLayoutParams(btnParams1);


        FrameLayout.LayoutParams btnParams2 = new FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.RIGHT);
        btnParams2.setMargins(_wMarginR,_t,0,0);
        btn2.setLayoutParams(btnParams2);


        FrameLayout.LayoutParams btnParams3 = new FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.LEFT);
        _t = (int) ((height *.2) + (height *.1));
        btnParams3.setMargins(_wMarginL,_t,0,0);
        btn3.setLayoutParams(btnParams3);

        FrameLayout.LayoutParams btnParams4 = new FrameLayout.LayoutParams(btnWidth,btnHeight,Gravity.RIGHT);
     //   btnParams4.setMargins(_wMarginR,_t,0,0);
        btnParams4.setMargins(0,0,(int)(width*.1),(int)(height*.1));
        //btnParams4.
        btn4.setLayoutParams(btnParams4);


        /*
        FrameLayout.LayoutParams two = new FrameLayout.LayoutParams((int)(width*.4),(int)(height*.2));
        two.setMargins((int)(width*.55),(int)(height*.2),0,0);
        red.setLayoutParams(two);

        FrameLayout.LayoutParams t3 = new FrameLayout.LayoutParams((int)(width*.4),(int)(height*.2));
        t3.setMargins((int)(width*.25),(int)(height*.4),0,0);
        green.setLayoutParams(t3);

        FrameLayout.LayoutParams t4 = new FrameLayout.LayoutParams((int)(width*.4),(int)(height*.2));
        t4.setMargins((int)(width*.4),(int)(height*.45),0,0);
        yellow.setLayoutParams(t4);
        */
    }

    private void findViews(){
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        flag1= findViewById(R.id.flag1);
        flag2 = findViewById(R.id.flag2);
        flag3 = findViewById(R.id.flag3);
        flag4 = findViewById(R.id.flag4);
        staticMe = findViewById(R.id.staticMe);
        round = findViewById(R.id.round);
        level = findViewById(R.id.level);
    }
}
