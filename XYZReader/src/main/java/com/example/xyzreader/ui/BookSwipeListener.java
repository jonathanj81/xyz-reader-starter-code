package com.example.xyzreader.ui;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class BookSwipeListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public BookSwipeListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public GestureDetector getGestureDetector(){
        return  gestureDetector;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int MIN_SWIPE_DISTANCE = 50;
        private static final int MIN_SWIPE_VELOCITY = 50;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > MIN_SWIPE_DISTANCE && Math.abs(velocityX) > MIN_SWIPE_VELOCITY) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}
