package com.rawalinfocom.rcontact.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rawalinfocom.rcontact.R;

/**
 * Created by Monal on 21/07/17.
 */

public class TutorialPagerAdapter extends PagerAdapter {

    Context context;
    /*private int images[] = {R.drawable.tutorial_1, R.drawable.tutorial_2,
            R.drawable.tutorial_3, R.drawable.tutorial_4, R.drawable.tutorial_5, R.drawable
            .tutorial_6};*/
    private int images[] = {R.drawable.tutorial_1};
    private LayoutInflater layoutInflater;

    public TutorialPagerAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.list_item_tutorial, container, false);

        ImageView imageView = itemView.findViewById(R.id.image_tutorial);
        imageView.setImageResource(images[position]);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

}
