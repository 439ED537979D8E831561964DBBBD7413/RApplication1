package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.ProfileDetailActivity;
import com.rawalinfocom.rcontact.helper.FlipAnimator;
import com.rawalinfocom.rcontact.helper.MaterialListDialog;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.CallLogType;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 01/05/17.
 */

public class SimpleCallLogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<CallLogType> arrayListCallLogs;
    private ArrayList<String> arrayListForKnownContact;
    private ArrayList<String> arrayListForUnknownContact;
    private MaterialListDialog materialListDialog;
    private int selectedPosition = 0;
    private Activity mActivity;
    private CallLogType selectedCallLogData;
    private long selectedLogDate = 0;
    private long dateFromReceiver;
    private String formattedNumber = "";
    ArrayList<CallLogType> arrayList;
    private int searchCount;

    private SimpleCallLogListAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    private ArrayList<CallLogType> arrayListToDelete;
    private String searchChar;

    public ArrayList<CallLogType> getArrayListToDelete() {
        return arrayListToDelete;
    }

    public void setArrayListToDelete(ArrayList<CallLogType> arrayListToDelete) {
        this.arrayListToDelete = arrayListToDelete;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public CallLogType getSelectedCallLogData() {
        return selectedCallLogData;
    }

    public long getSelectedLogDate() {
        return selectedLogDate;
    }

    public int getSearchCount() {
        return searchCount;
    }

    private void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    public ArrayList<CallLogType> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<CallLogType> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<CallLogType> getArrayListCallLogs() {
        return arrayListCallLogs;
    }

    private void setArrayListCallLogs(ArrayList<CallLogType> arrayListCallLogs) {
        this.arrayListCallLogs = arrayListCallLogs;
    }

    public SimpleCallLogListAdapter(Activity activity, ArrayList<CallLogType> callLogTypes) {
        this.mActivity = activity;
//        this.arrayListCallLogs = arraylistCallLogs;
        if (AppConstants.isFromSearchActivity) {
            this.arrayListCallLogs = new ArrayList<>();
            this.arrayListCallLogs.addAll(callLogTypes);
        } else {
            this.arrayListCallLogs = callLogTypes;
        }
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(arrayListCallLogs);
    }

    public SimpleCallLogListAdapter(Activity activity, ArrayList<CallLogType> callLogTypes,
                                    SimpleCallLogListAdapterListener listener) {
        this.mActivity = activity;
//        this.arrayListCallLogs = arraylistCallLogs;
        if (AppConstants.isFromSearchActivity) {
            this.arrayListCallLogs = new ArrayList<>();
            this.arrayListCallLogs.addAll(callLogTypes);
        } else {
            this.arrayListCallLogs = callLogTypes;
            this.listener = listener;
            selectedItems = new SparseBooleanArray();
            animationItemsIndex = new SparseBooleanArray();
            arrayListToDelete = new ArrayList<>();
        }
        this.arrayList = new ArrayList<>();
        this.arrayList.addAll(arrayListCallLogs);

    }

    @Override
    public CallLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_call_log_list,
                parent, false);
        return new CallLogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder contactViewHolder, final int position) {
        CallLogViewHolder holder = (CallLogViewHolder) contactViewHolder;
        final CallLogType callLogType = arrayListCallLogs.get(position);
        final String name = callLogType.getName();
        final String number = callLogType.getNumber();

        // change the row state to activated
        if(selectedItems != null )
            holder.itemView.setActivated(selectedItems.get(position, false));

        if (!StringUtils.isEmpty(number)) {
            formattedNumber = Utils.getFormattedNumber(mActivity, number);
        }
        final String uniqueRowID = callLogType.getUniqueContactId();

        if (!StringUtils.isEmpty(formattedNumber)) {
            holder.textTempNumber.setText(formattedNumber);
        }
        if (!StringUtils.isEmpty(name)) {
            holder.textContactName.setTypeface(Utils.typefaceBold(mActivity));
            holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                    .colorBlack));
            if (!StringUtils.isEmpty(callLogType.getSpamCount())) {
                holder.imageViewSpam.setVisibility(View.GONE);
                holder.textSpamCount.setVisibility(View.GONE);
                if (!StringUtils.equalsIgnoreCase(callLogType.getSpamCount(), "0"))
                    holder.textSpamCount.setText(callLogType.getSpamCount());
                else {
                    holder.imageViewSpam.setVisibility(View.GONE);
                    holder.textSpamCount.setVisibility(View.GONE);
                }

            } else {
                holder.imageViewSpam.setVisibility(View.GONE);
                holder.textSpamCount.setVisibility(View.GONE);
            }

            if (MoreObjects.firstNonNull(callLogType.isRcpUser(), false)) {
                String contactDisplayName = "";
                String firstName = callLogType.getRcpFirstName();
                String lastName = callLogType.getRcpLastName();

                if (StringUtils.length(firstName) > 0) {
                    contactDisplayName = contactDisplayName + firstName + " ";
                }

                if (StringUtils.length(lastName) > 0) {
                    contactDisplayName = contactDisplayName + lastName + "";
                }

                if (StringUtils.equalsIgnoreCase(name, contactDisplayName)) {
                    holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                            .colorAccent));
                    holder.textCloudContactName.setVisibility(View.GONE);
                    holder.textContactName.setText(name);
                } else {
                    holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                            .colorBlack));
                    holder.textCloudContactName.setVisibility(View.VISIBLE);
                    holder.textCloudContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                            .colorAccent));
                    holder.textContactName.setText(String.format("%s ", name));
                    if (!StringUtils.isEmpty(contactDisplayName))
                        holder.textCloudContactName.setText("(" + contactDisplayName + ")");
                    else
                        holder.textCloudContactName.setText("");

                }

            } else {
                holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                        .colorBlack));
                holder.textCloudContactName.setVisibility(View.GONE);
                holder.textContactName.setText(name);
            }
            Pattern numberPat = Pattern.compile("\\d+");
            Matcher matcher1 = numberPat.matcher(name);
            if (StringUtils.containsOnly(name, "\\d+")) {
                holder.textContactNumber.setText(mActivity.getString(R.string.str_unsaved));
            } else {
                holder.textContactNumber.setText(String.format("%s,", formattedNumber));
            }

        } else {
            holder.textCloudContactName.setVisibility(View.GONE);
            if (!StringUtils.isEmpty(number)) {
                if (!MoreObjects.firstNonNull(callLogType.isRcpUser(), false)) {
                    if (StringUtils.equalsIgnoreCase(callLogType.getIsRcpVerfied(), "0")) {
                        holder.textContactName.setTypeface(Utils.typefaceBold(mActivity));
                        holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                                .textColorBlue));
                        holder.textContactName.setText(formattedNumber);
                        holder.textContactNumber.setText(mActivity.getString(R.string.str_unsaved));
                        String contactNameToDisplay = "";
                        String prefix = callLogType.getPrefix();
                        String suffix = callLogType.getSuffix();
                        String firstName = callLogType.getRcpFirstName();
                        String middleName = callLogType.getMiddleName();
                        String lastName = callLogType.getRcpLastName();

                        if (StringUtils.length(prefix) > 0)
                            contactNameToDisplay = contactNameToDisplay + prefix + " ";
                        if (StringUtils.length(suffix) > 0)
                            contactNameToDisplay = contactNameToDisplay + suffix + " ";
                        if (StringUtils.length(firstName) > 0)
                            contactNameToDisplay = contactNameToDisplay + firstName + " ";
                        if (StringUtils.length(middleName) > 0)
                            contactNameToDisplay = contactNameToDisplay + middleName + " ";
                        if (StringUtils.length(lastName) > 0)
                            contactNameToDisplay = contactNameToDisplay + lastName + "";

                        if (!StringUtils.isEmpty(contactNameToDisplay)) {
                            holder.textCloudContactName.setVisibility(View.VISIBLE);
                            holder.textCloudContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                                    .textColorBlue));
                            holder.textCloudContactName.setText(" (" + contactNameToDisplay + ")");
                        } else {
                            holder.textCloudContactName.setVisibility(View.GONE);
                        }


                        if (!StringUtils.isEmpty(callLogType.getSpamCount())) {
                            holder.imageViewSpam.setVisibility(View.VISIBLE);
                            holder.textSpamCount.setVisibility(View.VISIBLE);
                            if (!StringUtils.equalsIgnoreCase(callLogType.getSpamCount(), "0")) {
                                holder.textSpamCount.setText(callLogType.getSpamCount());
                            } else {
                                holder.imageViewSpam.setVisibility(View.GONE);
                                holder.textSpamCount.setVisibility(View.GONE);
                            }

                        } else {
                            holder.imageViewSpam.setVisibility(View.GONE);
                            holder.textSpamCount.setVisibility(View.GONE);
                        }

                    } else if (StringUtils.equalsIgnoreCase(callLogType.getIsRcpVerfied(), "1")) {
                        holder.textContactName.setTypeface(Utils.typefaceBold(mActivity));
                        holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                                .colorAccent));
                        holder.textContactName.setText(formattedNumber);
                        holder.textContactNumber.setText(mActivity.getString(R.string.str_unsaved));

                        String contactNameToDisplay = "";
                        String prefix = callLogType.getPrefix();
                        String suffix = callLogType.getSuffix();
                        String firstName = callLogType.getRcpFirstName();
                        String middleName = callLogType.getMiddleName();
                        String lastName = callLogType.getRcpLastName();

                        if (StringUtils.length(prefix) > 0)
                            contactNameToDisplay = contactNameToDisplay + prefix + " ";
                        if (StringUtils.length(suffix) > 0)
                            contactNameToDisplay = contactNameToDisplay + suffix + " ";
                        if (StringUtils.length(firstName) > 0)
                            contactNameToDisplay = contactNameToDisplay + firstName + " ";
                        if (StringUtils.length(middleName) > 0)
                            contactNameToDisplay = contactNameToDisplay + middleName + " ";
                        if (StringUtils.length(lastName) > 0)
                            contactNameToDisplay = contactNameToDisplay + lastName + "";

                        if (!StringUtils.isEmpty(contactNameToDisplay)) {
                            holder.textCloudContactName.setVisibility(View.VISIBLE);
                            holder.textCloudContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                                    .colorAccent));
                            holder.textCloudContactName.setText(" (" + contactNameToDisplay + ")");
                        } else {
                            holder.textCloudContactName.setVisibility(View.GONE);
                        }

                        if (!StringUtils.isEmpty(callLogType.getSpamCount())) {
                            holder.imageViewSpam.setVisibility(View.VISIBLE);
                            holder.textSpamCount.setVisibility(View.VISIBLE);
                            if (!StringUtils.equalsIgnoreCase(callLogType.getSpamCount(), "0")) {
                                holder.textSpamCount.setText(callLogType.getSpamCount());
                            } else {
                                holder.imageViewSpam.setVisibility(View.GONE);
                                holder.textSpamCount.setVisibility(View.GONE);
                            }
                        } else {
                            holder.imageViewSpam.setVisibility(View.GONE);
                            holder.textSpamCount.setVisibility(View.GONE);
                        }
                    } else {
                        holder.textContactName.setTypeface(Utils.typefaceBold(mActivity));
                        holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                                .colorBlack));
                        holder.textContactName.setText(formattedNumber);
                        holder.textContactNumber.setText(mActivity.getString(R.string.str_unsaved));
                        if (!StringUtils.isEmpty(callLogType.getSpamCount())) {
                            holder.imageViewSpam.setVisibility(View.VISIBLE);
                            holder.textSpamCount.setVisibility(View.VISIBLE);
                            holder.textSpamCount.setText(callLogType.getSpamCount());
                        } else {
                            holder.imageViewSpam.setVisibility(View.GONE);
                            holder.textSpamCount.setVisibility(View.GONE);
                        }
                    }
                }
                if (!StringUtils.equalsIgnoreCase(callLogType.getSpamCount(), "0")) {
                    holder.textSpamCount.setText(callLogType.getSpamCount());
                } else {
                    holder.imageViewSpam.setVisibility(View.GONE);
                    holder.textSpamCount.setVisibility(View.GONE);
                }
                /*holder.textContactName.setTypeface(Utils.typefaceBold(mActivity));
                holder.textContactName.setTextColor(ContextCompat.getColor(mActivity, R.color
                        .colorBlack));
                holder.textContactName.setText(formattedNumber);
                holder.textContactNumber.setText(mActivity.getString(R.string.str_unsaved));*/
            } else {
                holder.textContactName.setText(" ");
            }
        }

        if(!StringUtils.isBlank(searchChar)){
            Pattern numberPat = Pattern.compile("\\d+");
            Matcher matcher1 = numberPat.matcher(searchChar);
            if (matcher1.find() || searchChar.matches("[+][0-9]+")) {
                int startPos =  formattedNumber.toLowerCase(Locale.US).indexOf(searchChar
                        .toLowerCase(Locale.US));
                int endPos = startPos + searchChar.length();
                if (startPos != -1) {
                    Spannable spannable = new SpannableString(formattedNumber);
                    ColorStateList blueColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.RED});
                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null);
                    spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.textContactNumber.setText(spannable);
                    holder.textContactName.setText(spannable);
                } else {
                    holder.textContactNumber.setText(formattedNumber);
                }
            }
        }

        final long date = callLogType.getDate();
