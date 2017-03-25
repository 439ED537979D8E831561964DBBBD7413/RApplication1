package com.rawalinfocom.rcontact.contacts;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.ContactListingActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.CallHistoryListAdapter;
import com.rawalinfocom.rcontact.adapters.OrganizationListAdapter;
import com.rawalinfocom.rcontact.adapters.ProfileDetailAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
//import com.rawalinfocom.rcontact.calllog.CallHistoryDetailsActivity;
import com.rawalinfocom.rcontact.calllog.CallLogFragment;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableContactRatingMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.ProfileMenuOptionDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.CallLogType;
import com.rawalinfocom.rcontact.model.DbRating;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.ProfileVisit;
import com.rawalinfocom.rcontact.model.Rating;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileDetailActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, WsResponseListener {

    private final String TAG_IMAGE_EDIT = "tag_edit";
    private final String TAG_IMAGE_FAVOURITE = "tag_favourite";
    private final String TAG_IMAGE_UN_FAVOURITE = "tag_un_favourite";
    private final String TAG_IMAGE_CALL = "tag_call";
    private final String TAG_IMAGE_SHARE = "tag_share";

    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    TextView textToolbarTitle;
    RippleView rippleActionBack;
    RippleView rippleActionRightLeft;
    RippleView rippleActionRightCenter;
    RippleView rippleActionRightRight;
    ImageView imageRightLeft;
    ImageView imageRightCenter;

    /* @BindView(R.id.text_joining_date)
     TextView textJoiningDate;*/
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.relative_contact_details)
    RelativeLayout relativeContactDetails;
    @BindView(R.id.text_user_rating)
    TextView textUserRating;
    @BindView(R.id.linear_basic_detail_rating)
    LinearLayout linearBasicDetailRating;
    @BindView(R.id.text_name)
    TextView textName;
    /*  @BindView(R.id.text_cloud_name)
      TextView textCloudName;*/
    @BindView(R.id.text_full_screen_text)
    TextView textFullScreenText;
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
    @BindView(R.id.linear_call_sms)
    LinearLayout linearCallSms;
    @BindView(R.id.button_call_log)
    Button buttonCallLog;
    @BindView(R.id.button_sms)
    Button buttonSms;
    @BindView(R.id.relative_call_history)
    RelativeLayout relativeCallHistory;
    /* @BindView(R.id.text_icon_history)
     TextView textIconHistory;*/
    @BindView(R.id.recycler_call_history)
    RecyclerView recyclerCallHistory;
    @BindView(R.id.ripple_call_log)
    RippleView rippleCallLog;
    @BindView(R.id.ripple_sms)
    RippleView rippleSms;
    @BindView(R.id.text_no_history_to_show)
    TextView textNoHistoryToShow;

    RelativeLayout relativeRootRatingDialog;

    ProfileDataOperation profileDataOperationVcard;

    String pmId, phoneBookId, contactName = "", cloudContactName = null, checkNumberFavourite =
            null;
    boolean displayOwnProfile = false, isHideFavourite = false;

    PhoneBookContacts phoneBookContacts;
    int listClickedPosition = -1;

    ProfileDetailAdapter phoneDetailAdapter;
    MaterialDialog callConfirmationDialog;

    ArrayList<String> arrayListFavouriteContacts;
    RContactApplication rContactApplication;
    boolean profileActivityCallInstance = false;
    ArrayList<CallLogType> arrayListHistory;
    String historyNumber = "";
    String historyName = "";
    long historyDate;
    Date callReceiverDate;
    CallHistoryListAdapter callHistoryListAdapter;
    String profileContactNumber;


    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        rContactApplication = (RContactApplication) getApplicationContext();
        ButterKnife.bind(this);

        phoneBookContacts = new PhoneBookContacts(this);
        Intent intent = getIntent();

        if (intent != null) {

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER)) {
                historyNumber = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_NAME)) {
                historyName = intent.getStringExtra(AppConstants.EXTRA_CALL_HISTORY_NAME);
            }

            if (intent.hasExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE)) {
                profileActivityCallInstance = intent.getBooleanExtra(AppConstants
                        .EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, false);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CALL_HISTORY_DATE)) {
                historyDate = intent.getLongExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, 0);
            }


            if (intent.hasExtra(AppConstants.EXTRA_PM_ID)) {
                pmId = intent.getStringExtra(AppConstants.EXTRA_PM_ID);
                if (!pmId.equalsIgnoreCase("-1") && !pmId.equalsIgnoreCase(getUserPmId())) {
                    if (Utils.isNetworkAvailable(this)) {
                        ArrayList<ProfileVisit> profileVisits = new ArrayList<>();
                        ProfileVisit profileVisit = new ProfileVisit();
                        profileVisit.setVisitorPmId(Integer.parseInt(pmId));
                        profileVisit.setVisitCount(1);
                        profileVisits.add(profileVisit);
                        profileVisit(profileVisits);
                    } else {
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

            if (intent.hasExtra(AppConstants.EXTRA_PHONE_BOOK_ID)) {
                phoneBookId = intent.getStringExtra(AppConstants.EXTRA_PHONE_BOOK_ID);
            } else {
                phoneBookId = "-1";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_NAME)) {
                contactName = intent.getStringExtra(AppConstants.EXTRA_CONTACT_NAME);
            } else {
                contactName = "";
            }

            if (intent.hasExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME)) {
                cloudContactName = intent.getStringExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME);
                cloudContactName = StringUtils.substring(cloudContactName, 2, cloudContactName
                        .length() - 1);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE)) {
                isHideFavourite = true;
                checkNumberFavourite = intent.getStringExtra(AppConstants
                        .EXTRA_CHECK_NUMBER_FAVOURITE);
            }

            if (intent.hasExtra(AppConstants.EXTRA_CONTACT_POSITION)) {
                listClickedPosition = intent.getIntExtra(AppConstants.EXTRA_CONTACT_POSITION, -1);
            }
        }

        if (pmId.equalsIgnoreCase(getUserPmId())) {
            displayOwnProfile = true;
        }

        arrayListFavouriteContacts = new ArrayList<>();
        if (!Utils.isArraylistNullOrEmpty(Utils.getArrayListPreference(this, AppConstants
                .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL))) {
            arrayListFavouriteContacts.addAll(Utils.getArrayListPreference(this, AppConstants
                    .PREF_FAVOURITE_CONTACT_NUMBER_EMAIL));
        }

        init();

       /* if (!TextUtils.isEmpty(historyName)) {
            fetchCallLogHistory(historyName);

        } else {*/
        fetchCallLogHistoryDateWise(historyNumber);

