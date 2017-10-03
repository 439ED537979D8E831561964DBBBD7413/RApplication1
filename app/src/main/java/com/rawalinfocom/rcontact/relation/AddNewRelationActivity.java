package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.contacts.EditProfileActivity;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 03/10/17.
 */

public class AddNewRelationActivity extends BaseActivity implements WsResponseListener, View.OnClickListener {

    @BindView(R.id.relative_root_new_relation)
    LinearLayout relativeRootNewRelation;
    @BindView(R.id.include_toolbar)
    Toolbar includeToolbar;
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_add_new)
    ImageView imageAddNew;
    @BindView(R.id.image_profile)
    ImageView imageProfile;
    @BindView(R.id.input_value_add_name)
    EditText inputValueAddName;
    @BindView(R.id.img_clear)
    ImageView imgClear;
    @BindView(R.id.input_value_business)
    EditText inputValueBusiness;
    @BindView(R.id.input_value_family)
    EditText inputValueFamily;
    @BindView(R.id.checkbox_friend)
    CheckBox checkboxFriend;
    @BindView(R.id.button_name_done)
    Button buttonNameDone;
    @BindView(R.id.button_name_cancel)
    Button buttonNameCancel;
    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.txt_title_business)
    TextView txtTitleBusiness;
    @BindView(R.id.txt_title_family)
    TextView txtTitleFamily;
    @BindView(R.id.txt_title_friend)
    TextView txtTitleFriend;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_relation);

        ButterKnife.bind(this);
        initToolbar();
        init();
    }

    private void initToolbar() {
        textToolbarTitle = ButterKnife.findById(includeToolbar, R.id.text_toolbar_title);
        textToolbarTitle.setTypeface(Utils.typefaceSemiBold(this));
        textToolbarTitle.setText(getResources().getString(R.string.add_relation_toolbar_title));
        imageActionBack = ButterKnife.findById(includeToolbar, R.id.image_action_back);
        imageActionBack.setOnClickListener(this);
        imageAddNew = ButterKnife.findById(includeToolbar, R.id.image_add_new);
        imageAddNew.setVisibility(View.GONE);
    }

    private void init() {

        activity = AddNewRelationActivity.this;

        inputValueAddName.setTypeface(Utils.typefaceRegular(this));
        inputValueBusiness.setTypeface(Utils.typefaceRegular(this));
        inputValueFamily.setTypeface(Utils.typefaceRegular(this));

        txtTitle.setTypeface(Utils.typefaceRegular(this));
        txtTitleBusiness.setTypeface(Utils.typefaceRegular(this));
        txtTitleFamily.setTypeface(Utils.typefaceRegular(this));
        txtTitleFriend.setTypeface(Utils.typefaceRegular(this));

        buttonNameDone.setTypeface(Utils.typefaceRegular(this));
        buttonNameDone.setOnClickListener(this);
        buttonNameCancel.setTypeface(Utils.typefaceRegular(this));
        buttonNameCancel.setOnClickListener(this);

        imgClear.setVisibility(View.GONE);

        inputValueAddName.setFocusable(false);
        inputValueBusiness.setFocusable(false);
        inputValueFamily.setFocusable(false);

        Glide.with(activity)
                .load(R.drawable.home_screen_profile)
                .bitmapTransform(new CropCircleTransformation(activity))
                .into(imageProfile);
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.image_action_back:
                finish();
                break;
            case R.id.button_name_done:
                Utils.showSuccessSnackBar(activity, relativeRootNewRelation, "Add New Relation Done");
                break;
            case R.id.button_name_cancel:
                finish();
                break;
        }
    }
}