//        long logDate1 = callLogType.getDate();
        Date date1 = new Date(date);
        String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date1);
//        Log.i("Call Log date", logDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesDate;
        yesDate = cal.getTime();
        String yesterdayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(yesDate);
//        Log.i("Call yesterday date", yesterdayDate);

        Calendar c = Calendar.getInstance();
        Date cDate = c.getTime();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cDate);
//        Log.i("Call Current date", currentDate);
        Date dateFromReceiver1 = callLogType.getCallReceiverDate();
        if (dateFromReceiver1 != null) {
            dateFromReceiver = dateFromReceiver1.getTime();
        }
        String finalDate = "";
        if (date > 0) {
            Date dateCallLog = new Date(date);
            if (logDate.equalsIgnoreCase(currentDate)) {
                finalDate = mActivity.getString(R.string.str_today) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dateCallLog);
            } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                finalDate = mActivity.getString(R.string.str_yesterday) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(dateCallLog);
            } else {
                finalDate = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(dateCallLog);
            }
            holder.textContactDate.setText(finalDate);
        } else {
            Date callDate = callLogType.getCallReceiverDate();
            if (callDate != null) {
                if (logDate.equalsIgnoreCase(currentDate)) {
                    finalDate = mActivity.getString(R.string.str_today) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(callDate);
                } else if (logDate.equalsIgnoreCase(yesterdayDate)) {
                    finalDate = mActivity.getString(R.string.str_yesterday) + ", " + new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(callDate);
                } else {
                    finalDate = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(callDate);
                }
                holder.textContactDate.setText(finalDate);
            }
        }

        int blockedType = callLogType.getBlockedType();

        if (blockedType > 0) {
            holder.imageCallType.setImageResource(R.drawable.ic_block);
        } else {

            int callType = callLogType.getType();
            if (callType > 0) {
                switch (callType) {
                    case AppConstants.INCOMING:
                        holder.imageCallType.setImageResource(R.drawable.ic_call_incoming);
                        break;
                    case AppConstants.OUTGOING:
                        holder.imageCallType.setImageResource(R.drawable.ic_call_outgoing);
                        break;
                    case AppConstants.MISSED:
                        holder.imageCallType.setImageResource(R.drawable.ic_call_missed);
                        break;
                    default:
                        break;

                }
            }
        }

        boolean isDual = AppConstants.isDualSimPhone();
        String simNumber;
        simNumber = callLogType.getCallSimNumber();
        if (isDual) {
            if (!TextUtils.isEmpty(simNumber)) {
                if (simNumber.equalsIgnoreCase("2")) {
                    holder.textSimType.setTextColor(ContextCompat.getColor(mActivity, R.color
                            .darkCyan));
                    holder.textSimType.setText(mActivity.getString(R.string.im_sim_2));
                    holder.textSimType.setTypeface(Utils.typefaceIcons(mActivity));
                } else {
                    holder.textSimType.setTextColor(ContextCompat.getColor(mActivity, R.color
                            .vividBlue));
                    holder.textSimType.setText(mActivity.getString(R.string.im_sim_1));
                    holder.textSimType.setTypeface(Utils.typefaceIcons(mActivity));
                }
            } else {
                holder.textSimType.setVisibility(View.GONE);
            }

        } else {

            holder.textSimType.setVisibility(View.GONE);

        }

        final String thumbnailUrl = callLogType.getProfileImage();
        if (!TextUtils.isEmpty(thumbnailUrl)) {
            Glide.with(mActivity)
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.home_screen_profile)
                    .error(R.drawable.home_screen_profile)
                    .bitmapTransform(new CropCircleTransformation(mActivity))
                    .override(200, 200)
                    .into(holder.imageProfile);

        } else {
            holder.imageProfile.setImageResource(R.drawable.home_screen_profile);
        }

        holder.image3dotsCallLog.setTag(position);
        holder.image3dotsCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = (int) v.getTag();

