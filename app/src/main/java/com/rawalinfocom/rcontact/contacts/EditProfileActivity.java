package com.rawalinfocom.rcontact.contacts;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.text_label_name)
    TextView textLabelName;
    @BindView(R.id.input_first_name)
    EditText inputFirstName;
    @BindView(R.id.input_last_name)
    EditText inputLastName;
    @BindView(R.id.linear_section_left)
    LinearLayout linearSectionLeft;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.linear_section_top)
    LinearLayout linearSectionTop;
    @BindView(R.id.image_add_phone)
    ImageView imageAddPhone;
    @BindView(R.id.text_label_phone)
    TextView textLabelPhone;
    @BindView(R.id.relative_contact_details)
    RelativeLayout relativeContactDetails;
    @BindView(R.id.linear_phone_details)
    LinearLayout linearPhoneDetails;
    @BindView(R.id.image_add_email)
    ImageView imageAddEmail;
    @BindView(R.id.text_label_email)
    TextView textLabelEmail;
    @BindView(R.id.relative_email_details)
    RelativeLayout relativeEmailDetails;
    @BindView(R.id.linear_email_details)
    LinearLayout linearEmailDetails;
    @BindView(R.id.image_add_website)
    ImageView imageAddWebsite;
    @BindView(R.id.text_label_website)
    TextView textLabelWebsite;
    @BindView(R.id.relative_website_details)
    RelativeLayout relativeWebsiteDetails;
    @BindView(R.id.linear_website_details)
    LinearLayout linearWebsiteDetails;
    @BindView(R.id.image_add_address)
    ImageView imageAddAddress;
    @BindView(R.id.text_label_address)
    TextView textLabelAddress;
    @BindView(R.id.relative_address_details)
    RelativeLayout relativeAddressDetails;
    @BindView(R.id.recycler_view_address)
    RecyclerView recyclerViewAddress;
    @BindView(R.id.image_add_social_contact)
    ImageView imageAddSocialContact;
    @BindView(R.id.text_label_social_contact)
    TextView textLabelSocialContact;
    @BindView(R.id.relative_social_contact_details)
    RelativeLayout relativeSocialContactDetails;
    @BindView(R.id.linear_social_contact_details)
    LinearLayout linearSocialContactDetails;
    @BindView(R.id.image_add_event)
    ImageView imageAddEvent;
    @BindView(R.id.text_label_event)
    TextView textLabelEvent;
    @BindView(R.id.relative_event_details)
    RelativeLayout relativeEventDetails;
    @BindView(R.id.recycler_view_event)
    RecyclerView recyclerViewEvent;
    @BindView(R.id.text_label_gender)
    TextView textLabelGender;
    @BindView(R.id.radio_male)
    RadioButton radioMale;
    @BindView(R.id.radio_female)
    RadioButton radioFemale;
    @BindView(R.id.radio_group_gender)
    RadioGroup radioGroupGender;

    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;

    ArrayList<Object> arrayListPhoneNumberObject;
    ArrayList<Object> arrayListEmailObject;
    ArrayList<Object> arrayListWebsiteObject;
    ArrayList<Object> arrayListSocialContactObject;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        init();
    }

    //</editor-fold>

    //<editor-fold desc="Onclick">

    @OnClick({R.id.image_add_phone, R.id.image_add_email, R.id.image_add_website,
            R.id.image_add_social_contact})
    public void onClick(View view) {
        switch (view.getId()) {

            //<editor-fold desc="image_add_phone">
            case R.id.image_add_phone:
                addView(AppConstants.PHONE_NUMBER, linearPhoneDetails, null);
                break;
            //</editor-fold>

            //<editor-fold desc="image_add_email">
            case R.id.image_add_email:
                addView(AppConstants.EMAIL, linearEmailDetails, null);
                break;
            //</editor-fold>

            //<editor-fold desc="image_add_website">
            case R.id.image_add_website:
                addView(AppConstants.WEBSITE, linearWebsiteDetails, null);
                break;
            //</editor-fold>

            // <editor-fold desc="image_add_social_contact">
            case R.id.image_add_social_contact:
                addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails, null);
                break;
            //</editor-fold>
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.ripple_action_back:
                onBackPressed();
                break;

            case R.id.ripple_action_right_left:
                for (int i = 0; i < linearPhoneDetails.getChildCount(); i++) {
                    View view = linearPhoneDetails.getChildAt(i);
                    EditText number = (EditText) view.findViewById(R.id.input_value);
                    Log.i("onComplete", number.getText().toString());
                }
                break;


        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);

        phoneNumberDetails();
        emailDetails();
        websiteDetails();
        socialContactDetails();

        rippleActionRightLeft.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);

    }

    private void phoneNumberDetails() {
        arrayListPhoneNumberObject = new ArrayList<>();
        ProfileDataOperationPhoneNumber phoneNumber = new ProfileDataOperationPhoneNumber();
        phoneNumber.setPhoneNumber("+911234567890");
        phoneNumber.setPhoneType("Home");
        arrayListPhoneNumberObject.add(phoneNumber);

        for (int i = 0; i < arrayListPhoneNumberObject.size(); i++) {
            addView(AppConstants.PHONE_NUMBER, linearPhoneDetails, arrayListPhoneNumberObject.get
                    (i));
        }
    }

    private void emailDetails() {
        arrayListEmailObject = new ArrayList<>();
        ProfileDataOperationEmail email = new ProfileDataOperationEmail();
        email.setEmEmailId("mg@gmail.com");
        email.setEmType("Work");
        arrayListEmailObject.add(email);

        for (int i = 0; i < arrayListEmailObject.size(); i++) {
            addView(AppConstants.EMAIL, linearEmailDetails, arrayListEmailObject.get(i));
        }

    }

    private void websiteDetails() {
        arrayListWebsiteObject = new ArrayList<>();
        ProfileDataOperationWebAddress webAddress = new ProfileDataOperationWebAddress();
        webAddress.setWebAddress("www.hotmail.com");
        webAddress.setWebType("Work");
        arrayListWebsiteObject.add(webAddress);

        for (int i = 0; i < arrayListWebsiteObject.size(); i++) {
            addView(AppConstants.WEBSITE, linearWebsiteDetails, arrayListWebsiteObject.get(i));
        }

    }

    private void socialContactDetails() {
        arrayListSocialContactObject = new ArrayList<>();
        ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
        imAccount.setIMAccountDetails("hello.world");
        arrayListSocialContactObject.add(imAccount);

        for (int i = 0; i < arrayListSocialContactObject.size(); i++) {
            addView(AppConstants.IM_ACCOUNT, linearSocialContactDetails,
                    arrayListSocialContactObject.get(i));
        }

    }

    private void addView(int viewType, LinearLayout linearLayout, Object detailObject) {
        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile, null);
        TextView textImageCross = (TextView) view.findViewById(R.id.text_image_cross);
        Spinner spinnerType = (Spinner) view.findViewById(R.id.spinner_type);
        EditText inputValue = (EditText) view.findViewById(R.id.input_value);
        final RelativeLayout relativeRowEditProfile = (RelativeLayout) view.findViewById(R.id
                .relative_row_edit_profile);

        textImageCross.setTypeface(Utils.typefaceIcons(this));
        inputValue.setTypeface(Utils.typefaceRegular(this));

        String[] spinnerArrayId = new String[0];

        switch (viewType) {
            case AppConstants.PHONE_NUMBER:
                spinnerArrayId = getResources().getStringArray(R.array.types_phone_number);
                inputValue.setInputType(InputType.TYPE_CLASS_PHONE);
                if (detailObject != null) {
                    ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                            detailObject;
                    inputValue.setText(phoneNumber.getPhoneNumber());
                }
                break;

            case AppConstants.EMAIL:
                spinnerArrayId = getResources().getStringArray(R.array.types_email_address);
                inputValue.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                if (detailObject != null) {
                    ProfileDataOperationEmail email = (ProfileDataOperationEmail) detailObject;
                    inputValue.setText(email.getEmEmailId());
                }
                break;

            case AppConstants.WEBSITE:
                spinnerArrayId = getResources().getStringArray(R.array.types_email_address);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationWebAddress webAddress = (ProfileDataOperationWebAddress)
                            detailObject;
                    inputValue.setText(webAddress.getWebAddress());
                }
                break;

            case AppConstants.IM_ACCOUNT:
                spinnerArrayId = getResources().getStringArray(R.array.types_social_media);
                inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
                if (detailObject != null) {
                    ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount)
                            detailObject;
                    inputValue.setText(imAccount.getIMAccountDetails());
                }
                break;

            default:
                getResources().getStringArray(R.array.types_email_address);
                break;
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout
                .list_item_spinner, spinnerArrayId);
        spinnerType.setAdapter(spinnerAdapter);

        textImageCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeRowEditProfile.setVisibility(View.GONE);
            }
        });

        linearLayout.addView(view);
    }

    //</editor-fold>
}
