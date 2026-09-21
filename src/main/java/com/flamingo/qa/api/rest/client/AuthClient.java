package com.flamingo.qa.api.rest.client;

import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.rest.model.AuthRequest;
import com.flamingo.qa.api.rest.model.AuthResponse;
import com.flamingo.qa.config.TestConfig;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class AuthClient extends RestClientSupport {

    public ApiResponse<AuthResponse> authenticate(String username, String password) {
        Response response = given()
                .spec(requestSpec())
                .body(new AuthRequest(username, password))
                .when()
                .post("/auth");

        return new ApiResponse<>(response.statusCode(), response.as(AuthResponse.class), response.asString());
    }

    public String getToken() {
        ApiResponse<AuthResponse> response = authenticate(TestConfig.restUsername(), TestConfig.restPassword());

        if (response.statusCode() != 200 || response.body() == null || response.body().token() == null
                || response.body().token().isBlank()) {
            throw new IllegalStateException("Unable to obtain Restful Booker auth token. Response: " + response.rawBody());
        }

        return response.body().token();
    }
}
