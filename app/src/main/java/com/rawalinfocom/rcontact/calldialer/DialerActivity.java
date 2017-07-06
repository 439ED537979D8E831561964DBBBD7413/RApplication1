package com.rawalinfocom.rcontact.calldialer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.calldialer.transition.ScaleTransition;
import com.rawalinfocom.rcontact.calllog.TelephonyInfo;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 08/04/2017
 */
public class DialerActivity extends Activity {

    View mCallButton;
    @BindView(R.id.num_pad_bg_to_animate)
    View numPadBgToAnimate;
    @BindView(R.id.image_button_add_to_contact)
    ImageButton imageButtonAddToContact;
    @BindView(R.id.text_add_to_contact)
    TextView textAddToContact;
    @BindView(R.id.linear_add_to_contact)
    LinearLayout linearAddToContact;
    @BindView(R.id.image_button_clear)
    ImageButton imageButtonClear;
    @BindView(R.id.edit_text_number)
    EditText editTextNumber;
    @BindView(R.id.linear_number)
    RelativeLayout linearNumber;
    @BindView(R.id.view_key_pad_separater)
    View viewKeyPadSeparater;
    @BindView(R.id.btn_1)
    TextView btn1;
    @BindView(R.id.linear1)
    LinearLayout linear1;
    @BindView(R.id.btn_2)
    TextView btn2;
    @BindView(R.id.linear2)
    LinearLayout linear2;
    @BindView(R.id.btn_3)
    TextView btn3;
    @BindView(R.id.linear3)
    LinearLayout linear3;
    @BindView(R.id.btn_4)
    TextView btn4;
    @BindView(R.id.linear4)
    LinearLayout linear4;
    @BindView(R.id.btn_5)
    TextView btn5;
    @BindView(R.id.linear5)
    LinearLayout linear5;
    @BindView(R.id.btn_6)
    TextView btn6;
    @BindView(R.id.linear6)
    LinearLayout linear6;
    @BindView(R.id.btn_7)
    TextView btn7;
    @BindView(R.id.linear7)
    LinearLayout linear7;
    @BindView(R.id.btn_8)
    TextView btn8;
    @BindView(R.id.linear8)
    LinearLayout linear8;
    @BindView(R.id.btn_9)
    TextView btn9;
    @BindView(R.id.linear9)
    LinearLayout linear9;
    @BindView(R.id.btn_stafr)
    TextView btnStafr;
    @BindView(R.id.linear_star)
    LinearLayout linearStar;
    @BindView(R.id.btn_0)
    TextView btn0;
    @BindView(R.id.linear0)
    LinearLayout linear0;
    @BindView(R.id.btn_hashTag)
    TextView btnHashTag;
    @BindView(R.id.linearHashTag)
    LinearLayout linearHashTag;
    @BindView(R.id.image_btn_message)
    ImageButton imageBtnMessage;
    @BindView(R.id.call_button)
    ImageButton callButton;
    @BindView(R.id.image_btn_close_drawer)
    ImageButton imageBtnCloseDrawer;
    @BindView(R.id.number_pad)
    LinearLayout numberPad;
    MaterialDialog callConfirmationDialog;
    boolean isCalledOnce = false;

    Animation slideDownAnimation;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.text_temp_number)
    TextView textTempNumber;
    @BindView(R.id.text_contact_name)
    TextView textContactName;
    @BindView(R.id.text_cloud_contact_name)
    TextView textCloudContactName;
    @BindView(R.id.text_contact_number)
    TextView textContactNumber;
    @BindView(R.id.linear_content_main)
    LinearLayout linearContentMain;
    @BindView(R.id.relative_contact)
    RelativeLayout relativeContact;

    String numberToCall;
    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG};
    MaterialDialog permissionConfirmationDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        ButterKnife.bind(this);

        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_animation);

        editTextNumber.setCursorVisible(false);
        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
                if (s.length() == 11) {
                    String number = s.toString();
                    if (!TextUtils.isEmpty(number))
                        showContactDetail(number);
                } else if (s.length() == 13) {
                    String number = s.toString();
                    if (!TextUtils.isEmpty(number))
                        showContactDetail(number);
                } else if (s.length() == 10) {
                    String number = s.toString();
                    if (!TextUtils.isEmpty(number))
                        showContactDetail(number);
                } else if (s.length() == 0) {
                    showContactDetail(s.toString());
                }  else if (s.length() > 11) {
                    editTextNumber.setTextSize(getResources().getDimension(R.dimen.text_size_14sp));
                } else if (s.length() < 11) {
                    editTextNumber.setTextSize(getResources().getDimension(R.dimen.text_size_25sp));
                }/*else if (s.length() >= 0) {
                    showContactDetail(s.toString());
                }*/

            }
        });
        initandClickEvents();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(makeEnterTransition());
            getWindow().setReturnTransition(makeReturnTransition());
        }
    }

    @SuppressLint("NewApi")
    private Transition makeReturnTransition() {
        // Return a set here, we could add as many transition animations here as we want
        TransitionSet enterTransition = new TransitionSet();

        Transition slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(300);

        slide.excludeTarget(getActionBarView(), true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.setInterpolator(new AccelerateInterpolator());

        enterTransition.addTransition(slide);
        enterTransition.setOrdering(TransitionSet.ORDERING_TOGETHER);

        return enterTransition;
    }

    @SuppressLint("NewApi")
    private Transition makeEnterTransition() {
        // Return a set here, we could add as many transition animations here as we want
        TransitionSet enterTransition = new TransitionSet();

        Transition slide = new Slide(Gravity.BOTTOM);
        slide.setDuration(300);

        slide.excludeTarget(getActionBarView(), true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.setInterpolator(new DecelerateInterpolator());
        enterTransition.addTransition(slide);

        // Exclude call button from the slide
        slide.excludeTarget(mCallButton, true);

        // Give the slide button a scale transition
        Transition scale = new ScaleTransition();
        scale.addTarget(mCallButton);
        enterTransition.addTransition(scale);

        // Set the ordering to sequential so that this happens after the slide
        enterTransition.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);

        return enterTransition;
    }

    private View getActionBarView() {
        final int actionBarId = getResources().getIdentifier("action_bar_container", "id",
                "android");
        final View decor = getWindow().getDecorView();
        return decor.findViewById(actionBarId);
    }

    private void initandClickEvents() {


        linearAddToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberToSave = editTextNumber.getText().toString();
                Utils.addToContact(DialerActivity.this, numberToSave);

            }
        });

        imageBtnCloseDrawer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_down_animation);
                }

            }
        });

        imageButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCalledOnce = false;
                int length = editTextNumber.getText().length();
                int positionFrom = editTextNumber.getSelectionEnd();
