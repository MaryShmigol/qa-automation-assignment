package com.flamingo.qa.api.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flamingo.qa.config.TestConfig;
import io.restassured.response.Response;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

abstract class RestClientSupport {
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.restBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .addFilter(new AllureRestAssured())
                .build();
    }

    protected <T> T deserialize(Response response, Class<T> responseType) {
        String rawBody = response.asString();

        if (rawBody == null || rawBody.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readValue(rawBody, responseType);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Unable to deserialize response." +
                            "\nStatus: " + response.statusCode() +
                            "\nContent-Type: " + response.contentType() +
                            "\nBody: " + rawBody,
                    exception
            );
        }
    }
}
