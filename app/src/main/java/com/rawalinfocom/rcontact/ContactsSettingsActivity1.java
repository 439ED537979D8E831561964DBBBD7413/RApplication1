package com.rawalinfocom.rcontact;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.rawalinfocom.rcontact.adapters.ShortByContactListAdapter;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.AppLanguage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsSettingsActivity1 extends BaseActivity implements RippleView
        .OnRippleCompleteListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_short_by)
    TextView textShortBy;
    @BindView(R.id.text_export_contact)
    TextView textExportContact;
    @BindView(R.id.ll_export)
    LinearLayout llExport;
    @BindView(R.id.ll_short_by)
    LinearLayout llShortBy;
    @BindView(R.id.activity_contact_settings)
    RelativeLayout activityContactSettings;
    @BindView(R.id.txt_short_by)
    TextView txtShortBy;
    @BindView(R.id.txt_export_contact)
    TextView txtExportContact;

    private Activity activity;
    private String vfile, selectedType;
    private GoogleApiClient mGoogleApiClient;
    //    private final int RC_SIGN_IN = 7;
//    private static final int GOOGLE_LOGIN_PERMISSION = 22;
    protected static final int REQUEST_CODE_RESOLUTION = 1337;
    protected static final int REQUEST_CODE_CREATOR = 101;
    private String FOLDER_NAME = "ContactBackup";
    public static String drive_id;
    public static DriveId driveID;

    private String[] requiredPermissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest
            .permission.WRITE_EXTERNAL_STORAGE};

    private ArrayList<AppLanguage> shortByContactArrayList;
    private String filePath;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);
        ButterKnife.bind(this);

        activity = ContactsSettingsActivity1.this;
        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
        }
    }

    //</editor-fold>
    private void init() {

        rippleActionBack.setOnRippleCompleteListener(this);

        textToolbarTitle.setText(getResources().getString(R.string.contacts));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        textShortBy.setTypeface(Utils.typefaceRegular(this));
        textExportContact.setTypeface(Utils.typefaceRegular(this));
        txtShortBy.setTypeface(Utils.typefaceRegular(this));
        txtExportContact.setTypeface(Utils.typefaceRegular(this));

        if (Utils.getStringPreference(activity, AppConstants.PREF_SHORT_BY_CONTACT, "0").equalsIgnoreCase("0")) {
            textShortBy.setText(getString(R.string.abc_by_first_name));
        } else {
            textShortBy.setText(getString(R.string.abc_by_last_name));
        }

        if (Utils.getStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, "0").equalsIgnoreCase("0")) {
            textExportContact.setText(getString(R.string.phone_storage));
        } else if (Utils.getStringPreference(activity, AppConstants.PREF_EXPORT_CONTACT, "0").equalsIgnoreCase("1")) {
            textExportContact.setText(R.string.sd_card);
        } else {
            textExportContact.setText(R.string.google_drive);
        }

        llExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportContactMaterialDialog();
            }
        });

        llShortBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShortContactMaterialDialog();
            }
        });
    }

    private void setArrayList(String type) {

        if (type.equals(getString(R.string.str_short))) {
            shortByContactArrayList = new ArrayList<>();

            if (Utils.getStringPreference(activity, AppConstants.PREF_SHORT_BY_CONTACT, "0").equalsIgnoreCase("0")) {
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_first_name), "0", true));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_last_name), "1", false));
            } else {
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_first_name), "0", false));
                shortByContactArrayList.add(new AppLanguage(getString(R.string.abc_by_last_name), "1", true));
            }
        } else {

            shortByContactArrayList = new ArrayList<>();

            shortByContactArrayList.add(new AppLanguage(getString(R.string.phone_storage), "0", true));
            shortByContactArrayList.add(new AppLanguage(getString(R.string.google_drive), "1", false));
        }
    }

    public void ShortContactMaterialDialog() {

        setArrayList(getString(R.string.str_short));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_language);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(getString(R.string.short_by));
        tvDialogTitle.setTypeface(Utils.typefaceBold(activity));

        Button btnRight = (Button) dialog.findViewById(R.id.btnRight);
        btnRight.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        btnRight.setTypeface(Utils.typefaceRegular(activity));
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.rippleRight);

        RecyclerView recyclerViewAppLanguage = (RecyclerView) dialog.findViewById(R.id.recyclerViewAppLanguage);
        recyclerViewAppLanguage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        final ShortByContactListAdapter shortByContactListAdapter = new ShortByContactListAdapter(shortByContactArrayList);
        recyclerViewAppLanguage.setAdapter(shortByContactListAdapter);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (shortByContactListAdapter.getSelected().equals("")) {
                    Utils.showErrorSnackBar(activity, activityContactSettings, getString(R.string.str_validation_short_type));
                } else {

                    AppConstants.isFromSettingActivity = true;
                    textShortBy.setText(shortByContactListAdapter.getSelected());
                    Utils.setStringPreference(activity, AppConstants.PREF_SHORT_BY_CONTACT, shortByContactListAdapter.getSelectedType());
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void ExportContactMaterialDialog() {

        setArrayList(getString(R.string.str_export));

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_app_language);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView tvDialogTitle = (TextView) dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setText(getString(R.string.export_format));
        tvDialogTitle.setTypeface(Utils.typefaceBold(activity));

        Button btnRight = (Button) dialog.findViewById(R.id.btnRight);
        btnRight.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        btnRight.setTypeface(Utils.typefaceRegular(activity));
        RippleView rippleRight = (RippleView) dialog.findViewById(R.id.rippleRight);

        RecyclerView recyclerViewAppLanguage = (RecyclerView) dialog.findViewById(R.id.recyclerViewAppLanguage);
        recyclerViewAppLanguage.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        final ShortByContactListAdapter shortByContactListAdapter = new ShortByContactListAdapter(shortByContactArrayList);
        recyclerViewAppLanguage.setAdapter(shortByContactListAdapter);

        rippleRight.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (shortByContactListAdapter.getSelected().equals("")) {
                    Utils.showErrorSnackBar(activity, activityContactSettings, getString(R.string.str_validation_export_type));
                } else {

                    dialog.dismiss();

                    selectedType = shortByContactListAdapter.getSelectedType();

                    switch (selectedType) {
                        case "0":
                            if (checkPermission())
                                generateExport();
                            break;

                        case "1":
                            if (checkPermission())
                                generateExport();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        dialog.show();
    }

    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int readPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:

                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                boolean isStorage = perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                boolean isStorageWrite = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (isStorage && isStorageWrite)
                    generateExport();
//                else
//                    Toast.makeText(activity, "Please grant both permission to work camera properly!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void generateExport() {
        if (selectedType.equals("0")) {
            new ExportContact("0").execute();
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                new ExportContact("1").execute();
            } else {
                buildGoogleDriveClient();
            }
        }
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    private void checkPermissionToExecute(String[] permissions, int requestCode) {
//        boolean READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission
//                (activity, permissions[0]) !=
//                PackageManager.PERMISSION_GRANTED;
//        boolean WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission
//                (activity, permissions[1]) !=
//                PackageManager.PERMISSION_GRANTED;
//        if (READ_EXTERNAL_STORAGE || WRITE_EXTERNAL_STORAGE) {
//            requestPermissions(permissions, requestCode);
//        } else {
//            prepareToLoginUsingSocialMedia(requestCode);
//        }
//    }
//
//    private void prepareToLoginUsingSocialMedia(int requestCode) {
//        switch (requestCode) {
//            case GOOGLE_LOGIN_PERMISSION:
//                googleSignIn();
//                break;
//        }
//    }

    private class ExportContact extends AsyncTask<Void, Void, Void> {

        private String selectedType;

        ExportContact(String selectedType) {
            this.selectedType = selectedType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(activity, "Please wait...", false);
            vfile = "Contacts" + "_" + new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault()).format(System.currentTimeMillis()) + ".vcf";
        }

        protected Void doInBackground(Void... urls) {
            getVCF();
            return null;
        }

        protected void onPostExecute(Void result) {

            Utils.hideProgressDialog();

            Utils.showSuccessSnackBar(activity, activityContactSettings, getString(R.string.str_vcf_done));

            if (selectedType.equals("1")) {
                Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
            }
        }
    }

    public void getVCF() {

        try {
            Cursor phones = activity.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
                    null, null);
            if (phones != null) {
                phones.moveToFirst();

                while (phones.moveToNext()) {
                    String lookupKey = phones.getString(phones
                            .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

                    Uri uri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
                    AssetFileDescriptor fd;
                    try {
                        fd = activity.getContentResolver().openAssetFileDescriptor(uri,
                                "r");

                        /* Test for Android 7.0 */
//                        FileDescriptor fdd = fd.getFileDescriptor();
//                        InputStream in = new FileInputStream(fdd);
//                        System.out.println("RContacts BLA BLA Len is :: " + (int) fd.getDeclaredLength());
//                        byte[] buf = new byte[(int) fd.getDeclaredLength()];
//                        in.read(buf);
//                        fd.close();

                        FileInputStream fis = fd.createInputStream();
                        byte[] buf = new byte[(int) fd.getDeclaredLength()];
                        fis.read(buf);
                        String VCard = new String(buf);

                        File dir = new File(Environment.getExternalStorageDirectory()
                                .toString() + File.separator + "RContacts");

                        try {
                            if (!dir.exists())
                                dir.mkdir();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        filePath = dir + File.separator + vfile;
                        FileOutputStream mFileOutputStream = new FileOutputStream(filePath,
                                true);
                        mFileOutputStream.write(VCard.getBytes());
//                        phones.moveToNext();
                        Log.d("Vcard", VCard);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                phones.close();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

//    private void googleSignIn() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

//    private void handleSignInResult(GoogleSignInResult result) {
//        Log.i("Sign In Result", "handleSignInResult:" + result.isSuccess());
//        if (result.isSuccess()) {
//            // Signed in successfully.
//            GoogleSignInAccount acct = result.getSignInAccount();
//
//            if (acct != null) {
//                Drive.DriveApi.newDriveContents(mGoogleApiClient);
//                new ExportContact("1").execute();
//            }
//        } else {
//            Utils.showErrorSnackBar(activity, activityContactSettings, getString(R.string.error_retrieving_details));
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mGoogleApiClient == null) {
//
//            /**
//             * Create the API client and bind it to an instance variable.
//             * We use this instance as the callback for connection and connection failures.
//             * Since no account name is passed, the user is prompted to choose.
//             */
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addApi(Drive.API)
//                    .addScope(Drive.SCOPE_FILE)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .build();
//        }
//
//        mGoogleApiClient.connect();
    }


    private void buildGoogleDriveClient() {
        if (mGoogleApiClient == null) {

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google API client connection
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * It invoked when Google API client connected
     *
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Drive.DriveApi.newDriveContents(mGoogleApiClient);
//        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
        new ExportContact("1").execute();
    }

    /*handles suspended connection callbacks*/
    @Override
    public void onConnectionSuspended(int cause) {
        switch (cause) {
            case 1:
                System.out.println("Connection suspended - Cause: " + "Service disconnected");
                break;
            case 2:
                System.out.println("Connection suspended - Cause: " + "Connection lost");
                break;
            default:
                System.out.println("Connection suspended - Cause: " + "Unknown");
                break;
        }
    }

    /*callback when there there's an error connecting the client to the service.*/
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        System.out.println("Connection failed");
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try {
            System.out.println("trying to resolve the Connection failed error...");
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            System.out.println("Exception while starting resolution activity " + e);
        }
    }


    /*Handles onConnectionFailed callbacks*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case REQUEST_CODE_RESOLUTION:
                if (resultCode == Activity.RESULT_OK)
                    if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                        mGoogleApiClient.connect();
                    }
                break;

            case REQUEST_CODE_CREATOR:
                if (resultCode == Activity.RESULT_OK) {
                    System.out.println("File added to Drive");
                    Utils.showSuccessSnackBar(activity, activityContactSettings, "File successfully added to Drive");
                } else {
                    System.out.println("Error creating the file");
                    Utils.showErrorSnackBar(activity, activityContactSettings, "Error adding file to Drive");
                }
                break;
        }
    }

    /*callback on getting the drive contents, contained in result*/
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        System.out.println("Error creating new file contents");
                        return;
                    }
                    final DriveContents driveContents = result.getDriveContents();
                    new Thread() {
                        @Override
                        public void run() {
                            OutputStream outputStream = driveContents.getOutputStream();
                            addTextfileToOutputStream(outputStream);

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(new File(vfile).getName())
                                    .setMimeType("text/vcf")
                                    .setDescription("This is a contact vcf file uploaded from device")
                                    .setStarred(true).build();

                            // Create an intent for the file chooser, and start it.
                            IntentSender intentSender = Drive.DriveApi
                                    .newCreateFileActivityBuilder()
                                    .setInitialMetadata(changeSet)
                                    .setInitialDriveContents(driveContents)
                                    .build(mGoogleApiClient);
                            try {
                                startIntentSenderForResult(
                                        intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                            } catch (IntentSender.SendIntentException e) {
                                System.out.println("Failed to launch file chooser.");
                            }

//                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
//                                    .createFile(mGoogleApiClient, changeSet, driveContents)
//                                    .setResultCallback(fileCallback);
                        }
                    }.start();
                }
            };

    private void addTextfileToOutputStream(OutputStream outputStream) {
        System.out.println("adding text file to outputstream...");
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {

            File file = new File(filePath);

            BufferedInputStream inputStream = new BufferedInputStream(
                    new FileInputStream(file));
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            System.out.println("problem converting input stream to output stream: " + e);
            e.printStackTrace();
        }
    }

    /*callback after creating the file, can get file info out of the result*/
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        System.out.println("Error creating the file");
                        Toast.makeText(activity, "Error adding file to Drive", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    System.out.println("File added to Drive");
                    System.out.println("Created a file with content: "
                            + result.getDriveFile().getDriveId());
                    Toast.makeText(activity, "File successfully added to Drive", Toast.LENGTH_SHORT).show();
                    final PendingResult<DriveResource.MetadataResult> metadata
                            = result.getDriveFile().getMetadata(mGoogleApiClient);
                    metadata.setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                        @Override
                        public void onResult(DriveResource.MetadataResult metadataResult) {

                            Metadata data = metadataResult.getMetadata();
                            System.out.println("Title: " + data.getTitle());
                            drive_id = data.getDriveId().encodeToString();
                            System.out.println("DrivId: " + drive_id);
                            driveID = data.getDriveId();
                            System.out.println("Description: " + data.getDescription());
                            System.out.println("MimeType: " + data.getMimeType());
                            System.out.println("File size: " + String.valueOf(data.getFileSize()));
                        }
                    });
                }
            };
}

