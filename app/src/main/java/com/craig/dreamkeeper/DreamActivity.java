package com.craig.dreamkeeper;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.UUID;



import com.craig.dreamkeeper.model.DreamContent;


public class DreamActivity extends AppCompatActivity {
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    LinkedHashMap<UUID, DreamContent.Dream> dreamMap;

    protected static final int RESULT_SPEECH = 1;
    public static final String DREAM_DIR_NAME = "Dream";
    public static final String WORDS_OF_POWER = "Purge everything";

    private static Intent alarmIntent;



    private static ImageButton btnSpeak;
    private static ImageButton btnSaveDream;



    private static DreamContent.Dream mDream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream);
        expListView = (ExpandableListView) findViewById(R.id.expandableDreamListView);

        dreamMap = loadDreams();
        listAdapter = new ExpandableListAdapter(this, dreamMap);
        listAdapter.syncDreams(dreamMap, mDream == null);
        ComponentName con= getCallingActivity();
        String caller = getCallingPackage();


        expListView.setAdapter(listAdapter);


        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechIntent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(speechIntent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        btnSaveDream = (ImageButton) findViewById(R.id.btnSaveDream);
        btnSaveDream.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mDream != null) {
                    saveDreams();
                    if(mDream.getDreamContent().equals(WORDS_OF_POWER)){
                        File dreamDir = new File(getFilesDir(), DREAM_DIR_NAME);
                        deleteRecursive(dreamDir);

                    }
                    Toast.makeText(getApplicationContext(),"kept dream: " + mDream.getDreamContent(),Toast.LENGTH_SHORT).show();
                    mDream = null;
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dream_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_alarm_btn:
                // User chose the "Settings" item, show the app settings UI...
                if(alarmIntent == null) {
                    alarmIntent = new Intent(this, AlarmActivity.class);
                }
                startActivity(alarmIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    private void saveDreams(){
        listAdapter.syncDreams(dreamMap, mDream == null);
        File dreamDir = new File(this.getFilesDir(), DREAM_DIR_NAME);
        boolean success;
        if(!dreamDir.exists()){
            success = dreamDir.mkdirs();
            Toast.makeText(getApplicationContext(), "Created Dreams: " + success, Toast.LENGTH_SHORT).show();
            if(!success) {
                return;
            }
        }

        boolean dirTest = dreamDir.isDirectory();
        if(!dirTest){
            Toast.makeText(getApplicationContext(),"Dream directory is not a Directory",Toast.LENGTH_SHORT).show();
            return;

        }

        boolean fileTest = dreamDir.isFile();
        if(fileTest){
            Toast.makeText(getApplicationContext(),"Dream directory is a file....not good",Toast.LENGTH_SHORT).show();
            dreamDir.delete();
            return;
        }

        String filename = mDream.getDate() + ".txt";
        File newFile = new File(this.getFilesDir(), DREAM_DIR_NAME + "/" + filename);
        String content = mDream.getDreamContent();

        try{
            FileOutputStream f = new FileOutputStream(newFile);
            PrintWriter pw = new PrintWriter(f);
            pw.append(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private LinkedHashMap<UUID, DreamContent.Dream> loadDreams(){
       dreamMap = new LinkedHashMap<UUID, DreamContent.Dream>();

        File dreamDir = new File(this.getFilesDir(), DREAM_DIR_NAME);

        if(dreamDir == null|| !dreamDir.isDirectory()){
            return dreamMap;
        }

        String[] dreamFileNames = dreamDir.list();
        if(dreamFileNames != null){

            if (dreamFileNames.length > 0) {
                for (File file : dreamDir.listFiles()) {
                    StringBuilder builder = new StringBuilder();

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = br.readLine()) != null) {
                            builder.append(line);
                            builder.append('\n');
                        }
                        br.close();

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MMM_dd_KK_mm");
                        try {
                            String filename = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1);
                            String date = filename.split("\\.")[0];
                            cal.setTime(formatter.parse(date));
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                        }

                        DreamContent.Dream dream = new DreamContent.Dream(cal, builder.toString());
                        dreamMap.put(dream.id, dream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        return dreamMap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(mDream == null){
                        mDream = new DreamContent.Dream(text.get(0));
                        dreamMap.put(mDream.id, mDream);
                    }else {
                        String dc = mDream.getDreamContent()+ "\n" + text.get(0);
                        mDream.setDreamContent(dc);
                        dreamMap.put(mDream.id, mDream);
                    }
                    saveDreams();
                    expListView.expandGroup(dreamMap.size() - 1);
                }
                break;
        }
    }
}
