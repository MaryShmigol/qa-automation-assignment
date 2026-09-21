package com.flamingo.qa.ui.components;

import com.flamingo.qa.ui.model.WebTableRecord;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class RegistrationFormModal {
    private final Locator modal;
    private final Locator firstName;
    private final Locator lastName;
    private final Locator email;
    private final Locator age;
    private final Locator salary;
    private final Locator department;
    private final Locator submitButton;

    public RegistrationFormModal(Page page) {
        this.modal = page.locator(".modal-content");
        this.firstName = page.locator("#firstName");
        this.lastName = page.locator("#lastName");
        this.email = page.locator("#userEmail");
        this.age = page.locator("#age");
        this.salary = page.locator("#salary");
        this.department = page.locator("#department");
        this.submitButton = page.locator("#submit");

        modal.waitFor();
    }

    public RegistrationFormModal fill(WebTableRecord record) {
        firstName.fill(record.firstName());
        lastName.fill(record.lastName());
        email.fill(record.email());
        age.fill(String.valueOf(record.age()));
        salary.fill(String.valueOf(record.salary()));
        department.fill(record.department());

        return this;
    }

    public void submit() {
        submitButton.click();

        modal.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
        );
    }
}
