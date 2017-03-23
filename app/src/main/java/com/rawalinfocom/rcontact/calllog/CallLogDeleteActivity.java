package com.rawalinfocom.rcontact.calllog;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.DeleteCallLogListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.CallLogType;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallLogDeleteActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {


    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionToolbarDelete;
    //    RippleView rippleActionToolbarSelectAll;
    ImageView imageDelete;
    //    ImageView imageSelectAll;
    @BindView(R.id.recycle_view_delete_call_log)
    RecyclerView recycleViewDeleteCallLog;

    ArrayList<CallLogType> arrayListCallLogType;
    DeleteCallLogListAdapter deleteCallLogListAdapter;
    @BindView(R.id.checkbox_select_all)
    CheckBox checkboxSelectAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_delete);
        ButterKnife.bind(this);
        receiveBundleData();
        init();

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_delete:
                break;

            /*case R.id.ripple_action_select_all:

                break;*/
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isTaskRoot()) {
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
        }
    }

    private void receiveBundleData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            arrayListCallLogType = (ArrayList<CallLogType>) bundle.getSerializable(AppConstants.EXTRA_CALL_ARRAY_LIST);
        }
    }


    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageDelete = ButterKnife.findById(includeToolbar, R.id.image_delete);
//        imageSelectAll = ButterKnife.findById(includeToolbar, R.id.image_select_all);
        rippleActionToolbarDelete = ButterKnife.findById(includeToolbar, R.id.ripple_action_delete);
//        rippleActionToolbarSelectAll = ButterKnife.findById(includeToolbar, R.id
//                .ripple_action_select_all);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText("Delete");
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionToolbarDelete.setOnRippleCompleteListener(this);
//        rippleActionToolbarSelectAll.setOnRippleCompleteListener(this);
        deleteCallLogListAdapter = new DeleteCallLogListAdapter(this, arrayListCallLogType);

        checkboxSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deleteCallLogListAdapter.isSelectAll(isChecked);
                if (!isChecked) {
                    deleteCallLogListAdapter.getArrayListCheckedPositions().clear();
                }

            }
        });

        setAdapter();

    }

    private void setAdapter() {
        if (arrayListCallLogType != null && arrayListCallLogType.size() > 0) {
            deleteCallLogListAdapter = new
                    DeleteCallLogListAdapter(this, arrayListCallLogType);
            recycleViewDeleteCallLog.setAdapter(deleteCallLogListAdapter);
            setRecyclerViewLayoutManager(recycleViewDeleteCallLog);
        }
    }

    /**
     * Set RecyclerView's LayoutManager
     */
    private void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

}
