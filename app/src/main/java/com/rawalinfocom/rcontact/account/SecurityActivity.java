package com.rawalinfocom.rcontact.account;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecurityActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {


    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.imageViewSecurityLogo)
    ImageView imageViewSecurityLogo;
    @BindView(R.id.text_view_security_info)
    TextView textViewSecurityInfo;
    @BindView(R.id.text_view_security_link)
    TextView textViewSecurityLink;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        ButterKnife.bind(this);
        initView();
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
        textToolbarTitle.setText(getString(R.string.security));
        setFonts();
    }

    private void setFonts(){
        textViewSecurityInfo.setTypeface(Utils.typefaceRegular(SecurityActivity.this));
        textViewSecurityLink.setTypeface(Utils.typefaceRegular(SecurityActivity.this));
        clickEvent();
    }

    private void clickEvent(){

        textViewSecurityLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getResources().getString(R.string.security_link);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }
}
