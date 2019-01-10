package com.example.israel.kittyengine;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static String triangleVert;
    public static String triangleFrag;

    private GLSurfaceView m_GLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        triangleVert = readRaw(R.raw.triangle_vert);
        triangleFrag = readRaw(R.raw.triangle_frag);

        m_GLView = new KGLSurfaceView(this);
        setContentView(m_GLView);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();

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
