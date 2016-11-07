package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.helper.RippleView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtpVerificationActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {


    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.toolbar_otp_verification)
    Toolbar toolbarOtpVerification;
    @BindView(R.id.text_verify_number)
    TextView textVerifyNumber;
    @BindView(R.id.text_enter_otp)
    TextView textEnterOtp;
    @BindView(R.id.input_otp)
    EditText inputOtp;
    @BindView(R.id.button_submit)
    Button buttonSubmit;
    @BindView(R.id.ripple_submit)
    RippleView rippleSubmit;
    @BindView(R.id.button_resend)
    Button buttonResend;
    @BindView(R.id.ripple_resend)
    RippleView rippleResend;
    @BindView(R.id.relative_root_otp_verification)
    RelativeLayout relativeRootOtpVerification;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

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


    //<editor-fold desc="Private Methods">

    private void init() {
        rippleActionBack.setOnRippleCompleteListener(this);
    }

    //</editor-fold>
}
