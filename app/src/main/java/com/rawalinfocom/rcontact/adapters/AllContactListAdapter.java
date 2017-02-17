package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.contacts.AllContactsFragment;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.TableProfileEmailMapping;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.ProfileEmailMapping;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class AllContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
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

    private final int HEADER = 0, CONTACT = 1;

    private int colorBlack, colorPineGreen;
    private int previousPosition = 0;
    private int listClickedPosition = -1;

    private TableProfileMaster tableProfileMaster;
    private TableProfileMobileMapping tableProfileMobileMapping;
    private TableProfileEmailMapping tableProfileEmailMapping;

    private ArrayList<Integer> arrayListExpandedPositions;


    //<editor-fold desc="Constructor">
    public AllContactListAdapter(Fragment fragment, ArrayList<Object> arrayListUserContact,
                                 ArrayList<String> arrayListContactHeader) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.arrayListUserContact = arrayListUserContact;
        this.arrayListContactHeader = arrayListContactHeader;
        this.arrayListContactHeader = new ArrayList<>();
        this.arrayListContactHeader.add("#");
        this.arrayListContactHeader.addAll(arrayListContactHeader);

        arrayListExpandedPositions = new ArrayList<>();

        colorBlack = ContextCompat.getColor(context, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);

        tableProfileMaster = new TableProfileMaster(((BaseActivity) context).databaseHandler);
        tableProfileMobileMapping = new TableProfileMobileMapping(((BaseActivity) context)
                .databaseHandler);
        tableProfileEmailMapping = new TableProfileEmailMapping(((BaseActivity) context)
                .databaseHandler);
    }
    //</editor-fold>

    //<editor-fold desc="Override Methods">

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
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (arrayListUserContact.get(position) instanceof ProfileData) {
            return CONTACT;
        } else if (arrayListUserContact.get(position) instanceof String) {
            return HEADER;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return arrayListUserContact.size();
    }

    /**
     * Section Indexer
     */

    @Override
    public Object[] getSections() {
        return arrayListContactHeader.toArray(new String[arrayListContactHeader.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= arrayListUserContact.size()) {
            position = arrayListUserContact.size() - 1;
        }

        if (arrayListUserContact.get(position) instanceof String) {
            String letter = (String) arrayListUserContact.get(position);
            previousPosition = arrayListContactHeader.indexOf(letter);

        } else {
            /*for (int i = position; i < arrayListUserContact.size(); i++) {
                if (arrayListUserContact.get(i) instanceof String) {
                    String letter = (String) arrayListUserContact.get(i);
                    previousPosition = arrayListContactHeader.indexOf(letter);
                    break;
                }
            }*/
            for (int i = position; i >= 0; i--) {
                if (arrayListUserContact.get(i) instanceof String) {
                    String letter = (String) arrayListUserContact.get(i);
                    previousPosition = arrayListContactHeader.indexOf(letter);
                    break;
                }
            }
        }

        return previousPosition;
    }

    //</editor-fold>

    //<editor-fold desc="Private Methods">

    private void configureAllContactViewHolder(final AllContactViewHolder holder, final int
            position) {
        final ProfileData profileData = (ProfileData) arrayListUserContact.get(position);

        holder.textContactName.setTag(position);
        if (arrayListExpandedPositions.contains(position)) {
            holder.recyclerViewMultipleRc.setVisibility(View.VISIBLE);
        } else {
            holder.recyclerViewMultipleRc.setVisibility(View.GONE);
        }

        String contactDisplayName = "";
        String prefix = profileData.getOperation().get(0).getPbNamePrefix();
        String firstName = profileData.getOperation().get(0).getPbNameFirst();
        String lastName = profileData.getOperation().get(0).getPbNameLast();
        String middleName = profileData.getOperation().get(0).getPbNameMiddle();
        String suffix = profileData.getOperation().get(0).getPbNameSuffix();

        if (StringUtils.length(prefix) > 0) {
            contactDisplayName = prefix + " ";
        } if (StringUtils.length(firstName) > 0) {
            contactDisplayName = contactDisplayName + firstName + " ";
        } if (StringUtils.length(middleName) > 0) {
            contactDisplayName = contactDisplayName + middleName + " ";
        } if (StringUtils.length(lastName) > 0) {
            contactDisplayName = contactDisplayName + lastName + " ";
        } if (StringUtils.length(suffix) > 0) {
            contactDisplayName = contactDisplayName + suffix;
        }
        contactDisplayName = StringUtils.trimToEmpty(contactDisplayName);

        holder.textContactName.setText(contactDisplayName.length() > 0 ? contactDisplayName :
                "[Unknown]");
        holder.textCloudContactName.setText("");
        holder.textContactNumber.setText("");
        holder.linearRating.setVisibility(View.GONE);
        holder.buttonInvite.setVisibility(View.VISIBLE);
        holder.imageSocialMedia.setVisibility(View.GONE);

        if (profileData.getOperation().get(0).getPbPhoneNumber().size() > 0) {
            displayNumber(holder, profileData, contactDisplayName, position);
        } else if (!Utils.isArraylistNullOrEmpty(profileData.getOperation().get(0)
                .getPbEmailId()) && profileData.getOperation().get(0).getPbEmailId().size() > 0) {
            displayEmail(holder, profileData, null, contactDisplayName, position);
        }

        /* Hide Divider if row is last in Section */
        if ((position + 1) < arrayListUserContact.size()) {
            if (arrayListUserContact.get(position + 1) instanceof String) {
                holder.dividerAllContact.setVisibility(View.GONE);
            } else {
                holder.dividerAllContact.setVisibility(View.VISIBLE);
            }
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
                  /*  TextView textName = (TextView) view.findViewById(R.id.text_contact_name);
                    TextView textCloudName = (TextView) view.findViewById(R.id
                            .text_cloud_contact_name);*/

                    bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText().toString
                            ());
                    listClickedPosition = (int) textName.getTag();
                    if (textCloudName.getVisibility() == View.VISIBLE) {
                        bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, textCloudName
                                .getText().toString());
                    }
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
                    selectContactDialog(profileData.getOperation().get(0).getPbNameFirst(),
                            phoneNumbers, emailIds);
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

    }

    private void configureHeaderViewHolder(ContactHeaderViewHolder holder, int position) {
        String letter = (String) arrayListUserContact.get(position);
        holder.textHeader.setText(letter);
    }

    private void displayNumber(AllContactViewHolder holder, ProfileData profileData, String
            contactDisplayName, int position) {

        ArrayList<String> arrayListMobileNumbers = new ArrayList<>();
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
            holder.imageSocialMedia.setVisibility(View.VISIBLE);

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
        }

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

        ArrayList<String> arrayListEmails = new ArrayList<>();
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
              /*  String displayNamePmId = arrayListDbEmailIds.get(0).getEpmCloudPmId();
                holder.relativeRowAllContact.setTag(displayNamePmId);*/

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
            /* Display mobile number if Email Id is not rcp */
                if (displayNumber != null) {
                    displayEmailId = displayNumber;
                }

            }
            holder.textCloudContactName.setText(displayName);
             /*holder.textContactNumber.setText(displayEmailId);*/
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

        }

    }

    private void showBottomSheet() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout
                .layout_bottom_sheet, null);
        RecyclerView recyclerViewShare = ButterKnife.findById(view, R.id.recycler_view_share);
        TextView textSheetHeader = ButterKnife.findById(view, R.id.text_sheet_header);

        textSheetHeader.setText("Social Media");
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
                                     ArrayList<ProfileDataOperationPhoneNumber> phoneNumbers,
                                     ArrayList<ProfileDataOperationEmail> emailIds) {

        final ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add("All");
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

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        textDialogTitle.setText("Invite " + contactName);
        textDialogTitle.setTypeface(Utils.typefaceSemiBold(context));

        Button buttonRight = (Button) dialog.findViewById(R.id.button_right);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.ripple_right);
        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);

        buttonRight.setTypeface(Utils.typefaceRegular(context));
        buttonRight.setText(R.string.action_cancel);
        buttonLeft.setTypeface(Utils.typefaceRegular(context));
        buttonLeft.setText("Invite");

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
                            ProfileDataOperationEmail email = (ProfileDataOperationEmail) arrayList
                                    .get(i);
                            emails.add(email.getEmEmailId());
                        }
                    }
                }
                inviteContact(numbers, emails);
            }
        });

        dialog.show();
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
        RecyclerView recyclerViewMultipleRc;
        @BindView(R.id.button_invite)
        Button buttonInvite;

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

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

    private void inviteContact(ArrayList<String> arrayListContactNumber, ArrayList<String>
            arrayListEmail) {

        WsRequestObject inviteContactObject = new WsRequestObject();
        inviteContactObject.setArrayListContactNumber(arrayListContactNumber);
        inviteContactObject.setArrayListEmailAddress(arrayListEmail);

        if (Utils.isNetworkAvailable(context)) {
            new AsyncWebServiceCall(fragment, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    inviteContactObject, null, WsResponseObject.class, WsConstants
                    .REQ_SEND_INVITATION, null, true).execute
                    (WsConstants.WS_ROOT + WsConstants.REQ_SEND_INVITATION);
        }
        /*else {
            Utils.showErrorSnackBar(getActivity(), relativeRootAllContacts, getResources()
                    .getString(R.string.msg_no_network));
        }*/
    }

    //</editor-fold>

}
