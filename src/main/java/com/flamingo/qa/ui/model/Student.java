package com.flamingo.qa.ui.model;

import java.nio.file.Path;
import java.util.List;

public record Student(
        String firstName,
        String lastName,
        String email,
        String gender,
        String mobile,
        int birthDay,
        String birthMonth,
        int birthYear,
        List<String> subjects,
        List<String> hobbies,
        Path picture,
        String currentAddress,
        String state,
        String city
) {
}
