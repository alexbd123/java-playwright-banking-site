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

    public static String getUsername() {
        return properties.getProperty("username");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }

    public static String getName() {
        return properties.getProperty("name");
    }

    public static String getLastName() {
        return properties.getProperty("lastname");
    }   

    public static String getAddress() {
        return properties.getProperty("address");
    }

    public static String getCity() {
        return properties.getProperty("city");
    }

    public static String getState() {
        return properties.getProperty("state");
    }

    public static String getZipCode() {
        return properties.getProperty("zipCode");
    }

    public static String getPhone() {
        return properties.getProperty("phone");
    }

    public static String getSsn() {
        return properties.getProperty("ssn");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(properties.getProperty("headless"));
    }

    public static String getBrowser() {
        return properties.getProperty("browser");
    }
}