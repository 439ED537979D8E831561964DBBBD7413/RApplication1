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

public class ProfileDetailAdapter extends RecyclerView.Adapter<ProfileDetailAdapter
        .ProfileDetailViewHolder> implements PrivacySettingPopupDialog.DialogCallback {


    private final String EVENT_GENERAL_DATE_FORMAT = "dd'th' MMMM, yyyy";
    private final String EVENT_ST_DATE_FORMAT = "dd'st' MMMM, yyyy";
    private final String EVENT_ND_DATE_FORMAT = "dd'nd' MMMM, yyyy";
    private final String EVENT_RD_DATE_FORMAT = "dd'rd' MMMM, yyyy";

    private Activity activity;
    private ArrayList<Object> arrayList;
    private int profileDetailType;

    private int colorBlack, colorPineGreen;
    private PrivacySettingPopupDialog.DialogCallback listner;
    private boolean isOwnProfile = false;
    private String pmId;

    public ProfileDetailAdapter(Activity activity, ArrayList<Object> arrayList, int
            profileDetailType, boolean isOwnProfile, String pmId) {
        this.activity = activity;
        this.profileDetailType = profileDetailType;
        this.arrayList = arrayList;
        this.isOwnProfile = isOwnProfile;
        colorBlack = ContextCompat.getColor(activity, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(activity, R.color.colorAccent);
        this.pmId = pmId;
        listner = this;

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
        holder.llPrivacy.setVisibility(View.GONE);
//        if (isOwnProfile) {
//            holder.viewOtherProfile.setVisibility(View.GONE);
//        } else {
//            holder.viewOwnProfile.setVisibility(View.GONE);
//        }
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

                if (!number.startsWith("+xx") && !number.startsWith("+XX")) {

                    if (ContextCompat.checkSelfPermission(activity, android.Manifest
                            .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        activity.requestPermissions(new String[]{Manifest.permission
                                .CALL_PHONE}, AppConstants
                                .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                        if (activity instanceof ProfileDetailActivity) {
                            ((ProfileDetailActivity) activity).callNumber = number;
                        }
                    } else {
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
                if (!number.startsWith("+xx") && !number.startsWith("+XX")) {
                    if (ContextCompat.checkSelfPermission(activity, android.Manifest
                            .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        activity.requestPermissions(new String[]{Manifest.permission
                                .CALL_PHONE}, AppConstants
                                .MY_PERMISSIONS_REQUEST_PHONE_CALL);
                        if (activity instanceof ProfileDetailActivity) {
                            ((ProfileDetailActivity) activity).callNumber = number;
                        }
                    } else {
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
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_number_clip_board));
                return true;
            }
        });

        int pbRcpType = phoneNumber.getPbRcpType();
        if (pbRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
            holder.textMain.setText(Utils.setMultipleTypeface(activity, number + " " + activity
                            .getString(R.string.im_icon_verify), 0,
                    (StringUtils.length(number) + 1), (
                            (StringUtils.length(number) + 1) + 1)));
            holder.textMain.setTextColor(colorPineGreen);
            if (isOwnProfile)
                holder.llPrivacy.setVisibility(View.GONE);
            else
                holder.llPrivacy.setVisibility(View.GONE);
        } else if (pbRcpType == IntegerConstants.RCP_TYPE_SECONDARY) {
            holder.textMain.setText(number);
            holder.textMain.setTextColor(colorPineGreen);
            if (isOwnProfile) {
                switch ((MoreObjects.firstNonNull(phoneNumber.getPhonePublic(), 2))) {
                    case 1:
                        //everyone
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_public);
                        break;
                    case 2:
                        //my contacts
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_my_contact);
                        break;
                    case 3:
                        //only me
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_onlyme);
                        break;

                }
                holder.llPrivacy.setVisibility(View.VISIBLE);

            } else {
                holder.llPrivacy.setVisibility(View.GONE);
                if ((MoreObjects.firstNonNull(phoneNumber.getIsPrivate(), 0)) == IntegerConstants
                        .IS_PRIVATE) {
//                    holder.imageView2.setVisibility(View.GONE);
                    holder.buttonRequest.setVisibility(View.VISIBLE);
                    holder.imgActionType.setVisibility(View.GONE);
                    holder.imgActionWhatsapp.setVisibility(View.GONE);
                }
            }
            holder.llPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivacySettingPopupDialog privacySettingPopupDialog = new
                            PrivacySettingPopupDialog(holder, activity, listner, AppConstants
                            .PHONE_NUMBER,
                            position, phoneNumber.getPhonePublic(), phoneNumber.getPhoneId());
                    privacySettingPopupDialog.setDialogTitle(activity.getResources().getString(R
                            .string.privacy_dialog_title));
                    privacySettingPopupDialog.showDialog();
                }
            });
            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_phone_number", phoneNumber.getPhoneId());
                }
            });
        } else {
            holder.textMain.setText(number);
            holder.textMain.setTextColor(colorBlack);
        }
    }

    private void displayEmail(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationEmail email = (ProfileDataOperationEmail) arrayList.get(position);
        String emailId = email.getEmEmailId();
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
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_email_clip_board));
                return true;
            }
        });

        int emRcpType = email.getEmRcpType();
        final ProfileDetailViewHolder viewHodler = holder;
        if (emRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
            holder.textMain.setText(Utils.setMultipleTypeface(activity, emailId + " " + activity
                            .getString(R.string.im_icon_verify), 0,
                    (StringUtils.length(emailId) + 1), (
                            (StringUtils.length(emailId) + 1) + 1)));
            holder.textMain.setTextColor(colorPineGreen);
            if (isOwnProfile)
//                holder.llPrivacy.setVisibility(View.INVISIBLE);
                holder.llPrivacy.setVisibility(View.GONE);
            else
                holder.llPrivacy.setVisibility(View.GONE);
        } else if (emRcpType == IntegerConstants.RCP_TYPE_SECONDARY) {
            holder.textMain.setText(emailId);
            holder.textMain.setTextColor(colorPineGreen);
            if (isOwnProfile) {
                switch ((MoreObjects.firstNonNull(email.getEmPublic(), 2))) {
                    case 1:
                        //everyone
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_public);
                        break;
                    case 2:
                        //my contacts
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_my_contact);
                        break;
                    case 3:
                        //only me
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_onlyme);
                        break;

                }
                holder.llPrivacy.setVisibility(View.VISIBLE);
            } else {
                holder.llPrivacy.setVisibility(View.GONE);
                if ((MoreObjects.firstNonNull(email.getEmIsPrivate(), 0)) == IntegerConstants
                        .IS_PRIVATE) {
//                    holder.imageView2.setVisibility(View.GONE);
                    holder.buttonRequest.setVisibility(View.VISIBLE);
                    holder.imgActionType.setVisibility(View.GONE);
                }
            }
            holder.llPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivacySettingPopupDialog privacySettingPopupDialog = new
                            PrivacySettingPopupDialog(viewHodler, activity, listner, AppConstants
                            .EMAIL,
                            position, email.getEmPublic(), email.getEmId());
                    privacySettingPopupDialog.setDialogTitle(activity.getResources().getString(R
                            .string.privacy_dialog_title));
                    privacySettingPopupDialog.showDialog();
                }
            });
            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_email_id", email.getEmId());
                }
            });
        } else {
            holder.textMain.setText(emailId);
            holder.textMain.setTextColor(colorBlack);
        }
    }

    private void displayWebsite(final ProfileDetailViewHolder holder, final int position) {
//        String website = (String) arrayList.get(position);
        ProfileDataOperationWebAddress webAddress = (ProfileDataOperationWebAddress) arrayList
                .get(position);

        holder.textSub.setVisibility(View.GONE);

        if (!isOwnProfile)
            holder.llPrivacy.setVisibility(View.GONE);

        holder.imgActionType.setImageResource(R.drawable.ico_website_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = holder.textMain.getText().toString();
                if (!url.startsWith("XX") && !url.startsWith("xx")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils
                            .startsWithIgnoreCase(url, "https://")) {
                        url = "http://" + url;
                    }
                    intent.setData(Uri.parse(url));
                    activity.startActivity(intent);
                }
            }
        });

        holder.textMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = holder.textMain.getText().toString();
                if (!url.startsWith("XX") && !url.startsWith("xx")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    if (!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils
                            .startsWithIgnoreCase(url, "https://")) {
                        url = "http://" + url;
                    }
                    intent.setData(Uri.parse(url));
                    activity.startActivity(intent);
                }
            }
        });

        holder.textMain.setText(webAddress.getWebAddress());

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_website), (
                        (TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_website_clip_board));
                return true;
            }
        });

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

        holder.imgActionType.setImageResource(R.drawable.ico_address_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strAddress = holder.textMain.getText().toString();
                if (!strAddress.startsWith("XX") && !strAddress.startsWith("xx")) {
                    if (address.getGoogleLatLong() != null) {
                        ArrayList<String> arrayListLatLong = new ArrayList<>();
                        arrayListLatLong.addAll(address.getGoogleLatLong());
                        String latitude = arrayListLatLong.get(1);
                        String longitude = arrayListLatLong.get(0);
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + latitude + "," + longitude));
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + holder.textMain
                                        .getText()));
                        activity.startActivity(intent);
                    }
                }
            }
        });

        holder.textMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strAddress = holder.textMain.getText().toString();
                if (!strAddress.startsWith("XX") && !strAddress.startsWith("xx")) {
                    if (address.getGoogleLatLong() != null) {
                        ArrayList<String> arrayListLatLong = new ArrayList<>();
                        arrayListLatLong.addAll(address.getGoogleLatLong());
                        String latitude = arrayListLatLong.get(1);
                        String longitude = arrayListLatLong.get(0);
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + latitude + "," + longitude));
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=" + holder.textMain
                                        .getText()));
                        activity.startActivity(intent);
                    }
                }
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_address), (
                        (TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_address_clip_board));
                return true;
            }
        });

        int addressRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(address.getRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));
        final ProfileDetailViewHolder viewHodler = holder;
        if (addressRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
            if (isOwnProfile) {

                switch ((MoreObjects.firstNonNull(address.getAddPublic(), 2))) {
                    case 1:
                        //everyone
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_public);
                        break;
                    case 2:
                        //my contacts
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_my_contact);
                        break;
                    case 3:
                        //only me
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_onlyme);
                        break;
                }
                holder.llPrivacy.setVisibility(View.VISIBLE);
