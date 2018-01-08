package com.rawalinfocom.rcontact.contacts;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.MainActivity;
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
    @BindView(R.id.relative_root_contacts)
    RelativeLayout relativeRootContacts;
    //    @BindView(R.id.image_sync)
//    ImageView imageSync;
//    @BindView(R.id.text_sync_progress)
//    TextView textSyncProgress;
//    @BindView(R.id.relative_sync_progress)
//    RelativeLayout relativeSyncProgress;
//    @BindView(R.id.progress_contacts)
//    AnimateHorizontalProgressBar progressContacts;
    @BindView(R.id.include_elevation)
    View includeElevation;

    AllContactsListFragment allContactsFragment;
    RContactsFragment rContactsFragment;
    FavoritesFragment favoritesFragment;

//    TextView textDescription, textHeader;
//    ArrayList<View> tutorialViews;
//    String[] arrayListTutorialHeaders = {"My Contacts", "RContacts", "Favorites", "Global " +
//            "Search", "Notifications", "Slide Menu"};
//    String[] arrayListTutorialDescription = {"Phone book contacts in black color\nintegrated with" +
//            " their RContacts profiles in pine green color", "Your phone book contacts who\nare " +
//            "registered with RContacts", "Contacts marked as Favorites in\nPhone book or " +
//            "RContacts profiles", "Find any Known or Unknown\nContact by Name or Number",
//            "Receive all Notifications related to\nyour RContacts Profile here", "Click on Slide " +
//            "Menu to view\nyour Profile and other Options"};
//    String[] arrayListTapContinue = {"TAP ANYWHERE TO MOVE AHEAD", "TAP ANYWHERE TO MOVE AHEAD",
//            "TAP ANYWHERE TO MOVE AHEAD", "TAP ANYWHERE TO MOVE AHEAD", "TAP ANYWHERE TO MOVE " +
//            "AHEAD", "TAP ON THE SLIDE MENU"};

    int currentTabPosition = -1;
//    int sequence = 1;

//    public ContactsFragment() {
    // // Required empty public constructor
//    }

//    public static ContactsFragment newInstance() {
//        return new ContactsFragment();
//    }

    //<editor-fold desc="Override Methods">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void getFragmentArguments() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        init();
//        replaceFragment(allContactsFragment, AppConstants.TAG_FRAGMENT_ALL_CONTACTS);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        AllContactsListFragment.arrayListPhoneBookContacts = null;
    }
    //</editor-fold>

    //<editor-fold desc="Private / Public Methods">

    private void init() {

       /* if (Utils.getBooleanPreference(getActivity(), AppConstants.PREF_SHOW_WALK_THROUGH,
       true)) {
            displayWalkThrough();
        } else {
            ((MainActivity) getActivity()).frameTutorial.setVisibility(View.GONE);
        }*/

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).frameTutorial.setVisibility(View.GONE);
        }

        allContactsFragment = new AllContactsListFragment();
        rContactsFragment = new RContactsFragment();
        favoritesFragment = new FavoritesFragment();

        includeElevation.setAlpha(0.5f);

        bindWidgetsWithAnEvent();
        setupTabLayout();
        Utils.changeTabsFont(getActivity(), tabContact, true);

    }

