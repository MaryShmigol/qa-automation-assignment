package com.flamingo.qa.api.graphql.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.graphql.model.GraphQlRequest;
import com.flamingo.qa.config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class GraphQlClient {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiResponse<JsonNode> execute(String query) {
        return execute(query, Map.of());
    }

    public ApiResponse<JsonNode> execute(String query, Map<String, Object> variables) {
        Response response = given()
                .baseUri(TestConfig.graphQlBaseUrl())
                .contentType("application/json")
                .accept("application/json")
                .filter(new AllureRestAssured())
                .body(new GraphQlRequest(query, variables))
                .when()
                .post();

        return new ApiResponse<>(response.statusCode(), parse(response.asString()), response.asString());
    }

    private JsonNode parse(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("GraphQL response is not valid JSON: " + body, exception);
        }
    }
}
