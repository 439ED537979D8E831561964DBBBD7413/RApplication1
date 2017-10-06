package com.rawalinfocom.rcontact.adapters;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.PublicProfileDetailActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.PrivacySettingPopupDialog;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.PrivacyDataItem;
import com.rawalinfocom.rcontact.model.PrivacyEntityItem;
import com.rawalinfocom.rcontact.model.ProfileDataOperationAddress;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEvent;
import com.rawalinfocom.rcontact.model.ProfileDataOperationImAccount;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileDataOperationWebAddress;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 22/12/16.
 */

public class PublicProfileDetailAdapter extends RecyclerView.Adapter<PublicProfileDetailAdapter
        .ProfileDetailViewHolder> {

    private Activity activity;
    private ArrayList<Object> arrayList;
    private int profileDetailType;

    private int colorBlack, colorPineGreen;
    private PrivacySettingPopupDialog.DialogCallback listner;
    //    private boolean isOwnProfile = false;
    private String pmId;

    boolean showNumber;

    public PublicProfileDetailAdapter(Activity activity, ArrayList<Object> arrayList, int
            profileDetailType, String pmId) {
        this.activity = activity;
        this.profileDetailType = profileDetailType;
        this.arrayList = arrayList;
//        this.isOwnProfile = isOwnProfile;
        colorBlack = ContextCompat.getColor(activity, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(activity, R.color.colorAccent);
        this.pmId = pmId;

    }

    @Override
    public ProfileDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_profile_detail,
                parent, false);
        return new ProfileDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ProfileDetailViewHolder holder, int position) {

        switch (profileDetailType) {
            case AppConstants.PHONE_NUMBER:
                displayPhoneNumber(holder, position);
                break;

            case AppConstants.EMAIL:
                displayEmail(holder, position);
                break;

            case AppConstants.WEBSITE:
                displayWebsite(holder, position);
                break;

            case AppConstants.ADDRESS:
                displayAddress(holder, position);
                break;

            case AppConstants.IM_ACCOUNT:
                displayImAccount(holder, position);
                break;

            case AppConstants.EVENT:
                displayEvent(holder, position);
                break;

            case AppConstants.GENDER:
                displayGender(holder, position);
                break;
        }
    }

    private void displayPhoneNumber(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationPhoneNumber phoneNumber = (ProfileDataOperationPhoneNumber)
                arrayList.get(position);
        final String number = phoneNumber.getPhoneNumber();
        holder.textSub.setText(phoneNumber.getPhoneType());
        holder.textSub.setVisibility(View.VISIBLE);

        if (Utils.isAppInstalled(activity, "com.whatsapp")) {
            holder.imgActionWhatsapp.setVisibility(View.VISIBLE);
        }

        holder.imgActionType.setImageResource(R.drawable.ico_phone_alt_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (!number.contains("xx") && !number.contains("XX")) {

                    if (ContextCompat.checkSelfPermission(activity, android.Manifest
                            .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        activity.requestPermissions(new String[]{Manifest.permission
                                .CALL_PHONE}, AppConstants
                                .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                        if (activity instanceof ProfileDetailActivity) {
                            ((ProfileDetailActivity) activity).callNumber = number;
                        }
                    } else {
                        if (!number.contains("xx") && !number.contains("XX"))
                            Utils.callIntent(activity, number);
                    }
                }
            }
        });

        holder.imgActionWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:" + number);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
                sendIntent.setPackage("com.whatsapp");
                activity.startActivity(sendIntent);
               /* Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp" +
                        ".Conversation"));
                sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(StringUtils.substring
                        (number, 1)) + "@s" +
                        ".whatsapp.net");//phone number without "+" prefix
                activity.startActivity(sendIntent);*/
            }
        });

        holder.textMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!number.contains("xx") && !number.contains("XX")) {
                    if (ContextCompat.checkSelfPermission(activity, android.Manifest
                            .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        activity.requestPermissions(new String[]{Manifest.permission
                                .CALL_PHONE}, AppConstants
                                .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                        if (activity instanceof ProfileDetailActivity) {
                            ((ProfileDetailActivity) activity).callNumber = number;
                        }
                    } else {
                        if (!number.contains("xx") && !number.contains("XX"))
                            Utils.callIntent(activity, number);
                    }
                }
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_number), (
                        (TextView) view).getText().toString());
                if (activity instanceof ProfileDetailActivity) {
                    Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                            .getRelativeRootProfileDetail(), activity.getString(R.string
                            .str_copy_number_clip_board));
                }
                return true;
            }
        });

        int pbRcpType = phoneNumber.getPbRcpType();
        if (pbRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
            holder.textMain.setText(number/*Utils.setMultipleTypeface(activity, + " " + activity
                            .getString(R.string.im_icon_verify), 0,
                    (StringUtils.length(number) + 1), (
                            (StringUtils.length(number) + 1) + 1))*/);
            holder.textMain.setTextColor(colorPineGreen);
            holder.buttonRequest.setVisibility(View.VISIBLE);
            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_phone_number", phoneNumber.getPhoneId());
                }
            });
        } else if (pbRcpType == IntegerConstants.RCP_TYPE_SECONDARY) {
            holder.textMain.setText(number);
            holder.textMain.setTextColor(colorPineGreen);
            holder.buttonRequest.setVisibility(View.GONE);

        } else {
            holder.textMain.setText(number);
            holder.textMain.setTextColor(colorBlack);
        }
