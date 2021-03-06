package com.example.israel.kittyengine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.InputStream;

import KittyEngine.Engine.KEngine;
import KittyGameTest.TGame;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //Log.i("KittyLogTest", "MainActivity thread: " + Thread.currentThread().getName()); // main is the name

        // engine should only be created once
        KEngine newEngine = new KEngine(this);
        TGame newGame = new TGame();
        newEngine.addGame(newGame);
        newEngine.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public String readRaw(int id) {
        try {
            InputStream in_s = getResources().openRawResource(id);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            return new String(b);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
