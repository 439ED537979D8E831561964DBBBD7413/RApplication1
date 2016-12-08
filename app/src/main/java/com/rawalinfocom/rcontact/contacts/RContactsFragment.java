package com.rawalinfocom.rcontact.contacts;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.AllContactListAdapter;
import com.rawalinfocom.rcontact.adapters.RContactListAdapter;
import com.rawalinfocom.rcontact.database.TableMobileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMaster;
import com.rawalinfocom.rcontact.database.TableProfileMobileMapping;
import com.rawalinfocom.rcontact.helper.ProgressWheel;
import com.rawalinfocom.rcontact.model.MobileNumber;
import com.rawalinfocom.rcontact.model.ProfileData;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationEmail;
import com.rawalinfocom.rcontact.model.ProfileDataOperationPhoneNumber;
import com.rawalinfocom.rcontact.model.UserProfile;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RContactsFragment extends BaseFragment {

    @BindView(R.id.progress_r_contact)
    ProgressWheel progressRContact;
    @BindView(R.id.recycler_view_contact_list)
    RecyclerView recyclerViewContactList;
    @BindView(R.id.text_empty_view)
    TextView textEmptyView;

    // Fetch from local Profile Master
    ArrayList<UserProfile> arrayListUserProfile;

    // for Adapter
    ArrayList<ProfileData> arrayListProfileData;

    RContactListAdapter rContactListAdapter;

    public RContactsFragment() {
        // Required empty public constructor
    }


    public static RContactsFragment newInstance() {
        return new RContactsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_r_contacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        recyclerViewContactList.setLayoutManager(new LinearLayoutManager(getActivity()));
//        fetchData();
        progressRContact.setVisibility(View.GONE);
        TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                (getDatabaseHandler());
        rContactListAdapter = new RContactListAdapter(getActivity(), tableProfileMobileMapping.getRContactList());
        recyclerViewContactList.setAdapter(rContactListAdapter);
    }

    private void fetchData() {
        TableProfileMaster tableProfileMaster = new TableProfileMaster(getDatabaseHandler());
        arrayListUserProfile = new ArrayList<>();
        arrayListUserProfile.addAll(tableProfileMaster.getAllUserProfiles());
        arrayListProfileData = new ArrayList<>();
        for (int i = 0; i < arrayListUserProfile.size(); i++) {
            ProfileData profileData = new ProfileData();
            ArrayList<ProfileDataOperation> arrayListOperation = new ArrayList<>();
            ProfileDataOperation profileDataOperation = new ProfileDataOperation();
            profileDataOperation.setPbNameFirst(arrayListUserProfile.get(i).getPmFirstName());
            profileDataOperation.setPbNameLast(arrayListUserProfile.get(i).getPmLastName());

           /* TableProfileMobileMapping tableProfileMobileMapping = new TableProfileMobileMapping
                    (getDatabaseHandler());
            tableProfileMobileMapping.getProfileMobileMappingPmId(Integer.parseInt
                    (arrayListUserProfile.get(i).getPmRcpId()));*/

            TableMobileMaster tableMobileMaster = new TableMobileMaster(getDatabaseHandler());
            ArrayList<MobileNumber> arrayListMobileNumber = tableMobileMaster
                    .getMobileNumbersFromPmId(Integer.parseInt(arrayListUserProfile.get(i)
                            .getPmRcpId()));
            ArrayList<ProfileDataOperationPhoneNumber> arrayListPhoneNumber = new ArrayList<>();
            for (int j = 0; j < arrayListMobileNumber.size(); j++) {
                ProfileDataOperationPhoneNumber phoneNumber = new ProfileDataOperationPhoneNumber();
                phoneNumber.setPhoneNumber(arrayListMobileNumber.get(j).getMnmMobileNumber());
                arrayListPhoneNumber.add(phoneNumber);
            }
            profileDataOperation.setPbPhoneNumber(arrayListPhoneNumber);
//            profileDataOperation.setPbPhoneNumber(new
// ArrayList<ProfileDataOperationPhoneNumber>());

            profileDataOperation.setPbEmailId(new ArrayList<ProfileDataOperationEmail>());

            arrayListOperation.add(profileDataOperation);
            profileData.setOperation(arrayListOperation);
            arrayListProfileData.add(profileData);
        }
    }
}
