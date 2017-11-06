package com.rawalinfocom.rcontact;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.rawalinfocom.rcontact.database.DatabaseHandler;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Abstract fragment that every other Fragment in this application should implement. It handles
 * basic fragment methods.
 */

public abstract class BaseFragment extends Fragment {

    DatabaseHandler databaseHandler;
    MainActivity activity;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity =  (MainActivity) getActivity();
        databaseHandler = new DatabaseHandler(getActivity());
        getFragmentArguments();
    }

    public MainActivity getMainActivity(){
        return this.activity;
    }
    /**
     * Retrieves arguments supplied to setArguments(Bundle), if any.
     */

    public abstract void getFragmentArguments();

    /**
     * Replace whatever is in the @containerView view with @newFragment,
     * and add the transaction to the back stack so the user can navigate back
     */

    public void replaceFragment(int containerView, Fragment newFragment, String fragmentTag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Enter-Exit Animation

        transaction.replace(containerView, newFragment, fragmentTag);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    /**
     * Add whatever is in the @containerView view with @newFragment,
     * and add the transaction to the back stack so the user can navigate back
     */

    public void addFragment(int containerView, Fragment newFragment, String fragmentTag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Enter-Exit Animation

        transaction.add(containerView, newFragment, fragmentTag);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public String getUserPmId() {
        if (getActivity() == null) {
            return null;
        }
        return ((BaseActivity) getActivity()).getUserPmId();
    }

    public String getPmBadge() {
        if (getActivity() == null) {
            return null;
        }
        return ((BaseActivity) getActivity()).getPmBadge();
    }
}
