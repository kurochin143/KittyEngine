package KittyGameTest;

import android.util.Log;
import android.view.MotionEvent;

import com.example.israel.kittyengine.R;

import KittyEngine.Container.KArrayList;
import KittyEngine.Engine.KEngine;
import KittyEngine.Engine.KGame;
import KittyEngine.Engine.KInput;
import KittyEngine.Engine.KObject;
import KittyEngine.Graphics.KHUDRenderer;
import KittyEngine.Graphics.KSprite;
import KittyEngine.Graphics.KTexture;
import KittyEngine.Math.KVec2;
import KittyEngine.Math.KVec4;

public class TGame extends KGame {

    @Override
    public void onEngineStarted() {
        super.onEngineStarted();

        KObject newObject = new KObject(this);
        newObject.setMotionEventListener(new KInput.MotionEventListener() {
            @Override
            public void onMotionEvent(MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN ||
                        e.getAction() == MotionEvent.ACTION_BUTTON_PRESS ||
                        e.getAction() == MotionEvent.ACTION_MOVE) {
                    Circle circle = new Circle();
                    circle.duration = 10.f;
                    circle.screenPosition = new KVec2(e.getX(), e.getY());
                    circle.screenRadius = 5.f;
                    circle.color = new KVec4(1.f);
                    circles.add(circle);
                }
            }
        });

        KSprite sprite = new KSprite();
        sprite.setTexture(KTexture.getTexture("textures/awesomeface.png"));
    }

    KVec2 currentP = new KVec2(100.f, 100.f);

    class Circle {
        float duration;
        float elapsedTime;

        KVec2 screenPosition;
        float screenRadius;
        KVec4 color;
        boolean bFilled = true;
        int sides = 5;
        float unfilledThickness = 1.f;
    }

    KArrayList<Circle> circles = new KArrayList<>();

    @Override
    public void update(float deltaSeconds) {
        super.update(deltaSeconds);

        KHUDRenderer HUDRenderer = KEngine.get().getHUDRenderer();

        for (int i = circles.lastIndex(); i != -1; --i) {
            Circle circle = circles.get(i);
            circle.elapsedTime += deltaSeconds;
            if (circle.elapsedTime > circle.duration) {
                circles.removeSwap(i);
            }
            else {
                HUDRenderer.drawCircle(circle.screenPosition, circle.screenRadius, circle.color, circle.bFilled, circle.sides, circle.unfilledThickness);
            }
        }

        currentP = currentP.add(new KVec2(-1.f, 1.f));
        HUDRenderer.drawCircle(currentP, 100.f, new KVec4(1.f, 1.f, 0.f, 1.f), true, 60, 1.f);
        HUDRenderer.drawCircle(new KVec2(200.f, 100.f), 200.f, new KVec4(0.f, 0.f, 1.f, 1.f), false, 60, 1.f);

        HUDRenderer.drawCircle(new KVec2(0.f), 100.f, new KVec4(1.f, 0.f, 0.f, 1.f), true, 60, 1.f);

        HUDRenderer.drawLine(new KVec2(-100.f, 100.f), new KVec2(100.f, 100.f), new KVec4(1.f, 1.f, 1.f, 1.f), 10.f);
        KVec2[] squareVertices = new KVec2[4];

        squareVertices[0] = new KVec2(-100.f, -100.f);
        squareVertices[1] = new KVec2(100.f, -100.f);
        squareVertices[2] = new KVec2(100.f, -200.f);
        squareVertices[3] = new KVec2(-100.f, -200.f);
        HUDRenderer.drawConvexPolygon(squareVertices, new KVec4(0.f, 1.f, 1.f, 1.f), true, 1.f);

        squareVertices[0] = new KVec2(-200.f, 200.f);
        squareVertices[1] = new KVec2(200.f, 200.f);
        squareVertices[2] = new KVec2(200.f, 300.f);
        squareVertices[3] = new KVec2(-200.f, 300.f);
        HUDRenderer.drawConvexPolygon(squareVertices, new KVec4(0.f, 1.f, 1.f, 1.f), false, 5.f);

    }

}
