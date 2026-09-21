package com.flamingo.qa.ui.pages;

import com.flamingo.qa.config.TestConfig;
import com.flamingo.qa.ui.components.RegistrationResultModal;
import com.flamingo.qa.ui.model.Student;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

public final class StudentRegistrationPage {
    private final Page page;

    private final Locator firstName;
    private final Locator lastName;
    private final Locator email;
    private final Locator mobile;
    private final Locator dateOfBirth;
    private final Locator subjects;
    private final Locator uploadPicture;
    private final Locator currentAddress;
    private final Locator submit;

    public StudentRegistrationPage(Page page) {
        this.page = page;
        this.firstName = page.locator("#firstName");
        this.lastName = page.locator("#lastName");
        this.email = page.locator("#userEmail");
        this.mobile = page.locator("#userNumber");
        this.dateOfBirth = page.locator("#dateOfBirthInput");
        this.subjects = page.locator("#subjectsInput");
        this.uploadPicture = page.locator("#uploadPicture");
        this.currentAddress = page.locator("#currentAddress");
        this.submit = page.locator("#submit");
    }

    public StudentRegistrationPage open() {
        page.navigate(TestConfig.uiBaseUrl() + "/automation-practice-form");
        firstName.waitFor();

        // DemoQA injects fixed advertising chrome that can cover controls. Hiding only that
        // third-party presentation noise keeps the test focused on the form itself.
        page.addStyleTag(new Page.AddStyleTagOptions()
                .setContent("#fixedban, footer { display: none !important; }"));

        return this;
    }

    public RegistrationResultModal submit(Student student) {
        firstName.fill(student.firstName());
        lastName.fill(student.lastName());
        email.fill(student.email());
        selectGender(student.gender());
        mobile.fill(student.mobile());
        selectDate(student.birthDay(), student.birthMonth(), student.birthYear());

        student.subjects().forEach(this::selectSubject);
        student.hobbies().forEach(this::selectHobby);

        uploadPicture.setInputFiles(student.picture());
        currentAddress.fill(student.currentAddress());
        selectReactOption("state", student.state());
        selectReactOption("city", student.city());

        submit.scrollIntoViewIfNeeded();
        submit.click();

        RegistrationResultModal modal = new RegistrationResultModal(page);
        page.locator(".modal-content").waitFor();
        return modal;
    }

    public StudentRegistrationPage submitEmptyForm() {
        submit.scrollIntoViewIfNeeded();
        submit.click();
        return this;
    }

    public boolean isSuccessModalVisible() {
        return page.locator(".modal-content").isVisible();
    }

    public boolean isFirstNameValid() {
        return (Boolean) firstName.evaluate("element => element.checkValidity()");
    }

    public boolean isLastNameValid() {
        return (Boolean) lastName.evaluate("element => element.checkValidity()");
    }

    public boolean isMobileValid() {
        return (Boolean) mobile.evaluate("element => element.checkValidity()");
    }

    private void selectGender(String gender) {
        page.locator("label[for='gender-radio-" + genderIndex(gender) + "']").click();
    }

    private int genderIndex(String gender) {
        return switch (gender.toLowerCase()) {
            case "male" -> 1;
            case "female" -> 2;
            case "other" -> 3;
            default -> throw new IllegalArgumentException("Unsupported gender: " + gender);
        };
    }

    private void selectDate(int day, String month, int year) {
        dateOfBirth.click();
        page.locator(".react-datepicker__month-select")
                .selectOption(new SelectOption().setLabel(month));
        page.locator(".react-datepicker__year-select")
                .selectOption(String.valueOf(year));

        String dayClass = String.format(".react-datepicker__day--%03d", day);
        page.locator(dayClass + ":not(.react-datepicker__day--outside-month)").click();
    }

    private void selectSubject(String subject) {
        subjects.fill(subject);
        page.locator("[id^='react-select-'][id*='-option-']")
                .filter(new Locator.FilterOptions().setHasText(subject))
                .first()
                .click();
    }

    private void selectHobby(String hobby) {
        String checkboxId = switch (hobby.toLowerCase()) {
            case "sports" -> "hobbies-checkbox-1";
            case "reading" -> "hobbies-checkbox-2";
            case "music" -> "hobbies-checkbox-3";
            default -> throw new IllegalArgumentException("Unsupported hobby: " + hobby);
        };

        page.locator("label[for='" + checkboxId + "']").click();
    }

    private void selectReactOption(String containerId, String optionText) {
        page.locator("#" + containerId).click();
        page.locator("[role='option']")
                .filter(new Locator.FilterOptions().setHasText(optionText))
                .first()
                .click();
    }
}
