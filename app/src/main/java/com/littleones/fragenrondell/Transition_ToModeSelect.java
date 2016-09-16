package com.littleones.fragenrondell;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joe on 24.04.2016.
 */
public class Transition_ToModeSelect extends Transition {


    // Define a key for storing a property value in
    // TransitionValues.values with the syntax
    // package_name:transition_class:property_name to avoid collisions
    private static final String PROPNAME_BACKGROUND =
            "com.littleones.fragenrondell.transition_tomodeselect:Transition_ToModeSelect:background";

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        // Call the convenience method captureValues
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    // For the view in transitionValues.view, get the values you
    // want and put them in transitionValues.values
    private void captureValues(TransitionValues transitionValues) {
        // Get a reference to the view
        View view = transitionValues.view;
        // Store its background property in the values map
        transitionValues.values.put(PROPNAME_BACKGROUND, view.getBackground());
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot,
                                   TransitionValues startValues,
                                   TransitionValues endValues) {
        if (null == startValues || null == endValues) {
            return null;
        }

        final View view = endValues.view;

        Drawable startBackground =
                (Drawable) startValues.values.get(PROPNAME_BACKGROUND);
        Drawable endBackground =
                (Drawable) endValues.values.get(PROPNAME_BACKGROUND);

        ColorDrawable startColor = (ColorDrawable) startBackground;
        ColorDrawable endColor = (ColorDrawable) endBackground;

        if (startColor.getColor() == endColor.getColor()) {
            return null;
        }

        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(),
                startColor.getColor(), endColor.getColor());
        animator
                .addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Object value = animation.getAnimatedValue();
                        if (null != value) {
                            view.setBackgroundColor((Integer) value);
                        }
                    }
                });
        return animator;
    }

}