//                System.out.println("RContact selectedPosition --> " + selectedPosition);

//                selectedCallLogData = callLogType;
                String blockedNumber = "";
                String key = "";
                key = callLogType.getLocalPbRowId();
                if (key.equalsIgnoreCase(" ")) {
                    key = callLogType.getUniqueContactId();
                }

                ArrayList<CallLogType> callLogTypeList = new ArrayList<CallLogType>();
                HashMap<String, ArrayList<CallLogType>> blockProfileHashMapList =
                        Utils.getHashMapPreferenceForBlock(mActivity, AppConstants.PREF_BLOCK_CONTACT_LIST);

                if (blockProfileHashMapList != null && blockProfileHashMapList.size() > 0) {
                    if (blockProfileHashMapList.containsKey(key))
                        callLogTypeList.addAll(blockProfileHashMapList.get(key));

                }
                if (callLogTypeList != null) {
                    for (int j = 0; j < callLogTypeList.size(); j++) {
//                        Log.i("value", callLogTypeList.get(j) + "");
                        String tempNumber = callLogTypeList.get(j).getNumber();
                        if (tempNumber.equalsIgnoreCase(number)) {
                            blockedNumber = tempNumber;
                        }
                    }
                }

                if (!TextUtils.isEmpty(blockedNumber)) {
                    if (!TextUtils.isEmpty(name)) {
                        Pattern numberPat = Pattern.compile("\\d+");
                        Matcher matcher1 = numberPat.matcher(name);
                        if (StringUtils.containsOnly(name, "\\d+")) {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call)
                                            + " " + name, mActivity.getString(R.string.add_to_contact),
                                    mActivity.getString(R.string.add_to_existing_contact)
                                    , mActivity.getString(R.string.send_sms), mActivity.getString(R.string.remove_from_call_log),
                                    mActivity.getString(R.string.copy_phone_number)/*,mActivity.getString(R.string.call_reminder),*/ /*mActivity.getString(R.string.unblock)*/));
                        } else {
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call)
                                            + " " + name, mActivity.getString(R.string.send_sms),
                                    mActivity.getString(R.string.remove_from_call_log), mActivity.getString(R.string.copy_phone_number)/*,
                                    mActivity.getString(R.string.call_reminder), mActivity.getString(R.string.unblock)*/));
                        }

                        materialListDialog = new MaterialListDialog(mActivity, arrayListForKnownContact, number, date, name, uniqueRowID,
                                key);
                        materialListDialog.setDialogTitle(name);
                        materialListDialog.showDialog();

                    } else {
                        if (!TextUtils.isEmpty(number)) {
                            String formatedNumber = Utils.getFormattedNumber(mActivity, number);
                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call)
                                            + " " + formatedNumber, mActivity.getString(R.string.add_to_contact),
                                    mActivity.getString(R.string.add_to_existing_contact)
                                    , mActivity.getString(R.string.send_sms), mActivity.getString(R.string.remove_from_call_log),
                                    mActivity.getString(R.string.copy_phone_number)/*,mActivity.getString(R.string.call_reminder),
                                    mActivity.getString(R.string.unblock)*/));

                            materialListDialog = new MaterialListDialog(mActivity, arrayListForUnknownContact, number, date, "", uniqueRowID,
                                    key);
                            materialListDialog.setDialogTitle(number);
                            materialListDialog.setCallingAdapter(SimpleCallLogListAdapter.this);
                            materialListDialog.showDialog();
                        }
                    }
                } else {

                    if(AppConstants.isFromSearchActivity){
                        arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
                                        (!TextUtils.isEmpty(name) ? (" " + name) : (" " + number)), mActivity.getString(R.string.send_sms),
                                mActivity.getString(R.string.remove_from_call_log), mActivity.getString(R.string.copy_phone_number)));

                        materialListDialog = new MaterialListDialog(mActivity, arrayListForKnownContact, number, date, name, uniqueRowID, "");
                        materialListDialog.setDialogTitle((!TextUtils.isEmpty(name) ? (" " + name) : (" " + number)));
                        materialListDialog.showDialog();
                    }else{
                        if(selectedItems != null && selectedItems.size()<=0){
                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
                                            (!TextUtils.isEmpty(name) ? (" " + name) : (" " + number)), mActivity.getString(R.string.send_sms),
                                    mActivity.getString(R.string.remove_from_call_log), mActivity.getString(R.string.copy_phone_number)));

                            materialListDialog = new MaterialListDialog(mActivity, arrayListForKnownContact, number, date, name, uniqueRowID, "");
                            materialListDialog.setDialogTitle((!TextUtils.isEmpty(name) ? (" " + name) : (" " + number)));
                            materialListDialog.showDialog();
                        }
                    }


