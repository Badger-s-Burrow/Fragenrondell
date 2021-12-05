package de.badgersburrow.fragenrondell.transitions;

import android.animation.Animator;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Joe on 28.04.2016.
 */
public class mainToModeSelect extends Transition {

    // Define a key for storing a property value in
    // TransitionValues.values with the syntax
    // package_name:transition_class:property_name to avoid collisions
    private static final String PROPNAME_BACKGROUND =
            "com.littleones.fragenrondell.maintomodeselect:MainToModeSelect:background";

    @Override
    public void captureStartValues(TransitionValues values) {
        // Call the convenience method captureValues
        captureValues(values);
    }

    @Override
    public void captureEndValues(TransitionValues values) {
        captureValues(values);
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
        return null;
    }

}

