package com.littleones.fragenrondell;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.PointF;
import android.media.AudioManager;
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


public class DismissStackCardListener implements View.OnTouchListener {

    private static final String TAG = DismissStackCardListener.class.getSimpleName();
    private static final int INVALID_POINTER_ID = -1;

    private final float objectX;
    private final float objectY;
    private final int objectH;
    private final int objectW;
    private final int parentWidth;
    private final int parentHeight;
    private final MoveListener mMoveListener;
    private final Object dataObject;
    private final float halfWidth;
    private float BASE_ROTATION_DEGREES;
    private float moveCardDeltaElevation;


    private float aPosX;
    private float aPosY;
    private float aDownTouchX;
    private float aDownTouchY;

    // The active pointer is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;
    private View frame = null;


    private final int TOUCH_ABOVE = 0;
    private final int TOUCH_BELOW = 1;
    private int touchPosition;
    private final Object obj = new Object();
    private boolean isAnimationRunning = false;
    private float MAX_COS = (float) Math.cos(Math.toRadians(45));



    public DismissStackCardListener(View frame, Object itemAtPosition, MoveListener moveListener) {
        this(frame, itemAtPosition, 0, 15f, moveListener);
    }

    public DismissStackCardListener(View frame, Object itemAtPosition, float deltaElevation, float rotation_degrees, MoveListener moveListener) {
        super();
        this.frame = frame;
        this.objectX = frame.getX();
        this.objectY = frame.getY();
        this.objectH = frame.getHeight();
        this.objectW = frame.getWidth();
        this.halfWidth = objectW / 2f;
        this.dataObject = itemAtPosition;
        this.parentWidth = ((ViewGroup) frame.getParent()).getWidth();
        this.parentHeight = ((ViewGroup) frame.getParent()).getHeight();
        this.BASE_ROTATION_DEGREES = rotation_degrees;
        this.mMoveListener = moveListener;
        this.moveCardDeltaElevation = deltaElevation;

    }


    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Save the ID of this pointer

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.setElevation(view.getElevation()+moveCardDeltaElevation);
                }

                mActivePointerId = event.getPointerId(0);
                float x = 0;
                float y = 0;
                boolean success = false;
                try {
                    x = event.getX(mActivePointerId);
                    y = event.getY(mActivePointerId);
                    success = true;
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "Exception in onTouch(view, event) : " + mActivePointerId, e);
                }
                if (success) {

                    float actualVolume = (float) Activity_Mode_FreePlay.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    float maxVolume = (float) Activity_Mode_FreePlay.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    float volume = actualVolume/maxVolume;
                    if (Activity_Mode_FreePlay.loaded) {
                        Activity_Mode_FreePlay.soundPool.play(Activity_Mode_FreePlay.soundID_pickcard,volume,volume,1,0,1f);
                        Log.e("Sound_Test","Played pickup card sound");
                    }

                    // Remember where we started
                    aDownTouchX = x;
                    aDownTouchY = y;
                    //to prevent an initial jump of the magnifier, aposX and aPosY must
                    //have the values from the magnifier frame
                    if (aPosX == 0) {
                        aPosX = frame.getX();
                    }
                    if (aPosY == 0) {
                        aPosY = frame.getY();
                    }

                    if (y < objectH / 2) {
                        touchPosition = TOUCH_ABOVE;
                    } else {
                        touchPosition = TOUCH_BELOW;
                    }
                }

                view.getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_UP:
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //    view.setElevation(view.getElevation()-moveCardDeltaElevation);
                //}
                mActivePointerId = INVALID_POINTER_ID;
                resetCardViewOnStack();
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
                final float yMove = event.getY(pointerIndexMove);

                //from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                final float dx = xMove - aDownTouchX;
                final float dy = yMove - aDownTouchY;


                // Move the frame
                aPosX += dx;
                aPosY += dy;

                // calculate the rotation degrees
                float distobjectX = aPosX - objectX;
                float distobjectY = aPosY - objectY;
                float rotation = BASE_ROTATION_DEGREES * 2.f * distobjectY / parentHeight;
                //if (touchPosition == TOUCH_BELOW) {
                //   rotation = -rotation;
                //}

                //in this area would be code for doing something with the view as the frame moves.
                frame.setX(aPosX);
                frame.setY(aPosY);
                frame.setRotation(rotation);
                mMoveListener.onScroll(getScrollProgressPercent());
                break;

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                view.getParent().requestDisallowInterceptTouchEvent(false);
                break;
            }
        }

        return true;
    }

    private float getScrollProgressPercent() {
        if (movedBeyondRightBorder()) {
            return -1f;
        } else {
            float zeroToOneValue = (aPosX + halfWidth - leftBorder()) / (rightBorder() - leftBorder());
            return zeroToOneValue * 2f - 1f;
        }
    }

    private boolean resetCardViewOnStack() {

        float actualVolume = (float) Activity_Mode_FreePlay.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) Activity_Mode_FreePlay.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = actualVolume/maxVolume;
        if (Activity_Mode_FreePlay.loaded) {
            Activity_Mode_FreePlay.soundPool.play(Activity_Mode_FreePlay.soundID_dropcard,volume,volume,1,0,1f);
            Log.e("Sound_Test","Played drop card sound");
        }

        if (movedBeyondRightBorder()) {
            // Left Swipe
            onSelected(true, 100);
            mMoveListener.onScroll(-1.0f);
        } else {
            float abslMoveDistance = Math.abs(aPosX - objectX);
            aPosX = 0;
            aPosY = 0;
            aDownTouchX = 0;
            aDownTouchY = 0;
            frame.animate()
                    .setDuration(200)
                    .setInterpolator(new OvershootInterpolator(1.5f))
                    .x(objectX)
                    .y(objectY)
                    .rotation(0);
            mMoveListener.onScroll(0.0f);
            if (abslMoveDistance < 4.0) {
                mMoveListener.onClick(dataObject);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                frame.setElevation(frame.getElevation() - moveCardDeltaElevation);
            }
        }
        return false;
    }

    private boolean movedBeyondRightBorder() {
        return aPosX + halfWidth > leftBorder();
    }

    public float leftBorder() {
        return parentWidth / 4.f;
    }

    public float rightBorder() {
        return 3 * parentWidth / 4.f;
    }

    public void onSelected(final boolean isDismiss, long duration) {

        isAnimationRunning = true;
        float mainX;
        float mainY;

        mainX = (parentWidth-objectW)/2.f;
        mainY = (parentHeight-objectH)/2.f;


        this.frame.animate()
                .setDuration(duration)
                .setInterpolator(new AccelerateInterpolator())
                .x(mainX)
                .y(mainY)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mMoveListener.toMainStack(dataObject);
                        isAnimationRunning = false;
                    }
                })
                .rotation(0);
    }


    /**
     * Starts a default left exit animation.
     */
    public void dismiss() {
        if (!isAnimationRunning)
            onSelected(true, 200);
    }

    /**
     * Starts a default right exit animation.
     */
    public void retract() {
        if (!isAnimationRunning)
            onSelected(false, 200);
    }


    private float getExitPoint(int exitXPoint) {
        float[] x = new float[2];
        x[0] = objectX;
        x[1] = aPosX;

        float[] y = new float[2];
        y[0] = objectY;
        y[1] = aPosY;

        LinearRegression regression = new LinearRegression(x, y);

        //Your typical y = ax+b linear regression
        return (float) regression.slope() * exitXPoint + (float) regression.intercept();
    }

    private float getExitRotation(boolean isLeft) {
        float rotation = BASE_ROTATION_DEGREES * 2.f * (parentWidth - objectX) / parentWidth;
        if (touchPosition == TOUCH_BELOW) {
            rotation = -rotation;
        }
        if (isLeft) {
            rotation = -rotation;
        }
        return rotation;
    }


    /**
     * When the object rotates it's width becomes bigger.
     * The maximum width is at 45 degrees.
     * <p/>
     * The below method calculates the width offset of the rotation.
     */
    private float getRotationWidthOffset() {
        return objectW / MAX_COS - objectW;
    }


    public void setRotationDegrees(float degrees) {
        this.BASE_ROTATION_DEGREES = degrees;
    }

    public boolean isTouching() {
        return this.mActivePointerId != INVALID_POINTER_ID;
    }

    public PointF getLastPoint() {
        return new PointF(this.aPosX, this.aPosY);
    }

    protected interface MoveListener {

        void toDismissStack(Object dataObject);

        void toMainStack(Object dataObject);

        void onClick(Object dataObject);

        void onScroll(float scrollProgressPercent);
    }

}





