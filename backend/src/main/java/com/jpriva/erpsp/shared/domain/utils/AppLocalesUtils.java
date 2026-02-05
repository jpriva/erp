package com.jpriva.erpsp.shared.domain.utils;

import java.util.List;
import java.util.Locale;

public class AppLocalesUtils {
    private AppLocalesUtils() {
    }

    public static final List<Locale> SUPPORTED = List.of(
            Locale.ENGLISH,
            Locale.forLanguageTag("es")
    );

    public static final Locale DEFAULT = Locale.ENGLISH;

    public static Locale resolveSafeLocale(String inputLang) {
        if (inputLang == null || inputLang.isBlank()) {
            return AppLocalesUtils.DEFAULT;
        }

        Locale requestLocale = Locale.forLanguageTag(inputLang);
        if (SUPPORTED.contains(requestLocale)) {
            return requestLocale;
        }

        for (Locale supported : AppLocalesUtils.SUPPORTED) {
            if (supported.getLanguage().equals(requestLocale.getLanguage())) {
                return supported;
            }
        }

        return AppLocalesUtils.DEFAULT;
    }
}
