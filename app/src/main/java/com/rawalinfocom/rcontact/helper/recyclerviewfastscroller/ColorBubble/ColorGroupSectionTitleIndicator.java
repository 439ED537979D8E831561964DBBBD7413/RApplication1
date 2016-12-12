package com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.ColorBubble;

/**
 * Created by user on 09/12/16.
 */

import android.content.Context;
import android.util.AttributeSet;

import com.rawalinfocom.rcontact.helper.recyclerviewfastscroller.sectionindicator.title
        .SectionTitleIndicator;

/**
 * Indicator for sections of type
 */
public class ColorGroupSectionTitleIndicator extends SectionTitleIndicator<String> {

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
    public void setSection(String string) {

        // Using a single character
        setTitleText(string);

    }

}
