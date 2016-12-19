package com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.sectionindicator.animation;

/**
 * Created by user on 08/12/16.
 */

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Utility class for animating the popup section indicator
 */
public class DefaultSectionIndicatorAlphaAnimator {

    private static final int ANIMATION_DURATION = 500;

    private final View mSectionIndicatorView;
    private float mTargetAlpha = 0;

    public DefaultSectionIndicatorAlphaAnimator(View sectionIndicatorView) {
        mSectionIndicatorView = sectionIndicatorView;
        mSectionIndicatorView.setAlpha(0);
    }

    public void animateTo(float target) {
        if (target == mTargetAlpha) {
            return;
        }

        ObjectAnimator alphaAnimator =
                ObjectAnimator.ofFloat(mSectionIndicatorView, "alpha", mTargetAlpha, target);
        alphaAnimator.setDuration(ANIMATION_DURATION);
        alphaAnimator.start();
        mTargetAlpha = target;
    }
}
