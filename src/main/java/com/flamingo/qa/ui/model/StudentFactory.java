package com.flamingo.qa.ui.model;

import java.nio.file.Path;
import java.util.List;

public final class StudentFactory {
    private StudentFactory() {
    }

    public static Student validStudent(Path uploadFile) {
        return new Student(
                "Maria",
                "Automation",
                "maria.automation@example.com",
                "Female",
                "1234567890",
                15,
                "May",
                1990,
                List.of("English"),
                List.of("Reading", "Music"),
                uploadFile,
                "Kyiv, Ukraine",
                "NCR",
                "Delhi"
        );
    }
}
