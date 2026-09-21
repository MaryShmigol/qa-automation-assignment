package com.flamingo.qa.tests.api.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.graphql.client.GraphQlClient;
import com.flamingo.qa.api.graphql.queries.GraphQlQueries;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;

@Tag("api")
@Tag("graphql")
@Tag("negative")
class GraphQlNegativeTest {
    private static final String NON_EXISTING_MOVIE_ID =
            "clzzzzzzzzzzzzzzzzzzzzzzz";

    private final GraphQlClient client = new GraphQlClient();

    @Test
    void nonExistingMovieIdShouldReturnNullEntityWithoutGraphQlErrors() {
        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIE_BY_ID,
                Map.of("id", NON_EXISTING_MOVIE_ID)
        );

        JsonNode responseBody = response.body();
        JsonNode movie = responseBody.path("data").path("movie");
        JsonNode errors = responseBody.path("errors");

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode())
                    .as("HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(movie.isNull())
                    .as("Movie should be null for a non-existing ID")
                    .isTrue();

            softly.assertThat(
                            errors.isMissingNode() || errors.isNull()
                    )
                    .as("GraphQL errors should be absent or null")
                    .isTrue();
        });
    }

    @Test
    void malformedQueryShouldReturnSyntaxErrorAndNoData() {
        ApiResponse<JsonNode> response =
                client.execute(GraphQlQueries.MALFORMED_QUERY);

        JsonNode responseBody = response.body();
        JsonNode errors = responseBody.path("errors");
        JsonNode data = responseBody.path("data");
        String errorMessage = errors.path(0)
                .path("message")
                .asText();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode())
                    .as("HTTP status code for a malformed GraphQL query")
                    .isIn(400);

            softly.assertThat(errors.isArray())
                    .as("GraphQL errors should be returned as an array")
                    .isTrue();

            softly.assertThat(errors.size())
                    .as("GraphQL errors array should not be empty")
                    .isPositive();

            softly.assertThat(
                            data.isMissingNode() || data.isNull()
                    )
                    .as("Data should be absent or null")
                    .isTrue();

            softly.assertThat(errorMessage)
                    .as("GraphQL syntax error message")
                    .isNotBlank();
        });
    }

    @Test
    void unknownFieldShouldReturnValidationErrorAndNoData() {
        ApiResponse<JsonNode> response =
                client.execute(GraphQlQueries.NON_EXISTENT_FIELD);

        JsonNode responseBody = response.body();
        JsonNode errors = responseBody.path("errors");
        JsonNode data = responseBody.path("data");
        String errorMessage = errors.path(0)
                .path("message")
                .asText();

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode())
                    .as("HTTP status code for an unknown GraphQL field")
                    .isIn(400);

            softly.assertThat(errors.isArray())
                    .as("GraphQL errors should be returned as an array")
                    .isTrue();

            softly.assertThat(errors.size())
                    .as("GraphQL errors array should not be empty")
                    .isPositive();

            softly.assertThat(
                            data.isMissingNode() || data.isNull()
                    )
                    .as("Data should be absent or null")
                    .isTrue();

            softly.assertThat(errorMessage)
                    .as("GraphQL validation error message")
                    .isNotBlank()
                    .containsIgnoringCase("field")
                    .contains("definitelyNotARealField");
        });
    }
}