//        if (activity instanceof PublicProfileDetailActivity) {
        if (pbRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
            String newNumber = "+" + holder.textMain.getText();
//            holder.textMain.setText(Utils.setMultipleTypeface(activity, newNumber + " " + activity.getString(R.string.im_icon_verify), 0, (StringUtils.length(newNumber) + 1), ((StringUtils.length(newNumber) + 1) + 1)));
            holder.textMain.setText(newNumber);
        } else {
            holder.textMain.setText("+" + holder.textMain.getText());
        }

        if (showNumber == false) {
            if (pbRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
                holder.textMain.setText(StringUtils.replacePattern(holder.textMain.getText().toString(), "[0-9]", "X"));
                holder.textMain.setText(Utils.setMultipleTypeface(activity, holder.textMain.getText() + " " + activity
                                .getString(R.string.im_icon_verify), 0,
                        (StringUtils.length(holder.textMain.getText()) + 1), ((StringUtils.length(holder.textMain.getText()) + 1) + 1)));
                holder.buttonRequest.setVisibility(View.VISIBLE);
            }
        }else{
            if (pbRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
                String numberToShow =  "+" + phoneNumber.getOriginalNumber();
                holder.textMain.setText(Utils.setMultipleTypeface(activity, numberToShow + " " + activity
                                .getString(R.string.im_icon_verify), 0,
                        (StringUtils.length(numberToShow) + 1), ((StringUtils.length(numberToShow) + 1) + 1)));

                holder.buttonRequest.setVisibility(View.GONE);
            }
        }

        if (StringUtils.contains(holder.textMain.getText().toString(), "X") ||
                StringUtils.contains(holder.textMain.getText().toString(), "x")) {
            holder.imgActionType.setVisibility(View.GONE);
            holder.imgActionWhatsapp.setVisibility(View.GONE);
        }

        holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pmTo = Integer.parseInt(pmId);
                sendAccessRequest(pmTo, "pb_phone_number", phoneNumber.getPhoneId());
            }
        });
    }

    private void displayEmail(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationEmail email = (ProfileDataOperationEmail) arrayList.get(position);
        String emailId = email.getEmEmailId();
        holder.textMain.setText(emailId);
        holder.textSub.setText(email.getEmType());
        holder.textSub.setVisibility(View.VISIBLE);

        holder.imgActionType.setImageResource(R.drawable.ico_envelop_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = holder.textMain.getText().toString();
                if (!email.startsWith("XX") && !email.startsWith("xx")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" +
                            email));
                    activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R
                            .string.str_send_email)));
                }
            }
        });

        holder.textMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = holder.textMain.getText().toString();
                if (!email.startsWith("XX") && !email.startsWith("xx")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" +
                            email));
                    activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R
                            .string.str_send_email)));
                }
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_email), (
                        (TextView) view).getText()
                        .toString());
                if (activity instanceof ProfileDetailActivity) {
                    Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                            .getRelativeRootProfileDetail(), activity.getString(R.string
                            .str_copy_email_clip_board));
                } else {
                }

                return true;
            }
        });

        int emRcpType = email.getEmRcpType();
        if (emRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
            holder.textMain.setText(Utils.setMultipleTypeface(activity, emailId + " " + activity
                            .getString(R.string.im_icon_verify), 0,
                    (StringUtils.length(emailId) + 1), (
                            (StringUtils.length(emailId) + 1) + 1)));
            holder.textMain.setTextColor(colorPineGreen);

        } else {
            holder.buttonRequest.setVisibility(View.GONE);
            /*if ((MoreObjects.firstNonNull(email.getEmIsPrivate(), 0)) == IntegerConstants
                    .IS_PRIVATE) {
//                    holder.imageView2.setVisibility(View.GONE);
                holder.buttonRequest.setVisibility(View.GONE);
            }*/

            /*holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_email_id", email.getEmId());
                }
            });*/
        }

        if ((MoreObjects.firstNonNull(email.getEmIsPrivate(), 0)) == IntegerConstants
                .IS_PRIVATE) {
            holder.imgActionType.setVisibility(View.GONE);
        }
