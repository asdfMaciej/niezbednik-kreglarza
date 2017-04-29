package io.maciej01.niezbednikkreglarza;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Maciej on 2017-03-30.
 */

public class ActivitySwipeDetector implements View.OnTouchListener {

    static final int MIN_DISTANCE = 200;
    private float downX, downY, upX, upY;

    public ActivitySwipeDetector() {}

    public void onRightToLeftSwipe() {

    }

    public void onLeftToRightSwipe(){
    }

    public void onTopToBottomSwipe(){

    }

    public void onBottomToTopSwipe(){

    }

    public boolean onTouch(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                //   return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // swipe horizontal?
                if(Math.abs(deltaX) > MIN_DISTANCE){
                    // left or right
                    if(deltaX < 0) { this.onLeftToRightSwipe(); return true; }
                    if(deltaX > 0) { this.onRightToLeftSwipe(); return true; }
                } else {

                }

                // swipe vertical?
                if(Math.abs(deltaY) > MIN_DISTANCE){
                    // top or down
                    if(deltaY < 0) { this.onTopToBottomSwipe(); return true; }
                    if(deltaY > 0) { this.onBottomToTopSwipe(); return true; }
                } else { }

                //     return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
