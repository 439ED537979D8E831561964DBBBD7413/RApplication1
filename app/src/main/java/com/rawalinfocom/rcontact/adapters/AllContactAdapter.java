package com.rawalinfocom.rcontact.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.SearchActivity;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.AllContactsListFragment;
import com.rawalinfocom.rcontact.contacts.FavoritesFragment;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.PhoneBookContacts;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/04/16.
 */

public class AllContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements SectionIndexer {

    /**
     * relativeRowAllContact tag :
     * rcp Contact: pm id
     * non rcp: -1
     * own profile: 0
     */

    private Context context;
    private Fragment fragment;
    /* phone book contacts */
    private ArrayList<Object> arrayListUserContact;
    private ArrayList<String> arrayListContactHeader;
    private int previousPosition = 0;

    private final int HEADER = 0, CONTACT = 1, FOOTER = 2;
    private int colorBlack, colorPineGreen;
    private int listClickedPosition = -1;

    private TableProfileMaster tableProfileMaster;
    private TableProfileMobileMapping tableProfileMobileMapping;
    private TableProfileEmailMapping tableProfileEmailMapping;

    private ArrayList<Integer> arrayListExpandedPositions;

    private PhoneBookContacts phoneBookContacts;
    private ArrayList<Integer> mSectionPositions;
    private ArrayList<Object> arraylist;
    private int searchCount;
    private MaterialDialog callConfirmationDialog;
    private ContactListExpandAdapter expandableAdapter;

    //<editor-fold desc="Constructor">
    public AllContactAdapter(Fragment fragment, ArrayList<Object> arrayListUserContact,
                             ArrayList<String> arrayListContactHeader) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.arrayListUserContact = arrayListUserContact;
        this.arrayListContactHeader = arrayListContactHeader;
        /*this.arrayListContactHeader = new ArrayList<>();
        this.arrayListContactHeader.add("#");
        this.arrayListContactHeader.addAll(arrayListContactHeader);*/


