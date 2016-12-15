package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.helper.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by user on 14/12/16.
 */

public class BottomSheetSocialMediaAdapter extends RecyclerView
        .Adapter<BottomSheetSocialMediaAdapter.SocialMediaViewHolder> {

    private Context context;
    private ArrayList<Integer> arrayListSocialMediaIcons;
    private ArrayList<String> arrayListSocialMediaNames;

    public BottomSheetSocialMediaAdapter(Context context) {
        this.context = context;
        arrayListSocialMediaIcons = new ArrayList<>();
        arrayListSocialMediaIcons.add(R.drawable.img_whatsapp);
        arrayListSocialMediaIcons.add(R.drawable.img_facebook);
        arrayListSocialMediaIcons.add(R.drawable.img_facebook_messenger);
        arrayListSocialMediaIcons.add(R.drawable.img_twitter);
        arrayListSocialMediaIcons.add(R.drawable.img_linkedin);
        arrayListSocialMediaIcons.add(R.drawable.img_skype);

        arrayListSocialMediaNames = new ArrayList<>();
        arrayListSocialMediaNames.add("Whatsapp");
        arrayListSocialMediaNames.add("Facebook");
        arrayListSocialMediaNames.add("Messenger");
        arrayListSocialMediaNames.add("Twitter");
        arrayListSocialMediaNames.add("Linkedin");
        arrayListSocialMediaNames.add("Skype");
    }

    @Override
    public SocialMediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_social_media,
                parent, false);
        return new BottomSheetSocialMediaAdapter.SocialMediaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SocialMediaViewHolder holder, int position) {
//        holder.imageSocialMedia.setImageResource(arrayListSocialMediaIcons.get(position));
        holder.imageSocialMedia.setImageDrawable(ContextCompat.getDrawable(context,
                arrayListSocialMediaIcons.get(position)));
        holder.textSocialMedia.setText(arrayListSocialMediaNames.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayListSocialMediaIcons.size();
    }

    class SocialMediaViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_social_media)
        ImageView imageSocialMedia;
        @BindView(R.id.text_social_media)
        TextView textSocialMedia;

        public SocialMediaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textSocialMedia.setTypeface(Utils.typefaceRegular(context));
        }
    }

}
