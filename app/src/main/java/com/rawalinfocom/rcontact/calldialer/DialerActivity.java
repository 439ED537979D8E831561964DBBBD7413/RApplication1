package com.rawalinfocom.rcontact.calldialer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.RContactApplication;
import com.rawalinfocom.rcontact.adapters.AllContactAdapter;
import com.rawalinfocom.rcontact.calldialer.transition.ScaleTransition;
import com.rawalinfocom.rcontact.calllog.TelephonyInfo;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.ContactStorageConstants;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileMobileMapping;
import com.rawalinfocom.rcontact.model.UserProfile;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Aniruddh on 08/04/2017
 */
public class DialerActivity extends BaseActivity {

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
    /*@BindView(R.id.image_profile)
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
    LinearLayout linearContentMain;*/
   /* @BindView(R.id.relative_contact)
    RelativeLayout relativeContact;*/
    @BindView(R.id.recycle_view_pb_contact)
    RecyclerView recycleViewPbContact;
    @BindView(R.id.relative_contact)
    RelativeLayout relativeContact;

    String numberToCall;
    @BindView(R.id.text_abc)
    TextView textAbc;
    @BindView(R.id.text_def)
    TextView textDef;
    @BindView(R.id.text_ghi)
    TextView textGhi;
    @BindView(R.id.text_jkl)
    TextView textJkl;
    @BindView(R.id.text_mno)
    TextView textMno;
    @BindView(R.id.text_pqrs)
    TextView textPqrs;
    @BindView(R.id.text_tuv)
    TextView textTuv;
    @BindView(R.id.text_wxyz)
    TextView textWxyz;
    @BindView(R.id.text_plus)
    TextView textPlus;
    //    @BindView(R.id.image_sim_preference)
//    ImageView imageSimPreference;
    private String[] requiredPermissions = {Manifest.permission.READ_CALL_LOG};
    MaterialDialog permissionConfirmationDialog;
    ArrayList<Object> objectArrayListContact;
    RContactApplication rContactApplication;
    AllContactAdapter allContactAdapter;
    private SyncGetContactNumber syncGetContactNumber;
    TableProfileMaster tableProfileMaster;
    TableProfileMobileMapping tableProfileMobileMapping;
    ArrayList<ProfileData> secondaryContactList;
//    boolean isDualSim;
//    int simCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        ButterKnife.bind(this);

