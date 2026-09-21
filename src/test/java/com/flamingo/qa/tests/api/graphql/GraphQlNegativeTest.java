package com.flamingo.qa.tests.api.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.graphql.client.GraphQlClient;
import com.flamingo.qa.api.graphql.queries.GraphQlQueries;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Tag("graphql")
class GraphQlNegativeTest {
    private final GraphQlClient client = new GraphQlClient();

    @Test
    void nonExistingMovieIdShouldReturnNullEntityWithoutGraphQlErrors() {
        String nonExistingCuid = "clzzzzzzzzzzzzzzzzzzzzzzz";

        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIE_BY_ID,
                Map.of("id", nonExistingCuid)
        );

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body().path("data").path("movie").isNull()).isTrue();
        assertThat(response.body().path("errors").isMissingNode()
                || response.body().path("errors").isNull()).isTrue();
    }

    @Test
    void malformedQueryShouldReturnSyntaxErrorAndNoData() {
        ApiResponse<JsonNode> response = client.execute(GraphQlQueries.MALFORMED_QUERY);

        assertThat(response.statusCode()).isIn(200, 400);
        assertErrorsPresent(response.body());
        assertDataAbsent(response.body());
        assertThat(response.body().path("errors").get(0).path("message").asText()).isNotBlank();
    }

    @Test
    void unknownFieldShouldReturnValidationErrorAndNoData() {
        ApiResponse<JsonNode> response = client.execute(GraphQlQueries.NON_EXISTENT_FIELD);

        assertThat(response.statusCode()).isIn(200, 400);
        assertErrorsPresent(response.body());
        assertDataAbsent(response.body());

        String message = response.body().path("errors").get(0).path("message").asText();
        assertThat(message)
                .isNotBlank()
                .containsIgnoringCase("field")
                .contains("definitelyNotARealField");
    }

    private void assertErrorsPresent(JsonNode body) {
        JsonNode errors = body.path("errors");
        assertThat(errors.isArray()).isTrue();
        assertThat(errors.size()).isGreaterThan(0);
    }

    private void assertDataAbsent(JsonNode body) {
        JsonNode data = body.path("data");
        assertThat(data.isMissingNode() || data.isNull()).isTrue();
    }
}
