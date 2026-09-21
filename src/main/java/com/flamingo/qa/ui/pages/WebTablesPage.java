package com.flamingo.qa.ui.pages;

import com.flamingo.qa.config.TestConfig;
import com.flamingo.qa.ui.components.RegistrationFormModal;
import com.flamingo.qa.ui.model.WebTableRecord;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class WebTablesPage {
    private final Page page;
    private final Locator addButton;
    private final Locator searchInput;
    private final Locator rows;
    private final Locator firstNameHeader;

    public WebTablesPage(Page page) {
        this.page = page;
        this.addButton = page.locator("#addNewRecordButton");
        this.searchInput = page.locator("#searchBox");
        this.rows = page.locator("table tbody tr");
        this.firstNameHeader = page.locator("table thead th").nth(0);
    }

    public WebTablesPage open() {
        page.navigate(TestConfig.uiBaseUrl() + "/webtables");
        addButton.waitFor();

        page.addStyleTag(
                new Page.AddStyleTagOptions()
                        .setContent(
                                "#fixedban, footer { display: none !important; }"
                        )
        );

        return this;
    }

    public WebTablesPage addRecord(WebTableRecord record) {
        addButton.click();

        new RegistrationFormModal(page)
                .fill(record)
                .submit();

        return this;
    }

    public WebTablesPage editRecord(
            String email,
            WebTableRecord updatedRecord
    ) {
        rowByEmail(email)
                .locator("[title='Edit']")
                .click();

        new RegistrationFormModal(page)
                .fill(updatedRecord)
                .submit();

        return this;
    }

    public WebTablesPage deleteRecord(String email) {
        rowByEmail(email)
                .locator("[title='Delete']")
                .click();

        return this;
    }

    public WebTablesPage search(String value) {
        searchInput.fill(value);
        return this;
    }

    public WebTablesPage clearSearch() {
        searchInput.clear();
        return this;
    }

    public WebTablesPage sortByFirstName() {
        firstNameHeader.click();
        return this;
    }

    public boolean containsEmail(String email) {
        return rowByEmail(email).count() > 0;
    }

    public WebTableRecord recordByEmail(String email) {
        Locator row = rowByEmail(email);

        if (row.count() == 0) {
            return null;
        }

        List<String> cells = row.locator("td").allTextContents();

        return new WebTableRecord(
                cells.get(0),
                cells.get(1),
                cells.get(3),
                Integer.parseInt(cells.get(2)),
                Integer.parseInt(cells.get(4)),
                cells.get(5)
        );
    }

    public List<String> visibleEmails() {
        return rows.locator("td:nth-child(4)")
                .allTextContents()
                .stream()
                .filter(email -> !email.isBlank())
                .toList();
    }


    public List<Integer> visibleAges() {
        return rows.locator("td:nth-child(3)")
                .allTextContents()
                .stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Integer::parseInt)
                .toList();
    }

    public List<Integer> visibleSalaries() {
        return rows.locator("td:nth-child(5)")
                .allTextContents()
                .stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Integer::parseInt)
                .toList();
    }

    public WebTablesPage sortByAge() {
        page.locator("table thead th")
                .filter(new Locator.FilterOptions().setHasText("Age"))
                .click();

        return this;
    }

    public WebTablesPage sortBySalary() {
        page.locator("table thead th")
                .filter(new Locator.FilterOptions().setHasText("Salary"))
                .click();

        return this;
    }

    private Locator rowByEmail(String email) {
        return rows.filter(
                new Locator.FilterOptions().setHasText(email)
        );
    }
}
