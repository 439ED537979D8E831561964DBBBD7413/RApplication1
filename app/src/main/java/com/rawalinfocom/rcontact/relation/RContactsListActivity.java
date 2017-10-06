package com.rawalinfocom.rcontact.relation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rawalinfocom.rcontact.BaseActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.alphabetsIndexFastScrollRecycler.IndexFastScrollRecyclerView;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 25/09/17.
 */

public class RContactsListActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {
    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.imgClose)
    ImageView imgClose;
    @BindView(R.id.main_toolbar)
    LinearLayout mainToolbar;
    @BindView(R.id.recycler_view_contact_list)
    IndexFastScrollRecyclerView recyclerViewContactList;
    @BindView(R.id.relative_root_r_contacts)
    RelativeLayout relativeRootRContacts;

    private ArrayList<UserProfile> arrayListRContact;
    RContactsListAdapter rContactListAdapter;

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_r_contacts_list);
        ButterKnife.bind(this);

        activity = RContactsListActivity.this;

        init();
    }

    private void init() {

        recyclerViewContactList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        getRContactFromDB();

        if (arrayListRContact.size() > 0) {
            rContactListAdapter = new RContactsListAdapter(activity, arrayListRContact);
            recyclerViewContactList.setAdapter(rContactListAdapter);
        }

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rContactListAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inputSearch.getText().toString().trim().length() > 0) {
                    rContactListAdapter.getFilter().filter("");
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            //<editor-fold desc="Back">
            case R.id.ripple_action_back:
                onBackPressed();
                break;
            //</editor-fold>
        }
    }

    private void getRContactFromDB() {

        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                (getDatabaseHandler());

        arrayListRContact = tableProfileMobileMapping.getRContactList(getUserPmId());
    }
}