//                holder.textActionType.setVisibility(View.GONE);
            } else {
                holder.llPrivacy.setVisibility(View.GONE);
//                holder.textActionType.setVisibility(View.VISIBLE);
                if ((MoreObjects.firstNonNull(address.getIsPrivate(), 0)) == IntegerConstants
                        .IS_PRIVATE) {
//                    holder.imageView2.setVisibility(View.GONE);
                    holder.buttonRequest.setVisibility(View.VISIBLE);
                    holder.imgActionType.setVisibility(View.GONE);
                }
            }
            holder.llPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivacySettingPopupDialog privacySettingPopupDialog = new
                            PrivacySettingPopupDialog(viewHodler, activity, listner, AppConstants
                            .ADDRESS,
                            position, address.getAddPublic(), address.getAddId());
                    privacySettingPopupDialog.setDialogTitle(activity.getResources().getString(R
                            .string.privacy_dialog_title));
                    privacySettingPopupDialog.showDialog();
                }
            });
            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_address", address.getAddId());
                }
            });
        }
    }

    private void displayImAccount(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationImAccount imAccount = (ProfileDataOperationImAccount) arrayList
                .get(position);
        holder.textMain.setText(imAccount.getIMAccountFirstName() + " " + imAccount.getIMAccountLastName());
        holder.textSub.setText(imAccount.getIMAccountProtocol());

        if (imAccount.getIMAccountProtocol().equalsIgnoreCase("facebook")) {
            holder.imgActionType.setImageResource(R.drawable.ico_facebook_svg);
        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("twitter")) {
            holder.imgActionType.setImageResource(R.drawable.ico_twitter_svg);
        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("linkedin")) {
            holder.imgActionType.setImageResource(R.drawable.ico_linkedin_svg);
        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("google")) {
            holder.imgActionType.setImageResource(R.drawable.ico_google_plus_svg);
        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("skype")) {
            holder.imgActionType.setImageResource(R.drawable.ico_skype_svg);
        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("whatsapp")) {
            holder.imgActionType.setImageResource(R.drawable.ico_whatsapp_svg);
        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("instagram")) {
            holder.imgActionType.setImageResource(R.drawable.ico_instagram_svg);
        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("pinterest")) {
            holder.imgActionType.setImageResource(R.drawable.ico_pinterest_svg);
        } else {
            holder.imgActionType.setImageResource(R.drawable.ico_other_svg);
        }

        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ima = holder.textMain.getText().toString();
                if (!ima.startsWith("XX") && !ima.startsWith("xx")) {
                    if (StringUtils.length(imAccount.getIMAccountDetails()) > 0) {
                        String url = null;

                        if (imAccount.getIMAccountProtocol().equalsIgnoreCase("facebook")) {
                            url = "https://www.facebook.com/" + imAccount.getIMAccountDetails();
                        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("twitter")) {
                            url = "https://twitter.com/" + imAccount.getIMAccountDetails();
                        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("linkedin")) {
                            if (!imAccount.getIMAccountDetails().startsWith("https://www.linkedin.com"))
                                url = "https://www.linkedin.com/" + imAccount.getIMAccountDetails();
                            else
                                url = imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("google")) {
                            url = "https://plus.google.com/" + imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("skype")) {
                            url = "https://web.skype.com/" + imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("whatsapp")) {
                            url = "https://web.whatsapp.com/" + imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("instagram")) {
                            url = "http://instagram.com/_u/" + imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("pinterest")) {
                            url = "https://www.pinterest.com/" + imAccount.getIMAccountDetails();
                        }

                        if (url != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            activity.startActivity(intent);
                        }
                    }
                }
            }
        });

        holder.textMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ima = holder.textMain.getText().toString();
                if (!ima.startsWith("XX") && !ima.startsWith("xx")) {
                    if (StringUtils.length(imAccount.getIMAccountDetails()) > 0) {
                        String url = null;
                        if (imAccount.getIMAccountProtocol().equalsIgnoreCase("facebook")) {
                            url = "https://www.facebook.com/" + imAccount.getIMAccountDetails();
                        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("twitter")) {
                            url = "https://twitter.com/" + imAccount.getIMAccountDetails();
                        } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("linkedin")) {
                            url = "https://www.linkedin.com/in/" + imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("google")) {
                            url = "https://plus.google.com/" + imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("skype")) {
                            url = "https://web.skype.com/" + imAccount.getIMAccountDetails();
                        } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains
                                ("whatsapp")) {
                            url = "https://web.whatsapp.com/" + imAccount.getIMAccountDetails();
                        }

                        if (url != null) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            activity.startActivity(intent);
                        }
                    }
                }
            }
        });

        holder.textMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_im_account), (
                        (TextView) view).getText().toString());
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_im_account_clip_board));
                return true;
            }
        });

        int imRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(imAccount.getIMRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));

        if (imRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
            final ProfileDetailViewHolder viewHodler = holder;
            if (isOwnProfile) {
                holder.buttonPrivacy.setVisibility(View.VISIBLE);
                switch ((MoreObjects.firstNonNull(imAccount.getIMAccountPublic(), 2))) {
                    case 1:
                        //everyone
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_public);
                        break;
                    case 2:
                        //my contacts
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_my_contact);
                        break;
                    case 3:
                        //only me
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_onlyme);
                        break;

                }
                holder.llPrivacy.setVisibility(View.VISIBLE);
            } else {
                holder.llPrivacy.setVisibility(View.GONE);
                if ((MoreObjects.firstNonNull(imAccount.getIMAccountIsPrivate(), 0)) ==
                        IntegerConstants.IS_PRIVATE) {
//                    holder.imageView2.setVisibility(View.GONE);
                    holder.buttonRequest.setVisibility(View.VISIBLE);
                    holder.imgActionType.setVisibility(View.GONE);
                }
            }
            holder.llPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivacySettingPopupDialog privacySettingPopupDialog = new
                            PrivacySettingPopupDialog(viewHodler, activity, listner,
                            AppConstants.IM_ACCOUNT, position, imAccount.getIMAccountPublic(),
                            imAccount.getIMId());
                    privacySettingPopupDialog.setDialogTitle(activity.getResources().getString(R
                            .string.privacy_dialog_title));
                    privacySettingPopupDialog.showDialog();
                }
            });

            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_im_accounts", imAccount.getIMId());
                }
            });
        }


    }

    private void displayEvent(ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationEvent event = (ProfileDataOperationEvent) arrayList
                .get(position);

        String convertedDate;
        if (StringUtils.startsWith(event.getEventDateTime(), "XXX")) {
            convertedDate = event.getEventDateTime();
        }
       /* else if (StringUtils.startsWith(event.getEventDateTime(), "--")) {
        }
        else if (StringUtils.startsWith(event.getEventDateTime(), "--")) {
            convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "--MM-dd", "dd'th' " +
                    "MMM");
        } */
        else {

            if (MoreObjects.firstNonNull(event.getIsYearHidden(), 0) == IntegerConstants
                    .IS_YEAR_HIDDEN) {

//                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "dd-MM",
//                        "dd'th' " +
//                                "MMM");
                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "dd-MM",
                        getEventDateFormat(event.getEventDateTime()));

            } else {
                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "yyyy-MM-dd",
                        getEventDateFormat(event.getEventDateTime()));
//                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "yyyy-MM-dd",
//                        "d'th' " +
//                                "MMM, yyyy");
            }
        }
        if (!isOwnProfile) {
            if (MoreObjects.firstNonNull(event.getIsYearHidden(), 0) == IntegerConstants
                    .IS_YEAR_HIDDEN) {
                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "dd-MM",
                        getEventDateFormat(event.getEventDateTime()));
//                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "dd-MM",
//                        "dd'th' " +
//                                "MMM");
            }
        }

        if (MoreObjects.firstNonNull(event.getIsPrivate(), 0) == IntegerConstants.IS_PRIVATE) {
            convertedDate = event.getEventDateTime();
        }

        holder.textMain.setText(convertedDate);
        holder.textSub.setText(event.getEventType());
        holder.textSub.setVisibility(View.VISIBLE);

        if (event.getEventType().equals("Birthday"))
            holder.imgActionType.setImageResource(R.drawable.ico_birthday_svg);
        else if (event.getEventType().equals("Anniversary"))
            holder.imgActionType.setImageResource(R.drawable.ico_anniversary_svg);
        else
            holder.imgActionType.setImageResource(R.drawable.ico_other_svg);

        int eventRcType = Integer.parseInt(StringUtils.defaultIfEmpty(event.getEventRcType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));

        if (eventRcType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {

            holder.textMain.setTextColor(colorBlack);
        } else {
            holder.textMain.setTextColor(colorPineGreen);
            if (isOwnProfile) {
                holder.buttonPrivacy.setVisibility(View.VISIBLE);
                switch (event.getEventPublic()) {
                    case 1:
                        //everyone
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_public);
                        break;
                    case 2:
                        //my contacts
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_my_contact);
                        break;
                    case 3:
                        //only me
                        holder.buttonPrivacy.setImageResource(R.drawable.ico_privacy_onlyme);
                        break;

                }
                holder.llPrivacy.setVisibility(View.VISIBLE);
            } else {
                holder.llPrivacy.setVisibility(View.GONE);
                if(event.getIsPrivate() != null){
                    if (event.getIsPrivate() == IntegerConstants.IS_PRIVATE) {
//                    holder.imageView2.setVisibility(View.GONE);
                        holder.buttonRequest.setVisibility(View.VISIBLE);
                        holder.imgActionType.setVisibility(View.GONE);
                    }
                }

            }
            final ProfileDetailViewHolder viewHodler = holder;
            holder.llPrivacy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivacySettingPopupDialog privacySettingPopupDialog = new
                            PrivacySettingPopupDialog(viewHodler, activity, listner, AppConstants
                            .EVENT, position,
                            event.getEventPublic(), event.getEventId());
                    privacySettingPopupDialog.setDialogTitle(activity.getResources().getString(R
                            .string.privacy_dialog_title));
                    privacySettingPopupDialog.showDialog();
                }
            });
            holder.buttonRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(activity, "requesting profile", Toast.LENGTH_SHORT).show();
                    int pmTo = Integer.parseInt(pmId);
                    // sendAccessRequest(int toPMId, String carFiledType, String recordIndexId)
                    sendAccessRequest(pmTo, "pb_event", event.getEventId());
                }
            });
        }
    }

    private void displayGender(ProfileDetailViewHolder holder, final int position) {
        String gender = (String) arrayList.get(position);
        holder.textMain.setText(gender);
        holder.textSub.setVisibility(View.GONE);
        holder.textMain.setTextColor(colorBlack);

        if (gender.equals("Male"))
            holder.imgActionType.setImageResource(R.drawable.ico_male_svg);
        else
            holder.imgActionType.setImageResource(R.drawable.ico_female_svg);
    }

    private String getEventDateFormat(String date) {

        date = StringUtils.substring(date, 8, 10);
        if (!StringUtils.isNumeric(date)) {
            date = StringUtils.substring(date, 0, 1);
        }

        String format;
        if (date.endsWith("1") && !date.endsWith("11"))
//            format = "d'st' MMMM, yyyy";
            format = EVENT_ST_DATE_FORMAT;
        else if (date.endsWith("2") && !date.endsWith("12"))
            format = EVENT_ND_DATE_FORMAT;
        else if (date.endsWith("3") && !date.endsWith("13"))
            format = EVENT_RD_DATE_FORMAT;
        else
            format = EVENT_GENERAL_DATE_FORMAT;
        return format;
    }

    public ArrayList<Object> getDetailList() {
        return arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onSettingSaved(ProfileDetailViewHolder viewHolder, int whichItem, int newPrivacy,
                               int itemPosition, int oldPrivacy, String cloudId) {
        if (oldPrivacy == newPrivacy + 1) {
            return;
        }

        WsRequestObject wsRequestObject = new WsRequestObject();

        PrivacyEntityItem privacyEntityItem = new PrivacyEntityItem();
        privacyEntityItem.setId(cloudId);
        privacyEntityItem.setValue(newPrivacy + 1);
        ArrayList<PrivacyEntityItem> privacyEntityItems = new ArrayList<>();
        privacyEntityItems.add(privacyEntityItem);
        ArrayList<PrivacyDataItem> privacyItems = new ArrayList<>();
        PrivacyDataItem privacyDataItem = new PrivacyDataItem();
        switch (whichItem) {
            case AppConstants.PHONE_NUMBER:
                privacyDataItem.setPbPhoneNumber(privacyEntityItems);
                break;

            case AppConstants.EMAIL:
                privacyDataItem.setPbEmailId(privacyEntityItems);
                break;

            case AppConstants.ADDRESS:
                privacyDataItem.setPbAddress(privacyEntityItems);
                break;

            case AppConstants.IM_ACCOUNT:
                privacyDataItem.setPbIMAccounts(privacyEntityItems);
                break;

            case AppConstants.EVENT:
                privacyDataItem.setPbEvent(privacyEntityItems);
                break;
        }
        privacyItems.add(privacyDataItem);
        wsRequestObject.setPrivacyData(privacyItems);
//        wsRequestObject.setPmId(pmId);
        if (Utils.isNetworkAvailable(activity)) {
            new AsyncWebServiceCall(activity, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    wsRequestObject, null, WsResponseObject.class, WsConstants
                    .REQ_SET_PRIVACY_SETTING, activity.getResources().getString(R.string
                    .msg_please_wait), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT + WsConstants.REQ_SET_PRIVACY_SETTING);
        } else {
            //show no toast
            Toast.makeText(activity, activity.getResources().getString(R.string.msg_no_network),
                    Toast.LENGTH_SHORT).show();
        }
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
        }
    }
}
