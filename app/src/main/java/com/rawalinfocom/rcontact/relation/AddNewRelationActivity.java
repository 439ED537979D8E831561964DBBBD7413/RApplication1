package com.rawalinfocom.rcontact.relation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.BuildConfig;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.EditProfileActivity;
import com.rawalinfocom.rcontact.database.TableOrganizationMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableRelationMappingMaster;
import com.rawalinfocom.rcontact.database.TableRelationMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.IndividualRelationType;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.Relation;
import com.rawalinfocom.rcontact.model.RelationRecommendationType;
import com.rawalinfocom.rcontact.model.RelationRequest;
import com.rawalinfocom.rcontact.model.RelationRequestResponse;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

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

    private TableRelationMaster tableRelationMaster;

    private Activity activity;
    private String isFrom = "";

    public static int orgPosition = 0;
    public static int businessRelationPosition = 0;
    public static int familyRelationPosition = 0;

    private Dialog businessRelationDialog;
    private String pmId = "", contactName = "", contactNumber = "", profileImage = "", organizationName = "",
            businessRelationName = "", strGender = "", familyRelation = "", friendRelation = "";
    private int businessRelationId, familyRelationId, organizationId;
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;
    private int colorPineGreen;

    private RelationRecommendationType recommendationType;
    private ArrayList<IndividualRelationType> arrayList;
    private ArrayList<String> arrayListOrgName, arrayListOrgId;
    private int businessPosition;
    private boolean isAdd = false, isFamilyAlreadyAdded = false;

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

        tableRelationMaster = new TableRelationMaster(databaseHandler);

        if (tableRelationMaster.getRelationCount() == 0) {
            tableRelationMaster.insertData();
        }

        activity = AddNewRelationActivity.this;
        arrayListOrganization = new ArrayList<>();

        colorPineGreen = ContextCompat.getColor(activity, R.color.colorAccent);

        inputValueAddName.setTypeface(Utils.typefaceRegular(this));
        inputValueBusiness.setTypeface(Utils.typefaceRegular(this));
        inputValueFamily.setTypeface(Utils.typefaceRegular(this));

        inputValueAddName.setFocusable(false);
        inputValueBusiness.setFocusable(false);
        inputValueFamily.setFocusable(false);

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

        arrayList = new ArrayList<>();
        arrayListOrgName = new ArrayList<>();
        arrayListOrgId = new ArrayList<>();

        if (isFrom.equalsIgnoreCase("existing")) {
            setExistingRelation();
        } else {

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
    protected void onResume() {
        super.onResume();
        getGender();
        getOrganizationsList();
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {

            //<editor-fold desc="REQ_SEND_RELATION_REQUEST">
            if (serviceType.contains(WsConstants.REQ_SEND_RELATION_REQUEST)) {
                WsResponseObject sendRelationRequestObject = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (sendRelationRequestObject != null && StringUtils.equalsIgnoreCase
                        (sendRelationRequestObject.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    ArrayList<RelationRequest> relationRequestResponse = sendRelationRequestObject.
                            getArrayListRelationRequestResponse();

                    Utils.showSuccessSnackBar(activity, relativeRootNewRelation,
                            "New Relation Added Successfully!!!");
                    storeProfileDataToDb(relationRequestResponse);

                    Utils.setBooleanPreference(AddNewRelationActivity.this,
                            AppConstants.PREF_GET_RELATION, false);

                    finish();

                } else {
                    if (sendRelationRequestObject != null) {
                        Log.e("error response", sendRelationRequestObject.getMessage());
                        Utils.showErrorSnackBar(this, relativeRootNewRelation,
                                sendRelationRequestObject.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "sendRelationRequestResponse null");
                        Utils.showErrorSnackBar(this, relativeRootNewRelation, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

        } else {
            Utils.showErrorSnackBar(this, relativeRootNewRelation, "" + error
                    .getLocalizedMessage());
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.input_value_family:

                if (StringUtils.isEmpty(strGender)) {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                } else {
                    dialogFamilyRelation();
                }
                break;

            case R.id.input_value_add_name:
                Intent intent = new Intent(activity, RContactsListActivity.class);
                startActivityForResult(intent, 101);// Activity is started with requestCode
                break;
            case R.id.input_value_business:

                if (arrayListOrganization.size() > 0) {
                    dialogBusinessRelation();
                } else {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                }
                break;

            case R.id.image_action_back:
                finish();
                break;
            case R.id.button_name_done:

                if (StringUtils.isEmpty(contactName)) {
                    Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                            "Please select User to establish relation!!");
                } else if (StringUtils.isEmpty(organizationName)) {
                    Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                            "Please select organization to establish relation!!");
                } else {

                    ArrayList<RelationRequest> relationRequests = new ArrayList<>();

                    RelationRequest friendRelationRequest = new RelationRequest();

                    if (friendRelation.equalsIgnoreCase("")) {
                        if (checkboxFriend.isChecked()) {
                            friendRelationRequest.setRcRelationMasterId(1);
                            friendRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                            friendRelationRequest.setRrmType(1);

                            relationRequests.add(friendRelationRequest);
                        }
                    }
//                    else {
//                        friendRelationRequest.setRcRelationMasterId(1);
//                        friendRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
//                        friendRelationRequest.setRrmType(1);

//                        relationRequests.add(friendRelationRequest);
//                    }


                    RelationRequest familyRelationRequest = new RelationRequest();

                    if (!StringUtils.isBlank(familyRelation)) {

                        if (!isFamilyAlreadyAdded) {
                            familyRelationRequest.setRcRelationMasterId(familyRelationId);
                            familyRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                            familyRelationRequest.setRrmType(2);
                            familyRelationRequest.setGender(strGender.equals("Male") ? 1 : 2);

                            relationRequests.add(familyRelationRequest);
                        }
                    }

                    if (isFrom.equalsIgnoreCase("existing")) {

                        for (int i = 0; i < arrayList.size(); i++) {

                            if (arrayList.get(i).getIsVerify().equalsIgnoreCase("0")) {
                                RelationRequest businessRelationRequest = new RelationRequest();

                                businessRelationRequest.setRcRelationMasterId(Integer.parseInt(
                                        arrayList.get(i).getRelationId()));
                                businessRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                                businessRelationRequest.setRcOrgId(Integer.parseInt(
                                        arrayList.get(i).getOrganizationId()));
                                businessRelationRequest.setRrmType(3);
                                businessRelationRequest.setOmName(arrayList.get(i).getOrganizationName());

                                relationRequests.add(businessRelationRequest);
                            }
                        }
                    } else {

                        RelationRequest businessRelationRequest = new RelationRequest();

                        if (!StringUtils.isBlank(organizationName)) {
                            businessRelationRequest.setRcRelationMasterId(businessRelationId);
                            businessRelationRequest.setRrmToPmId(Integer.parseInt(pmId));
                            businessRelationRequest.setRcOrgId(organizationId);
                            businessRelationRequest.setRrmType(3);
                            businessRelationRequest.setOmName(organizationName);

                            relationRequests.add(businessRelationRequest);
                        }
                    }

//                    System.out.println("Service call");
                    sendRelationRequest(relationRequests);
                }
                break;
            case R.id.button_name_cancel:
                finish();
                break;

            case R.id.img_clear:

                pmId = "";
                contactName = "";
                profileImage = "";
                businessRelationName = "";
                organizationId = 0;
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

                organizationId = 0;
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

            if (intent.hasExtra(AppConstants.EXTRA_EXISTING_RELATION_DETAILS)) {
                recommendationType = (RelationRecommendationType) getIntent().getSerializableExtra
                        (AppConstants.EXTRA_EXISTING_RELATION_DETAILS);
            } else {
                recommendationType = null;
            }

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

            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            }

            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL)) {
                profileImage = intent.getStringExtra(AppConstants.EXTRA_PROFILE_IMAGE_URL);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NUMBER)) {
                contactNumber = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NUMBER);
            }
        }
    }

    private void setExistingRelation() {

        linearSingleBusinessRelation.setVisibility(View.GONE);
        linearBusinessRelation.setVisibility(View.VISIBLE);

        if (recommendationType != null) {

            contactName = recommendationType.getFirstName() + " " + recommendationType.getLastName();
            profileImage = recommendationType.getProfileImage();
            contactNumber = recommendationType.getNumber();

            ArrayList<IndividualRelationType> individualRelationTypes = recommendationType
                    .getIndividualRelationTypeList();

            for (int i = 0; i < individualRelationTypes.size(); i++) {

                if (individualRelationTypes.get(i).getRelationType() == 1) {

                    friendRelation = "Friend";
                    inputValueFriend.setText(friendRelation);

                    imgFriendClear.setVisibility(View.VISIBLE);
                    imgFriendClear.setImageResource(R.drawable.ico_relation_lock_svg);
                    imgFriendClear.setEnabled(false);
                    inputValueFriend.setEnabled(false);
                    checkboxFriend.setVisibility(View.GONE);
                    inputValueFriend.setVisibility(View.VISIBLE);

                } else if (individualRelationTypes.get(i).getRelationType() == 2) {

                    isFamilyAlreadyAdded = true;

                    familyRelationId = Integer.parseInt(individualRelationTypes.get(i).getRelationId());
                    familyRelation = individualRelationTypes.get(i).getFamilyName();
                    inputValueFamily.setText(familyRelation);

                    imgFamilyClear.setVisibility(View.VISIBLE);
                    imgFamilyClear.setImageResource(R.drawable.ico_relation_lock_svg);
                    imgFamilyClear.setEnabled(false);
                    inputValueFamily.setEnabled(false);

                } else {

                    organizationName = individualRelationTypes.get(i).getOrganizationName();

                    IndividualRelationType relationType = new IndividualRelationType();
                    relationType.setRelationId(individualRelationTypes.get(i).getRelationId());
                    relationType.setRelationName(individualRelationTypes.get(i).getRelationName());
                    relationType.setOrganizationName(individualRelationTypes.get(i).getOrganizationName());
                    relationType.setOrganizationId(individualRelationTypes.get(i).getOrganizationId());
                    relationType.setIsVerify(individualRelationTypes.get(i).getIsVerify());
                    arrayList.add(relationType);

                    arrayListOrgName.add(individualRelationTypes.get(i).getOrganizationName());
                    arrayListOrgId.add(individualRelationTypes.get(i).getOrganizationId());

//                imgClear.setEnabled(false);
//                imgClear.setImageResource(R.drawable.ico_relation_lock_svg);
                }
            }

        } else {

            inputValueFamily.setFocusable(false);
            imgFamilyClear.setOnClickListener(this);

            checkboxFriend.setVisibility(View.VISIBLE);
            inputValueFriend.setVisibility(View.GONE);

        }

        inputValueAddName.setEnabled(false);
        inputValueAddName.setText(Html.fromHtml("<font color='#00796B'> " + contactName +
                "</font><br/>" + contactNumber));

        businessRelationDetails();

        Glide.with(activity)
                .load(profileImage)
                .placeholder(R.drawable.home_screen_profile)
                .error(R.drawable.home_screen_profile)
                .bitmapTransform(new CropCircleTransformation(activity))
                .override(512, 512)
                .into(imageProfile);
    }

    private void businessRelationDetails() {

        if (arrayList.size() == 0) {
            addBusinessRelationView(0, null);
        } else {
            for (int i = 0; i < arrayList.size(); i++) {
                addBusinessRelationView(i, arrayList.get(i));
            }
            addBusinessRelationView(arrayList.size(), null);
        }
    }

    @SuppressLint("InflateParams")
    private void addBusinessRelationView(int position, Object detailObject) {

        View view = LayoutInflater.from(this).inflate(R.layout.list_item_business_relation_list, null);
        ImageView imageViewDelete = view.findViewById(R.id.img_business_clear);
        imageViewDelete.setVisibility(View.GONE);

        final EditText inputValueBusiness = view.findViewById(R.id.input_value_business);
        final TextView textRelationName = view.findViewById(R.id.text_relation_name);
        final TextView textOrganizationName = view.findViewById(R.id.text_organization_name);
        final TextView textOrganizationId = view.findViewById(R.id.text_organization_id);
        final TextView textVerify = view.findViewById(R.id.text_verify);
        final TextView textRelationId = view.findViewById(R.id.text_relation_id);

        final LinearLayout linearRowBusinessRelationItem = view.findViewById(R.id
                .linear_row_business_relation_item);

        imageViewDelete.setTag(AppConstants.RELATION);
        inputValueBusiness.setHint(R.string.choose_relation);
        inputValueBusiness.setTypeface(Utils.typefaceIcons(activity));
        inputValueBusiness.setInputType(InputType.TYPE_CLASS_TEXT);
        inputValueBusiness.setFocusable(false);

        if (detailObject != null) {

            IndividualRelationType relationType = (IndividualRelationType) detailObject;

            imageViewDelete.setVisibility(View.VISIBLE);
            inputValueBusiness.setEnabled(false);

            inputValueBusiness.setText(String.format("%s at %s", relationType.getRelationName(),
                    relationType.getOrganizationName()));
            textRelationName.setText(relationType.getRelationName());
            textOrganizationName.setText(relationType.getOrganizationName());
            textOrganizationId.setText(relationType.getOrganizationId());
            textVerify.setText(relationType.getIsVerify());
            textRelationId.setText(relationType.getRelationId());
            linearRowBusinessRelationItem.setTag(relationType.getRelationId());

            if (relationType.getIsVerify().equalsIgnoreCase("1")) {
                imageViewDelete.setEnabled(false);
                imageViewDelete.setImageResource(R.drawable.ico_relation_lock_svg);
            } else {
                imageViewDelete.setEnabled(true);
                imageViewDelete.setImageResource(R.drawable.close_vector);
            }
        }

        inputValueBusiness.setTag(position);
        inputValueBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arrayListOrganization.size() > 0) {

                    if (StringUtils.isEmpty(contactName)) {
                        Utils.showErrorSnackBar(activity, relativeRootNewRelation,
                                "Please select User to establish relation!!");
                    } else {
                        businessPosition = (int) view.getTag();
                        dialogBusinessRelation();
                    }
                } else {
                    startActivityIntent(activity, EditProfileActivity.class, null);
                }
            }
        });

        imageViewDelete.setTag(position);
        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView textVerify = linearRowBusinessRelationItem.findViewById(R.id.text_verify);

                if (linearBusinessRelation.getChildCount() > 1) {
                    if (!textVerify.getText().toString().trim().equalsIgnoreCase("1")) {
                        linearBusinessRelation.removeView(linearRowBusinessRelationItem);

                        addBusinessDetailsToList();

                        linearBusinessRelation.removeAllViews();
                        businessRelationDetails();
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

    private void addBusinessDetailsToList() {

        arrayList.clear();

        for (int i = 0; i < linearBusinessRelation.getChildCount() - 1; i++) {

            IndividualRelationType relationType = new IndividualRelationType();
            View linearBusiness = linearBusinessRelation.getChildAt(i);

            final EditText inputValueBusiness = linearBusiness.findViewById(R.id.input_value_business);
            final TextView textRelationName = linearBusiness.findViewById(R.id.text_relation_name);
            final TextView textOrganizationName = linearBusiness.findViewById(R.id.text_organization_name);
            final TextView textOrganizationId = linearBusiness.findViewById(R.id.text_organization_id);
            final TextView textVerify = linearBusiness.findViewById(R.id.text_verify);
            final TextView textRelationId = linearBusiness.findViewById(R.id.text_relation_id);

            relationType.setRelationName(textRelationName.getText().toString().trim());
            relationType.setOrganizationId(textOrganizationId.getText().toString().trim());
            relationType.setOrganizationName(textOrganizationName.getText().toString().trim());

            if (StringUtils.length(textVerify.getText().toString()) > 0) {
                relationType.setIsVerify(textVerify.getText().toString().trim());
            } else {
                relationType.setIsVerify("0");
            }

            if (StringUtils.length(textRelationId.getText().toString()) > 0) {
                relationType.setRelationId(textRelationId.getText().toString().trim());
            } else {
                relationType.setRelationId("0");
            }

            arrayList.add(relationType);
        }

        for (int j = 0; j < arrayListOrganization.size(); j++) {

            ProfileDataOperationOrganization organization = new ProfileDataOperationOrganization();
            organization.setOrgId(arrayListOrganization.get(j).getOrgId());
            organization.setOrgName(arrayListOrganization.get(j).getOrgName());
            organization.setOrgJobTitle(arrayListOrganization.get(j).getOrgJobTitle());
            organization.setOrgIndustryType(arrayListOrganization.get(j).getOrgIndustryType());
            organization.setOrgEntId(arrayListOrganization.get(j).getOrgEntId());
            organization.setOrgLogo(arrayListOrganization.get(j).getOrgLogo());
            organization.setOrgFromDate(arrayListOrganization.get(j).getOrgFromDate());
            organization.setOrgToDate(arrayListOrganization.get(j).getOrgToDate());
            organization.setIsVerify(arrayListOrganization.get(j).getIsVerify());
            organization.setOrgRcpType(arrayListOrganization.get(j).getOrgRcpType());
            organization.setIsInUse("0");
            arrayListOrganization.set(j, organization);
        }
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
        } else {
            inputValueFamily.setFocusable(false);
            inputValueFamily.setText("");
            inputValueFamily.setTextColor(colorPineGreen);
        }
    }

    private void dialogBusinessRelation() {

        ArrayList<Relation> relationList = tableRelationMaster.getRelation(3);

        businessRelationDialog = new Dialog(this);
        businessRelationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        businessRelationDialog.setContentView(R.layout.dialog_all_organization);
        businessRelationDialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(businessRelationDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        businessRelationDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = businessRelationDialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.business_relation));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = businessRelationDialog.findViewById(R.id.button_right);
        Button buttonLeft = businessRelationDialog.findViewById(R.id.button_left);
        RippleView rippleRight = businessRelationDialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = businessRelationDialog.findViewById(R.id.ripple_left);

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

//        relationList.add(AppConstants.RELATION_COWORKER);
//        relationList.add(AppConstants.RELATION_SUPPLIER);
//        relationList.add(AppConstants.RELATION_COMPETITOR);
//        relationList.add(AppConstants.RELATION_CUSTOMER);

        RecyclerView recyclerViewDialogList = businessRelationDialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        BusinessRelationListAdapter adapter = new BusinessRelationListAdapter(this, relationList,
                new BusinessRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String relationName, int id) {
                        businessRelationName = relationName;
                        businessRelationId = id;
                    }
                }, "business");

        recyclerViewDialogList.setAdapter(adapter);

        businessRelationDialog.show();
    }

    private void showAllOrganizations() {

//        if (arrayList.size() > 0) {
////            for (int i = 0; i < arrayListOrganization.size(); i++) {
////                setOrganizationArrayList(i);
////            }
//            for (int i = 0; i < arrayList.size(); i++) {
//                setOrganizationArrayList(i);
//            }
//        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.title_all_organizations));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = dialog.findViewById(R.id.button_right);
        Button buttonLeft = dialog.findViewById(R.id.button_left);
        RippleView rippleRight = dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = dialog.findViewById(R.id.ripple_left);

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


                if (isFrom.equalsIgnoreCase("rcp") ||
                        isFrom.equalsIgnoreCase("existing")) {

                    IndividualRelationType relationType = new IndividualRelationType();

                    relationType.setRelationName(businessRelationName);
                    relationType.setOrganizationName(organizationName);

                    if (businessPosition == arrayList.size()) {

                        relationType.setRelationId(String.valueOf(businessRelationId));
                        relationType.setIsVerify("0");
                        relationType.setOrganizationId(String.valueOf(organizationId));
                        arrayList.add(relationType);

                        isAdd = true;

                    } else {

                        IndividualRelationType relationType1 = arrayList.
                                get(businessPosition);

                        relationType.setRelationId(relationType1.getRelationId());
                        relationType.setOrganizationId(String.valueOf(organizationId));
                        relationType.setIsVerify("0");
                        arrayList.set(businessPosition, relationType);

                        isAdd = false;
                    }

                    linearBusinessRelation.removeAllViews();

                    for (int i = 0; i < arrayList.size(); i++) {
                        addBusinessRelationView(i, arrayList.get(i));
                    }

                    if (isAdd)
                        addBusinessRelationView(arrayList.size(), null);

                } else {
                    inputValueBusiness.setText(String.format("%s at %s", businessRelationName,
                            organizationName));
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

        RecyclerView recyclerViewDialogList = dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        OrganizationRelationListAdapter adapter = new OrganizationRelationListAdapter(this,
                arrayListOrganization, arrayListOrgName, arrayListOrgId,
                new OrganizationRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String orgId, String orgName) {

                        organizationId = (int) Long.parseLong(orgId);
                        organizationName = orgName;
                    }
                }, businessRelationName);

        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void setOrganizationArrayList(int i) {

        for (int j = 0; j < arrayList.size(); j++) {

            ProfileDataOperationOrganization organization = new ProfileDataOperationOrganization();
            organization.setOrgId(arrayListOrganization.get(j).getOrgId());
            organization.setOrgName(arrayListOrganization.get(j).getOrgName());
            organization.setOrgJobTitle(arrayListOrganization.get(j).getOrgJobTitle());
            organization.setOrgIndustryType(arrayListOrganization.get(j).getOrgIndustryType());
            organization.setOrgEntId(arrayListOrganization.get(j).getOrgEntId());
            organization.setOrgLogo(arrayListOrganization.get(j).getOrgLogo());
            organization.setOrgFromDate(arrayListOrganization.get(j).getOrgFromDate());
            organization.setOrgToDate(arrayListOrganization.get(j).getOrgToDate());
            organization.setIsVerify(arrayListOrganization.get(j).getIsVerify());
            organization.setOrgRcpType(arrayListOrganization.get(j).getOrgRcpType());

            if (arrayListOrganization.get(j).getOrgName().equalsIgnoreCase(
                    arrayList.get(i).getOrganizationName())) {

                System.out.println("RContacts data getOrgName --> " + arrayListOrganization.get(j).getOrgName()
                        + " " + arrayList.get(i).getOrganizationName());
                System.out.println("RContacts data getOrgEntId --> " + arrayListOrganization.get(j).getOrgEntId()
                        + " " + arrayList.get(i).getOrganizationId());

                if (arrayListOrganization.get(j).getOrgEntId().equalsIgnoreCase(
                        arrayList.get(i).getOrganizationId())) {
                    organization.setIsInUse("1");
                    arrayListOrganization.set(j, organization);
                } else {
                    organization.setIsInUse("0");
                    arrayListOrganization.set(j, organization);
                }
            } else {

                System.out.println("RContacts data getIsInUse --> " + arrayListOrganization.get(j).getIsInUse());
                if (arrayListOrganization.get(j).getIsInUse().equalsIgnoreCase("1")) {
//                    organization.setIsInUse("1");
//                    arrayListOrganization.set(j, organization);
                    if (arrayListOrganization.get(j).getOrgEntId().equalsIgnoreCase(
                            arrayList.get(i).getOrganizationId())) {
                        organization.setIsInUse("1");
                        arrayListOrganization.set(j, organization);
                    } else {
                        organization.setIsInUse("0");
                        arrayListOrganization.set(j, organization);
                    }
                } else {
                    organization.setIsInUse("0");
                    arrayListOrganization.set(j, organization);
                }
            }
        }
    }

    private void dialogFamilyRelation() {

        ArrayList<Relation> familyRelationList = tableRelationMaster.getRelation(2);

        final Dialog familyDialog = new Dialog(this);
        familyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        familyDialog.setContentView(R.layout.dialog_all_organization);
        familyDialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(familyDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        familyDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = familyDialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(getString(R.string.family_relation));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));

        Button buttonRight = familyDialog.findViewById(R.id.button_right);
        Button buttonLeft = familyDialog.findViewById(R.id.button_left);
        RippleView rippleRight = familyDialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = familyDialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(this));
        buttonRight.setText(R.string.str_done);
        buttonLeft.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setText(R.string.str_cancel);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                familyDialog.dismiss();

                isFamilyAlreadyAdded = false;
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

        RecyclerView recyclerViewDialogList = familyDialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(this));

        BusinessRelationListAdapter adapter = new BusinessRelationListAdapter(this, familyRelationList,
                new BusinessRelationListAdapter.OnClickListener() {
                    @Override
                    public void onClick(String relationName, int id) {
                        familyRelation = relationName;
                        familyRelationId = id;
                    }

                }, "family");

        recyclerViewDialogList.setAdapter(adapter);

        familyDialog.show();
    }

    private void sendRelationRequest(ArrayList<RelationRequest> relationRequest) {

        WsRequestObject sendRelationRequestObject = new WsRequestObject();
        sendRelationRequestObject.setArrayListRelationRequest(relationRequest);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    sendRelationRequestObject, null, WsResponseObject.class, WsConstants
                    .REQ_SEND_RELATION_REQUEST, getResources().getString(R.string.msg_please_wait),
                    true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    BuildConfig.WS_ROOT + WsConstants.REQ_SEND_RELATION_REQUEST);
        } else {
            Utils.showErrorSnackBar(this, relativeRootNewRelation, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void storeProfileDataToDb(ArrayList<RelationRequest> relationRequestResponse) {

        //<editor-fold desc="Relation Mapping Master">
        TableRelationMappingMaster tableRelationMappingMaster = new
                TableRelationMappingMaster(databaseHandler);

        if (!Utils.isArraylistNullOrEmpty(relationRequestResponse)) {

            ArrayList<RelationRequestResponse> relationResponseList = new ArrayList<>();

            for (int i = 0; i < relationRequestResponse.size(); i++) {
                RelationRequestResponse relationResponse = new RelationRequestResponse();

                relationResponse.setId(relationRequestResponse.get(i).getId());
                relationResponse.setRcRelationMasterId(relationRequestResponse.get(i).getRcRelationMasterId());
                relationResponse.setRrmToPmId(relationRequestResponse.get(i).getRrmToPmId());
                relationResponse.setRrmType(relationRequestResponse.get(i).getRrmType());
                relationResponse.setRrmFromPmId(relationRequestResponse.get(i).getRrmFromPmId());
                relationResponse.setRcStatus(relationRequestResponse.get(i).getRcStatus());
                relationResponse.setRcOrgId(relationRequestResponse.get(i).getRcOrgId());
                relationResponse.setCreatedAt(relationRequestResponse.get(i).getCreatedAt());

                relationResponseList.add(relationResponse);
            }

            tableRelationMappingMaster.addRelationMapping(relationResponseList);
        }
    }
}
