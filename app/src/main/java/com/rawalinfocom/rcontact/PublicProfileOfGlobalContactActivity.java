package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublicProfileOfGlobalContactActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.webview_public_profile)
    WebView webviewPublicProfile;
    @BindView(R.id.relative_root_public_profile)
    RelativeLayout relativeRootPublicProfile;
    String publicProfileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile_of_global_contact);
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
        textToolbarTitle.setText(getResources().getString(R.string.public_profile_toolbar_title));
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(AppConstants.EXTRA_GLOBAL_PUBLIC_PROFILE_URL)) {
                publicProfileUrl = intent.getStringExtra(AppConstants.EXTRA_GLOBAL_PUBLIC_PROFILE_URL);
            }
        }

        populateWebView();

    }

    private void populateWebView() {
        webviewPublicProfile.getSettings().setJavaScriptEnabled(true); // enable javascript
        webviewPublicProfile.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Utils.showErrorSnackBar(PublicProfileOfGlobalContactActivity.this, relativeRootPublicProfile,
                        description);
            }

            ProgressDialog progressDialog;

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!StringUtils.isEmpty(url)) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(PublicProfileOfGlobalContactActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
//                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        //Load url in webview
        if (!StringUtils.isEmpty(publicProfileUrl))
            webviewPublicProfile.loadUrl(publicProfileUrl);

    }
}
