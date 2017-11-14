package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.common.base.MoreObjects;
import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.IntegerConstants;
import com.rawalinfocom.rcontact.helper.Utils;
import com.rawalinfocom.rcontact.helper.imagetransformation.CropCircleTransformation;
import com.rawalinfocom.rcontact.model.ProfileDataOperationOrganization;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Monal on 21/10/16.
 */

public class OrganizationListAdapter extends RecyclerView.Adapter<OrganizationListAdapter
        .OrganizationViewHolder> {

    private final int colorPineGreen;
    private Context context;
    private ArrayList<ProfileDataOperationOrganization> arrayListOrganization;


    public OrganizationListAdapter(Context context, ArrayList<ProfileDataOperationOrganization>
            arrayListOrganization) {
        this.context = context;
        this.arrayListOrganization = arrayListOrganization;
        colorPineGreen = ContextCompat.getColor(context, R.color.colorAccent);
    }

    @Override
    public OrganizationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .list_item_all_organization, parent, false);
        return new OrganizationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OrganizationViewHolder holder, int position) {

        holder.imgTic.setVisibility(View.GONE);
        holder.imgTic.setColorFilter(colorPineGreen);

        ProfileDataOperationOrganization organization = arrayListOrganization.get(position);

        holder.textSub.setText(organization.getOrgJobTitle());

        int orgRcpType = Integer.parseInt(StringUtils.defaultString(organization.getOrgRcpType()
                , String.valueOf(IntegerConstants.RCP_TYPE_CLOUD_PHONE_BOOK)));

        if (orgRcpType == IntegerConstants.RCP_TYPE_LOCAL_PHONE_BOOK) {
            holder.textMain.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.textSub.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        } else {
            holder.textMain.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textSub.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            holder.textType.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }

        if (MoreObjects.firstNonNull(organization.getIsVerify(), 0) == IntegerConstants.RCP_TYPE_PRIMARY) {

//            String s = Utils.setMultipleTypeface(context, organization.getOrgName() + " <font color" + "='#00796B'>" + context.getString(R.string.im_icon_verify)
//                    + "</font>", 0, (StringUtils.length(organization.getOrgName()) + 1), ((StringUtils.length(organization.getOrgName()) + 1) + 1)).toString();

            holder.textMain.setText(organization.getOrgName());
            holder.textMain.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_double_tick_svg, 0);
            holder.textType.setText(Html.fromHtml("<small> (" + organization.getOrgIndustryType() + ") </small>"));
//            holder.imgTic.setImageResource(R.drawable.ico_double_tick_svg);

        } else {

            holder.textMain.setText(organization.getOrgName());
            holder.textMain.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ico_relation_single_tick_svg, 0);
            holder.textType.setVisibility(View.GONE);
//            holder.imgTic.setImageResource(R.drawable.ico_relation_single_tick_svg);
        }

        Glide.with(context)
                .load(organization.getOrgLogo())
                .placeholder(R.drawable.default_org)
                .error(R.drawable.default_org)
                .bitmapTransform(new CropCircleTransformation(context))
                .override(120, 120)
                .into(holder.imageOrgProfile);

        if (StringUtils.equalsIgnoreCase(organization.getOrgToDate(), "")) {
            if (!StringUtils.isEmpty(organization.getOrgFromDate())) {
                String formattedFromDate = Utils.convertDateFormat(organization.getOrgFromDate(),
                        "yyyy-MM-dd", Utils.getEventDateFormat(organization.getOrgFromDate()));

                holder.textTime.setText(String.format("%s to Present ", formattedFromDate));
            } else {
                holder.textTime.setVisibility(View.GONE);
            }
        } else {
            if (!StringUtils.isEmpty(organization.getOrgFromDate()) && !StringUtils.isEmpty(organization.getOrgToDate())) {
                String formattedFromDate = Utils.convertDateFormat(organization.getOrgFromDate(),
                        "yyyy-MM-dd", Utils.getEventDateFormat(organization.getOrgFromDate()));
                String formattedToDate = Utils.convertDateFormat(organization.getOrgToDate(),
                        "yyyy-MM-dd", Utils.getEventDateFormat(organization.getOrgToDate()));

                holder.textTime.setText(String.format("%s to %s ", formattedFromDate, formattedToDate));
            }
        }
    }

    @Override
    public int getItemCount() {
        return arrayListOrganization.size();
    }

    class OrganizationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_main)
        TextView textMain;
        @BindView(R.id.text_sub)
        TextView textSub;
        @BindView(R.id.text_time)
        TextView textTime;
        @BindView(R.id.text_type)
        TextView textType;
        @BindView(R.id.image_org_profile)
        ImageView imageOrgProfile;
        @BindView(R.id.img_tic)
        ImageView imgTic;

        OrganizationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            textMain.setTypeface(Utils.typefaceRegular(context));
            textSub.setTypeface(Utils.typefaceRegular(context));
            textTime.setTypeface(Utils.typefaceRegular(context));
            textType.setTypeface(Utils.typefaceRegular(context));

            textTime.setVisibility(View.VISIBLE);
            textType.setVisibility(View.VISIBLE);

            imgTic.setVisibility(View.GONE);
        }
    }
}
