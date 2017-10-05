package com.rawalinfocom.rcontact.relation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.EditProfileActivity;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 03/10/17.
 */

public class AddNewRelationActivity extends BaseActivity implements WsResponseListener, View.OnClickListener {

    @BindView(R.id.relative_root_new_relation)
    LinearLayout relativeRootNewRelation;
    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_add_new)
    ImageView imageAddNew;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.input_value_add_name)
    EditText inputValueAddName;
    @BindView(R.id.img_clear)
    ImageView imgClear;
    @BindView(R.id.input_value_business)
    EditText inputValueBusiness;
    @BindView(R.id.input_value_family)
    EditText inputValueFamily;
    @BindView(R.id.checkbox_friend)
    CheckBox checkboxFriend;
    @BindView(R.id.button_name_done)
    Button buttonNameDone;
    @BindView(R.id.button_name_cancel)
    Button buttonNameCancel;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_title_business)
    TextView txtTitleBusiness;
    @BindView(R.id.txt_title_family)
    TextView txtTitleFamily;
    @BindView(R.id.txt_title_friend)
    TextView txtTitleFriend;
    @BindView(R.id.linear_business_relation)
    LinearLayout linearBusinessRelation;
    @BindView(R.id.img_business_clear)
    ImageView imgBusinessClear;
    @BindView(R.id.img_family_clear)
    ImageView imgFamilyClear;

    private Activity activity;

    public static int orgPosition = 0;
    public static int businessRelationPosition = 0;
    public static int familyRelationPosition = 0;

    private Dialog businessRelationDialog;
    private String pmId = "", contactName = "", profileImage = "", organizationId = "", organizationName = "",
            businessRelationName = "", strGender = "", familyRelation = "";
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;
    private int colorPineGreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_relation);

        ButterKnife.bind(this);
        initToolbar();
        init();
    }

    private void initToolbar() {
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText(getResources().getString(R.string.add_relation_toolbar_title));
        imageActionBack = ButterKnife.findById(includeToolbar, R.id.image_action_back);
        imageActionBack.setOnClickListener(this);
        imageAddNew = ButterKnife.findById(includeToolbar, R.id.image_add_new);
        imageAddNew.setVisibility(View.GONE);
    }

    private void init() {

        activity = AddNewRelationActivity.this;
        arrayListOrganization = new ArrayList<>();

        colorPineGreen = ContextCompat.getColor(activity, R.color.colorAccent);

        inputValueAddName.setTypeface(Utils.typefaceRegular(this));
        inputValueBusiness.setTypeface(Utils.typefaceRegular(this));
        inputValueFamily.setTypeface(Utils.typefaceRegular(this));

        inputValueAddName.setOnClickListener(this);
        inputValueBusiness.setOnClickListener(this);
        inputValueFamily.setOnClickListener(this);

        txtTitle.setTypeface(Utils.typefaceRegular(this));
        txtTitleBusiness.setTypeface(Utils.typefaceRegular(this));
        txtTitleFamily.setTypeface(Utils.typefaceRegular(this));
        txtTitleFriend.setTypeface(Utils.typefaceRegular(this));

        buttonNameDone.setTypeface(Utils.typefaceRegular(this));
        buttonNameDone.setOnClickListener(this);
        buttonNameCancel.setTypeface(Utils.typefaceRegular(this));
        buttonNameCancel.setOnClickListener(this);

        inputValueAddName.setFocusable(false);
        inputValueBusiness.setFocusable(false);
        inputValueFamily.setFocusable(false);

        Glide.with(activity)
                .load(R.drawable.home_screen_profile)
                .bitmapTransform(new CropCircleTransformation(activity))
                .into(imageProfile);

        imgClear.setOnClickListener(this);
        imgBusinessClear.setOnClickListener(this);
        imgFamilyClear.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getGender();
        getOrganizationsList();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.input_value_family:

                if (StringUtils.isEmpty(strGender)) {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                } else {
                    if (StringUtils.isEmpty(contactName)) {
                        Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                                "Please select User to establish relation!!");
                    } else {
                        dialogFamilyRelation();
                    }
                }
                break;

            case R.id.input_value_add_name:
                Intent intent = new Intent(activity, RContactsListActivity.class);
                startActivityForResult(intent, 101);// Activity is started with requestCode
                break;
            case R.id.input_value_business:

                if (arrayListOrganization.size() > 0) {

                    if (StringUtils.isEmpty(contactName)) {
                        Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                                "Please select User to establish relation!!");
                    } else {
                        dialogBusinessRelation();
                    }
                } else {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                }
                break;

            case R.id.image_action_back:
                finish();
                break;
            case R.id.button_name_done:
                Utils.showSuccessSnackBar(activity, relativeRootNewRelation, "Add New Relation Done");
                break;
            case R.id.button_name_cancel:
                finish();
                break;

            case R.id.img_clear:

                pmId = "";
                contactName = "";
                profileImage = "";
                businessRelationName = "";
                organizationId = "";
                organizationName = "";
                familyRelation = "";

                orgPosition = 0;
                businessRelationPosition = 0;

                imgClear.setVisibility(View.GONE);
                imgBusinessClear.setVisibility(View.GONE);
                imgFamilyClear.setVisibility(View.GONE);
                inputValueAddName.setText(contactName);
                inputValueBusiness.setText(organizationName);
                inputValueFamily.setText(familyRelation);

                Glide.with(activity)
                        .load(R.drawable.home_screen_profile)
                        .bitmapTransform(new CropCircleTransformation(activity))
                        .into(imageProfile);

                break;

            case R.id.img_business_clear:

                orgPosition = 0;
                businessRelationPosition = 0;

                organizationId = "";
                organizationName = "";
                businessRelationName = "";

                imgBusinessClear.setVisibility(View.GONE);
                inputValueBusiness.setText(organizationName);

                break;

            case R.id.img_family_clear:

                familyRelation = "";
                familyRelationPosition = 0;

                imgFamilyClear.setVisibility(View.GONE);
                inputValueFamily.setText(familyRelation);

                break;
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 101) {

            if (data != null) {
                if (data.getStringExtra("isBack").equalsIgnoreCase("0")) {
                    //If everything went Ok, change to another activity.
                    pmId = data.getStringExtra("pmId");
                    contactName = data.getStringExtra("contactName");
                    profileImage = data.getStringExtra("profileImage");

                    inputValueAddName.setText(contactName);

                    Glide.with(activity)
                            .load(profileImage)
                            .placeholder(R.drawable.home_screen_profile)
                            .error(R.drawable.home_screen_profile)
                            .bitmapTransform(new CropCircleTransformation(activity))
                            .override(512, 512)
                            .into(imageProfile);

                    imgClear.setVisibility(View.VISIBLE);

                } else {
                    Utils.showErrorSnackBar(this, relativeRootNewRelation, "You didn't select any RContact!");
                }
            } else {
                Utils.showErrorSnackBar(this, relativeRootNewRelation, "You didn't select any RContact!");
            }
        }
    }

