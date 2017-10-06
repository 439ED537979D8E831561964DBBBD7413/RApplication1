package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SocialMediaJoinUsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.imgFacebook)
    ImageView imgFacebook;
    @BindView(R.id.imgTwitter)
    ImageView imgTwitter;
    @BindView(R.id.imgLinkedin)
    ImageView imgLinkedin;
    @BindView(R.id.imgPintrest)
    ImageView imgPintrest;
    @BindView(R.id.imgYouTube)
    ImageView imgYouTube;
    @BindView(R.id.imgGooglePlus)
    ImageView imgGooglePlus;
    @BindView(R.id.activity_contact_settings)
    RelativeLayout activityContactSettings;
    private Activity activity;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);
        ButterKnife.bind(this);

        activity = SocialMediaJoinUsActivity.this;
        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    //</editor-fold>
    private void init() {

        rippleActionBack.setOnRippleCompleteListener(this);

        textToolbarTitle.setText(getResources().getString(R.string.str_join_us));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.WS_FACEBOOK_URL));
                startActivity(i);
            }
        });

        imgTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.WS_TWITTER_URL));
                startActivity(i);
            }
        });

        imgLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.WS_LINKEDIN_URL));
                startActivity(i);
            }
        });

        imgPintrest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.WS_PINTEREST_URL));
                startActivity(i);
            }
        });

        imgYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.WS_YOUTUBE_URL));
                startActivity(i);
            }
        });

        imgGooglePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.WS_GOOGLE_URL));
                startActivity(i);
            }
        });
    }
}