//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter(AppConstants.ACTION_LOCAL_BROADCAST_PROFILE);
        localBroadcastManager.registerReceiver(localBroadcastReceiver, intentFilter);



        if(profileActivityCallInstance){
            fetchCallLogHistoryDateWise(historyNumber);

        }else{
            if (!TextUtils.isEmpty(contactName) && !contactName.equalsIgnoreCase("[Unknown]")) {
                fetchAllCallLogHistory(contactName);
            } else {
                if (!TextUtils.isEmpty(profileContactNumber)) {
                    fetchAllCallLogHistory(profileContactNumber);
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(localBroadcastReceiver);
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

            case R.id.ripple_call_log:

               /* buttonCallLog.setClickable(false);
                buttonCallLog.setEnabled(false);
                buttonCallLog.setBackgroundColor(getResources().getColor(R.color.colorDarkGray));
                rippleCallLog.setEnabled(false);*/
               /* Intent intent = new Intent(ProfileDetailActivity.this, CallHistoryDetailsActivity.class);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER,historyNumber);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME,historyName);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE,historyDate);
                intent.putExtra(AppConstants.EXTRA_PM_ID,pmId);
                intent.putExtra(AppConstants.EXTRA_PHONE_BOOK_ID,phoneBookId);
                intent.putExtra(AppConstants.EXTRA_CONTACT_NAME,contactName);
                intent.putExtra(AppConstants.EXTRA_CLOUD_CONTACT_NAME,cloudContactName);
                intent.putExtra(AppConstants.EXTRA_CHECK_NUMBER_FAVOURITE,checkNumberFavourite);
                intent.putExtra(AppConstants.EXTRA_CONTACT_POSITION,listClickedPosition);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);*/

                /*profileLayoutVisibility();
                if (!TextUtils.isEmpty(contactName) && !contactName.equalsIgnoreCase("[Unknown]")) {
                    fetchAllCallLogHistory(contactName);
                } else {
                    if (!TextUtils.isEmpty(profileContactNumber)) {
                        fetchAllCallLogHistory(profileContactNumber);
                    }
                }*/
                break;


            case R.id.ripple_action_right_center:


                if (StringUtils.equals(imageRightCenter.getTag().toString(), TAG_IMAGE_CALL)) {
                    showCallConfirmationDialog(historyNumber);

                } else {
                    if (!StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                        TableProfileMaster tableProfileMaster = new TableProfileMaster
                                (databaseHandler);
                        UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                                .parseInt(pmId));
                        showChooseShareOption(StringUtils.trimToEmpty(userProfile.getPmFirstName()),
                                StringUtils.trimToEmpty(userProfile.getPmLastName()));
                    } else {
                        showChooseShareOption(null, null);
                    }
                }
                break;

            case R.id.ripple_action_right_left:
                if (StringUtils.equals(imageRightLeft.getTag().toString(), TAG_IMAGE_FAVOURITE)
                        || StringUtils.equals(imageRightLeft.getTag().toString(),
                        TAG_IMAGE_UN_FAVOURITE)) {
                    int favStatus;
                    if (StringUtils.equals(imageRightLeft.getTag().toString(),
                            TAG_IMAGE_FAVOURITE)) {
                        favStatus = PhoneBookContacts.STATUS_UN_FAVOURITE;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                        imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
                    } else {
                        favStatus = PhoneBookContacts.STATUS_FAVOURITE;
                        imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                        imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
                    }
                    int updateStatus = phoneBookContacts.setFavouriteStatus(phoneBookId, favStatus);
                    if (updateStatus != 1) {
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, "Error while " +
                                "updating favourite status!");
                    }
                    ArrayList<ProfileData> arrayListFavourites = new ArrayList<>();
                    ProfileData favouriteStatus = new ProfileData();
                    favouriteStatus.setLocalPhoneBookId(phoneBookId);
                    favouriteStatus.setIsFavourite(String.valueOf(favStatus));
                    arrayListFavourites.add(favouriteStatus);
                    setFavouriteStatus(arrayListFavourites);

                    rContactApplication.setFavouriteModified(true);

                } else if (StringUtils.equals(imageRightLeft.getTag().toString(), TAG_IMAGE_EDIT)) {
                    startActivityIntent(ProfileDetailActivity.this, EditProfileActivity.class,
                            null);
                }
                break;

            case R.id.ripple_action_right_right:
                ProfileMenuOptionDialog profileMenuOptionDialog;
                boolean isFromCallLogTab = false;
                if (profileActivityCallInstance) {
                    isFromCallLogTab = true;
                    if (!TextUtils.isEmpty(historyName)) {
                        ArrayList<String> arrayListName = new ArrayList<>(Arrays.asList(this.getString(R.string.edit),
                                this.getString(R.string.view_in_ac), this.getString(R.string.view_in_rc),
                                this.getString(R.string.call_reminder),
                                this.getString(R.string.block), this.getString(R.string.delete),
                                this.getString(R.string.clear_call_log)));
                        profileMenuOptionDialog = new ProfileMenuOptionDialog(this, arrayListName, historyNumber,
                                historyDate, isFromCallLogTab, arrayListHistory);
                        profileMenuOptionDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(historyNumber)) {
                            ArrayList<String> arrayListNumber = new ArrayList<>(Arrays.asList(this.getString(R.string.add_to_contact),
                                    this.getString(R.string.add_to_existing_contact), this.getString(R.string.view_profile),
                                    this.getString(R.string.copy_phone_number),
                                    this.getString(R.string.call_reminder), this.getString(R.string.block),
                                    this.getString(R.string.delete), this.getString(R.string.clear_call_log)));
                            profileMenuOptionDialog = new ProfileMenuOptionDialog(this, arrayListNumber, historyNumber,
                                    historyDate, isFromCallLogTab, arrayListHistory);
                            profileMenuOptionDialog.showDialog();
                        }
                    }
                } else {
                    isFromCallLogTab = false;
                    if (!TextUtils.isEmpty(contactName) && !contactName.equalsIgnoreCase("[Unknown]")) {
                        ArrayList<String> arrayListName = new ArrayList<>(Arrays.asList(this.getString(R.string.edit),
                                this.getString(R.string.view_in_ac), this.getString(R.string.view_in_rc),
                                this.getString(R.string.call_reminder),
                                this.getString(R.string.block), this.getString(R.string.delete), this.getString(R.string.clear_call_log)));
                        profileMenuOptionDialog = new ProfileMenuOptionDialog(this, arrayListName, contactName,
                                historyDate, isFromCallLogTab, arrayListHistory);
                        profileMenuOptionDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(profileContactNumber)) {
                            ArrayList<String> arrayListNumber = new ArrayList<>(Arrays.asList(this.getString(R.string.add_to_contact),
                                    this.getString(R.string.add_to_existing_contact), this.getString(R.string.view_profile),
                                    this.getString(R.string.copy_phone_number),
                                    this.getString(R.string.call_reminder), this.getString(R.string.block), this.getString(R.string.delete),
                                    this.getString(R.string.clear_call_log)));
                            profileMenuOptionDialog = new ProfileMenuOptionDialog(this, arrayListNumber, profileContactNumber
                                    , historyDate, isFromCallLogTab, arrayListHistory);
                            profileMenuOptionDialog.showDialog();
                        }

                    }
                }
                break;
        }
    }


    private BroadcastReceiver localBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Profile Activity ", "onReceive() of LocalBroadcast");

            arrayListHistory.clear();
            recyclerCallHistory.setVisibility(View.GONE);
            setHistoryAdapter();

        }
    };

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {

            //<editor-fold desc="REQ_GET_PROFILE_DETAIL">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_PROFILE_DETAIL)) {
                WsResponseObject profileDetailResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (profileDetailResponse != null && StringUtils.equalsIgnoreCase
                        (profileDetailResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    final ProfileDataOperation profileDetail = profileDetailResponse
                            .getProfileDetail();
                    setUpView(profileDetail);

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

            // <editor-fold desc="REQ_MARK_AS_FAVOURITE">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_MARK_AS_FAVOURITE)) {
                WsResponseObject favouriteStatusResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (favouriteStatusResponse == null || !StringUtils.equalsIgnoreCase
                        (favouriteStatusResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {
                    if (favouriteStatusResponse != null) {
                        Log.e("error response", favouriteStatusResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }
            }
            //</editor-fold>

            // <editor-fold desc="REQ_RCP_PROFILE_SHARING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_RCP_PROFILE_SHARING)) {
                WsResponseObject profileSharingResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (profileSharingResponse != null && StringUtils.equalsIgnoreCase
                        (profileSharingResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    File vcfFile = new File(this.getExternalFilesDir(null), contactName + ".vcf");
                    FileWriter fw;
                    try {
                        fw = new FileWriter(vcfFile);
                        fw.write(profileSharingResponse.getProfileSharingData());
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/x-vcard");
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(vcfFile));
                    startActivity(sendIntent);

                } else {
                    if (profileSharingResponse != null) {
                        Log.e("error response", profileSharingResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "otpDetailResponse null");
                        Utils.showErrorSnackBar(this, relativeRootProfileDetail, getString(R
                                .string.msg_try_later));
                    }
                }

            }
            //</editor-fold>

            // <editor-fold desc="REQ_PROFILE_RATING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_PROFILE_RATING)) {
                WsResponseObject profileRatingResponse = (WsResponseObject) data;
                if (profileRatingResponse != null && StringUtils.equalsIgnoreCase
                        (profileRatingResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(this, relativeRootProfileDetail, "Rating Submitted");

                    if (profileRatingResponse.getProfileRating() != null) {

                        DbRating dbRating = new DbRating();
                        Rating responseRating = profileRatingResponse.getProfileRating();
                        dbRating.setRcProfileMasterPmId(String.valueOf(responseRating.getPrToPmId
                                ()));
                        dbRating.setCrmStatus(String.valueOf(responseRating.getPrStatus()));
                        dbRating.setCrmRating(responseRating.getPrRatingStars());
                        dbRating.setCrmCloudPrId(String.valueOf(responseRating.getPrId()));
                        dbRating.setCrmComment(responseRating.getPrComment());
                        dbRating.setCrmCreatedAt(responseRating.getCreatedAt());

                        TableContactRatingMaster tableContactRatingMaster = new
                                TableContactRatingMaster(databaseHandler);
                        tableContactRatingMaster.addRating(dbRating);

                        TableProfileMaster tableProfileMaster = new TableProfileMaster
                                (databaseHandler);
                        tableProfileMaster.updateUserProfileRating(pmId, responseRating
                                .getProfileRating(), responseRating.getTotalProfileRateUser());

                        ratingUser.setRating(Float.parseFloat(responseRating.getProfileRating()));
                        textUserRating.setText(responseRating.getTotalProfileRateUser());
                    }
                } else {
                    if (profileRatingResponse != null) {
                        Log.e("error response", profileRatingResponse.getMessage());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.linear_basic_detail_rating)
    public void onRatingClick() {

        if (!StringUtils.equalsIgnoreCase(pmId, "-1") && !displayOwnProfile) {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_submit_rating);
            dialog.setCancelable(false);

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

            TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
            relativeRootRatingDialog = (RelativeLayout) dialog.findViewById(R.id
                    .relative_root_rating_dialog);
            final RatingBar ratingUser = (RatingBar) dialog.findViewById(R.id.rating_user);
            TextView textComment = (TextView) dialog.findViewById(R.id.text_comment);
            final TextView textRemainingCharacters = (TextView) dialog.findViewById(R.id
                    .text_remaining_characters);
            final EditText inputComment = (EditText) dialog.findViewById(R.id.input_comment);
            RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
            Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
            RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
            Button buttonRight = (Button) dialog.findViewById(R.id.button_right);

            textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
            textComment.setTypeface(Utils.typefaceRegular(this));
            textRemainingCharacters.setTypeface(Utils.typefaceLight(this));
            inputComment.setTypeface(Utils.typefaceRegular(this));
            buttonLeft.setTypeface(Utils.typefaceSemiBold(this));
            buttonRight.setTypeface(Utils.typefaceSemiBold(this));

            textDialogTitle.setText("Rate " + contactName);
            textRemainingCharacters.setText(getResources().getInteger(R.integer
                    .max_comment_length) + " characters left");

            buttonRight.setText(R.string.action_submit);
            buttonLeft.setText(R.string.action_cancel);

            rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    dialog.dismiss();
                }
            });

            rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    if (ratingUser.getRating() > 0) {
                        submitRating(String.valueOf(ratingUser.getRating()), inputComment.getText
                                ().toString());
                        dialog.dismiss();
                    } else {
                        Utils.showErrorSnackBar(ProfileDetailActivity.this,
                                relativeRootRatingDialog, "Please fill appropriate stars!");
                    }
                }
            });

            inputComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    int characters = getResources().getInteger(R.integer.max_comment_length) -
                            charSequence.toString().length();
                    textRemainingCharacters.setText(characters + (characters == 1 ? " character" :
                            " characters" + " left"));
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            dialog.show();

        }
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void profileLayoutVisibility() {
        relativeContactDetails.setVisibility(View.GONE);
        relativeCallHistory.setVisibility(View.VISIBLE);
//        textIconHistory.setTypeface(Utils.typefaceIcons(this));

    }

    private void layoutVisibility() {
        if (profileActivityCallInstance) {
            relativeContactDetails.setVisibility(View.GONE);
            relativeCallHistory.setVisibility(View.VISIBLE);
//            textIconHistory.setTypeface(Utils.typefaceIcons(this));
            rippleCallLog.setVisibility(View.GONE);
            setCallLogHistoryDetails();
        } else {
            relativeContactDetails.setVisibility(View.VISIBLE);
            relativeCallHistory.setVisibility(View.GONE);
            textFullScreenText.setText(contactName);
            if (StringUtils.length(cloudContactName) > 0) {
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                textName.setText(cloudContactName);

            } else {
                if (StringUtils.equalsIgnoreCase(pmId, "-1")) {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorBlack));
                } else {
                    textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color
                            .colorAccent));
                }
                textName.setVisibility(View.GONE);
            }

            imageRightCenter.setImageResource(R.drawable.ic_action_share);
            imageRightCenter.setTag(TAG_IMAGE_SHARE);

            if (displayOwnProfile) {
                textToolbarTitle.setText(getString(R.string.title_my_profile));
                linearCallSms.setVisibility(View.GONE);
                imageRightLeft.setImageResource(R.drawable.ic_action_edit);
                imageRightLeft.setTag(TAG_IMAGE_EDIT);
            } else {
                textToolbarTitle.setText("Profile Detail");
                linearCallSms.setVisibility(View.VISIBLE);
            }

            if (isHideFavourite) {
                rippleActionRightLeft.setEnabled(false);
                if (checkNumberFavourite != null && arrayListFavouriteContacts.contains
                        (checkNumberFavourite)) {
                    imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                } else {
                    imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                }
            }
        }
    }

    private void init() {
        rippleActionBack = ButterKnife.findById(includeToolbar, R.id.ripple_action_back);
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        imageRightLeft = ButterKnife.findById(includeToolbar, R.id.image_right_left);
        imageRightCenter = ButterKnife.findById(includeToolbar, R.id.image_right_center);
        rippleActionRightLeft = ButterKnife.findById(includeToolbar, R.id.ripple_action_right_left);
        rippleActionRightCenter = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_center);
        rippleActionRightRight = ButterKnife.findById(includeToolbar, R.id
                .ripple_action_right_right);

        recyclerViewContactNumber.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmail.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewWebsite.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddress.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSocialContact.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewContactNumber.setNestedScrollingEnabled(false);
        recyclerViewEmail.setNestedScrollingEnabled(false);
        recyclerViewWebsite.setNestedScrollingEnabled(false);
        recyclerViewAddress.setNestedScrollingEnabled(false);
        recyclerViewEvent.setNestedScrollingEnabled(false);
        recyclerViewSocialContact.setNestedScrollingEnabled(false);

        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFullScreenText.setTypeface(Utils.typefaceSemiBold(this));
        textName.setTypeface(Utils.typefaceSemiBold(this));
        textDesignation.setTypeface(Utils.typefaceRegular(this));
        textOrganization.setTypeface(Utils.typefaceRegular(this));
        textViewAllOrganization.setTypeface(Utils.typefaceRegular(this));
        textUserRating.setTypeface(Utils.typefaceRegular(this));
        textFullScreenText.setSelected(true);
        rippleViewMore.setOnRippleCompleteListener(this);
        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRightLeft.setOnRippleCompleteListener(this);
        rippleActionRightCenter.setOnRippleCompleteListener(this);
        rippleActionRightRight.setOnRippleCompleteListener(this);
        rippleCallLog.setOnRippleCompleteListener(this);


        /*buttonCallLog.setClickable(true);
        buttonCallLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        rippleCallLog.setEnabled(true);*/

        LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
        // Filled stars
        Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(this, R.color
                .vivid_yellow));
        // half stars
        Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(this, android.R
                .color.darker_gray));
        // Empty stars
        Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(this, android.R
                .color.darker_gray));

        if (!StringUtils.equalsIgnoreCase(pmId, "-1")) {
            // RC Profile
//            getProfileDetail();
            if (displayOwnProfile) {
                ProfileDataOperation profileDataOperation = (ProfileDataOperation) Utils
                        .getObjectPreference(this, AppConstants.PREF_REGS_USER_OBJECT,
                                ProfileDataOperation.class);
                setUpView(profileDataOperation);
            } else {
//                TableProfileMaster tableProfileMaster = new TableProfileMaster(databaseHandler);
                QueryManager queryManager = new QueryManager(databaseHandler);
                ProfileDataOperation profileDataOperation = queryManager.getRcProfileDetail
                        (this, pmId);
                setUpView(profileDataOperation);
            }
        } else {
            // Non-RC Profile
//            textJoiningDate.setVisibility(View.GONE);
            setUpView(null);
        }

        layoutVisibility();

        initSwipe();

    }

    private void setCallLogHistoryDetails() {
        if (!TextUtils.isEmpty(historyName)) {
            Pattern numberPat = Pattern.compile("\\d+");
            Matcher matcher1 = numberPat.matcher(historyName);
            if (matcher1.find()) {
//                textToolbarTitle.setText("Unknown number");
                textToolbarTitle.setText(historyName);
            } else {
                textToolbarTitle.setText(historyName);
            }
            textFullScreenText.setTypeface(Utils.typefaceBold(this));
            textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
            textFullScreenText.setText(historyName);

        } else {
            if (!TextUtils.isEmpty(historyNumber)) {
                textFullScreenText.setTypeface(Utils.typefaceBold(this));
                textFullScreenText.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
                textFullScreenText.setText(historyNumber);
//                textToolbarTitle.setText("Unknown number");
                textToolbarTitle.setText(historyNumber);

            }

        }
        imageRightCenter.setImageResource(R.drawable.ic_phone);
        imageRightCenter.setTag(TAG_IMAGE_CALL);

    }

    private void setUpView(final ProfileDataOperation profileDetail) {

        profileDataOperationVcard = new ProfileDataOperation();

        profileDataOperationVcard.setPbNameFirst(contactName);

        //<editor-fold desc="Favourite">

        if (!displayOwnProfile && !isHideFavourite) {

            int isFavourite = 0;
            Cursor contactFavouriteCursor = phoneBookContacts.getStarredStatus(phoneBookId);

            if (contactFavouriteCursor != null && contactFavouriteCursor.getCount() > 0) {
                while (contactFavouriteCursor.moveToNext()) {
                    isFavourite = contactFavouriteCursor.getInt(contactFavouriteCursor
                            .getColumnIndex(ContactsContract.Contacts.STARRED));
                }
                contactFavouriteCursor.close();
            }

            if (isFavourite == 0) {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_border);
                imageRightLeft.setTag(TAG_IMAGE_UN_FAVOURITE);
            } else {
                imageRightLeft.setImageResource(R.drawable.ic_action_favorite_fill);
                imageRightLeft.setTag(TAG_IMAGE_FAVOURITE);
            }
        }

        //</editor-fold>

        //<editor-fold desc="Joining Date">
       /* if (profileDetail != null) {
            String joiningDate = StringUtils.defaultString(Utils.convertDateFormat(profileDetail
                    .getJoiningDate(), "yyyy-MM-dd HH:mm:ss", "dd'th' MMM, yyyy"), "-");
            textJoiningDate.setText("Joining Date:- " + joiningDate);
        }*/
        //</editor-fold>

        //<editor-fold desc="User Name">
        /*if (profileDetail != null) {
            textName.setText(profileDetail.getPbNameFirst() + " " + profileDetail.getPbNameLast());
        }*/
        //</editor-fold>

        //<editor-fold desc="Organization Detail">

        // From Cloud
        ArrayList<ProfileDataOperationOrganization> arrayListOrganization = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail
                .getPbOrganization())) {
            arrayListOrganization.addAll(profileDetail.getPbOrganization());
        }

        // From PhoneBook
        Cursor contactOrganizationCursor = phoneBookContacts.getContactOrganization(phoneBookId);
        ArrayList<ProfileDataOperationOrganization> arrayListPhoneBookOrganization = new
                ArrayList<>();
        ArrayList<ProfileDataOperationOrganization> arrayListPhoneBookOrganizationOperation = new
                ArrayList<>();

        if (contactOrganizationCursor != null && contactOrganizationCursor.getCount() > 0) {
            while (contactOrganizationCursor.moveToNext()) {

                ProfileDataOperationOrganization organization = new
                        ProfileDataOperationOrganization();
                ProfileDataOperationOrganization organizationOperation = new
                        ProfileDataOperationOrganization();

                organization.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organization.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organization.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organization.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organization.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organization.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));
                organization.setOrgRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                organizationOperation.setOrgName(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.COMPANY)));
                organizationOperation.setOrgJobTitle(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.TITLE)));
                organizationOperation.setOrgDepartment(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.DEPARTMENT)));
                organizationOperation.setOrgType(phoneBookContacts.getOrganizationType
                        (contactOrganizationCursor,
                                contactOrganizationCursor.getInt((contactOrganizationCursor
                                        .getColumnIndex(ContactsContract.CommonDataKinds
                                                .Organization.TYPE)))));
                organizationOperation.setOrgJobDescription(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.JOB_DESCRIPTION)));
                organizationOperation.setOrgOfficeLocation(contactOrganizationCursor.getString
                        (contactOrganizationCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Organization.OFFICE_LOCATION)));


                if (!arrayListOrganization.contains(organization)) {
                    arrayListPhoneBookOrganization.add(organization);
                }
                arrayListPhoneBookOrganizationOperation.add(organizationOperation);
            }
            contactOrganizationCursor.close();
            profileDataOperationVcard.setPbOrganization(arrayListPhoneBookOrganizationOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListOrganization) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookOrganization)) {

            final ArrayList<ProfileDataOperationOrganization> tempOrganization = new ArrayList<>();
            tempOrganization.addAll(arrayListOrganization);
            tempOrganization.addAll(arrayListPhoneBookOrganization);

            if (tempOrganization.size() == 1) {
                textViewAllOrganization.setVisibility(View.GONE);
            } else {
                textViewAllOrganization.setVisibility(View.VISIBLE);
            }
            textDesignation.setText(tempOrganization.get(0).getOrgJobTitle());
            textOrganization.setText(tempOrganization.get(0).getOrgName());

            textViewAllOrganization.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAllOrganizations(tempOrganization);
                }
            });

        } else {
            linearOrganizationDetail.setVisibility(View.INVISIBLE);
        }
        //</editor-fold>

        //<editor-fold desc="User Rating">
        if (profileDetail != null) {
            textUserRating.setText(profileDetail.getTotalProfileRateUser());
            ratingUser.setRating(Float.parseFloat(profileDetail.getProfileRating()));
        } else {
            textUserRating.setText("0");
            ratingUser.setRating(0);
            ratingUser.setEnabled(false);
        }
        //</editor-fold>

        //<editor-fold desc="Phone Number">

        // From Cloud
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
        ArrayList<String> arrayListCloudNumber = new ArrayList<>();

        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbPhoneNumber
                ())) {
            arrayListPhoneNumber.addAll(profileDetail.getPbPhoneNumber());
            for (int i = 0; i < arrayListPhoneNumber.size(); i++) {
                String number = Utils.getFormattedNumber(this, arrayListPhoneNumber.get(i)
                        .getPhoneNumber());
                arrayListCloudNumber.add(number);
            }
        }

        // From PhoneBook
        Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(phoneBookId);
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumber = new ArrayList<>();
        ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneBookNumberOperation = new
                ArrayList<>();

        if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
            while (contactNumberCursor.moveToNext()) {

                ProfileDataOperationPhoneNumber phoneNumber = new
                        ProfileDataOperationPhoneNumber();
                ProfileDataOperationPhoneNumber phoneNumberOperation = new
                        ProfileDataOperationPhoneNumber();

                phoneNumber.setPhoneNumber(Utils.getFormattedNumber(this, contactNumberCursor
                        .getString(contactNumberCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER))));
                phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                        (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.TYPE))));
                phoneNumberOperation.setPhoneNumber(Utils.getFormattedNumber(this,
                        contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                phoneNumberOperation.setPhoneType(phoneBookContacts.getPhoneNumberType
                        (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.TYPE))));
                phoneNumber.setPbRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                if (!arrayListCloudNumber.contains(phoneNumber.getPhoneNumber())) {
                    arrayListPhoneBookNumber.add(phoneNumber);
                }
                arrayListPhoneBookNumberOperation.add(phoneNumberOperation);
                profileContactNumber = phoneNumber.getPhoneNumber();

            }
            contactNumberCursor.close();
            profileDataOperationVcard.setPbPhoneNumber(arrayListPhoneBookNumberOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListPhoneNumber) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookNumber)) {
            ArrayList<Object> tempPhoneNumber = new ArrayList<>();
            tempPhoneNumber.addAll(arrayListPhoneNumber);
            tempPhoneNumber.addAll(arrayListPhoneBookNumber);

            linearPhone.setVisibility(View.VISIBLE);
            phoneDetailAdapter = new ProfileDetailAdapter(this,
                    tempPhoneNumber, AppConstants.PHONE_NUMBER);
            recyclerViewContactNumber.setAdapter(phoneDetailAdapter);
        } else {
            linearPhone.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Email Id">

        // From Cloud
        ArrayList<ProfileDataOperationEmail> arrayListEmail = new ArrayList<>();
        ArrayList<String> arrayListCloudEmail = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEmailId())) {
            arrayListEmail.addAll(profileDetail.getPbEmailId());
            for (int i = 0; i < arrayListEmail.size(); i++) {
                String email = arrayListEmail.get(i).getEmEmailId();
                arrayListCloudEmail.add(email);
            }
        }

        // From PhoneBook
        Cursor contactEmailCursor = phoneBookContacts.getContactEmail(phoneBookId);
        ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmail = new ArrayList<>();
        ArrayList<ProfileDataOperationEmail> arrayListPhoneBookEmailOperation = new ArrayList<>();

        if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
            while (contactEmailCursor.moveToNext()) {

                ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();
                ProfileDataOperationEmail emailIdOperation = new ProfileDataOperationEmail();

                emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));
                emailId.setEmRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                emailIdOperation.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                emailIdOperation.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                        contactEmailCursor.getInt
                                (contactEmailCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Email.TYPE))));

                if (!arrayListCloudEmail.contains(emailId.getEmEmailId())) {
                    arrayListPhoneBookEmail.add(emailId);
                }
                arrayListPhoneBookEmailOperation.add(emailIdOperation);
            }
            contactEmailCursor.close();
            profileDataOperationVcard.setPbEmailId(arrayListPhoneBookEmailOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEmail) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEmail)) {
            ArrayList<Object> tempEmail = new ArrayList<>();
            tempEmail.addAll(arrayListEmail);
            tempEmail.addAll(arrayListPhoneBookEmail);
            linearEmail.setVisibility(View.VISIBLE);
            ProfileDetailAdapter emailDetailAdapter = new ProfileDetailAdapter(this, tempEmail,
                    AppConstants.EMAIL);
            recyclerViewEmail.setAdapter(emailDetailAdapter);
        } else {
            linearEmail.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Website">

        // From Cloud
        ArrayList<ProfileDataOperationWebAddress> arrayListWebsite = new ArrayList<>();
        ArrayList<String> arrayListCloudWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
            for (int i = 0; i < arrayListWebsite.size(); i++) {
                String website = arrayListWebsite.get(i).getWebAddress();
                arrayListCloudWebsite.add(website);
            }
        }
       /* ArrayList<String> arrayListWebsite = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbWebAddress
                ())) {
            arrayListWebsite.addAll(profileDetail.getPbWebAddress());
        }*/

        // From PhoneBook
        Cursor contactWebsiteCursor = phoneBookContacts.getContactWebsite(phoneBookId);
        ArrayList<ProfileDataOperationWebAddress> arrayListPhoneBookWebsite = new ArrayList<>();
        ArrayList<ProfileDataOperationWebAddress> arrayListPhoneBookWebsiteOperation = new
                ArrayList<>();

        if (contactWebsiteCursor != null && contactWebsiteCursor.getCount() > 0) {
            while (contactWebsiteCursor.moveToNext()) {

                ProfileDataOperationWebAddress webAddress = new ProfileDataOperationWebAddress();
                ProfileDataOperationWebAddress webAddressOperation = new
                        ProfileDataOperationWebAddress();

                webAddress.setWebAddress(contactWebsiteCursor.getString(contactWebsiteCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));
                webAddress.setWebRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                webAddressOperation.setWebAddress(contactWebsiteCursor.getString
                        (contactWebsiteCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Website.URL)));


                if (!arrayListCloudWebsite.contains(webAddress.getWebAddress())) {
                    arrayListPhoneBookWebsite.add(webAddress);
                }
                arrayListPhoneBookWebsiteOperation.add(webAddressOperation);
            }
            contactWebsiteCursor.close();
