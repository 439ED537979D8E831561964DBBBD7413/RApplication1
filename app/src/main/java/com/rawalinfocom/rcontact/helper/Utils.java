package com.rawalinfocom.rcontact.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.model.Country;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Class containing some static utility methods.
 */

public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();

    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    //<editor-fold desc="Check Android OS Version">

    /**
     * Uses static final constants to detect if the device's platform version is Jellybean or
     * later.
     */
    public static boolean hasJellybean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    //</editor-fold>

    //<editor-fold desc="Network Info">

    /**
     * Check Network Availability
     */
    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                    .CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="SnackBar">

    public static void showErrorSnackBar(@NonNull Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarNegative));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void showSuccessSnackBar(@NonNull Context context, View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(context, R.color
                .colorSnackBarPositive));
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id
                .snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    //</editor-fold>

    //<editor-fold desc="Device Dimensions">
    public static int getDeviceHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static int getDeviceWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }
    //</editor-fold>

    //<editor-fold desc="Data Type Operations">
    public static boolean isArraylistNullOrEmpty(ArrayList arrayList) {
        return !(arrayList != null && arrayList.size() > 0);
    }
    //</editor-fold>

    //<editor-fold desc="Date Time Functions">

    /**
     * Returns UTC time in Date Format
     */
    public static Date getUtcDateTimeAsDate() {
        return getStringDateToDate(getUtcDateTimeAsString());
    }

    /**
     * Returns UTC time in String Format
     */
    public static String getUtcDateTimeAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        sdf.setTimeZone(timeZone);
        return sdf.format(calendar.getTime());


    }

    /**
     * Converts Date to String
     */
    public static Date getStringDateToDate(String StrDate) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date dateToReturn = null;
        try {
            dateToReturn = sdf.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }

    /**
     * Returns OTP expiration Time
     */
    public static String getOtpExpirationTime(String startTime) {
        String endTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            Date d = sdf.parse(startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, AppConstants.OTP_VALIDITY_DURATION);
            endTime = sdf.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endTime;
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateFormat(String oldFormattedDate, String oldFormat, String
            newFormat) {
        SimpleDateFormat oldDateFormatter = new SimpleDateFormat(oldFormat);
        try {
            Date tempDate = oldDateFormatter.parse(oldFormattedDate);
            SimpleDateFormat newDateFormatter = new SimpleDateFormat(newFormat);
            return newDateFormatter.format(tempDate);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    //</editor-fold>

    //<editor-fold desc="Shared Preferences">

    public static void setStringPreference(Context context, String key, @Nullable String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(key, defaultValue);
    }

    public static void setArrayListPreference(Context context, String key, @Nullable ArrayList
            arrayList) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<String> getArrayListPreference(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(key, null);
        Type type = new TypeToken<ArrayList>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void setIntegerPreference(Context context, String key, int value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntegerPreference(Context context, String key, int defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getInt(key, defaultValue);
    }

    public static void setBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean(key, defaultValue);
    }

    public static void setObjectPreference(Context context, String key, @Nullable Object object) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String json = gson.toJson(object);
        editor.putString(key, json);
        editor.apply();
    }

    public static Object getObjectPreference(Context context, String key, Class classObject) {
        try {
            SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                    .KEY_PREFERENCES, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedpreferences.getString(key, "");
            return gson.fromJson(json, classObject);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "method : getObjectPreference()");
            return null;
        }
    }

    public static void removePreference(Context context, String key) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants
                .KEY_PREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences.edit().remove(key).apply();
    }

    //</editor-fold>

    //<editor-fold desc="Progress Dialog">
    private static MaterialProgressDialog progressDialog;

    public static void showProgressDialog(Context context, String msg, boolean isCancelable) {
        if (context != null) {

            progressDialog = (MaterialProgressDialog) MaterialProgressDialog.ctor(context, msg);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(isCancelable);
            progressDialog.show();

        }
    }

    public static void hideProgressDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "method : hideProgressDialog()");
        }
    }
    //</editor-fold>

    //<editor-fold desc="Typeface">

    public static Typeface typefaceRegular(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceRegular() , Null context");
            return null;
        }
    }

    public static Typeface typefaceBold(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceBold() , Null context");
            return null;
        }
    }

    public static Typeface typefaceLight(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceLight() , Null context");
            return null;
        }
    }

    public static Typeface typefaceItalic(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Italic.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceItalic() , Null context");
            return null;
        }
    }

    public static Typeface typefaceSemiBold(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Semibold.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceSemiBold() , Null context");
            return null;
        }
    }

    public static Typeface typefaceIcons(Context context) {
        if (context != null) {
            return Typeface.createFromAsset(context.getAssets(), "fonts/icon_fonts.ttf");
//            return Typeface.createFromAsset(context.getAssets(), "fonts/icomoon_latest.ttf");
        } else {
            Log.e(LOG_TAG, "method : typefaceSemiBold() , Null context");
            return null;
        }
    }

    //</editor-fold>

    //<editor-fold desc="Image Conversion">

    public static String convertBitmapToBase64(Bitmap imgBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    //</editor-fold>

    //<editor-fold desc="Keyboard Visibility">
    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService
                (Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public static void showSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
    //</editor-fold>

    //<editor-fold desc="Check Location Enability">
    public static boolean isLocationEnabled(Context context) {
        return Settings.Secure.isLocationProviderEnabled(context.getContentResolver(),
                LocationManager.GPS_PROVIDER);
    }
    //</editor-fold>

    public static void copyToClipboard(Context context, String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);
    }

    public static void changeTabsFont(Context context, TabLayout tabLayout) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Utils.typefaceSemiBold(context));
                }
            }
        }
    }

    public static String getFormattedNumber(Context context, String phoneNumber) {
        Country country = (Country) Utils.getObjectPreference(context, AppConstants
                .PREF_SELECTED_COUNTRY_OBJECT, Country.class);
        String defaultCountryCode = "+91";
        if (country != null) {
            defaultCountryCode = country.getCountryCodeNumber();
        }
        if (!StringUtils.startsWith(phoneNumber, "+")) {
            if (StringUtils.startsWith(phoneNumber, "0")) {
                phoneNumber = defaultCountryCode + StringUtils.substring(phoneNumber, 1);
            } else {
                phoneNumber = defaultCountryCode + phoneNumber;
            }
        }

        /* remove special characters from number */
        return "+" + StringUtils.replaceAll(StringUtils.substring(phoneNumber, 1),
                "[\\D]", "");
    }

    public static boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition ==
                    recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    public static boolean isFirstItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();
            if (firstVisibleItemPosition != RecyclerView.NO_POSITION && firstVisibleItemPosition
                    == 0)
                return true;
        }
        return false;
    }

    public static void setRatingStarColor(Drawable drawable, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DrawableCompat.setTint(drawable, color);
        } else {
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }

    public static void addToContact(Context context, String number){
        Intent intent = new Intent(Intent.ACTION_INSERT,
                ContactsContract.Contacts.CONTENT_URI);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        context.startActivity(intent);
    }

    public static void addToExistingContact(Context context, String number){
        Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT,
                ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
        context.startActivity(intent);

    }


}
