package com.rawalinfocom.rcontact.helper.circleprogressview;

/**
 * Created by Monal on 03/10/17.
 */

public interface AnimationStateChangedListener {

    /**
     * Call if animation state changes.
     * This code runs in the animation loop, so keep your code short!
     *
     * @param _animationState The new animation state
     */
    void onAnimationStateChanged(AnimationState _animationState);
}
