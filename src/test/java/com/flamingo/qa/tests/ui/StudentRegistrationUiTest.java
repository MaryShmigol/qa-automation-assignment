package com.flamingo.qa.tests.ui;

import com.flamingo.qa.tests.ui.base.BaseUiTest;
import com.flamingo.qa.ui.components.RegistrationResultModal;
import com.flamingo.qa.ui.model.Student;
import com.flamingo.qa.ui.model.StudentFactory;
import com.flamingo.qa.ui.pages.StudentRegistrationPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("ui")
class StudentRegistrationUiTest extends BaseUiTest {

    @Test
    @Tag("smoke")
    void validStudentFormShouldShowSubmittedDataInSuccessModal() {
        Path uploadFile = Path.of("src", "test", "resources", "files", "sample-upload.txt").toAbsolutePath();
        Student student = StudentFactory.validStudent(uploadFile);
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page).open();

        RegistrationResultModal modal = registrationPage.submit(student);
        Map<String, String> values = modal.submittedValues();

        assertThat(modal.isVisible()).isTrue();
        assertThat(modal.title()).isEqualTo("Thanks for submitting the form");
        assertThat(values.get("Student Name")).isEqualTo(student.firstName() + " " + student.lastName());
        assertThat(values.get("Student Email")).isEqualTo(student.email());
        assertThat(values.get("Gender")).isEqualTo(student.gender());
        assertThat(values.get("Mobile")).isEqualTo(student.mobile());
        assertThat(values.get("Date of Birth")).isEqualTo("15 May,1990");
        assertThat(values.get("Subjects")).contains("English");
        assertThat(values.get("Hobbies")).contains("Reading", "Music");
        assertThat(values.get("Picture")).isEqualTo("sample-upload.txt");
        assertThat(values.get("Address")).isEqualTo(student.currentAddress());
        assertThat(values.get("State and City")).isEqualTo(student.state() + " " + student.city());
    }

    @Test
    void emptyRequiredFieldsShouldPreventFormSubmission() {
        StudentRegistrationPage registrationPage = new StudentRegistrationPage(page)
                .open()
                .submitEmptyForm();

        assertThat(registrationPage.isSuccessModalVisible()).isFalse();
        assertThat(registrationPage.isFirstNameValid()).isFalse();
        assertThat(registrationPage.isLastNameValid()).isFalse();
        assertThat(registrationPage.isMobileValid()).isFalse();
    }
}