//    private void displayWalkThrough() {
//
////        ((MainActivity) getActivity()).frameTutorial.setVisibility(View.GONE);
//
//        TableMobileMaster tableMobileMaster = new TableMobileMaster(getMainActivity().databaseHandler);
//        String number = tableMobileMaster.getUserMobileNumber(getUserPmId());
//
//        getMainActivity().tutorialUserName.setTypeface(Utils.typefaceSemiBold
//                (getActivity()));
//        getMainActivity().tutorialNumber.setTypeface(Utils.typefaceRegular
//                (getActivity()));
//        getMainActivity().tutorialRatingCount.setTypeface(Utils.typefaceBold
//                (getActivity()));
//
//        getMainActivity().tutorialUserName.setText(Utils.getStringPreference
//                (getActivity(),
//                        AppConstants.PREF_USER_NAME, ""));
//        getMainActivity().tutorialNumber.setText(number);
//        getMainActivity().tutorialRatingCount.setText(Utils.getStringPreference
//                (getActivity(), AppConstants
//                        .PREF_USER_TOTAL_RATING, ""));
//
//        if (!StringUtils.isEmpty(Utils.getStringPreference(getActivity(), AppConstants
//                .PREF_USER_RATING, "")))
//            getMainActivity().tutorialRatingUser.setRating(Float.parseFloat(Utils
//                    .getStringPreference(getActivity(), AppConstants.PREF_USER_RATING, "0")));
//        else
//            getMainActivity().tutorialRatingUser.setRating(0f);
//
//        final String thumbnailUrl = Utils.getStringPreference(getActivity(), AppConstants
//                        .PREF_USER_PHOTO,
//                "");
//        if (!TextUtils.isEmpty(thumbnailUrl)) {
//            Glide.with(getActivity())
//                    .load(thumbnailUrl)
//                    .placeholder(R.drawable.home_screen_profile)
//                    .error(R.drawable.home_screen_profile)
//                    .bitmapTransform(new CropCircleTransformation(getActivity()))
//                    .override(500, 500)
//                    .into(getMainActivity().tutorialProfileImage);
//        } else {
//            getMainActivity().tutorialProfileImage.setImageResource(R.drawable
//                    .home_screen_profile);
//        }
//
//        tutorialViews = new ArrayList<>();
//
//        final LinearLayout linearTabs = new LinearLayout(getActivity());
//        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(FrameLayout
//                .LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        layoutParamsFrame.topMargin = (int) getResources().getDimension(R.dimen
//                .padding_around_content_area);
//        linearTabs.setLayoutParams(layoutParamsFrame);
//
//        final ViewGroup.LayoutParams profileLayoutParams = getMainActivity()
//                .tutorialUserProfile.getLayoutParams();
//        /*profileLayoutParams.width = Utils.getDeviceWidth(getActivity()) - android.R.attr
//                .actionBarSize;*/
//        ViewTreeObserver vto = getMainActivity().navigationView.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    getMainActivity().navigationView.getViewTreeObserver()
//                            .removeGlobalOnLayoutListener(this);
//                } else {
//                    getMainActivity().navigationView.getViewTreeObserver()
//                            .removeOnGlobalLayoutListener(this);
//                }
////                int width  = getMainActivity().navigationView.getMeasuredWidth();
//                profileLayoutParams.width = getMainActivity().navigationView
//                        .getWidth();
//            }
//        });
////        profileLayoutParams.width = getMainActivity().navigationView.getWidth();
//        getMainActivity().tutorialUserProfile.setLayoutParams(profileLayoutParams);
//
//        final float scale = getResources().getDisplayMetrics().density;
//
//        final LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams
//                (LinearLayout.LayoutParams.MATCH_PARENT, (int) (43 * scale));
//        layoutParamsLinear.weight = 1;
//
//        final LinearLayout linearDescription = new LinearLayout(getActivity());
//        linearDescription.setLayoutParams(layoutParamsFrame);
//        linearDescription.setOrientation(LinearLayout.VERTICAL);
//
//        final LinearLayout.LayoutParams descriptionLayoutParam = new LinearLayout.LayoutParams
//                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        descriptionLayoutParam.leftMargin = (int) getResources().getDimension(R.dimen
//                .activity_horizontal_margin);
//        descriptionLayoutParam.rightMargin = (int) getResources().getDimension(R.dimen
//                .activity_horizontal_margin);
//
//        final LinearLayout.LayoutParams headerLayoutParam = new LinearLayout.LayoutParams
//                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        headerLayoutParam.leftMargin = (int) getResources().getDimension(R.dimen
//                .activity_horizontal_margin);
//        headerLayoutParam.rightMargin = (int) getResources().getDimension(R.dimen
//                .activity_horizontal_margin);
//
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                layoutParamsLinear.topMargin = getMainActivity().toolbar
//                        .getHeight() + getMainActivity().tabMain.getHeight();
//
//                /*ViewTreeObserver vto = getMainActivity().tabMain
//                .getViewTreeObserver();
//                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        int[] location = new int[2];
//                        getMainActivity().tabMain.getLocationOnScreen(location);
//                        getMainActivity().tabMain.getViewTreeObserver()
//                                .removeGlobalOnLayoutListener(this);
//
//                        layoutParamsLinear.topMargin = location[1] + (int) getResources()
//                                .getDimension(R.dimen.padding_around_content_area);
//                    }
//                });*/
//            }
//        }, 500);
//
//
//        descriptionLayoutParam.topMargin = (int) (Utils.getDeviceHeight(getActivity()) / 2.5);
//
//        getMainActivity().textTapContinue.setText(arrayListTapContinue[0]);
//
//        final TextView textMyContacts = new TextView(getActivity());
//        textMyContacts.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color
//                .lightGrayishCyan));
//        textMyContacts.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
//        textMyContacts.setTypeface(Utils.typefaceRegular(getActivity()));
//        textMyContacts.setGravity(Gravity.CENTER);
//        textMyContacts.setTextSize(14);
//        textMyContacts.setLayoutParams(layoutParamsLinear);
//        textMyContacts.setText("My Contacts");
//        linearTabs.addView(textMyContacts);
//
//        final TextView textRContacts = new TextView(getActivity());
//        textRContacts.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
//        textRContacts.setTextColor(ContextCompat.getColor(getActivity(), android.R.color
//                .tab_indicator_text));
//        textRContacts.setTypeface(Utils.typefaceRegular(getActivity()));
//        textRContacts.setGravity(Gravity.CENTER);
//        textRContacts.setTextSize(14);
//        textRContacts.setLayoutParams(layoutParamsLinear);
//        textRContacts.setText("RContacts");
//        linearTabs.addView(textRContacts);
//        textRContacts.setVisibility(View.INVISIBLE);
//
//        final TextView textFavorites = new TextView(getActivity());
//        textFavorites.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color
//                .colorWhite));
//        textFavorites.setTextColor(ContextCompat.getColor(getActivity(), android.R.color
//                .tab_indicator_text));
//        textFavorites.setTypeface(Utils.typefaceRegular(getActivity()));
//        textFavorites.setGravity(Gravity.CENTER);
//        textFavorites.setTextSize(14);
//        textFavorites.setLayoutParams(layoutParamsLinear);
//        textFavorites.setText("Favorites");
//        linearTabs.addView(textFavorites);
//        textFavorites.setVisibility(View.INVISIBLE);
//
//        getMainActivity().frameTutorial.addView(linearTabs);
//
//        textHeader = new TextView(getActivity());
//        textHeader.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
//        textHeader.setTypeface(Utils.typefaceBold(getActivity()));
//        textHeader.setLayoutParams(descriptionLayoutParam);
//        textHeader.setTextSize(18);
//        textHeader.setPaintFlags(textHeader.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        textHeader.setText(arrayListTutorialHeaders[0]);
//        linearDescription.addView(textHeader);
//
//        textDescription = new TextView(getActivity());
//        textDescription.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
//        textDescription.setTypeface(Utils.typefaceRegular(getActivity()));
//        textDescription.setLayoutParams(headerLayoutParam);
//        textDescription.setTextSize(14);
//        textDescription.setText(arrayListTutorialDescription[0]);
//        linearDescription.addView(textDescription);
//
//        getMainActivity().frameTutorial.addView(linearDescription);
//
//        tutorialViews.add(textMyContacts);
//        tutorialViews.add(textRContacts);
//        tutorialViews.add(textFavorites);
//        tutorialViews.add(getMainActivity().linearTutorialSearch);
//        tutorialViews.add(getMainActivity().imageTutorialNotification);
//        tutorialViews.add(getMainActivity().imageTutorialDrawer);
//
//        getMainActivity().frameTutorial.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (sequence < 6) {
//                    setTutorialViewVisibility(sequence);
//                    sequence++;
//                }
//            }
//        });
//
//        getMainActivity().imageTutorialDrawer.setOnClickListener(new View
//                .OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (view.getVisibility() == View.VISIBLE) {
//                    getMainActivity().tutorialUserProfile.setVisibility(View.VISIBLE);
//
//                    getMainActivity().drawer.openDrawer(GravityCompat.START);
//                    getMainActivity().imageTutorialDrawer.setVisibility(View.GONE);
//                    textHeader.setText("Your Profile");
//                    textDescription.setText("Click to view your profile");
//                    getMainActivity().textTapContinue.setText("TAP ANYWHERE ON YOUR " +
//                            "DETAILS");
//                }
//            }
//        });
//
//        getMainActivity().tutorialUserProfile.setOnClickListener(new View
//                .OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        getMainActivity().frameTutorial.setVisibility(View.GONE);
//                        getMainActivity().drawer.closeDrawer(Gravity.START);
//                    }
//                }, 700);
//                Bundle bundle = new Bundle();
//                bundle.putString(AppConstants.EXTRA_PM_ID, getUserPmId());
//                bundle.putString(AppConstants.EXTRA_PHONE_BOOK_ID, "");
//                bundle.putString(AppConstants.EXTRA_CONTACT_NAME, Utils.getStringPreference
//                        (getActivity(), AppConstants.PREF_USER_NAME, ""));
//                bundle.putString(AppConstants.EXTRA_PROFILE_IMAGE_URL, thumbnailUrl);
//                bundle.putInt(AppConstants.EXTRA_CONTACT_POSITION, 1);
//                getMainActivity().startActivityIntent(getActivity(),
//                        ProfileDetailActivity.class,
//                        bundle);
//            }
//        });
//    }