        arrayListExpandedPositions = new ArrayList<>();

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);

        tableProfileMaster = new TableProfileMaster(((BaseActivity) context).databaseHandler);
        tableProfileMobileMapping = new TableProfileMobileMapping(((BaseActivity) context)
                .databaseHandler);
        tableProfileEmailMapping = new TableProfileEmailMapping(((BaseActivity) context)
                .databaseHandler);

        phoneBookContacts = new PhoneBookContacts(context);
    }

    public AllContactAdapter(Activity activity, ArrayList<Object> arrayListUserContact) {
        this.context = activity;
        this.fragment = fragment;
//        this.arrayListUserContact = arrayListUserContact;
        this.arrayListUserContact = new ArrayList<>();
        this.arrayListUserContact.addAll(arrayListUserContact);
        /*this.arrayListContactHeader = new ArrayList<>();
        this.arrayListContactHeader.add("#");
        this.arrayListContactHeader.addAll(arrayListContactHeader);*/
        this.arraylist = new ArrayList<Object>();
        this.arraylist.addAll(arrayListUserContact);

        arrayListExpandedPositions = new ArrayList<>();

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);

        tableProfileMaster = new TableProfileMaster(((BaseActivity) context).databaseHandler);
        tableProfileMobileMapping = new TableProfileMobileMapping(((BaseActivity) context)
                .databaseHandler);
        tableProfileEmailMapping = new TableProfileEmailMapping(((BaseActivity) context)
                .databaseHandler);

        phoneBookContacts = new PhoneBookContacts(context);
    }
    //</editor-fold>

    //<editor-fold desc="Override Methods">


    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case HEADER:
                View v1 = inflater.inflate(R.layout.list_item_header_contact, parent, false);
                viewHolder = new ContactHeaderViewHolder(v1);
                break;
            case CONTACT:
                View v2 = inflater.inflate(R.layout.list_item_all_contacts, parent, false);
                viewHolder = new AllContactViewHolder(v2);
                break;
            case FOOTER:
                View v3 = inflater.inflate(R.layout.footer_all_contacts, parent, false);
                viewHolder = new ContactFooterViewHolder(v3);
                break;
        }
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case HEADER:
                ContactHeaderViewHolder contactHeaderViewHolder = (ContactHeaderViewHolder) holder;
                configureHeaderViewHolder(contactHeaderViewHolder, position);
                break;
            case CONTACT:
                AllContactViewHolder contactViewHolder = (AllContactViewHolder) holder;
                configureAllContactViewHolder(contactViewHolder, position);
                break;
            case FOOTER:
                ContactFooterViewHolder contactFooterViewHolder = (ContactFooterViewHolder) holder;
                configureFooterViewHolder(contactFooterViewHolder);
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
//        if (position == arrayListUserContact.size() && fragment!=null) {
        if (position == arrayListUserContact.size() && !(context instanceof SearchActivity)) {
            return FOOTER;
        } else if (arrayListUserContact.get(position) instanceof ProfileData) {
            return CONTACT;
        } else if (arrayListUserContact.get(position) instanceof String) {
            return HEADER;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
//        if(fragment!=null){
        if (!(context instanceof SearchActivity)) {
            return (arrayListUserContact.size() + 1);
        } else {
            return arrayListUserContact.size();
        }

    }

    /**
     * Section Indexer
     */

    @Override
    public Object[] getSections() {
        //return arrayListContactHeader.toArray(new String[arrayListContactHeader.size()]);
        List<String> sections = new ArrayList<>();
        mSectionPositions = new ArrayList<>();
        int startPosition = 0;
        if (fragment != null) {
            if (fragment instanceof AllContactsListFragment) {
                startPosition = 2;
            } else {
                startPosition = 0;
            }
        }

        for (int i = startPosition, size = arrayListUserContact.size(); i < size; i++) {
            if (arrayListUserContact.get(i) instanceof ProfileData) {
                String contactDisplayName = "";
                ProfileData profileData = (ProfileData) arrayListUserContact.get(i);
//                String prefix = profileData.getTempPrefix();
//                String firstName = profileData.getTempFirstName();
//                String lastName = profileData.getTempLastName();
//                String middleName = profileData.getTempMiddleName();
//                String suffix = profileData.getTempSufix();

//                if (StringUtils.length(prefix) > 0) {
//                    contactDisplayName = prefix + " ";
//                }
//                if (StringUtils.length(firstName) > 0) {
//                    contactDisplayName = contactDisplayName + firstName + " ";
//                }
//                if (StringUtils.length(middleName) > 0) {
//                    contactDisplayName = contactDisplayName + middleName + " ";
//                }
//                if (StringUtils.length(lastName) > 0) {
//                    contactDisplayName = contactDisplayName + lastName + " ";
//                }
//                if (StringUtils.length(suffix) > 0) {
//                    contactDisplayName = contactDisplayName + suffix;
//                }
                contactDisplayName = /*StringUtils.trimToEmpty(contactDisplayName);*/StringUtils.defaultIfEmpty(profileData.getName(), "");
                if (contactDisplayName == null || contactDisplayName.length() == 0) {
                    String section = "#";
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                } else {
                    String section = String.valueOf(contactDisplayName.charAt(0)).toUpperCase();
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                }
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void configureAllContactViewHolder(final AllContactViewHolder holder, final int
            position) {

        final ProfileData profileData = (ProfileData) arrayListUserContact.get(position);
        final String thumbnailUrl = profileData.getProfileUrl();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(context)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(500, 500)
                    .into(holder.imageProfile);

        } else {
            holder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        holder.textContactName.setTag(position);
        if (arrayListExpandedPositions.contains(position)) {
            holder.recyclerViewMultipleRc.setVisibility(View.VISIBLE);
        } else {
            holder.recyclerViewMultipleRc.setVisibility(View.GONE);
        }

//        holder.relativeRowAllContact.setTag(profileData.getTempRcpName());

        boolean showPineGreen = false;

        String contactDisplayName = "";
        String prefix = profileData.getTempPrefix();
        String firstName = profileData.getTempFirstName();
        String lastName = profileData.getTempLastName();
        String middleName = profileData.getTempMiddleName();
        String suffix = profileData.getTempSufix();

        if (StringUtils.length(prefix) > 0) {
            contactDisplayName = prefix + " ";
        }
        if (StringUtils.length(firstName) > 0) {
            contactDisplayName = contactDisplayName + firstName + " ";
        }
        if (StringUtils.length(middleName) > 0) {
            contactDisplayName = contactDisplayName + middleName + " ";
        }
        if (StringUtils.length(lastName) > 0) {
            contactDisplayName = contactDisplayName + lastName + " ";
        }
        if (StringUtils.length(suffix) > 0) {
            contactDisplayName = contactDisplayName + suffix;
        }
        contactDisplayName = /*StringUtils.trimToEmpty(contactDisplayName);*/StringUtils.defaultIfEmpty(profileData.getName(), "");

        holder.textContactName.setText(contactDisplayName.length() > 0 ? contactDisplayName :
                context.getString(R.string.unknown));

         /* Hide Divider if row is last in Section */
        if ((position + 1) < arrayListUserContact.size()) {
            if (arrayListUserContact.get(position + 1) instanceof String) {
                holder.dividerAllContact.setVisibility(View.GONE);
            } else {
                holder.dividerAllContact.setVisibility(View.VISIBLE);
            }
        }

        if (profileData.getTempIsRcp()) {
            holder.textCloudContactName.setVisibility(View.VISIBLE);
            if (contactDisplayName.equalsIgnoreCase(profileData.getTempRcpName())) {
                holder.textCloudContactName.setVisibility(View.GONE);
                showPineGreen = true;
            } else {
                holder.textCloudContactName.setVisibility(View.VISIBLE);
                if (!StringUtils.isEmpty(profileData.getTempRcpName())) {
                    holder.textCloudContactName.setText(" (" + profileData.getTempRcpName() + ")");
                    showPineGreen = false;
                }
            }
            holder.rippleInvite.setVisibility(View.GONE);
            holder.relativeRowAllContact.setTag(profileData.getTempRcpId());
            if (StringUtils.contains(profileData.getTempRcpName(), ",")) {
                holder.relativeRowAllContact.setTag(profileData.getTempRcpName());
                holder.textCloudContactName.setText(" (" + String.valueOf(StringUtils.countMatches
                        (profileData.getTempRcpName(), ",") + 1) + " RC)");
                holder.rippleInvite.setVisibility(View.GONE);
            }
        } else {
            showPineGreen = false;
            holder.relativeRowAllContact.setTag("-1");
            holder.textCloudContactName.setVisibility(View.GONE);
            holder.textCloudContactName.setText("");
            if (Utils.getBooleanPreference(context, AppConstants.PREF_CONTACT_SYNCED, false)) {
                holder.rippleInvite.setVisibility(View.VISIBLE);
            } else {
                holder.rippleInvite.setVisibility(View.GONE);
            }
        }

        /*if (fragment instanceof AllContactsListFragment) {
            if (position == 1) {
                holder.textContactName.setTextColor(colorPineGreen);
                holder.textContactNumber.setTextColor(colorPineGreen);
                holder.textCloudContactName.setVisibility(View.GONE);
            } else {
                if (showPineGreen) {
                    holder.textContactName.setTextColor(colorPineGreen);
                } else {
                    holder.textContactName.setTextColor(colorBlack);
                }
                holder.textContactNumber.setTextColor(colorBlack);
                holder.textCloudContactName.setVisibility(View.VISIBLE);
            }
        }*/
        if (fragment instanceof AllContactsListFragment && position == 1) {
//            if (position == 1) {
            holder.textContactName.setTextColor(colorPineGreen);
            holder.textContactNumber.setTextColor(colorPineGreen);
            holder.textCloudContactName.setVisibility(View.GONE);
        } else {
            if (showPineGreen) {
                holder.textContactName.setTextColor(colorPineGreen);
            } else {
                holder.textContactName.setTextColor(colorBlack);
            }
            holder.textContactNumber.setTextColor(colorBlack);
            holder.textCloudContactName.setVisibility(View.VISIBLE);
        }
//        }

        holder.textContactNumber.setText(Utils.getFormattedNumber(context, profileData
                .getTempNumber()));

        //<editor-fold desc="relativeRowAllContact Click">
        holder.relativeRowAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                TextView textName = (TextView) v.findViewById(R.id.text_contact_name);
                TextView textCloudName = (TextView) v.findViewById(R.id.text_cloud_contact_name);

//                if (StringUtils.isNumeric(String.valueOf(v.getTag()))) {
                if (holder.recyclerViewMultipleRc.getVisibility() == View.GONE) {
                    if (StringUtils.contains(String.valueOf(v.getTag()), ",")) {
                        // Multiple RCP
                        holder.recyclerViewMultipleRc.setVisibility(View.VISIBLE);
                        QueryManager queryManager = new QueryManager(((BaseActivity) context)
                                .databaseHandler);
                        ArrayList<ProfileData> arrayList = new ArrayList<>();
                        arrayList.addAll(queryManager.getRcpNumberName(String.valueOf(v.getTag())));
                        holder.recyclerViewMultipleRc.setLayoutManager(new LinearLayoutManager
                                (context));
                        expandableAdapter = new ContactListExpandAdapter(context,
                                arrayList, profileData.getLocalPhoneBookId(), holder
                                .textContactName.getText().toString());
                        holder.recyclerViewMultipleRc.setAdapter(expandableAdapter);
                    } else if (StringUtils.equalsAnyIgnoreCase(String.valueOf(v.getTag()), "-1")) {
                        // Non Rcp
                        holder.recyclerViewMultipleRc.setVisibility(View.GONE);
                        bundle.putString(AppConstants.EXTRA_PM_ID, "-1");
                        bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, profileData
                                .getLocalPhoneBookId());
                    } else if (StringUtils.equalsAnyIgnoreCase(String.valueOf(v.getTag()), "0")) {
                        // Own Profile
                        holder.recyclerViewMultipleRc.setVisibility(View.GONE);
                    } else if (StringUtils.isNumeric(String.valueOf(v.getTag()))) {
                        // Single Rcp
                        bundle.putString(AppConstants.EXTRA_PM_ID, String.valueOf(v.getTag()));
                        bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, profileData
                                .getLocalPhoneBookId());
                        holder.recyclerViewMultipleRc.setVisibility(View.GONE);
                    }
                    if (!String.valueOf(v.getTag()).contains(",")) {
                  /*  TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                        TextView textCloudName = (TextView) view.findViewById(R.id
                                .text_cloud_contact_name);*/

                        bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText()
                                .toString());
                        bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, thumbnailUrl);
                        listClickedPosition = (int) textName.getTag();
                        bundle.putInt(AppConstants.EXTRA_CONTACT_POSITION, listClickedPosition);
                        if (textCloudName.getVisibility() == View.VISIBLE) {
                            bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, textCloudName
                                    .getText().toString());
                        }
//                        bundle.putString(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE, profileImage);
                        if (fragment instanceof AllContactsListFragment) {
                            Intent intent = new Intent(context, ProfileDetailActivity.class);
                            intent.putExtras(bundle);
                            fragment.startActivityForResult(intent, AppConstants
                                    .REQUEST_CODE_PROFILE_DETAIL);
                            ((BaseActivity) context).overridePendingTransition(R.anim.enter, R
                                    .anim.exit);
                        } else if (fragment instanceof FavoritesFragment) {
                            Intent intent = new Intent(context, ProfileDetailActivity.class);
                            bundle.putBoolean(AppConstants.EXTRA_IS_FROM_FAVOURITE, true);
                            intent.putExtras(bundle);
                            fragment.startActivityForResult(intent, AppConstants
                                    .REQUEST_CODE_PROFILE_DETAIL);
                            ((BaseActivity) context).overridePendingTransition(R.anim.enter, R
                                    .anim.exit);
                        } else {
                            if (context instanceof SearchActivity) {
                                Intent intent = new Intent(context, ProfileDetailActivity.class);
                                intent.putExtras(bundle);
                                context.startActivity(intent);
                                ((BaseActivity) context).overridePendingTransition(R.anim.enter, R
                                        .anim.exit);
                            }
                        }
                    }
                } else {
                    holder.recyclerViewMultipleRc.setVisibility(View.GONE);
                }
            }
        });
        //</editor-fold>

        //<editor-fold desc="imageSocialMedia Click">
        holder.imageSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });
        //</editor-fold>

        //<editor-fold desc="buttonInvite Click">
      /*  holder.buttonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers = new ArrayList<>();

                Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(profileData
                        .getLocalPhoneBookId());
                if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                    while (contactNumberCursor.moveToNext()) {

                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();
                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(context,
                                contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                        phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                                (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.TYPE))));

                        phoneNumbers.add(phoneNumber);

                    }
                    contactNumberCursor.close();
                }

                ArrayList<ProfileDataOperationEmail> emailIds = new ArrayList<>();

                Cursor contactEmailCursor = phoneBookContacts.getContactEmail(profileData
                        .getLocalPhoneBookId());
                if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                    while (contactEmailCursor.moveToNext()) {

                        ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();
                        emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                        emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                                contactEmailCursor.getInt(contactEmailCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Email.TYPE))));

                        emailIds.add(emailId);

                    }
                    contactEmailCursor.close();
                }

                if (phoneNumbers.size() + emailIds.size() > 1) {
                    selectContactDialog(profileData.getTempFirstName(), phoneNumbers, emailIds);
                } else {
                    if (phoneNumbers.size() > 0) {
                        ArrayList<String> numbers = new ArrayList<>();
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            numbers.add(phoneNumbers.get(i).getPhoneNumber());
                        }
                        inviteContact(numbers, null);
                    } else if (emailIds.size() > 0) {
                        ArrayList<String> emails = new ArrayList<>();
                        for (int i = 0; i < emailIds.size(); i++) {
                            emails.add(emailIds.get(i).getEmEmailId());
                        }
                        inviteContact(null, emails);
                    }
                }
            }
        });*/

        holder.rippleInvite.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers = new ArrayList<>();

                Cursor contactNumberCursor = phoneBookContacts.getContactNumbers(profileData
                        .getLocalPhoneBookId());
                if (contactNumberCursor != null && contactNumberCursor.getCount() > 0) {
                    while (contactNumberCursor.moveToNext()) {

                        ProfileDataOperationPhoneNumber phoneNumber = new
                                ProfileDataOperationPhoneNumber();
                        phoneNumber.setPhoneNumber(Utils.getFormattedNumber(context,
                                contactNumberCursor.getString(contactNumberCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.NUMBER))));
                        phoneNumber.setPhoneType(phoneBookContacts.getPhoneNumberType
                                (contactNumberCursor.getInt(contactNumberCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Phone.TYPE))));

                        phoneNumbers.add(phoneNumber);

                    }
                    contactNumberCursor.close();
                }

                ArrayList<ProfileDataOperationEmail> emailIds = new ArrayList<>();

                Cursor contactEmailCursor = phoneBookContacts.getContactEmail(profileData
                        .getLocalPhoneBookId());
                if (contactEmailCursor != null && contactEmailCursor.getCount() > 0) {
                    while (contactEmailCursor.moveToNext()) {

                        ProfileDataOperationEmail emailId = new ProfileDataOperationEmail();
                        emailId.setEmEmailId(contactEmailCursor.getString(contactEmailCursor
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)));
                        emailId.setEmType(phoneBookContacts.getEmailType(contactEmailCursor,
                                contactEmailCursor.getInt(contactEmailCursor.getColumnIndex
                                        (ContactsContract.CommonDataKinds.Email.TYPE))));

                        emailIds.add(emailId);

                    }
                    contactEmailCursor.close();
                }

                if (phoneNumbers.size() + emailIds.size() > 1) {
                    selectContactDialog(profileData.getTempFirstName(), phoneNumbers, emailIds);
                } else {
                    if (phoneNumbers.size() > 0) {
                        ArrayList<String> numbers = new ArrayList<>();
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            numbers.add(phoneNumbers.get(i).getPhoneNumber());
                        }
                        inviteContact(numbers, null);
                    } else if (emailIds.size() > 0) {
                        ArrayList<String> emails = new ArrayList<>();
                        for (int i = 0; i < emailIds.size(); i++) {
                            emails.add(emailIds.get(i).getEmEmailId());
                        }
                        inviteContact(null, emails);
                    }
                }
            }
        });

        //</editor-fold>

       /* if (profileData.getOperation().get(0).getPbPhoneNumber().size() > 0) {
            displayNumber(holder, profileData, contactDisplayName, position);
        } else if (!Utils.isArraylistNullOrEmpty(profileData.getOperation().get(0)
                .getPbEmailId()) && profileData.getOperation().get(0).getPbEmailId().size() > 0) {
            displayEmail(holder, profileData, null, contactDisplayName, position);
        }

        *//* Hide Divider if row is last in Section *//*
        if ((position + 1) < arrayListUserContact.size()) {
            if (arrayListUserContact.get(position + 1) instanceof String) {
                holder.dividerAllContact.setVisibility(View.GONE);
            } else {
                holder.dividerAllContact.setVisibility(View.VISIBLE);
            }
        }

        final String profileImage =  profileData.getProfileUrl();
        if(!TextUtils.isEmpty(profileImage)){
            Glide.with(context)
                    .load(profileImage)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .override(200, 200)
                    .into(holder.imageProfile);
        }else{
            holder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }


        holder.relativeRowAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();

                TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                TextView textCloudName = (TextView) view.findViewById(R.id
                        .text_cloud_contact_name);

                if (StringUtils.equalsIgnoreCase(view.getTag().toString(), "0")) {
                    // Display own profile
                    bundle.putString(AppConstants.EXTRA_PM_ID, ((BaseActivity) context)
                            .getUserPmId());
                    bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "-1");
                } else if (!StringUtils.equalsIgnoreCase(view.getTag().toString(), "-1")) {
                    // RCP profile

                    if (String.valueOf(view.getTag()).contains(",")) {
                        if (holder.recyclerViewMultipleRc.getVisibility() == View.VISIBLE) {
                            holder.recyclerViewMultipleRc.setVisibility(View.GONE);
                            arrayListExpandedPositions.remove((Object) textName.getTag());
                        } else {
                            arrayListExpandedPositions.add((int) textName.getTag());
                            holder.recyclerViewMultipleRc.setVisibility(View.VISIBLE);
                        }
                    } else {
                        bundle.putString(AppConstants.EXTRA_PM_ID, String.valueOf(view.getTag()));
                        bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, profileData
                                .getLocalPhoneBookId());
                    }

                } else {
                    // Non RCP profile
                    bundle.putString(AppConstants.EXTRA_PM_ID, "-1");
                    bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, profileData
                            .getLocalPhoneBookId());
                }
                if (!String.valueOf(view.getTag()).contains(",")) {
                  *//*  TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                    TextView textCloudName = (TextView) view.findViewById(R.id
                            .text_cloud_contact_name);*//*

                    bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText().toString
                            ());
                    listClickedPosition = (int) textName.getTag();
                    if (textCloudName.getVisibility() == View.VISIBLE) {
                        bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, textCloudName
                                .getText().toString());
                    }
                    bundle.putString(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE,profileImage);
                    ((BaseActivity) context).startActivityIntent(context, ProfileDetailActivity
                            .class, bundle);
                }
            }
        });

        holder.imageSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });

        holder.buttonInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers = new ArrayList<>();
                phoneNumbers.addAll(profileData.getOperation().get(0).getPbPhoneNumber());
                ArrayList<ProfileDataOperationEmail> emailIds = new ArrayList<>();
                emailIds.addAll(profileData.getOperation().get(0).getPbEmailId());

                if (phoneNumbers.size() + emailIds.size() > 1) {
                    selectContactDialog(profileData.getOperation()
                            .get(0).getPbNameFirst(), phoneNumbers, emailIds);
                } else {
                    if (phoneNumbers.size() > 0) {
                        ArrayList<String> numbers = new ArrayList<>();
                        for (int i = 0; i < phoneNumbers.size(); i++) {
                            numbers.add(phoneNumbers.get(i).getPhoneNumber());
                        }
                        inviteContact(numbers, null);
                    } else if (emailIds.size() > 0) {
                        ArrayList<String> emails = new ArrayList<>();
                        for (int i = 0; i < emailIds.size(); i++) {
                            emails.add(emailIds.get(i).getEmEmailId());
                        }
                        inviteContact(null, emails);
                    }
                }

            }
        });*/

    }

    private void configureHeaderViewHolder(ContactHeaderViewHolder holder, int position) {
        String letter = (String) arrayListUserContact.get(position);
        holder.textHeader.setText(letter);
    }

    private void configureFooterViewHolder(ContactFooterViewHolder holder) {
//        String letter = (String) arrayListUserContact.get(position);
        if (fragment instanceof AllContactsListFragment) {
            holder.textTotalContacts.setText(String.format(Locale.getDefault(), "%d%s", arrayListUserContact.size() - 3, context.getString(R.string.contacts)));
        } else if (fragment instanceof FavoritesFragment) {
            holder.textTotalContacts.setText(String.format(Locale.getDefault(), "%d%s", arrayListUserContact.size() -
                    arrayListContactHeader.size(), context.getString(R.string.contacts)));

        }
    }

    private void displayNumber(AllContactViewHolder holder, ProfileData profileData, String
            contactDisplayName, int position) {

       /* ArrayList<String> arrayListMobileNumbers = new ArrayList<>();
        for (int i = 0; i < profileData.getOperation().get(0).getPbPhoneNumber().size(); i++) {
            arrayListMobileNumbers.add(profileData.getOperation().get(0).getPbPhoneNumber().get
                    (i).getPhoneNumber());
        }

        ArrayList<ProfileMobileMapping> arrayListDbMobileNumbers = tableProfileMobileMapping
                .getProfileMobileMappingFromNumber(arrayListMobileNumbers.toArray(new
                        String[arrayListMobileNumbers.size()]));

        String displayNumber, displayName, rating, totalRatingUser;
        boolean isRcp;
        if (arrayListDbMobileNumbers.size() > 0) {

            displayNumber = arrayListDbMobileNumbers.get(0).getMpmMobileNumber();

            if (arrayListDbMobileNumbers.size() == 1) {

//                holder.recyclerViewMultipleRc.setVisibility(View.GONE);

                String displayNamePmId = arrayListDbMobileNumbers.get(0).getMpmCloudPmId();
                holder.relativeRowAllContact.setTag(displayNamePmId);

                UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                        .parseInt(displayNamePmId));

                rating = userProfile.getTotalProfileRateUser();
                totalRatingUser = userProfile.getProfileRating();

                holder.linearRating.setVisibility(View.VISIBLE);
                holder.buttonInvite.setVisibility(View.GONE);
                holder.imageSocialMedia.setVisibility(View.VISIBLE);
                holder.textRatingUserCount.setText(rating);
                holder.ratingUser.setRating(Float.parseFloat(totalRatingUser));

                displayName = ((userProfile.getPmFirstName().length() > 0 || userProfile
                        .getPmLastName().length() > 0) ? " (" + userProfile.getPmFirstName() + " " +
                        "" + userProfile.getPmLastName() + ")" : "");

            } else {

                holder.linearRating.setVisibility(View.GONE);
                holder.buttonInvite.setVisibility(View.VISIBLE);
                holder.imageSocialMedia.setVisibility(View.GONE);
//                holder.recyclerViewMultipleRc.setVisibility(View.VISIBLE);
                populateNumberRecyclerView(holder.recyclerViewMultipleRc, arrayListDbMobileNumbers,
                        contactDisplayName, profileData.getLocalPhoneBookId());

                String[] pmIds = new String[arrayListDbMobileNumbers.size()];
                for (int i = 0; i < arrayListDbMobileNumbers.size(); i++) {
                    pmIds[i] = arrayListDbMobileNumbers.get(i).getMpmCloudPmId();
                }
                holder.relativeRowAllContact.setTag(StringUtils.join(pmIds, ","));

                displayName = " (" + arrayListDbMobileNumbers.size() + "RC)";

            }

            if (StringUtils.equals(displayName, (" (" + contactDisplayName + ")"))) {
                holder.textCloudContactName.setVisibility(View.GONE);
                holder.textContactName.setTextColor(colorPineGreen);
            } else {
                holder.textCloudContactName.setVisibility(View.VISIBLE);

                if (position == 1 && fragment instanceof AllContactsFragment) {
                    holder.textContactName.setTextColor(colorPineGreen);
                } else {
                    holder.textContactName.setTextColor(colorBlack);
                }
            }

            holder.textContactNumber.setTextColor(colorPineGreen);
            isRcp = true;
            holder.linearRating.setVisibility(View.VISIBLE);
            holder.buttonInvite.setVisibility(View.GONE);
//            holder.imageSocialMedia.setVisibility(View.VISIBLE);

        } else {

//            holder.recyclerViewMultipleRc.setVisibility(View.GONE);

            displayNumber = profileData.getOperation().get(0).getPbPhoneNumber().get(0)
                    .getPhoneNumber();
            rating = profileData.getOperation().get(0).getProfileRating();
            totalRatingUser = profileData.getOperation().get(0).getTotalProfileRateUser();

            displayName = " ";
            if (position == 1 && fragment instanceof AllContactsFragment) {
                holder.relativeRowAllContact.setTag("0");
                holder.textContactName.setTextColor(colorPineGreen);
                holder.textContactNumber.setTextColor(colorPineGreen);
                holder.linearRating.setVisibility(View.VISIBLE);
                holder.buttonInvite.setVisibility(View.GONE);
                holder.imageSocialMedia.setVisibility(View.VISIBLE);
                holder.textRatingUserCount.setText(rating);
                holder.ratingUser.setRating(Float.parseFloat(totalRatingUser));
            } else {
                holder.relativeRowAllContact.setTag("-1");
                holder.textContactName.setTextColor(colorBlack);
                holder.textContactNumber.setTextColor(colorBlack);
                holder.linearRating.setVisibility(View.GONE);
                holder.buttonInvite.setVisibility(View.VISIBLE);
                holder.imageSocialMedia.setVisibility(View.GONE);
            }

            isRcp = false;
        }

        displayNumber = Utils.getFormattedNumber(context, displayNumber);

        holder.textCloudContactName.setText(displayName);
        holder.textContactNumber.setText(displayNumber);

        if (!isRcp && !Utils.isArraylistNullOrEmpty(profileData.getOperation().get(0)
                .getPbEmailId())) {
            displayEmail(holder, profileData, displayNumber, contactDisplayName, position);
        }*/




       /* if (holder.textCloudContactName.getVisibility() == View.VISIBLE && holder
                .textCloudContactName.getText().toString().length() > 0) {
            holder.textContactName.setMaxWidth(((LinearLayout) holder.textContactName.getParent()
            ).getWidth() / 2);
            holder.textCloudContactName.setMaxWidth(((LinearLayout) holder
                    .textCloudContactName.getParent()).getWidth() / 2);
        } else {
            holder.textContactName.setWidth(((LinearLayout) holder.textContactName.getParent())
                    .getWidth());
        }*/

    }

    private void populateNumberRecyclerView(RecyclerView recyclerViewMultipleRc,
                                            ArrayList<ProfileMobileMapping>
                                                    arrayListDbMobileNumbers, String
                                                    contactDisplayName, String phonebookId) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewMultipleRc.setLayoutManager(linearLayoutManager);

        ExpandableContactListAdapter adapter = new ExpandableContactListAdapter(fragment,
                arrayListDbMobileNumbers, null, contactDisplayName, phonebookId);
        recyclerViewMultipleRc.setAdapter(adapter);

    }

    private void populateEmailRecyclerView(RecyclerView recyclerViewMultipleRc,
                                           ArrayList<ProfileEmailMapping> arrayListDbEmailIds,
                                           String contactDisplayName, String phonebookId) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerViewMultipleRc.setLayoutManager(linearLayoutManager);

        ExpandableContactListAdapter adapter = new ExpandableContactListAdapter(fragment, null,
                arrayListDbEmailIds, contactDisplayName, phonebookId);
        recyclerViewMultipleRc.setAdapter(adapter);

    }

    private void displayEmail(AllContactViewHolder holder, ProfileData profileData, String
            displayNumber, String contactDisplayName, int position) {
        /*TableProfileEmailMapping tableProfileEmailMapping = new TableProfileEmailMapping((
                (BaseActivity) context).databaseHandler);*/

       /* ArrayList<String> arrayListEmails = new ArrayList<>();
        for (int i = 0; i < profileData.getOperation().get(0).getPbEmailId().size(); i++) {
            arrayListEmails.add(profileData.getOperation().get(0).getPbEmailId().get
                    (i).getEmEmailId());
        }

        if (arrayListEmails.size() > 0) {
            ArrayList<ProfileEmailMapping> arrayListDbEmailIds = tableProfileEmailMapping
                    .getProfileEmailMappingFromEmailId(arrayListEmails.toArray(new
                            String[arrayListEmails.size()]));

            String displayEmailId, displayName;
            boolean isRcp;
            if (arrayListDbEmailIds.size() > 0) {
                displayEmailId = arrayListDbEmailIds.get(0).getEpmEmailId();
              *//*  String displayNamePmId = arrayListDbEmailIds.get(0).getEpmCloudPmId();
                holder.relativeRowAllContact.setTag(displayNamePmId);*//*

                if (arrayListDbEmailIds.size() == 1) {

                    String displayNamePmId = arrayListDbEmailIds.get(0).getEpmCloudPmId();
                    holder.relativeRowAllContact.setTag(displayNamePmId);

                    UserProfile userProfile = tableProfileMaster.getProfileFromCloudPmId(Integer
                            .parseInt(displayNamePmId));

                    holder.linearRating.setVisibility(View.VISIBLE);
                    holder.buttonInvite.setVisibility(View.GONE);
                    holder.imageSocialMedia.setVisibility(View.VISIBLE);
                    holder.textRatingUserCount.setText(userProfile.getTotalProfileRateUser());
                    holder.ratingUser.setRating(Float.parseFloat(userProfile.getProfileRating()));

                    displayName = ((userProfile.getPmFirstName().length() > 0 || userProfile
                            .getPmLastName().length() > 0) ? " (" + userProfile
                            .getPmFirstName() + " " + userProfile
                            .getPmLastName() + ")" : "");

                } else {

                    holder.linearRating.setVisibility(View.GONE);
                    holder.buttonInvite.setVisibility(View.VISIBLE);
                    holder.imageSocialMedia.setVisibility(View.GONE);
                    populateEmailRecyclerView(holder.recyclerViewMultipleRc, arrayListDbEmailIds,
                            contactDisplayName, profileData.getLocalPhoneBookId());

                    String[] pmIds = new String[arrayListDbEmailIds.size()];
                    for (int i = 0; i < arrayListDbEmailIds.size(); i++) {
                        pmIds[i] = arrayListDbEmailIds.get(i).getEpmCloudPmId();
                    }
                    holder.relativeRowAllContact.setTag(StringUtils.join(pmIds, ","));

                    displayName = " (" + arrayListDbEmailIds.size() + "RC)";
                }

                if (StringUtils.equals(displayName, (" (" + contactDisplayName + ")"))) {
                    holder.textCloudContactName.setVisibility(View.GONE);
                    holder.textContactName.setTextColor(colorPineGreen);
                } else {
                    holder.textCloudContactName.setVisibility(View.VISIBLE);

                    if (position == 1) {
                        holder.textContactName.setTextColor(colorPineGreen);
                    } else {
                        holder.textContactName.setTextColor(colorBlack);
                    }
                }

//                holder.textContactNumber.setTextColor(colorPineGreen);
                isRcp = true;
                holder.linearRating.setVisibility(View.VISIBLE);
                holder.buttonInvite.setVisibility(View.GONE);
                holder.imageSocialMedia.setVisibility(View.VISIBLE);
            } else {

                if (position == 1 && fragment instanceof AllContactsFragment) {
                    holder.linearRating.setVisibility(View.VISIBLE);
                    holder.buttonInvite.setVisibility(View.GONE);
                    holder.imageSocialMedia.setVisibility(View.VISIBLE);
                    holder.relativeRowAllContact.setTag("0");
                } else {
                    holder.linearRating.setVisibility(View.GONE);
                    holder.buttonInvite.setVisibility(View.VISIBLE);
                    holder.imageSocialMedia.setVisibility(View.GONE);
                    holder.relativeRowAllContact.setTag("-1");
                }
                displayEmailId = profileData.getOperation().get(0).getPbEmailId().get(0)
                        .getEmEmailId();
                displayName = "";
//                holder.textContactNumber.setTextColor(colorBlack);
                isRcp = false;
            *//* Display mobile number if Email Id is not rcp *//*
                if (displayNumber != null) {
                    displayEmailId = displayNumber;
                }

            }
            holder.textCloudContactName.setText(displayName);
             *//*holder.textContactNumber.setText(displayEmailId);*//*
            if (displayNumber != null) {
                holder.textContactNumber.setText(displayNumber);
            } else {
                if (isRcp || (position == 1 && fragment instanceof AllContactsFragment)) {
                    holder.textContactNumber.setTextColor(colorPineGreen);
                } else {
                    holder.textContactNumber.setTextColor(colorBlack);
                }
                holder.textContactNumber.setText(displayEmailId);
            }

        }*/

    }

    private void showBottomSheet() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout
                .layout_bottom_sheet, null);
        RecyclerView recyclerViewShare = ButterKnife.findById(view, R.id.recycler_view_share);
        TextView textSheetHeader = ButterKnife.findById(view, R.id.text_sheet_header);

        textSheetHeader.setText(context.getString(R.string.social_media));
        textSheetHeader.setTypeface(Utils.typefaceBold(context));

        BottomSheetSocialMediaAdapter adapter = new BottomSheetSocialMediaAdapter(context);

        recyclerViewShare.setLayoutManager(gridLayoutManager);
        recyclerViewShare.setAdapter(adapter);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    public int getListClickedPosition() {
        return listClickedPosition;
    }

    private void selectContactDialog(String contactName,
                                     final ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers,
                                     ArrayList<ProfileDataOperationEmail> emailIds) {

        final ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(context.getString(R.string.str_all));
        arrayList.addAll(phoneNumbers);
        arrayList.addAll(emailIds);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_all_organization);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        final LinearLayout relativeRootDialogList = (LinearLayout) dialog.findViewById(R.id
                .relative_root_dialog_list);
        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText(String.format(Locale.getDefault(), "%s%s", context.getString(R.string.str_invite),
                contactName));
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(context));

        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(context));
        buttonRight.setText(R.string.action_cancel);
        buttonLeft.setTypeface(Utils.typefaceRegular(context));
        buttonLeft.setText(context.getString(R.string.str_invite));

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });


        RecyclerView recyclerViewDialogList = (RecyclerView) dialog.findViewById(R.id
                .recycler_view_dialog_list);
        recyclerViewDialogList.setLayoutManager(new LinearLayoutManager(context));

        final PhoneBookContactDetailAdapter adapter = new PhoneBookContactDetailAdapter(context,
                arrayList);
        recyclerViewDialogList.setAdapter(adapter);

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (adapter.getArrayListSelectedContacts().size() > 0) {
                    dialog.dismiss();
                    ArrayList<String> numbers = new ArrayList<>();
                    ArrayList<String> emails = new ArrayList<>();
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (adapter.getArrayListSelectedContacts().contains(i)) {
                            if (arrayList.get(i) instanceof ProfileDataOperationPhoneNumber) {
                                ProfileDataOperationPhoneNumber number =
                                        (ProfileDataOperationPhoneNumber) arrayList.get(i);
                                numbers.add(number.getPhoneNumber());
                            }
                            if (arrayList.get(i) instanceof ProfileDataOperationEmail) {
                                ProfileDataOperationEmail email = (ProfileDataOperationEmail)
                                        arrayList.get(i);
                                emails.add(email.getEmEmailId());
                            }
                        }
                    }
                    inviteContact(numbers, emails);
                } else {
                    Utils.showErrorSnackBar(context, relativeRootDialogList, context.getString(R.string.please_select_one));
                }
            }
        });

        dialog.show();
    }

    private void initSwipe(RecyclerView recyclerView) {
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
                String actionNumber = StringUtils.defaultString(((ContactListExpandAdapter
                        .AllContactViewHolder) viewHolder).textContactNumber.getText()
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
                    context.startActivity(smsIntent);

                } else {
                    if (fragment instanceof AllContactsListFragment) {
                        ((AllContactsListFragment) fragment).callNumber = actionNumber;
                    } else if (fragment instanceof FavoritesFragment) {
                        ((FavoritesFragment) fragment).callNumber = actionNumber;
                    }
                    showCallConfirmationDialog();
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (expandableAdapter != null)
                            expandableAdapter.notifyDataSetChanged();
                    }
                }, 1500);
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
               /* if (viewHolder instanceof AllContactAdapter.ContactHeaderViewHolder || viewHolder
                        instanceof AllContactAdapter.ContactFooterViewHolder) {
                    return 0;
                }
                if (viewHolder instanceof AllContactAdapter.AllContactViewHolder) {
                    if (((AllContactAdapter.AllContactViewHolder) viewHolder)
                            .recyclerViewMultipleRc.getVisibility() == View.VISIBLE) {
                        return 0;
                    }
                }*/
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
                        p.setColor(ContextCompat.getColor(context, R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(context, R.color.brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(context.getResources(), R.drawable
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
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showCallConfirmationDialog() {

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
                        if (ContextCompat.checkSelfPermission(context, android.Manifest
                                .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            fragment.requestPermissions(new String[]{Manifest.permission
                                    .CALL_PHONE}, AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL);
                        } else {
                           /* Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                    callNumber));
                            startActivity(intent);*/
                            if (fragment instanceof AllContactsListFragment) {
                                Utils.callIntent(context, ((AllContactsListFragment) fragment)
                                        .callNumber);
                            } else if (fragment instanceof FavoritesFragment) {
                                Utils.callIntent(context, ((FavoritesFragment) fragment)
                                        .callNumber);
                            }
                        }
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(context, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(context.getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(context.getString(R.string.action_call));
        if (fragment instanceof AllContactsListFragment) {
            callConfirmationDialog.setDialogBody(context.getString(R.string.action_call)
                    + " " + ((AllContactsListFragment) fragment).callNumber + "?");
        } else if (fragment instanceof FavoritesFragment) {
            callConfirmationDialog.setDialogBody(context.getString(R.string.action_call)
                    + " " + ((FavoritesFragment) fragment).callNumber + "?");
        }

        callConfirmationDialog.showDialog();

    }

    //</editor-fold>

    //<editor-fold desc="View Holders">

    public class AllContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.text_contact_name)
        TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.text_contact_number)
        public TextView textContactNumber;
        @BindView(R.id.divider_all_contact)
        View dividerAllContact;
        @BindView(R.id.relative_row_all_contact)
        RelativeLayout relativeRowAllContact;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.recycler_view_multiple_rc)
        public RecyclerView recyclerViewMultipleRc;
        @BindView(R.id.button_invite)
        Button buttonInvite;
        @BindView(R.id.ripple_invite)
        RippleView rippleInvite;


        AllContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(context));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(context));
            textContactNumber.setTypeface(Utils.typefaceRegular(context));
            textRatingUserCount.setTypeface(Utils.typefaceRegular(context));

            textContactName.setTextColor(colorBlack);
            textContactNumber.setTextColor(colorBlack);

            textCloudContactName.setTextColor(colorPineGreen);

            recyclerViewMultipleRc.setVisibility(View.GONE);
            linearRating.setVisibility(View.GONE);
            imageSocialMedia.setVisibility(View.GONE);
//            buttonInvite.setVisibility(View.GONE);

            Utils.setRoundedCornerBackground(buttonInvite, ContextCompat.getColor(context, R
                    .color.colorAccent), 5, 0, ContextCompat.getColor(context, R.color
                    .colorAccent));

            LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
            // Filled stars
            Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(context, R
                    .color.vivid_yellow));
            // half stars
            Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(context,
                    android.R.color.darker_gray));
            // Empty stars
            Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(context,
                    android.R.color.darker_gray));