//                    if (!TextUtils.isEmpty(name)) {
//                        if (StringUtils.containsOnly(name, "\\d+")) {
//                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
//                                            " " + name, mActivity.getString(R.string.add_to_contact),
//                                    mActivity.getString(R.string.add_to_existing_contact)
//                                    , mActivity.getString(R.string.send_sms), mActivity.getString(R.string.remove_from_call_log),
//                                    mActivity.getString(R.string.copy_phone_number)/*,mActivity.getString(R.string.call_reminder), mActivity.getString(R.string.block)*/));
//
//                        } else {
//                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
//                                            " " + name, mActivity.getString(R.string.send_sms),
//                                    mActivity.getString(R.string.remove_from_call_log), mActivity.getString(R.string.copy_phone_number)
//                                    /*mActivity.getString(R.string.call_reminder), mActivity.getString(R.string.block)*/));
//                        }
//
//                        materialListDialog = new MaterialListDialog(mActivity, arrayListForKnownContact, number, date, name, uniqueRowID, "");
//                        materialListDialog.setDialogTitle(name);
//                        materialListDialog.showDialog();
//
//                    } else {
//                        if (!TextUtils.isEmpty(number)) {
//                            String formatedNumber = Utils.getFormattedNumber(mActivity, number);
//                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
//                                            " " + formatedNumber, mActivity.getString(R.string.add_to_contact),
//                                    mActivity.getString(R.string.add_to_existing_contact)
//                                    , mActivity.getString(R.string.send_sms), mActivity.getString(R.string.remove_from_call_log),
//                                    mActivity.getString(R.string.copy_phone_number)/*,mActivity.getString(R.string.call_reminder), mActivity.getString(R.string.block)*/));
//
//                            materialListDialog = new MaterialListDialog(mActivity, arrayListForUnknownContact, number, date, "", uniqueRowID,
//                                    "");
//                            materialListDialog.setDialogTitle(number);
//                            materialListDialog.setCallingAdapter(SimpleCallLogListAdapter.this);
//                            materialListDialog.showDialog();
//                        }
//                    }if (!TextUtils.isEmpty(name)) {
//                        if (StringUtils.containsOnly(name, "\\d+")) {
//                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
//                                            " " + name, mActivity.getString(R.string.add_to_contact),
//                                    mActivity.getString(R.string.add_to_existing_contact)
//                                    , mActivity.getString(R.string.send_sms), mActivity.getString(R.string.remove_from_call_log),
//                                    mActivity.getString(R.string.copy_phone_number)/*,mActivity.getString(R.string.call_reminder), mActivity.getString(R.string.block)*/));
//
//                        } else {
//                            arrayListForKnownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
//                                            " " + name, mActivity.getString(R.string.send_sms),
//                                    mActivity.getString(R.string.remove_from_call_log), mActivity.getString(R.string.copy_phone_number)
//                                    /*mActivity.getString(R.string.call_reminder), mActivity.getString(R.string.block)*/));
//                        }
//
//                        materialListDialog = new MaterialListDialog(mActivity, arrayListForKnownContact, number, date, name, uniqueRowID, "");
//                        materialListDialog.setDialogTitle(name);
//                        materialListDialog.showDialog();
//
//                    } else {
//                        if (!TextUtils.isEmpty(number)) {
//                            String formatedNumber = Utils.getFormattedNumber(mActivity, number);
//                            arrayListForUnknownContact = new ArrayList<>(Arrays.asList(mActivity.getString(R.string.action_call) +
//                                            " " + formatedNumber, mActivity.getString(R.string.add_to_contact),
//                                    mActivity.getString(R.string.add_to_existing_contact)
//                                    , mActivity.getString(R.string.send_sms), mActivity.getString(R.string.remove_from_call_log),
//                                    mActivity.getString(R.string.copy_phone_number)/*,mActivity.getString(R.string.call_reminder), mActivity.getString(R.string.block)*/));
//
//                            materialListDialog = new MaterialListDialog(mActivity, arrayListForUnknownContact, number, date, "", uniqueRowID,
//                                    "");
//                            materialListDialog.setDialogTitle(number);
//                            materialListDialog.setCallingAdapter(SimpleCallLogListAdapter.this);
//                            materialListDialog.showDialog();
//                        }
//                    }
                }

            }
        });

        holder.relativeRowMain.setTag(position);
        holder.relativeRowMain.setClickable(true);
        holder.relativeRowMain.setEnabled(true);
        holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPosition = (int) v.getTag();
                selectedCallLogData = arrayListCallLogs.get(selectedPosition);
                String key = "";
                key = callLogType.getLocalPbRowId();
                if (key.equalsIgnoreCase(" ")) {
                    key = callLogType.getUniqueContactId();
                }

                Boolean isRcpUser = selectedCallLogData.isRcpUser();
                String firstName = selectedCallLogData.getRcpFirstName();
                String lastName = selectedCallLogData.getRcpLastName();
                String name = selectedCallLogData.getName();
                String cloudName = "";
                String contactDisplayName = "";
                String contactNameToDisplay = "";
                String prefix = selectedCallLogData.getPrefix();
                String suffix = selectedCallLogData.getSuffix();
                String middleName = selectedCallLogData.getMiddleName();

                if (StringUtils.length(prefix) > 0)
                    contactNameToDisplay = contactNameToDisplay + prefix + " ";
                if (StringUtils.length(suffix) > 0)
                    contactNameToDisplay = contactNameToDisplay + suffix + " ";
                if (StringUtils.length(firstName) > 0)
                    contactNameToDisplay = contactNameToDisplay + firstName + " ";
                if (StringUtils.length(middleName) > 0)
                    contactNameToDisplay = contactNameToDisplay + middleName + " ";
                if (StringUtils.length(lastName) > 0)
                    contactNameToDisplay = contactNameToDisplay + lastName + "";


                if (MoreObjects.firstNonNull(isRcpUser, false)) {
                    if (StringUtils.length(firstName) > 0) {
                        contactDisplayName = contactDisplayName + firstName + " ";
                    }
                    if (StringUtils.length(lastName) > 0) {
                        contactDisplayName = contactDisplayName + lastName + "";
                    }
                    if (!StringUtils.equalsIgnoreCase(name, contactDisplayName)) {
                        cloudName = contactDisplayName;
                    }
                }

                if (date == 0) {
                    selectedLogDate = dateFromReceiver;
                } else {
                    selectedLogDate = date;
                }
                AppConstants.isFromReceiver = false;
