package com.rawalinfocom.rcontact.account;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewAccountActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_lable_first_name)
    TextView textLableFirstName;
    @BindView(R.id.text_first_name)
    TextView textFirstName;
    @BindView(R.id.text_lable_last_name)
    TextView textLableLastName;
    @BindView(R.id.text_last_name)
    TextView textLastName;
    @BindView(R.id.text_lable_phone)
    TextView textLablePhone;
    @BindView(R.id.text_phone_number)
    TextView textPhoneNumber;
    @BindView(R.id.text_lable_email)
    TextView textLableEmail;
    @BindView(R.id.text_lable_joining_date)
    TextView textLableJoiningDate;
    @BindView(R.id.text_joining_date)
    TextView textJoiningDate;
    @BindView(R.id.rl_main_content)
    RelativeLayout rlMainContent;
    @BindView(R.id.activity_view_account)
    RelativeLayout activityViewAccount;

    String firstName;
    String lastName;
    String phoneNumber;
    ArrayList<String> emailIdList;
    String joiningDate;
    @BindView(R.id.text_phone_verified)
    TextView textPhoneVerified;
    @BindView(R.id.ll_email_vertical)
    LinearLayout llEmailVertical;
    TextView textEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
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
        textToolbarTitle.setText(getString(R.string.view_account));

        getPreference();
    }

    private void getPreference() {
        firstName = Utils.getStringPreference(this, AppConstants.PREF_USER_FIRST_NAME, "");
        lastName = Utils.getStringPreference(this, AppConstants.PREF_USER_LAST_NAME, "");
        phoneNumber = Utils.getStringPreference(this, AppConstants.PREF_USER_NUMBER, "");
        emailIdList = Utils.getArrayListPreference(this, AppConstants.PREF_USER_VERIFIED_EMAIL);
        if(emailIdList == null){
            emailIdList = new ArrayList<>();
        }
        joiningDate = Utils.getStringPreference(this, AppConstants.PREF_USER_JOINING_DATE, "");

        populateView();
    }

    private void populateView() {
        textFirstName.setTypeface(Utils.typefaceRegular(this));
        textLastName.setTypeface(Utils.typefaceRegular(this));
        textPhoneNumber.setTypeface(Utils.typefaceRegular(this));
        textJoiningDate.setTypeface(Utils.typefaceRegular(this));

        if (!StringUtils.isEmpty(firstName))
            textFirstName.setText(firstName);
        else {
            textFirstName.setVisibility(View.GONE);
            textLableFirstName.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(lastName))
            textLastName.setText(lastName);
        else {
            textLastName.setVisibility(View.GONE);
            textLableLastName.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(phoneNumber)) {
            if (phoneNumber.contains("91")) {
                String firstPath = phoneNumber.substring(0, 2);
                String lastPath = phoneNumber.substring(2, phoneNumber.length());
                String newString = "+" + firstPath + " " + lastPath;
                textPhoneNumber.setText(newString);
                textPhoneVerified.setText(this.getString(R.string
                        .im_icon_verify));
                textPhoneVerified.setTypeface(Utils.typefaceIcons(this));
            }
        } else {
            textPhoneNumber.setVisibility(View.GONE);
            textLablePhone.setVisibility(View.GONE);
            textPhoneVerified.setVisibility(View.GONE);
        }

        if (emailIdList.size() > 0) {
            llEmailVertical.removeAllViews();
            for (int i = 0; i < emailIdList.size(); i++) {
                String emailId = emailIdList.get(i);
                textEmail = new TextView(this);
                textEmail.setTypeface(Utils.typefaceIcons(this));
                textEmail.setText(emailId + " " + this.getString(R.string.im_icon_verify));
                textEmail.setTextColor(ContextCompat.getColor(this,R.color.colorAccent));
                textEmail.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.text_size_16sp));
                textEmail.setPadding(8,8,8,8);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15,
                        getResources().getDisplayMetrics());
                params.setMargins((int) pixels,0,0,0);
                textEmail.setLayoutParams(params);
                llEmailVertical.addView(textEmail);
            }
        } else {
            llEmailVertical.setVisibility(View.GONE);
            textEmail = new TextView(this);
            textEmail.setVisibility(View.GONE);
            textLableEmail.setVisibility(View.GONE);
        }

        if (!StringUtils.isEmpty(joiningDate)) {
            String dtStart = joiningDate;
            Date joinDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                joinDate = format.parse(dtStart);
                System.out.println(joinDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat format1 = new SimpleDateFormat("d");
            String date = format1.format(joinDate);

            if (date.endsWith("1") && !date.endsWith("11"))
                format1 = new SimpleDateFormat("d'st' MMMM, yyyy");
            else if (date.endsWith("2") && !date.endsWith("12"))
                format1 = new SimpleDateFormat("d'nd' MMMM, yyyy");
            else if (date.endsWith("3") && !date.endsWith("13"))
                format1 = new SimpleDateFormat("d'rd' MMMM, yyyy");
            else
                format1 = new SimpleDateFormat("d'th' MMMM, yyyy");

            String yourDate = format1.format(joinDate);
            textJoiningDate.setText(yourDate);

        } else {
            textJoiningDate.setVisibility(View.GONE);
            textLableJoiningDate.setVisibility(View.GONE);
        }

    }
}