//            profileDataOperationVcard.setPbWebAddress(arrayListPhoneBookWebsiteOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite)) {
            ArrayList<Object> tempWebsite = new ArrayList<>();
            tempWebsite.addAll(arrayListWebsite);
            tempWebsite.addAll(arrayListPhoneBookWebsite);

            linearWebsite.setVisibility(View.VISIBLE);
            ProfileDetailAdapter websiteDetailAdapter = new ProfileDetailAdapter(this,
                    tempWebsite, AppConstants.WEBSITE);
            recyclerViewWebsite.setAdapter(websiteDetailAdapter);
        } else {
            linearWebsite.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Address">

        // From Cloud
        ArrayList<ProfileDataOperationAddress> arrayListAddress = new ArrayList<>();
        ArrayList<String> arrayListCloudAddress = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbAddress())) {
            arrayListAddress.addAll(profileDetail.getPbAddress());
            for (int i = 0; i < arrayListAddress.size(); i++) {
                String address = arrayListAddress.get(i).getFormattedAddress();
                arrayListCloudAddress.add(address);
            }
        }

        // From PhoneBook
        Cursor contactAddressCursor = phoneBookContacts.getContactAddress(phoneBookId);
        ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddress = new ArrayList<>();
        ArrayList<ProfileDataOperationAddress> arrayListPhoneBookAddressOperation = new
                ArrayList<>();

        if (contactAddressCursor != null && contactAddressCursor.getCount() > 0) {
            while (contactAddressCursor.moveToNext()) {

                ProfileDataOperationAddress address = new ProfileDataOperationAddress();
                ProfileDataOperationAddress addressOperation = new ProfileDataOperationAddress();

                address.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                address.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                address.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                address.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                address.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                address.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                address.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                address.setAddressType(phoneBookContacts.getAddressType(contactAddressCursor,
                        contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));
                address.setRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                addressOperation.setFormattedAddress(contactAddressCursor.getString
                        (contactAddressCursor.getColumnIndex(ContactsContract
                                .CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)));
                addressOperation.setCity(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .CITY)));
                addressOperation.setCountry(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .COUNTRY)));
                addressOperation.setNeighborhood(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .NEIGHBORHOOD)));
                addressOperation.setPostCode(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POSTCODE)));
                addressOperation.setPoBox(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .POBOX)));
                addressOperation.setStreet(contactAddressCursor.getString(contactAddressCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal
                                .STREET)));
                addressOperation.setAddressType(phoneBookContacts.getAddressType
                        (contactAddressCursor,
                                contactAddressCursor.getInt(contactAddressCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.StructuredPostal.TYPE))));

                if (!arrayListCloudAddress.contains(address.getFormattedAddress())) {
                    arrayListPhoneBookAddress.add(address);
                }
                arrayListPhoneBookAddressOperation.add(addressOperation);
            }
            contactAddressCursor.close();
            profileDataOperationVcard.setPbAddress(arrayListPhoneBookAddressOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookAddress)) {
            ArrayList<Object> tempAddress = new ArrayList<>();
            tempAddress.addAll(arrayListAddress);
            tempAddress.addAll(arrayListPhoneBookAddress);
            linearAddress.setVisibility(View.VISIBLE);
            ProfileDetailAdapter addressDetailAdapter = new ProfileDetailAdapter(this,
                    tempAddress, AppConstants.ADDRESS);
            recyclerViewAddress.setAdapter(addressDetailAdapter);
        } else {
            linearAddress.setVisibility(View.GONE);
        }
        //</editor-fold>

        // <editor-fold desc="Im Account">

        // From Cloud
        ArrayList<ProfileDataOperationImAccount> arrayListImAccount = new ArrayList<>();
        ArrayList<String> arrayListCloudImAccount = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbIMAccounts
                ())) {
            arrayListImAccount.addAll(profileDetail.getPbIMAccounts());
            for (int i = 0; i < arrayListImAccount.size(); i++) {
                String imAccount = arrayListImAccount.get(i).getIMAccountProtocol();
                arrayListCloudImAccount.add(imAccount);
            }
        }

        // From PhoneBook
        Cursor contactImAccountCursor = phoneBookContacts.getContactIm(phoneBookId);
        ArrayList<ProfileDataOperationImAccount> arrayListPhoneBookImAccount = new ArrayList<>();

        if (contactImAccountCursor != null && contactImAccountCursor.getCount() > 0) {
            while (contactImAccountCursor.moveToNext()) {

                ProfileDataOperationImAccount imAccount = new ProfileDataOperationImAccount();

                imAccount.setIMAccountDetails(contactImAccountCursor.getString
                        (contactImAccountCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA1)));

                imAccount.setIMAccountType(phoneBookContacts.getImAccountType
                        (contactImAccountCursor,
                                contactImAccountCursor.getInt(contactImAccountCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Im.TYPE))));

                imAccount.setIMAccountProtocol(phoneBookContacts.getImProtocol
                        (contactImAccountCursor.getInt((contactImAccountCursor.getColumnIndex
                                (ContactsContract.CommonDataKinds.Im.PROTOCOL)))));

                imAccount.setIMRcpType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                if (!arrayListCloudImAccount.contains(imAccount.getIMAccountProtocol())) {
                    arrayListPhoneBookImAccount.add(imAccount);
                }

            }
            contactImAccountCursor.close();
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookImAccount)) {
            ArrayList<Object> tempImAccount = new ArrayList<>();
            tempImAccount.addAll(arrayListImAccount);
            tempImAccount.addAll(arrayListPhoneBookImAccount);
            linearSocialContact.setVisibility(View.VISIBLE);
            ProfileDetailAdapter imAccountDetailAdapter = new ProfileDetailAdapter(this,
                    tempImAccount, AppConstants.IM_ACCOUNT);
            recyclerViewSocialContact.setAdapter(imAccountDetailAdapter);
        } else {
            linearSocialContact.setVisibility(View.GONE);
        }
        //</editor-fold>

        if ((!Utils.isArraylistNullOrEmpty(arrayListWebsite) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookWebsite))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListAddress) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookAddress))
                ||
                (!Utils.isArraylistNullOrEmpty(arrayListImAccount) || !Utils
                        .isArraylistNullOrEmpty(arrayListPhoneBookImAccount))
                ) {
            rippleViewMore.setVisibility(View.VISIBLE);
        } else {
            rippleViewMore.setVisibility(View.GONE);
        }

        // <editor-fold desc="Event">

        // From Cloud
        ArrayList<ProfileDataOperationEvent> arrayListEvent = new ArrayList<>();
        if (profileDetail != null && !Utils.isArraylistNullOrEmpty(profileDetail.getPbEvent())) {
            arrayListEvent.addAll(profileDetail.getPbEvent());
        }

        // From PhoneBook
        Cursor contactEventCursor = phoneBookContacts.getContactEvent(phoneBookId);
        ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEvent = new ArrayList<>();
        ArrayList<ProfileDataOperationEvent> arrayListPhoneBookEventOperation = new ArrayList<>();

        if (contactEventCursor != null && contactEventCursor.getCount() > 0) {
            while (contactEventCursor.moveToNext()) {

                ProfileDataOperationEvent event = new ProfileDataOperationEvent();
                ProfileDataOperationEvent eventOperation = new ProfileDataOperationEvent();

                event.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                event.setEventDate(contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE)));

                event.setEventRcType(String.valueOf(getResources().getInteger(R.integer
                        .rcp_type_local_phone_book)));

                eventOperation.setEventType(phoneBookContacts.getEventType(contactEventCursor,
                        contactEventCursor
                                .getInt(contactEventCursor.getColumnIndex(ContactsContract
                                        .CommonDataKinds.Event.TYPE))));

                eventOperation.setEventDate(contactEventCursor.getString(contactEventCursor
                        .getColumnIndex(ContactsContract.CommonDataKinds.Event
                                .START_DATE)));

                if (!arrayListEvent.contains(event)) {
                    arrayListPhoneBookEvent.add(event);
                }
                arrayListPhoneBookEventOperation.add(eventOperation);
            }
            contactEventCursor.close();
            profileDataOperationVcard.setPbEvent(arrayListPhoneBookEventOperation);
        }

        if (!Utils.isArraylistNullOrEmpty(arrayListEvent) || !Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEvent)) {
            ArrayList<Object> tempEvent = new ArrayList<>();
            tempEvent.addAll(arrayListEvent);
            tempEvent.addAll(arrayListPhoneBookEvent);
            linearEvent.setVisibility(View.VISIBLE);
            ProfileDetailAdapter eventDetailAdapter = new ProfileDetailAdapter(this, tempEvent,
                    AppConstants.EVENT);
            recyclerViewEvent.setAdapter(eventDetailAdapter);
        } else {
            linearEvent.setVisibility(View.GONE);
        }
        //</editor-fold>

        linearGender.setVisibility(View.GONE);

        if (Utils.isArraylistNullOrEmpty(arrayListEvent) && Utils.isArraylistNullOrEmpty
                (arrayListPhoneBookEvent)
//                && Utils.isArraylistNullOrEmpty(arrayListAddress)
                ) {
            cardOtherDetails.setVisibility(View.GONE);
        } else {
            cardOtherDetails.setVisibility(View.VISIBLE);
        }

    }


    //    private void showAllOrganizations(ProfileDataOperation profileDetail) {
    private void showAllOrganizations(ArrayList<ProfileDataOperationOrganization>
                                              arrayListOrganization) {
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
        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);

        rippleLeft.setVisibility(View.GONE);

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

        /*OrganizationListAdapter adapter = new OrganizationListAdapter(this, profileDetail
                .getPbOrganization());*/
        OrganizationListAdapter adapter = new OrganizationListAdapter(this, arrayListOrganization);
        recyclerViewDialogList.setAdapter(adapter);

        dialog.show();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String actionNumber = StringUtils.defaultString(((ProfileDetailAdapter
                        .ProfileDetailViewHolder) viewHolder).textMain.getText()
                        .toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                  /*  smsIntent.setData(Uri.parse("sms:" + ((ProfileData)
                            arrayListPhoneBookContacts.get(position)).getOperation().get(0)
                            .getPbPhoneNumber().get(0).getPhoneNumber()));*/
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    showCallConfirmationDialog(actionNumber);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (phoneDetailAdapter != null) {
                            phoneDetailAdapter.notifyDataSetChanged();
                        }
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                Paint p = new Paint();
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(ContextCompat.getColor(ProfileDetailActivity.this, R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(ProfileDetailActivity.this, R.color
                                .brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable
                                .ic_action_sms);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width, (float) itemView.getRight() -
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState,
                        isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewContactNumber);
    }

    private void showCallConfirmationDialog(final String number) {

        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        callConfirmationDialog.dismissDialog();
                        break;

                    case R.id.rippleRight:
                        callConfirmationDialog.dismissDialog();
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                number));
                        startActivity(intent);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");

        callConfirmationDialog.showDialog();

    }

    public RelativeLayout getRelativeRootProfileDetail() {
        return relativeRootProfileDetail;
    }

    public int getListClickedPosition() {
        return listClickedPosition;
    }

    private void showChooseShareOption(final String firstName, final String lastName) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_share_invite);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        TextView textFromContact = (TextView) dialog.findViewById(R.id.text_from_contact);
        TextView textFromSocialMedia = (TextView) dialog.findViewById(R.id.text_from_social_media);

        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFromContact.setTypeface(Utils.typefaceRegular(this));
        textFromSocialMedia.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText("Share " + contactName + "'s Profile");

        buttonLeft.setText(R.string.action_cancel);

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        textFromSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                    // RCP profile or Own Profile
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = WsConstants.WS_PROFILE_VIEW_ROOT + firstName
                            + "." + lastName + "." + pmId;
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share Contact Via"));
                } else {
                    // Non-Rcp profile
                    shareContact();

                }
            }
        });

        textFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                if (!StringUtils.equalsAnyIgnoreCase(pmId, "-1")) {
                    bundle.putString(AppConstants.EXTRA_PM_ID, pmId);
                } else {
                    bundle.putSerializable(AppConstants.EXTRA_OBJECT_CONTACT,
                            profileDataOperationVcard);
                }
                startActivityIntent(ProfileDetailActivity.this, ContactListingActivity.class,
                        bundle);
            }
        });

        dialog.show();
    }

    private void fetchCallLogHistoryDateWise(String value) {
        ArrayList<CallLogType> tempList = new ArrayList<>();
        arrayListHistory = new ArrayList<>();
        if (!TextUtils.isEmpty(value)) {
            tempList = callLogHistory(value);
            Log.i("History size  ", tempList.size() + "" + " of  " + value);
        }
        for (int i = 0; i < tempList.size(); i++) {
            CallLogType callLogTypeHistory = tempList.get(i);
            long date = callLogTypeHistory.getHistoryDate();
            Date objDate1 = new Date(date);
            String arrayDate = new SimpleDateFormat("yyyy-MM-dd").format(objDate1);
            String intentDate = "";
            if (historyDate > 0) {
                Date intentDate1 = new Date(historyDate);
                intentDate = new SimpleDateFormat("yyyy-MM-dd").format(intentDate1);
            } else {
                intentDate = new SimpleDateFormat("yyyy-MM-dd").format(CallLogFragment
                        .callLogTypeReceiver.getCallReceiverDate());
            }
            if (intentDate.equalsIgnoreCase(arrayDate)) {
                arrayListHistory.add(callLogTypeHistory);
            }

        }
        setHistoryAdapter();
    }

    private void fetchAllCallLogHistory(String value) {
        if (!TextUtils.isEmpty(value)) {
            arrayListHistory = callLogHistory(value);
            Log.i("History size  ", arrayListHistory.size() + "" + " of  " + value);
        }
        setHistoryAdapter();
    }

    private void setHistoryAdapter() {
//        if (callHistoryListAdapter == null) {
        if (arrayListHistory != null && arrayListHistory.size() > 0) {
            textNoHistoryToShow.setVisibility(View.GONE);
            recyclerCallHistory.setVisibility(View.VISIBLE);
            callHistoryListAdapter = new CallHistoryListAdapter(arrayListHistory);
            recyclerCallHistory.setAdapter(callHistoryListAdapter);
            recyclerCallHistory.setFocusable(false);
            setRecyclerViewLayoutManager(recyclerCallHistory);
        } else {
            recyclerCallHistory.setVisibility(View.GONE);
            textNoHistoryToShow.setVisibility(View.VISIBLE);
            textNoHistoryToShow.setText(getResources().getString(R.string.text_no_history));
        }
//        } else {
//            callHistoryListAdapter.notifyDataSetChanged();
//        }


    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;
        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByNumber(String number) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog
                    .Calls.NUMBER + " =?", new String[]{number}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    @TargetApi(Build.VERSION_CODES.M)
    private Cursor getCallHistoryDataByName(String name) {
        Cursor cursor = null;
        String order = CallLog.Calls.DATE + " DESC";
        try {
            cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog
                    .Calls.CACHED_NAME + " =?", new String[]{name}, order);

        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return cursor;
    }


    private ArrayList callLogHistory(String number) {
        ArrayList<CallLogType> callDetails = new ArrayList<>();
        Cursor cursor = null;
        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(number);
        if (matcher1.find()) {
            cursor = getCallHistoryDataByNumber(number);
        } else {
            cursor = getCallHistoryDataByName(number);
        }

        try {
            if (cursor != null && cursor.getCount() > 0) {
                int number1 = cursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = cursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
                int callLogId = cursor.getColumnIndex(CallLog.Calls._ID);
                int numberType = cursor.getColumnIndex(CallLog.Calls.CACHED_NUMBER_TYPE);
                int account = -1;
                int account_id = -1;
                int profileImage = -1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    account = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME);
                    //for versions above lollipop
                    account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                    profileImage = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
                } else {
//                        account_id = cursor.getColumnIndex(CallLog.Calls.PHONE_ACCOUNT_ID);
                }

                while (cursor.moveToNext()) {
                    String phNum = cursor.getString(number1);
                    int callType = Integer.parseInt(cursor.getString(type));
                    String callDate = cursor.getString(date);
                    long dateOfCall = Long.parseLong(callDate);
                    String callDuration = cursor.getString(duration);
                    String numberTypeLog = getPhoneNumberType(cursor.getInt(numberType));
                    String accountId = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        accountId = cursor.getString(account_id);
                        if (!TextUtils.isEmpty(accountId) && account_id > 0)
                            Log.e("Sim Type", accountId);
                        String accountName = cursor.getString(account);
                        if (!TextUtils.isEmpty(accountName))
                            Log.e("Sim Name", accountName);
//                        String userImage = cursor.getString(profileImage);
//                        if (userImage != null)
//                            Log.e("User Image", userImage);
                    }
                    int histroyId = Integer.parseInt(cursor.getString(callLogId));
                    CallLogType logObject = new CallLogType();
                    logObject.setHistoryNumber(phNum);
                    logObject.setHistoryType(callType);
                    logObject.setHistoryDate(dateOfCall);
                    logObject.setHistoryDuration(Integer.parseInt(callDuration));
                    logObject.setHistoryCallSimNumber(accountId);
                    logObject.setHistoryId(histroyId);
                    logObject.setHistoryNumberType(numberTypeLog);
                    callDetails.add(logObject);
                }
            }
            cursor.close();


        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return callDetails;
    }

    public String getPhoneNumberType(int type) {
        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return "Fax Work";

            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                return "Fax Home";

            case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                return "Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                return "Other";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                return "Callback";

            case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                return "Car";

            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                return "Company Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                return "ISDN";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                return "Main";

            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX:
                return "Other Fax";

            case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                return "Radio";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                return "Telex";

            case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                return "Tty Tdd";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                return "Work Mobile";

            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                return "Work Pager";

            case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                return "Assistant";

            case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                return "MMS";

        }
        return "Other";
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void getProfileDetail() {

        WsRequestObject profileDetailObject = new WsRequestObject();
        profileDetailObject.setPmId(pmId);

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

    private void setFavouriteStatus(ArrayList<ProfileData> favourites) {

        WsRequestObject favouriteStatusObject = new WsRequestObject();
        favouriteStatusObject.setPmId(getUserPmId());
        favouriteStatusObject.setFavourites(favourites);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    favouriteStatusObject, null, WsResponseObject.class, WsConstants
                    .REQ_MARK_AS_FAVOURITE, null, true).execute(WsConstants.WS_ROOT + WsConstants
                    .REQ_MARK_AS_FAVOURITE);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void submitRating(String ratingStar, String comment) {

        WsRequestObject ratingObject = new WsRequestObject();
        ratingObject.setPmId(getUserPmId());
        ratingObject.setPrComment(comment);
        ratingObject.setPrRatingStars(ratingStar);
        ratingObject.setPrStatus(String.valueOf(getResources().getInteger(R.integer.rating_done)));
        ratingObject.setPrToPmId(pmId);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    ratingObject, null, WsResponseObject.class, WsConstants.REQ_PROFILE_RATING,
                    null, true).execute(WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_RATING);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources().getString(R
                    .string.msg_no_network));
        }
    }

    private void shareContact() {

        WsRequestObject uploadContactObject = new WsRequestObject();
        uploadContactObject.setPmId(getUserPmId());
        uploadContactObject.setSendProfileType(getResources().getInteger(R.integer
                .send_profile_non_rcp_social));
        uploadContactObject.setContactData(profileDataOperationVcard);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    uploadContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_RCP_PROFILE_SHARING, getResources().getString(R.string.msg_please_wait),
                    true).execute(WsConstants.WS_ROOT + WsConstants.REQ_RCP_PROFILE_SHARING);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    private void profileVisit(ArrayList<ProfileVisit> arrayListProfileVisit) {

        WsRequestObject profileVisitObject = new WsRequestObject();
        profileVisitObject.setArrayListProfileVisit(arrayListProfileVisit);

        if (Utils.isNetworkAvailable(this)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    profileVisitObject, null, WsResponseObject.class, WsConstants
                    .REQ_ADD_PROFILE_VISIT, null, true).execute(WsConstants.WS_ROOT + WsConstants
                    .REQ_ADD_PROFILE_VISIT);
        } else {
            Utils.showErrorSnackBar(this, relativeRootProfileDetail, getResources()
                    .getString(R.string.msg_no_network));
        }
    }

    //</editor-fold>

}
