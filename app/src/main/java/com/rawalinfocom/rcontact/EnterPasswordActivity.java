package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EnterPasswordActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.image_set_password_logo)
    ImageView imageSetPasswordLogo;
    @BindView(R.id.text_password_protected)
    TextView textPasswordProtected;
    @BindView(R.id.text_msg_enter_password)
    TextView textMsgEnterPassword;
    @BindView(R.id.input_enter_password)
    EditText inputEnterPassword;
    @BindView(R.id.linear_layout_edit_box)
    LinearLayout linearLayoutEditBox;
    @BindView(R.id.button_login)
    Button buttonLogin;
    @BindView(R.id.ripple_login)
    RippleView rippleLogin;
    @BindView(R.id.button_forgot_password)
    Button buttonForgotPassword;
    @BindView(R.id.ripple_forget_password)
    RippleView rippleForgetPassword;
    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText(getResources().getString(R.string.str_enter_password));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        textPasswordProtected.setTypeface(Utils.typefaceRegular(this));
        textMsgEnterPassword.setTypeface(Utils.typefaceRegular(this));
        inputEnterPassword.setTypeface(Utils.typefaceRegular(this));
        buttonLogin.setTypeface(Utils.typefaceRegular(this));
        buttonForgotPassword.setTypeface(Utils.typefaceRegular(this));
    }


    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }

    }
}
