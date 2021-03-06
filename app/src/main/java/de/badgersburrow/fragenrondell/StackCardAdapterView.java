package de.badgersburrow.fragenrondell;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.FrameLayout;

/**
 * Created by dionysis_lorentzos on 5/8/14
 * for package com.lorentzos.swipecards
 * and project Swipe cards.
 * Use with caution dinosaurs might appear!
 */

public class StackCardAdapterView extends BaseCardAdapterView {


    private int MAX_VISIBLE = 6;
    private int MIN_ADAPTER_STACK = 6;
    private float ROTATION_DEGREES = 15.f;

    private Adapter mAdapter;
    private int LAST_OBJECT_IN_STACK = 0;
    private int LAST_OBJECT_IN_DISMISS_STACK = 0;
    private onMoveListener mMoveListener;
    private AdapterDataSetObserver mDataSetObserver;
    private boolean mInLayout = false;
    private View mActiveCard = null;
    private View mDismissActiveCard = null;
    private OnItemClickListener mOnItemClickListener;
    private MainStackCardListener mainStackCardListener;
    private DismissStackCardListener dismissStackCardListener;
    private PointF mLastTouchPoint;

    private int moveCardDeltaElevation = 5;
    private int position = Activity_Mode_FreePlay.position;
    private int oldPosition = Activity_Mode_FreePlay.position;

    public StackCardAdapterView(Context context) {
        this(context, null);
    }