//    private void setTutorialViewVisibility(int sequence) {
//        textHeader.setText(arrayListTutorialHeaders[sequence]);
//        textDescription.setText(arrayListTutorialDescription[sequence]);
//        getMainActivity().textTapContinue.setText(arrayListTapContinue[sequence]);
//        for (int i = 0; i < tutorialViews.size(); i++) {
//            if (i == sequence) {
//                tutorialViews.get(i).setVisibility(View.VISIBLE);
//            } else {
//                tutorialViews.get(i).setVisibility(View.INVISIBLE);
//            }
//        }
//    }

    private void replaceFragment(Fragment fragment, String tag) {

//        frameContainerCallTab.removeAllViews();
//
//        if (tag.equalsIgnoreCase(AppConstants.TAG_FRAGMENT_R_CONTACTS)) {
//            Utils.showProgressDialog(getMainActivity(), getString(R.string.msg_please_wait), false);
//        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container_call_tab, fragment, tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

//        FragmentManager fm = getChildFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.replace(R.id.frame_container_call_tab, fragment, tag)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }

    private void setupTabLayout() {
        tabContact.addTab(tabContact.newTab().setText(getMainActivity().getString(R.string
                .tab_all_contact)), true);
        tabContact.addTab(tabContact.newTab().setText(getMainActivity().getString(R.string
                .tab_r_contact)));
        tabContact.addTab(tabContact.newTab().setText(getMainActivity().getString(R.string
                .tab_favorites)));
    }

    private void bindWidgetsWithAnEvent() {
        tabContact.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                frameContainerCallTab.removeAllViews();
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
//                allContactsFragment = new AllContactsListFragment();
                replaceFragment(allContactsFragment, AppConstants.TAG_FRAGMENT_ALL_CONTACTS);
//                replaceFragment(new ContactsFragment(), AppConstants.TAG_FRAGMENT_ALL_CONTACTS);
                break;
            case R_CONTACT_FRAGMENT:
//                rContactsFragment = new RContactsFragment();
                replaceFragment(rContactsFragment, AppConstants.TAG_FRAGMENT_R_CONTACTS);
//                replaceFragment(new RContactsFragment(), AppConstants.TAG_FRAGMENT_R_CONTACTS);
                break;
            case FAVOURITE_FRAGMENT:
//                favoritesFragment = new FavoritesFragment();
                replaceFragment(favoritesFragment, AppConstants.TAG_FRAGMENT_FAVORITES);
//                replaceFragment(new FavoritesFragment(), AppConstants.TAG_FRAGMENT_FAVORITES);
                break;
        }
    }

    //</editor-fold>

}
