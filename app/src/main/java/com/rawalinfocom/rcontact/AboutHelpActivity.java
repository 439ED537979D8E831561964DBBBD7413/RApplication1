package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.finestwebview.FinestWebView;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.Contact;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AboutHelpActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.txt_about)
    TextView txtAbout;
    @BindView(R.id.ll_about)
    LinearLayout llAbout;
    @BindView(R.id.txt_faq)
    TextView txtFaq;
    @BindView(R.id.ll_faq)
    LinearLayout llFaq;
    @BindView(R.id.txt_terms)
    TextView txtTerms;
    @BindView(R.id.ll_terms)
    LinearLayout llTerms;
    @BindView(R.id.txt_contact_us)
    TextView txtContactUs;
    @BindView(R.id.ll_contact_us)
    LinearLayout llContactUs;
    @BindView(R.id.txt_join_us)
    TextView txtJoinUs;
    @BindView(R.id.ll_join_us)
    LinearLayout llJoinUs;
    @BindView(R.id.activity_contact_settings)
    RelativeLayout activityContactSettings;
    private Activity activity;
    private static String version;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_help);
        ButterKnife.bind(this);

        activity = AboutHelpActivity.this;
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

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
    }

    //</editor-fold>
    private void init() {

        rippleActionBack.setOnRippleCompleteListener(this);

        textToolbarTitle.setText(getResources().getString(R.string.nav_text_about_help));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        txtAbout.setTypeface(Utils.typefaceRegular(this));
        txtFaq.setTypeface(Utils.typefaceRegular(this));
        txtTerms.setTypeface(Utils.typefaceRegular(this));
        txtContactUs.setTypeface(Utils.typefaceRegular(this));
        txtJoinUs.setTypeface(Utils.typefaceRegular(this));

        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        llTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView(getString(R.string.str_terms), WsConstants.URL_TERMS_CONDITIONS);
            }
        });

        llFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWebView(getString(R.string.str_faq), WsConstants.URL_FAQ);
            }
        });

        txtJoinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIntent(activity, SocialMediaJoinUsActivity.class, null);
            }
        });

        txtContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ContactUsActivity.class));
                //showWebView(getString(R.string.str_contact_us), WsConstants.URL_CONTACT_US);
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new MyDialogFragment();
        newFragment.show(ft, "dialog");
    }

    public static class MyDialogFragment extends DialogFragment {

        @BindView(R.id.txt_version_number)
        TextView txtVersionNumber;
        @BindView(R.id.image_splash)
        ImageView imageSplash;
        @BindView(R.id.txt_copy_right)
        TextView txtCopyRight;
        @BindView(R.id.txt_terms)
        TextView txtTerms;
        Unbinder unbinder;
        @BindView(R.id.txt_app_name)
        TextView txtAppName;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, R.style.MyCustomTheme);
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                d.getWindow().setLayout(width, height);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_about, container, false);
            unbinder = ButterKnife.bind(this, v);

            txtAppName.setTypeface(Utils.typefaceBold(getActivity()));
            txtVersionNumber.setTypeface(Utils.typefaceRegular(getActivity()));
            txtCopyRight.setTypeface(Utils.typefaceRegular(getActivity()));
            txtTerms.setTypeface(Utils.typefaceRegular(getActivity()));

            txtVersionNumber.setText(String.format("Version %s", version));
            txtCopyRight.setText("Copyright \u00A9 " + Calendar.getInstance().get(Calendar.YEAR) +
                    " RContacts App inc.\nAll rights reserved");

            txtTerms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWebView(getString(R.string.str_terms), WsConstants.URL_TERMS_CONDITIONS);
                }
            });

            return v;
        }

        private void showWebView(String title, String url) {

            new FinestWebView.Builder(getActivity()).theme(R.style.FinestWebViewTheme)
                    .titleDefault(title)
                    .showUrl(false)
                    .statusBarColorRes(R.color.colorPrimaryDark)
                    .toolbarColorRes(R.color.colorPrimary)
                    .titleColorRes(R.color.finestWhite)
                    .urlColorRes(R.color.colorPrimary)
                    .iconDefaultColorRes(R.color.finestWhite)
                    .progressBarColorRes(R.color.finestWhite)
                    .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                    .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                    .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                    .showSwipeRefreshLayout(true)
                    .swipeRefreshColorRes(R.color.colorPrimaryDark)
                    .menuSelector(R.drawable.selector_light_theme)
                    .menuTextGravity(Gravity.CENTER)
                    .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                    .dividerHeight(0)
                    .gradientDivider(false)
                    .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                    .show(url);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }
    }

    private void showWebView(String title, String url) {

        new FinestWebView.Builder(this).theme(R.style.FinestWebViewTheme)
                .titleDefault(title).showUrl(false)
                .statusBarColorRes(R.color.colorPrimaryDark)
                .toolbarColorRes(R.color.colorPrimary)
                .titleColorRes(R.color.finestWhite)
                .urlColorRes(R.color.colorPrimary)
                .iconDefaultColorRes(R.color.finestWhite)
                .progressBarColorRes(R.color.finestWhite)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .showSwipeRefreshLayout(true)
                .swipeRefreshColorRes(R.color.colorPrimaryDark)
                .menuSelector(R.drawable.selector_light_theme)
                .menuTextGravity(Gravity.CENTER)
                .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                .dividerHeight(0)
                .gradientDivider(false)
                .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
                .show(url);
    }
}
