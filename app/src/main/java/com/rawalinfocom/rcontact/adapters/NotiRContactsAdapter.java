package com.rawalinfocom.rcontact.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.rawalinfocom.rcontact.AboutHelpActivity;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.WsConstants;
import com.rawalinfocom.rcontact.helper.TouchImageView;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.NotiRContactsItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by maulik on 15/03/17.
 */

public class NotiRContactsAdapter extends RecyclerView.Adapter<NotiRContactsAdapter.MyViewHolder> implements View.OnClickListener {

    private List<NotiRContactsItem> list;
    private Activity activity;
    private File dir;

    public NotiRContactsAdapter(Activity activity, List<NotiRContactsItem> list) {
        this.activity = activity;
        this.list = list;

        dir = new File(Environment.getExternalStorageDirectory()
                .toString() + File.separator + "RContacts" + File.separator + "saved_images");

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.relative_row_main)
        RelativeLayout relativeRowMain;
        @BindView(R.id.linear_layout_banner)
        LinearLayout linearLayoutBanner;
        @BindView(R.id.image_rcontacts_icon)
        ImageView imageRcontactsIcon;
        @BindView(R.id.imgShare)
        ImageView imgShare;
        @BindView(R.id.imgBanner)
        ImageView imgBanner;
        @BindView(R.id.text_title)
        TextView textTitle;
        @BindView(R.id.text_noti_time)
        TextView textNotiTime;
        @BindView(R.id.text_detail_info)
        TextView textDetailInfo;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            textTitle.setTypeface(Utils.typefaceBold(activity));

