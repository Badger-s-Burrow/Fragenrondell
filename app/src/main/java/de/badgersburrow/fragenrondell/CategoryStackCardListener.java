package de.badgersburrow.fragenrondell;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinausaurs might appear!
 */


public class CategoryStackCardListener implements View.OnTouchListener {

    private static final String TAG = CategoryStackCardListener.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;

    private final float objectX;
    private final float objectW;
    private final float boxTopWidth;
    private final float boxBottomMargin;
    private final MoveListener mMoveListener;
    private final Category category;
    private final float halfWidth;

    private float aPosX;
    private float aDownTouchX;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;


    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    private final Object obj = new Object();
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));

    public CategoryStackCardListener(View frame, Object itemAtPosition, float boxTopWidth, float boxBottomMargin, MoveListener moveListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.boxTopWidth = boxTopWidth;
        this.boxBottomMargin = boxBottomMargin;
        this.category = (Category) itemAtPosition;
        this.mMoveListener = moveListener;
    }


    public boolean onTouch(View view, MotionEvent event) {
        if (this.category.get_state() == 0) {
            return false;
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                    // Save the ID of this pointer
                    mActivePointerId = event.getPointerId(0);
                    float x = 0;
                    boolean success = false;
                    try {
                        x = event.getX(mActivePointerId);
                        success = true;
                    } catch (IllegalArgumentException e) {
                        Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
                    }
                    if (x<boxTopWidth-frame.getX()){
                        return false;
                    }

                    if (success) {
                        // Remember where we started
                        aDownTouchX = x;
                        //to prevent an initial jump of the magnifier, aposX and aPosY must
                        //have the values from the magnifier frame
                        if (aPosX == 0) {
                            aPosX = frame.getX();
                        }
                    }

                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    mActivePointerId = INVALID_POINTER_ID;
                    changeCardState();
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    // Extract the index of the pointer that left the touch sensor
                    final int pointerIndex = (event.getAction() &
                            MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mActivePointerId = event.getPointerId(newPointerIndex);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    // Find the index of the active pointer and fetch its position
                    final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                    final float xMove = event.getX(pointerIndexMove);

                    final float dx = xMove - aDownTouchX;

                    // Move the frame
                    aPosX += dx;

                    //in this area would be code for doing something with the view as the frame moves.
                    frame.setX(Math.max(aPosX, boxBottomMargin));
                    break;

                case MotionEvent.ACTION_CANCEL:
                    mActivePointerId = INVALID_POINTER_ID;
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;

            }
            return true;



        }
        //return false;
    }

    private boolean changeCardState() {
        if (category.get_state() == 1) {
            if (movedToSelect()) {
                aPosX = 0;
                aDownTouchX = 0;
                frame.animate()
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(1.5f))
                        .x(boxTopWidth/2.f);
                category.set_state(2);
                Activity_CategorySelect.num_selected_categories.inc();
                return true;
            } else {
                aPosX = 0;
                aDownTouchX = 0;
                frame.animate()
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(1.5f))
                        .x(boxBottomMargin);
            }
        } else if (category.get_state() == 2){
            if (movedToUnselect()) {
                aPosX = 0;
                aDownTouchX = 0;
                frame.animate()
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(1.5f))
                        .x(boxBottomMargin);
                category.set_state(1);
                Activity_CategorySelect.num_selected_categories.dec();
                return true;
            } else {
                aPosX = 0;
                aDownTouchX = 0;
                frame.animate()
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(1.5f))
                        .x(boxTopWidth/2.f);

            }
        }
        return false;
    }

    private boolean movedToSelect() {
        return aPosX > boxTopWidth/9.f*4.5f;
    }

    private boolean movedToUnselect() {
        return aPosX < boxTopWidth/9.f*4.f;
    }


    protected interface MoveListener {

        void selectCategory(Object dataObject);

        void unselectCategory(Object dataObject);

    }

}