//                Log.e("Dialer cursor position", positionFrom+"");
                if (positionFrom > 0) {
                    editTextNumber.getText().delete(positionFrom - 1, positionFrom);
                } else {
                    editTextNumber.setCursorVisible(false);
                }
            }
        });

        imageButtonClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isCalledOnce = false;
                editTextNumber.getText().clear();
                return true;
            }
        });

        linear0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "+");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "+");
                }

//                inputNumberValidation();
                return true;
            }
        });

        linear0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "0");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "0");
                }
                //                inputNumberValidation();
            }
        });

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "1");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "1");
                }
//                inputNumberValidation();

            }
        });

        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "2");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "2");
                }
//                inputNumberValidation();
            }
        });

        linear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "3");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "3");
                }
//                inputNumberValidation();
            }
        });

        linear4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "4");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "4");
                }
//                inputNumberValidation();

            }
        });
        linear5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "5");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "5");
                }
//                inputNumberValidation();
            }
        });

        linear6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "6");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "6");
                }
//                inputNumberValidation();
            }
        });

        linear7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "7");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "7");
                }
//                inputNumberValidation();

            }
        });

        linear8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "8");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "8");
                }
//                inputNumberValidation();

            }
        });

        linear9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "9");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "9");
                }
//                inputNumberValidation();

            }
        });

        linearStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "*");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "*");
                }
//                inputNumberValidation();

            }
        });

        linearHashTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int positionFrom = editTextNumber.getSelectionStart(); //this is to get the the
                // cursor position
                if (positionFrom >= 0) {
                    editTextNumber.getText().insert(positionFrom, "#");
                } else {
                    editTextNumber.setText(editTextNumber.getText().toString() + "#");
                }
