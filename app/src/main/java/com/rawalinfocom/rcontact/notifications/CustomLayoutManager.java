package com.rawalinfocom.rcontact.notifications;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by maulik on 11/03/17.
 */

public class CustomLayoutManager extends LinearLayoutManager {
    private RecyclerView recyclerView;
    private int height;

    public CustomLayoutManager(Context context, RecyclerView recyclerView, int height) {
        super(context);
        this.recyclerView = recyclerView;
        this.height = height;
    }


    @Override
    public void onLayoutCompleted(RecyclerView.State state) {
        int x = recyclerView.computeVerticalScrollRange();
        if (x > height) {
            recyclerView.getLayoutParams().height = height;
        } else {
            recyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        super.onLayoutCompleted(state);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }
}
