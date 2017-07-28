package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    }

    void showDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = MyDialogFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    public static class MyDialogFragment extends DialogFragment {

        static MyDialogFragment newInstance() {
            MyDialogFragment f = new MyDialogFragment();
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.activity_splash, container, false);
            return v;
        }
    }
}
