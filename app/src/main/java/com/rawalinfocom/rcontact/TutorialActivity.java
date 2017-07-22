package com.rawalinfocom.rcontact;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rawalinfocom.rcontact.adapters.TutorialPagerAdapter;
import com.rawalinfocom.rcontact.helper.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorialActivity extends BaseActivity {

    private final int TUTORIAL_SCREENS = 5;

    @BindView(R.id.text_tutorial_header)
    TextView textTutorialHeader;
    @BindView(R.id.text_tutorial_content)
    TextView textTutorialContent;
    @BindView(R.id.text_next)
    TextView textNext;
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

    int pagerCurrentPosition = 0;

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
        textTutorialHeader.setText(getString(R.string.tutorial_header_1));
        textTutorialContent.setText(R.string.tutorial_content_1);
        textNext.setText(R.string.tutorial_and);

        textTutorialHeader.setTypeface(Utils.typefaceSemiBold(TutorialActivity.this));
        textTutorialContent.setTypeface(Utils.typefaceRegular(TutorialActivity.this));
        textNext.setTypeface(Utils.typefaceRegular(TutorialActivity.this));

        pagerTutorial.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                pagerCurrentPosition = position;
                setIndicatorSelection(position);

                switch (position) {
                    case 0:
                        textTutorialHeader.setText(getString(R.string.tutorial_header_1));
                        textTutorialContent.setText(R.string.tutorial_content_1);
                        textNext.setText(R.string.tutorial_and);
                        break;

                    case 1:
                        textTutorialHeader.setText(getString(R.string.tutorial_header_2));
                        textTutorialContent.setText(R.string.tutorial_content_2);
                        textNext.setText(R.string.tutorial_and);
                        break;

                    case 2:
                        textTutorialHeader.setText(getString(R.string.tutorial_header_3));
                        textTutorialContent.setText(R.string.tutorial_content_3);
                        textNext.setText(R.string.tutorial_and);
                        break;

                    case 3:
                        textTutorialHeader.setText(getString(R.string.tutorial_header_4));
                        textTutorialContent.setText(R.string.tutorial_content_4);
                        textNext.setText(R.string.tutorial_and);
                        break;

                    case 4:
                        textTutorialHeader.setText(getString(R.string.tutorial_header_5));
                        textTutorialContent.setText(R.string.tutorial_content_5);
                        textNext.setText(R.string.tutorial_lets_go);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        textNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pagerCurrentPosition != (TUTORIAL_SCREENS - 1)) {
                    pagerTutorial.setCurrentItem(++pagerCurrentPosition);
                } else {
                    Toast.makeText(TutorialActivity.this, "Last!", Toast.LENGTH_SHORT).show();
                }
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
