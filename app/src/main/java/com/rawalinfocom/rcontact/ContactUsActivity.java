package com.rawalinfocom.rcontact;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.asynctasks.AsyncWebServiceCall;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.enumerations.WSRequestType;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.helper.imgcrop.CropImage;
import com.rawalinfocom.rcontact.helper.imgcrop.CropImageView;
import com.rawalinfocom.rcontact.interfaces.WsResponseListener;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;
import com.rawalinfocom.rcontact.model.WsRequestObject;
import com.rawalinfocom.rcontact.model.WsResponseObject;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactUsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener, View.OnClickListener, WsResponseListener {

    @BindView(R.id.image_action_back)
    ImageView imageActionBack;
    @BindView(R.id.ripple_action_back)
    RippleView rippleActionBack;
    @BindView(R.id.text_toolbar_title)
    TextView textToolbarTitle;
    @BindView(R.id.image_right)
    ImageView imageRight;
    @BindView(R.id.ripple_action_right)
    RippleView rippleActionRight;
    @BindView(R.id.relative_action_back)
    RelativeLayout relativeActionBack;
    @BindView(R.id.text_describe_problem)
    TextView textDescribeProblem;
    @BindView(R.id.edit_text_problem)
    EditText editTextProblem;
    @BindView(R.id.edit_text_read_faq)
    Button editTextReadFaq;
    @BindView(R.id.text_add_screen_shot)
    TextView textAddScreenShot;
    @BindView(R.id.imgScreenshot1)
    ImageView imgScreenshot1;
    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.imgScreenshot2)
    ImageView imgScreenshot2;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.imgScreenshot3)
    ImageView imgScreenshot3;
    @BindView(R.id.img3)
    ImageView img3;
    @BindView(R.id.activity_contact_us)
    RelativeLayout activityContactUs;
    @BindView(R.id.frameImageView1)
    FrameLayout frameImageView1;
    @BindView(R.id.frameImageView2)
    FrameLayout frameImageView2;
    @BindView(R.id.frameImageView3)
    FrameLayout frameImageView3;
    private Activity activity;
    private String strImage1 = "", strImage2 = "", strImage3 = "", selectedImageNumber = "", strDescription;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        ButterKnife.bind(this);

        activity = ContactUsActivity.this;
        init();
    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.ripple_action_back:
                onBackPressed();
                break;
            case R.id.ripple_action_right:

                strDescription = editTextProblem.getText().toString().trim();
                ArrayList<String> arrayListScreenShot = new ArrayList<>();
                if (!strImage1.equals(""))
                    arrayListScreenShot.add(strImage1);
                if (!strImage2.equals(""))
                    arrayListScreenShot.add(strImage2);
                if (!strImage3.equals(""))
                    arrayListScreenShot.add(strImage3);

                if (strDescription.length() > 0) {
                    // Service call
                    contactUs(arrayListScreenShot);
                } else {
                    Utils.showErrorSnackBar(activity, activityContactUs, getString(R.string.str_describe_problem));
                }

                break;
        }
    }

    //</editor-fold>
    private void init() {

        rippleActionRight.setVisibility(View.VISIBLE);

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRight.setOnRippleCompleteListener(this);

        editTextReadFaq.setOnClickListener(this);
        frameImageView1.setOnClickListener(this);
        frameImageView2.setOnClickListener(this);
        frameImageView3.setOnClickListener(this);

        textToolbarTitle.setText(getResources().getString(R.string.str_contact_us));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        textDescribeProblem.setTypeface(Utils.typefaceRegular(this));
        textAddScreenShot.setTypeface(Utils.typefaceRegular(this));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.edit_text_read_faq:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(WsConstants.URL_FAQ));
                startActivity(i);
                break;
            case R.id.frameImageView1:
                selectedImageNumber = "1";
                showChooseImageIntent();
                break;
            case R.id.frameImageView2:
                selectedImageNumber = "2";
                showChooseImageIntent();
                break;
            case R.id.frameImageView3:
                selectedImageNumber = "3";
                showChooseImageIntent();
                break;
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(ContactUsActivity.this, fileUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(fileUri);
            }
        }

        if (requestCode == GALLERY_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {

            fileUri = data.getData();

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(ContactUsActivity.this, fileUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(fileUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                File file = new File(getRealPathFromURI(result.getUri()));
                Bitmap bitmap = Utils.decodeFile(file, 512, 512);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                if (bitmap != null) {

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

                    String bitmapString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

                    switch (selectedImageNumber) {
                        case "1":

                            imgScreenshot1.setVisibility(View.VISIBLE);
                            img1.setVisibility(View.GONE);
                            strImage1 = bitmapString;

                            Glide.with(this)
                                    .load(Uri.fromFile(file))
                                    .into(imgScreenshot1);

                            break;
                        case "2":

                            imgScreenshot2.setVisibility(View.VISIBLE);
                            img2.setVisibility(View.GONE);
                            strImage2 = bitmapString;

                            Glide.with(this)
                                    .load(Uri.fromFile(file))
                                    .into(imgScreenshot2);

                            break;
                        case "3":

                            imgScreenshot3.setVisibility(View.VISIBLE);
                            img3.setVisibility(View.GONE);
                            strImage3 = bitmapString;

                            Glide.with(this)
                                    .load(Uri.fromFile(file))
                                    .into(imgScreenshot3);

                            break;
                    }
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(ContactUsActivity.this, " " + getString(R.string.crop_failed) +
                        result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showChooseImageIntent() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_share_invite);
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(layoutParams.width, layoutParams.height);

        TextView textDialogTitle = (TextView) dialog.findViewById(R.id.text_dialog_title);
        TextView textFromContact = (TextView) dialog.findViewById(R.id.text_from_contact);
        TextView textFromSocialMedia = (TextView) dialog.findViewById(R.id.text_from_social_media);
        final TextView textRemovePhoto = (TextView) dialog.findViewById(R.id.text_remove_photo);

        RippleView rippleLeft = (RippleView) dialog.findViewById(R.id.ripple_left);
        Button buttonLeft = (Button) dialog.findViewById(R.id.button_left);

        textDialogTitle.setTypeface(Utils.typefaceSemiBold(this));
        textFromContact.setTypeface(Utils.typefaceRegular(this));
        textFromSocialMedia.setTypeface(Utils.typefaceRegular(this));
        buttonLeft.setTypeface(Utils.typefaceSemiBold(this));

        textDialogTitle.setText(R.string.str_upload_via);
        textFromContact.setText(R.string.str_take_photo);
        textFromSocialMedia.setText(R.string.str_choose_photo);
        textRemovePhoto.setText(R.string.str_remove_photo);

        switch (selectedImageNumber) {
            case "1":
                if (strImage1.equalsIgnoreCase("")) {
                    textRemovePhoto.setVisibility(View.GONE);
                } else {
                    textRemovePhoto.setVisibility(View.VISIBLE);
                }
                break;
            case "2":
                if (strImage2.equalsIgnoreCase("")) {
                    textRemovePhoto.setVisibility(View.GONE);
                } else {
                    textRemovePhoto.setVisibility(View.VISIBLE);
                }
                break;
            case "3":
                if (strImage3.equalsIgnoreCase("")) {
                    textRemovePhoto.setVisibility(View.GONE);
                } else {
                    textRemovePhoto.setVisibility(View.VISIBLE);
                }
                break;
        }

        buttonLeft.setText(R.string.action_cancel);

        rippleLeft.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
            }
        });

        textFromSocialMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (ContextCompat.checkSelfPermission(activity,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity, new
                            String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, AppConstants
                            .MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                } else {
                    selectImageFromGallery();
                }
            }
        });

        textFromContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (checkPermission()) {
                    selectImageFromCamera();
                }
            }
        });

        textRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

                switch (selectedImageNumber) {
                    case "1":
                        imgScreenshot1.setVisibility(View.GONE);
                        img1.setVisibility(View.VISIBLE);
                        strImage1 = "";
                        break;
                    case "2":
                        imgScreenshot2.setVisibility(View.GONE);
                        img2.setVisibility(View.VISIBLE);
                        strImage2 = "";
                        break;
                    case "3":
                        imgScreenshot3.setVisibility(View.GONE);
                        img3.setVisibility(View.VISIBLE);
                        strImage3 = "";
                        break;
                }
            }
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:

                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                boolean isCamera = perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
                boolean isStorage = perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
                boolean isStorageWrite = perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                if (isCamera && isStorage && isStorageWrite)
                    selectImageFromCamera();
                else
                    Utils.showErrorSnackBar(ContactUsActivity.this, activityContactUs, getString(R.string.camera_permission));
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    selectImageFromGallery();
                else
                    Utils.showErrorSnackBar(ContactUsActivity.this, activityContactUs, getString(R.string.storage_permission));
                break;

            case 3:

                if (fileUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // required permissions granted, start crop image activity
                    startCropImageActivity(fileUri);
                }

                break;
        }
    }
    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(ContactUsActivity.this);
    }

    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCamera = ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.CAMERA);
            int readPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            List<String> listPermissionsNeeded = new ArrayList<>();
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                        1);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