//                inputNumberValidation();

            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(DialerActivity.this);
                    if (telephonyInfo != null) {
                        String simSerialNumber = telephonyInfo.simSerialNumber;
                        if (!StringUtils.isEmpty(simSerialNumber)) {
                            String numberToCall = editTextNumber.getText().toString();
                            if (!TextUtils.isEmpty(numberToCall))
                                showCallConfirmationDialog(numberToCall);
                        } else {
                            Toast.makeText(DialerActivity.this, getString(R.string.str_no_sim),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageBtnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberToSendMessage = editTextNumber.getText().toString();
                if (!TextUtils.isEmpty(numberToSendMessage)) {
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + numberToSendMessage));
                    startActivity(smsIntent);
                }
            }
        });

        editTextNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.requestFocus();
                int positionFrom = editTextNumber.getSelectionEnd();
               /* if(positionFrom==0)
                    editTextNumber.setCursorVisible(false);
                else
                    editTextNumber.setCursorVisible(true);*/
                editTextNumber.setCursorVisible(true);
                String mainText = editTextNumber.getText().toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextNumber.getWindowToken(), 0);
            }
        });
    }

    private void inputNumberValidation() {
        if (!isCalledOnce) {
            isCalledOnce = true;
            if (editTextNumber != null && editTextNumber.getText().toString().length() > 0) {
                String stringText = editTextNumber.getText().toString();
                String firstChar = stringText.substring(0, 1);
                if (firstChar.equalsIgnoreCase("0")) {
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(11);
                    editTextNumber.setFilters(FilterArray);
                } else if (firstChar.equalsIgnoreCase("+")) {
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(13);
                    editTextNumber.setFilters(FilterArray);
                } else {
                    InputFilter[] FilterArray = new InputFilter[1];
                    FilterArray[0] = new InputFilter.LengthFilter(10);
                    editTextNumber.setFilters(FilterArray);
                }
            }
        }
    }

    private void showContactDetail(final String number) {
        if (!TextUtils.isEmpty(number)) {
            String name = getNameFromNumber(number);
            if (!TextUtils.isEmpty(name)) {
                relativeContact.setVisibility(View.VISIBLE);
                linearAddToContact.setVisibility(View.GONE);
                textContactName.setText(name);
                textContactNumber.setText(number);
                String imageUrl = getPhotoUrlFromNumber(number);
                if (!TextUtils.isEmpty(imageUrl)) {
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.home_screen_profile)
                            .error(R.drawable.home_screen_profile)
                            .bitmapTransform(new CropCircleTransformation(this))
                            .override(200, 200)
                            .into(imageProfile);

                } else {
                    imageProfile.setImageResource(R.drawable.home_screen_profile);
                }
                relativeContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS,
                                    number);
                        } else {
                            showCallConfirmationDialog(number);
                        }
                    }
                });

            } else {
                relativeContact.setVisibility(View.GONE);
                linearAddToContact.setVisibility(View.VISIBLE);
            }
        } else {
            relativeContact.setVisibility(View.GONE);
            linearAddToContact.setVisibility(View.VISIBLE);
        }

    }


    // A method to check if a permission is granted then execute tasks depending on that
    // particular permission
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissionToExecute(String permissions[], int requestCode, String number) {

        boolean logs = ContextCompat.checkSelfPermission(this, permissions[0]) !=
                PackageManager.PERMISSION_GRANTED;
        if (logs) {
            numberToCall = number;
            requestPermissions(permissions, requestCode);
        } else {
            showCallConfirmationDialog(number);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppConstants.READ_LOGS && permissions[0].equals(Manifest.permission
                .READ_CALL_LOG)) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                if (!TextUtils.isEmpty(numberToCall))
                    showCallConfirmationDialog(numberToCall);
            } else {
                showPermissionConfirmationDialog();
            }
        }
    }

    private void showPermissionConfirmationDialog() {
        RippleView.OnRippleCompleteListener cancelListener = new RippleView
                .OnRippleCompleteListener() {

            @Override
            public void onComplete(RippleView rippleView) {
                switch (rippleView.getId()) {
                    case R.id.rippleLeft:
                        permissionConfirmationDialog.dismissDialog();
                        finish();
                        break;

                    case R.id.rippleRight:
                        permissionConfirmationDialog.dismissDialog();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                }
            }
        };

        permissionConfirmationDialog = new MaterialDialog(DialerActivity.this, cancelListener);
        permissionConfirmationDialog.setTitleVisibility(View.GONE);
        permissionConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
        permissionConfirmationDialog.setRightButtonText(getString(R.string.action_ok));
        permissionConfirmationDialog.setDialogBody(getString(R.string.call_log_permission));

        permissionConfirmationDialog.showDialog();
    }

    private void showCallConfirmationDialog(final String number) {
//        final String formattedNumber = Utils.getFormattedNumber(DialerActivity.this, number);
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
                       /* String unicodeNumber = number.replace("*", Uri.encode("*")).replace("#",
                                Uri.encode("#"));
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                unicodeNumber));
                        startActivity(intent);*/
                        Utils.callIntent(DialerActivity.this, number);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(DialerActivity.this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
        callConfirmationDialog.setRightButtonText(getString(R.string.action_call));
        callConfirmationDialog.setDialogBody(getString(R.string.action_call) + " " + number + " ?");
        callConfirmationDialog.showDialog();
    }

    private String getPhotoUrlFromNumber(String phoneNumber) {
        String photoThumbUrl = "";
        try {
            photoThumbUrl = "";
            ContentResolver contentResolver = this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    /*String contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));*/
                    photoThumbUrl = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.PHOTO_THUMBNAIL_URI));
//                Log.d("LocalPBId", "contactMatch id: " + numberId + " of " + contactName);
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoThumbUrl;
    }

    private String getNameFromNumber(String phoneNumber) {
        String contactName = "", numberId;
        try {

            contactName = "";
            ContentResolver contentResolver = this.getContentResolver();

            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri
                    .encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.LOOKUP_KEY};
            Cursor cursor =
                    contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    /*contactName = cursor.getString(cursor.getColumnIndexOrThrow
                            (ContactsContract.PhoneLookup.DISPLAY_NAME));*/
                    numberId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract
                            .PhoneLookup.LOOKUP_KEY));
                    Uri uri1 = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection1 = new String[]{
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    };

                    String selection = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?";
                    String[] selectionArgs = new String[]{numberId};

                    Cursor cursor1 = getContentResolver().query(uri1, projection1, selection,
                            selectionArgs, null);
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            contactName = cursor1.getString(cursor1.getColumnIndexOrThrow
                                    (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        }
                        cursor1.close();
                    }
                }
                cursor.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactName;
    }
}
