package com.flamingo.qa.ui.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.LinkedHashMap;
import java.util.Map;

public final class RegistrationResultModal {
    private final Locator modal;

    public RegistrationResultModal(Page page) {
        this.modal = page.locator(".modal-content");
    }

    public boolean isVisible() {
        return modal.isVisible();
    }

    public String title() {
        return modal.locator("#example-modal-sizes-title-lg").textContent().trim();
    }

    public Map<String, String> submittedValues() {
        Map<String, String> values = new LinkedHashMap<>();
        Locator rows = modal.locator("tbody tr");

        for (int index = 0; index < rows.count(); index++) {
            Locator cells = rows.nth(index).locator("td");
            if (cells.count() >= 2) {
                values.put(cells.nth(0).innerText().trim(), cells.nth(1).innerText().trim());
            }
        }

        return values;
    }
}
