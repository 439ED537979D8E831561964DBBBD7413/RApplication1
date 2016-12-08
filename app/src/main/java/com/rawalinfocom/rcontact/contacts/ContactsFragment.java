package com.rawalinfocom.rcontact.contacts;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsFragment extends BaseFragment {

    @BindView(R.id.linear_call_tabs)
    LinearLayout linearCallTabs;
    @BindView(R.id.frame_container_call_tab)
    FrameLayout frameContainerCallTab;
    @BindView(R.id.button_all_contacts)
    Button buttonAllContacts;
    @BindView(R.id.button_r_contacts)
    Button buttonRContacts;
    @BindView(R.id.button_favorites)
    Button buttonFavorites;

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

        defaultButtonTextColor = buttonFavorites.getTextColors().getDefaultColor();

        allContactsFragment = AllContactsFragment.newInstance();
        rContactsFragment = RContactsFragment.newInstance();
        favoritesFragment = FavoritesFragment.newInstance();

        buttonAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(allContactsFragment);
                selectButton((Button) view);
            }
        });

        buttonRContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(rContactsFragment);
                selectButton((Button) view);
            }
        });

        buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(favoritesFragment);
                selectButton((Button) view);
            }
        });

        buttonAllContacts.setSelected(true);
        buttonAllContacts.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container_call_tab, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

   /* private void addFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.frame_container_call_tab, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }*/

    private void selectButton(Button button) {

        Button[] buttons = {buttonAllContacts, buttonRContacts, buttonFavorites};

        for (Button button1 : buttons) {
            if (button1 == button) {
                button1.setSelected(true);
                button1.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            } else {
                button1.setSelected(false);
                button1.setTextColor(defaultButtonTextColor);
            }
        }

    }
}
