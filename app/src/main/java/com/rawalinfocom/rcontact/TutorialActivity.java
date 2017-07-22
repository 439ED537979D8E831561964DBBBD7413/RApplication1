package com.rawalinfocom.rcontact;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rawalinfocom.rcontact.adapters.TutorialPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorialActivity extends BaseActivity {

    @BindView(R.id.text_tutorial_header)
    TextView textTutorialHeader;
    @BindView(R.id.text_tutorial_content)
    TextView textTutorialContent;
    @BindView(R.id.linear_indicator)
    LinearLayout linearIndicator;
    @BindView(R.id.relative_indicator)
    RelativeLayout relativeIndicator;
    @BindView(R.id.pager_tutorial)
    ViewPager pagerTutorial;
    @BindView(R.id.image_tutorial_1)
    ImageView imageTutorial1;
    @BindView(R.id.image_tutorial_2)
    ImageView imageTutorial2;
    @BindView(R.id.image_tutorial_3)
    ImageView imageTutorial3;
    @BindView(R.id.image_tutorial_4)
    ImageView imageTutorial4;
    @BindView(R.id.image_tutorial_5)
    ImageView imageTutorial5;

    TutorialPagerAdapter tutorialPagerAdapter;

    //<editor-fold desc="Override Methods">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);

        init();
    }
    //</editor-fold>

    //<editor-fold desc="Private Methods">
    private void init() {
        tutorialPagerAdapter = new TutorialPagerAdapter(TutorialActivity.this);
        pagerTutorial.setAdapter(tutorialPagerAdapter);
        setIndicatorSelection(0);


        pagerTutorial.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setIndicatorSelection(position);

                switch (position) {
                    case 0:
                        break;

                    case 1:
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setIndicatorSelection(int selection) {
        ImageView[] tutorialImages = {imageTutorial1, imageTutorial2, imageTutorial3,
                imageTutorial4, imageTutorial5};
        for (int i = 0; i < tutorialImages.length; i++) {
            if (i == selection) {
                tutorialImages[i].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor
                        (TutorialActivity.this, R.color.colorAccent), PorterDuff.Mode.MULTIPLY));
            } else {
                tutorialImages[i].setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor
                        (TutorialActivity.this, R.color.lightGrey), PorterDuff.Mode.MULTIPLY));
            }
        }
    }

    //</editor-fold>
}