//    private void socialContactDetails() {
//
//        socialTypeList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R
//                .array.types_social_media)));
//
//        TableImMaster tableImMaster = new TableImMaster(databaseHandler);
//
//        ArrayList<ImAccount> arrayListImAccount = tableImMaster.getImAccountFromPmId(Integer
//                .parseInt(getUserPmId()));
//        arrayListSocialContactObject = new ArrayList<>();
//        for (int i = 0; i < arrayListImAccount.size(); i++) {
//            ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();
//            imAccount.setIMAccountProtocol(arrayListImAccount.get(i).getImImProtocol());
//            imAccount.setIMAccountDetails(arrayListImAccount.get(i).getImImDetail());
//            imAccount.setIMAccountFirstName(arrayListImAccount.get(i).getImImFirstName());
//            imAccount.setIMAccountLastName(arrayListImAccount.get(i).getImImLastName());
//            imAccount.setIMAccountProfileImage(arrayListImAccount.get(i).getImImProfileImage());
//            imAccount.setIMId(arrayListImAccount.get(i).getImRecordIndexId());
//            imAccount.setIMAccountPublic(Integer.parseInt(arrayListImAccount.get(i)
//                    .getImImPrivacy()));
//            arrayListSocialContactObject.add(imAccount);
//
//            if (arrayListImAccount.get(i).getImImProtocol().equalsIgnoreCase("Facebook")
//                    || arrayListImAccount.get(i).getImImProtocol().equalsIgnoreCase("GooglePlus")
//                    || arrayListImAccount.get(i).getImImProtocol().equalsIgnoreCase("LinkedIn")) {
//                socialTypeList.remove(arrayListImAccount.get(i).getImImProtocol());
//            }
//        }
//
//        if (arrayListSocialContactObject.size() > 0) {
//            for (int i = 0; i < arrayListSocialContactObject.size(); i++) {
//                addSocialConnectView(arrayListSocialContactObject.get(i), "");
//            }
//            for (int i = 0; i < linearBusinessRelation.getChildCount(); i++) {
//                View linearSocialContact = linearBusinessRelation.getChildAt(i);
//                EditText socialContact = linearSocialContact.findViewById(R.id
//                        .input_value);
//                socialContact.addTextChangedListener(valueTextWatcher);
//            }
//        } else {
//            addSocialConnectView(arrayListSocialContactObject.get(i), "");
//        }
//    }

    @SuppressLint("InflateParams")
    private void addSocialConnectView(Object detailObject, String imAccountProtocol) {

        View view = LayoutInflater.from(this).inflate(R.layout.list_item_edit_profile_social, null);
        ImageView imageViewDelete = view.findViewById(R.id.image_delete);
        ImageView imageViewSocialIcon = view.findViewById(R.id.image_social_icon);
        ImageView imageViewSocialProfile = view.findViewById(R.id.image_social_profile);

        imageViewSocialProfile.setVisibility(View.GONE);
        imageViewDelete.setVisibility(View.GONE);

        LinearLayout linearContent = view.findViewById(R.id.linear_content);

        final EditText inputValue = view.findViewById(R.id.input_value);
//        TextView textFirstName = view.findViewById(R.id.text_first_name);
//        TextView textLastName = view.findViewById(R.id.text_last_name);
//        TextView textIsVerified = view.findViewById(R.id.text_is_verified);
//        TextView textProtocol = view.findViewById(R.id.input_protocol);
//        TextView imAccountProfileImage = view.findViewById(R.id.text_profile_image);
//        TextView textIsPublic = view.findViewById(R.id.text_is_public);

//        textIsVerified.setText(R.string.verify_now);
//        textIsVerified.setVisibility(View.GONE);

        final RelativeLayout relativeRowAddNewRelation = view.findViewById(R.id
                .relative_row_edit_profile_social);

        imageViewDelete.setTag(AppConstants.IM_ACCOUNT);
        inputValue.setHint(R.string.hint_account_name);
        inputValue.setTypeface(Utils.typefaceIcons(activity));

        inputValue.setInputType(InputType.TYPE_CLASS_TEXT);
        if (detailObject != null) {
            ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount)
                    detailObject;

            inputValue.setText(imAccount.getIMAccountDetails());
//            textProtocol.setText(imAccount.getIMAccountProtocol());
//            textFirstName.setText(imAccount.getIMAccountFirstName());
//            textLastName.setText(imAccount.getIMAccountLastName());
//            textIsPublic.setText(String.valueOf(imAccount.getIMAccountPublic()));

            relativeRowAddNewRelation.setTag(imAccount.getIMId());

        }

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linearBusinessRelation.getChildCount() > 1) {
                    linearBusinessRelation.removeView(relativeRowAddNewRelation);
                } else if (linearBusinessRelation.getChildCount() == 1) {
                    inputValue.getText().clear();
                }
            }
        });
        //</editor-fold>

        linearBusinessRelation.addView(view);
    }

    private void getOrganizationsList() {

        TableOrganizationMaster tableOrganizationMaster = new TableOrganizationMaster
                (databaseHandler);

        arrayListOrganization = tableOrganizationMaster
                .getAllOrganizationsFromPmId(Integer.parseInt(getUserPmId()));

        if (!(arrayListOrganization.size() > 0)) {
            inputValueBusiness.setText(R.string.str_hint_add_organization);
            inputValueBusiness.setTextColor(colorPineGreen);
        }
    }

    private void getGender() {

        TableProfileMaster tableProfileMaster = new TableProfileMaster
                (databaseHandler);

        strGender = tableProfileMaster
                .getUserGender(Integer.parseInt(getUserPmId()));

        if (StringUtils.isEmpty(strGender)) {
            inputValueFamily.setText(R.string.str_hint_add_family);
            inputValueFamily.setTextColor(colorPineGreen);
        }
    }

    private void dialogBusinessRelation() {

        ArrayList<String> relationList = new ArrayList<>();

        businessRelationDialog = new Dialog(this);
        businessRelationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        businessRelationDialog.setContentView(R.layout.dialog_all_organization);
        businessRelationDialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(businessRelationDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        businessRelationDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) businessRelationDialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.business_relation));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = (Button) businessRelationDialog.findViewById(R.id.button_right);
        Button buttonLeft = (Button) businessRelationDialog.findViewById(R.id.button_left);
        RippleView rippleRight = (RippleView) businessRelationDialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = (RippleView) businessRelationDialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_next);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_cancel);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                businessRelationDialog.dismiss();
                showAllOrganizations();
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                businessRelationDialog.dismiss();
            }
        });

        relationList.add(AppConstants.RELATION_COWORKER);
        relationList.add(AppConstants.RELATION_SUPPLIER);
        relationList.add(AppConstants.RELATION_COMPETITOR);
        relationList.add(AppConstants.RELATION_CUSTOMER);

        RecyclerView recyclerViewDialogList = (RecyclerView) businessRelationDialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        BusinessRelationListAdapter adapter = new BusinessRelationListAdapter(this, relationList,
                new BusinessRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String relationName) {
                        businessRelationName = relationName;
                    }
                }, "business");

        recyclerViewDialogList.setAdapter(adapter);

        businessRelationDialog.show();
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
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_done);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_back);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (businessRelationDialog != null & businessRelationDialog.isShowing())
                    businessRelationDialog.dismiss();
                dialog.dismiss();

                inputValueBusiness.setText(businessRelationName + " at " + organizationName);
                imgBusinessClear.setVisibility(View.VISIBLE);
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
                dialogBusinessRelation();
            }
        });

        RecyclerView recyclerViewDialogList = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        OrganizationRelationListAdapter adapter = new OrganizationRelationListAdapter(this, arrayListOrganization,
                new OrganizationRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String orgId, String orgName) {

                        organizationId = orgId;
                        organizationName = orgName;
                    }
                });

        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void dialogFamilyRelation() {

        ArrayList<String> familyRelationList = new ArrayList<String>(Arrays.asList(AppConstants.FAMILY_RELATION));

        final Dialog familyDialog = new Dialog(this);
        familyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        familyDialog.setContentView(R.layout.dialog_all_organization);
        familyDialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(familyDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        familyDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) familyDialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.family_relation));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = (Button) familyDialog.findViewById(R.id.button_right);
        Button buttonLeft = (Button) familyDialog.findViewById(R.id.button_left);
        RippleView rippleRight = (RippleView) familyDialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = (RippleView) familyDialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_done);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_cancel);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                familyDialog.dismiss();

                inputValueFamily.setText(familyRelation);
                imgFamilyClear.setVisibility(View.VISIBLE);
            }
        });

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                familyDialog.dismiss();
            }
        });

        RecyclerView recyclerViewDialogList = (RecyclerView) familyDialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        BusinessRelationListAdapter adapter = new BusinessRelationListAdapter(this, familyRelationList,
                new BusinessRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String relationName) {
                        familyRelation = relationName;
                    }
                }, "family");

        recyclerViewDialogList.setAdapter(adapter);

        familyDialog.show();
    }
}
