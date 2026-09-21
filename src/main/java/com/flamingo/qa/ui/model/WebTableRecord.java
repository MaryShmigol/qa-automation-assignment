package com.flamingo.qa.ui.model;

public record WebTableRecord(
        String firstName,
        String lastName,
        String email,
        int age,
        int salary,
        String department
) {
}
