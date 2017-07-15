package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
        holder.textSub1.setText(phoneNumber.getPhoneType());
        holder.textSub1.setVisibility(View.VISIBLE);

        holder.imgActionType.setImageResource(R.drawable.ico_phone_alt_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.callIntent(activity, number);
            }
        });

        holder.textMain1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_number), (
                        (TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_number_clip_board));
                return false;
            }
        });

        int pbRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(phoneNumber.getPbRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));
        final ProfileDetailViewHolder viewHodler = holder;
        if (pbRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
            holder.textMain1.setTypeface(Utils.typefaceIcons(activity));
            holder.textMain1.setText(String.format("%s %s", number, activity.getString(R.string.im_icon_verify)));
            holder.textMain1.setTextColor(colorPineGreen);
            if (isOwnProfile)
                holder.llPrivacy.setVisibility(View.GONE);
            else
                holder.llPrivacy.setVisibility(View.GONE);
        } else if (pbRcpType == IntegerConstants.RCP_TYPE_SECONDARY) {
            holder.textMain1.setText(number);
            holder.textMain1.setTextColor(colorPineGreen);
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
                if ((MoreObjects.firstNonNull(phoneNumber.getIsPrivate(), 0)) == IntegerConstants.IS_PRIVATE) {
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
            holder.textMain1.setText(number);
            holder.textMain1.setTextColor(colorBlack);
        }
    }

    private void displayEmail(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationEmail email = (ProfileDataOperationEmail) arrayList.get(position);
        String emailId = email.getEmEmailId();
        holder.textSub1.setText(email.getEmType());
        holder.textSub1.setVisibility(View.VISIBLE);

        holder.imgActionType.setImageResource(R.drawable.ico_envelop_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" +
                        holder.textMain1.getText()));
                activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R
                        .string.str_send_email)));
            }
        });

        holder.textMain1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_email), (
                        (TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_email_clip_board));
                return false;
            }
        });

        int emRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(email.getEmRcpType(), String
                .valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));
        final ProfileDetailViewHolder viewHodler = holder;
        if (emRcpType == IntegerConstants.RCP_TYPE_PRIMARY) {
            holder.textMain1.setTypeface(Utils.typefaceIcons(activity));
            holder.textMain1.setText(String.format("%s %s", emailId, activity.getString(R.string.im_icon_verify)));
            holder.textMain1.setTextColor(colorPineGreen);
            if (isOwnProfile)
                holder.llPrivacy.setVisibility(View.INVISIBLE);
            else
                holder.llPrivacy.setVisibility(View.GONE);
        } else if (emRcpType == IntegerConstants.RCP_TYPE_SECONDARY) {
            holder.textMain1.setText(emailId);
            holder.textMain1.setTextColor(colorPineGreen);
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
                if ((MoreObjects.firstNonNull(email.getEmIsPrivate(), 0)) == IntegerConstants.IS_PRIVATE) {
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
            holder.textMain1.setText(emailId);
            holder.textMain1.setTextColor(colorBlack);
        }
    }

    private void displayWebsite(final ProfileDetailViewHolder holder, final int position) {
//        String website = (String) arrayList.get(position);
        ProfileDataOperationWebAddress webAddress = (ProfileDataOperationWebAddress) arrayList
                .get(position);

        holder.textSub1.setVisibility(View.GONE);

        if (!isOwnProfile)
            holder.llPrivacy.setVisibility(View.GONE);

        holder.imgActionType.setImageResource(R.drawable.ico_website_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = holder.textMain1.getText().toString();
                if (!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils
                        .startsWithIgnoreCase(url, "https://")) {
                    url = "http://" + url;
                }
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            }
        });

        holder.textMain1.setText(webAddress.getWebAddress());

        holder.textMain1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_website), (
                        (TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_website_clip_board));
                return false;
            }
        });

        int rcpType = Integer.parseInt(StringUtils.defaultIfEmpty(webAddress.getWebRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));
        if (rcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain1.setTextColor(colorBlack);
        } else {
            holder.textMain1.setTextColor(colorPineGreen);
        }
    }

    private void displayAddress(final ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationAddress address = (ProfileDataOperationAddress) arrayList.get
                (position);
        holder.textMain1.setText(address.getFormattedAddress());
        holder.textSub1.setText(address.getAddressType());
        holder.textSub1.setVisibility(View.VISIBLE);

        holder.imgActionType.setImageResource(R.drawable.ico_address_svg);
        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            Uri.parse("google.navigation:q=" + holder.textMain1
                                    .getText()));
                    activity.startActivity(intent);
                }
            }
        });

        holder.textMain1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Utils.copyToClipboard(activity, activity.getString(R.string.str_copy_address), (
                        (TextView) view).getText()
                        .toString());
                Utils.showSuccessSnackBar(activity, ((ProfileDetailActivity) activity)
                        .getRelativeRootProfileDetail(), activity.getString(R.string
                        .str_copy_address_clip_board));
                return false;
            }
        });

        int addressRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(address.getRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));
        final ProfileDetailViewHolder viewHodler = holder;
        if (addressRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain1.setTextColor(colorBlack);
        } else {
            holder.textMain1.setTextColor(colorPineGreen);
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
                if ((MoreObjects.firstNonNull(address.getIsPrivate(), 0)) == IntegerConstants.IS_PRIVATE) {
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
        holder.textMain1.setText(imAccount.getIMAccountDetails());
        holder.textSub1.setText(imAccount.getIMAccountProtocol());

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
        } else {
            holder.imgActionType.setImageResource(R.drawable.ico_other_svg);
        }

        holder.imgActionType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.length(imAccount.getIMAccountDetails()) > 0) {
                    String url = null;

                    if (imAccount.getIMAccountProtocol().equalsIgnoreCase("facebook")) {
                        url = "https://www.facebook.com/" + imAccount.getIMAccountDetails();
                    } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("twitter")) {
                        url = "https://twitter.com/" + imAccount.getIMAccountDetails();
                    } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("linkedin")) {
                        url = "https://www.linkedin.com/in/" + imAccount.getIMAccountDetails();
                    } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("google")) {
                        url = "https://plus.google.com/" + imAccount.getIMAccountDetails();
                    } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("skype")) {
                        url = "https://web.skype.com/" + imAccount.getIMAccountDetails();
                    } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("whatsapp")) {
                        url = "https://web.whatsapp.com/" + imAccount.getIMAccountDetails();
                    }

                    if (url != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                    }
                }
            }
        });

        int imRcpType = Integer.parseInt(StringUtils.defaultIfEmpty(imAccount.getIMRcpType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));

        if (imRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain1.setTextColor(colorBlack);
        } else {
            holder.textMain1.setTextColor(colorPineGreen);
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
                if ((MoreObjects.firstNonNull(imAccount.getIMAccountIsPrivate(), 0)) == IntegerConstants.IS_PRIVATE) {
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

        holder.llProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.length(imAccount.getIMAccountDetails()) > 0) {
                    String url = null;
                    if (imAccount.getIMAccountProtocol().equalsIgnoreCase("facebook")) {
                        url = "https://www.facebook.com/" + imAccount.getIMAccountDetails();
                    } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("twitter")) {
                        url = "https://twitter.com/" + imAccount.getIMAccountDetails();
                    } else if (imAccount.getIMAccountProtocol().equalsIgnoreCase("linkedin")) {
                        url = "https://www.linkedin.com/in/" + imAccount.getIMAccountDetails();
                    } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("google")) {
                        url = "https://plus.google.com/" + imAccount.getIMAccountDetails();
                    } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("skype")) {
                        url = "https://web.skype.com/" + imAccount.getIMAccountDetails();
                    } else if (StringUtils.lowerCase(imAccount.getIMAccountProtocol()).contains("whatsapp")) {
                        url = "https://web.whatsapp.com/" + imAccount.getIMAccountDetails();
                    }

                    if (url != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                    }
                }
            }
        });
    }

    private void displayEvent(ProfileDetailViewHolder holder, final int position) {
        final ProfileDataOperationEvent event = (ProfileDataOperationEvent) arrayList
                .get(position);

        String convertedDate;
        if (StringUtils.startsWith(event.getEventDateTime(), "XXX")) {
            convertedDate = event.getEventDateTime();
        } else if (StringUtils.startsWith(event.getEventDateTime(), "--")) {
            convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "--MM-dd", "dd'th' " +
                    "MMM");
        } else {
            convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "yyyy-MM-dd",
                    "dd'th' " +
                            "MMM, yyyy");
        }
        if (!isOwnProfile) {
            if (MoreObjects.firstNonNull(event.getIsYearHidden(), 0) == IntegerConstants.IS_YEAR_HIDDEN) {
                convertedDate = Utils.convertDateFormat(event.getEventDateTime(), "MM-dd",
                        "dd'th' " +
                                "MMM");
            }
        }

        if (MoreObjects.firstNonNull(event.getIsPrivate(), 0) == IntegerConstants.IS_PRIVATE) {
            convertedDate = event.getEventDateTime();
        }

        holder.textMain1.setText(convertedDate);
        holder.textSub1.setText(event.getEventType());
        holder.textSub1.setVisibility(View.VISIBLE);

        if (event.getEventType().equals("Birthday"))
            holder.imgActionType.setImageResource(R.drawable.ico_birthday_svg);
        else if (event.getEventType().equals("Anniversary"))
            holder.imgActionType.setImageResource(R.drawable.ico_anniversary_svg);
        else
            holder.imgActionType.setImageResource(R.drawable.ico_other_svg);

        int eventRcType = Integer.parseInt(StringUtils.defaultIfEmpty(event.getEventRcType(),
                String.valueOf(IntegerConstants.RCP_TYPE_SECONDARY)));

        if (eventRcType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {

            holder.textMain1.setTextColor(colorBlack);
        } else {
            holder.textMain1.setTextColor(colorPineGreen);
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
                if (event.getIsPrivate() == IntegerConstants.IS_PRIVATE) {
//                    holder.imageView2.setVisibility(View.GONE);
                    holder.buttonRequest.setVisibility(View.VISIBLE);
                    holder.imgActionType.setVisibility(View.GONE);
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
        holder.textMain1.setText(gender);
        holder.textSub1.setVisibility(View.GONE);
        holder.textMain1.setTextColor(colorBlack);

        if (gender.equals("Male"))
            holder.imgActionType.setImageResource(R.drawable.ico_male_svg);
        else
            holder.imgActionType.setImageResource(R.drawable.ico_female_svg);
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
        @BindView(R.id.text_main)
        public TextView textMain1;
        @BindView(R.id.text_sub)
        TextView textSub1;
        //        @BindView(R.id.image_view)
//        ImageView imageView1;
        @BindView(R.id.button_privacy)
        ImageView buttonPrivacy;
        //        @BindView(R.id.view_own_profile)
//        LinearLayout viewOwnProfile;
//        @BindView(R.id.text_main1)
//        public TextView textMain2;
//        @BindView(R.id.text_sub2)
//        TextView textSub2;
        @BindView(R.id.button_request)
        AppCompatButton buttonRequest;
        //        @BindView(R.id.image_view1)
//        ImageView imageView2;
//        @BindView(R.id.view_other_profile)
//        LinearLayout viewOtherProfile;
        @BindView(R.id.ll_profile_data)
        LinearLayout llProfileData;
        @BindView(R.id.ll_privacy)
        LinearLayout llPrivacy;

//        TextView getTextMain(boolean isOwnProfile) {
//            if (isOwnProfile) {
//                return textMain1;
//            } else {
//                return textMain2;
//            }
//        }

//        TextView getTextSub(boolean isOwnProfile) {
//            if (isOwnProfile) {
//                return textSub1;
//            } else {
//                return textSub2;
//            }
//        }

        ProfileDetailViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain1.setTypeface(Utils.typefaceRegular(activity));
            textSub1.setTypeface(Utils.typefaceRegular(activity));
//            textMain2.setTypeface(Utils.typefaceRegular(activity));
//            textSub2.setTypeface(Utils.typefaceRegular(activity));
        }
    }
}
