package com.rawalinfocom.rcontact;

import android.*;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.contacts.EditProfileActivity;
import com.rawalinfocom.rcontact.helper.RippleView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.ProfileDataOperation;
import com.rawalinfocom.rcontact.model.ReverseGeocodingAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactUsActivity extends BaseActivity implements RippleView
        .OnRippleCompleteListener {

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
    @BindView(R.id.activity_contact_settings)
    RelativeLayout activityContactSettings;
    @BindView(R.id.frameImageView1)
    FrameLayout frameImageView1;
    @BindView(R.id.frameImageView2)
    FrameLayout frameImageView2;
    @BindView(R.id.frameImageView3)
    FrameLayout frameImageView3;
    private Activity activity;
    private String strImage1 = "", strImage2 = "", strImage3 = "", selectedImageNumber = "";
    private Bitmap selectedBitmap;

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
                onBackPressed();
                break;
        }
    }

    //</editor-fold>
    private void init() {

        rippleActionRight.setVisibility(View.GONE);

        rippleActionBack.setOnRippleCompleteListener(this);
        rippleActionRight.setOnRippleCompleteListener(this);

        frameImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageNumber = "1";
                showChooseImageIntent();
            }
        });
        frameImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageNumber = "2";
                showChooseImageIntent();
            }
        });
        frameImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageNumber = "3";
                showChooseImageIntent();
            }
        });

        textToolbarTitle.setText(getResources().getString(R.string.str_contact_us));
        textToolbarTitle.setTypeface(Utils.typefaceRegular(this));

        textDescribeProblem.setTypeface(Utils.typefaceRegular(this));
        textAddScreenShot.setTypeface(Utils.typefaceRegular(this));
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
                if (ContextCompat.checkSelfPermission(activity, android.Manifest
                        .permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity, new
                            String[]{android.Manifest.permission.CAMERA}, AppConstants
                            .MY_PERMISSIONS_REQUEST_CAMERA);

                } else {
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

    private File mFileTemp;
    private Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_REQUEST_CODE = 500;
    private static final String IMAGE_DIRECTORY_NAME = "RContactImages";
    public static String TEMP_PHOTO_FILE_NAME = "";

    private void selectImageFromGallery() {
        mFileTemp = getOutputMediaFile(MEDIA_TYPE_IMAGE);

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GALLERY_IMAGE_REQUEST_CODE);
    }

    private void selectImageFromCamera() {
        mFileTemp = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private File getOutputMediaFile(int type) {

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
        if (type == MEDIA_TYPE_IMAGE) {

            TEMP_PHOTO_FILE_NAME = "IMG_" + timeStamp + ".jpg";
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + TEMP_PHOTO_FILE_NAME);

        } else {
            return null;
        }

        return mediaFile;
    }

    private Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void copyStream(InputStream inputStream, FileOutputStream fileOutputStream) throws
            IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            selectedBitmap = BitmapFactory.decodeFile(fileUri.getPath());

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2; //4, 8, etc. the more value, the worst quality of image
            try {
                selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream
                        (fileUri), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (selectedBitmap != null) {
                String bitmapString = Utils.convertBitmapToBase64(selectedBitmap, fileUri.getPath
                        ());
                switch (selectedImageNumber) {
                    case "1":

                        imgScreenshot1.setVisibility(View.VISIBLE);
                        img1.setVisibility(View.GONE);
                        strImage1 = bitmapString;

                        Glide.with(this)
                                .load(fileUri)
                                .bitmapTransform(new CropCircleTransformation(activity))
                                .into(imgScreenshot1);

                        break;
                    case "2":

                        imgScreenshot2.setVisibility(View.VISIBLE);
                        img2.setVisibility(View.GONE);
                        strImage2 = bitmapString;

                        Glide.with(this)
                                .load(fileUri)
                                .bitmapTransform(new CropCircleTransformation(activity))
                                .into(imgScreenshot1);

                        break;
                    case "3":

                        imgScreenshot3.setVisibility(View.VISIBLE);
                        img3.setVisibility(View.GONE);
                        strImage3 = bitmapString;

                        Glide.with(this)
                                .load(fileUri)
                                .bitmapTransform(new CropCircleTransformation(activity))
                                .into(imgScreenshot1);

                        break;
                }
            }


        } else if (requestCode == GALLERY_IMAGE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK)
                return;

            if (null == data)
                return;
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            String picturePath = "";
            if (c != null) {
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);
                c.close();
            }

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                if (inputStream != null) {
                    copyStream(inputStream, fileOutputStream);
                    inputStream.close();
                }
                fileOutputStream.close();

                selectedBitmap = BitmapFactory.decodeFile(picturePath);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; //4, 8, etc. the more value, the worst quality of image
                try {
                    selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream
                            (selectedImage), null, options);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (selectedBitmap != null) {
                    String bitmapString = Utils.convertBitmapToBase64(selectedBitmap, picturePath);

                    switch (selectedImageNumber) {
                        case "1":

                            imgScreenshot1.setVisibility(View.VISIBLE);
                            img1.setVisibility(View.GONE);
                            strImage1 = bitmapString;

                            Glide.with(this)
                                    .load(fileUri)
                                    .bitmapTransform(new CropCircleTransformation(activity))
                                    .into(imgScreenshot1);

                            break;
                        case "2":

                            imgScreenshot2.setVisibility(View.VISIBLE);
                            img2.setVisibility(View.GONE);
                            strImage2 = bitmapString;

                            Glide.with(this)
                                    .load(fileUri)
                                    .bitmapTransform(new CropCircleTransformation(activity))
                                    .into(imgScreenshot1);

                            break;
                        case "3":

                            imgScreenshot3.setVisibility(View.VISIBLE);
                            img3.setVisibility(View.GONE);
                            strImage3 = bitmapString;

                            Glide.with(this)
                                    .load(fileUri)
                                    .bitmapTransform(new CropCircleTransformation(activity))
                                    .into(imgScreenshot1);

                            break;
                    }
                }

            } catch (Exception e) {
                Log.e("TAG", "Error while creating temp file", e);
            }
        }
    }
}
