package com.rawalinfocom.rcontact.contacts;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileDetailActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    RippleView rippleActionRightRight;

    @BindView(R.id.text_joining_date)
    TextView textJoiningDate;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_user_rating)
    TextView textUserRating;
    @BindView(R.id.linear_basic_detail_rating)
    LinearLayout linearBasicDetailRating;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_designation)
    TextView textDesignation;
    @BindView(R.id.text_organization)
    TextView textOrganization;
    @BindView(R.id.text_view_all_organization)
    TextView textViewAllOrganization;
    @BindView(R.id.linear_basic_detail)
    LinearLayout linearBasicDetail;
    @BindView(R.id.relative_basic_detail)
    RelativeLayout relativeBasicDetail;
    @BindView(R.id.image_call)
    ImageView imageCall;
    @BindView(R.id.text_label_phone)
    TextView textLabelPhone;
    @BindView(R.id.recycler_view_contact_number)
    RecyclerView recyclerViewContactNumber;
    @BindView(R.id.linear_phone)
    LinearLayout linearPhone;
    @BindView(R.id.image_email)
    ImageView imageEmail;
    @BindView(R.id.text_label_email)
    TextView textLabelEmail;
    @BindView(R.id.recycler_view_email)
    RecyclerView recyclerViewEmail;
    @BindView(R.id.linear_email)
    LinearLayout linearEmail;
    @BindView(R.id.image_website)
    ImageView imageWebsite;
    @BindView(R.id.text_label_website)
    TextView textLabelWebsite;
    @BindView(R.id.recycler_view_website)
    RecyclerView recyclerViewWebsite;
    @BindView(R.id.linear_website)
    LinearLayout linearWebsite;
    @BindView(R.id.image_address)
    ImageView imageAddress;
    @BindView(R.id.text_label_address)
    TextView textLabelAddress;
    @BindView(R.id.recycler_view_address)
    RecyclerView recyclerViewAddress;
    @BindView(R.id.linear_address)
    LinearLayout linearAddress;
    @BindView(R.id.image_social_contact)
    ImageView imageSocialContact;
    @BindView(R.id.text_label_social_contact)
    TextView textLabelSocialContact;
    @BindView(R.id.recycler_view_social_contact)
    RecyclerView recyclerViewSocialContact;
    @BindView(R.id.linear_social_contact)
    LinearLayout linearSocialContact;
    @BindView(R.id.card_contact_details)
    CardView cardContactDetails;
    @BindView(R.id.image_event)
    ImageView imageEvent;
    @BindView(R.id.text_label_event)
    TextView textLabelEvent;
    @BindView(R.id.recycler_view_event)
    RecyclerView recyclerViewEvent;
    @BindView(R.id.linear_event)
    LinearLayout linearEvent;
    @BindView(R.id.image_gender)
    ImageView imageGender;
    @BindView(R.id.text_label_gender)
    TextView textLabelGender;
    @BindView(R.id.image_icon_gender)
    ImageView imageIconGender;
    @BindView(R.id.text_gender)
    TextView textGender;
    @BindView(R.id.linear_gender)
    LinearLayout linearGender;
    @BindView(R.id.card_other_details)
    CardView cardOtherDetails;
    @BindView(R.id.button_view_more)
    Button buttonViewMore;
    @BindView(R.id.ripple_view_more)
    RippleView rippleViewMore;
    @BindView(R.id.relative_section_view_more)
    RelativeLayout relativeSectionViewMore;
    @BindView(R.id.relative_root_profile_detail)
    RelativeLayout relativeRootProfileDetail;
    @BindView(R.id.linear_organization_detail)
    LinearLayout linearOrganizationDetail;
    @BindView(R.id.rating_user)
    RatingBar ratingUser;

    ProfileDataOperation profileDetail;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.ripple_view_more:
                if (relativeSectionViewMore.getVisibility() == View.VISIBLE) {
                    relativeSectionViewMore.setVisibility(View.GONE);
                    buttonViewMore.setText("View More");
                } else {
                    relativeSectionViewMore.setVisibility(View.VISIBLE);
                    buttonViewMore.setText("View Less");
                }
                break;

            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_GET_PROFILE_DETAIL">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DETAIL)) {
                WsResponseObject profileDetailResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (profileDetailResponse != null && StringUtils.equalsIgnoreCase
                        (profileDetailResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    profileDetail = profileDetailResponse.getProfileDetail();
                    setUpView();

                } else {
                    if (profileDetailResponse != null) {
                        Log.e("error response", profileDetailResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
//            AppUtils.hideProgressDialog();
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, "" + error
                    .getLocalizedMessage());
        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void init() {

        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        textToolbarTitle.setText(getString(R.string.title_my_profile));

        recyclerViewContactNumber.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialContact.setLayoutManager(new LinearLayoutManager(this));

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textJoiningDate.setTypeface(Utils.typefaceRegular(this));
        textName.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));

        rippleViewMore.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);

        textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllOrganizations();
            }
        });

        getProfileDetail();
    }

    private void setUpView() {

        //<editor-fold desc="Joining Date">
        String joiningDate = StringUtils.defaultString(Utils.convertDateFormat(profileDetail
                .getJoiningDate(), "yyyy-MM-dd HH:mm:ss", "dd'th' MMM, yyyy"), "-");
        textJoiningDate.setText("Joining Date:- " + joiningDate);
        textName.setText(profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
        //</editor-fold>

        //<editor-fold desc="Organization Detail">
        if (!Utils.isArraylistNullOrEmpty(profileDetail.getPbOrganization())) {
            linearOrganizationDetail.setVisibility(View.VISIBLE);
            if (profileDetail.getPbOrganization().size() == 1) {
                textViewAllOrganization.setVisibility(View.GONE);
            } else {
                textViewAllOrganization.setVisibility(View.VISIBLE);
            }
            textDesignation.setText(profileDetail.getPbOrganization().get(0).getOrgJobTitle());
            textOrganization.setText(profileDetail.getPbOrganization().get(0).getOrgName());
        } else {
            linearOrganizationDetail.setVisibility(View.INVISIBLE);
        }
        //</editor-fold>

        //<editor-fold desc="User Rating">
        textUserRating.setText(profileDetail.getTotalProfileRateUser());
        ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
        //</editor-fold>

        //<editor-fold desc="Phone Number">
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = profileDetail
                .getPbPhoneNumber();
        if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber)) {
            ArrayList<Object> tempPhoneNumber = new ArrayList<>();
            tempPhoneNumber.addAll(arrayListPhoneNumber);
            linearPhone.setVisibility(View.VISIBLE);
            ProfileDetailAdapter phoneDetailAdapter = new ProfileDetailAdapter(this,
                    tempPhoneNumber, AppConstants.PHONE_NUMBER);
            recyclerViewContactNumber.setAdapter(phoneDetailAdapter);
        } else {
            linearPhone.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Email Id">
        ArrayList<ProfileDataOperationEmail> arrayListEmail = profileDetail.getPbEmailId();
        if (!Utils.isArraylistNullOrEmpty(arrayListEmail)) {
            ArrayList<Object> tempEmail = new ArrayList<>();
            tempEmail.addAll(arrayListEmail);
            linearEmail.setVisibility(View.VISIBLE);
            ProfileDetailAdapter emailDetailAdapter = new ProfileDetailAdapter(this, tempEmail,
                    AppConstants.EMAIL);
            recyclerViewEmail.setAdapter(emailDetailAdapter);
        } else {
            linearEmail.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Website">
        ArrayList<String> arrayListWebsite = profileDetail.getPbWebAddress();
        if (!Utils.isArraylistNullOrEmpty(arrayListWebsite)) {
            ArrayList<Object> tempWebsite = new ArrayList<>();
            tempWebsite.addAll(arrayListWebsite);
            linearWebsite.setVisibility(View.VISIBLE);
            ProfileDetailAdapter websiteDetailAdapter = new ProfileDetailAdapter(this,
                    tempWebsite, AppConstants.WEBSITE);
            recyclerViewWebsite.setAdapter(websiteDetailAdapter);
        } else {
            linearWebsite.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Address">
        ArrayList<ProfileDataOperationAddress> arrayListAddress = profileDetail.getPbAddress();
        if (!Utils.isArraylistNullOrEmpty(arrayListAddress)) {
            ArrayList<Object> tempAddress = new ArrayList<>();
            tempAddress.addAll(arrayListAddress);
            linearAddress.setVisibility(View.VISIBLE);
            ProfileDetailAdapter addressDetailAdapter = new ProfileDetailAdapter(this,
                    tempAddress, AppConstants.ADDRESS);
            recyclerViewAddress.setAdapter(addressDetailAdapter);
        } else {
            linearAddress.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Im Account">
        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = profileDetail
                .getPbIMAccounts();
        if (!Utils.isArraylistNullOrEmpty(arrayListImAccount)) {
            ArrayList<Object> tempImAccount = new ArrayList<>();
            tempImAccount.addAll(arrayListImAccount);
            linearSocialContact.setVisibility(View.VISIBLE);
            ProfileDetailAdapter imAccountDetailAdapter = new ProfileDetailAdapter(this,
                    tempImAccount, AppConstants.IM_ACCOUNT);
            recyclerViewSocialContact.setAdapter(imAccountDetailAdapter);
        } else {
            linearSocialContact.setVisibility(View.GONE);
        }
        //</editor-fold>

        if (Utils.isArraylistNullOrEmpty(arrayListWebsite) && Utils.isArraylistNullOrEmpty
                (arrayListAddress) && Utils.isArraylistNullOrEmpty(arrayListImAccount)) {
            rippleViewMore.setVisibility(View.GONE);
        } else {
            rippleViewMore.setVisibility(View.VISIBLE);
        }

        // <editor-fold desc="Event">
        ArrayList<ProfileDataOperationEvent> arrayListEvent = profileDetail.getPbEvent();
        if (!Utils.isArraylistNullOrEmpty(arrayListEvent)) {
            ArrayList<Object> tempEvent = new ArrayList<>();
            tempEvent.addAll(arrayListEvent);
            linearEvent.setVisibility(View.VISIBLE);
            ProfileDetailAdapter eventDetailAdapter = new ProfileDetailAdapter(this, tempEvent,
                    AppConstants.EVENT);
            recyclerViewEvent.setAdapter(eventDetailAdapter);
        } else {
            linearEvent.setVisibility(View.GONE);
        }
        //</editor-fold>

//        linearGender.setVisibility(View.GONE);

        if (Utils.isArraylistNullOrEmpty(arrayListEvent)
//                && Utils.isArraylistNullOrEmpty(arrayListAddress)
                ) {
            cardOtherDetails.setVisibility(View.GONE);
        } else {
            cardOtherDetails.setVisibility(View.VISIBLE);
        }


    }

    private void showAllOrganizations() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.action_close);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        OrganizationListAdapter adapter = new OrganizationListAdapter(this, profileDetail
                .getPbOrganization());
        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    public RelativeLayout getRelativeRootProfileDetail() {
        return relativeRootProfileDetail;
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void getProfileDetail() {

        WsRequestObject profileDetailObject = new WsRequestObject();
        // TODO: 22/12/16 pmId
        profileDetailObject.setPmId("4");
//        profileDetailObject.setPmId(getUserPmId());

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileDetailObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_PROFILE_DETAIL, getString(R.string.msg_please_wait), true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_GET_PROFILE_DETAIL);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    //</editor-fold>
}
