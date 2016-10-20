package com.rawalinfocom.rcontact.helper;

import android.os.Build;

/**
 * Created by Monal on 10/10/16.
 * <p>
 * Class containing some static utility methods.
 */

public class Utils {

    /**
     * Uses static final constants to detect if the device's platform version is Jellybean or
     * later.
     */
    public static boolean hasJellybean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

}
