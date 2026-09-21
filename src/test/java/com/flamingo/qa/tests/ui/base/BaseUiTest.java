package com.flamingo.qa.tests.ui.base;

import com.flamingo.qa.config.TestConfig;
import com.flamingo.qa.tests.ui.extensions.ScreenshotOnFailureExtension;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

public abstract class BaseUiTest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @RegisterExtension
    final ScreenshotOnFailureExtension screenshotOnFailure = new ScreenshotOnFailureExtension(() -> page);

    @BeforeEach
    void startBrowser() {
        playwright = Playwright.create();
        browser = browserType().launch(new BrowserType.LaunchOptions()
                .setHeadless(TestConfig.headless()));

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 1000));
        context.setDefaultTimeout(TestConfig.uiTimeoutMs());
        page = context.newPage();
    }

    @AfterEach
    void stopBrowser() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    private BrowserType browserType() {
        return switch (TestConfig.browser()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            case "chromium", "chrome" -> playwright.chromium();
            default -> throw new IllegalArgumentException("Unsupported browser: " + TestConfig.browser());
        };
    }
}
