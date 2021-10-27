package org.green.chat.util;

import java.util.Random;

public final class ColorUtils {

    public static String randomColor() {
        return "#" + getRandom() + getRandom() + getRandom();
    }

    private static String getRandom() {
        return Integer.toHexString(new Random().nextInt(255));
    }
}
