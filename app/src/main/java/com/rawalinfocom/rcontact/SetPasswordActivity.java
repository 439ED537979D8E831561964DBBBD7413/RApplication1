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

public class SetPasswordActivity extends BaseActivity implements RippleView
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
    @BindView(R.id.text_configure_password)
    TextView textConfigurePassword;
    @BindView(R.id.text_msg_set_password)
    TextView textMsgSetPassword;
    @BindView(R.id.input_set_password)
    EditText inputSetPassword;
    @BindView(R.id.input_set_confirm_password)
    EditText inputSetConfirmPassword;
    @BindView(R.id.linear_layout_edit_box)
    LinearLayout linearLayoutEditBox;
    @BindView(R.id.button_submit)
    Button buttonSubmit;
    @BindView(R.id.ripple_register)
    RippleView rippleRegister;
    @BindView(R.id.layout_root)
    RelativeLayout layoutRoot;
    @BindView(R.id.text_tip)
    TextView textTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        rippleActionBack.setOnRippleCompleteListener(this);
        textToolbarTitle.setText(getResources().getString(R.string.set_password));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));
        textConfigurePassword.setTypeface(Utils.typefaceRegular(this));
        textMsgSetPassword.setTypeface(Utils.typefaceRegular(this));
        inputSetPassword.setTypeface(Utils.typefaceRegular(this));
        inputSetConfirmPassword.setTypeface(Utils.typefaceRegular(this));
        buttonSubmit.setTypeface(Utils.typefaceRegular(this));
        textTip.setTypeface(Utils.typefaceRegular(this));
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

    }

    @Override
    public void onComplete(RippleView rippleView) {

    }
}
