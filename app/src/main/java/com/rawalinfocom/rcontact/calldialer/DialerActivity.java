package com.rawalinfocom.rcontact.calldialer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.calldialer.transition.ScaleTransition;

/**
 * Created by kevingrant on 3/26/15.
 */
public class DialerActivity extends Activity {

	View mCallButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialer);

		mCallButton = findViewById(R.id.call_button);
		mCallButton.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				finishAfterTransition();
			}
		});

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setEnterTransition(makeEnterTransition());
			getWindow().setReturnTransition(makeReturnTransition());
		}

	}
	@SuppressLint("NewApi")
	private Transition makeReturnTransition() {
		// Return a set here, we could add as many transition animations here as we want
		TransitionSet enterTransition = new TransitionSet();

		Transition slide = new Slide(Gravity.BOTTOM);
		slide.setDuration(500);

		slide.excludeTarget(getActionBarView(), true);
		slide.excludeTarget(android.R.id.navigationBarBackground, true);
		slide.excludeTarget(android.R.id.statusBarBackground, true);
		slide.setInterpolator(new AccelerateInterpolator());

		enterTransition.addTransition(slide);
		enterTransition.setOrdering(TransitionSet.ORDERING_TOGETHER);

		return enterTransition;
	}

	@SuppressLint("NewApi")
	private Transition makeEnterTransition() {
		// Return a set here, we could add as many transition animations here as we want
		TransitionSet enterTransition = new TransitionSet();

		Transition slide = new Slide(Gravity.BOTTOM);
		slide.setDuration(500);

		slide.excludeTarget(getActionBarView(), true);
		slide.excludeTarget(android.R.id.navigationBarBackground, true);
		slide.excludeTarget(android.R.id.statusBarBackground, true);
		slide.setInterpolator(new DecelerateInterpolator());
		enterTransition.addTransition(slide);

		// Exclude call button from the slide
		slide.excludeTarget(mCallButton, true);

		// Give the slide button a scale transition
		Transition scale = new ScaleTransition();
		scale.addTarget(mCallButton);
		enterTransition.addTransition(scale);

		// Set the ordering to sequential so that this happens after the slide
		enterTransition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

		return enterTransition;
	}

	private View getActionBarView() {
		final int actionBarId = getResources().getIdentifier("action_bar_container", "id", "android");
		final View decor = getWindow().getDecorView();
		return decor.findViewById(actionBarId);
	}
}
