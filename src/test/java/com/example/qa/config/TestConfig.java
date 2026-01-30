package com.example.qa.config;

import java.util.Properties;

public class TestConfig {

    private static final Properties properties = new Properties();

    public static String getBaseUrl() {
        return properties.getProperty("base.url");
    }
}