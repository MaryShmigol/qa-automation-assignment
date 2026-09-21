package com.flamingo.qa.tests.ui;

import com.flamingo.qa.tests.ui.base.BaseUiTest;
import com.flamingo.qa.ui.model.WebTableRecord;
import com.flamingo.qa.ui.model.WebTableRecordFactory;
import com.flamingo.qa.ui.pages.WebTablesPage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.assertj.core.api.Assertions.assertThat;
@Tag("ui")
public class WebTablesUiTest extends BaseUiTest {

    @Test
    @Tag("smoke")
    void shouldAddNewRecord() {
        WebTableRecord expectedRecord =
                WebTableRecordFactory.validRecord();

        WebTablesPage webTablesPage = new WebTablesPage(page)
                .open()
                .addRecord(expectedRecord);

        WebTableRecord actualRecord =
                webTablesPage.recordByEmail(expectedRecord.email());

        assertThat(actualRecord)
                .as("Added record should match submitted data")
                .isEqualTo(expectedRecord);
    }

    @Test
    void shouldEditExistingRecord() {
        WebTableRecord originalRecord =
                WebTableRecordFactory.validRecord();

        WebTableRecord updatedRecord =
                WebTableRecordFactory.updatedRecord();

        WebTablesPage webTablesPage = new WebTablesPage(page)
                .open()
                .addRecord(originalRecord)
                .editRecord(
                        originalRecord.email(),
                        updatedRecord
                );

        WebTableRecord actualRecord =
                webTablesPage.recordByEmail(updatedRecord.email());

        assertThat(actualRecord)
                .as("Updated record should match expected data")
                .isEqualTo(updatedRecord);
    }

    @Test
    void shouldDeleteRecord() {
        WebTableRecord record = WebTableRecordFactory.validRecord();

        WebTablesPage webTablesPage = new WebTablesPage(page)
                .open()
                .addRecord(record)
                .deleteRecord(record.email());

        assertThat(webTablesPage.containsEmail(record.email()))
                .as("Deleted record should not be displayed in the table")
                .isFalse();
    }

    @Test
    void shouldFindRecordBySearch() {
        WebTableRecord record = WebTableRecordFactory.validRecord();

        WebTablesPage webTablesPage = new WebTablesPage(page)
                .open()
                .addRecord(record)
                .search(record.email());

        assertThat(webTablesPage.visibleEmails())
                .as("Search results should contain only the matching record")
                .containsExactly(record.email());
    }

    @Tag("known-issue")
    @Disabled("DemoQA table headers do not trigger sorting")
    @Test
    void shouldSortAgeAndSalaryAfterAddingNewRecord() {
        WebTableRecord newRecord =
                WebTableRecordFactory.recordForSorting();

        WebTablesPage webTablesPage = new WebTablesPage(page)
                .open()
                .addRecord(newRecord);

        webTablesPage.sortByAge();

        List<Integer> actualAges =
                webTablesPage.visibleAges();

        List<Integer> expectedAges = actualAges.stream()
                .sorted()
                .toList();

        webTablesPage.sortBySalary();

        List<Integer> actualSalaries =
                webTablesPage.visibleSalaries();

        List<Integer> expectedSalaries = actualSalaries.stream()
                .sorted()
                .toList();

        assertSoftly(softly -> {
            softly.assertThat(actualAges)
                    .as("Age column should contain the added value")
                    .contains(newRecord.age());

            softly.assertThat(actualAges)
                    .as("Age column should be sorted in ascending order")
                    .containsExactlyElementsOf(expectedAges);

            softly.assertThat(actualSalaries)
                    .as("Salary column should contain the added value")
                    .contains(newRecord.salary());

            softly.assertThat(actualSalaries)
                    .as("Salary column should be sorted in ascending order")
                    .containsExactlyElementsOf(expectedSalaries);
        });
    }
}
