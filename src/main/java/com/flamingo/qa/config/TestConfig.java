package com.flamingo.qa.config;

public final class TestConfig {
    private TestConfig() {
    }

    public static String restBaseUrl() {
        return value("rest.baseUrl", "REST_BASE_URL", "https://restful-booker.herokuapp.com");
    }

    public static String restUsername() {
        return value("rest.username", "REST_USERNAME", "admin");
    }

    public static String restPassword() {
        return value("rest.password", "REST_PASSWORD", "password123");
    }

    public static String graphQlBaseUrl() {
        return value(
                "graphql.baseUrl",
                "GRAPHQL_BASE_URL",
                "https://api-us-east-1-shared-usea1-02.hygraph.com/v2/cluh3nib3000008jk3ippd87t/master"
        );
    }

    public static String uiBaseUrl() {
        return value("ui.baseUrl", "UI_BASE_URL", "https://demoqa.com");
    }

    public static String browser() {
        return value("browser", "BROWSER", "chromium").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(value("headless", "HEADLESS", "true"));
    }

    public static double uiTimeoutMs() {
        return Double.parseDouble(value("ui.timeout.ms", "UI_TIMEOUT_MS", "10000"));
    }

    private static String value(String systemProperty, String environmentVariable, String defaultValue) {
        String systemValue = System.getProperty(systemProperty);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return defaultValue;
    }
}
