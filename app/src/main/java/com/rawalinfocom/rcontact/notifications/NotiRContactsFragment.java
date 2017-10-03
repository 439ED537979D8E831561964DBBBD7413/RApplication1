
package com.rawalinfocom.rcontact.notifications;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rawalinfocom.rcontact.BaseFragment;
import com.rawalinfocom.rcontact.BaseNotificationFragment;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.adapters.NotiRContactsAdapter;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.database.TableRCNotificationUpdates;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.NotiRContactsItem;
import com.rawalinfocom.rcontact.model.NotificationData;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRContactsFragment extends BaseNotificationFragment implements WsResponseListener {

    TableRCNotificationUpdates tableRCNotificationUpdates;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerRContactsNoti;
    NotiRContactsAdapter updtaesAdapter;
    private File dir;
    private Uri fileUri;
    private int sharePosition = 0;

    private ArrayList<NotiRContactsItem> updates;

    @Override
    public void getFragmentArguments() {

    }

    public static NotiRContactsFragment newInstance() {
        return new NotiRContactsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_rcontacts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        tableRCNotificationUpdates = new TableRCNotificationUpdates(getDatabaseHandler());
        initData();
    }

    private void initData() {

        dir = new File(Environment.getExternalStorageDirectory()
                .toString() + File.separator + "RContacts" + File.separator + "saved_images");

        updates = tableRCNotificationUpdates.getAllUpdatesFromDB();

        updtaesAdapter = new NotiRContactsAdapter(getActivity(), updates, new NotiRContactsAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {

                sharePosition = position;

                if (checkPermission()) {
                    shareImage();
                }

            }
        });
        recyclerRContactsNoti.setAdapter(updtaesAdapter);
        recyclerRContactsNoti.setLayoutManager(new LinearLayoutManager(getActivity()));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null)
                    ((NotificationsDetailActivity) getActivity()).updateNotificationCount(AppConstants.NOTIFICATION_TYPE_RUPDATE);
            }
        }, 800);

        getAllRContactUpdates();
        getImageFromVideo();
    }

    private void getImageFromVideo() {

        try {

            for (int i = 0; i < updates.size(); i++) {
                if (updates.get(i).getNotiType().equalsIgnoreCase("video")) {
                    String img_url = "http://img.youtube.com/vi/" +
                            updates.get(i).getNotiUrl().substring(updates.get(i).getNotiUrl().lastIndexOf("/") + 1)
                        /*Utils.extractYoutubeId(updates.get(i).getNotiUrl()) */ + "/0.jpg";

                    NotiRContactsItem notiRContactsItem = new NotiRContactsItem();
                    notiRContactsItem.setNotiId(updates.get(i).getNotiId());
                    notiRContactsItem.setNotiTitle(updates.get(i).getNotiTitle());
                    notiRContactsItem.setNotiDetails(updates.get(i).getNotiDetails());
                    notiRContactsItem.setNotiImage(img_url);
                    notiRContactsItem.setNotiType(updates.get(i).getNotiType());
                    notiRContactsItem.setNotiTime(updates.get(i).getNotiTime());
                    notiRContactsItem.setNotiUrl(updates.get(i).getNotiUrl());
                    updates.set(i, notiRContactsItem);
                }
            }

            if (updates.size() > 0)
                updtaesAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            System.out.println("RContacts error get image from video");
        }

    }

    private void getAllRContactUpdates() {

        WsRequestObject allUpdatesObject = new WsRequestObject();
        allUpdatesObject.setTimeStamp(Utils.getStringPreference(getActivity(),
                AppConstants.KEY_RCONTACTS_API_CALL_TIME_STAMP, ""));

        if (Utils.isNetworkAvailable(getActivity())) {
            new AsyncWebServiceCall(getActivity(), WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    allUpdatesObject, null, WsResponseObject.class, WsConstants
                    .REQ_GET_RCONTACT_UPDATES, "Getting updates..", true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    WsConstants.WS_ROOT_V2 + WsConstants.REQ_GET_RCONTACT_UPDATES);
        } else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.msg_no_internet),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {
        if (error == null) {
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_GET_RCONTACT_UPDATES)) {

                WsResponseObject wsResponseObject = (WsResponseObject) data;
                if (wsResponseObject != null) {

                    ArrayList<NotificationData> updatesData = wsResponseObject.getRcontactUpdate();
                    saveUpdatesToDb(updatesData);
                    Utils.setStringPreference(getActivity(), AppConstants.KEY_RCONTACTS_API_CALL_TIME_STAMP,
                            wsResponseObject.getTimestamp());
                }

                Utils.hideProgressDialog();

            }
        } else {
            Utils.hideProgressDialog();
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUpdatesToDb(ArrayList<NotificationData> updatesData) {
        if (updatesData == null) {
            return;
        }
        for (NotificationData rconUpdate : updatesData) {
            tableRCNotificationUpdates.addUpdate(rconUpdate);
        }

        if (updatesData.size() > 0)
            refreshAllList();
    }

    private void refreshAllList() {
        updates = tableRCNotificationUpdates.getAllUpdatesFromDB();
        updtaesAdapter.updateList(updates);
        getImageFromVideo();
    }

    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int readPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(
                        new String[listPermissionsNeeded.size()]), 1);
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
                    shareImage();
