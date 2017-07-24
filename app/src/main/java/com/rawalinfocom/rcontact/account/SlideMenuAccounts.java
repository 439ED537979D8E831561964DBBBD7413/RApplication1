package com.rawalinfocom.rcontact.account;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SlideMenuAccounts extends BaseActivity implements RippleView
        .OnRippleCompleteListener {


    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_view_account)
    TextView textViewAccount;
    @BindView(R.id.ll_view_account)
    LinearLayout llViewAccount;
    @BindView(R.id.text_security)
    TextView textSecurity;
    @BindView(R.id.ll_security)
    LinearLayout llSecurity;
    @BindView(R.id.activity_settings)
    RelativeLayout activitySettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_menu_accounts);
        ButterKnife.bind(this);
        initView();
        makeDataAndPopulate();
        clickEvents();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    private void initView() {
        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        textToolbarTitle.setText(getString(R.string.action_bar_title));
    }

    private void makeDataAndPopulate() {
        textViewAccount.setTypeface(Utils.typefaceSemiBold(this));
        textSecurity.setTypeface(Utils.typefaceSemiBold(this));
        textViewAccount.setText(getResources().getString(R.string.view_account));
        textSecurity.setText(getResources().getString(R.string.security));
    }

    private void clickEvents(){
        llViewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntent(SlideMenuAccounts.this,ViewAccountActivity.class,null);
            }
        });

        llSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntent(SlideMenuAccounts.this,SecurityActivity.class,null);
            }
        });
    }

}