//    private void check_folder_exists() {
//        Query query =
//                new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, FOLDER_NAME),
//                        Filters.eq(SearchableField.TRASHED, false))).build();
//
//        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
//            @Override
//            public void onResult(DriveApi.MetadataBufferResult result) {
//                if (!result.getStatus().isSuccess()) {
//                    System.out.println("RContact Cannot create folder in the root.");
//                } else {
//                    boolean isFound = false;
//                    for (Metadata m : result.getMetadataBuffer()) {
//                        if (m.getTitle().equals(FOLDER_NAME)) {
//                            System.out.println("RContact Folder exists");
//                            isFound = true;
//                            DriveId driveId = m.getDriveId();
//                            create_file_in_folder(driveId);
//                            break;
//                        }
//                    }
//                    if (!isFound) {
//                        System.out.println("RContact Folder not found; creating it.");
//                        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(FOLDER_NAME).build();
//                        Drive.DriveApi.getRootFolder(mGoogleApiClient)
//                                .createFolder(mGoogleApiClient, changeSet)
//                                .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
//                                    @Override
//                                    public void onResult(DriveFolder.DriveFolderResult result) {
//                                        if (!result.getStatus().isSuccess()) {
//                                            System.out.println("RContact U AR A MORON! Error while trying to create the folder");
//                                        } else {
//                                            System.out.println("RContact Created a folder");
//                                            DriveId driveId = result.getDriveFolder().getDriveId();
//                                            create_file_in_folder(driveId);
//                                        }
//                                    }
//                                });
//                    }
//                }
//            }
//        });
//    }
//
//    private void create_file_in_folder(final DriveId driveId) {
//
//        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
//            @Override
//            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {
//                if (!driveContentsResult.getStatus().isSuccess()) {
//                    System.out.println("RContact U AR A MORON! Error while trying to create new file contents");
//                    return;
//                }
//
//                OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();
//
//                //------ THIS IS AN EXAMPLE FOR FILE --------
//                Toast.makeText(activity, "Uploading to drive. ", Toast.LENGTH_LONG).show();
//                try {
//                    FileInputStream fileInputStream = new FileInputStream(new File(vfile));
//                    byte[] buffer = new byte[1024];
//                    int bytesRead;
//                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, bytesRead);
//                    }
//                } catch (IOException e1) {
//                    System.out.println("RContact U AR A MORON! Unable to write file contents.");
//                }
//
//                MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(new File(vfile).getName())
//                        .setMimeType("text/vcf").setStarred(false).build();
//                DriveFolder folder = driveId.asDriveFolder();
//                folder.createFile(mGoogleApiClient, changeSet, driveContentsResult.getDriveContents())
//                        .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
//                            @Override
//                            public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
//                                if (!driveFileResult.getStatus().isSuccess()) {
//                                    System.out.println("RContact U AR A MORON!  Error while trying to create the file");
//                                    return;
//                                }
//                                System.out.println("RContact Created a file: " + driveFileResult.getDriveFile().getDriveId());
//                            }
//                        });
//            }
//        });
//    }