        objectArrayListContact = new ArrayList<>();
        secondaryContactList = new ArrayList<>();
        tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
        tableProfileMobileMapping = new TableProfileMobileMapping(getDatabaseHandler());
        syncGetContactNumber = new SyncGetContactNumber();
        syncGetContactNumber.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        rContactApplication = (RContactApplication) getApplicationContext();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleViewPbContact.setLayoutManager(linearLayoutManager);
        populateDataToFilter();
        slideDownAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_animation);

        /*TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(DialerActivity
                .this);
        if (telephonyInfo != null) {
            isDualSim = telephonyInfo.isDualSIM();
            if(isDualSim){
                imageSimPreference.setVisibility(View.GONE);
            }else{
                imageSimPreference.setVisibility(View.GONE);
            }
        }*/

        editTextNumber.setCursorVisible(false);
        editTextNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 11) {
                    editTextNumber.setTextSize(getResources().getDimension(R.dimen.text_size_14sp));
                } else if (s.length() < 11) {
                    editTextNumber.setTextSize(getResources().getDimension(R.dimen.text_size_25sp));
//                    if (allContactAdapter == null) {
                    allContactAdapter = new AllContactAdapter(DialerActivity.this,
                            objectArrayListContact);
//                    }
                }

                String number = s.toString();
                if (number.length() > 0) {
                    showContactDetail(number);
                } /*else {
                    if (number.length() == 0)
                        showContactDetail(number);
                }*/
                /*if (s.length() == 11) {
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
                }*/ /*else if (s.length() >= 0) {
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

    @Override
    protected void onResume() {
        super.onResume();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextNumber.getWindowToken(), 0);
//        editTextNumber.getText().clear();
        /*int length = editTextNumber.getText().length();
        if (length == 0) {
            showContactDetail(editTextNumber.getText().toString());
        }*/
    }

    @Override
    protected void onDestroy() {
        if (syncGetContactNumber != null)
            syncGetContactNumber.cancel(true);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
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

    private class SyncGetContactNumber extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getContactFromSecondaryNumber();
            return null;
        }
    }

    private void initandClickEvents() {

        textAddToContact.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        editTextNumber.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn0.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn1.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn2.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn3.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn4.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn5.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn6.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn7.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn8.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        btn9.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textAbc.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textDef.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textGhi.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textJkl.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textMno.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textPqrs.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textTuv.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textWxyz.setTypeface(Utils.typefaceRegular(DialerActivity.this));
        textPlus.setTypeface(Utils.typefaceRegular(DialerActivity.this));

        linearAddToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberToSave = editTextNumber.getText().toString();
                Utils.addToContact(DialerActivity.this, numberToSave);

            }
        });

        imageButtonAddToContact.setOnClickListener(new View.OnClickListener() {
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

                if (length == 1 && StringUtils.isEmpty(editTextNumber.getText().toString())) {
                    editTextNumber.getText().clear();
                    length = editTextNumber.getText().length();
                }

                if (length == 0) {
                    showContactDetail(editTextNumber.getText().toString());
                }
            }
        });

        imageButtonClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isCalledOnce = false;
                editTextNumber.getText().clear();
                int length = editTextNumber.getText().length();
                if (length == 0) {
                    showContactDetail(editTextNumber.getText().toString());
                }
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

        /*if(isDualSim){
            imageSimPreference.setVisibility(View.GONE);
            if(simCount == 0){
                Utils.setStringPreference(DialerActivity.this,AppConstants.EXTRA_DIALER_SIM_PREFERENCE,"0");
            }
            imageSimPreference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(simCount == 0){
                        imageSimPreference.setImageResource(R.drawable.ico_sim1_svg);
                        simCount = 1;
                        Utils.setStringPreference(DialerActivity.this,AppConstants.EXTRA_DIALER_SIM_PREFERENCE,"1");
                    }else if(simCount == 1){
                        imageSimPreference.setImageResource(R.drawable.ico_sim2_svg);
                        simCount = 2;
                        Utils.setStringPreference(DialerActivity.this,AppConstants.EXTRA_DIALER_SIM_PREFERENCE,"2");
                    }else if(simCount == 2){
                        imageSimPreference.setImageResource(R.drawable.ico_ask_sim_svg);
                        simCount = 0;
                        Utils.setStringPreference(DialerActivity.this,AppConstants.EXTRA_DIALER_SIM_PREFERENCE,"0");
                    }
                }
            });
        }else{
            imageSimPreference.setVisibility(View.GONE);
        }*/

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String numberToCall = editTextNumber.getText().toString();
                    if (!TextUtils.isEmpty(numberToCall)) {
                        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(DialerActivity
                                .this);
                        if (telephonyInfo != null) {
                            boolean isDualSim = telephonyInfo.isDualSIM();
//                            if (!isDualSim) {
//                                imageSimPreference.setVisibility(View.GONE);
                            String simSerialNumber = telephonyInfo.simSerialNumber;
                            if (!StringUtils.isEmpty(simSerialNumber)) {
                                numberToCall = Utils.getFormattedNumber(DialerActivity.this,
                                        numberToCall);
                                Utils.callIntent(DialerActivity.this, numberToCall);
//                                showCallConfirmationDialog(numberToCall);
                            } else {
                                Toast.makeText(DialerActivity.this, getString(R.string.str_no_sim),
                                        Toast.LENGTH_SHORT).show();
                            }
                            /*} else {
                                String callFromSim =  Utils.getStringPreference(DialerActivity.this,
                                        AppConstants.EXTRA_DIALER_SIM_PREFERENCE,"-1");
                                if(StringUtils.equalsIgnoreCase(callFromSim,"1")){
                                    numberToCall = Utils.getFormattedNumber(DialerActivity.this,
                                            numberToCall);
                                    Utils.callIntentWithSimPreference(DialerActivity.this, numberToCall,"0");

                                }else if(StringUtils.equalsIgnoreCase(callFromSim,"2")){

                                    numberToCall = Utils.getFormattedNumber(DialerActivity.this,
                                            numberToCall);
                                    Utils.callIntentWithSimPreference(DialerActivity.this, numberToCall,"1");

                                }else if(StringUtils.equalsIgnoreCase(callFromSim,"0")){
                                    String simSerialNumber = telephonyInfo.simSerialNumber;
                                    if (!StringUtils.isEmpty(simSerialNumber)) {
                                        numberToCall = Utils.getFormattedNumber(DialerActivity.this,
                                                numberToCall);
                                        Utils.callIntent(DialerActivity.this, numberToCall);
                                    } else {
                                        Toast.makeText(DialerActivity.this, getString(R.string.str_no_sim),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }*/

                        }
                    } else {
                        Toast.makeText(DialerActivity.this, getString(R.string.str_no_number),
                                Toast.LENGTH_SHORT).show();
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

    private void populateDataToFilter() {
        if (rContactApplication.getArrayListAllPhoneBookContacts() != null)
            objectArrayListContact.addAll(rContactApplication.getArrayListAllPhoneBookContacts());

        if (objectArrayListContact != null && objectArrayListContact.size() > 0) {
            allContactAdapter = new AllContactAdapter(DialerActivity.this, objectArrayListContact);
        }
    }

    private void getContactFromSecondaryNumber() {
        try {
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = new String[]{
//                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY,
                    ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.RawContacts.ACCOUNT_TYPE,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
            };
            String selection = ContactsContract.CommonDataKinds.Phone.NUMBER /*+ " LIKE '%"+number+"%' "*/
                    + " and " + ContactsContract.RawContacts.ACCOUNT_TYPE
                    + " in (" + ContactStorageConstants.CONTACT_STORAGE + ")";
            String sortOrder = "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC";
//            String[] selectionArg =  {number};
            Cursor cursor = getContentResolver().query(uri, projection, selection, null, sortOrder);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    if (syncGetContactNumber != null && syncGetContactNumber.isCancelled())
                        return;

                    ProfileData profileData = new ProfileData();
                    String contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String contactThumbnail = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

                    if (!StringUtils.isEmpty(contactNumber))
                        profileData.setTempNumber(contactNumber);
                    if (!StringUtils.isEmpty(contactName))
                        profileData.setName(contactName);
                    if (!StringUtils.isEmpty(contactThumbnail))
                        profileData.setProfileUrl(contactThumbnail);

                    String newNumber = contactNumber.replace(" ", "").replace("-", "");
                    String formattedNumber = Utils.getFormattedNumber(this, newNumber);
                    ProfileMobileMapping profileMobileMapping =
                            tableProfileMobileMapping
                                    .getCloudPmIdFromProfileMappingFromNumber(formattedNumber);
                    if (profileMobileMapping != null) {
                        String cloudPmId = profileMobileMapping.getMpmCloudPmId();
                        if (!StringUtils.isEmpty(cloudPmId)) {
                            UserProfile userProfile = tableProfileMaster
                                    .getRCPProfileFromPmId(Integer.parseInt(cloudPmId));

                            String firstName = userProfile.getPmFirstName();
                            String lastName = userProfile.getPmLastName();
                            String rcpId = userProfile.getPmRcpId();
                            String profileImage = userProfile.getPmProfileImage();

                            if (!StringUtils.isEmpty(firstName))
                                profileData.setTempRcpName(firstName + " " + lastName);
                            if (!StringUtils.isEmpty(rcpId)) {
                                profileData.setTempRcpId(rcpId);
                                profileData.setTempIsRcp(true);
                            }
                            if (!StringUtils.isEmpty(profileImage))
                                profileData.setTempRcpImageURL(profileImage);
                        } else {
                            profileData.setTempIsRcp(false);
                        }
                    }

                    secondaryContactList.add(profileData);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showContactDetail(final String number) {
        if (!TextUtils.isEmpty(number)) {
            if (allContactAdapter != null) {
                allContactAdapter.filter(number);

                if (allContactAdapter.getSearchCount() > 0) {
                    relativeContact.setVisibility(View.VISIBLE);
                    linearAddToContact.setVisibility(View.GONE);
                    recycleViewPbContact.setAdapter(allContactAdapter);
                } else {
                    if (secondaryContactList.size() > 0) {
                        ArrayList<Object> newObjectArrayListContact = new ArrayList<>();
                        newObjectArrayListContact.addAll(secondaryContactList);
                        allContactAdapter = new AllContactAdapter(this, newObjectArrayListContact);
                        allContactAdapter.filter(number);
                        if (allContactAdapter.getSearchCount() > 0) {
                            relativeContact.setVisibility(View.VISIBLE);
                            linearAddToContact.setVisibility(View.GONE);
                            recycleViewPbContact.setAdapter(allContactAdapter);
                        } else {
                            relativeContact.setVisibility(View.GONE);
                            linearAddToContact.setVisibility(View.VISIBLE);
                        }

                    }
                    /*String name = getNameFromNumber(number);
                    if (!TextUtils.isEmpty(name)) {
                        relativeContact.setVisibility(View.VISIBLE);
                        linearAddToContact.setVisibility(View.GONE);
                        ProfileData profileData = new ProfileData();
                        String imageUrl = getPhotoUrlFromNumber(number);
                        if (!StringUtils.isEmpty(imageUrl))
                            profileData.setProfileUrl(imageUrl);
                        else
                            profileData.setProfileUrl("");

                        profileData.setName(name);
                        profileData.setTempNumber(number);

                        TableProfileMobileMapping tableProfileMobileMapping =
                                new TableProfileMobileMapping(getDatabaseHandler());
                        ProfileMobileMapping profileMobileMapping =
                                tableProfileMobileMapping.getCloudPmIdFromProfileMappingFromNumber(
                                        Utils.getFormattedNumber(this, number));

                        if (profileMobileMapping != null) {
                            String cloudPmId = profileMobileMapping.getMpmCloudPmId();
                            // To do
                            // Pass this cloudId to fetch FirstName and Last Name from
                            // ProfileMasterTable
                            if (!StringUtils.isEmpty(cloudPmId)) {
                                TableProfileMaster tableProfileMaster = new
                                        TableProfileMaster(getDatabaseHandler());
                                UserProfile userProfile = tableProfileMaster
                                        .getProfileFromCloudPmId(Integer.parseInt(cloudPmId));
                                String firstName = userProfile.getPmFirstName();
                                String lastName = userProfile.getPmLastName();
                                String rcpId = userProfile.getPmRcpId();
                                String imagePath = userProfile.getPmProfileImage();
                                profileData.setTempIsRcp(true);
                                profileData.setTempRcpName(firstName + " " + lastName);
                                profileData.setTempRcpId(rcpId);

                            }
                        }
//                            objectArrayListContact.add(profileData);
                        ArrayList<Object> tempContact = new ArrayList<>();
                        tempContact.add(profileData);
                        if (allContactAdapter != null) {
                            allContactAdapter = new AllContactAdapter(DialerActivity.this,
                                    tempContact);
                            recycleViewPbContact.setAdapter(allContactAdapter);
                            allContactAdapter = null;

                        }
                    } else {
                        relativeContact.setVisibility(View.GONE);
                        linearAddToContact.setVisibility(View.VISIBLE);
                    }*/

                }
            }

            /*String name = getNameFromNumber(number);
            if (!TextUtils.isEmpty(name)) {
                relativeContact.setVisibility(View.VISIBLE);
                linearAddToContact.setVisibility(View.GONE);*/
               /* textContactName.setText(name);
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
                });*/

            /*} else {
                relativeContact.setVisibility(View.GONE);
                linearAddToContact.setVisibility(View.VISIBLE);
            }*/
        } else {
            relativeContact.setVisibility(View.GONE);
            linearAddToContact.setVisibility(View.VISIBLE);
        }
        initSwipe();
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
            Utils.callIntent(DialerActivity.this, number);
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
                    AppConstants.setIsFirstTime(false);
                Utils.callIntent(DialerActivity.this, numberToCall);
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

//    private void showCallConfirmationDialog(final String number) {
//
////        final String finalNumber = Utils.getFormattedNumber(DialerActivity.this, number);
//
//       /* if (number.startsWith("*")) {
//            finalNumber = number;
//        } else {
//            finalNumber = Utils.getFormattedNumber(DialerActivity.this,number);
//        }*/
//
////        final String formattedNumber = Utils.getFormattedNumber(DialerActivity.this, number);
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
//
//                       /* String unicodeNumber = number.replace("*", Uri.encode("*")).replace("#",
//                                Uri.encode("#"));
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
//                                unicodeNumber));
//                        startActivity(intent);*/
//
//
//                        AppConstants.setIsFirstTime(false);
//                        Utils.callIntent(DialerActivity.this, number);
//                        break;
//
//                }
//            }
//        };
//
//        callConfirmationDialog = new MaterialDialog(DialerActivity.this, cancelListener);
//        callConfirmationDialog.setTitleVisibility(View.GONE);
//        callConfirmationDialog.setLeftButtonText(getString(R.string.action_cancel));
//        callConfirmationDialog.setRightButtonText(getString(R.string.action_call));
//        callConfirmationDialog.setDialogBody(getString(R.string.action_call) + " " + number
//                + " ?");
//        callConfirmationDialog.showDialog();
//    }

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
                String actionNumber = StringUtils.defaultString(((AllContactAdapter
                        .AllContactViewHolder) viewHolder).textContactNumber
                        .getText().toString());
                if (direction == ItemTouchHelper.LEFT) {
                    /* SMS */
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + actionNumber));
                    startActivity(smsIntent);

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkPermissionToExecute(requiredPermissions, AppConstants.READ_LOGS,
                                actionNumber);
                    } else {
                        actionNumber = Utils.getFormattedNumber(DialerActivity.this, actionNumber);
                        Utils.callIntent(DialerActivity.this, actionNumber);
//                        showCallConfirmationDialog(actionNumber);
                    }
                }
                try {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (allContactAdapter != null)
                                allContactAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                /* Disable swiping in headers */
                if (viewHolder instanceof AllContactAdapter.AllContactViewHolder) {
                    /* Disable swiping in multiple RC case */
                    if (((AllContactAdapter.AllContactViewHolder) viewHolder)
                            .recyclerViewMultipleRc.getVisibility() == View.VISIBLE) {
                        return 0;
                    }
                    /* Disable swiping for No number */
                    if (StringUtils.length(((AllContactAdapter.AllContactViewHolder) viewHolder)
                            .textContactNumber.getText().toString()) <= 0) {
                        return 0;
                    }
                }

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
                        p.setColor(ContextCompat.getColor(DialerActivity.this, R.color
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
                        p.setColor(ContextCompat.getColor(DialerActivity.this, R.color
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
        itemTouchHelper.attachToRecyclerView(recycleViewPbContact);
    }
}