//                String formatedNumber = Utils.getFormattedNumber(mActivity, number);
                Intent intent = new Intent(mActivity, ProfileDetailActivity.class);
                intent.putExtra(AppConstants.EXTRA_PROFILE_ACTIVITY_CALL_INSTANCE, true);
                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NUMBER, number);

                if (selectedCallLogData.getRcpId() == null)
                    intent.putExtra(AppConstants.EXTRA_PM_ID, "-1");
                else
                    intent.putExtra(AppConstants.EXTRA_PM_ID, selectedCallLogData.getRcpId());

                intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_NAME, name);
                if (date == 0) {
                    intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, dateFromReceiver);
                } else {
                    intent.putExtra(AppConstants.EXTRA_CALL_HISTORY_DATE, date);
                }
                intent.putExtra(AppConstants.EXTRA_RCP_VERIFIED_ID, selectedCallLogData.getIsRcpVerfied());
                intent.putExtra(AppConstants.EXTRA_CALL_UNIQUE_ID, key);
                intent.putExtra(AppConstants.EXTRA_UNIQUE_CONTACT_ID, uniqueRowID);
                intent.putExtra(AppConstants.EXTRA_CONTACT_PROFILE_IMAGE, thumbnailUrl);
                intent.putExtra(AppConstants.EXTRA_IS_RCP_USER, isRcpUser);
                if (!StringUtils.isEmpty(cloudName))
                    intent.putExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME, cloudName);
                else
                    intent.putExtra(AppConstants.EXTRA_CALL_LOG_CLOUD_NAME, contactNameToDisplay);
                mActivity.startActivity(intent);
                ((Activity) mActivity).overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        // handle icon animation
        applyIconAnimation(holder, position);

        // apply click events
        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return arrayListCallLogs.size();
    }

    public class CallLogViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        @BindView(R.id.image_profile)
        ImageView imageProfile;
        @BindView(R.id.image_3dots_call_log)
        ImageView image3dotsCallLog;
        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.text_contact_name)
        public TextView textContactName;
        @BindView(R.id.text_cloud_contact_name)
        TextView textCloudContactName;
        @BindView(R.id.image_call_type)
        ImageView imageCallType;
        @BindView(R.id.text_contact_date)
        TextView textContactDate;
        @BindView(R.id.text_sim_type)
        TextView textSimType;
        @BindView(R.id.text_rating_user_count)
        TextView textRatingUserCount;
        @BindView(R.id.rating_user)
        RatingBar ratingUser;
        @BindView(R.id.linear_rating)
        LinearLayout linearRating;
        @BindView(R.id.linear_content_main)
        LinearLayout linearContentMain;
        @BindView(R.id.relative_row_main)
        public RelativeLayout relativeRowMain;
        @BindView(R.id.textCount)
        TextView textCount;
        @BindView(R.id.text_temp_number)
        public TextView textTempNumber;
        @BindView(R.id.text_contact_number)
        TextView textContactNumber;
        @BindView(R.id.image_view_spam)
        ImageView imageViewSpam;
        @BindView(R.id.text_spam_count)
        TextView textSpamCount;
        @BindView(R.id.icon_back)
        RelativeLayout iconBack;
        @BindView(R.id.rl_imageProfile)
        RelativeLayout rlImageProfile;

        CallLogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textSimType.setVisibility(View.GONE);
            image3dotsCallLog.setVisibility(View.VISIBLE);