//            textRatingUserCount.setText("0");

            /*textContactName.setMaxWidth(Utils.getDeviceWidth(context) / 2);
            textCloudContactName.setMaxWidth(Utils.getDeviceWidth(context) / 2);*/

            initSwipe(recyclerViewMultipleRc);

        }
    }

    public class ContactHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_header)
        TextView textHeader;

        ContactHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textHeader.setTypeface(Utils.typefaceSemiBold(context));

        }
    }

    public class ContactFooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_total_contacts)
        TextView textTotalContacts;

        ContactFooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textTotalContacts.setTypeface(Utils.typefaceSemiBold(context));

        }
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void inviteContact(ArrayList<String> arrayListContactNumber, ArrayList<String>
            arrayListEmail) {

        WsRequestObject inviteContactObject = new WsRequestObject();
        inviteContactObject.setArrayListContactNumber(arrayListContactNumber);
        inviteContactObject.setArrayListEmailAddress(arrayListEmail);

        if (Utils.isNetworkAvailable(context)) {
            if (fragment != null) {
                new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                        inviteContactObject, null, WsResponseObject.class, WsConstants
                        .REQ_SEND_INVITATION, null, true).execute
                        (WsConstants.WS_ROOT + WsConstants.REQ_SEND_INVITATION);
            } else {
                if (context instanceof SearchActivity) {
                    new AsyncWebServiceCall(context, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                            inviteContactObject, null, WsResponseObject.class, WsConstants
                            .REQ_SEND_INVITATION, null, true).execute
                            (WsConstants.WS_ROOT + WsConstants.REQ_SEND_INVITATION);
                }

            }

        }
        /*else {
            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }


    // Filter Class
    public void filter(String charText) {
        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(charText);
        if (matcher1.find()) {
            arrayListUserContact.clear();
            if (charText.length() == 0) {
                arrayListUserContact.addAll(arraylist);
            } else {
                for (int i = 0; i < arraylist.size(); i++) {
                    if (arraylist.get(i) instanceof ProfileData) {
                        ProfileData profileData = (ProfileData) arraylist.get(i);
                        if (!TextUtils.isEmpty(profileData.getTempNumber())) {
                            if (profileData.getTempNumber().contains(charText)) {
                                arrayListUserContact.add(profileData);
                            }
                        }

                    }
                }
            }

        } else {
            charText = charText.toLowerCase(Locale.getDefault());
            arrayListUserContact.clear();
            if (charText.length() == 0) {
                arrayListUserContact.addAll(arraylist);
            } else {

                for (int i = 0; i < arraylist.size(); i++) {
                    if (arraylist.get(i) instanceof ProfileData) {
                        ProfileData profileData = (ProfileData) arraylist.get(i);
                        if (!TextUtils.isEmpty(profileData.getName())) {
                            if (profileData.getName().toLowerCase(Locale.getDefault()).contains
                                    (charText)) {
                                arrayListUserContact.add(profileData);
                            }
                        }

                    }
                }
            }
        }

        setSearchCount(arrayListUserContact.size());
        notifyDataSetChanged();
    }

    //</editor-fold>
}
