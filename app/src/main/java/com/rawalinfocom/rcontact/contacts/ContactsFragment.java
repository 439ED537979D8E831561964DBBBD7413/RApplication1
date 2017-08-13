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
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsFragment extends BaseFragment {

    public final int ALL_CONTACT_FRAGMENT = 0;
    public final int R_CONTACT_FRAGMENT = 1;
    public final int FAVOURITE_FRAGMENT = 2;

    @BindView(R.id.frame_container_call_tab)
    FrameLayout frameContainerCallTab;
    @BindView(R.id.tab_contact)
    TabLayout tabContact;

    //    AllContactsFragment allContactsFragment;
    AllContactsListFragment allContactsFragment;
    RContactsFragment rContactsFragment;
    FavoritesFragment favoritesFragment;

    int currentTabPosition = -1;

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
    public void onResume() {
        super.onResume();
        /*if (currentTabPosition == FAVOURITE_FRAGMENT && favoritesFragment != null) {
            if (favoritesFragment.getAllContactListAdapter() != null) {
//                favoritesFragment.getFavouriteContacts();
                getLoaderManager().initLoader(0, null, favoritesFragment);
             *//*   int clickedPosition = favoritesFragment.getAllContactListAdapter()
                        .getListClickedPosition();
                Log.i("onResume", String.valueOf(favoritesFragment.getAllContactListAdapter()
                        .getListClickedPosition()));
                favoritesFragment.getArrayListPhoneBookContacts().remove(clickedPosition);
                 favoritesFragment.getAllContactListAdapter().notifyItemRemoved(clickedPosition);
                 *//*
            }
        }*/
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
        replaceFragment(allContactsFragment, AppConstants.TAG_FRAGMENT_ALL_CONTACTS);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        RContactsFragment.arrayListRContact = null;
        AllContactsListFragment.arrayListPhoneBookContacts = null;
    }

    private void init() {

//        allContactsFragment = AllContactsFragment.newInstance();
        allContactsFragment = AllContactsListFragment.newInstance();
        rContactsFragment = RContactsFragment.newInstance();
        favoritesFragment = FavoritesFragment.newInstance();

        bindWidgetsWithAnEvent();
        setupTabLayout();
        Utils.changeTabsFont(getActivity(), tabContact, true);

    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container_call_tab, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    private void setupTabLayout() {
        tabContact.addTab(tabContact.newTab().setText(getActivity().getString(R.string
                .tab_all_contact)), true);
        tabContact.addTab(tabContact.newTab().setText(getActivity().getString(R.string
                .tab_r_contact)));
        tabContact.addTab(tabContact.newTab().setText(getActivity().getString(R.string
                .tab_favorites)));
    }

    private void bindWidgetsWithAnEvent() {
        tabContact.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
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
            case ALL_CONTACT_FRAGMENT:
                replaceFragment(allContactsFragment, AppConstants.TAG_FRAGMENT_ALL_CONTACTS);
                break;
            case R_CONTACT_FRAGMENT:
                replaceFragment(rContactsFragment, AppConstants.TAG_FRAGMENT_R_CONTACTS);
                break;
            case FAVOURITE_FRAGMENT:
                replaceFragment(favoritesFragment, AppConstants.TAG_FRAGMENT_FAVORITES);
                break;
        }
    }

}