    public StackCardAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.SwipeFlingStyle);
    }

    public StackCardAdapterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StackCardAdapterView, defStyle, 0);
        MAX_VISIBLE = a.getInt(R.styleable.StackCardAdapterView_max_visible, MAX_VISIBLE);
        MIN_ADAPTER_STACK = a.getInt(R.styleable.StackCardAdapterView_min_adapter_stack, MIN_ADAPTER_STACK);
        ROTATION_DEGREES = a.getFloat(R.styleable.StackCardAdapterView_rotation_degrees, ROTATION_DEGREES);
        a.recycle();
    }


    /**
     * A shortcut method to set both the listeners and the adapter.
     *
     * @param context The activity context which extends onFlingListener, OnItemClickListener or both
     * @param mAdapter The adapter you have to set.
     */
    public void init(final Context context, Adapter mAdapter) {
        if(context instanceof onMoveListener) {
            mMoveListener = (onMoveListener) context;
        }else{
            throw new RuntimeException("Activity does not implement SwipeFlingAdapterView.onFlingListener");
        }
        if(context instanceof OnItemClickListener){
            mOnItemClickListener = (OnItemClickListener) context;
        }
        setAdapter(mAdapter);
    }

    @Override
    public View getSelectedView() {
        return mActiveCard;
    }


    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        position = Activity_Mode_FreePlay.position;
        // if we don't have an adapter, we don't need to do anything
        if (mAdapter == null) {
            return;
        }

        mInLayout = true;
        final int adapterCount = mAdapter.getCount();

        if(adapterCount == 0) {
            removeAllViewsInLayout();
        }else {

            View topCard = getChildAt(LAST_OBJECT_IN_STACK - position+oldPosition);
            if(mActiveCard!=null && topCard!=null && topCard==mActiveCard) {
                if (this.mainStackCardListener.isTouching()) {
                    PointF lastPoint = this.mainStackCardListener.getLastPoint();
                    if (this.mLastTouchPoint == null || !this.mLastTouchPoint.equals(lastPoint)) {
                        this.mLastTouchPoint = lastPoint;
                        removeViewsInLayout(0, LAST_OBJECT_IN_STACK);
                        layoutChildren(1, adapterCount);
                    }
                }
            }else{
                // Reset the UI and set top view listener
                removeAllViewsInLayout();
                layoutChildren(0, adapterCount);
                setTopView();
            }
        }

        mInLayout = false;

        if(adapterCount <= MIN_ADAPTER_STACK) mMoveListener.onAdapterAboutToEmpty(adapterCount);
    }


    private void layoutChildren(int startingIndex, int adapterCount){
        position = Activity_Mode_FreePlay.position;
        oldPosition = position;
        int mainCount = 0;
        for (int i=position; i< Math.min(adapterCount, MAX_VISIBLE+position-Math.min(position,MAX_VISIBLE/2));i++){
            View newUnderChild = mAdapter.getView(i, null, this);
            if (newUnderChild.getVisibility() != GONE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    newUnderChild.setElevation(adapterCount-i);
                }
                makeAndAddView(newUnderChild, false);

            }
            mainCount++;
        }
        int dismissCount = 0;
        LAST_OBJECT_IN_DISMISS_STACK = -1;
        for (int i=position-1; i>= position - (MAX_VISIBLE-mainCount);i--){
            View newUnderChild = mAdapter.getView(i, null, this);
            if (newUnderChild.getVisibility() != GONE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    newUnderChild.setElevation(i+1);
                }
                makeAndAddView(newUnderChild, true);

                LAST_OBJECT_IN_DISMISS_STACK = dismissCount;
            }
            dismissCount++;
        }
        if (adapterCount==position){
            LAST_OBJECT_IN_STACK = -1;
        }else{
            LAST_OBJECT_IN_STACK = Math.min(MAX_VISIBLE,adapterCount)-1;
        }


        /*
        while (count <  ) {
            View newUnderChild = mAdapter.getView(startingIndex, null, this);
            if (newUnderChild.getVisibility() != GONE) {
                if (count<position){
                    makeAndAddView(newUnderChild, true);
                } else{
                    makeAndAddView(newUnderChild, false);
                }

                LAST_OBJECT_IN_STACK = startingIndex;
            }
            count++;
        }*/
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void makeAndAddView(View child, boolean isDismissStack) {

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        addViewInLayout(child, 0, lp, true);

        final boolean needToMeasure = child.isLayoutRequested();
        if (needToMeasure) {
            int childWidthSpec = getChildMeasureSpec(getWidthMeasureSpec(),
                    getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(getHeightMeasureSpec(),
                    getPaddingTop() + getPaddingBottom() + lp.topMargin + lp.bottomMargin,
                    lp.height);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }


        int w = child.getMeasuredWidth();
        int h = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }


        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight()  - w) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - w - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom()  - h) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - h - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }
        if (isDismissStack){
            child.layout(-w*4/5, childTop, -w*4/5 + w, childTop + h);
        } else {
            child.layout(childLeft, childTop, childLeft + w, childTop + h);
        }

    }




    /**
     *  Set the top view and add the fling listener
     */
    private void setTopView() {
        position = Activity_Mode_FreePlay.position;
        int adapterCount = mAdapter.getCount();
        float deltaElevation = 0f;

        if(getChildCount()>0){

            mActiveCard = getChildAt(LAST_OBJECT_IN_STACK);
            if(mActiveCard!=null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    deltaElevation = Math.max(adapterCount-position,position)-mActiveCard.getElevation()+moveCardDeltaElevation;
                }

                mainStackCardListener = new MainStackCardListener(mActiveCard, mAdapter.getItem(position),
                        deltaElevation,
                        ROTATION_DEGREES, new MainStackCardListener.MoveListener() {

                    @Override
                    public void toDismissStack(Object dataObject) {
                        mMoveListener.onDismissCard(dataObject);
                    }

                    @Override
                    public void toMainStack(Object dataObject) {
                        mMoveListener.onRetractCard(dataObject);
                    }

                    @Override
                    public void onClick(Object dataObject) {
                        if(mOnItemClickListener!=null)
                            mOnItemClickListener.onItemClicked(0, dataObject);

                    }

                    @Override
                    public void onScroll(float scrollProgressPercent) {
                        mMoveListener.onScroll(scrollProgressPercent);
                    }
                });

                mActiveCard.setOnTouchListener(mainStackCardListener);
            }

            mDismissActiveCard = getChildAt(LAST_OBJECT_IN_DISMISS_STACK);
            if(mDismissActiveCard!=null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    deltaElevation = Math.max(adapterCount-position,position)-mDismissActiveCard.getElevation()+moveCardDeltaElevation;
                }

                dismissStackCardListener = new DismissStackCardListener(mDismissActiveCard, mAdapter.getItem(position-1),
                        deltaElevation,
                        ROTATION_DEGREES, new DismissStackCardListener.MoveListener() {

                    @Override
                    public void toDismissStack(Object dataObject) {
                        mMoveListener.onDismissCard(dataObject);
                    }

                    @Override
                    public void toMainStack(Object dataObject) {
                        mMoveListener.onRetractCard(dataObject);
                    }

                    @Override
                    public void onClick(Object dataObject) {
                        if(mOnItemClickListener!=null)
                            mOnItemClickListener.onItemClicked(0, dataObject);

                    }

                    @Override
                    public void onScroll(float scrollProgressPercent) {
                        mMoveListener.onScroll(scrollProgressPercent);
                    }
                });

                mDismissActiveCard.setOnTouchListener(dismissStackCardListener);
            }
        }
    }

    public MainStackCardListener getTopCardListener() throws NullPointerException{
        if(mainStackCardListener ==null){
            throw new NullPointerException();
        }
        return mainStackCardListener;
    }

    public void setMaxVisible(int MAX_VISIBLE){
        this.MAX_VISIBLE = MAX_VISIBLE;
    }

    public void setMinStackInAdapter(int MIN_ADAPTER_STACK){
        this.MIN_ADAPTER_STACK = MIN_ADAPTER_STACK;
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
            mDataSetObserver = null;
        }

        mAdapter = adapter;

        if (mAdapter != null  && mDataSetObserver == null) {
            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }
    }

    public void setMoveListener(onMoveListener onMoveListener) {
        this.mMoveListener = onMoveListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }




    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FrameLayout.LayoutParams(getContext(), attrs);
    }


    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            requestLayout();
        }

    }


    public interface OnItemClickListener {
        void onItemClicked(int itemPosition, Object dataObject);
    }

    public interface onMoveListener {
        void onDismissCard(Object dataObject);
        void onRetractCard(Object dataObject);
        void onAdapterAboutToEmpty(int itemsInAdapter);
        void onScroll(float scrollProgressPercent);
    }


}