//    private File mFileTemp;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 500;
    private static final String IMAGE_DIRECTORY_NAME = "RContactImages";
    public static String TEMP_PHOTO_FILE_NAME = "";

    private void selectImageFromGallery() {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);
    }

    private void selectImageFromCamera() {

        File photoFile = getOutputMediaFile();

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (photoFile != null) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                fileUri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            } else {
                fileUri = FileProvider.getUriForFile(getApplicationContext(),
                        getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            }
        }
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment
                .DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " " +
                        "directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format
                (new Date());
        File mediaFile;

        TEMP_PHOTO_FILE_NAME = "IMG_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + TEMP_PHOTO_FILE_NAME);

        return mediaFile;
    }

    private void copyStream(InputStream inputStream, FileOutputStream fileOutputStream) throws
            IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = ContactUsActivity.this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void contactUs(ArrayList<String> arrayListScreenShot) {

//        Log.i("savePackages", phoneBookContacts.getContactStorageAccounts().toString());

        WsRequestObject contactus = new WsRequestObject();
        contactus.setArrayListScreenShot(arrayListScreenShot);
        contactus.setType("Android");
        contactus.setDescription(strDescription);

        if (Utils.isNetworkAvailable(activity)) {
            new AsyncWebServiceCall(this, WSRequestType.REQUEST_TYPE_JSON.getValue(),
                    contactus, null, WsResponseObject.class, WsConstants
                    .REQ_CONTACT_US, getResources().getString(R.string.msg_please_wait), true).executeOnExecutor(AsyncTask
                    .THREAD_POOL_EXECUTOR, WsConstants.WS_ROOT + WsConstants.REQ_CONTACT_US);
        } else {
            Utils.showErrorSnackBar(activity, activityContactUs, getResources().getString(R.string.msg_no_network));
        }
    }

    @Override
    public void onDeliveryResponse(String serviceType, Object data, Exception error) {

        if (error == null) {
            // <editor-fold desc="REQ_RCP_PROFILE_SHARING">
            if (serviceType.equalsIgnoreCase(WsConstants.REQ_CONTACT_US)) {

                Utils.hideProgressDialog();

                WsResponseObject contactUsResponse = (WsResponseObject) data;
                Utils.hideProgressDialog();
                if (contactUsResponse != null && StringUtils.equalsIgnoreCase
                        (contactUsResponse.getStatus(), WsConstants.RESPONSE_STATUS_TRUE)) {

                    Utils.showSuccessSnackBar(this, activityContactUs, getString(R.string.str_msg_problem));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onBackPressed();
                        }
                    }, 1000);

                } else {
                    if (contactUsResponse != null) {
                        Log.e("error response", contactUsResponse.getMessage());
                        Utils.showErrorSnackBar(this, activityContactUs,
                                contactUsResponse.getMessage());
                    } else {
                        Log.e("onDeliveryResponse: ", "contactUs null");
                        Utils.showErrorSnackBar(this, activityContactUs, getString(R
                                .string.msg_try_later));
                    }
                }

            }
        } else {
            Utils.hideProgressDialog();
        }
    }
}
