package com.rawalinfocom.rcontact;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermsConditionsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.text_terms_conditions_header)
    TextView textTermsConditionsHeader;
    @BindView(R.id.text_terms_conditions_content)
    TextView textTermsConditionsContent;
    @BindView(R.id.checkbox_terms_conditions)
    CheckBox checkboxTermsConditions;
    @BindView(R.id.text_terms_conditions)
    TextView textTermsConditions;
    @BindView(R.id.button_get_started)
    Button buttonGetStarted;
    @BindView(R.id.ripple_get_started)
    RippleView rippleGetStarted;
    @BindView(R.id.linear_bottom)
    LinearLayout linearBottom;
    @BindView(R.id.image_terms_conditions)
    ImageView imageTermsConditions;
    @BindView(R.id.relative_root_terms_conditions)
    RelativeLayout relativeRootTermsConditions;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(TermsConditionsActivity.this, R.color
                    .iconColor));
        }
        setContentView(R.layout.activity_terms_conditions);
        ButterKnife.bind(this);

        init();

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            //<editor-fold desc="ripple_get_started">
            case R.id.ripple_get_started:
                if (checkboxTermsConditions.isChecked()) {
                    Utils.setIntegerPreference(TermsConditionsActivity.this, AppConstants
                            .PREF_LAUNCH_SCREEN_INT, IntegerConstants.LAUNCH_MOBILE_REGISTRATION);
                    startActivityIntent(TermsConditionsActivity.this,
                            MobileNumberRegistrationActivity.class, null);
                    finish();
                } else {
                    Utils.showErrorSnackBar(TermsConditionsActivity.this,
                            relativeRootTermsConditions, getString(R.string
                                    .accept_terms_conditions));
                }
                break;
            //</editor-fold>

        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        textTermsConditionsHeader.setText(R.string.str_welcomesch);
        textTermsConditionsContent.setText(R.string.str_welcom_message);

        textTermsConditionsHeader.setTypeface(Utils.typefaceSemiBold(TermsConditionsActivity.this));
        textTermsConditionsContent.setTypeface(Utils.typefaceRegular(TermsConditionsActivity.this));
        textTermsConditions.setTypeface(Utils.typefaceRegular(TermsConditionsActivity.this));
        buttonGetStarted.setTypeface(Utils.typefaceRegular(TermsConditionsActivity.this));

        Utils.setRoundedCornerBackground(buttonGetStarted, ContextCompat.getColor
                (TermsConditionsActivity.this, R.color.colorAccent), 5, 0, ContextCompat.getColor
                (TermsConditionsActivity.this, R.color.colorAccent));

        SpannableString ss = new SpannableString("By continuing you accept Terms of Use");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(BuildConfig.URL_TERMS_CONDITIONS));
                startActivity(i);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        ss.setSpan(clickableSpan, 25, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textTermsConditions.setText(ss);
        textTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        textTermsConditions.setHighlightColor(Color.TRANSPARENT);

        rippleGetStarted.setOnRippleCompleteListener(this);

    }

    //</editor-fold>
}
