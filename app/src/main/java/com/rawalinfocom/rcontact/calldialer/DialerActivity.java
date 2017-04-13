package com.rawalinfocom.rcontact.calldialer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.calldialer.transition.ScaleTransition;
import com.rawalinfocom.rcontact.helper.MaterialDialog;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        ButterKnife.bind(this);

        editTextNumber.setCursorVisible(false);
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
        slide.setDuration(500);

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
        slide.setDuration(500);

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
        final int actionBarId = getResources().getIdentifier("action_bar_container", "id", "android");
        final View decor = getWindow().getDecorView();
        return decor.findViewById(actionBarId);
    }

    private void initandClickEvents(){


        linearAddToContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberToSave =  editTextNumber.getText().toString();
                Utils.addToContact(DialerActivity.this,numberToSave);

            }
        });

        imageBtnCloseDrawer.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                finishAfterTransition();

            }
        });

        imageButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = editTextNumber.getText().length();
                if (length > 0) {
                    editTextNumber.getText().delete(length - 1, length);
                }
            }
        });

        imageButtonClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editTextNumber.getText().clear();
                return true;
            }
        });
        linear0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                editTextNumber.setText(editTextNumber.getText().toString() + "+");
                return true;
            }
        });

        linear0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "0");
            }
        });

        linear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "1");
            }
        });

        linear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "2");
            }
        });

        linear3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "3");
            }
        });

        linear4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "4");
            }
        });
        linear5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "5");
            }
        });

        linear6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "6");
            }
        });

        linear7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "7");
            }
        });

        linear8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "8");
            }
        });

        linear9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "9");
            }
        });

        linearStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "*");
            }
        });

        linearHashTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextNumber.setText(editTextNumber.getText().toString() + "#");
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberToCall = editTextNumber.getText().toString();
                if(!TextUtils.isEmpty(numberToCall))
                    showCallConfirmationDialog(numberToCall);
            }
        });

        imageBtnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberToSendMessage =  editTextNumber.getText().toString();
                if(!TextUtils.isEmpty(numberToSendMessage)){
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + numberToSendMessage));
                    startActivity(smsIntent);
                }

            }
        });

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
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                                number));
                        startActivity(intent);
                        break;
                }
            }
        };

        callConfirmationDialog = new MaterialDialog(DialerActivity.this, cancelListener);
        callConfirmationDialog.setTitleVisibility(View.GONE);
        callConfirmationDialog.setLeftButtonText("Cancel");
        callConfirmationDialog.setRightButtonText("Call");
        callConfirmationDialog.setDialogBody("Call " + number + "?");
        callConfirmationDialog.showDialog();

    }
}
