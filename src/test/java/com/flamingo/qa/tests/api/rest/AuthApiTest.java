package com.flamingo.qa.tests.api.rest;

import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.rest.client.AuthClient;
import com.flamingo.qa.api.rest.model.AuthResponse;
import com.flamingo.qa.config.TestConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Tag("rest")
class AuthApiTest {
    private final AuthClient authClient = new AuthClient();

    @Test
    void validCredentialsShouldReturnAuthToken() {
        ApiResponse<AuthResponse> response = authClient.authenticate(
                TestConfig.restUsername(),
                TestConfig.restPassword()
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().token()).isNotBlank();
    }
}
