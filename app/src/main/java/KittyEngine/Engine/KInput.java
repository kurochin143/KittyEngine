package KittyEngine.Engine;

import android.view.MotionEvent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import KittyEngine.Container.KArrayList;

public class KInput {

    // @TODO more input types

    private Lock m_mutex_eventsAccess = new ReentrantLock();

    private boolean m_bMotionEvent_THREAD_UNSAFE;
    private MotionEvent m_lastMotionEvent_THREAD_UNSAFE;
    private KArrayList<MotionEventObject> m_motionEventListeners = new KArrayList<>();

    private boolean m_bPressing;

    public void recieveMotionEvent(MotionEvent e) {
        m_mutex_eventsAccess.lock();
        m_bMotionEvent_THREAD_UNSAFE = true;
        m_lastMotionEvent_THREAD_UNSAFE = MotionEvent.obtain(e);
        m_mutex_eventsAccess.unlock();
    }

    public void dispatch() {
        MotionEvent lastMotionEvent_threadSafe;
        m_mutex_eventsAccess.lock();
        if (m_bMotionEvent_THREAD_UNSAFE) {
            m_bMotionEvent_THREAD_UNSAFE = false;
            lastMotionEvent_threadSafe = MotionEvent.obtain(m_lastMotionEvent_THREAD_UNSAFE);
        }
        else {
            lastMotionEvent_threadSafe = null;
        }

        m_mutex_eventsAccess.unlock();

        if (lastMotionEvent_threadSafe != null) {
            int action = lastMotionEvent_threadSafe.getAction();

            if (m_bPressing) {
                if (action == MotionEvent.ACTION_DOWN ||
                        action == MotionEvent.ACTION_BUTTON_RELEASE ||
                        action == MotionEvent.ACTION_HOVER_EXIT) {
                    m_bPressing = false;
                }
            }
            else {
                if (action == MotionEvent.ACTION_UP ||
                        action == MotionEvent.ACTION_BUTTON_PRESS ||
                        action == MotionEvent.ACTION_MOVE) {
                    m_bPressing = true;
                }
            }

        }

        // dispatch motion event
        if (lastMotionEvent_threadSafe != null) {
            for (int i = m_motionEventListeners.lastIndex(); i != -1; --i) {
                m_motionEventListeners.get(i).motionEventListener.onMotionEvent(lastMotionEvent_threadSafe);
            }
        }
    }

    public void addMotionEventListener(KObject object, MotionEventListener motionEventListener) {
        m_motionEventListeners.add(new MotionEventObject(object, motionEventListener));
    }

    public void removeMotionEventListener(final KObject object) {
        int i = m_motionEventListeners.findByPredicate(new KArrayList.FindPredicate() {
            @Override
            public boolean found(Object arrayObject) {
                return ((MotionEventObject)arrayObject).object == object;
            }
        });

        m_motionEventListeners.removeSwap(i);
    }

    public interface MotionEventListener {
        void onMotionEvent(final MotionEvent e);
    }

    private class MotionEventObject {
        public MotionEventObject(KObject inObject, MotionEventListener inMotionEventListener) {
            object = inObject;
            motionEventListener = inMotionEventListener;
        }

        public KObject object;
        public MotionEventListener motionEventListener;
    }

}