//            image3dotsCallLog.setEnabled(true);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    // Filter Class
    public void filter(String charText) {
        Pattern numberPat = Pattern.compile("\\d+");
        Matcher matcher1 = numberPat.matcher(charText);
        if (matcher1.find()) {
            arrayListCallLogs.clear();
            if (charText.length() == 0) {
                arrayListCallLogs.addAll(arrayList);
            } else {
                charText = charText.trim();
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) instanceof CallLogType) {
                        CallLogType profileData = arrayList.get(i);
                        if (!TextUtils.isEmpty(profileData.getNumber())) {
                            if (profileData.getNumber().contains(charText)) {
                                arrayListCallLogs.add(profileData);
                            }
                        }
                        setArrayList(arrayList);
                    }
                }
            }
        }

        if (arrayListCallLogs.size() > 0) {
            setSearchCount(arrayListCallLogs.size());
            setArrayListCallLogs(arrayListCallLogs);
        }

        searchChar =  charText;
        notifyDataSetChanged();
    }


    public interface SimpleCallLogListAdapterListener {
        void onIconClicked(int position);

        void onMessageRowClicked(int position);

        void onRowLongClicked(int position);

    }


    private void applyClickEvents(CallLogViewHolder holder, final int position) {

        if(!AppConstants.isFromSearchActivity){
            holder.rlImageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onIconClicked(position);
                }
            });


            holder.relativeRowMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageRowClicked(position);
                }
            });

            holder.relativeRowMain.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onRowLongClicked(position);
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return true;
                }
            });
        }



    }


    private void applyIconAnimation(CallLogViewHolder holder, int position) {
        if(selectedItems != null){
            if (selectedItems.get(position, false)) {
                holder.rlImageProfile.setVisibility(View.INVISIBLE);
                resetIconYAxis(holder.iconBack);
                holder.iconBack.setVisibility(View.VISIBLE);
                holder.iconBack.setAlpha(1);
                if (currentSelectedIndex == position) {
                    FlipAnimator.flipView(mActivity, holder.iconBack, holder.rlImageProfile, true);
                    resetCurrentIndex();
                }
            } else {
                holder.iconBack.setVisibility(View.GONE);
                resetIconYAxis(holder.rlImageProfile);
                holder.rlImageProfile.setVisibility(View.VISIBLE);
                holder.rlImageProfile.setAlpha(1);
                if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                    FlipAnimator.flipView(mActivity, holder.iconBack, holder.rlImageProfile, false);
                    resetCurrentIndex();
                }
                holder.image3dotsCallLog.setEnabled(true);
            }


        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }


    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if(selectedItems != null){
            if (selectedItems.get(pos, false)) {
                selectedItems.delete(pos);
                animationItemsIndex.delete(pos);
                arrayListToDelete.remove(arrayListCallLogs.get(pos));
                setArrayListToDelete(arrayListToDelete);
            } else {
                selectedItems.put(pos, true);
                animationItemsIndex.put(pos, true);
                arrayListToDelete.add(arrayListCallLogs.get(pos));
                setArrayListToDelete(arrayListToDelete);
            }
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        if(selectedItems != null)
            selectedItems.clear();
        notifyDataSetChanged();
//        mActivity.findViewById(R.id.image_3dots_call_log).setEnabled(true);

    }

    public int getSelectedItemCount() {
        if(selectedItems != null)
            return selectedItems.size();
        else
            return 0;
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        arrayListCallLogs.remove(position);
        resetCurrentIndex();
    }

    public void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

}
