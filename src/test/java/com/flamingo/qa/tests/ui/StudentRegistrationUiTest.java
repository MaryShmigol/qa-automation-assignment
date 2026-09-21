package com.flamingo.qa.tests.ui;

import com.flamingo.qa.tests.ui.base.BaseUiTest;
import com.flamingo.qa.ui.components.RegistrationResultModal;
import com.flamingo.qa.ui.model.Student;
import com.flamingo.qa.ui.model.StudentFactory;
import com.flamingo.qa.ui.pages.StudentRegistrationPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.nio.file.Path;
import java.util.Map;

@Tag("ui")
class StudentRegistrationUiTest extends BaseUiTest {
    private static final Path UPLOAD_FILE = Path.of(
            "src",
            "test",
            "resources",
            "files",
            "sample-upload.txt"
    ).toAbsolutePath();

    @Test
    @Tag("smoke")
    void validStudentFormShouldShowSubmittedDataInSuccessModal() {
        Student student = StudentFactory.validStudent(UPLOAD_FILE);
        StudentRegistrationPage registrationPage =
                new StudentRegistrationPage(page).open();

        RegistrationResultModal modal = registrationPage.submit(student);
        Map<String, String> submittedValues = modal.submittedValues();

        assertSoftly(softly -> {
            softly.assertThat(modal.isVisible())
                    .as("Submission modal should be visible")
                    .isTrue();

            softly.assertThat(modal.title())
                    .as("Submission modal title")
                    .isEqualTo("Thanks for submitting the form");

            softly.assertThat(submittedValues)
                    .as("Submitted student data")
                    .containsEntry(
                            "Student Name",
                            student.firstName() + " " + student.lastName()
                    )
                    .containsEntry("Student Email", student.email())
                    .containsEntry("Gender", student.gender())
                    .containsEntry("Mobile", student.mobile())
                    .containsEntry(
                            "Date of Birth",
                            "%d %s,%d".formatted(
                                    student.birthDay(),
                                    student.birthMonth(),
                                    student.birthYear()
                            )
                    )
                    .containsEntry(
                            "Subjects",
                            String.join(", ", student.subjects())
                    )
                    .containsEntry(
                            "Hobbies",
                            String.join(", ", student.hobbies())
                    )
                    .containsEntry(
                            "Picture",
                            student.picture().getFileName().toString()
                    )
                    .containsEntry("Address", student.currentAddress())
                    .containsEntry(
                            "State and City",
                            student.state() + " " + student.city()
                    );
        });
    }

    @Test
    void emptyRequiredFieldsShouldPreventFormSubmission() {
        StudentRegistrationPage registrationPage =
                new StudentRegistrationPage(page)
                        .open()
                        .submitEmptyForm();

        assertSoftly(softly -> {
            softly.assertThat(registrationPage.isSuccessModalVisible())
                    .as("Success modal should not be visible")
                    .isFalse();

            softly.assertThat(registrationPage.isFirstNameValid())
                    .as("First name should be invalid")
                    .isFalse();

            softly.assertThat(registrationPage.isLastNameValid())
                    .as("Last name should be invalid")
                    .isFalse();

            softly.assertThat(registrationPage.isMobileValid())
                    .as("Mobile number should be invalid")
                    .isFalse();
        });
    }
}
