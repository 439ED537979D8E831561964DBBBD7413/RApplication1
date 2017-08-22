package com.rawalinfocom.rcontact.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rawalinfocom.rcontact.calldialer.DialerActivity;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.AllContactsListFragment;
import com.rawalinfocom.rcontact.contacts.FavoritesFragment;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.database.QueryManager;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RecyclerItemDecoration;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.ProfileData;

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

    private Activity activity;
    private Fragment fragment;
    /* phone book contacts */
    private ArrayList<Object> arrayListUserContact;
    private ArrayList<String> arrayListContactHeader;

    private final int HEADER = 0, CONTACT = 1, FOOTER = 2;
    private int colorBlack, colorPineGreen;
    private int listClickedPosition = -1;

    private ArrayList<Integer> arrayListExpandedPositions;

    private ArrayList<Integer> mSectionPositions;
    private ArrayList<Object> arraylist;
    private int searchCount;
    private MaterialDialog callConfirmationDialog;
    private ContactListExpandAdapter expandableAdapter;

    //<editor-fold desc="Constructor">

    public AllContactAdapter(Fragment fragment, ArrayList<Object> arrayListUserContact,
                             ArrayList<String> arrayListContactHeader) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.arrayListUserContact = arrayListUserContact;
        this.arrayListContactHeader = arrayListContactHeader;

        arrayListExpandedPositions = new ArrayList<>();

        colorBlack = ContextCompat.getColor(activity, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(activity, R.color.colorAccent);

    }

    public AllContactAdapter(Activity activity, ArrayList<Object> arrayListUserContact) {
        this.activity = activity;
        this.arrayListUserContact = new ArrayList<>();
        this.arrayListUserContact.addAll(arrayListUserContact);

        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(arrayListUserContact);

        arrayListExpandedPositions = new ArrayList<>();

        colorBlack = ContextCompat.getColor(activity, R.color.colorBlack);
        colorPineGreen = ContextCompat.getColor(activity, R.color.colorAccent);
    }

    //</editor-fold>

    //<editor-fold desc="Override Methods">

    public int getSearchCount() {
        return searchCount;
    }

    private void setSearchCount(int searchCount) {
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
        if (position == arrayListUserContact.size() && !(activity instanceof SearchActivity)
                && !(activity instanceof DialerActivity)) {
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
        if (!(activity instanceof SearchActivity) && !(activity instanceof DialerActivity)) {
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
            /*if (fragment instanceof AllContactsListFragment) {
                startPosition = 2;
            } else {*/
            startPosition = 0;
//            }
        }

        for (int i = startPosition, size = arrayListUserContact.size(); i < size; i++) {
            if (arrayListUserContact.get(i) instanceof ProfileData) {
                String contactDisplayName;
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
                contactDisplayName = /*StringUtils.trimToEmpty(contactDisplayName);*/StringUtils
                        .defaultIfEmpty(profileData.getName(), "");
                if (contactDisplayName == null || contactDisplayName.length() == 0) {
                    String section = "#";
                    if (!sections.contains(section)) {
                        sections.add(section);
                        mSectionPositions.add(i);
                    }
                } else {
                    int start = contactDisplayName.charAt(0);
                    if (('a' <= start && start <= 'z') || ('A' <= start && start <= 'Z')) {
                        String section = String.valueOf(contactDisplayName.charAt(0)).toUpperCase();
                        if (!sections.contains(section)) {
                            sections.add(section);
                            mSectionPositions.add(i);
                        }
                    } else {
                        String section = "#";
                        if (!sections.contains(section)) {
                            sections.add(section);
                            mSectionPositions.add(i);
                        }
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
        if (StringUtils.length(thumbnailUrl) > 0) {
            Glide.with(activity)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .override(300, 300)
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

        boolean showPineGreen = false;

        String contactDisplayName = "";

        if (Utils.getStringPreference(activity, AppConstants.PREF_SHORT_BY_CONTACT, "0")
                .equalsIgnoreCase("0")) {
            contactDisplayName = StringUtils.defaultIfEmpty(profileData.getName(), "");
        } else {

            String firstName = profileData.getTempFirstName();
            String lastName = profileData.getTempLastName();
            if (StringUtils.length(lastName) > 0) {
                contactDisplayName = contactDisplayName + lastName + " ";
            }
            if (StringUtils.length(firstName) > 0) {
                contactDisplayName = contactDisplayName + firstName + " ";
            }
        }

        holder.textContactName.setText(contactDisplayName.length() > 0 ? contactDisplayName :
                activity.getString(R.string.unknown));

        if (profileData.getTempIsRcp()) {
            holder.textCloudContactName.setVisibility(View.VISIBLE);
            if (contactDisplayName.equalsIgnoreCase(profileData.getTempRcpName())) {
                holder.textCloudContactName.setVisibility(View.GONE);
                holder.textCloudContactName.setText("");
                showPineGreen = true;
            } else {
                holder.textCloudContactName.setVisibility(View.VISIBLE);
                if (!StringUtils.isEmpty(profileData.getTempRcpName())) {
                    holder.textCloudContactName.setText(" (" + profileData.getTempRcpName() + ")");
                    showPineGreen = false;
                }
            }

            Glide.with(activity)
                    .load(profileData.getTempRcpImageURL())
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(activity))
                    .override(300, 300)
                    .into(holder.imageProfile);

            holder.relativeRowAllContact.setTag(profileData.getTempRcpId());

            if (StringUtils.contains(profileData.getTempRcpName(), ",")) {
                holder.relativeRowAllContact.setTag(profileData.getTempRcpName());
                holder.textCloudContactName.setText(" (" + String.valueOf(StringUtils.countMatches
                        (profileData.getTempRcpName(), ",") + 1) + " RC)");
            }

        } else {
            showPineGreen = false;
            holder.relativeRowAllContact.setTag("-1");
            holder.textCloudContactName.setVisibility(View.GONE);
            holder.textCloudContactName.setText("");
        }

        /*if (fragment instanceof AllContactsListFragment && position == 1) {
            holder.textContactName.setTextColor(colorPineGreen);
            holder.textContactNumber.setTextColor(colorPineGreen);
            holder.textCloudContactName.setVisibility(View.GONE);
            holder.textCloudContactName.setText("");
        } else {*/
        if (showPineGreen) {
            holder.textContactName.setTextColor(colorPineGreen);
        } else {
            holder.textContactName.setTextColor(colorBlack);
        }
        holder.textContactNumber.setTextColor(colorBlack);
        holder.textCloudContactName.setVisibility(View.VISIBLE);
//        }

        holder.textContactNumber.setText(Utils.getFormattedNumber(activity, profileData
                .getTempNumber()));

        //<editor-fold desc="relativeRowAllContact Click">
        holder.relativeRowAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                TextView textName = (TextView) v.findViewById(R.id.text_contact_name);
                TextView textCloudName = (TextView) v.findViewById(R.id.text_cloud_contact_name);

                bundle.putBoolean(AppConstants.EXTRA_IS_RCP_USER, profileData.getTempIsRcp());
                bundle.putString(AppConstants.EXTRA_CALL_HISTORY_NAME, profileData.getName());

                if (holder.recyclerViewMultipleRc.getVisibility() == View.GONE) {
                    if (StringUtils.contains(String.valueOf(v.getTag()), ",")) {
                        // Multiple RCP
                        holder.recyclerViewMultipleRc.setVisibility(View.VISIBLE);
                        QueryManager queryManager = new QueryManager(((BaseActivity) activity)
                                .databaseHandler);
                        ArrayList<ProfileData> arrayList = new ArrayList<>();
                        arrayList.addAll(queryManager.getRcpNumberName(String.valueOf(v.getTag())));
                        holder.recyclerViewMultipleRc.setLayoutManager(new LinearLayoutManager
                                (activity));
                        expandableAdapter = new ContactListExpandAdapter(activity,
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

                        bundle.putString(AppConstants.EXTRA_CONTACT_NAME, textName.getText()
                                .toString());
//                        bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, thumbnailUrl);
                        if (StringUtils.length(profileData.getTempRcpImageURL()) > 0) {
                            bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, profileData
                                    .getTempRcpImageURL());
                        } else {
                            bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, thumbnailUrl);
                        }
                        listClickedPosition = (int) textName.getTag();
                        bundle.putInt(AppConstants.EXTRA_CONTACT_POSITION, listClickedPosition);
                        if (textCloudName.getVisibility() == View.VISIBLE) {
                            bundle.putString(AppConstants.EXTRA_CLOUD_CONTACT_NAME, textCloudName
                                    .getText().toString());
                        }
                        if (fragment instanceof AllContactsListFragment) {
                            Intent intent = new Intent(activity, ProfileDetailActivity.class);
                            intent.putExtras(bundle);
                            fragment.startActivityForResult(intent, AppConstants
                                    .REQUEST_CODE_PROFILE_DETAIL);
                            activity.overridePendingTransition(R.anim.enter, R
                                    .anim.exit);
                        } else if (fragment instanceof FavoritesFragment) {
                            Intent intent = new Intent(activity, ProfileDetailActivity.class);
                            bundle.putBoolean(AppConstants.EXTRA_IS_FROM_FAVOURITE, true);
                            intent.putExtras(bundle);
                            fragment.startActivityForResult(intent, AppConstants
                                    .REQUEST_CODE_PROFILE_DETAIL);
                            activity.overridePendingTransition(R.anim.enter, R
                                    .anim.exit);
                        } else {
                            if (activity instanceof SearchActivity) {
                                Intent intent = new Intent(activity, ProfileDetailActivity.class);
                                intent.putExtras(bundle);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.enter, R
                                        .anim.exit);
                            } else if (activity instanceof DialerActivity) {
                                Intent intent = new Intent(activity, ProfileDetailActivity.class);
                                intent.putExtras(bundle);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.enter, R
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

    }

    private void configureHeaderViewHolder(ContactHeaderViewHolder holder, int position) {
        String letter = (String) arrayListUserContact.get(position);
        holder.textHeader.setText(letter);
    }

    private void configureFooterViewHolder(ContactFooterViewHolder holder) {
        if (fragment instanceof AllContactsListFragment) {
            holder.textTotalContacts.setText(String.format(Locale.getDefault(), "%d %s",
                    arrayListUserContact.size(), activity.getString(R.string.str_count_contacts)));
        } else if (fragment instanceof FavoritesFragment) {
            holder.textTotalContacts.setText(String.format(Locale.getDefault(), "%d %s",
                    arrayListUserContact.size() -
                            arrayListContactHeader.size(), activity.getString(R.string.str_count_contacts)));

        }
    }

    public int getListClickedPosition() {
        return listClickedPosition;
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
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    activity.startActivity(smsIntent);

                } else {
                    if (fragment instanceof AllContactsListFragment) {
                        ((AllContactsListFragment) fragment).callNumber = actionNumber;
                    } else if (activity instanceof SearchActivity) {
                        ((SearchActivity) activity).numberToSend = actionNumber;
                    } else if (fragment instanceof FavoritesFragment) {
                        ((FavoritesFragment) fragment).callNumber = actionNumber;
                    }

                    if (ContextCompat.checkSelfPermission(activity, android.Manifest
                            .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        fragment.requestPermissions(new String[]{Manifest.permission
                                .CALL_PHONE}, AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL);
                    } else {
                        if (fragment instanceof AllContactsListFragment) {
                            Utils.callIntent(activity, ((AllContactsListFragment) fragment)
                                    .callNumber);
                        } else if (activity instanceof SearchActivity) {
                            Utils.callIntent(activity, ((SearchActivity) activity)
                                    .numberToSend);
                        } else if (fragment instanceof FavoritesFragment) {
                            Utils.callIntent(activity, ((FavoritesFragment) fragment)
                                    .callNumber);
                        }
                    }

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
                        p.setColor(ContextCompat.getColor(activity, R.color
                                .darkModerateLimeGreen));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView
                                .getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable
                                .ic_action_call);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float)
                                itemView.getTop() + width, (float) itemView.getLeft() + 2 *
                                width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(ContextCompat.getColor(activity, R.color.brightOrange));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float)
                                itemView.getTop(), (float) itemView.getRight(), (float) itemView
                                .getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(activity.getResources(), R.drawable
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

//    private void showCallConfirmationDialog() {
//
//        RippleView.OnRippleCompleteListener cancelListener = new RippleView
//                .OnRippleCompleteListener() {
//
//            @Override
//            public void onComplete(RippleView rippleView) {
//                switch (rippleView.getId()) {
//                    case R.id.rippleLeft:
//                        callConfirmationDialog.dismissDialog();
//                        break;
//
//                    case R.id.rippleRight:
//                        callConfirmationDialog.dismissDialog();
//                        if (ContextCompat.checkSelfPermission(activity, android.Manifest
//                                .permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            fragment.requestPermissions(new String[]{Manifest.permission
//                                    .CALL_PHONE}, AppConstants.MY_PERMISSIONS_REQUEST_PHONE_CALL);
//                        } else {
//                            if (fragment instanceof AllContactsListFragment) {
//                                Utils.callIntent(activity, ((AllContactsListFragment) fragment)
//                                        .callNumber);
//                            } else if (activity instanceof SearchActivity) {
//                                Utils.callIntent(activity, ((SearchActivity) activity)
//                                        .numberToSend);
//                            } else if (fragment instanceof FavoritesFragment) {
//                                Utils.callIntent(activity, ((FavoritesFragment) fragment)
//                                        .callNumber);
//                            }
//                        }
//                        break;
//                }
//            }
//        };
//
//        callConfirmationDialog = new MaterialDialog(activity, cancelListener);
//        callConfirmationDialog.setTitleVisibility(View.GONE);
//        callConfirmationDialog.setLeftButtonText(activity.getString(R.string.action_cancel));
//        callConfirmationDialog.setRightButtonText(activity.getString(R.string.action_call));
//        if (fragment instanceof AllContactsListFragment) {
//            callConfirmationDialog.setDialogBody(activity.getString(R.string.action_call)
//                    + " " + ((AllContactsListFragment) fragment).callNumber + "?");
//        } else if (fragment instanceof FavoritesFragment) {
//            callConfirmationDialog.setDialogBody(activity.getString(R.string.action_call)
//                    + " " + ((FavoritesFragment) fragment).callNumber + "?");
//        } else if (activity instanceof SearchActivity) {
//            callConfirmationDialog.setDialogBody(activity.getString(R.string.action_call)
//                    + " " + ((SearchActivity) activity).numberToSend + "?");
//        }
//
//        callConfirmationDialog.showDialog();
//
//    }

    //</editor-fold>

    //<editor-fold desc="View Holders">

    public class AllContactViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.text_contact_name)
        public TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.text_contact_number)
        public TextView textContactNumber;
        /*@BindView(R.id.divider_all_contact)
        View dividerAllContact;*/
        @BindView(R.id.relative_row_all_contact)
        RelativeLayout relativeRowAllContact;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.recycler_view_multiple_rc)
        public RecyclerView recyclerViewMultipleRc;

        AllContactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textContactName.setTypeface(Utils.typefaceSemiBold(activity));
            textCloudContactName.setTypeface(Utils.typefaceSemiBold(activity));
            textContactNumber.setTypeface(Utils.typefaceRegular(activity));
            textRatingUserCount.setTypeface(Utils.typefaceRegular(activity));

            textContactName.setTextColor(colorBlack);
            textContactNumber.setTextColor(colorBlack);

            textCloudContactName.setTextColor(colorPineGreen);

            recyclerViewMultipleRc.setVisibility(View.GONE);
            linearRating.setVisibility(View.GONE);

            RecyclerItemDecoration decoration = new RecyclerItemDecoration(activity, ContextCompat
                    .getColor(activity, R.color.lightGrey), 0.5f);
            recyclerViewMultipleRc.addItemDecoration(decoration);

          /*  Utils.setRoundedCornerBackground(buttonInvite, ContextCompat.getColor(activity, R
                    .color.colorAccent), 5, 0, ContextCompat.getColor(activity, R.color
                    .colorAccent));*/

            LayerDrawable stars = (LayerDrawable) ratingUser.getProgressDrawable();
            // Filled stars
            Utils.setRatingStarColor(stars.getDrawable(2), ContextCompat.getColor(activity, R
                    .color.vivid_yellow));
            // half stars
            Utils.setRatingStarColor(stars.getDrawable(1), ContextCompat.getColor(activity,
                    android.R.color.darker_gray));
            // Empty stars
            Utils.setRatingStarColor(stars.getDrawable(0), ContextCompat.getColor(activity,
                    android.R.color.darker_gray));

            initSwipe(recyclerViewMultipleRc);

        }
    }

    public class ContactHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_header)
        TextView textHeader;

        ContactHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textHeader.setTypeface(Utils.typefaceSemiBold(activity));

        }
    }

    public class ContactFooterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_total_contacts)
        TextView textTotalContacts;

        ContactFooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textTotalContacts.setTypeface(Utils.typefaceSemiBold(activity));

        }
    }

    //</editor-fold>

    //<editor-fold desc="Web Service Call">

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
                        charText = charText.trim();
                        ProfileData profileData = (ProfileData) arraylist.get(i);
                        if (!StringUtils.isEmpty(profileData.getTempNumber())) {
                            String number = profileData.getTempNumber();
                            number = number.replace(" ", "").replace("-", "");
                            if (number.contains(charText)) {
                                arrayListUserContact.add(profileData);
                            }
                        }
                    }
                }
            }
        } else {
            charText = charText.toLowerCase(Locale.getDefault());
            charText = charText.trim();
            arrayListUserContact.clear();
            if (charText.length() == 0) {
                arrayListUserContact.addAll(arraylist);
            } else {

                for (int i = 0; i < arraylist.size(); i++) {
                    if (arraylist.get(i) instanceof ProfileData) {
                        ProfileData profileData = (ProfileData) arraylist.get(i);
                        if (!StringUtils.isEmpty(profileData.getName())) {
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
