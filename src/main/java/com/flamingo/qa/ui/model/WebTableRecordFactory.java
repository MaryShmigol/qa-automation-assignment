package com.flamingo.qa.ui.model;

public class WebTableRecordFactory {
    private WebTableRecordFactory() {
    }
    public static WebTableRecord validRecord() {
        return new WebTableRecord(
                "Maria",
                "Klymenko",
                "maria.klymenko@example.com",
                37,
                5000,
                "Quality Assurance"
        );
    }

    public static WebTableRecord updatedRecord() {
        return new WebTableRecord(
                "Maria",
                "Klymenko",
                "maria.klymenko@example.com",
                37,
                6000,
                "Automation QA"
        );
    }

    public static WebTableRecord recordForSorting() {
        return new WebTableRecord(
                "Anna",
                "Smith",
                "anna.smith@example.com",
                25,
                15000,
                "Engineering"
        );
    }
}
