package com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.vertical;

/**
 * Created by user on 08/12/16.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.AbsRecyclerViewFastScroller;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.RecyclerViewScroller;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.calculation
        .VerticalScrollBoundsProvider;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.calculation.position
        .VerticalScreenPositionCalculator;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.calculation.progress
        .TouchableScrollProgressCalculator;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.calculation.progress
        .VerticalLinearLayoutManagerScrollProgressCalculator;
import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.calculation.progress
        .VerticalScrollProgressCalculator;

/**
 * Widget used to fast-scroll a vertical {@link RecyclerView}.
 * Currently assumes the use of a {@link LinearLayoutManager}
 */
public class VerticalRecyclerViewFastScroller extends AbsRecyclerViewFastScroller implements RecyclerViewScroller {

    @Nullable
    private VerticalScrollProgressCalculator mScrollProgressCalculator;
    @Nullable private VerticalScreenPositionCalculator mScreenPositionCalculator;

    public VerticalRecyclerViewFastScroller(Context context) {
        this(context, null);
    }

    public VerticalRecyclerViewFastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalRecyclerViewFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.vertical_recycler_fast_scroller_layout;
    }

    @Override
    @Nullable
    protected TouchableScrollProgressCalculator getScrollProgressCalculator() {
        return mScrollProgressCalculator;
    }

    @Override
    public void moveHandleToPosition(float scrollProgress) {
        if (mScreenPositionCalculator == null) {
            return;
        }
        mHandle.setY(mScreenPositionCalculator.getYPositionFromScrollProgress(scrollProgress));
    }

    protected void onCreateScrollProgressCalculator() {
        VerticalScrollBoundsProvider boundsProvider =
                new VerticalScrollBoundsProvider(mBar.getY(), mBar.getY() + mBar.getHeight() - mHandle.getHeight());
        mScrollProgressCalculator = new VerticalLinearLayoutManagerScrollProgressCalculator(boundsProvider);
        mScreenPositionCalculator = new VerticalScreenPositionCalculator(boundsProvider);
    }
}
