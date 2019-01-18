package KittyEngine.Engine;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import KittyEngine.Container.KArrayList;
import KittyEngine.Graphics.KGLSurfaceView;
import KittyEngine.Graphics.KHUDRenderer;

public class KEngine {

    public KEngine(AppCompatActivity activity){
        if (sm_engine != null) {
            Log.w("KittyLog", "KEngine already created");
            return;
        }

        Log.i("KittyLog", "KEngine successfully created");

        sm_engine = this;

        m_input = new KInput();

        m_GLSurfaceView = new KGLSurfaceView(activity, m_input);
        activity.setContentView(m_GLSurfaceView);
    }

    private static KEngine sm_engine;
    private EngineThread m_engineThread;

    private KInput m_input;
    private KGLSurfaceView m_GLSurfaceView;

    private boolean m_bRunning;

    private float m_targetFps = 120.f;
    private float m_targetMilliSecondsPerFrame = 1000.f / m_targetFps;
    private long m_targetNanoSecondsPerFrame = (long)((1.f / m_targetFps) * 1000000000);
    private int m_fps;

    private KArrayList<KGame> m_games = new KArrayList<>();

    public static KEngine get() {
        return sm_engine;
    }

    public Thread getThread() {
        return m_engineThread;
    }

    public KInput getInput() {
        return m_input;
    }

    public KGLSurfaceView getGLSurfaceView() {
        return m_GLSurfaceView;
    }

    public KHUDRenderer getHUDRenderer() {
        return m_GLSurfaceView.getRenderer().getHUDRenderer();
    }

    public boolean isRunning() {
        return m_bRunning;
    }

    public void start() {
        if (m_bRunning) {
            return;
        }

        m_engineThread = new EngineThread();
        m_engineThread.start();
    }
    public void stop() {
        m_bRunning = false;

        // @TODO should the engine be able to restart after stopping it once
    }

    public float getTargetFps() {
        return m_targetFps;
    }

    public void setTargetFps(float newTargetFps) {
        if (m_targetFps < 0.f) {
            return;
        }

        m_targetFps = newTargetFps;

        if (m_targetFps == 0.f) {
            m_targetMilliSecondsPerFrame = 0.f;
        }

        m_targetMilliSecondsPerFrame = 1000.f / m_targetFps;
    }

    public int getFps() {
        return m_fps;
    }

    public void addGame(KGame game) {
        m_games.add(game);
    }

    public void removeGame(KGame game) {
        m_games.set(m_games.find(game), null);
    }

    private class EngineThread extends Thread {

        public EngineThread() {
            super("Kitty Engine thread");
        }

        @Override
        public void run() {
            super.run();

            Log.i("KittyLog", "KEngine started");

            // wait until the first draw call is finished
            while (!m_GLSurfaceView.isRenderFinished()) {

            }

            m_bRunning = true;

            // initialize all games
            for (KGame game : m_games) {
                game.onEngineStarted();
            }

            float elapsedMilliSecondsSinceLastFPSCheck = 0.f;
            int frameNum = 0;

            // @TODO use System.nanoTime();
            float deltaMilliSeconds;
            double lastTickRealTime = (double)SystemClock.elapsedRealtime();
            while (m_bRunning) {
                double elapsedRealTime = (double)SystemClock.elapsedRealtime();
                deltaMilliSeconds = (float)(elapsedRealTime - lastTickRealTime);

                if (deltaMilliSeconds < m_targetMilliSecondsPerFrame) {
                    continue;
                }

                // do a frame

                lastTickRealTime = elapsedRealTime;

                elapsedMilliSecondsSinceLastFPSCheck += deltaMilliSeconds;
                frameNum += 1;
                if (elapsedMilliSecondsSinceLastFPSCheck >= 1000.f) {
                    m_fps = frameNum;
                    String FPSStr = "fps: ";
                    FPSStr += String.valueOf(frameNum);
                    Log.i("KittyLog", FPSStr);
                    frameNum = 0;
                    elapsedMilliSecondsSinceLastFPSCheck = 0.f;
                }

                float deltaSeconds = deltaMilliSeconds * 0.001f;

                // set this to max because this thread will be doing heavy stuff
                m_engineThread.setPriority(MAX_PRIORITY);
                // input
                m_input.dispatch();

                // game
                for (int i = m_games.lastIndex(); i != -1; --i) {
                    KGame game = m_games.get(i);
                    if (game != null) {
                        m_games.get(i).update(deltaSeconds);
                    }
                    else {
                        m_games.removeSwap(m_games.size() - 1);
                    }
                }

                /** game objects must be immutable at this point */

                // lower priority because this thread will not be doing anything anyway
                m_engineThread.setPriority(MIN_PRIORITY);
                // render
                m_GLSurfaceView.requestRender();
                // wait until renderer has finished drawing
                while (!m_GLSurfaceView.isRenderFinished()) {

                }
                // set back to normal priority because this thread will be doing time calculation
                m_engineThread.setPriority(NORM_PRIORITY);
            }

            Log.i("KittyLog", "KEngine stopped");
        }
    }

    public class FPSCounter {
        long startTime = System.nanoTime();
        int frames = 0;

        public void logFrame(String name) {
            frames++;
            long currentTime = System.nanoTime();
            if(currentTime - startTime >= 1000000000) {
                Log.d("KittyLog", name + "fps: " + frames);
                frames = 0;
                startTime = currentTime;
            }
        }
    }

}
