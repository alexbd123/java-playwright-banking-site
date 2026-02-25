package com.example.qa.config;

import java.io.InputStream;
import java.util.Properties;

public class TestConfig {

    private static final Properties properties = new Properties();

        static {
        try (InputStream input =
                 TestConfig.class
                     .getClassLoader()
                     .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties not found");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getBaseUrl() {
        return properties.getProperty("base.url");
    }

    public static String getApiBaseUrl() {
            return properties.getProperty("api.base.url");
    }

    public static String getInitialBalance() {
            return properties.getProperty("initial.balance");
    }

    public static boolean getHeadless() { return Boolean.parseBoolean(properties.getProperty("headless")); }
}