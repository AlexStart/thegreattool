package com.sam.jcc.cloud.utils;

import com.google.common.annotations.VisibleForTesting;
import com.sam.jcc.cloud.i.OSDependent;

import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

/**
 * @author Alexey Zhytnik
 * @since 30-Dec-16
 */
@OSDependent("Resolves only Windows & Unix")
public class SystemUtils {

    private static boolean isWindows;

    static {
        setUp();
    }

    private SystemUtils() {
    }

    private static void setUp() {
        isWindows = IS_OS_WINDOWS;
    }

    public static boolean isWindowsOS() {
        return isWindows;
    }

    @VisibleForTesting public static void setWindowsOS(boolean windowsOS) {
        isWindows = windowsOS;
    }

    @VisibleForTesting public static void resetOSSettings() {
        setUp();
    }
}