//        else {
//
//            holder.textMain.setText(emailId);
//
//            if (isOwnProfile) {
//                holder.llPrivacy.setVisibility(View.VISIBLE);
//                holder.textMain.setTextColor(colorPineGreen);
//            } else {
//                holder.llPrivacy.setVisibility(View.GONE);
//                holder.textMain.setTextColor(colorBlack);
//            }
//
//            holder.llPrivacy.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PrivacySettingPopupDialog privacySettingPopupDialog = new
//                            PrivacySettingPopupDialog(holder, activity, listner, AppConstants
//                            .EMAIL,
//                            position, email.getEmPublic(), email.getEmId());
//                    privacySettingPopupDialog.setDialogTitle(activity.getResources().getString(R
//                            .string.privacy_dialog_title));
//                    privacySettingPopupDialog.showDialog();
//                }
//            });
//        }
    }

    private void displayWebsite(final ProfileDetailViewHolder holder, final int position) {
//        String website = (String) arrayList.get(position);
        ProfileDataOperationWebAddress webAddress = (ProfileDataOperationWebAddress) arrayList
                .get(position);

        holder.textSub.setVisibility(View.GONE);

        holder.textMain.setText(webAddress.getWebAddress());

        int rcpType = Integer.parseInt(StringUtils.defaultIfEmpty(webAddress.getWebRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));
        if (rcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
        }
    }

    private void displayAddress(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationAddress address = (ProfileDataOperationAddress) arrayList.get
                (position);
        holder.textMain.setText(address.getFormattedAddress());
        holder.textSub.setText(address.getAddressType());
        holder.textSub.setVisibility(View.VISIBLE);

        int addressRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(address.getRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));
        if (addressRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);

            holder.buttonRequest.setVisibility(View.GONE);

           /* if ((MoreObjects.firstNonNull(address.getIsPrivate(), 0)) == IntegerConstants
                    .IS_PRIVATE) {
                holder.buttonRequest.setVisibility(View.VISIBLE);
            }*/

            /*holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_address", address.getAddId());
                }
            });*/

        }
        if ((MoreObjects.firstNonNull(address.getIsPrivate(), 0)) == IntegerConstants
                .IS_PRIVATE) {
            holder.imgActionType.setVisibility(View.GONE);
        }
    }

    private void displayImAccount(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount) arrayList
                .get(position);

        if (!imAccount.getIMAccountFirstName().equalsIgnoreCase(""))
            holder.textMain.setText(imAccount.getIMAccountFirstName() + " " + imAccount.getIMAccountLastName());
        else
            holder.textMain.setText(imAccount.getIMAccountDetails());

        holder.textSub.setText(imAccount.getIMAccountProtocol());


        int imRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(imAccount.getIMRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));

        if (imRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
            final ProfileDetailViewHolder viewHodler = holder;
            holder.buttonRequest.setVisibility(View.GONE);

            /*if ((MoreObjects.firstNonNull(imAccount.getIMAccountIsPrivate(), 0)) ==
                    IntegerConstants.IS_PRIVATE) {
                holder.buttonRequest.setVisibility(View.VISIBLE);
            }


            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_im_accounts", imAccount.getIMId());
                }
            });*/
        }
        if ((MoreObjects.firstNonNull(imAccount.getIMAccountIsPrivate(), 0)) ==
                IntegerConstants.IS_PRIVATE) {
            holder.imgActionType.setVisibility(View.VISIBLE);
        }

    }

    private void displayEvent(ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationEvent event = (ProfileDataOperationEvent) arrayList
                .get(position);

        String convertedDate;
        if (StringUtils.startsWith(event.getEventDateTime(), "XXX")) {
            convertedDate = event.getEventDateTime();
        } else {

            if (MoreObjects.firstNonNull(event.getIsYearHidden(), 0) == IntegerConstants
                    .IS_YEAR_HIDDEN) {


                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "dd-MM",
                        getEventDateFormat(event.getEventDateTime()));

            } else {
                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "yyyy-MM-dd",
                        getEventDateFormat(event.getEventDateTime()));

            }
        }

        if (MoreObjects.firstNonNull(event.getIsYearHidden(), 0) == IntegerConstants
                .IS_YEAR_HIDDEN) {
            convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "dd-MM",
                    getEventDateFormat(event.getEventDateTime()));

        }


        if (MoreObjects.firstNonNull(event.getIsPrivate(), 0) == IntegerConstants.IS_PRIVATE) {
            convertedDate = event.getEventDateTime();
        }

        holder.textMain.setText(convertedDate);
        holder.textSub.setText(event.getEventType());
        holder.textSub.setVisibility(View.VISIBLE);

        int eventRcType = Integer.parseInt(StringUtils.defaultIfEmpty(event.getEventRcType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));

        if (eventRcType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {

            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);

            holder.buttonRequest.setVisibility(View.GONE);

            /*if (event.getIsPrivate() != null) {
                if (event.getIsPrivate() == IntegerConstants.IS_PRIVATE) {
                    holder.buttonRequest.setVisibility(View.VISIBLE);
                }
            }


            final ProfileDetailViewHolder viewHodler = holder;

            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pmTo = Integer.parseInt(pmId);
                    sendAccessRequest(pmTo, "pb_event", event.getEventId());
                }
            });*/
        }

        if (event.getIsPrivate() != null) {
            if (event.getIsPrivate() == IntegerConstants.IS_PRIVATE) {
                holder.imgActionType.setVisibility(View.VISIBLE);
            }
        }

    }

    private void displayGender(ProfileDetailViewHolder holder, final int position) {
        String gender = (String) arrayList.get(position);
        holder.textMain.setText(gender);
        holder.textSub.setVisibility(View.GONE);
        holder.textMain.setTextColor(colorBlack);


    }

    private String getEventDateFormat(String date) {

        date = StringUtils.substring(date, 8, 10);
        if (!StringUtils.isNumeric(date)) {
            date = StringUtils.substring(date, 0, 1);
        }

        String format;
        if (date.endsWith("1") && !date.endsWith("11"))
//            format = "d'st' MMMM, yyyy";
            format = AppConstants.EVENT_ST_DATE_FORMAT;
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = AppConstants.EVENT_ND_DATE_FORMAT;
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = AppConstants.EVENT_RD_DATE_FORMAT;
        else
            format = AppConstants.EVENT_GENERAL_DATE_FORMAT;
        return format;
    }

    public ArrayList<Object> getDetailList() {
        return arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void sendAccessRequest(int toPMId, String carFiledType, String recordIndexId) {

        WsRequestObject requestObj = new WsRequestObject();
        requestObj.setCarPmIdTo(toPMId);
        requestObj.setCarFiledType(carFiledType);
        requestObj.setCarStatus(0);
        requestObj.setCarMongoDbRecordIndex(recordIndexId);

        if (Utils.isNetworkAvailable(activity)) {
            new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    requestObj, null, WsResponseObject.class, WsConstants
                    .REQ_PROFILE_PRIVACY_REQUEST, activity.getResources().getString(R.string
                    .msg_please_wait), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT + WsConstants.REQ_PROFILE_PRIVACY_REQUEST);
        } else {
            //show no net
            Toast.makeText(activity, activity.getResources().getString(R.string.msg_no_network),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class ProfileDetailViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_action_type)
        ImageView imgActionType;
        @BindView(R.id.img_action_whatsapp)
        ImageView imgActionWhatsapp;
        @BindView(R.id.text_main)
        public TextView textMain;
        @BindView(R.id.text_tic)
        public TextView textTic;
        @BindView(R.id.text_sub)
        TextView textSub;
        @BindView(R.id.button_privacy)
        ImageView buttonPrivacy;
        @BindView(R.id.button_request)
        AppCompatButton buttonRequest;
        @BindView(R.id.ll_profile_data)
        LinearLayout llProfileData;
        @BindView(R.id.ll_privacy)
        LinearLayout llPrivacy;

        ProfileDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain.setTypeface(Utils.typefaceRegular(activity));
            textSub.setTypeface(Utils.typefaceRegular(activity));
            imgActionWhatsapp.setVisibility(View.GONE);
            textTic.setVisibility(View.GONE);
            imgActionType.setVisibility(View.VISIBLE);
            buttonPrivacy.setVisibility(View.GONE);
        }
    }

    public void setShowNumber(boolean showNumber) {
        this.showNumber = showNumber;
    }
}
