package com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.ColorBubble;

/**
 * Created by user on 09/12/16.
 */

import android.content.Context;
import android.util.AttributeSet;

import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.sectionindicator.title
        .SectionTitleIndicator;

/**
 * Indicator for sections of type {@link ColorGroup}
 */
public class ColorGroupSectionTitleIndicator extends SectionTitleIndicator<ColorGroup> {

    public ColorGroupSectionTitleIndicator(Context context) {
        super(context);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorGroupSectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(ColorGroup colorGroup) {
        // Example of using a single character
//        setTitleText(colorGroup.getName().charAt(0) + "");
        setTitleText("A");

        // Example of using a longer string
        // setTitleText(colorGroup.getName());

        setIndicatorTextColor(colorGroup.getAsColor());
    }

}
