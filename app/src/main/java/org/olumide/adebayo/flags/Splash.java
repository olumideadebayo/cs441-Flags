package org.olumide.adebayo.flags;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;

import static android.R.attr.start;






/*
@author: olumide marc adebayo - 374994
 */


public class Splash extends Activity {

    private String[] continents =null;
    int selectionCount = 0;
    HashSet<String> continentSelected = new HashSet<String>();

    AlertDialog multiDialog =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        /*
        //button onclick listener
        Button but = findViewById(R.id.action);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Splash.this,FlagActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.continent_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        Intent intent = new Intent(Splash.this,FlagActivity.class);
        intent.putExtra("isStart",true);
        startActivityForResult(intent, 211);
       */
        if( getContinentList()){
            showContinentChoices();
        }
        return super.onOptionsItemSelected(item);
    }


    private boolean getContinentList(){
        try {
            AssetManager assetManager = getAssets();
            continents = assetManager.list("flags");
            for(int i=0;i<continents.length;i++){
                //fix underscores in folder names
                continents[i] = continents[i].replaceAll("_"," ");
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private void showContinentChoices(){
        //reset
        selectionCount=0;
        continentSelected.clear();


        AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
        builder.setTitle(R.string.choose_4_continets);

        builder.setMultiChoiceItems(continents, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(multiDialog != null) {
                    multiDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }

                if( b) {//selected
                    selectionCount++;
                    //add to list (set)
                    continentSelected.add(""+i);
                }else{
                    selectionCount--;
                    //remove from list (set)
                    continentSelected.remove(""+i);
                }

                if(selectionCount>=4 && multiDialog!= null){
                    Log.d("M",selectionCount+"");

                    multiDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });


        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectionCount>=4){
                    Log.d("MM","ready to go to next activity");

                    Intent intent = new Intent(Splash.this,FlagActivity.class);

                    ArrayList<String> list = new ArrayList<String>();
                    for(String s:continentSelected){
                        String c = continents[ Integer.parseInt(s)];
                        c= c.replaceAll(" ","_");//reverse what we did earlier

                        list.add(c);
                    }
                    intent.putExtra("continents",list);
                    startActivityForResult(intent, 211);
                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //actually make the dialog
        multiDialog = builder.create();
        //done.setCancelable(false);
        //done.setCanceledOnTouchOutside(false);
        multiDialog.show();

        // Initially disable the button
        multiDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);


    }
}
