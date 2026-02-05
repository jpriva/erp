package com.jpriva.erpsp.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Configuration
public class JsonI18nConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource() {
            @NonNull
            @Override
            protected ResourceBundle doGetBundle(@NonNull String basename, @NonNull Locale locale) throws MissingResourceException {
                return ResourceBundle.getBundle(
                        basename,
                        locale,
                        Objects.requireNonNull(getBundleClassLoader()),
                        new JsonResourceBundleControl()
                );
            }
        };

        messageSource.setBasenames(
                "i18n/auth/messages",
                "i18n/notification/messages",
                "i18n/shared/messages"
        );

        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    public static class JsonResourceBundleControl extends ResourceBundle.Control {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public List<String> getFormats(String baseName) {
            return List.of("json");
        }

        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return null;
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "json");

            try (InputStream stream = loader.getResourceAsStream(resourceName)) {
                if (stream != null) {
                    Map<String, Object> map = mapper.readValue(stream, new TypeReference<>() {
                    });
                    Map<String, Object> flatMap = flatten(map);

                    return new ResourceBundle() {
                        @Override
                        protected Object handleGetObject(@NonNull String key) {
                            return flatMap.get(key);
                        }

                        @NonNull
                        @Override
                        public Enumeration<String> getKeys() {
                            return Collections.enumeration(flatMap.keySet());
                        }
                    };
                }
            }
            return null;
        }

        private Map<String, Object> flatten(Map<String, Object> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> flatMap = new HashMap<>();
            flatten(map, flatMap, "");
            return flatMap;
        }

        private void flatten(Map<String, Object> source, Map<String, Object> target, String prefix) {
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                if (entry.getValue() instanceof Map) {
                    //noinspection unchecked
                    flatten((Map<String, Object>) entry.getValue(), target, key);
                } else {
                    target.put(key, entry.getValue());
                }
            }
        }
    }
}
