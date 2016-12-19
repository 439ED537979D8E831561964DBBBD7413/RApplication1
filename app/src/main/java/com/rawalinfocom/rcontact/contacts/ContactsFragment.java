package com.rawalinfocom.rcontact.contacts;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsFragment extends BaseFragment {

    @BindView(R.id.frame_container_call_tab)
    FrameLayout frameContainerCallTab;
    @BindView(R.id.tab_contact)
    TabLayout tabContact;

    AllContactsFragment allContactsFragment;
    RContactsFragment rContactsFragment;
    FavoritesFragment favoritesFragment;

    private int defaultButtonTextColor;

    public ContactsFragment() {
        // Required empty public constructor
    }


    public static ContactsFragment newInstance() {
        return new ContactsFragment();
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
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
        replaceFragment(allContactsFragment);
    }

    private void init() {

        allContactsFragment = AllContactsFragment.newInstance();
        rContactsFragment = RContactsFragment.newInstance();
        favoritesFragment = FavoritesFragment.newInstance();

        bindWidgetsWithAnEvent();
        setupTabLayout();
        Utils.changeTabsFont(getActivity(), tabContact);

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container_call_tab, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void setupTabLayout() {
        tabContact.addTab(tabContact.newTab().setText("All Contacts"), true);
        tabContact.addTab(tabContact.newTab().setText("R Contacts"));
        tabContact.addTab(tabContact.newTab().setText("Favorites"));
    }

    private void bindWidgetsWithAnEvent() {
        tabContact.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(allContactsFragment);
                break;
            case 1:
                replaceFragment(rContactsFragment);
                break;
            case 2:
                replaceFragment(favoritesFragment);
                break;
        }
    }

}