            textTitle.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
            textNotiTime.setTypeface(Utils.typefaceRegular(activity));
            textDetailInfo.setTypeface(Utils.typefaceRegular(activity));
        }
    }

    public void updateList(List<NotiRContactsItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity)
                .inflate(R.layout.list_item_noti_rcontacts, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        NotiRContactsItem item = list.get(position);

        holder.textTitle.setText(item.getNotiTitle());
        holder.textNotiTime.setText(Utils.formatDateTime(item.getNotiTime(), "dd MMM, hh:mm a"));

        String text = item.getNotiDetails().trim();

        if (item.getNotiType().equalsIgnoreCase("text")) {
            if (text.length() > 100) {
                text = text.substring(0, 100) + "...";
                holder.textDetailInfo.setText(Html.fromHtml(text + "<font color='red'> <u>View More</u></font>"));

            } else {
                holder.textDetailInfo.setText(text);
            }
        } else {
            holder.textDetailInfo.setText(text);
        }

        if (item.getNotiType().equalsIgnoreCase("video")) {

            System.out.println("RContacts video image --> " + item.getNotiImage());

            holder.imageRcontactsIcon.setVisibility(View.VISIBLE);
            holder.imgBanner.setVisibility(View.GONE);
            holder.imgShare.setVisibility(View.VISIBLE);

            Glide.with(activity)
                    .load(item.getNotiImage())
                    .placeholder(R.drawable.ico_youtube_svg)
                    .error(R.drawable.ico_youtube_svg)
                    .into(holder.imageRcontactsIcon);

        } else if (item.getNotiType().equalsIgnoreCase("image")) {

            holder.imageRcontactsIcon.setVisibility(View.GONE);
            holder.imgShare.setVisibility(View.VISIBLE);
            holder.imgBanner.setVisibility(View.VISIBLE);

            Glide.with(activity)
                    .load(item.getNotiUrl())
                    .placeholder(R.drawable.rcontacticon)
                    .error(R.drawable.rcontacticon)
                    .into(holder.imgBanner);

        } else {
            holder.imageRcontactsIcon.setVisibility(View.GONE);
            holder.imgShare.setVisibility(View.GONE);
        }

        holder.imgShare.setTag(position);
        holder.imgShare.setOnClickListener(this);

        holder.textDetailInfo.setTag(position);
        holder.textTitle.setTag(position);
        holder.textDetailInfo.setOnClickListener(this);
        holder.textTitle.setOnClickListener(this);

        holder.linearLayoutBanner.setTag(position);
        holder.linearLayoutBanner.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        int pos;

        switch (view.getId()) {

            case R.id.text_title:
            case R.id.text_detail_info:

                pos = (int) view.getTag();

                if (list.get(pos).getNotiType().equalsIgnoreCase("video")) {

                    String youTubeId = list.get(pos).getNotiUrl().substring(list.get(pos).getNotiUrl().lastIndexOf("/") + 1);

                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youTubeId));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + youTubeId));
                    try {
                        activity.startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        activity.startActivity(webIntent);
                    }
                } else {

                    FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                    DialogFragment newFragment = new MyDialogFragment(list.get(pos).getNotiType(),
                            list.get(pos).getNotiDetails());
                    newFragment.show(ft, "dialog");
                }

                break;

            case R.id.linear_layout_banner:

                pos = (int) view.getTag();
                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                DialogFragment newFragment = new MyDialogFragment(list.get(pos).getNotiType(),
                        list.get(pos).getNotiUrl());
                newFragment.show(ft, "dialog");

                break;

            case R.id.imgShare:

                pos = (int) view.getTag();

                if (list.get(pos).getNotiType().equalsIgnoreCase("video")) {

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/*");
                    String shareBody = list.get(pos).getNotiTitle() + "\n\n" + list.get(pos).getNotiDetails() + "\n\n" +
                            list.get(pos).getNotiUrl();
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    activity.startActivity(sharingIntent);

                } else {

                    // Execute DownloadImage AsyncTask

                    File file = new File(dir, list.get(pos).getNotiUrl().substring(
                            list.get(pos).getNotiUrl().lastIndexOf("/") + 1));

                    if (file.exists()) {

                        String path = file.getAbsolutePath();

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("image/*");
                        String shareBody = list.get(pos).getNotiTitle() + "\n\n" + list.get(pos).getNotiDetails() + "\n\n";
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
                        activity.startActivity(sharingIntent);

                    } else {
                        new DownloadImage(pos).execute(list.get(pos).getNotiUrl());
                    }
                }

                break;
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
            Utils.showProgressDialog(activity, "Please wait...", false);
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

            String path = SaveImage(result,
                    list.get(pos).getNotiUrl().substring(list.get(pos).getNotiUrl().lastIndexOf("/") + 1));

            System.out.println("RContacts path --> " + path);

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            String shareBody = list.get(pos).getNotiTitle() + "\n\n" + list.get(pos).getNotiDetails() + "\n\n";
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
            activity.startActivity(sharingIntent);

        }
    }

    private String SaveImage(Bitmap finalBitmap, String imageName) {

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
        activity.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);

        System.out.println("RContacts file path --> " + file.getAbsolutePath());

        return file.getAbsolutePath();
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

    public static class MyDialogFragment extends DialogFragment {

        @BindView(R.id.image_banner)
        TouchImageView imageBanner;
        @BindView(R.id.image_action_back)
        ImageView imageActionBack;
        Unbinder unbinder;
        @BindView(R.id.txt_details)
        TextView txtDetails;

        private String notiType, notiUrl;

        public MyDialogFragment(String notiType, String notiUrl) {
            this.notiType = notiType;
            this.notiUrl = notiUrl;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(DialogFragment.STYLE_NORMAL, R.style.MyCustomTheme);
        }

        @Override
        public void onStart() {
            super.onStart();
            Dialog d = getDialog();
            if (d != null) {
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                d.getWindow().setLayout(width, height);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.dialog_rconatcts_notification, container, false);
            unbinder = ButterKnife.bind(this, v);

            imageActionBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            txtDetails.setTypeface(Utils.typefaceBold(getActivity()));

            if (notiType.equalsIgnoreCase("image")) {

                txtDetails.setVisibility(View.GONE);
                imageBanner.setVisibility(View.VISIBLE);

                Glide.with(getActivity())
                        .load(notiUrl)
                        .placeholder(R.drawable.home_screen_profile)
                        .error(R.drawable.home_screen_profile)
                        .into(imageBanner);

            } else {

                txtDetails.setVisibility(View.VISIBLE);
                imageBanner.setVisibility(View.GONE);

                txtDetails.setText(notiUrl);
            }

            return v;
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            unbinder.unbind();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