//                else
//                    Toast.makeText(activity, "Please grant both permission to work camera properly!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void shareImage() {

        // Execute DownloadImage AsyncTask

        File file = new File(dir, updates.get(sharePosition).getNotiUrl().substring(
                updates.get(sharePosition).getNotiUrl().lastIndexOf("/") + 1));

        if (file.exists()) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                fileUri = Uri.fromFile(new File(file.getAbsolutePath()));
                sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
//                sharingIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            } else {
                fileUri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getPackageName() + ".provider", new File(file.getAbsolutePath()));
                sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            }

            sharingIntent.setType("image/*");
            String shareBody = updates.get(sharePosition).getNotiTitle() + "\n\n" +
                    updates.get(sharePosition).getNotiDetails() + "\n\n";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            getActivity().startActivity(sharingIntent);

        } else {
            new DownloadImage(sharePosition).execute(updates.get(sharePosition).getNotiUrl());
        }
    }

    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        private int pos;

        DownloadImage(int pos) {
            this.pos = pos;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.showProgressDialog(getActivity(), "Please wait...", false);
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            Utils.hideProgressDialog();

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            File file = SaveImage(result,
                    updates.get(pos).getNotiUrl().substring(updates.get(pos).getNotiUrl().lastIndexOf("/") + 1));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                fileUri = Uri.fromFile(file);
                sharingIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            } else {
                fileUri = FileProvider.getUriForFile(getActivity(),
                        getActivity().getPackageName() + ".provider", file);
                sharingIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }

            sharingIntent.setType("image/*");
            String shareBody = updates.get(pos).getNotiTitle() + "\n\n" + updates.get(pos).getNotiDetails() + "\n\n";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            getActivity().startActivity(sharingIntent);

        }
    }

    private File SaveImage(Bitmap finalBitmap, String imageName) {

        try {
            if (!dir.exists())
                dir.mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = new File(dir, imageName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ContentValues image = getImageContent(file, imageName);
        getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);

//        System.out.println("RContacts file path --> " + file.getAbsolutePath());

        return file;
    }

    public ContentValues getImageContent(File parent, String imageName) {
        ContentValues image = new ContentValues();
        image.put(MediaStore.Images.Media.TITLE, imageName);
        image.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        image.put(MediaStore.Images.Media.DESCRIPTION, "App Image");
        image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        image.put(MediaStore.Images.Media.ORIENTATION, 0);
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, parent.toString()
                .toLowerCase().hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName()
                .toLowerCase());
        image.put(MediaStore.Images.Media.SIZE, parent.length());
        image.put(MediaStore.Images.Media.DATA, parent.getAbsolutePath());
        return image;
    }
}
