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
import android.text.Html;
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
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
    @BindView(R.id.input_value_friend)
    EditText inputValueFriend;
    @BindView(R.id.img_friend_clear)
    ImageView imgFriendClear;
    @BindView(R.id.linear_single_business_relation)
    LinearLayout linearSingleBusinessRelation;

    private Activity activity;
    private String isFrom = "";

    public static int orgPosition = 0;
    public static int businessRelationPosition = 0;
    public static int familyRelationPosition = 0;

    private Dialog businessRelationDialog;
    private String pmId = "", contactName = "", contactNumber = "", profileImage = "", organizationId = "", organizationName = "",
            businessRelationName = "", strGender = "", familyRelation = "", friendRelation = "";
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;
    private int colorPineGreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_relation);

        ButterKnife.bind(this);
        getIntentDetails(getIntent());
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

        if (isFrom.equalsIgnoreCase("rcp")) {
            setExistingData();
        } else {

            inputValueAddName.setFocusable(false);
            inputValueBusiness.setFocusable(false);
            inputValueFamily.setFocusable(false);

            imgClear.setOnClickListener(this);
            imgBusinessClear.setOnClickListener(this);
            imgFamilyClear.setOnClickListener(this);

            Glide.with(activity)
                    .load(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .into(imageProfile);
        }
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
                    contactNumber = data.getStringExtra("contactNumber");
                    profileImage = data.getStringExtra("profileImage");

                    inputValueAddName.setText(Html.fromHtml("<font color='#00796B'> " + contactName + "</font><br/>" + contactNumber));

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

    private void getIntentDetails(Intent intent) {

        if (intent != null) {

            if (intent.hasExtra(AppConstants.EXTRA_IS_FROM)) {
                isFrom = getIntent().getStringExtra(AppConstants.EXTRA_IS_FROM);
            } else {
                isFrom = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_FAMILY_RELATION)) {
                familyRelation = getIntent().getStringExtra(AppConstants.EXTRA_FAMILY_RELATION);
            } else {
                familyRelation = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_FRIEND_RELATION)) {
                friendRelation = getIntent().getStringExtra(AppConstants.EXTRA_FRIEND_RELATION);
            } else {
                friendRelation = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            } else {
                contactName = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NUMBER)) {
                contactNumber = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NUMBER);
            } else {
                contactNumber = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL)) {
                profileImage = intent.getStringExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL);
            } else {
                profileImage = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
                if (!StringUtils.isEmpty(pmId)) {
                    if (!pmId.equalsIgnoreCase("-1") && !pmId.equalsIgnoreCase(getUserPmId())) {
                        if (!Utils.isNetworkAvailable(this)) {

                            HashMap<String, String> mapProfileViews = new HashMap<>();
                            if (Utils.getHashMapPreference(this, AppConstants
                                    .PREF_PROFILE_VIEWS) != null) {
                                mapProfileViews.putAll(Utils.getHashMapPreference(this, AppConstants
                                        .PREF_PROFILE_VIEWS));
                            }
                            if (mapProfileViews.containsKey(pmId)) {
                                int count = Integer.parseInt(mapProfileViews.get(pmId));
                                mapProfileViews.put(pmId, String.valueOf(++count));
                            } else {
                                mapProfileViews.put(pmId, "1");
                            }
                            Utils.setHashMapPreference(this, AppConstants.PREF_PROFILE_VIEWS,
                                    mapProfileViews);
                        }
                    }
                } else {
                    pmId = "-1";
                }
            } else {
                pmId = "-1";
            }
        }
    }

    private void setExistingData() {

        inputValueAddName.setEnabled(false);
        inputValueFamily.setEnabled(false);
        inputValueFriend.setEnabled(false);
        imgClear.setEnabled(false);
        imgFamilyClear.setEnabled(false);
        imgFriendClear.setEnabled(false);

        imgFamilyClear.setVisibility(View.VISIBLE);
        imgFriendClear.setVisibility(View.VISIBLE);

        inputValueFriend.setVisibility(View.VISIBLE);
        checkboxFriend.setVisibility(View.GONE);

        linearSingleBusinessRelation.setVisibility(View.GONE);
        linearBusinessRelation.setVisibility(View.VISIBLE);

        imgClear.setImageResource(R.drawable.ico_relation_lock_svg);
        imgFamilyClear.setImageResource(R.drawable.ico_relation_lock_svg);
        imgFriendClear.setImageResource(R.drawable.ico_relation_lock_svg);

        inputValueAddName.setText(Html.fromHtml("<font color='#00796B'> " + contactName + "</font><br/>" + contactNumber));
        inputValueFamily.setText(familyRelation);
        inputValueFriend.setText(friendRelation);

        Glide.with(activity)
                .load(profileImage)
                .placeholder(R.drawable.home_screen_profile)
                .error(R.drawable.home_screen_profile)
                .bitmapTransform(new CropCircleTransformation(activity))
                .override(512, 512)
                .into(imageProfile);

        businessRelationDetails();

    }

    private void businessRelationDetails() {

        ArrayList<IndividualRelationType> arrayListData = makeTempData();
        ArrayList<IndividualRelationType> arrayList = new ArrayList<>();

        for (int i = 0; i < arrayListData.size(); i++) {
            IndividualRelationType relationType = new IndividualRelationType();
            relationType.setRelationId(String.valueOf(i));
            relationType.setRelationName(arrayListData.get(i).getRelationName());
            relationType.setOrganizationName(arrayListData.get(i).getOrganizationName());
            relationType.setFamilyName(arrayListData.get(i).getFamilyName());
            relationType.setIsVerify(arrayListData.get(i).getIsVerify());
            relationType.setIsFriendRelation(arrayListData.get(i).getIsFriendRelation());
            arrayList.add(relationType);
        }

        if (arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                addBusinessRelationView(arrayList.get(i));
            }
            addBusinessRelationView(null);
        } else {
            addBusinessRelationView(null);
        }
    }

    @SuppressLint("InflateParams")
    private void addBusinessRelationView(Object detailObject) {

        View view = LayoutInflater.from(this).inflate(R.layout.list_item_business_relation_list, null);
        ImageView imageViewDelete = view.findViewById(R.id.img_business_clear);
        imageViewDelete.setVisibility(View.GONE);

        final EditText inputValueBusiness = view.findViewById(R.id.input_value_business);
        final TextView textRelationName = view.findViewById(R.id.text_relation_name);
        final TextView textOrganizationName = view.findViewById(R.id.text_organization_name);
        final TextView textVerify = view.findViewById(R.id.text_verify);

        final LinearLayout linearRowBusinessRelationItem = view.findViewById(R.id
                .linear_row_business_relation_item);

        imageViewDelete.setTag(AppConstants.IM_ACCOUNT);
        inputValueBusiness.setHint(R.string.choose_relation);
        inputValueBusiness.setTypeface(Utils.typefaceIcons(activity));
        inputValueBusiness.setInputType(InputType.TYPE_CLASS_TEXT);
        inputValueBusiness.setFocusable(false);

        if (detailObject != null) {

            IndividualRelationType relationType = (IndividualRelationType) detailObject;

            imageViewDelete.setVisibility(View.VISIBLE);
            inputValueBusiness.setEnabled(false);

            inputValueBusiness.setText(relationType.getRelationName() + " at " + relationType.getOrganizationName());
            textRelationName.setText(relationType.getRelationName());
            textOrganizationName.setText(relationType.getOrganizationName());
            textVerify.setText(relationType.getIsVerify());
            linearRowBusinessRelationItem.setTag(relationType.getRelationId());

            if (relationType.getIsVerify().equalsIgnoreCase("1")) {
                imageViewDelete.setEnabled(false);
                imageViewDelete.setImageResource(R.drawable.ico_relation_lock_svg);
            } else {
                imageViewDelete.setEnabled(true);
                imageViewDelete.setImageResource(R.drawable.close_vector);
            }
        }

        inputValueBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView textVerify = linearRowBusinessRelationItem.findViewById(R.id.text_verify);

                if (linearBusinessRelation.getChildCount() > 1) {
                    if (!textVerify.getText().toString().trim().equalsIgnoreCase("1")) {
                        linearBusinessRelation.removeView(linearRowBusinessRelationItem);
                    }
                } else if (linearBusinessRelation.getChildCount() == 1) {
                    if (!textVerify.getText().toString().trim().equalsIgnoreCase("1"))
                        inputValueBusiness.getText().clear();
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
                if (businessRelationDialog != null && businessRelationDialog.isShowing())
                    businessRelationDialog.dismiss();
                dialog.dismiss();


                if (isFrom.equalsIgnoreCase("rcp")) {
                    IndividualRelationType relationType = new IndividualRelationType();
                    relationType.setRelationId("15");
                    relationType.setRelationName(businessRelationName);
                    relationType.setOrganizationName(organizationName);
                    relationType.setIsVerify("0");
                    relationType.setFamilyName("'");
                    relationType.setIsFriendRelation(false);

                    addBusinessRelationView(relationType);
                } else {
                    inputValueBusiness.setText(businessRelationName + " at " + organizationName);
                    imgBusinessClear.setVisibility(View.VISIBLE);
                }
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

    private ArrayList<IndividualRelationType> makeTempData() {

        ArrayList<IndividualRelationType> arrayList = new ArrayList<>();

        IndividualRelationType individualRelationTypeList;

        // All
        individualRelationTypeList = new IndividualRelationType();
        individualRelationTypeList.setRelationId("1");
        individualRelationTypeList.setRelationName("Co-worker");
        individualRelationTypeList.setOrganizationName("Hungama");
        individualRelationTypeList.setIsVerify("1");
        individualRelationTypeList.setFamilyName("");
        individualRelationTypeList.setIsFriendRelation(false);

        arrayList.add(individualRelationTypeList);

        individualRelationTypeList = new IndividualRelationType();
        individualRelationTypeList.setRelationId("2");
        individualRelationTypeList.setIsVerify("1");
        individualRelationTypeList.setRelationName("Co-worker");
        individualRelationTypeList.setOrganizationName("RawalInfocom");
        individualRelationTypeList.setFamilyName("");
        individualRelationTypeList.setIsFriendRelation(false);

        arrayList.add(individualRelationTypeList);

        return arrayList;
    }
}
