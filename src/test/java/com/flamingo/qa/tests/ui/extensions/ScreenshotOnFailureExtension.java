package com.flamingo.qa.tests.ui.extensions;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public final class ScreenshotOnFailureExtension implements TestWatcher {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private final Supplier<Page> pageSupplier;

    public ScreenshotOnFailureExtension(Supplier<Page> pageSupplier) {
        this.pageSupplier = pageSupplier;
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Page page = pageSupplier.get();
        if (page == null || page.isClosed()) {
            return;
        }

        try {
            Path directory = Path.of("target", "screenshots");
            Files.createDirectories(directory);

            String safeTestName = context.getDisplayName().replaceAll("[^a-zA-Z0-9._-]", "_");
            Path screenshotPath = directory.resolve(
                    safeTestName + "-" + LocalDateTime.now().format(TIMESTAMP) + ".png"
            );

            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(true));

            Allure.addAttachment(
                    "Screenshot on failure",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        } catch (IOException ignored) {
            // Artifact capture must never replace the original test failure.
        }
    }
}
